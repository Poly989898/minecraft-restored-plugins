package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.utils.logger.LogColor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorUnknownBoneBehavior extends IError.Error {
   private final String boneName;
   private final String id;

   @Override
   public String getErrorMessage() {
      return String.format("Error: Unknown bone behavior %s on bone %s.", LogColor.BLUE + this.id + LogColor.RED, LogColor.BLUE + this.boneName + LogColor.RED);
   }
}
