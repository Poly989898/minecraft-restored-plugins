package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorWrongBoneBehaviorDataType extends IError.Error {
   private final String bone;
   private final BoneBehaviorType<?> boneBehaviorType;
   private final String key;
   private final Class<?> expects;
   private final Class<?> provided;

   @Override
   public String getErrorMessage() {
      return String.format(
         "Error: The bone behavior %s of %s was given the wrong data type for %s. Expected %s, provided %s. Removing behavior.",
         LogColor.BLUE + this.boneBehaviorType.getId() + LogColor.RED,
         LogColor.BLUE + this.bone + LogColor.RED,
         LogColor.BLUE + this.key + LogColor.RED,
         LogColor.BLUE + this.expects.getSimpleName() + LogColor.RED,
         LogColor.BLUE + this.provided.getSimpleName() + LogColor.RED
      );
   }
}
