package com.nexomc.nexo.utils;

import java.util.Arrays;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\"\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\t0\rJ\u0019\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000f2\u0006\u0010\u0010\u001a\u00020\u0011¢\u0006\u0002\u0010\u0012J\u001a\u0010\u0013\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\u0011H\u0002R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0016"},
   d2 = {"Lcom/nexomc/nexo/utils/LocationUtils;", "", "<init>", "()V", "CHUNK_RANGE_XZ", "Lkotlin/ranges/IntRange;", "getCHUNK_RANGE_XZ", "()Lkotlin/ranges/IntRange;", "blocksWithCustomData", "", "world", "Lorg/bukkit/World;", "block", "Lkotlin/Function1;", "Lorg/bukkit/block/Block;", "", "chunk", "Lorg/bukkit/Chunk;", "(Lorg/bukkit/Chunk;)[Lorg/bukkit/block/Block;", "blockFromKey", "key", "Lnet/kyori/adventure/key/Key;", "com.nexomc:core"}
)
public final class LocationUtils {
   @NotNull
   public static final LocationUtils INSTANCE = new LocationUtils();
   @NotNull
   private static final IntRange CHUNK_RANGE_XZ = RangesKt.until(0, 16);

   private LocationUtils() {
   }

   @NotNull
   public final IntRange getCHUNK_RANGE_XZ() {
      return CHUNK_RANGE_XZ;
   }

   public final void blocksWithCustomData(@NotNull World world, @NotNull Function1<? super Block, Unit> block) {
      Chunk[] loadedChunks = world.getLoadedChunks();
      Object[] objs = loadedChunks;
      int value = 0;

      for(int i = objs.length; value < i; ++value) {
         Object element = objs[value];
         Chunk chunk = (Chunk)element;
         int value2 = 0;
         LocationUtils locationUtils = INSTANCE;
         Object[] objs2 = locationUtils.blocksWithCustomData(chunk);
         int value3 = 0;

         for(int j = objs2.length; value3 < j; ++value3) {
            Object obj = objs2[value3];
            ((Function1)block).invoke(obj);
         }
      }

   }

   @NotNull
   public final Block[] blocksWithCustomData(@NotNull Chunk chunk) {
      Set keys = chunk.getPersistentDataContainer().getKeys();
      Iterable iterable = (Iterable)keys;
      Object result = null;
      result = new Block[CollectionsKt.count(iterable)];
      int index = 0;

      for(Object item : iterable) {
         NamespacedKey key = (NamespacedKey)item;
         int value = 0;
         LocationUtils locationUtils = INSTANCE;
         Block block = locationUtils.blockFromKey((Key)key, chunk);
         if (block != null) {
            Object it = block;
            int value2 = 0;
            if (index >= ((Object[])result).length) {
               Object[] objs = Arrays.copyOf((Object[])result, Math.max(((Object[])result).length * 2, CollectionsKt.count(iterable)));
               result = objs;
            }

            int value3 = index++;
            ((Object[])result)[value3] = it;
         }
      }

      Object[] objs2 = Arrays.copyOf((Object[])result, index);
      Object[] objs3 = objs2;
      if (objs3 == null) {
         throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<org.bukkit.block.Block>");
      } else {
         return (Block[])((Block[])objs3);
      }
   }

   private final Block blockFromKey(Key key, Chunk chunk) {
      if (!Intrinsics.areEqual(key.namespace(), "nexo")) {
         return null;
      } else {
         String str = key.value();
         String value = str;
         Integer number = StringsKt.toIntOrNull(StringUtilsKt.substringBetween(value, "x", "y"));
         if (number != null) {
            Integer number2 = number;
            IntRange intRange = CHUNK_RANGE_XZ;
            int p0 = ((Number)number2).intValue();
            int value2 = 0;
            number = intRange.contains(p0) ? number2 : null;
            if (number != null) {
               int x = number;
               number = StringsKt.toIntOrNull(StringsKt.substringAfter(value, "z", value));
               if (number != null) {
                  Integer number3 = number;
                  IntRange it = CHUNK_RANGE_XZ;
                  value2 = ((Number)number3).intValue();
                  int value3 = 0;
                  number = it.contains(value2) ? number3 : null;
                  if (number != null) {
                     int z = number;
                     number = StringsKt.toIntOrNull(StringUtilsKt.substringBetween(value, "y", "z"));
                     if (number != null) {
                        int y = number;
                        World world = chunk.getWorld();
                        value2 = 0;
                        IntRange intRange2 = RangesKt.until(world.getMinHeight(), world.getMaxHeight());
                        int first = intRange2.getFirst();
                        if (!(y <= intRange2.getLast() ? first <= y : false)) {
                           return null;
                        }

                        return chunk.getBlock(x, y, z);
                     }

                     return null;
                  }
               }

               return null;
            }
         }

         return null;
      }
   }
}
