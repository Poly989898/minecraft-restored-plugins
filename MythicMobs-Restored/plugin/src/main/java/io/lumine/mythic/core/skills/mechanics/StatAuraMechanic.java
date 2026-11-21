package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.stats.StatModifierType;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatSource;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "stataura",
   aliases = {"statbuff", "statdebuff"},
   description = "Applies a generic aura to the target",
   deprecated = true
)
public class StatAuraMechanic extends Aura implements ITargetedEntitySkill {
   private PlaceholderString stat;
   private StatType statType;
   private PlaceholderString modifier;
   private StatModifierType modifierType;
   private PlaceholderDouble modifierValue;

   public StatAuraMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.stat = mlc.getPlaceholderString(new String[]{"stat", "s"}, null);
      if (this.stat == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "Stat is required");
      } else {
         this.modifier = mlc.getPlaceholderString(new String[]{"type", "t", "modifier", "mod", "m"}, StatModifierType.ADDITIVE.toString());
         if (this.modifier == null) {
            MythicLogger.errorMechanicConfig(this, mlc, "Modifier type is required");
         } else {
            this.modifierValue = mlc.getPlaceholderDouble(new String[]{"value", "val", "v"}, 0.0);
         }
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer() && !this.getPlugin().getMobManager().isMythicMob(target)) {
         return SkillResult.INVALID_TARGET;
      } else {
         this.statType = this.getPlugin().getStatManager().getStat(this.stat.get(data, target)).get();
         this.modifierType = StatModifierType.get(this.modifier.get(data, target).toUpperCase());
         new StatAuraMechanic.Tracker(data, target);
         return SkillResult.SUCCESS;
      }
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable, StatSource {
      private final AbstractEntity target;
      private final Optional<StatRegistry> registry;
      private double value;
      private final SkillMetadata data;

      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         this.target = entity;
         this.registry = StatAuraMechanic.this.getPlugin().getStatManager().getStatRegistry(entity);
         this.value = StatAuraMechanic.this.modifierValue.get(data, entity) * this.stacks;
         this.data = data;
         this.start();
      }

      @Override
      public void auraTick() {
         super.auraTick();
         double val = StatAuraMechanic.this.modifierValue.get(this.data, this.target) * this.stacks;
         if (val != this.value) {
            this.value = val;
            this.registry.ifPresent(r -> r.putValue(StatAuraMechanic.this.statType, this, StatAuraMechanic.this.modifierType, val));
         }
      }

      @Override
      public void auraStart() {
         this.executeAuraSkill(StatAuraMechanic.this.onStartSkill, this.skillMetadata);
         this.registry.ifPresent(r -> r.putValue(StatAuraMechanic.this.statType, this, StatAuraMechanic.this.modifierType, this.value));
      }

      @Override
      public void auraStop() {
         this.registry.ifPresent(r -> r.removeValue(StatAuraMechanic.this.statType, this));
         this.executeAuraSkill(StatAuraMechanic.this.onEndSkill, this.skillMetadata);
      }

      @Override
      public boolean removeOnReload() {
         return false;
      }
   }
}
