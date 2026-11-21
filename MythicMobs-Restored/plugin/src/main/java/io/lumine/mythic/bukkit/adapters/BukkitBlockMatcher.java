package io.lumine.mythic.bukkit.adapters;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractBlockMatcher;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Collection;
import java.util.regex.Pattern;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BukkitBlockMatcher extends AbstractBlockMatcher {
   @Getter
   private final Collection<Material> materials = Sets.newHashSet();
   private final SetMultimap<Material, BlockData> specificBlocks = HashMultimap.create();
   @Getter
   private final Collection<Tag<Material>> tags = Sets.newHashSet();

   public Collection<BlockData> getSpecificBlocks() {
      return this.specificBlocks.values();
   }

   public BukkitBlockMatcher(String filter) {
      for (String line : MythicLineConfigImpl.splitTopLevelCommaList(filter)) {
         try {
            this.parse(line);
         } catch (Throwable e) {
            MythicLogger.error("Failed to parse BlockMatcher element {0}", line);
            e.printStackTrace();
         }
      }
   }

   public BukkitBlockMatcher(Collection<String> filter) {
      for (String line : filter) {
         try {
            this.parse(line);
         } catch (Throwable e) {
            MythicLogger.error("Failed to parse BlockMatcher element {0}", line);
            e.printStackTrace();
         }
      }
   }

   private void parse(String filter) {
      if (filter != null && !filter.isEmpty()) {
         if (filter.startsWith("#")) {
            filter = filter.substring(1);
            if (!filter.contains(":")) {
               filter = "minecraft:" + filter;
            }

            NamespacedKey tagKey = NamespacedKey.fromString(filter);
            Tag<Material> tag = Bukkit.getTag("blocks", tagKey, Material.class);
            if (tag == null) {
               MythicLogger.error("[BlockType] BlockTag '#" + tagKey + "' does not exist");
            } else {
               this.tags.add(tag);
            }
         } else if (filter.startsWith("*[")) {
            String blockState = filter.substring(filter.indexOf(91)).toLowerCase();

            for (Material material : Material.values()) {
               if (material.isBlock()) {
                  try {
                     this.addSpecificBlock(Bukkit.createBlockData(material.getKey() + blockState));
                  } catch (IllegalArgumentException e) {
                  }
               }
            }
         } else if (filter.contains("*")) {
            int wildcardIndex = filter.indexOf(42);
            String before = filter.substring(0, wildcardIndex);
            String after = filter.substring(wildcardIndex + 1);
            String regex = "^" + Pattern.quote(before).toUpperCase() + ".*" + Pattern.quote(after).toUpperCase() + "$";
            Pattern pattern = Pattern.compile(regex);
            boolean matched = false;

            for (Material materialx : Material.values()) {
               if (pattern.matcher(materialx.name()).matches()) {
                  this.materials.add(materialx);
                  matched = true;
               }
            }

            if (!matched) {
               MythicLogger.error("[BlockType] Wildcard '" + filter + "' did not match any block type.");
            }
         } else {
            String stateError = null;
            if (filter.contains("[")) {
               try {
                  this.addSpecificBlock(Bukkit.createBlockData(filter.toLowerCase()));
                  return;
               } catch (IllegalArgumentException ex) {
                  stateError = ex.getMessage();
               }
            }

            Material materialxx = Material.getMaterial(filter.toUpperCase());
            if (materialxx != null) {
               this.materials.add(materialxx);
            } else {
               AbstractBlock block = MythicBukkit.inst().getBootstrap().getBlock(filter);
               if (!(block instanceof AbstractBlock.InvalidBlock)) {
                  this.addSpecificBlock(block.getBlockData());
               } else {
                  if (stateError != null) {
                     MythicLogger.error("[BlockType] '" + filter + "' is not a valid block state: " + stateError);
                  } else {
                     MythicLogger.error("[BlockType] '" + filter + "' is not a valid block type.");
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean matches(AbstractLocation location) {
      AbstractBlock block = location.getBlock();
      if (!this.materials.isEmpty() && this.materials.contains(block.getBlockData().getMaterial())) {
         return true;
      } else if (this.matchesSpecificBlock(block.getBlockData())) {
         return true;
      } else {
         if (!this.tags.isEmpty()) {
            Material material = block.getBlockData().getMaterial();

            for (Tag<Material> tag : this.tags) {
               if (tag.isTagged(material)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean matchesSpecificBlock(BlockData blockData) {
      for (BlockData specific : this.specificBlocks.get(blockData.getMaterial())) {
         if (blockData.matches(specific)) {
            return true;
         }
      }

      return false;
   }

   private void addSpecificBlock(BlockData blockData) {
      this.specificBlocks.put(blockData.getMaterial(), blockData);
   }

   public boolean matches(Block block) {
      if (!this.materials.isEmpty() && this.materials.contains(block.getType())) {
         return true;
      } else if (this.matchesSpecificBlock(block.getBlockData())) {
         return true;
      } else {
         if (!this.tags.isEmpty()) {
            Material material = block.getBlockData().getMaterial();

            for (Tag<Material> tag : this.tags) {
               if (tag.isTagged(material)) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
