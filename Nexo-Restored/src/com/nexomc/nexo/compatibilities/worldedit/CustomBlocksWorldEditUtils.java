package com.nexomc.nexo.compatibilities.worldedit;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockRegistry;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockType;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import javax.annotation.Nullable;
import kotlin.Metadata;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\n\u0010\b\u001a\u0006\u0012\u0002\b\u00030\t2\n\u0010\n\u001a\u0006\u0012\u0002\b\u00030\tJ \u0010\u000b\u001a\u0004\u0018\u00010\f2\u0010\b\u0001\u0010\r\u001a\u0006\u0012\u0002\b\u00030\t:\u0002\b\u000eH\u0003b\u0002\b\u000f¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/worldedit/CustomBlocksWorldEditUtils;", "", "<init>", "()V", "processBlock", "", "location", "Lorg/bukkit/Location;", "stateToSet", "Lcom/sk89q/worldedit/world/block/BlockStateHolder;", "stateBefore", "getCustomBlockByBlockState", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "blockState", "Lorg/jetbrains/annotations/NotNull;", "Ljavax/annotation/Nullable;", "com.nexomc:core"}
)
public final class CustomBlocksWorldEditUtils {
   @NotNull
   public static final CustomBlocksWorldEditUtils INSTANCE = new CustomBlocksWorldEditUtils();

   private CustomBlocksWorldEditUtils() {
   }

   public final void processBlock(@NotNull Location location, @NotNull BlockStateHolder<?> stateToSet, @NotNull BlockStateHolder<?> stateBefore) {
      CustomBlockMechanic customBlockByBlockState = this.getCustomBlockByBlockState(stateBefore);
      if (customBlockByBlockState != null) {
         CustomBlockMechanic customBlockMechanic = customBlockByBlockState;
         int value = 0;
         CustomBlockType byClass = CustomBlockRegistry.INSTANCE.getByClass(customBlockMechanic.getClass());
         if (byClass != null) {
            CustomBlockType type = byClass;
            type.removeWorldEdit(location, customBlockMechanic);
         }
      }

      customBlockByBlockState = this.getCustomBlockByBlockState(stateToSet);
      if (customBlockByBlockState != null) {
         CustomBlockMechanic customBlockMechanic2 = customBlockByBlockState;
         int value2 = 0;
         CustomBlockType customBlockType = CustomBlockRegistry.INSTANCE.getByClass(customBlockMechanic2.getClass());
         if (customBlockType != null) {
            CustomBlockType type = customBlockType;
            type.placeWorldEdit(location, customBlockMechanic2);
         }
      }

   }

   @Nullable
   private final CustomBlockMechanic getCustomBlockByBlockState(@NotNull BlockStateHolder<?> blockState) {
      BlockData blockData = BukkitAdapter.adapt((BlockStateHolder)blockState.toImmutableState());
      return NexoBlocks.customBlockMechanic(blockData);
   }
}
