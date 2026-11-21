package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
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
import org.bukkit.event.entity.EntityShootBowEvent;

public class OnShootAuraComponent implements AuraComponent {
   private final Aura.AuraTracker tracker;
   private final Optional<Skill> onShootSkill;
   private final boolean cancelEvent;

   public OnShootAuraComponent(Aura.AuraTracker tracker, Optional<Skill> onShootSkill, boolean cancelEvent) {
      this.tracker = tracker;
      this.onShootSkill = onShootSkill;
      this.cancelEvent = cancelEvent;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         this.tracker
            .registerAuraComponent(
               Events.subscribe(EntityShootBowEvent.class)
                  .filter(event -> event.getEntity().getUniqueId().equals(maybeEntity.get().getUniqueId()))
                  .handler(event -> {
                     SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                     meta.setEntityTarget(BukkitAdapter.adapt(event.getEntity()));
                     BukkitTriggerMetadata.setEntityShotBowEventData(event, meta);
                     if (this.tracker.executeAuraSkill(this.onShootSkill, meta)) {
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
      name = "onshoot",
      aliases = {"onbowshoot"},
      description = "Triggers a skill when the aura holder shoots a bow"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final String onShootSkillName;
      private Optional<Skill> onShootSkill = Optional.empty();
      private final boolean cancelEvent;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.onShootSkillName = mlc.getString(new String[]{"onshootskill", "onshoot", "osh", "onbowshoot", "onbowshootskill"});
         this.cancelEvent = mlc.getBoolean(new String[]{"cancelevent", "ce"}, false);
         mechanic.getManager().queueSecondPass(() -> {
            if (this.onShootSkillName != null) {
               this.onShootSkill = mechanic.getManager().getSkill(mechanic.getFile(), mechanic, this.onShootSkillName);
            }
         });
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new OnShootAuraComponent(tracker, this.onShootSkill, this.cancelEvent);
      }
   }
}
