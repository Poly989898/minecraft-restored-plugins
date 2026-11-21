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
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.util.Vector;

@MythicMechanic(
   author = "jaylawl",
   name = "teleportin",
   aliases = {"tpdir", "tpin", "tpi"},
   description = "Teleports the target in a direction"
)
public class TeleportInMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private boolean targetAsOrigin;
   protected PlaceholderDouble x;
   protected PlaceholderDouble y;
   protected PlaceholderDouble z;
   protected PlaceholderDouble yaw;
   protected PlaceholderDouble pitch;
   protected AbstractTeleportData teleportData;

   public TeleportInMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.targetAsOrigin = mlc.getBoolean(new String[]{"targetasorigin", "tao"}, false);
      this.yaw = mlc.getPlaceholderDouble(new String[]{"yaw", "y"}, 0.0);
      this.teleportData = this.getPlugin().getBootstrap().createTeleportData(mlc);
      String direction = mlc.getString(new String[]{"direction", "dir", "d", "vector", "v"}, null);
      if (direction != null) {
         String[] split = direction.split(",");

         try {
            this.x = PlaceholderDouble.of(split[0]);
            this.y = PlaceholderDouble.of(split[1]);
            this.z = PlaceholderDouble.of(split[2]);
         } catch (Exception e) {
            MythicLogger.errorMechanicConfig(this, mlc, "The 'direction' option must be in the format 'c=x,y,z.'");
         }
      } else {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'direction' option is required");
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      AbstractEntity caster = data.getCaster().getEntity();
      double x = this.x == null ? 0.0 : this.x.get(data, target) * -1.0;
      double y = this.y == null ? 0.0 : this.y.get(data, target);
      double z = this.z == null ? 0.0 : this.z.get(data, target) * -1.0;
      double yaw = this.yaw.get(data, target);
      AbstractLocation location;
      if (this.targetAsOrigin) {
         location = target.getLocation();
      } else {
         location = caster.getLocation();
      }

      float yawD = location.getYaw();
      if (yaw != 0.0) {
         yawD += (float)yaw;
      }

      float pitchD = 0.0F;
      double mod = Math.PI / 180.0;
      double div = Math.cos(pitchD * mod);
      double yD = Math.sin(pitchD * mod);
      double xD = Math.cos(yawD * mod);
      double zD = Math.sin(yawD * mod);
      xD *= div;
      zD *= div;
      Vector v1 = new Vector(xD, yD, zD);
      Vector v2 = v1.clone();
      if (z > 0.0) {
         v1.rotateAroundY(-Math.PI / 2);
      } else {
         v1.rotateAroundY(Math.PI / 2);
      }

      v1.multiply(x);
      v2.multiply(z);
      v1.add(v2);
      v1.setY(y);
      location.add(v1.getX(), v1.getY(), v1.getZ());
      if (target != caster) {
         location.setYaw(target.getLocation().getYaw());
         location.setPitch(target.getLocation().getPitch());
      }

      target.teleport(location, this.teleportData);
      return SkillResult.SUCCESS;
   }
}
