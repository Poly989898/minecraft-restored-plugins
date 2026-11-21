package team.unnamed.creative.base;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Vector3Float(float x, float y, float z) implements Iterable<Float> {
   public static final Vector3Float ZERO = new Vector3Float(0.0F, 0.0F, 0.0F);
   public static final Vector3Float ONE = new Vector3Float(1.0F, 1.0F, 1.0F);

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector3Float x(float x) {
      return new Vector3Float(x, this.y, this.z);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector3Float y(float y) {
      return new Vector3Float(this.x, y, this.z);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector3Float z(float z) {
      return new Vector3Float(this.x, this.y, z);
   }

   @Contract(
      value = "_, _, _ -> new",
      pure = true
   )
   public @NotNull Vector3Float add(float x, float y, float z) {
      return new Vector3Float(this.x + x, this.y + y, this.z + z);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector3Float add(@NotNull Vector3Float value) {
      return new Vector3Float(this.x + value.x(), this.y + value.y(), this.z + value.z());
   }

   @Contract(
      value = "_, _, _ -> new",
      pure = true
   )
   public @NotNull Vector3Float subtract(float x, float y, float z) {
      return new Vector3Float(this.x - x, this.y - y, this.z - z);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector3Float subtract(@NotNull Vector3Float value) {
      return new Vector3Float(this.x - value.x(), this.y - value.y(), this.z - value.z());
   }

   public @NotNull Vector3Float multiply(float value) {
      return new Vector3Float(this.x * value, this.y * value, this.z * value);
   }

   public @NotNull Vector3Float multiply(float x, float y, float z) {
      return new Vector3Float(this.x * x, this.y * y, this.z * z);
   }

   public @NotNull Vector3Float multiply(@NotNull Vector3Float value) {
      return new Vector3Float(this.x * value.x(), this.y * value.y(), this.z * value.z());
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector3Float divide(float value) {
      return new Vector3Float(this.x / value, this.y / value, this.z / value);
   }

   @Contract(
      value = "_, _, _ -> new",
      pure = true
   )
   public @NotNull Vector3Float divide(float x, float y, float z) {
      return new Vector3Float(this.x / x, this.y / y, this.z / z);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector3Float divide(@NotNull Vector3Float value) {
      return new Vector3Float(this.x / value.x(), this.y / value.y(), this.z / value.z());
   }

   @Contract(
      pure = true
   )
   public float dot(@NotNull Vector3Float vector) {
      return this.x * vector.x() + this.y * vector.y() + this.z * vector.z();
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector3Float crossProduct(@NotNull Vector3Float vector) {
      return new Vector3Float(this.y * vector.z - vector.y * this.z, this.z * vector.x - vector.z * this.x, this.x * vector.y - vector.x * this.y);
   }

   @Contract(
      value = "_, _ -> new",
      pure = true
   )
   public @NotNull Vector3Float with(@NotNull Axis3D axis, float value) {
      float x = this.x;
      float y = this.y;
      float z = this.z;
      switch (axis) {
         case X -> x = value;
         case Y -> y = value;
         case Z -> z = value;
         default -> throw new IllegalArgumentException("Invalid axis: " + String.valueOf(axis));
      }

      return new Vector3Float(x, y, z);
   }

   public float get(@NotNull Axis3D axis) {
      float var10000;
      switch (axis) {
         case X -> var10000 = this.x;
         case Y -> var10000 = this.y;
         case Z -> var10000 = this.z;
         default -> throw new IllegalArgumentException("Invalid axis: " + String.valueOf(axis));
      }

      return var10000;
   }

   public float @NotNull [] toArray() {
      return new float[]{this.x, this.y, this.z};
   }

   public float @NotNull [] toArray(Function<Float, Float> block) {
      return new float[]{(Float)block.apply(this.x), (Float)block.apply(this.y), (Float)block.apply(this.z)};
   }

   public @NotNull Iterator<Float> iterator() {
      return Arrays.asList(this.x, this.y, this.z).iterator();
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Vector3Float that = (Vector3Float)o;
         return this.x == that.x && this.y == that.y && this.z == that.z;
      } else {
         return false;
      }
   }
}
