package com.nexomc.nexo.utils;

import kotlin.Metadata;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.ranges.IntRange;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000,\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\u001c\u0010\u0002\u001a\u0014\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u0005\u001aW\u0010\u0006\u001a\u00020\u0007*\u00020\u00052K\u0010\b\u001aG\u0012\u0013\u0012\u00110\n¢\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\u0013\u0012\u00110\n¢\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u000e\u0012\u0013\u0012\u00110\n¢\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u000f\u0012\u0004\u0012\u00020\u00070\t*\n\u0010\u0000\"\u00020\u00012\u00020\u0001¨\u0006\u0010"},
   d2 = {"FurnitureTransform", "Lorg/bukkit/entity/ItemDisplay$ItemDisplayTransform;", "toBlockIntRanges", "Lkotlin/Triple;", "Lkotlin/ranges/IntRange;", "Lorg/bukkit/util/BoundingBox;", "forEachBlock", "", "block", "Lkotlin/Function3;", "", "Lkotlin/ParameterName;", "name", "x", "y", "z", "com.nexomc:core"}
)
public final class FurnitureHelpersKt {
   @NotNull
   public static final Triple<IntRange, IntRange, IntRange> toBlockIntRanges(@NotNull BoundingBox boundingBox) {
      return TuplesKt.to(kotlin.TuplesKt.to(new IntRange(UtilsKt.floorToInt(boundingBox.getMinX()), UtilsKt.floorToInt(boundingBox.getMaxX())), new IntRange(UtilsKt.floorToInt(boundingBox.getMinY()), UtilsKt.floorToInt(boundingBox.getMaxY()))), new IntRange(UtilsKt.floorToInt(boundingBox.getMinZ()), UtilsKt.floorToInt(boundingBox.getMaxZ())));
   }

   public static final void forEachBlock(@NotNull BoundingBox boundingBox, @NotNull Function3<? super Integer, ? super Integer, ? super Integer, Unit> block) {
      Triple triple = toBlockIntRanges(boundingBox);
      IntRange xRange = (IntRange)triple.component1();
      IntRange yRange = (IntRange)triple.component2();
      IntRange zRange = (IntRange)triple.component3();
      int x = xRange.getFirst();
      int last = xRange.getLast();
      if (x <= last) {
         while(true) {
            int y = yRange.getFirst();
            int value = yRange.getLast();
            if (y <= value) {
               while(true) {
                  int z = zRange.getFirst();
                  int value2 = zRange.getLast();
                  if (z <= value2) {
                     while(true) {
                        block.invoke(x, y, z);
                        if (z == value2) {
                           break;
                        }

                        ++z;
                     }
                  }

                  if (y == value) {
                     break;
                  }

                  ++y;
               }
            }

            if (x == last) {
               break;
            }

            ++x;
         }
      }

   }
}
