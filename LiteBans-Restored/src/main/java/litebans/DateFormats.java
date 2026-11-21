package litebans;

public final class DateFormats {
   public static final ConnectionUtil CONNECTION_UTIL = new ConnectionUtil(null);
   private static final ThreadLocal return2 = new DateFormatThreadLocal();

   public static final ThreadLocal obtainThreadLocal() {
      return return2;
   }
}
