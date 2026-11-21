package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WarnBadTexture extends IError.Warn {
   private final ResourceLocation name;
   private final ResourceLocation correct;

   @Override
   public String getErrorMessage() {
      return String.format(
         "Warn: Texture name %s contains non [a-z0-9/._-] characters. Using alternative texture name %s.",
         LogColor.BLUE + this.name.toString() + LogColor.YELLOW,
         LogColor.BLUE + this.correct.toString() + LogColor.YELLOW
      );
   }
}
