package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Ashijin",
   name = "playerWithin",
   aliases = {"playerswithin"},
   description = "Checks if any players are within a radius of the target"
)
public class PlayerWithinCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "distance",
      aliases = {"d"},
      description = "The radius to check in"
   )
   private double distance;

   public PlayerWithinCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.distance = Math.pow(Double.valueOf(mlc.getString(new String[]{"distance", "d"}, "0", this.conditionVar)), 2.0);
   }

   @Override
   public boolean check(AbstractLocation location) {
      Location l = BukkitAdapter.adapt(location);

      for (Player p : l.getWorld().getPlayers()) {
         if (p.getGameMode() != GameMode.SPECTATOR && p.getGameMode() != GameMode.CREATIVE && l.distanceSquared(p.getLocation()) <= this.distance) {
            return true;
         }
      }

      return false;
   }
}
