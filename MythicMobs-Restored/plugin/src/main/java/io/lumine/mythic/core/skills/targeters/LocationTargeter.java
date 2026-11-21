package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "Location",
   aliases = {"LOC", "L"},
   description = "Targets the specified coordinates in the a world"
)
public class LocationTargeter extends ILocationSelector {
   protected PlaceholderString world;
   protected PlaceholderDouble x;
   protected PlaceholderDouble y;
   protected PlaceholderDouble z;
   protected PlaceholderDouble yaw;
   protected PlaceholderDouble pitch;

   public LocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      String coords = mlc.getString(new String[]{"location", "loc", "l", "c"}, null);
      if (coords != null) {
         String[] split = coords.split(",");

         try {
            this.x = PlaceholderDouble.of(split[0]);
            this.y = PlaceholderDouble.of(split[1]);
            this.z = PlaceholderDouble.of(split[2]);
            if (split.length > 3) {
               this.yaw = PlaceholderDouble.of(split[3]);
            } else {
               this.yaw = PlaceholderDouble.of("0");
            }

            if (split.length > 4) {
               this.pitch = PlaceholderDouble.of(split[4]);
            } else {
               this.pitch = PlaceholderDouble.of("0");
            }
         } catch (Exception e) {
            MythicLogger.errorTargeterConfig(this, mlc, "The 'location' attribute is required and must be in the format l=x,y,z.");
            return;
         }
      } else {
         this.x = mlc.getPlaceholderDouble("x", "0");
         this.y = mlc.getPlaceholderDouble("y", "0");
         this.z = mlc.getPlaceholderDouble("z", "0");
         this.yaw = mlc.getPlaceholderDouble("yaw", "0");
         this.pitch = mlc.getPlaceholderDouble("pitch", "0");
      }

      this.world = mlc.getPlaceholderString(new String[]{"world", "w"}, null);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      HashSet<AbstractLocation> targets = new HashSet<>();
      double x = this.x.get(data);
      double y = this.y.get(data);
      double z = this.z.get(data);
      float yaw = (float)this.yaw.get(data);
      float pitch = (float)this.pitch.get(data);
      AbstractWorld world = this.world == null ? am.getEntity().getWorld() : this.getPlugin().getBootstrap().getWorld(this.world.get(data));
      AbstractLocation l = MythicBukkit.inst().getBootstrap().newLocation(world, x, y, z, yaw, pitch);
      targets.add(l);
      return targets;
   }
}
