package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import lombok.Getter;

@Getter
public class ErrorCollector {
   private final String fileName;
   private final List<IError> errorList = new ArrayList<>();

   public void collect(IError error) {
      this.errorList.add(error);
   }

   public void logAll() {
      if (!ConfigProperty.LOG_ONLY_ERROR.getBoolean() || !this.errorList.isEmpty()) {
         if (!this.errorList.isEmpty()) {
            TLogger.log();
         }

         TLogger.log(LogColor.CYAN + "Importing " + this.fileName + ".");
         this.errorList.forEach(IError::log);
      }
   }

   public boolean hasError() {
      return !this.errorList.isEmpty();
   }

   @Generated
   public ErrorCollector(String fileName) {
      this.fileName = fileName;
   }
}
