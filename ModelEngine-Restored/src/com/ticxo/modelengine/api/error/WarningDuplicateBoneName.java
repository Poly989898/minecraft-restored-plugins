package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.utils.logger.LogColor;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WarningDuplicateBoneName extends IError.Warn {
   private final String bone;
   private final UUID uuid;

   @Override
   public String getErrorMessage() {
      return String.format(
         "Warning: Model contains duplicate bone names %s. Naming bone with UUID %s",
         LogColor.BLUE + this.bone + LogColor.YELLOW,
         LogColor.BLUE + this.uuid.toString() + LogColor.YELLOW
      );
   }
}
