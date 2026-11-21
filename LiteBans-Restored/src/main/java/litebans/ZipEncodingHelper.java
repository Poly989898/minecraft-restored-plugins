package litebans;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

public abstract class ZipEncodingHelper {
   static final String TEXT = "UTF8";
   static final ZipEncoding ZIP_ENCODING = computeZipEncoding("UTF8");

   public static final ZipEncoding computeZipEncoding(String text) {
      Charset defaultCharset2 = Charset.defaultCharset();
      if (text != null) {
         try {
            defaultCharset2 = Charset.forName(text);
         } catch (UnsupportedCharsetException e) {
         }
      }

      boolean flag = checkText(defaultCharset2.name());
      return new NioZipEncoding(defaultCharset2, flag);
   }

   static final boolean checkText(String text) {
      if (text == null) {
         text = Charset.defaultCharset().name();
      }

      if (StandardCharsets.UTF_8.name().equalsIgnoreCase(text)) {
         return true;
      }

      for (String text2 : StandardCharsets.UTF_8.aliases()) {
         if (text2.equalsIgnoreCase(text)) {
            return true;
         }
      }

      return false;
   }
}
