package com.nexomc.nexo.utils;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J$\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u0007J\u0016\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u0007J\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u0007J\u000e\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u0005¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/utils/QuaternionUtils;", "", "<init>", "()V", "fromEuler", "Lorg/joml/Quaternionf;", "x", "", "y", "z", "rotationFromYawPitch", "yaw", "pitch", "applyYawPitchToMatrix", "Lorg/joml/Matrix4f;", "leftRotationToYaw", "leftRotation", "com.nexomc:core"}
)
public final class QuaternionUtils {
   @NotNull
   public static final QuaternionUtils INSTANCE = new QuaternionUtils();

   private QuaternionUtils() {
   }

   @NotNull
   public final Quaternionf fromEuler(float x, float y, float z) {
      float xRad = (float)Math.toRadians((double)x) / 2.0F;
      float yRad = (float)Math.toRadians((double)y) / 2.0F;
      float zRad = (float)Math.toRadians((double)z) / 2.0F;
      float sinX = (float)Math.sin((double)xRad);
      float cosX = (float)Math.cos((double)xRad);
      float sinY = (float)Math.sin((double)yRad);
      float cosY = (float)Math.cos((double)yRad);
      float sinZ = (float)Math.sin((double)zRad);
      float cosZ = (float)Math.cos((double)zRad);
      Quaternionf xQuat = new Quaternionf(sinX, 0.0F, 0.0F, cosX);
      Quaternionf yQuat = new Quaternionf(0.0F, sinY, 0.0F, cosY);
      Quaternionf zQuat = new Quaternionf(0.0F, 0.0F, sinZ, cosZ);
      Quaternionf quaternionf = zQuat.mul((Quaternionfc)yQuat, new Quaternionf()).mul((Quaternionfc)xQuat, new Quaternionf());
      return quaternionf;
   }

   @NotNull
   public final Quaternionf rotationFromYawPitch(float yaw, float pitch) {
      float yawRad = (float)Math.toRadians((double)yaw);
      float pitchRad = (float)Math.toRadians((double)pitch);
      Quaternionf qYaw = (new Quaternionf()).rotateY(yawRad);
      Quaternionf qPitch = (new Quaternionf()).rotateX(pitchRad);
      Quaternionf quaternionf = qYaw.mul((Quaternionfc)qPitch);
      return quaternionf;
   }

   @NotNull
   public final Matrix4f applyYawPitchToMatrix(float yaw, float pitch) {
      Matrix4f matrix = new Matrix4f();
      float yawRad = (float)Math.toRadians((double)yaw);
      float pitchRad = (float)Math.toRadians((double)pitch);
      matrix.rotateY(yawRad);
      matrix.rotateX(pitchRad);
      return matrix;
   }

   public final float leftRotationToYaw(@NotNull Quaternionf leftRotation) {
      Quaternionf n = leftRotation.normalize();
      return (float)Math.toDegrees((double)((float)Math.atan2((double)(2.0F * (n.y * n.w + n.x * n.z)), (double)(1.0F - 2.0F * (n.y * n.y + n.x * n.x)))));
   }
}
