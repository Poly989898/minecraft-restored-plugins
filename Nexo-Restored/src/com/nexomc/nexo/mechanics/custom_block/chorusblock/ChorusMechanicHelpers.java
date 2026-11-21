package com.nexomc.nexo.mechanics.custom_block.chorusblock;

import kotlin.Metadata;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusMechanicHelpers;", "", "<init>", "()V", "blockData", "Lorg/bukkit/block/data/MultipleFacing;", "customVariation", "", "com.nexomc:core"}
)
public final class ChorusMechanicHelpers {
   @NotNull
   public static final ChorusMechanicHelpers INSTANCE = new ChorusMechanicHelpers();

   private ChorusMechanicHelpers() {
   }

   @NotNull
   public final MultipleFacing blockData(int customVariation) {
      BlockData blockData = Bukkit.createBlockData(Material.CHORUS_PLANT);
      MultipleFacing chorus = (MultipleFacing)blockData;
      int value = 0;
      chorus.setFace(BlockFace.NORTH, (customVariation & 1) != 0);
      chorus.setFace(BlockFace.SOUTH, (customVariation & 2) != 0);
      chorus.setFace(BlockFace.EAST, (customVariation & 4) != 0);
      chorus.setFace(BlockFace.WEST, (customVariation & 8) != 0);
      chorus.setFace(BlockFace.UP, (customVariation & 16) != 0);
      chorus.setFace(BlockFace.DOWN, (customVariation & 32) != 0);
      return chorus;
   }
}
