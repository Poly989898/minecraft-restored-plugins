package com.nexomc.nexo.mechanics.furniture.placements;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u0000 \u00072\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0007B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/placements/PlacementType;", "", "<init>", "(Ljava/lang/String;I)V", "FLOOR", "WALL", "ROOF", "Companion", "com.nexomc:core"}
)
public enum PlacementType {
   FLOOR,
   WALL,
   ROOF;

   @NotNull
   public static final Companion Companion = new Companion();


   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/placements/PlacementType$Companion;", "", "<init>", "()V", "fromBlockFace", "Lcom/nexomc/nexo/mechanics/furniture/placements/PlacementType;", "blockFace", "Lorg/bukkit/block/BlockFace;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final PlacementType fromBlockFace(@NotNull BlockFace blockFace) {
         return blockFace == BlockFace.DOWN ? PlacementType.ROOF : (blockFace.getModY() == 0 ? PlacementType.WALL : PlacementType.FLOOR);
      }
   }
}
