package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractTeleportData;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "teleport",
   aliases = {"tp"},
   description = "Teleports to the target location"
)
public class TeleportMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PlaceholderDouble spreadH;
   protected PlaceholderDouble spreadV;
   protected boolean preservePitch;
   protected boolean preserveYaw;
   protected boolean unsafe;
   protected AbstractTeleportData teleportData;

   public TeleportMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.spreadH = mlc.getPlaceholderDouble(new String[]{"radius", "r", "spreadh", "sh"}, 0.0);
      this.spreadV = mlc.getPlaceholderDouble(new String[]{"spreadv", "sv"}, 0.0);
      this.preservePitch = mlc.getBoolean(new String[]{"preservepitch", "pp"}, true);
      this.preserveYaw = mlc.getBoolean(new String[]{"preserveyaw", "py"}, true);
      this.unsafe = mlc.getBoolean(new String[]{"unsafe", "us"}, false);
      this.teleportData = this.getPlugin().getBootstrap().createTeleportData(mlc);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).entity(target).build();
      return this.cast(context);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();
      return this.cast(context);
   }

   public SkillResult cast(PlaceholderContext context) {
      AbstractWorld targetWorld = context.location().getWorld();
      if (targetWorld != null && targetWorld.isLoaded()) {
         double sH = this.spreadH.get(context);
         double sV = this.spreadV.get(context);
         AbstractLocation teleportLoc = context.location().clone();
         if (this.preservePitch) {
            teleportLoc.setPitch(context.meta().getCaster().getEntity().getLocation().getPitch());
         }

         if (this.preserveYaw) {
            teleportLoc.setYaw(context.meta().getCaster().getEntity().getLocation().getYaw());
         }

         if (sH > 0.0 || sV > 0.0) {
            if (context.meta().getCaster() instanceof ActiveMob am) {
               teleportLoc = MobExecutor.findSafeSpawnLocation(teleportLoc, sH, sV, am.getType().getMythicEntity().getHeight() + 1, true, !this.unsafe);
            } else {
               teleportLoc = MobExecutor.findSafeSpawnLocation(teleportLoc, sH, sV, 2, true, !this.unsafe);
            }
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Teleporting to {0}", teleportLoc.toString());
         context.meta().getCaster().getEntity().teleport(teleportLoc, this.teleportData);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
