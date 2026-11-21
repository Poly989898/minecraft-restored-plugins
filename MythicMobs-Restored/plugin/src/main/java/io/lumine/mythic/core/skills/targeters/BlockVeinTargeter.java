package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;

@MythicTargeter(
   author = "Seyarada",
   name = "blockVein",
   aliases = {"vein", "bv"},
   description = "Targets all blocks in the radius of the inherited target"
)
public class BlockVeinTargeter extends ILocationSelector {
   @MythicField(
      name = "blocktype",
      aliases = {"blocktypes", "bt", "t", "material", "materials", "mat", "m", "blocks", "block", "b"},
      description = "Blocks to add to the Vein",
      defValue = "STONE"
   )
   protected PlaceholderString blockType;
   @MythicField(
      name = "limit",
      aliases = {"l", "max", "m"},
      description = "Limit of blocks added to the vein",
      defValue = "10"
   )
   protected PlaceholderInt limit;
   @MythicField(
      name = "originMustMatch",
      aliases = {"match"},
      description = "Check if the origin matches the blocktypes",
      defValue = "true"
   )
   protected boolean originMustMatch;
   List<String> wantedBlockTypes;

   public BlockVeinTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.blockType = mlc.getPlaceholderString(
         new String[]{"blocktypes", "blocktypes", "bt", "t", "material", "materials", "m", "blocks", "block", "b"}, "STONE"
      );
      this.limit = mlc.getPlaceholderInteger(new String[]{"limit", "l", "max", "m"}, 10);
      this.originMustMatch = mlc.getBoolean(new String[]{"originMustMatch", "match"}, true);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      HashSet<AbstractLocation> targets = new HashSet<>();
      AbstractLocation origin = data.getOrigin();
      this.wantedBlockTypes = List.of(this.blockType.get(data).split(","));
      this.findConnectedBlocks(targets, origin, this.limit.get(data), true);
      return targets;
   }

   private void findConnectedBlocks(Set<AbstractLocation> targets, AbstractLocation origin, int limit, boolean first) {
      if (limit != 0 && targets.size() < limit) {
         if (first && this.originMustMatch) {
            Material originBlock = BukkitAdapter.adapt(origin).getBlock().getType();
            if (!blockMatches(originBlock, this.wantedBlockTypes)) {
               return;
            }
         }

         for (int[] direction : this.generateRandomizedDirections()) {
            int x = direction[0];
            int y = direction[1];
            int z = direction[2];
            AbstractLocation neighbor = new AbstractLocation(origin.getWorld(), origin.getX() + x, origin.getY() + y, origin.getZ() + z);
            if (!targets.contains(neighbor)) {
               Material material = BukkitAdapter.adapt(neighbor).getBlock().getType();
               if (blockMatches(material, this.wantedBlockTypes)) {
                  targets.add(neighbor);
                  this.findConnectedBlocks(targets, neighbor, limit - 1, false);
               }
            }
         }
      }
   }

   List<int[]> generateRandomizedDirections() {
      List<int[]> directions = new ArrayList<>();

      for (int x = -1; x <= 1; x++) {
         for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
               if (x != 0 || y != 0 || z != 0) {
                  directions.add(new int[]{x, y, z});
               }
            }
         }
      }

      Collections.shuffle(directions);
      return directions;
   }
}
