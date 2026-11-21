package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
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
import org.bukkit.event.entity.EntityShootBowEvent;

@MythicMechanic(
   author = "Ashijin",
   name = "onbowshoot",
   aliases = {"onshoot"},
   description = "Applies an aura to the target that triggers a skill when they shoot a bow",
   deprecated = true
)
public class OnShootMechanic extends Aura implements ITargetedEntitySkill {
   protected Optional<Skill> onShootSkill = Optional.empty();
   protected String onShootSkillName;
   @MythicField(
      name = "cancelEvent",
      aliases = {"ce"},
      defValue = "false",
      version = "4.6",
      description = "Whether or not to cancel the event that triggered the aura"
   )
   protected boolean cancelEvent;
   @MythicField(
      name = "forceAsPower",
      aliases = {"fap"},
      defValue = "true",
      version = "4.6",
      description = "Whether to pass the force of the bow as the skill's power"
   )
   protected boolean forceAsPower;

   public OnShootMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.onShootSkillName = mlc.getString(new String[]{"onshootskill", "onshoot", "osh", "onbowshoot", "onbowshootskill"});
      this.cancelEvent = mlc.getBoolean(new String[]{"cancelevent", "ce"}, false);
      this.forceAsPower = mlc.getBoolean(new String[]{"forceaspower", "fap"}, true);
      this.getManager().queueSecondPass(() -> {
         if (this.onShootSkillName != null) {
            this.onShootSkill = this.getManager().getSkill(file, this, this.onShootSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new OnShootMechanic.Tracker(data, target);
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
            Events.subscribe(EntityShootBowEvent.class)
               .filter(event -> event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId()))
               .handler(event -> {
                  SkillMetadata meta = this.skillMetadata.deepClone();
                  meta.setEntityTarget(BukkitAdapter.adapt(event.getEntity()));
                  BukkitTriggerMetadata.setEntityShotBowEventData(event, meta);
                  if (this.executeAuraSkill(OnShootMechanic.this.onShootSkill, meta)) {
                     this.consumeCharge();
                     if (OnShootMechanic.this.cancelEvent) {
                        event.setCancelled(true);
                     }
                  }
               })
         );
         this.executeAuraSkill(OnShootMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}
