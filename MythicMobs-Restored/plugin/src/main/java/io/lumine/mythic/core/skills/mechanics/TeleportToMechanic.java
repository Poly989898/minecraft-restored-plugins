package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractTeleportData;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin/jaylawl",
   name = "teleportto",
   aliases = {"tpt", "teleportlocation", "tpl"},
   description = "Teleports the target entity to a location"
)
public class TeleportToMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "relative",
      aliases = {"m"},
      version = "4.9",
      description = "Whether to use relative teleportation. Default=false"
   )
   private boolean relative;
   @MythicField(
      name = "world",
      aliases = {"w"},
      version = "?",
      description = "Which world to teleport to. Only used it mode=absolute"
   )
   protected PlaceholderString world;
   @MythicField(
      name = "location",
      aliases = {"target", "to", "loc", "l", "coordinates", "c"},
      version = "?",
      description = "The coordinates to teleport to in format x,y,z."
   )
   private Optional<SkillTargeter> targeter = Optional.empty();
   private String coords;
   protected PlaceholderDouble x;
   protected PlaceholderDouble y;
   protected PlaceholderDouble z;
   @MythicField(
      name = "yaw",
      aliases = {"y"},
      version = "?",
      description = "The yaw to be applied when teleporting. Acts as an offset when using mode=relative"
   )
   protected PlaceholderDouble yaw;
   @MythicField(
      name = "pitch",
      aliases = {"p"},
      version = "?",
      description = "The pitch to be applied when teleporting. Acts as an offset when using mode=relative"
   )
   protected PlaceholderDouble pitch;
   @MythicField(
      name = "origin",
      aliases = {"o"},
      version = "4.9",
      description = "Wether to base relative teleportation on the caster's or the target's location - Default=false"
   )
   private boolean targetAsOrigin;
   private AbstractTeleportData teleportData;

   public TeleportToMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.relative = mlc.getBoolean(new String[]{"relative", "r"}, false);
      this.targetAsOrigin = mlc.getBoolean(new String[]{"targetasorigin", "tao"}, false);
      this.world = mlc.getPlaceholderString(new String[]{"world", "w"}, null);
      this.yaw = mlc.getPlaceholderDouble(new String[]{"yaw", "y"}, 0.0);
      this.pitch = mlc.getPlaceholderDouble(new String[]{"pitch", "p"}, 0.0);
      this.teleportData = this.getPlugin().getBootstrap().createTeleportData(mlc);
      this.coords = mlc.getString(new String[]{"target", "to", "t", "location", "loc", "l", "coordinates", "c"}, null);
      if (this.coords != null) {
         if (this.coords.startsWith("@")) {
            this.targeter = Optional.ofNullable(this.getPlugin().getSkillManager().getTargeter(this.coords));
         } else {
            String[] split = this.coords.split(",");

            try {
               this.x = PlaceholderDouble.of(split[0]);
               this.y = PlaceholderDouble.of(split[1]);
               this.z = PlaceholderDouble.of(split[2]);
            } catch (Exception e) {
               MythicLogger.errorMechanicConfig(this, mlc, "The 'coordinates' attribute must be in the format c=x,y,z.");
               return;
            }
         }
      }

      if (this.coords == null) {
         if (this.relative) {
            MythicLogger.errorMechanicConfig(this, mlc, "'Coordinates' field is required for using relative teleportation");
         } else if (this.world == null) {
            MythicLogger.errorMechanicConfig(this, mlc, "Either the 'coordinates' attribute or 'world' attribute is required.");
         }
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      AbstractEntity caster = data.getCaster().getEntity();
      AbstractLocation location;
      if (this.targeter.isPresent()) {
         location = this.getPlugin().getSkillManager().getLocationTarget(this.targeter.get(), data);
         if (location == null) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Teleporting failed: targeter returned nothing");
            return SkillResult.INVALID_TARGET;
         }
      } else if (!this.relative) {
         if (this.world == null && this.coords != null) {
            location = new AbstractLocation(target.getWorld(), this.x.get(data, target), this.y.get(data, target), this.z.get(data, target));
         } else if (this.world != null && this.coords == null) {
            location = new AbstractLocation(
               MythicBukkit.inst().getBootstrap().getWorld(this.world.get(data, target)),
               target.getLocation().getX(),
               target.getLocation().getY(),
               target.getLocation().getZ()
            );
         } else if (this.world != null && this.coords != null) {
            location = new AbstractLocation(
               MythicBukkit.inst().getBootstrap().getWorld(this.world.get(data, target)),
               this.x.get(data, target),
               this.y.get(data, target),
               this.z.get(data, target)
            );
         } else {
            location = target.getLocation().clone();
         }

         location.setYaw((float)this.yaw.get(data, target));
         location.setPitch((float)this.pitch.get(data, target));
      } else {
         if (this.targetAsOrigin) {
            location = target.getLocation();
         } else {
            location = caster.getLocation();
         }

         location.add(this.x.get(data, target), this.y.get(data, target), this.z.get(data, target));
         location.setYaw(location.getYaw() + (float)this.yaw.get(data, target));
         location.setPitch(location.getPitch() + (float)this.pitch.get(data, target));
      }

      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Teleporting to {0}", location.toString());
      target.teleport(location, this.teleportData);
      return SkillResult.SUCCESS;
   }
}
