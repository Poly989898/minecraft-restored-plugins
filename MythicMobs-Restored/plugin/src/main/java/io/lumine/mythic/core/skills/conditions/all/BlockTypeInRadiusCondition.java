package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractBlockMatcher;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitBlockMatcher;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;

@MythicCondition(
   author = "Ashijin",
   name = "blockTypeInRadius",
   aliases = {},
   description = "Tests the material type present at the target location"
)
public class BlockTypeInRadiusCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "types",
      aliases = {"types", "type", "t", "material", "mat", "m"},
      description = "A list of materials to check"
   )
   private AbstractBlockMatcher matcher;
   @MythicField(
      name = "amount"
   )
   private PlaceholderString amount;
   private PlaceholderInt radius;

   public BlockTypeInRadiusCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.amount = mlc.getPlaceholderString(new String[]{"amount", "a"}, ">0");
      this.radius = mlc.getPlaceholderInteger(new String[]{"radius", "r"}, 8);
      getPlugin().getClock().queuePostReload(() -> {
         List<String> list = mlc.getStringList(new String[]{"types", "type", "t", "material", "mat", "m", "block", "b"}, "DIRT", this.conditionVar);
         this.matcher = getPlugin().getBootstrap().getBlockMatcher(list);
      });
   }

   @Override
   public boolean check(AbstractLocation location) {
      RangedDouble amount = new RangedDouble(this.amount.get());
      int radius = this.radius.get();
      Location bukkitLoc = BukkitAdapter.adapt(location);
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitLoc)) {
         Boolean result = Schedulers.of(bukkitLoc).supply(() -> {
            int foundx = this.countBlocks(location, radius);
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Found {0} blocks in radius {1}", foundx, radius);
            return amount.equals(foundx);
         }).join();
         return result != null ? result : false;
      } else {
         int found = this.countBlocks(location, radius);
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Found {0} blocks in radius {1}", found, radius);
         return amount.equals(found);
      }
   }

   private int countBlocks(AbstractLocation location, int radius) {
      BukkitBlockMatcher matcher = (BukkitBlockMatcher)this.matcher;
      Location loc = BukkitAdapter.adapt(location);
      int count = 0;
      int startX = location.getBlockX() - radius;
      int startY = location.getBlockY() - radius;
      int startZ = location.getBlockZ() - radius;
      int endX = startX + 2 * radius;
      int endY = startY + 2 * radius;
      int endZ = startZ + 2 * radius;

      for (int x = startX; x <= endX; x++) {
         for (int y = startY; y <= endY; y++) {
            for (int z = startZ; z <= endZ; z++) {
               Block bukkitBlock = loc.getWorld().getBlockAt(x, y, z);
               if (matcher.matches(bukkitBlock) && bukkitBlock.getLocation().distance(loc) <= radius) {
                  count++;
               }
            }
         }
      }

      return count;
   }
}
