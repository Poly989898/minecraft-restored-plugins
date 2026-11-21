package team.unnamed.creative.blockstate;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class Variant {
   public static final int DEFAULT_X_ROTATION = 0;
   public static final int DEFAULT_Y_ROTATION = 0;
   public static final boolean DEFAULT_UV_LOCK = false;
   public static final int DEFAULT_WEIGHT = 1;
   private final Key model;
   private final int x;
   private final int y;
   private final boolean uvLock;
   private final int weight;

   private Variant(Key model, int x, int y, boolean uvLock, int weight) {
      this.model = (Key)Objects.requireNonNull(model, "model");
      this.x = x;
      this.y = y;
      this.uvLock = uvLock;
      this.weight = weight;
      this.validate();
   }

   private void validate() {
      if (this.x % 90 == 0 && this.x >= 0 && this.x <= 270) {
         if (this.y % 90 == 0 && this.y >= 0 && this.y <= 270) {
            if (this.weight <= 0) {
               throw new IllegalArgumentException("Zero or negative weight");
            }
         } else {
            throw new IllegalArgumentException("Y rotation must be a positive multiple of 90");
         }
      } else {
         throw new IllegalArgumentException("X rotation must be a positive multiple of 90");
      }
   }

   public Key model() {
      return this.model;
   }

   public int x() {
      return this.x;
   }

   public int y() {
      return this.y;
   }

   public boolean uvLock() {
      return this.uvLock;
   }

   public int weight() {
      return this.weight;
   }

   public @NotNull Builder toBuilder() {
      return builder().model(this.model).x(this.x).y(this.y).uvLock(this.uvLock).weight(this.weight);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{model=" + String.valueOf(this.model) + ", x=" + this.x + ", y=" + this.y + ", uvlock=" + this.uvLock + ", weight=" + this.weight + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Variant that = (Variant)o;
         return this.x == that.x && this.y == that.y && this.uvLock == that.uvLock && this.weight == that.weight && this.model.equals(that.model);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.model, this.x, this.y, this.uvLock, this.weight});
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Key model;
      private int x = 0;
      private int y = 0;
      private boolean uvLock = false;
      private int weight = 1;

      private Builder() {
      }

      public Builder model(Key model) {
         this.model = (Key)Objects.requireNonNull(model, "model");
         return this;
      }

      public Builder x(int x) {
         this.x = x;
         return this;
      }

      public Builder y(int y) {
         this.y = y;
         return this;
      }

      public Builder uvLock(boolean uvLock) {
         this.uvLock = uvLock;
         return this;
      }

      public Builder weight(int weight) {
         this.weight = weight;
         return this;
      }

      public Variant build() {
         return new Variant(this.model, this.x, this.y, this.uvLock, this.weight);
      }
   }
}
