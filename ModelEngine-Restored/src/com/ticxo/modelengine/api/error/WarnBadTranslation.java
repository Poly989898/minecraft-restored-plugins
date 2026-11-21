package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.utils.logger.LogColor;
import lombok.Generated;
import lombok.Setter;

@Setter
public class WarnBadTranslation extends IError.Warn {
   private final String bone;
   private double x;
   private double y;
   private double z;

   @Override
   public String getErrorMessage() {
      return String.format(
         "Warning: The bone %s is translated too far away from the pivot. Maximum translation is 80x80x80. [ %s, %s, %s ]",
         LogColor.BLUE + this.bone + LogColor.YELLOW,
         this.x,
         this.y,
         this.z
      );
   }

   @Generated
   public WarnBadTranslation(String bone) {
      this.bone = bone;
   }
}
