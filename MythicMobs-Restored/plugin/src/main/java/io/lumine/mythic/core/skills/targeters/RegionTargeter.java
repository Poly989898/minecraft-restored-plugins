package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;

@Getter
@MythicTargeter(
   author = "Ashijin",
   name = "region",
   aliases = {},
   description = "Special targeter to target a region. Only works with specific mechanics."
)
public class RegionTargeter extends SkillTargeter {
   private PlaceholderString world;
   private PlaceholderString min;
   private PlaceholderString max;

   public RegionTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.min = mlc.getPlaceholderString(new String[]{"min"}, "0,0,0");
      this.max = mlc.getPlaceholderString(new String[]{"max"}, "0,0,0");
      this.world = mlc.getPlaceholderString(new String[]{"world"}, "");
   }

   public Position getMin(SkillMetadata data) {
      try {
         String strWorld = this.world.get(data);
         World world;
         if (strWorld.isEmpty()) {
            world = BukkitAdapter.adapt(data.getOrigin().getWorld());
         } else {
            world = Bukkit.getWorld(strWorld);
         }

         String[] split = this.min.get(data).split(",");
         Integer x = Integer.valueOf(split[0]);
         Integer y = Integer.valueOf(split[1]);
         Integer z = Integer.valueOf(split[2]);
         return Position.of((double)x.intValue(), (double)y.intValue(), (double)z.intValue(), world);
      } catch (Error | Exception e) {
         MythicLogger.errorTargeterConfig(this, this.config, "Invalid syntax for 'min' attribute");
         return null;
      }
   }

   public Position getMax(SkillMetadata data) {
      try {
         String strWorld = this.world.get(data);
         World world;
         if (strWorld.isEmpty()) {
            world = BukkitAdapter.adapt(data.getOrigin().getWorld());
         } else {
            world = Bukkit.getWorld(strWorld);
         }

         String[] split = this.max.get(data).split(",");
         Integer x = Integer.valueOf(split[0]);
         Integer y = Integer.valueOf(split[1]);
         Integer z = Integer.valueOf(split[2]);
         return Position.of((double)x.intValue(), (double)y.intValue(), (double)z.intValue(), world);
      } catch (Error | Exception e) {
         MythicLogger.errorTargeterConfig(this, this.config, "Invalid syntax for 'max' attribute");
         return null;
      }
   }
}
