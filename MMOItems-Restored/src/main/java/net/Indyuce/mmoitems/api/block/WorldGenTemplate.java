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

   public WorldGenTemplate(ConfigurationSection var1) {
      Validate.notNull(var1, "Could not read gen template config");
      this.id = var1.getName().toLowerCase().replace(" ", "-").replace("_", "-");
      var1.getStringList("replace").forEach(var1x -> this.replaceable.add(Material.valueOf(var1x.toUpperCase().replace("-", "_").replace(" ", "_"))));
      var1.getStringList("bordering").forEach(var1x -> this.bordering.add(Material.valueOf(var1x.toUpperCase().replace("-", "_").replace(" ", "_"))));
      var1.getStringList("not-bordering").forEach(var1x -> this.notBordering.add(Material.valueOf(var1x.toUpperCase().replace("-", "_").replace(" ", "_"))));

      for (String var3 : var1.getStringList("worlds")) {
         (var3.startsWith("!") ? this.worldBlacklist : this.worldWhitelist).add(var3.toLowerCase().replace("_", "-"));
      }

      for (String var6 : var1.getStringList("biomes")) {
         (var6.startsWith("!") ? this.biomeBlacklist : this.biomeWhitelist).add(var6.toUpperCase().replace("-", "_").replace(" ", "_"));
      }

      this.chunkChance = var1.getDouble("chunk-chance");
      this.slimeChunk = var1.getBoolean("slime-chunk", false);
      String[] var5 = var1.getString("depth").split("=");
      this.minDepth = Integer.parseInt(var5[0]);
      this.maxDepth = Integer.parseInt(var5[1]);
      this.veinSize = var1.getInt("vein-size");
      this.veinCount = var1.getInt("vein-count");
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

   public boolean canGenerateInWorld(String var1) {
      String var2 = var1.toLowerCase().replace("_", "-");
      return !this.worldWhitelist.isEmpty() && !this.worldWhitelist.contains(var2)
         ? false
         : this.worldBlacklist.isEmpty() || !this.worldBlacklist.contains(var2);
   }

   public boolean canGenerate(Location var1) {
      Biome var2 = var1.getWorld().getBiome(var1.getBlockX(), var1.getBlockY(), var1.getBlockZ());
      if ((this.biomeWhitelist.isEmpty() || this.biomeWhitelist.contains(var2.name()))
         && (this.biomeBlacklist.isEmpty() || !this.biomeBlacklist.contains(var2.name()))) {
         if (this.slimeChunk && !var1.getChunk().isSlimeChunk()) {
            return false;
         } else if (!this.bordering.isEmpty() && !this.checkIfBorderingBlocks(var1)) {
            return false;
         } else {
            return !this.notBordering.isEmpty() ? this.checkIfNotBorderingBlocks(var1) : true;
         }
      } else {
         return false;
      }
   }

   public boolean canReplace(Material var1) {
      return this.replaceable.isEmpty() || this.replaceable.contains(var1);
   }

   public boolean canBorder(Material var1) {
      return this.bordering.isEmpty() || this.bordering.contains(var1);
   }

   public boolean checkIfBorderingBlocks(Location var1) {
      return Arrays.stream(BlockFace.values()).map(var1.getBlock()::getRelative).<Material>map(Block::getType).allMatch(this::canBorder);
   }

   public boolean canNotBorder(Material var1) {
      return !this.notBordering.isEmpty() && this.notBordering.contains(var1);
   }

   public boolean checkIfNotBorderingBlocks(Location var1) {
      return Arrays.stream(BlockFace.values()).map(var1.getBlock()::getRelative).<Material>map(Block::getType).allMatch(this::canNotBorder);
   }
}
