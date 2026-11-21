package com.ticxo.modelengine.api.generator.assets;

import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public enum JavaDisplay {
   THIRDPERSON_RIGHTHAND,
   THIRDPERSON_LEFTHAND,
   FIRSTPERSON_RIGHTHAND,
   FIRSTPERSON_LEFTHAND,
   GROUND,
   GUI,
   HEAD,
   FIXED;

   @Override
   public String toString() {
      return super.toString().toLowerCase(Locale.ENGLISH);
   }

   @AllArgsConstructor(access = AccessLevel.PACKAGE)
   public enum Transform {
      ROTATION(vals -> {
         vals[0] %= 360.0F;
         vals[1] %= 360.0F;
         vals[2] %= 360.0F;
      }, vals -> TMath.isSimilar(vals[0], 0.0F) && TMath.isSimilar(vals[1], 0.0F) && TMath.isSimilar(vals[2], 0.0F)),
      TRANSLATION(vals -> {
         vals[0] = TMath.clamp(vals[0], -80.0F, 80.0F);
         vals[1] = TMath.clamp(vals[1], -80.0F, 80.0F);
         vals[2] = TMath.clamp(vals[2], -80.0F, 80.0F);
      }, vals -> TMath.isSimilar(vals[0], 0.0F) && TMath.isSimilar(vals[1], 0.0F) && TMath.isSimilar(vals[2], 0.0F)),
      SCALE(vals -> {
         vals[0] = TMath.clamp(vals[0], 0.0F, 4.0F);
         vals[1] = TMath.clamp(vals[1], 0.0F, 4.0F);
         vals[2] = TMath.clamp(vals[2], 0.0F, 4.0F);
      }, vals -> TMath.isSimilar(vals[0], 1.0F) && TMath.isSimilar(vals[1], 1.0F) && TMath.isSimilar(vals[2], 1.0F));

      private final Consumer<float[]> sanitizer;
      private final Predicate<float[]> isDirty;

      public void sanitize(float[] val) {
         this.sanitizer.accept(val);
      }

      public boolean isDefault(float[] val) {
         return this.isDirty.test(val);
      }

      @Override
      public String toString() {
         return super.toString().toLowerCase(Locale.ENGLISH);
      }
   }
}
