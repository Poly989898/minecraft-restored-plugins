package com.nexomc.nexo.mechanics.furniture.hitbox;

import com.nexomc.nexo.mechanics.furniture.BlockLocation;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import kotlin.Metadata;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\u0018\u00002\u00020\u0001B!\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007B\u0011\b\u0016\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\u0006\u0010\nB\u0011\b\u0016\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\u0006\u0010\rB\u0019\b\u0016\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\u000e\u001a\u00020\u000f¢\u0006\u0004\b\u0006\u0010\u0010J\u000e\u0010\u0011\u001a\u00020\u00152\u0006\u0010\u000e\u001a\u00020\u000fJ\u0010\u0010\u0016\u001a\u00020\u00002\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018R\u0011\u0010\u0011\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0019"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/hitbox/BarrierHitbox;", "Lcom/nexomc/nexo/mechanics/furniture/BlockLocation;", "x", "", "y", "z", "<init>", "(III)V", "hitboxString", "", "(Ljava/lang/String;)V", "location", "Lorg/bukkit/Location;", "(Lorg/bukkit/Location;)V", "waterloggable", "", "(Lorg/bukkit/Location;Z)V", "barrierData", "Lorg/bukkit/block/data/Waterlogged;", "getBarrierData", "()Lorg/bukkit/block/data/Waterlogged;", "Lorg/bukkit/block/data/BlockData;", "from", "hitboxObject", "", "com.nexomc:core"}
)
public final class BarrierHitbox extends BlockLocation {
   @NotNull
   private final Waterlogged barrierData;

   @NotNull
   public final Waterlogged getBarrierData() {
      return this.barrierData;
   }

   @NotNull
   public final BlockData barrierData(boolean waterloggable) {
      return this.barrierData.isWaterlogged() && waterloggable ? (BlockData)this.barrierData : (BlockData)IFurniturePacketManager.Companion.getBARRIER_DATA();
   }

   @NotNull
   public final BarrierHitbox from(@Nullable Object hitboxObject) {
      return hitboxObject instanceof String ? new BarrierHitbox((String)hitboxObject) : new BarrierHitbox("0,0,0");
   }

   public BarrierHitbox(int x, int y, int z) {
      super(x, y, z);
      this.barrierData = IFurniturePacketManager.Companion.getBARRIER_DATA();
   }

   public BarrierHitbox(@NotNull String hitboxString) {
      super(hitboxString);
      this.barrierData = IFurniturePacketManager.Companion.getBARRIER_DATA();
   }

   public BarrierHitbox(@NotNull Location location) {
      super(location);
      Waterlogged barrieR_DATA = IFurniturePacketManager.Companion.getBARRIER_DATA();
      int value = 0;
      boolean flag = location.getBlock().getType() == Material.WATER;
      Waterlogged waterlogged = !flag ? barrieR_DATA : null;
      if ((!flag ? barrieR_DATA : null) == null) {
         waterlogged = IFurniturePacketManager.Companion.getBARRIER_DATA_WATERLOGGED();
      }

      this.barrierData = waterlogged;
   }

   public BarrierHitbox(@NotNull Location location, boolean waterloggable) {
      super(location);
      Waterlogged barrieR_DATA = IFurniturePacketManager.Companion.getBARRIER_DATA();
      int value = 0;
      boolean flag = waterloggable && location.getBlock().getType() == Material.WATER;
      Waterlogged waterlogged = !flag ? barrieR_DATA : null;
      if ((!flag ? barrieR_DATA : null) == null) {
         waterlogged = IFurniturePacketManager.Companion.getBARRIER_DATA_WATERLOGGED();
      }

      this.barrierData = waterlogged;
   }
}
