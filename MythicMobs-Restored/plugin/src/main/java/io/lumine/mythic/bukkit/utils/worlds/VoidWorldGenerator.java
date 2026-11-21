package io.lumine.mythic.bukkit.utils.worlds;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class VoidWorldGenerator extends ChunkGenerator {
   private Biome biome = Biome.PLAINS;

   public final ChunkData generateChunkData(World world, Random random, int ChunkX, int ChunkZ, BiomeGrid biome) {
      ChunkData chunkData = this.createChunkData(world);

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            biome.setBiome(x, z, this.biome);
         }
      }

      return chunkData;
   }

   public final Location getFixedSpawnLocation(World world, Random random) {
      return new Location(world, 0.0, 65.0, 0.0);
   }

   public List<BlockPopulator> getDefaultPopulators(World world) {
      return Arrays.asList();
   }

   public boolean canSpawn(World world, int x, int z) {
      return true;
   }

   public int xyzToByte(int x, int y, int z) {
      return (x * 16 + z) * 128 + y;
   }

   public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
      return new byte[32768];
   }
}
