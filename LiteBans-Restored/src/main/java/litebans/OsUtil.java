package litebans;

public class OsUtil {
   private static final ClockSource obtainClockSource() {
      String property = System.getProperty("os.name");
      return "Mac OS X".equals(property) ? new MillisecondClockSource() : new NanosecondClockSource();
   }

   static ClockSource obtainClockSource2() {
      return obtainClockSource();
   }
}
