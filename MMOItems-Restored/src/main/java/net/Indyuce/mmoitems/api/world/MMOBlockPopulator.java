package net.Indyuce.mmoitems.api.world;

import java.util.Map;
import java.util.Random;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import net.Indyuce.mmoitems.api.block.WorldGenTemplate;
import net.Indyuce.mmoitems.manager.WorldGenManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

public class MMOBlockPopulator extends BlockPopulator {
   private static final BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.DOWN, BlockFace.UP};
   private final WorldGenManager manager;
   private final World world;

   public MMOBlockPopulator(World var1, WorldGenManager var2) {
      this.manager = var2;
      this.world = var1;
   }

   public void populate(@NotNull WorldInfo var1, @NotNull Random var2, int var3, int var4, @NotNull LimitedRegion var5) {
      Map<CustomBlock, WorldGenTemplate> var6 = this.manager.assigned();
      var6.entrySet()
         .stream()
         .filter(var1x -> ((WorldGenTemplate)var1x.getValue()).canGenerateInWorld(var1.getName()))
         .filter(var1x -> ((WorldGenTemplate)var1x.getValue()).getMinDepth() >= var1.getMinHeight())
         .filter(var1x -> ((WorldGenTemplate)var1x.getValue()).getMaxDepth() <= var1.getMaxHeight())
         .forEach(var5x -> {
            CustomBlock var6x = (CustomBlock)var5x.getKey();
            WorldGenTemplate var7 = (WorldGenTemplate)var5x.getValue();
            if (!(var2.nextDouble() > var7.getChunkChance())) {
               for (int var8 = 0; var8 < var7.getVeinCount(); var8++) {
                  int var9 = var3 * 16 + var2.nextInt(16);
                  int var10 = var2.nextInt(var7.getMaxDepth() - var7.getMinDepth() + 1) + var7.getMinDepth();
                  int var11 = var4 * 16 + var2.nextInt(16);
                  Location var12 = new Location(this.world, var9, var10, var11);
                  if (var7.canGenerate(var12) && var12.getWorld() != null) {
                     Block var13 = var12.getWorld().getBlockAt(var12);

                     for (int var14 = 0; var14 < var7.getVeinSize(); var14++) {
                        if (var5.isInRegion(var13.getLocation())) {
                           if (var7.canReplace(var5.getType(var13.getLocation()))) {
                              var5.setType(var13.getLocation(), var6x.getState().getType());
                              var5.setBlockData(var13.getLocation(), var6x.getState().getBlockData());
                           }

                           BlockFace var15 = faces[var2.nextInt(faces.length)];
                           var13 = var13.getRelative(var15);
                        }
                     }
                  }
               }
            }
         });
   }
}
