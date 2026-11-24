package me.TechsCode.UltraPermissions.base.debug;

import lombok.Generated;

public class TechException extends RuntimeException {
   private static final long serialVersionUID = 1L;
   private static boolean errorSavedAutomatically = true;

   public TechException(Throwable exception) {
      super(exception);
      if (errorSavedAutomatically) {
         Debugger.saveError(exception);
      }
   }

   public TechException(String text) {
      super(text);
      if (errorSavedAutomatically) {
         Debugger.saveError(this, text);
      }
   }

   public TechException(String text, Throwable exception) {
      this(exception, text);
   }

   public TechException(Throwable exception, String text) {
      super(text, exception);
      if (errorSavedAutomatically) {
         Debugger.saveError(exception, text);
      }
   }

   public TechException() {
      if (errorSavedAutomatically) {
         Debugger.saveError(this);
      }
   }

   @Override
   public String getMessage() {
      return "Report: " + super.getMessage();
   }

   public static void sneaky(Throwable exception) {
      try {
         SneakyThrow.sneaky(exception);
      } catch (NoClassDefFoundError | NoSuchFieldError | NoSuchMethodError reflectionError) {
         throw new TechException(exception);
      }
   }

   @Generated
   public static boolean isErrorSavedAutomatically() {
      return errorSavedAutomatically;
   }

   @Generated
   public static void setErrorSavedAutomatically(boolean flag) {
      errorSavedAutomatically = flag;
   }
}
