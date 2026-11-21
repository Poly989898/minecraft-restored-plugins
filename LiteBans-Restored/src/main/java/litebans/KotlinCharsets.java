package litebans;

import java.nio.charset.Charset;

public final class KotlinCharsets {
   public static final KotlinCharsets KOTLIN_CHARSETS = new KotlinCharsets();
   public static final Charset CHARSET = Charset.forName("UTF-8");
   public static final Charset CHARSET_2 = Charset.forName("UTF-16");
   public static final Charset CHARSET_3 = Charset.forName("UTF-16BE");
   public static final Charset CHARSET_4 = Charset.forName("UTF-16LE");
   public static final Charset CHARSET_5 = Charset.forName("US-ASCII");
   public static final Charset CHARSET_6 = Charset.forName("ISO-8859-1");

   private KotlinCharsets() {
   }
}
