package io.lumine.mythic.core.skills.mechanics;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Phil",
   name = "OnJump",
   description = "Applies an aura to the target that triggers a skill when they jump"
)
public class OnJumpMechanic extends Aura implements ITargetedEntitySkill {
   private String onJumpSkillName;
   private Optional<Skill> onJumpSkill;
   private boolean cancelEvent;

   public OnJumpMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.onJumpSkillName = mlc.getString(new String[]{"onjumpskill", "onjump", "oj"});
      this.cancelEvent = mlc.getBoolean(new String[]{"cancelevent", "ce"}, false);
      this.getPlugin().getSkillManager().queueSecondPass(() -> {
         if (this.onJumpSkillName != null) {
            this.onJumpSkill = this.getPlugin().getSkillManager().getSkill(this.onJumpSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!ServerVersion.isPaper()) {
         MythicLogger.errorMechanicConfig(this, this.config, "Mechanic requires Paper");
         return SkillResult.INVALID_VERSION;
      } else {
         new OnJumpMechanic.Tracker(target, data);
         return SkillResult.SUCCESS;
      }
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      public Tracker(AbstractEntity entity, SkillMetadata data) {
         super(entity, data);
         this.start();
      }

      @Override
      public void auraStart() {
         this.registerAuraComponent(
            Events.subscribe(EntityJumpEvent.class).filter(event -> event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId())).handler(event -> {
               SkillMetadata meta = this.skillMetadata.deepClone();
               if (this.executeAuraSkill(OnJumpMechanic.this.onJumpSkill, meta)) {
                  this.consumeCharge();
                  if (OnJumpMechanic.this.cancelEvent) {
                     event.setCancelled(true);
                  }
               }
            })
         );
         this.registerAuraComponent(
            Events.subscribe(PlayerJumpEvent.class).filter(event -> event.getPlayer().getUniqueId().equals(this.entity.get().getUniqueId())).handler(event -> {
               SkillMetadata meta = this.skillMetadata.deepClone();
               if (this.executeAuraSkill(OnJumpMechanic.this.onJumpSkill, meta)) {
                  this.consumeCharge();
                  if (OnJumpMechanic.this.cancelEvent) {
                     event.setCancelled(true);
                  }
               }
            })
         );
         this.executeAuraSkill(OnJumpMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}
