package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitTriggerMetadata;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.Optional;
import org.bukkit.event.block.BlockPlaceEvent;

public class OnBlockPlaceAuraComponent implements AuraComponent {
   private final Aura.AuraTracker tracker;
   private final Optional<Skill> onPlaceSkill;
   private final boolean cancelEvent;

   public OnBlockPlaceAuraComponent(Aura.AuraTracker tracker, Optional<Skill> onPlaceSkill, boolean cancelEvent) {
      this.tracker = tracker;
      this.onPlaceSkill = onPlaceSkill;
      this.cancelEvent = cancelEvent;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         this.tracker
            .registerAuraComponent(
               Events.subscribe(BlockPlaceEvent.class)
                  .filter(event -> event.getPlayer().getUniqueId().equals(maybeEntity.get().getUniqueId()))
                  .handler(event -> {
                     SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                     AbstractLocation target = BukkitAdapter.adapt(event.getBlock().getLocation()).add(0.5, 0.5, 0.5);
                     meta.setLocationTarget(target);
                     BukkitTriggerMetadata.apply(meta, event);
                     if (this.tracker.executeAuraSkill(this.onPlaceSkill, meta)) {
                        this.tracker.consumeCharge();
                        if (this.cancelEvent) {
                           event.setCancelled(true);
                        }
                     }
                  })
            );
      }
   }

   @MythicAuraComponent(
      name = "onblockplace",
      aliases = {"onplaceblock"},
      description = "Triggers a skill when the aura holder places a block"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final String onPlaceSkillName;
      private Optional<Skill> onPlaceSkill = Optional.empty();
      private final boolean cancelEvent;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.onPlaceSkillName = mlc.getString(new String[]{"onplaceskill", "onplace", "op"});
         this.cancelEvent = mlc.getBoolean(new String[]{"cancelevent", "ce", "cancel"}, false);
         mechanic.getManager().queueSecondPass(() -> {
            if (this.onPlaceSkillName != null) {
               this.onPlaceSkill = mechanic.getManager().getSkill(mechanic.getFile(), mechanic, this.onPlaceSkillName);
            }
         });
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new OnBlockPlaceAuraComponent(tracker, this.onPlaceSkill, this.cancelEvent);
      }
   }
}
