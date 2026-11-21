package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.utils.logger.LogColor;
import lombok.Generated;
import lombok.Setter;

@Setter
public class WarnBoneTooLarge extends IError.Warn {
   private final String bone;
   private double x;
   private double y;
   private double z;

   @Override
   public String getErrorMessage() {
      return String.format(
         "Warning: The bone %s exceeds the maximum size of 120x120x120. [ %s, %s, %s ]", LogColor.BLUE + this.bone + LogColor.YELLOW, this.x, this.y, this.z
      );
   }

   @Generated
   public WarnBoneTooLarge(String bone) {
      this.bone = bone;
   }
}
