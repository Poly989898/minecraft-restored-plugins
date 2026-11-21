package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.Optional;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class OnLeftClickAuraComponent implements AuraComponent {
   private final Aura.AuraTracker tracker;
   private final Optional<Skill> onSwingSkill;

   public OnLeftClickAuraComponent(Aura.AuraTracker tracker, Optional<Skill> onSwingSkill) {
      this.tracker = tracker;
      this.onSwingSkill = onSwingSkill;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         this.tracker
            .registerAuraComponent(
               Events.subscribe(PlayerInteractEvent.class)
                  .filter(event -> event.getPlayer().getUniqueId().equals(maybeEntity.get().getUniqueId()))
                  .filter(event -> event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                  .filter(event -> event.getHand() == EquipmentSlot.HAND)
                  .handler(event -> {
                     SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                     if (this.tracker.executeAuraSkill(this.onSwingSkill, meta)) {
                        this.tracker.consumeCharge();
                     }
                  })
            );
      }
   }

   @MythicAuraComponent(
      name = "onleftclick",
      aliases = {"onswing"},
      description = "Triggers a skill when the aura holder left-clicks"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final String onSwingSkillName;
      private Optional<Skill> onSwingSkill = Optional.empty();

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.onSwingSkillName = mlc.getString(new String[]{"onswingskill", "onswing", "osw"});
         mechanic.getManager().queueSecondPass(() -> {
            if (this.onSwingSkillName != null) {
               this.onSwingSkill = mechanic.getManager().getSkill(mechanic.getFile(), mechanic, this.onSwingSkillName);
            }
         });
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new OnLeftClickAuraComponent(tracker, this.onSwingSkill);
      }
   }
}
