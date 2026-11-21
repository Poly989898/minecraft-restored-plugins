package litebans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONArray extends ArrayList implements List, JSONAware, JSONStreamAware {
   private static final long VALUE_3957988303675231981 = 3957988303675231981L;

   public static final String computeText(List list) {
      if (list == null) {
         return "null";
      }

      boolean flag = true;
      StringBuffer stringBuffer = new StringBuffer();
      Iterator iterator2 = list.iterator();
      stringBuffer.append('[');

      while (iterator2.hasNext()) {
         if (flag) {
            flag = false;
         } else {
            stringBuffer.append(',');
         }

         Object obj = iterator2.next();
         if (obj == null) {
            stringBuffer.append("null");
         } else {
            stringBuffer.append(JSONValue.computeText(obj));
         }
      }

      stringBuffer.append(']');
      return stringBuffer.toString();
   }

   public String obtainText() {
      return computeText(this);
   }

   public String toString() {
      return this.obtainText();
   }
}
