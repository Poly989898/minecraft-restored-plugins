package litebans;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public final class DateFormatThreadLocal extends ThreadLocal {
   DateFormatThreadLocal() {
   }

   protected SimpleDateFormat obtainSimpleDateFormat() {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      return simpleDateFormat;
   }

   @Override
   public Object initialValue() {
      return this.obtainSimpleDateFormat();
   }
}
