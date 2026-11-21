package com.nexomc.nexo.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.text.StringsKt;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u0005*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0010\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u0017J\u0016\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u0017¨\u0006\u0019"},
   d2 = {"Lcom/nexomc/nexo/utils/VectorUtils;", "", "<init>", "()V", "toLocation", "Lorg/bukkit/Location;", "Lorg/joml/Vector3f;", "world", "Lorg/bukkit/World;", "quaternionfFromString", "Lorg/joml/Quaternionf;", "quaternion", "", "defaultValue", "", "vector3fFromString", "vector", "vectorFromString", "Lorg/bukkit/util/Vector;", "rotateAroundAxisX", "", "v", "angle", "", "rotateAroundAxisY", "com.nexomc:core"}
)
public final class VectorUtils {
   @NotNull
   public static final VectorUtils INSTANCE = new VectorUtils();

   private VectorUtils() {
   }

   @NotNull
   public final Location toLocation(@NotNull Vector3f vector3f, @NotNull World world) {
      return new Location(world, (double)vector3f.x, (double)vector3f.y, (double)vector3f.z);
   }

   @NotNull
   public final Quaternionf quaternionfFromString(@NotNull String quaternion, float defaultValue) {
      List list;
      block: {
         CharSequence charSequence = (CharSequence)StringUtilsKt.removeSpaces(quaternion);
         String[] strs = new String[]{","};
         List list2 = StringsKt.split(charSequence, strs, false, 0);
         if (!list2.isEmpty()) {
            ListIterator listIterator = list2.listIterator(list2.size());

            while(listIterator.hasPrevious()) {
               String p0 = (String)listIterator.previous();
               int value = 0;
               if (((CharSequence)p0).length() != 0) {
                  list = CollectionsKt.take((Iterable)list2, listIterator.nextIndex() + 1);
                  break block;
               }
            }
         }

         list = CollectionsKt.emptyList();
      }

      Iterable iterable = (Iterable)list;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String it = (String)item;
         int value2 = 0;
         Float number = StringsKt.toFloatOrNull(it);
         destination.add(number != null ? number : defaultValue);
      }

      List floats = CollectionsKt.toMutableList((Collection)((List)destination));

      while(floats.size() < 4) {
         ((Collection)floats).add(defaultValue);
      }

      return new Quaternionf(((Number)floats.get(0)).floatValue(), ((Number)floats.get(1)).floatValue(), ((Number)floats.get(2)).floatValue(), ((Number)floats.get(3)).floatValue());
   }

   @NotNull
   public final Vector3f vector3fFromString(@NotNull String vector, float defaultValue) {
      List list;
      block: {
         CharSequence charSequence = (CharSequence)StringUtilsKt.removeSpaces(vector);
         String[] strs = new String[]{","};
         List list2 = StringsKt.split(charSequence, strs, false, 0);
         if (!list2.isEmpty()) {
            ListIterator listIterator = list2.listIterator(list2.size());

            while(listIterator.hasPrevious()) {
               String p0 = (String)listIterator.previous();
               int value = 0;
               if (((CharSequence)p0).length() != 0) {
                  list = CollectionsKt.take((Iterable)list2, listIterator.nextIndex() + 1);
                  break block;
               }
            }
         }

         list = CollectionsKt.emptyList();
      }

      Iterable iterable = (Iterable)list;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String it = (String)item;
         int value2 = 0;
         Float number = StringsKt.toFloatOrNull(it);
         destination.add(number != null ? number : defaultValue);
      }

      List floats = CollectionsKt.toMutableList((Collection)((List)destination));

      while(floats.size() < 3) {
         floats.add(defaultValue);
      }

      return new Vector3f(((Number)floats.get(0)).floatValue(), ((Number)floats.get(1)).floatValue(), ((Number)floats.get(2)).floatValue());
   }

   @NotNull
   public final Vector vectorFromString(@NotNull String vector, float defaultValue) {
      List list;
      block: {
         CharSequence charSequence = (CharSequence)StringUtilsKt.removeSpaces(vector);
         String[] strs = new String[]{","};
         List list2 = StringsKt.split(charSequence, strs, false, 0);
         if (!list2.isEmpty()) {
            ListIterator listIterator = list2.listIterator(list2.size());

            while(listIterator.hasPrevious()) {
               String p0 = (String)listIterator.previous();
               int value = 0;
               if (((CharSequence)p0).length() != 0) {
                  list = CollectionsKt.take((Iterable)list2, listIterator.nextIndex() + 1);
                  break block;
               }
            }
         }

         list = CollectionsKt.emptyList();
      }

      Iterable iterable = (Iterable)list;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String it = (String)item;
         int value2 = 0;
         Float number = StringsKt.toFloatOrNull(it);
         destination.add(number != null ? number : defaultValue);
      }

      List floats = CollectionsKt.toMutableList((Collection)((List)destination));

      while(floats.size() < 3) {
         floats.add(defaultValue);
      }

      return new Vector(((Number)floats.get(0)).floatValue(), ((Number)floats.get(1)).floatValue(), ((Number)floats.get(2)).floatValue());
   }

   public final void rotateAroundAxisX(@NotNull Vector v, double angle) {
      Pair pair = kotlin.TuplesKt.to(Math.cos(angle), Math.sin(angle));
      double cos = ((Number)pair.component1()).doubleValue();
      double sin = ((Number)pair.component2()).doubleValue();
      double y = v.getY() * cos - v.getZ() * sin;
      double z = v.getY() * sin + v.getZ() * cos;
      v.setY(y).setZ(z);
   }

   public final void rotateAroundAxisY(@NotNull Vector v, double angle) {
      Pair pair = kotlin.TuplesKt.to(Math.cos(angle), Math.sin(angle));
      double cos = ((Number)pair.component1()).doubleValue();
      double sin = ((Number)pair.component2()).doubleValue();
      double x = v.getX() * cos + v.getZ() * sin;
      double z = v.getX() * -sin + v.getZ() * cos;
      v.setX(x).setZ(z);
   }
}
