package com.ticxo.modelengine.api.error;

public class WarnNoHitbox extends IError.Warn {
   @Override
   public String getErrorMessage() {
      return "Warning: Missing hitbox.";
   }
}
