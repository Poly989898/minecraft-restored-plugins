package com.ticxo.modelengine.api.error;

public class ErrorUnknownFormat extends IError.Error {
   @Override
   public String getErrorMessage() {
      return "Error: Unknown format.";
   }
}
