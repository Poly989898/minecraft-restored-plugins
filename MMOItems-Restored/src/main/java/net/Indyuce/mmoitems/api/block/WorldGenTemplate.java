package net.Indyuce.mmoitems.api.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;

public class WorldGenTemplate {
   private final String id;
   private final double chunkChance;
   private final int minDepth;
   private final int maxDepth;
   private final int veinSize;
   private final int veinCount;
   private final List<Material> replaceable = new ArrayList<>();
   private final List<Material> bordering = new ArrayList<>();
   private final List<Material> notBordering = new ArrayList<>();
   private final List<String> worldWhitelist = new ArrayList<>();
   private final List<String> worldBlacklist = new ArrayList<>();
   private final List<String> biomeWhitelist = new ArrayList<>();
   private final List<String> biomeBlacklist = new ArrayList<>();
   private final boolean slimeChunk;

   public WorldGenTemplate(ConfigurationSection section) {
      Validate.notNull(section, "Could not read gen template config");
      this.id = section.getName().toLowerCase().replace(" ", "-").replace("_", "-");
      section.getStringList("replace").forEach(var1x -> this.replaceable.add(Material.valueOf(var1x.toUpperCase().replace("-", "_").replace(" ", "_"))));
      section.getStringList("bordering").forEach(var1x -> this.bordering.add(Material.valueOf(var1x.toUpperCase().replace("-", "_").replace(" ", "_"))));
      section.getStringList("not-bordering").forEach(var1x -> this.notBordering.add(Material.valueOf(var1x.toUpperCase().replace("-", "_").replace(" ", "_"))));

      for (String text : section.getStringList("worlds")) {
         (text.startsWith("!") ? this.worldBlacklist : this.worldWhitelist).add(text.toLowerCase().replace("_", "-"));
      }

      for (String text : section.getStringList("biomes")) {
         (text.startsWith("!") ? this.biomeBlacklist : this.biomeWhitelist).add(text.toUpperCase().replace("-", "_").replace(" ", "_"));
      }

      this.chunkChance = section.getDouble("chunk-chance");
      this.slimeChunk = section.getBoolean("slime-chunk", false);
      String[] texts = section.getString("depth").split("=");
      this.minDepth = Integer.parseInt(texts[0]);
      this.maxDepth = Integer.parseInt(texts[1]);
      this.veinSize = section.getInt("vein-size");
      this.veinCount = section.getInt("vein-count");
      Validate.isTrue(this.veinSize > 0 && this.veinCount > 0, "Vein size and count must be at least 1");
   }

   public String getId() {
      return this.id;
   }

   public double getChunkChance() {
      return this.chunkChance;
   }

   public int getVeinSize() {
      return this.veinSize;
   }

   public int getVeinCount() {
      return this.veinCount;
   }

   public int getMinDepth() {
      return this.minDepth;
   }

   public int getMaxDepth() {
      return this.maxDepth;
   }

   public boolean canGenerateInWorld(String text) {
      String configKey = text.toLowerCase().replace("_", "-");
      return !this.worldWhitelist.isEmpty() && !this.worldWhitelist.contains(configKey)
         ? false
         : this.worldBlacklist.isEmpty() || !this.worldBlacklist.contains(configKey);
   }

   public boolean canGenerate(Location location) {
      Biome biome = location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      if ((this.biomeWhitelist.isEmpty() || this.biomeWhitelist.contains(biome.name()))
         && (this.biomeBlacklist.isEmpty() || !this.biomeBlacklist.contains(biome.name()))) {
         if (this.slimeChunk && !location.getChunk().isSlimeChunk()) {
            return false;
         } else if (!this.bordering.isEmpty() && !this.checkIfBorderingBlocks(location)) {
            return false;
         } else {
            return !this.notBordering.isEmpty() ? this.checkIfNotBorderingBlocks(location) : true;
         }
      } else {
         return false;
      }
   }

   public boolean canReplace(Material material) {
      return this.replaceable.isEmpty() || this.replaceable.contains(material);
   }

   public boolean canBorder(Material material) {
      return this.bordering.isEmpty() || this.bordering.contains(material);
   }

   public boolean checkIfBorderingBlocks(Location location) {
      return Arrays.stream(BlockFace.values()).map(location.getBlock()::getRelative).<Material>map(Block::getType).allMatch(this::canBorder);
   }

   public boolean canNotBorder(Material material) {
      return !this.notBordering.isEmpty() && this.notBordering.contains(material);
   }

   public boolean checkIfNotBorderingBlocks(Location location) {
      return Arrays.stream(BlockFace.values()).map(location.getBlock()::getRelative).<Material>map(Block::getType).allMatch(this::canNotBorder);
   }
}
