package litebans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class JSONObject extends HashMap implements Map, JSONAware, JSONStreamAware {
   private static final long VALUE_MINUS_503443796854799292 = -503443796854799292L;

   public static final String computeText(Map map) {
      if (map == null) {
         return "null";
      }

      StringBuffer stringBuffer = new StringBuffer();
      boolean flag = true;
      Iterator iterator2 = map.entrySet().iterator();
      stringBuffer.append('{');

      while (iterator2.hasNext()) {
         if (flag) {
            flag = false;
         } else {
            stringBuffer.append(',');
         }

         Entry entry = (Entry)iterator2.next();
         computeText(String.valueOf(entry.getKey()), entry.getValue(), stringBuffer);
      }

      stringBuffer.append('}');
      return stringBuffer.toString();
   }

   public String obtainText() {
      return computeText(this);
   }

   private static final String computeText(String text, Object obj, StringBuffer stringBuffer) {
      stringBuffer.append('"');
      if (text == null) {
         stringBuffer.append("null");
      } else {
         JSONValue.handleText(text, stringBuffer);
      }

      stringBuffer.append('"').append(':');
      stringBuffer.append(JSONValue.computeText(obj));
      return stringBuffer.toString();
   }

   public String toString() {
      return this.obtainText();
   }
}
