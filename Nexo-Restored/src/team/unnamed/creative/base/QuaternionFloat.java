package team.unnamed.creative.base;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record QuaternionFloat(float x, float y, float z, float w) implements Iterable<Float> {
   public static final QuaternionFloat DEFAULT = new QuaternionFloat(0.0F, 0.0F, 0.0F, 1.0F);

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull QuaternionFloat x(float x) {
      return new QuaternionFloat(x, this.y, this.z, this.w);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull QuaternionFloat y(float y) {
      return new QuaternionFloat(this.x, y, this.z, this.w);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull QuaternionFloat z(float z) {
      return new QuaternionFloat(this.x, this.y, z, this.w);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull QuaternionFloat w(float w) {
      return new QuaternionFloat(this.x, this.y, this.z, w);
   }

   @Contract(
      value = "_, _, _, _ -> new",
      pure = true
   )
   public @NotNull QuaternionFloat add(float x, float y, float z, float w) {
      return new QuaternionFloat(this.x + x, this.y + y, this.z + z, this.w + w);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull QuaternionFloat add(@NotNull QuaternionFloat value) {
      return new QuaternionFloat(this.x + value.x(), this.y + value.y(), this.z + value.z(), this.w + value.w());
   }

   @Contract(
      value = "_, _, _, _ -> new",
      pure = true
   )
   public @NotNull QuaternionFloat subtract(float x, float y, float z, float w) {
      return new QuaternionFloat(this.x - x, this.y - y, this.z - z, this.w - w);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull QuaternionFloat subtract(@NotNull QuaternionFloat value) {
      return new QuaternionFloat(this.x - value.x(), this.y - value.y(), this.z - value.z(), this.w - value.w());
   }

   public @NotNull QuaternionFloat multiply(float value) {
      return new QuaternionFloat(this.x * value, this.y * value, this.z * value, this.w * value);
   }

   public @NotNull QuaternionFloat multiply(float x, float y, float z, float w) {
      return new QuaternionFloat(this.x * x, this.y * y, this.z * z, this.w * w);
   }

   public @NotNull QuaternionFloat multiply(@NotNull QuaternionFloat value) {
      return new QuaternionFloat(this.x * value.x(), this.y * value.y(), this.z * value.z(), this.w * value.w());
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull QuaternionFloat divide(float value) {
      return new QuaternionFloat(this.x / value, this.y / value, this.z / value, this.w / value);
   }

   @Contract(
      value = "_, _, _, _ -> new",
      pure = true
   )
   public @NotNull QuaternionFloat divide(float x, float y, float z, float w) {
      return new QuaternionFloat(this.x / x, this.y / y, this.z / z, this.w / z);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull QuaternionFloat divide(@NotNull QuaternionFloat value) {
      return new QuaternionFloat(this.x / value.x(), this.y / value.y(), this.z / value.z(), this.w / value.w());
   }

   @Contract(
      pure = true
   )
   public float dot(@NotNull QuaternionFloat vector) {
      return this.x * vector.x() + this.y * vector.y() + this.z * vector.z() + this.w * vector.w();
   }

   public float @NotNull [] toArray() {
      return new float[]{this.x, this.y, this.z};
   }

   public float @NotNull [] toArray(Function<Float, Float> block) {
      return new float[]{(Float)block.apply(this.x), (Float)block.apply(this.y), (Float)block.apply(this.z), (Float)block.apply(this.w)};
   }

   public @NotNull Iterator<Float> iterator() {
      return Arrays.asList(this.x, this.y, this.z, this.w).iterator();
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", w=" + this.w + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         QuaternionFloat that = (QuaternionFloat)o;
         return this.x == that.x && this.y == that.y && this.z == that.z && this.w == that.w;
      } else {
         return false;
      }
   }
}
