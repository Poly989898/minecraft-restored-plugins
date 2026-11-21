package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.players.PlayerManager;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Location;

@MythicCondition(
   author = "Ashijin",
   name = "directionalvelocity",
   aliases = {"dvelocity"},
   description = "If the target has a velocity matching the given parameters"
)
public class DirectionalVelocityCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "x",
      aliases = {"x"},
      description = "The X velocity"
   )
   private final RangedDouble velocityX;
   @MythicField(
      name = "absx",
      aliases = {"absx"},
      description = "Use the absolute value of the X velocity",
      defValue = "false"
   )
   private final boolean absX;
   @MythicField(
      name = "y",
      aliases = {"y"},
      description = "The Y velocity"
   )
   private final RangedDouble velocityY;
   @MythicField(
      name = "absy",
      aliases = {"absy"},
      description = "Use the absolute value of the Y velocity",
      defValue = "false"
   )
   private final boolean absY;
   @MythicField(
      name = "z",
      aliases = {"z"},
      description = "The Z velocity"
   )
   private final RangedDouble velocityZ;
   @MythicField(
      name = "absz",
      aliases = {"absz"},
      description = "Use the absolute value of the Z velocity",
      defValue = "false"
   )
   private final boolean absZ;
   @MythicField(
      name = "relative",
      aliases = {"rel"},
      description = "If true, X is forward/backward and Z is side-to-side",
      defValue = "false"
   )
   private final boolean relative;

   public DirectionalVelocityCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.relative = mlc.getBoolean(new String[]{"relative", "rel"}, false);
      String zValue = mlc.getString(new String[]{"x", "side", "s"}, null);
      this.velocityZ = zValue != null ? new RangedDouble(zValue) : null;
      this.absZ = mlc.getBoolean(new String[]{"absx", "ax", "abss", "as"}, this.relative);
      String yValue = mlc.getString(new String[]{"y", "up", "down", "vertical", "v"}, null);
      this.velocityY = yValue != null ? new RangedDouble(yValue) : null;
      this.absY = mlc.getBoolean(new String[]{"absy", "ay"}, false);
      String xValue = mlc.getString(new String[]{"z", "forward", "f"}, null);
      this.velocityX = xValue != null ? new RangedDouble(xValue) : null;
      this.absX = mlc.getBoolean(new String[]{"absz", "az", "absf", "af"}, false);
      getPlugin().getPlayerManager().trackPlayerMovement();
   }

   @Override
   public boolean check(AbstractEntity entity) {
      AbstractVector velocity;
      if (entity.isPlayer()) {
         PlayerManager.PlayerMovementData data = getPlugin().getPlayerManager().getPlayerPositions().get(entity.getUniqueId());
         if (data != null && System.currentTimeMillis() - data.getLastMovementTime() <= 50L) {
            Location t = data.getTo();
            Location f = data.getFrom();
            velocity = BukkitAdapter.adapt(t.clone().subtract(f).toVector());
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Player hasn't moved recently");
            velocity = new AbstractVector(0, 0, 0);
         }
      } else {
         velocity = entity.getMotion();
      }

      if (this.relative) {
         float yaw = entity.getBukkitEntity().getLocation().getYaw();
         AbstractVector relativeVelocity = new AbstractVector(
            -Math.sin(Math.toRadians(yaw)) * velocity.getX() + Math.cos(Math.toRadians(yaw)) * velocity.getZ(),
            velocity.getY(),
            Math.cos(Math.toRadians(yaw)) * velocity.getX() + Math.sin(Math.toRadians(yaw)) * velocity.getZ()
         );
         boolean xCheck = this.velocityX == null || this.velocityX.equals(this.absX ? Math.abs(relativeVelocity.getX()) : relativeVelocity.getX());
         boolean yCheck = this.velocityY == null || this.velocityY.equals(this.absY ? Math.abs(relativeVelocity.getY()) : relativeVelocity.getY());
         boolean zCheck = this.velocityZ == null || this.velocityZ.equals(this.absZ ? Math.abs(relativeVelocity.getZ()) : relativeVelocity.getZ());
         MythicLogger.debug(
            MythicLogger.DebugLevel.CONDITION, "Relative Velocity: {0} {1} {2}", relativeVelocity.getX(), relativeVelocity.getY(), relativeVelocity.getZ()
         );
         return xCheck && yCheck && zCheck;
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Velocity: {0} {1} {2}", velocity.getX(), velocity.getY(), velocity.getZ());
         boolean xCheck = this.velocityX == null || this.velocityX.equals(this.absX ? Math.abs(velocity.getX()) : velocity.getX());
         boolean yCheck = this.velocityY == null || this.velocityY.equals(this.absY ? Math.abs(velocity.getY()) : velocity.getY());
         boolean zCheck = this.velocityZ == null || this.velocityZ.equals(this.absZ ? Math.abs(velocity.getZ()) : velocity.getZ());
         return xCheck && yCheck && zCheck;
      }
   }
}
