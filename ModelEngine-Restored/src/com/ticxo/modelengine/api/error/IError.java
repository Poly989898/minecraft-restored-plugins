package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.TLogger;

public interface IError {
   ErrorUnknownFormat UNKNOWN_FORMAT = new ErrorUnknownFormat();
   WarnBoxUV BOX_UV = new WarnBoxUV();
   WarnBadEyeHeight BAD_EYE_HEIGHT = new WarnBadEyeHeight();
   WarnNoHitbox NO_HITBOX = new WarnNoHitbox();

   String getErrorMessage();

   IError.Severity getSeverity();

   default void log(ErrorCollector collector) {
      collector.collect(this);
   }

   default void log() {
      if (ConfigProperty.ERROR.getBoolean()) {
         switch (this.getSeverity()) {
            case WARN:
               TLogger.warn("--" + this.getErrorMessage());
               break;
            case ERROR:
               TLogger.error("--" + this.getErrorMessage());
         }
      }
   }

   abstract class Error implements IError {
      @Override
      public IError.Severity getSeverity() {
         return IError.Severity.ERROR;
      }
   }

   enum Severity {
      WARN,
      ERROR;
   }

   abstract class Warn implements IError {
      @Override
      public IError.Severity getSeverity() {
         return IError.Severity.WARN;
      }
   }
}
