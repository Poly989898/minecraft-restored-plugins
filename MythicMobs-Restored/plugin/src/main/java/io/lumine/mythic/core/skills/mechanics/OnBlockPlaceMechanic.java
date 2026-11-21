package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitTriggerMetadata;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.event.block.BlockPlaceEvent;

@MythicMechanic(
   author = "Ashijin",
   name = "onblockplace",
   aliases = {"onplaceblock"},
   description = "Applies an aura to the target that triggers a skill when they place a block",
   deprecated = true
)
public class OnBlockPlaceMechanic extends Aura implements ITargetedEntitySkill {
   @MythicField(
      name = "onPlace",
      aliases = {"ob"},
      version = "4.14",
      description = "Skill to execute when a block is placed"
   )
   protected Optional<Skill> onPlaceSkill = Optional.empty();
   protected String onPlaceSkillName;
   @MythicField(
      name = "cancelEvent",
      aliases = {"ce"},
      defValue = "false",
      version = "4.13",
      description = "Whether or not to cancel the event that triggered the aura"
   )
   protected boolean cancelEvent;

   public OnBlockPlaceMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.onPlaceSkillName = mlc.getString(new String[]{"onplaceskill", "onplace", "op"});
      this.cancelEvent = mlc.getBoolean(new String[]{"cancelevent", "ce", "cancel"}, false);
      this.getManager().queueSecondPass(() -> {
         if (this.onPlaceSkillName != null) {
            this.onPlaceSkill = this.getManager().getSkill(file, this, this.onPlaceSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new OnBlockPlaceMechanic.Tracker(data, target);
      return SkillResult.SUCCESS;
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         this.start();
      }

      @Override
      public void auraStart() {
         this.registerAuraComponent(
            Events.subscribe(BlockPlaceEvent.class).filter(event -> event.getPlayer().getUniqueId().equals(this.entity.get().getUniqueId())).handler(event -> {
               SkillMetadata meta = this.skillMetadata.deepClone();
               AbstractLocation target = BukkitAdapter.adapt(event.getBlock().getLocation()).add(0.5, 0.5, 0.5);
               meta.setLocationTarget(target);
               BukkitTriggerMetadata.apply(meta, event);
               if (this.executeAuraSkill(OnBlockPlaceMechanic.this.onPlaceSkill, meta)) {
                  this.consumeCharge();
                  if (OnBlockPlaceMechanic.this.cancelEvent) {
                     event.setCancelled(true);
                  }
               }
            })
         );
         this.executeAuraSkill(OnBlockPlaceMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}
