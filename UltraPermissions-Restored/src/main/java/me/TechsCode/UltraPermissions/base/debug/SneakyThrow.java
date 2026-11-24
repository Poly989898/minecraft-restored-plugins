package me.TechsCode.UltraPermissions.base.debug;

class SneakyThrow {
   SneakyThrow() {
   }

   public static void sneaky(Throwable exception) {
      throw (RuntimeException)superSneaky(exception);
   }

   private static <T extends Throwable> T superSneaky(Throwable exception) throws T {
      throw (T)exception;
   }
}
