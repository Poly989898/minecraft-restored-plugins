package com.nexomc.nexo.mechanics.furniture.hitbox;

import com.nexomc.nexo.utils.VectorUtils;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.apache.commons.lang3.StringUtils;
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
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u0000  2\u00020\u0001:\u0001 B%\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bB\u0011\b\u0016\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u0007\u0010\u000bJ\u000e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0011\u001a\u00020\u0005J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0005HÆ\u0003J'\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u001d\u001a\u00020\u001eHÖ\u0081\u0004J\n\u0010\u001f\u001a\u00020\nHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000f¨\u0006!"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/hitbox/InteractionHitbox;", "", "offset", "Lorg/joml/Vector3f;", "width", "", "height", "<init>", "(Lorg/joml/Vector3f;FF)V", "hitboxString", "", "(Ljava/lang/String;)V", "getOffset", "()Lorg/joml/Vector3f;", "getWidth", "()F", "getHeight", "angle", "boundingBox", "Lorg/bukkit/util/BoundingBox;", "center", "Lorg/bukkit/Location;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "Companion", "com.nexomc:core"}
)
public final class InteractionHitbox {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Vector3f offset;
   private final float width;
   private final float height;
   @NotNull
   private static InteractionHitbox DEFAULT = new InteractionHitbox(new org.joml.Vector3f(), 1.0F, 1.0F);

   public InteractionHitbox(@NotNull Vector3f offset, float width, float height) {
      super();
      this.offset = offset;
      this.width = width;
      this.height = height;
   }

   @NotNull
   public final Vector3f getOffset() {
      return this.offset;
   }

   public final float getWidth() {
      return this.width;
   }

   public final float getHeight() {
      return this.height;
   }

   public InteractionHitbox(@NotNull String hitboxString) {
        Intrinsics.checkNotNullParameter((Object)hitboxString, "hitboxString");
        String[] stringArray = StringUtils.split(hitboxString, " ", 2);
        Intrinsics.checkNotNullExpressionValue((Object)stringArray, "split(...)");
        String string = (String)ArraysKt.firstOrNull((Object[])stringArray);
        if (string == null) {
            string = "0,0,0";
        }
        Vector3f vector3f = VectorUtils.INSTANCE.vector3fFromString(string, 0.0f);
        String[] stringArray2 = StringUtils.split(hitboxString, " ", 2);
        Intrinsics.checkNotNullExpressionValue((Object)stringArray2, "split(...)");
        Object object = (String)ArraysKt.getOrNull((Object[])stringArray2, 1);
        float f = object != null && (object = StringsKt.substringBefore((String)object, ",", (String)object)) != null && (object = StringsKt.toFloatOrNull((String)object)) != null ? ((Float)object).floatValue() : 1.0f;
        String[] stringArray3 = StringUtils.split(hitboxString, " ", 2);
        Intrinsics.checkNotNullExpressionValue((Object)stringArray3, "split(...)");
        Object object2 = (String)ArraysKt.getOrNull((Object[])stringArray3, 1);
        this(vector3f, f, object2 != null && (object2 = StringsKt.substringAfter((String)object2, ",", (String)object2)) != null && (object2 = StringsKt.toFloatOrNull((String)object2)) != null ? ((Float)object2).floatValue() : 1.0f);
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
      BoundingBox boundingBox = BoundingBox.of(center, (double)this.width / (double)2.0F, (double)this.height / (double)2.0F, (double)this.width / (double)2.0F);
      return boundingBox;
   }

   @NotNull
   public final Vector3f component1() {
      return this.offset;
   }

   public final float component2() {
      return this.width;
   }

   public final float component3() {
      return this.height;
   }

   @NotNull
   public final InteractionHitbox copy(@NotNull Vector3f offset, float width, float height) {
      return new InteractionHitbox(offset, width, height);
   }

   @NotNull
   public String toString() {
      return "InteractionHitbox(offset=" + this.offset + ", width=" + this.width + ", height=" + this.height + ")";
   }

   public int hashCode() {
      int result = this.offset.hashCode();
      result = result * 31 + Float.hashCode(this.width);
      result = result * 31 + Float.hashCode(this.height);
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof InteractionHitbox)) {
         return false;
      } else {
         InteractionHitbox interactionHitbox = (InteractionHitbox)other;
         if (!Intrinsics.areEqual(this.offset, interactionHitbox.offset)) {
            return false;
         } else if (Float.compare(this.width, interactionHitbox.width) != 0) {
            return false;
         } else {
            return Float.compare(this.height, interactionHitbox.height) == 0;
         }
      }
   }

   public InteractionHitbox() {
      this(new org.joml.Vector3f(), 1.0F, 1.0F);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/hitbox/InteractionHitbox$Companion;", "", "<init>", "()V", "DEFAULT", "Lcom/nexomc/nexo/mechanics/furniture/hitbox/InteractionHitbox;", "getDEFAULT", "()Lcom/nexomc/nexo/mechanics/furniture/hitbox/InteractionHitbox;", "setDEFAULT", "(Lcom/nexomc/nexo/mechanics/furniture/hitbox/InteractionHitbox;)V", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final InteractionHitbox getDEFAULT() {
         return InteractionHitbox.DEFAULT;
      }

      public final void setDEFAULT(@NotNull InteractionHitbox interactionHitbox) {
         InteractionHitbox.DEFAULT = interactionHitbox;
      }
   }
}
