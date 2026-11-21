package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

@MythicCondition(
   author = "Ashijin",
   name = "altitude",
   aliases = {"heightfromsurface"},
   description = "Tests how far above the ground the target entity is"
)
public class AltitudeCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "height",
      aliases = {"altitude", "a", "h"},
      description = "The height range to check"
   )
   private RangedDouble height;
   @MythicField(
      name = "maxHeight",
      aliases = {"mH"},
      description = "The maximum height range to check"
   )
   private double maxHeight;

   public AltitudeCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.height = new RangedDouble(mlc.getString(new String[]{"height", "h", "altitude", "a"}, this.conditionVar));
      this.maxHeight = mlc.getDouble(new String[]{"maxHeight", "mH"}, 30.0);
   }

   @Override
   public boolean check(AbstractEntity target) {
      Entity e = target.getBukkitEntity();
      Location location = e.getLocation();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(location)) {
         Boolean result = Schedulers.of(location).supply(() -> this.checkAltitude(location)).join();
         return result != null ? result : false;
      } else {
         return this.checkAltitude(location);
      }
   }

   private boolean checkAltitude(Location location) {
      double yEnt = location.getY();
      Block groundBlock = location.getBlock();

      while (groundBlock.isPassable() && yEnt - groundBlock.getY() < this.maxHeight) {
         groundBlock = groundBlock.getRelative(0, -1, 0);
      }

      double ydiff = yEnt - groundBlock.getY();
      MythicLogger.debug(
         MythicLogger.DebugLevel.CONDITION, "Altitude is {0}, checking against {1}, with maximum height of {2}", ydiff, this.height.toString(), this.maxHeight
      );
      return this.height.equals(ydiff);
   }
}
