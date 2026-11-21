package com.nexomc.nexo.mechanics.furniture.hitbox;

import com.nexomc.nexo.utils.VectorUtils;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bB\u0011\b\u0016\u0012\u0006\u0010\f\u001a\u00020\r¢\u0006\u0004\b\n\u0010\u000eJ\u000e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u0007J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bJ\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001f\u001a\u00020\tHÆ\u0003J1\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÆ\u0001J\u0014\u0010!\u001a\u00020\t2\b\u0010\"\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010#\u001a\u00020$HÖ\u0081\u0004J\n\u0010%\u001a\u00020\rHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016¨\u0006&"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/hitbox/GhastHitbox;", "", "offset", "Lorg/joml/Vector3f;", "scale", "", "rotation", "", "visible", "", "<init>", "(Lorg/joml/Vector3f;DFZ)V", "hitboxString", "", "(Ljava/lang/String;)V", "getOffset", "()Lorg/joml/Vector3f;", "getScale", "()D", "getRotation", "()F", "getVisible", "()Z", "angle", "boundingBox", "Lorg/bukkit/util/BoundingBox;", "center", "Lorg/bukkit/Location;", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "com.nexomc:core"}
)
public final class GhastHitbox {
   @NotNull
   private final Vector3f offset;
   private final double scale;
   private final float rotation;
   private final boolean visible;

   public GhastHitbox(@NotNull Vector3f offset, double scale, float rotation, boolean visible) {
      super();
      this.offset = offset;
      this.scale = scale;
      this.rotation = rotation;
      this.visible = visible;
   }

   @NotNull
   public final Vector3f getOffset() {
      return this.offset;
   }

   public final double getScale() {
      return this.scale;
   }

   public final float getRotation() {
      return this.rotation;
   }

   public final boolean getVisible() {
      return this.visible;
   }

   public GhastHitbox(@NotNull String hitboxString) {
      org.joml.Vector3f vector3f;
      java.lang.Double number;
      VectorUtils vectorUtils = VectorUtils.INSTANCE;
      CharSequence charSequence = (CharSequence)hitboxString;
      String[] strs = new String[]{" "};
      String str = CollectionsKt.firstOrNull(StringsKt.split(charSequence, strs, false, 0));
      if (str == null) {
         str = "0,0,0";
      }

      block: {
         vector3f = vectorUtils.vector3fFromString(str, 0.0F);
         CharSequence charSequence2 = (CharSequence)hitboxString;
         strs = new String[]{" "};
         String orNull = CollectionsKt.getOrNull(StringsKt.split(charSequence2, strs, false, 0), 1);
         if (orNull != null) {
            Double number2 = StringsKt.toDoubleOrNull(orNull);
            if (number2 != null) {
               number = number2;
               break block;
            }
         }

         number = (double)0.25F;
      }

      float value;
      block: {
         CharSequence charSequence3 = (CharSequence)hitboxString;
         strs = new String[]{" "};
         String str2 = CollectionsKt.getOrNull(StringsKt.split(charSequence3, strs, false, 0), 2);
         if (str2 != null) {
            Float number3 = StringsKt.toFloatOrNull(str2);
            if (number3 != null) {
               value = number3;
               break block;
            }
         }

         value = 0.0F;
      }

      Boolean flag = StringsKt.toBooleanStrictOrNull(StringsKt.substringAfterLast(hitboxString, " ", hitboxString));
      this(vector3f, number, value, flag != null ? flag : false);
   }

   @NotNull
   public final Vector3f offset(float angle) {
      float angle_1 = angle;
      if (angle_1 < 0.0F) {
         angle_1 = angle_1 + 360.0F;
      }

      float radians = Math.toRadians(angle_1);
      float x = this.offset.x * (float)java.lang.Math.cos((double)radians) - -this.offset.z * (float)java.lang.Math.sin((double)radians);
      float z = this.offset.x * (float)java.lang.Math.sin((double)radians) + -this.offset.z * (float)java.lang.Math.cos((double)radians);
      return new Vector3f(x, this.offset.y, z);
   }

   @NotNull
   public final BoundingBox boundingBox(@NotNull Location center) {
      BoundingBox boundingBox = BoundingBox.of(center, this.scale / (double)2.0F, this.scale / (double)2.0F, this.scale / (double)2.0F);
      return boundingBox;
   }

   @NotNull
   public final Vector3f component1() {
      return this.offset;
   }

   public final double component2() {
      return this.scale;
   }

   public final float component3() {
      return this.rotation;
   }

   public final boolean component4() {
      return this.visible;
   }

   @NotNull
   public final GhastHitbox copy(@NotNull Vector3f offset, double scale, float rotation, boolean visible) {
      return new GhastHitbox(offset, scale, rotation, visible);
   }

   @NotNull
   public String toString() {
      return "GhastHitbox(offset=" + this.offset + ", scale=" + this.scale + ", rotation=" + this.rotation + ", visible=" + this.visible + ")";
   }

   public int hashCode() {
      int result = this.offset.hashCode();
      result = result * 31 + Double.hashCode(this.scale);
      result = result * 31 + Float.hashCode(this.rotation);
      result = result * 31 + Boolean.hashCode(this.visible);
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof GhastHitbox)) {
         return false;
      } else {
         GhastHitbox ghastHitbox = (GhastHitbox)other;
         if (!Intrinsics.areEqual(this.offset, ghastHitbox.offset)) {
            return false;
         } else if (Double.compare(this.scale, ghastHitbox.scale) != 0) {
            return false;
         } else if (Float.compare(this.rotation, ghastHitbox.rotation) != 0) {
            return false;
         } else {
            return this.visible == ghastHitbox.visible;
         }
      }
   }

   public GhastHitbox() {
      this(new org.joml.Vector3f(), 1.0D, 0.0F, false);
   }
}
