package litebans;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

public class JSONValue {
   public static final Object computeObj(Reader reader) {
      try {
         JSONParser urlConnectionFactory = new JSONParser();
         return urlConnectionFactory.computeObj(reader);
      } catch (Exception e) {
         return null;
      }
   }

   public static final Object computeObj(String text) {
      StringReader stringReader = new StringReader(text);
      return computeObj(stringReader);
   }

   public static final String computeText(Object obj) {
      if (obj == null) {
         return "null";
      } else if (obj instanceof String) {
         return "\"" + computeText((String)obj) + "\"";
      } else if (obj instanceof Double) {
         return !((Double)obj).isInfinite() && !((Double)obj).isNaN() ? obj.toString() : "null";
      } else if (obj instanceof Float) {
         return !((Float)obj).isInfinite() && !((Float)obj).isNaN() ? obj.toString() : "null";
      } else if (obj instanceof Number) {
         return obj.toString();
      } else if (obj instanceof Boolean) {
         return obj.toString();
      } else if (obj instanceof JSONAware) {
         return ((JSONAware)obj).obtainText();
      } else if (obj instanceof Map) {
         return JSONObject.computeText((Map)obj);
      } else {
         return obj instanceof List ? JSONArray.computeText((List)obj) : obj.toString();
      }
   }

   public static final String computeText(String text) {
      if (text == null) {
         return null;
      }

      StringBuffer stringBuffer = new StringBuffer();
      handleText(text, stringBuffer);
      return stringBuffer.toString();
   }

   static final void handleText(String text, StringBuffer stringBuffer) {
      for (int i = 0; i < text.length(); i++) {
         char charAt2 = text.charAt(i);
         switch (charAt2) {
            case '\b':
               stringBuffer.append("\\b");
               continue;
            case '\t':
               stringBuffer.append("\\t");
               continue;
            case '\n':
               stringBuffer.append("\\n");
               continue;
            case '\f':
               stringBuffer.append("\\f");
               continue;
            case '\r':
               stringBuffer.append("\\r");
               continue;
            case '"':
               stringBuffer.append("\\\"");
               continue;
            case '/':
               stringBuffer.append("\\/");
               continue;
            case '\\':
               stringBuffer.append("\\\\");
               continue;
         }

         if (charAt2 >= 0 && charAt2 <= 31 || charAt2 >= 127 && charAt2 <= 159 || charAt2 >= 8192 && charAt2 <= 8447) {
            String hexString = Integer.toHexString(charAt2);
            stringBuffer.append("\\u");

            for (int i2 = 0; i2 < 4 - hexString.length(); i2++) {
               stringBuffer.append('0');
            }

            stringBuffer.append(hexString.toUpperCase());
         } else {
            stringBuffer.append(charAt2);
         }
      }
   }
}
