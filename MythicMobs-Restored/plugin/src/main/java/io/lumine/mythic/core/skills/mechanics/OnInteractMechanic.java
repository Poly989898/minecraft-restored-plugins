package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

@MythicMechanic(
   author = "Phil",
   name = "OnInteract",
   aliases = {"OnInteract"},
   deprecated = true
)
public class OnInteractMechanic extends Aura implements ITargetedEntitySkill {
   private String onInteractSkillName;
   private Optional<Skill> onInteractSkill = Optional.empty();

   public OnInteractMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.onInteractSkillName = mlc.getString(new String[]{"onrightclickskill", "oninteractskill", "onrightclick", "oninteract", "or", "oi"});
      this.getManager().queueSecondPass(() -> {
         if (this.onInteractSkillName != null) {
            this.onInteractSkill = this.getManager().getSkill(file, this, this.onInteractSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new OnInteractMechanic.Tracker(target, data);
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
            Events.subscribe(PlayerInteractAtEntityEvent.class)
               .filter(event -> event.getRightClicked().getUniqueId().equals(this.entity.get().getUniqueId()))
               .filter(event -> event.getHand() == EquipmentSlot.HAND)
               .handler(event -> {
                  SkillMetadata meta = this.skillMetadata.deepClone();
                  meta.setTrigger(BukkitAdapter.adapt(event.getPlayer()));
                  if (this.executeAuraSkill(OnInteractMechanic.this.onInteractSkill, meta)) {
                     this.consumeCharge();
                  }
               })
         );
         this.registerAuraComponent(
            Events.subscribe(PlayerInteractEvent.class)
               .filter(event -> event.getPlayer().getUniqueId().equals(this.entity.get().getUniqueId()))
               .filter(event -> event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
               .filter(event -> event.getHand() == EquipmentSlot.HAND)
               .handler(event -> {
                  SkillMetadata meta = this.skillMetadata.deepClone();
                  if (this.executeAuraSkill(OnInteractMechanic.this.onInteractSkill, meta)) {
                     this.consumeCharge();
                  }
               })
         );
         this.executeAuraSkill(OnInteractMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}
