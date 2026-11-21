package com.nexomc.nexo.mechanics.furniture.hitbox;

import com.nexomc.nexo.utils.VectorUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u0000 *2\u00020\u0001:\u0001*B9\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fB\u0011\b\u0016\u0012\u0006\u0010\r\u001a\u00020\u000e¢\u0006\u0004\b\u000b\u0010\u000fJ\u000e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u0007\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eJ\t\u0010\u001f\u001a\u00020\u0003HÆ\u0003J\t\u0010 \u001a\u00020\u0005HÆ\u0003J\t\u0010!\u001a\u00020\u0005HÆ\u0003J\t\u0010\"\u001a\u00020\bHÆ\u0003J\t\u0010#\u001a\u00020\nHÆ\u0003J;\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nHÆ\u0001J\u0014\u0010%\u001a\u00020\n2\b\u0010&\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010'\u001a\u00020(HÖ\u0081\u0004J\n\u0010)\u001a\u00020\u000eHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018¨\u0006+"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/hitbox/ShulkerHitbox;", "", "offset", "Lorg/joml/Vector3f;", "scale", "", "length", "direction", "Lorg/bukkit/block/BlockFace;", "visible", "", "<init>", "(Lorg/joml/Vector3f;DDLorg/bukkit/block/BlockFace;Z)V", "hitboxString", "", "(Ljava/lang/String;)V", "getOffset", "()Lorg/joml/Vector3f;", "getScale", "()D", "getLength", "getDirection", "()Lorg/bukkit/block/BlockFace;", "getVisible", "()Z", "angle", "", "boundingBox", "Lorg/bukkit/util/BoundingBox;", "center", "Lorg/bukkit/Location;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "Companion", "com.nexomc:core"}
)
public final class ShulkerHitbox {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Vector3f offset;
   private final double scale;
   private final double length;
   @NotNull
   private final BlockFace direction;
   private final boolean visible;
   @NotNull
   private static ShulkerHitbox DEFAULT = new ShulkerHitbox(new org.joml.Vector3f(), 1.0D, 1.0D, org.bukkit.block.BlockFace.DOWN, false);

   public ShulkerHitbox(@NotNull Vector3f offset, double scale, double length, @NotNull BlockFace direction, boolean visible) {
      super();
      this.offset = offset;
      this.scale = scale;
      this.length = length;
      this.direction = direction;
      this.visible = visible;
   }

   @NotNull
   public final Vector3f getOffset() {
      return this.offset;
   }

   public final double getScale() {
      return this.scale;
   }

   public final double getLength() {
      return this.length;
   }

   @NotNull
   public final BlockFace getDirection() {
      return this.direction;
   }

   public final boolean getVisible() {
      return this.visible;
   }

   public ShulkerHitbox(@NotNull String hitboxString) {
      double value;
      /* this-alias removed */
      VectorUtils vectorUtils = VectorUtils.INSTANCE;
      CharSequence charSequence = (CharSequence)hitboxString;
      String[] strs = new String[]{" "};
      String str = CollectionsKt.firstOrNull(StringsKt.split(charSequence, strs, false, 0));
      if (str == null) {
         str = "0,0,0";
      }

      Vector3f vector3f = vectorUtils.vector3fFromString(str, 0.0F);
      CharSequence charSequence2 = (CharSequence)hitboxString;
      strs = new String[]{" "};
      List list = StringsKt.split(charSequence2, strs, false, 0);
      byte b2 = 1;
      Object obj;
      if (b2 < list.size()) {
         obj = list.get(b2);
      } else {
         Vector3f vector3f2 = vector3f;
         int value2 = 0;
         String str2 = "1.0";
         /* this-alias removed */
         vector3f = vector3f2;
         obj = str2;
      }

      Double number = StringsKt.toDoubleOrNull((String)obj);
      double value3 = number != null ? number : (double)1.0F;
      CharSequence charSequence3 = (CharSequence)hitboxString;
      String[] strs2 = new String[]{" "};
      List list2 = StringsKt.split(charSequence3, strs2, false, 0);
      b2 = 2;
      Object obj2;
      if (b2 < list2.size()) {
         obj2 = list2.get(b2);
      } else {
         double value4 = value3;
         Vector3f vector3f3 = vector3f;
         int value5 = 0;
         String str3 = "1.0";
         vector3f = vector3f3;
         value3 = value4;
         obj2 = str3;
      }

      BlockFace blockFace;
      block: {
         Double number2 = StringsKt.toDoubleOrNull((String)obj2);
         value = number2 != null ? Math.max(1.0, Math.min(2.0, number2)) : (double)1.0F;
         CharSequence charSequence4 = (CharSequence)hitboxString;
         String[] strs3 = new String[]{" "};
         String orNull = CollectionsKt.getOrNull(StringsKt.split(charSequence4, strs3, false, 0), 3);
         if (orNull != null) {
            String it = orNull;
            double value6 = value;
            double value7 = value3;
            Vector3f vector3f4 = vector3f;
            int value8 = 0;
            Iterable iterable = (Iterable)ShulkerHitbox.EntriesMappings.entries$0;
            Iterator iterator = iterable.iterator();

            BlockFace blockFace2;
            while(true) {
               if (!iterator.hasNext()) {
                  blockFace2 = null;
                  break;
               }

               Object obj3 = iterator.next();
               BlockFace b = (BlockFace)obj3;
               int value9 = 0;
               String str4 = b.name().toUpperCase(Locale.ROOT);
               if (Intrinsics.areEqual(str4, it)) {
                  blockFace2 = b;
                  break;
               }
            }

            BlockFace blockFace3 = blockFace2;
            vector3f = vector3f4;
            value3 = value7;
            value = value6;
            blockFace = blockFace3;
            if (blockFace3 != null) {
               break block;
            }
         }

         blockFace = BlockFace.DOWN;
      }

      CharSequence charSequence5 = (CharSequence)hitboxString;
      String[] strs4 = new String[]{" "};
      List list3 = StringsKt.split(charSequence5, strs4, false, 0);
      byte b3 = 4;
      Object obj4;
      if (b3 < list3.size()) {
         obj4 = list3.get(b3);
      } else {
         BlockFace blockFace4 = blockFace;
         double value10 = value;
         double value11 = value3;
         Vector3f vector3f5 = vector3f;
         int it = 0;
         CharSequence charSequence6 = (CharSequence)hitboxString;
         String[] strs5 = new String[]{" "};
         List list4 = StringsKt.split(charSequence6, strs5, false, 0);
         byte b4 = 3;
         Object obj5;
         if (b4 < list4.size()) {
            obj5 = list4.get(b4);
         } else {
            int value12 = 0;
            obj5 = "false";
         }

         String str5 = (String)obj5;
         vector3f = vector3f5;
         value3 = value11;
         value = value10;
         blockFace = blockFace4;
         obj4 = str5;
      }

      this(vector3f, value3, value, blockFace, Boolean.parseBoolean((String)obj4));
   }

   @NotNull
   public final Vector3f offset(float angle) {
      float angle_3 = angle;
      if (angle_3 < 0.0F) {
         angle_3 = angle_3 + 360.0F;
      }

      float radians = Math.toRadians(angle_3);
      float x = this.offset.x * (float)java.lang.Math.cos((double)radians) - -this.offset.z * (float)java.lang.Math.sin((double)radians);
      float z = this.offset.x * (float)java.lang.Math.sin((double)radians) + -this.offset.z * (float)java.lang.Math.cos((double)radians);
      return new Vector3f(x, this.offset.y, z);
   }

   @NotNull
   public final Vector3f direction(float angle) {
      float angle_2 = angle;
      if (angle_2 < 0.0F) {
         angle_2 = angle_2 + 360.0F;
      }

      Vector vector;
      switch (ShulkerHitbox.WhenMappings.$EnumSwitchMapping$0[this.direction.ordinal()]) {
         case 1:
            vector = new Vector(0, 0, -1);
            break;
         case 2:
            vector = new Vector(0, 0, 1);
            break;
         case 3:
            vector = new Vector(-1, 0, 0);
            break;
         case 4:
            vector = new Vector(1, 0, 0);
            break;
         default:
            Vector3f toVector3f = this.direction.getDirection().toVector3f();
            return toVector3f;
      }

      Vector dirVector = vector;
      double radians = Math.toRadians((double)angle_2);
      double x = dirVector.getX() * java.lang.Math.cos(radians) - -dirVector.getZ() * java.lang.Math.sin(radians);
      double z = dirVector.getX() * java.lang.Math.sin(radians) + -dirVector.getZ() * java.lang.Math.cos(radians);
      Vector rotatedVector = new Vector(x, (double)0.0F, z);
      BlockFace[] blockFaces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};
      Iterable iterable = (Iterable)CollectionsKt.listOf(blockFaces);
      Iterator iterator = iterable.iterator();
      Object obj;
      if (!iterator.hasNext()) {
         obj = null;
      } else {
         Object minElem = iterator.next();
         if (!iterator.hasNext()) {
            obj = minElem;
         } else {
            BlockFace it = (BlockFace)minElem;
            int value = 0;
            double minValue = it.getDirection().distance(rotatedVector);

            do {
               Object obj2 = iterator.next();
               BlockFace it_1 = (BlockFace)obj2;
               int value2 = 0;
               double value3 = it_1.getDirection().distance(rotatedVector);
               if (Double.compare(minValue, value3) > 0) {
                  minElem = obj2;
                  minValue = value3;
               }
            } while(iterator.hasNext());

            obj = minElem;
         }
      }

      BlockFace blockFace = (BlockFace)obj;
      if (blockFace == null) {
         blockFace = this.direction;
      }

      Vector3f vector3f = blockFace.getDirection().toVector3f();
      return vector3f;
   }

   @NotNull
   public final BoundingBox boundingBox(@NotNull Location center) {
      BoundingBox boundingBox = BoundingBox.of(center, this.scale / (double)2, this.scale * this.length / (double)2, this.scale / (double)2);
      return boundingBox;
   }

   @NotNull
   public final Vector3f component1() {
      return this.offset;
   }

   public final double component2() {
      return this.scale;
   }

   public final double component3() {
      return this.length;
   }

   @NotNull
   public final BlockFace component4() {
      return this.direction;
   }

   public final boolean component5() {
      return this.visible;
   }

   @NotNull
   public final ShulkerHitbox copy(@NotNull Vector3f offset, double scale, double length, @NotNull BlockFace direction, boolean visible) {
      return new ShulkerHitbox(offset, scale, length, direction, visible);
   }

   @NotNull
   public String toString() {
      return "ShulkerHitbox(offset=" + this.offset + ", scale=" + this.scale + ", length=" + this.length + ", direction=" + this.direction + ", visible=" + this.visible + ")";
   }

   public int hashCode() {
      int result = this.offset.hashCode();
      result = result * 31 + Double.hashCode(this.scale);
      result = result * 31 + Double.hashCode(this.length);
      result = result * 31 + this.direction.hashCode();
      result = result * 31 + Boolean.hashCode(this.visible);
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof ShulkerHitbox)) {
         return false;
      } else {
         ShulkerHitbox shulkerHitbox = (ShulkerHitbox)other;
         if (!Intrinsics.areEqual(this.offset, shulkerHitbox.offset)) {
            return false;
         } else if (Double.compare(this.scale, shulkerHitbox.scale) != 0) {
            return false;
         } else if (Double.compare(this.length, shulkerHitbox.length) != 0) {
            return false;
         } else if (this.direction != shulkerHitbox.direction) {
            return false;
         } else {
            return this.visible == shulkerHitbox.visible;
         }
      }
   }

   public ShulkerHitbox() {
      this(new org.joml.Vector3f(), 1.0D, 1.0D, org.bukkit.block.BlockFace.DOWN, false);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/hitbox/ShulkerHitbox$Companion;", "", "<init>", "()V", "DEFAULT", "Lcom/nexomc/nexo/mechanics/furniture/hitbox/ShulkerHitbox;", "getDEFAULT", "()Lcom/nexomc/nexo/mechanics/furniture/hitbox/ShulkerHitbox;", "setDEFAULT", "(Lcom/nexomc/nexo/mechanics/furniture/hitbox/ShulkerHitbox;)V", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final ShulkerHitbox getDEFAULT() {
         return ShulkerHitbox.DEFAULT;
      }

      public final void setDEFAULT(@NotNull ShulkerHitbox shulkerHitbox) {
         ShulkerHitbox.DEFAULT = shulkerHitbox;
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.block.BlockFace.values().length];
         try { a_0[org.bukkit.block.BlockFace.NORTH.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.SOUTH.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.WEST.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.EAST.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
   public static final class EntriesMappings {
      public static final kotlin.enums.EnumEntries entries$0 = kotlin.enums.EnumEntriesKt.enumEntries(org.bukkit.block.BlockFace.values());
   }
}
