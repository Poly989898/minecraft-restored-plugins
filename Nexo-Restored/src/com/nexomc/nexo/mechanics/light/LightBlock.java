package com.nexomc.nexo.mechanics.light;

import com.nexomc.nexo.mechanics.furniture.BlockLocation;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Light;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u0000\n\u0000\u0018\u00002\u00020\u0001B\u0011\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u0019\b\u0016\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\u0004\u0010\nB!\b\u0016\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\u0004\u0010\rB)\b\u0016\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u000f\u0012\u0006\u0010\u0011\u001a\u00020\u000f\u0012\u0006\u0010\u0012\u001a\u00020\u000f¢\u0006\u0004\b\u0004\u0010\u0013J\u0010\u0010\u0018\u001a\u00020\u00002\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aR\u0011\u0010\u0012\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017¨\u0006\u001b"},
   d2 = {"Lcom/nexomc/nexo/mechanics/light/LightBlock;", "Lcom/nexomc/nexo/mechanics/furniture/BlockLocation;", "hitboxString", "", "<init>", "(Ljava/lang/String;)V", "location", "Lorg/bukkit/Location;", "lightData", "Lorg/bukkit/block/data/type/Light;", "(Lorg/bukkit/Location;Lorg/bukkit/block/data/type/Light;)V", "waterlogged", "", "(Lorg/bukkit/Location;Lorg/bukkit/block/data/type/Light;Z)V", "x", "", "y", "z", "lightLevel", "(IIII)V", "getLightLevel", "()I", "getLightData", "()Lorg/bukkit/block/data/type/Light;", "from", "hitboxObject", "", "com.nexomc:core"}
)
public final class LightBlock extends BlockLocation {
   private final int lightLevel;
   @NotNull
   private final Light lightData;

   public final int getLightLevel() {
      return this.lightLevel;
   }

   @NotNull
   public final Light getLightData() {
      return this.lightData;
   }

   @NotNull
   public final LightBlock from(@Nullable Object hitboxObject) {
      return hitboxObject instanceof String ? new LightBlock((String)hitboxObject) : new LightBlock("0,0,0 15");
   }

   public LightBlock(@NotNull String hitboxString) {
      super(StringsKt.substringBefore(hitboxString, " ", hitboxString));
      Integer number = StringsKt.toIntOrNull(StringsKt.substringAfter(hitboxString, " ", hitboxString));
      this.lightLevel = number != null ? RangesKt.coerceIn(number.intValue(), 0, 15) : 15;
      BlockData blockData = Material.LIGHT.createBlockData((a0_ -> LightBlock._init_$lambda$1((b0_ -> LightBlock._init_$lambda$0(this, (BlockData)b0_)), a0_)));
      this.lightData = (Light)blockData;
   }

   public LightBlock(@NotNull Location location, @NotNull Light lightData) {
      super(location);
      this.lightLevel = lightData.getLevel();
      int value = 0;
      lightData.setWaterlogged(location.getBlock().getType() == Material.WATER);
      this.lightData = lightData;
   }

   public LightBlock(@NotNull Location location, @NotNull Light lightData, boolean waterlogged) {
      super(location);
      this.lightLevel = lightData.getLevel();
      int value = 0;
      lightData.setWaterlogged(waterlogged);
      this.lightData = lightData;
   }

   public LightBlock(int x, int y, int z, int lightLevel) {
      super(x, y, z);
      this.lightLevel = lightLevel;
      BlockData blockData = Material.LIGHT.createBlockData((a0_ -> LightBlock._init_$lambda$5((b0_ -> LightBlock._init_$lambda$4(lightLevel, (BlockData)b0_)), a0_)));
      this.lightData = (Light)blockData;
   }

   private static final Unit _init_$lambda$0(LightBlock lightBlock, BlockData it) {
      ((Light)it).setLevel(lightBlock.lightLevel);
      return Unit.INSTANCE;
   }

   private static final void _init_$lambda$1(Function1 tmp0, Object p0) {
      tmp0.invoke(p0);
   }

   private static final Unit _init_$lambda$4(int value, BlockData it) {
      ((Light)it).setLevel(value);
      return Unit.INSTANCE;
   }

   private static final void _init_$lambda$5(Function1 tmp0, Object p0) {
      tmp0.invoke(p0);
   }
}
