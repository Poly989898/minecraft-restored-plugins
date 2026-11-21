package litebans;

import java.text.DecimalFormat;

public class NumberFormats {
   public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
   public static final DecimalFormat DECIMAL_FORMAT_2 = new DecimalFormat("#.##");
   public static final DecimalFormat DECIMAL_FORMAT_3 = new DecimalFormat("#.###");

   public static String computeText(double number) {
      return DECIMAL_FORMAT.format(number);
   }

   public static String computeText2(double number) {
      return DECIMAL_FORMAT_2.format(number);
   }
}
