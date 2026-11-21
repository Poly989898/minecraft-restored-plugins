package team.unnamed.creative.base;

import java.util.Arrays;
import java.util.Iterator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Vector2Float(float x, float y) implements Iterable<Float> {
   public static final Vector2Float ZERO = new Vector2Float(0.0F, 0.0F);
   public static final Vector2Float ONE = new Vector2Float(1.0F, 1.0F);

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector2Float add(@NotNull Vector2Float value) {
      return new Vector2Float(this.x + value.x, this.y + value.y);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector2Float subtract(@NotNull Vector2Float value) {
      return new Vector2Float(this.x - value.x, this.y - value.y);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector2Float multiply(@NotNull Vector2Float value) {
      return new Vector2Float(this.x * value.x, this.y * value.y);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector2Float multiply(float value) {
      return new Vector2Float(this.x * value, this.y * value);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector2Float divide(@NotNull Vector2Float value) {
      return new Vector2Float(this.x / value.x, this.y / value.y);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   public @NotNull Vector2Float divide(float value) {
      return new Vector2Float(this.x / value, this.y / value);
   }

   @Contract(
      value = "-> new",
      pure = true
   )
   public float @NotNull [] toArray() {
      return new float[]{this.x, this.y};
   }

   public @NotNull Iterator<Float> iterator() {
      return Arrays.asList(this.x, this.y).iterator();
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{x=" + this.x + ", y=" + this.y + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Vector2Float that = (Vector2Float)o;
         return this.x == that.x && this.y == that.y;
      } else {
         return false;
      }
   }
}
