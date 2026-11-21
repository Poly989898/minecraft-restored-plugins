package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.Optional;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class OnInteractAuraComponent implements AuraComponent {
   private final Aura.AuraTracker tracker;
   private final Optional<Skill> onInteractSkill;

   public OnInteractAuraComponent(Aura.AuraTracker tracker, Optional<Skill> onInteractSkill) {
      this.tracker = tracker;
      this.onInteractSkill = onInteractSkill;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         this.tracker
            .registerAuraComponent(
               Events.subscribe(PlayerInteractAtEntityEvent.class)
                  .filter(event -> event.getRightClicked().getUniqueId().equals(maybeEntity.get().getUniqueId()))
                  .filter(event -> event.getHand() == EquipmentSlot.HAND)
                  .handler(event -> {
                     SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                     meta.setTrigger(BukkitAdapter.adapt(event.getPlayer()));
                     if (this.tracker.executeAuraSkill(this.onInteractSkill, meta)) {
                        this.tracker.consumeCharge();
                     }
                  })
            );
         this.tracker
            .registerAuraComponent(
               Events.subscribe(PlayerInteractEvent.class)
                  .filter(event -> event.getPlayer().getUniqueId().equals(maybeEntity.get().getUniqueId()))
                  .filter(event -> event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                  .filter(event -> event.getHand() == EquipmentSlot.HAND)
                  .handler(event -> {
                     SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                     if (this.tracker.executeAuraSkill(this.onInteractSkill, meta)) {
                        this.tracker.consumeCharge();
                     }
                  })
            );
      }
   }

   @MythicAuraComponent(
      name = "oninteract",
      aliases = {"onrightclick"},
      description = "Triggers a skill when the aura holder interacts"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final String onInteractSkillName;
      private Optional<Skill> onInteractSkill = Optional.empty();

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.onInteractSkillName = mlc.getString(new String[]{"onrightclickskill", "oninteractskill", "onrightclick", "oninteract", "or", "oi"});
         mechanic.getManager().queueSecondPass(() -> {
            if (this.onInteractSkillName != null) {
               this.onInteractSkill = mechanic.getManager().getSkill(mechanic.getFile(), mechanic, this.onInteractSkillName);
            }
         });
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new OnInteractAuraComponent(tracker, this.onInteractSkill);
      }
   }
}
