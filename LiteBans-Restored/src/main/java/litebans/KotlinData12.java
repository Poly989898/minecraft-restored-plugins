package litebans;

import java.util.HashMap;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public final class KotlinData12 {
   public static final KotlinData12Companion KOTLIN_DATA12_COMPANION = new KotlinData12Companion(null);
   private final int intValue;
   private final HashMap hashMap = new HashMap();
   private final HashMap hashMap2 = new HashMap();
   public static final int VALUE_3 = 3;
   public static final int VALUE_6 = 6;
   public static final long VALUE_MINUS_1 = -1L;

   public KotlinData12(long value) {
      KotlinRandom kotlinRandom = new KotlinRandom(value);
      this.intValue = 8500 + kotlinRandom.nextIntLenient(4000);
      this.handleKotlinRandom(kotlinRandom);
   }

   public final int getIntValue() {
      return this.intValue;
   }

   public final HashMap getHashMap() {
      return this.hashMap;
   }

   public final String computeText(long value) {
      try {
         long value2 = value + this.intValue;
         String text = String.valueOf(value2);
         String text2 = this.computeText(text);
         StringBuilder stringBuilder = new StringBuilder();
         StringBuilder stringBuilder2 = new StringBuilder();
         boolean flag = true;
         int value3 = 0;

         for (int i = text2.length(); value3 < i; value3++) {
            char charAt2 = text2.charAt(value3);
            stringBuilder2.append(charAt2);
            if (stringBuilder2.length() >= 3) {
               if (!flag) {
                  if (StringsKt.startsWithWithDefaults(stringBuilder2, "000", false, 2, null)) {
                     stringBuilder.append("m");
                  } else if (StringsKt.startsWithWithDefaults(stringBuilder2, "00", false, 2, null)) {
                     stringBuilder.append("v");
                  } else if (StringsKt.startsWithWithDefaults(stringBuilder2, "0", false, 2, null)) {
                     stringBuilder.append("z");
                  }
               }

               stringBuilder.append(this.computeText(stringBuilder2));
               stringBuilder2.setLength(0);
               flag = false;
            }
         }

         if (stringBuilder2.length() > 0) {
            throw new KotlinRuntimeExceptionSub2(null, 1, null);
         } else {
            return stringBuilder.toString().toUpperCase(Locale.ROOT);
         }
      } catch (KotlinRuntimeExceptionSub2 e) {
         return "error";
      }
   }

   private final String computeText(String text) {
      String text2;
      switch (text.length() % 3) {
         case 1:
            text2 = "00" + text;
            break;
         case 2:
            text2 = '0' + text;
            break;
         default:
            text2 = text;
      }

      return text2;
   }

   private final String computeText(int value, int value2) {
      String text;
      switch (value) {
         case 0:
            text = String.valueOf(value2);
            break;
         case 1:
            text = "" + '0' + value2;
            break;
         case 2:
            text = "00" + value2;
            break;
         case 3:
            text = "000";
            break;
         default:
            throw new KotlinRuntimeExceptionSub2(null, 1, null);
      }

      return text;
   }

   public final long computeLongValue(@NotNull String text) {
      try {
         String lowerCase = text.toLowerCase(Locale.ROOT);
         StringBuilder stringBuilder = new StringBuilder();
         StringBuilder stringBuilder2 = new StringBuilder();
         byte b = 0;
         int value = 0;

         for (int i = lowerCase.length(); value < i; value++) {
            char charAt2 = lowerCase.charAt(value);
            switch (charAt2) {
               case 'm':
                  b = 3;
                  break;
               case 'v':
                  b = 2;
                  break;
               case 'z':
                  b = 1;
                  break;
               default:
                  stringBuilder2.append(charAt2);
            }

            if (stringBuilder2.length() >= 3) {
               stringBuilder.append(this.computeText(stringBuilder2, b));
               stringBuilder2.setLength(0);
               b = 0;
            }
         }

         if (stringBuilder2.length() > 0) {
            throw new KotlinRuntimeExceptionSub2(null, 1, null);
         } else {
            return Long.parseLong(stringBuilder.toString()) - this.intValue;
         }
      } catch (KotlinRuntimeExceptionSub2 e) {
         return -1L;
      }
   }

   private final void handleKotlinRandom(KotlinRandom kotlinRandom) {
      for (int i = 0; i < 1000; i++) {
         String text = this.computeText(kotlinRandom);

         while (text.length() != 3 || this.hashMap2.containsKey(text)) {
            text = this.computeText(kotlinRandom);
         }

         Integer boxed = i;
         this.hashMap.put(boxed, text);
         Integer boxed2 = i;
         this.hashMap2.put(text, boxed2);
      }
   }

   private final String computeText(KotlinRandom kotlinRandom) {
      return Long.toString(kotlinRandom.nextLong() & 4095L, KotlinStub13.computeIntValue(16));
   }

   private final String computeText(StringBuilder builder) {
      String string = (String)this.hashMap.get(Integer.parseInt(builder.toString()));
      if (string == null) {
         throw new KotlinRuntimeExceptionSub2("No shuffle input for " + builder);
      } else {
         return string;
      }
   }

   private final String computeText(StringBuilder builder, int value) {
      Integer integer = (Integer)this.hashMap2.get(builder.toString());
      if (integer != null) {
         int value2 = integer;
         return this.computeText(value, value2);
      } else {
         throw new KotlinRuntimeExceptionSub2("No shuffle output for " + builder);
      }
   }
}
