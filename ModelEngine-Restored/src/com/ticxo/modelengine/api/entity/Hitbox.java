package com.ticxo.modelengine.api.entity;

import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.math.Box;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString
@AllArgsConstructor
public class Hitbox implements Box {
   private final double width;
   private final double height;
   private final double depth;
   private final double eyeHeight;

   public Hitbox clone() {
      return new Hitbox(this.width, this.height, this.depth, this.eyeHeight);
   }

   public double getMaxWidth() {
      return Math.max(this.width, this.depth);
   }

   @NotNull
   @Override
   public BoundingBox createBoundingBox(Vector vector) {
      return new BoundingBox(
         vector.getX() - this.width * 0.5,
         vector.getY(),
         vector.getZ() - this.depth * 0.5,
         vector.getX() + this.width * 0.5,
         vector.getY() + this.height,
         vector.getZ() + this.depth * 0.5
      );
   }

   public String toSimpleString() {
      return MiscUtils.FORMATTER.format(this.width) + " x " + MiscUtils.FORMATTER.format(this.height) + " x " + MiscUtils.FORMATTER.format(this.width);
   }

   public String toEyeHeightString() {
      return MiscUtils.FORMATTER.format(this.eyeHeight);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Hitbox other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Double.compare(this.getWidth(), other.getWidth()) != 0) {
         return false;
      } else if (Double.compare(this.getHeight(), other.getHeight()) != 0) {
         return false;
      } else {
         return Double.compare(this.getDepth(), other.getDepth()) != 0 ? false : Double.compare(this.getEyeHeight(), other.getEyeHeight()) == 0;
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof Hitbox;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long $width = Double.doubleToLongBits(this.getWidth());
      result = result * 59 + (int)($width >>> 32 ^ $width);
      long $height = Double.doubleToLongBits(this.getHeight());
      result = result * 59 + (int)($height >>> 32 ^ $height);
      long $depth = Double.doubleToLongBits(this.getDepth());
      result = result * 59 + (int)($depth >>> 32 ^ $depth);
      long $eyeHeight = Double.doubleToLongBits(this.getEyeHeight());
      return result * 59 + (int)($eyeHeight >>> 32 ^ $eyeHeight);
   }
}
