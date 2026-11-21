package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.utils.logger.LogColor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorIncompatibleBone extends IError.Error {
   private final boolean rendering;
   private final String boneName;
   private final String id;

   @Override
   public String getErrorMessage() {
      return String.format(
         "Error: The %s bone behavior type %s is not compatible with %s %s.",
         this.rendering ? "ghost" : "renderer",
         LogColor.BLUE + this.id + LogColor.RED,
         this.rendering ? "renderer bone" : "ghost bone",
         LogColor.BLUE + this.boneName + LogColor.RED
      );
   }
}
