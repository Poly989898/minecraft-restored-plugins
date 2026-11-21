package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.utils.logger.LogColor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorNoFaceCube extends IError.Error {
   private final String bone;
   private final String cube;

   @Override
   public String getErrorMessage() {
      return String.format(
         "Error: The cube %s in bone %s has no faces. This might be caused by all faces having UV size of 0. Excluding cube from bone.",
         LogColor.BLUE + this.cube + LogColor.RED,
         LogColor.BLUE + this.bone + LogColor.RED
      );
   }
}
