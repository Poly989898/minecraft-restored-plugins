package com.nexomc.nexo.utils;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import team.unnamed.creative.base.Vector3Float;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000.\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\u001a\"\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0003\u001a\u0012\u0010\u0000\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006\u001a\"\u0010\u0000\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0003\u001a6\u0010\b\u001a\u00020\t*\u00020\t2\u0014\b\u0002\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r0\u000b2\u0014\b\u0002\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\f0\u000b¨\u0006\u000f"},
   d2 = {"plus", "Lorg/bukkit/util/Vector;", "x", "", "y", "z", "Lorg/joml/Vector3f;", "vector3f", "clampToFirstAxis", "Lteam/unnamed/creative/base/Vector3Float;", "condition", "Lkotlin/Function1;", "", "", "modify", "com.nexomc:core"}
)
public final class VectorUtilsKt {
   @NotNull
   public static final Vector plus(@NotNull Vector vector, int x, int y, int z) {
      Vector vector2 = vector.clone();
      int value = 0;
      vector2.setX(vector2.getX() + (double)x);
      vector2.setY(vector2.getY() + (double)y);
      vector2.setZ(vector2.getZ() + (double)z);
      return vector2;
   }

   @NotNull
   public static final Vector3f plus(@NotNull Vector3f vector3f2, @NotNull Vector3f vector3f) {
      return new Vector3f(vector3f2.x + vector3f.x, vector3f2.y + vector3f.y, vector3f2.z + vector3f.z);
   }

   @NotNull
   public static final Vector3f plus(@NotNull Vector3f vector3f, int x, int y, int z) {
      return new Vector3f(vector3f.x + (float)x, vector3f.y + (float)y, vector3f.z + (float)z);
   }

   @NotNull
   public static final Vector3Float clampToFirstAxis(@NotNull Vector3Float vector3Float, @NotNull Function1<? super Float, Boolean> condition, @NotNull Function1<? super Float, Float> modify) {
      return (Boolean)condition.invoke(vector3Float.x()) ? new Vector3Float(((Number)modify.invoke(vector3Float.x())).floatValue(), 0.0F, 0.0F) : ((Boolean)condition.invoke(vector3Float.y()) ? new Vector3Float(0.0F, ((Number)modify.invoke(vector3Float.y())).floatValue(), 0.0F) : ((Boolean)condition.invoke(vector3Float.z()) ? new Vector3Float(0.0F, 0.0F, ((Number)modify.invoke(vector3Float.z())).floatValue()) : vector3Float));
   }

   private static final boolean clampToFirstAxis$lambda$0(float it) {
      return it != 0.0F;
   }

   private static final float clampToFirstAxis$lambda$1(float it) {
      return it;
   }
}
