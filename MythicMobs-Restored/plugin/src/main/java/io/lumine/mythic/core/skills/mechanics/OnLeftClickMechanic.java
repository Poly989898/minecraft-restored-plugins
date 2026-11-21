package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

@MythicMechanic(
   author = "Phil",
   name = "OnSwing",
   aliases = {"OnLeftClick"},
   deprecated = true
)
public class OnLeftClickMechanic extends Aura implements ITargetedEntitySkill {
   @MythicField(
      name = "onSwingSkill",
      aliases = {"onswing", "osw"},
      description = "The name of the skill to trigger when the left click event occurs."
   )
   private String onSwingSkillName;
   private Optional<Skill> onSwingSkill = Optional.empty();

   public OnLeftClickMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.onSwingSkillName = mlc.getString(new String[]{"onswingskill", "onswing", "osw"});
      this.getManager().queueSecondPass(() -> {
         if (this.onSwingSkillName != null) {
            this.onSwingSkill = this.getManager().getSkill(file, this, this.onSwingSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new OnLeftClickMechanic.Tracker(target, data);
      return SkillResult.SUCCESS;
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      public Tracker(AbstractEntity entity, SkillMetadata data) {
         super(entity, data);
         this.start();
      }

      @Override
      public void auraStart() {
         this.registerAuraComponent(
            Events.subscribe(PlayerInteractEvent.class)
               .filter(event -> event.getPlayer().getUniqueId().equals(this.entity.get().getUniqueId()))
               .filter(event -> event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
               .filter(event -> event.getHand() == EquipmentSlot.HAND)
               .handler(event -> {
                  SkillMetadata meta = this.skillMetadata.deepClone();
                  if (this.executeAuraSkill(OnLeftClickMechanic.this.onSwingSkill, meta)) {
                     this.consumeCharge();
                  }
               })
         );
         this.executeAuraSkill(OnLeftClickMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}
