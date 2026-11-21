package litebans;

public final class PlatformHolder {
   public static final PlatformState PLATFORM_STATE = new PlatformState(null);
   private static boolean value;
   public static Platform platform;

   public static final boolean obtainFlag() {
      return value;
   }

   public static final void handleFlag(boolean flag) {
      value = flag;
   }
}
