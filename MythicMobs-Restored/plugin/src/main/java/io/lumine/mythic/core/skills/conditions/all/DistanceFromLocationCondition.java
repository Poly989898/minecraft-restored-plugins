package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@MythicCondition(
   author = "Joshinn",
   name = "distanceFromLocation",
   description = "Tests if the caster is within a certain distance of a location."
)
public class DistanceFromLocationCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "x",
      description = ""
   )
   private PlaceholderDouble x;
   @MythicField(
      name = "y",
      description = ""
   )
   private PlaceholderDouble y;
   @MythicField(
      name = "z",
      description = ""
   )
   private PlaceholderDouble z;
   @MythicField(
      name = "world",
      description = ""
   )
   private PlaceholderString world;
   @MythicField(
      name = "distance",
      aliases = {"d"},
      description = "The distance to match"
   )
   protected RangedDouble distance;

   public DistanceFromLocationCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.x = mlc.getPlaceholderDouble(new String[]{"x"}, 0.0);
      this.y = mlc.getPlaceholderDouble(new String[]{"y"}, 0.0);
      this.z = mlc.getPlaceholderDouble(new String[]{"z"}, 0.0);
      this.world = mlc.getPlaceholderString(new String[]{"world", "w"}, null);
      String d = mlc.getString(new String[]{"distance", "d"}, this.conditionVar);
      this.distance = new RangedDouble(d, true);
   }

   @Override
   public boolean check(AbstractEntity target) {
      Location checkLocation = null;
      if (this.world != null && this.world.get() != null) {
         checkLocation = new Location(Bukkit.getWorld(this.world.get()), this.x.get(), this.y.get(), this.z.get());
      } else {
         checkLocation = new Location(BukkitAdapter.adapt(target.getWorld()), this.x.get(), this.y.get(), this.z.get());
      }

      double diffSq = (float)checkLocation.distanceSquared(BukkitAdapter.adapt(target.getLocation()));
      return this.distance.equals(diffSq);
   }
}
