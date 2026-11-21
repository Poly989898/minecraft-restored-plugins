package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.skills.stats.StatModifierType;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatSource;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.Collection;
import java.util.Optional;

public class StatAuraComponent implements AuraComponent, StatSource {
   private final MythicBukkit plugin;
   private final Aura.AuraTracker tracker;
   private final PlaceholderString stat;
   private final PlaceholderString modifier;
   private final PlaceholderDouble modifierValue;
   private AbstractEntity target;
   private Optional<StatRegistry> registry = Optional.empty();
   private StatType statType;
   private StatModifierType modifierType;
   private double value;
   private boolean initialized;

   public StatAuraComponent(MythicBukkit plugin, Aura.AuraTracker tracker, PlaceholderString stat, PlaceholderString modifier, PlaceholderDouble modifierValue) {
      this.plugin = plugin;
      this.tracker = tracker;
      this.stat = stat;
      this.modifier = modifier;
      this.modifierValue = modifierValue;
   }

   @Override
   public void auraStart() {
      SkillMetadata data = this.tracker.getSkillMetadata();
      AbstractEntity entityTarget = this.firstEntityTarget(data.getEntityTargets());
      if (entityTarget != null) {
         if (entityTarget.isPlayer() || this.plugin.getMobManager().isMythicMob(entityTarget)) {
            this.target = entityTarget;
            String statKey = this.stat.get(data, this.target);
            Optional<StatType> statEntry = this.plugin.getStatManager().getStat(statKey);
            if (!statEntry.isEmpty()) {
               this.statType = statEntry.get();
               this.modifierType = StatModifierType.get(this.modifier.get(data, this.target).toUpperCase());
               this.registry = this.plugin.getStatManager().getStatRegistry(this.target);
               this.value = this.modifierValue.get(data, this.target) * this.tracker.getStacks();
               this.registry.ifPresent(r -> r.putValue(this.statType, this, this.modifierType, this.value));
               this.initialized = true;
            }
         }
      }
   }

   @Override
   public void auraTick() {
      if (this.initialized && this.target != null) {
         SkillMetadata data = this.tracker.getSkillMetadata();
         double updated = this.modifierValue.get(data, this.target) * this.tracker.getStacks();
         if (updated != this.value) {
            this.value = updated;
            this.registry.ifPresent(r -> r.putValue(this.statType, this, this.modifierType, updated));
         }
      }
   }

   @Override
   public void auraStop() {
      if (this.initialized) {
         this.registry.ifPresent(r -> r.removeValue(this.statType, this));
      }
   }

   @Override
   public boolean removeOnReload() {
      return false;
   }

   private AbstractEntity firstEntityTarget(Collection<AbstractEntity> targets) {
      return targets != null && !targets.isEmpty() ? targets.iterator().next() : null;
   }

   @MythicAuraComponent(
      name = "stat",
      aliases = {"stats", "stataura"},
      description = "Applies stat modifiers while aura is active"
   )
   public static class Factory implements AuraComponentFactory {
      private final MythicBukkit plugin;
      private final AuraMechanic mechanic;
      private final PlaceholderString stat;
      private final PlaceholderString modifier;
      private final PlaceholderDouble modifierValue;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.plugin = mechanic.getPlugin();
         this.stat = mlc.getPlaceholderString(new String[]{"stat", "s"}, null);
         this.modifier = mlc.getPlaceholderString(new String[]{"type", "t", "modifier", "mod", "m"}, StatModifierType.ADDITIVE.toString());
         this.modifierValue = mlc.getPlaceholderDouble(new String[]{"value", "val", "v"}, 0.0);
         if (this.stat == null) {
            MythicLogger.errorMechanicConfig(mechanic, mlc, "Stat aura component requires a stat");
         }

         if (this.modifier == null) {
            MythicLogger.errorMechanicConfig(mechanic, mlc, "Stat aura component requires a modifier type");
         }
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return this.stat != null && this.modifier != null ? new StatAuraComponent(this.plugin, tracker, this.stat, this.modifier, this.modifierValue) : null;
      }
   }
}
