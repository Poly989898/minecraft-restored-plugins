package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ILocationComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

@MythicCondition(
   author = "jaylawl",
   name = "cuboid",
   aliases = {"incuboid"},
   version = "4.5",
   description = "Whether the target is within the given cuboid between location1 x location2"
)
public class CuboidCondition extends SkillCondition implements IEntityComparisonCondition, ILocationComparisonCondition, ILocationCondition {
   @MythicField(
      name = "location1",
      aliases = {"loc1", "l1", "a"},
      description = "x,y,z coordinates for the 1st point"
   )
   protected String coords1;
   @MythicField(
      name = "location2",
      aliases = {"loc2", "l2", "b"},
      description = "x,y,z coordinates for the 2nd point"
   )
   protected String coords2;
   @MythicField(
      name = "relative",
      aliases = {"r"},
      description = "Whether or not the coordinates should be relative to the caster"
   )
   protected Boolean relative;
   protected BoundingBox cuboid;

   public CuboidCondition(String line, MythicLineConfig mlc) {
      super(line);
      String input1 = mlc.getString(new String[]{"location1", "loc1", "l1", "a"}, null);
      String input2 = mlc.getString(new String[]{"location2", "loc2", "l2", "b"}, null);
      this.relative = mlc.getBoolean(new String[]{"relative", "r"}, false);
      if (input1 != null && input2 != null) {
         String[] split1 = input1.split(",");
         String[] split2 = input2.split(",");

         try {
            this.cuboid = new BoundingBox(
               Double.parseDouble(split1[0]),
               Double.parseDouble(split1[1]),
               Double.parseDouble(split1[2]),
               Double.parseDouble(split2[0]),
               Double.parseDouble(split2[1]),
               Double.parseDouble(split2[2])
            );
         } catch (Exception e) {
            MythicLogger.errorConditionConfig(this, mlc, "Incorrectly configured location attribute(s); couldn't parse as cuboid");
         }
      } else {
         MythicLogger.errorConditionConfig(this, mlc, "Two coordinates/vectors are required");
      }
   }

   @Override
   public boolean check(AbstractEntity caster, AbstractEntity target) {
      Vector targetVector = target.getBukkitEntity().getLocation().toVector();
      return this.relative
         ? this.cuboid.clone().shift(caster.getBukkitEntity().getLocation().toVector()).contains(targetVector)
         : this.cuboid.contains(targetVector);
   }

   @Override
   public boolean check(AbstractLocation location, AbstractLocation target) {
      Vector targetVector = BukkitAdapter.adapt(target).toVector();
      return this.relative ? this.cuboid.clone().shift(BukkitAdapter.adapt(location).toVector()).contains(targetVector) : this.cuboid.contains(targetVector);
   }

   @Override
   public boolean check(AbstractLocation target) {
      return this.cuboid.contains(BukkitAdapter.adapt(target).toVector());
   }
}
