package litebans;

import org.jetbrains.annotations.NotNull;

class LiteUtil2 extends LiteStubX {
   public static final Double computeDouble(@NotNull String text) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");

      Double double2;
      try {
         Double double3;
         if (checkText(text)) {
            String text2 = text;
            double3 = Double.parseDouble(text2);
         } else {
            double3 = null;
         }

         double2 = double3;
      } catch (NumberFormatException e) {
         double2 = null;
      }

      return double2;
   }

   private static final boolean checkText(String text) {
      byte b = 0;
      int value = text.length() - 1;

      int value2;
      for (value2 = b; value2 <= value; value2++) {
         char charAt2 = text.charAt(value2);
         if (charAt2 > ' ') {
            break;
         }
      }

      int value3 = value2;
      if (value3 > value) {
         return false;
      }


      int value4;
      for (value4 = value; value4 > value3; value4--) {
         char charAt3 = text.charAt(value4);
         if (charAt3 > ' ') {
            break;
         }
      }

      int value5 = value4;
      if (text.charAt(value3) == '+' || text.charAt(value3) == '-') {
         value3++;
      }

      if (value3 > value5) {
         return false;
      }

      boolean flag = false;
      if (text.charAt(value3) == '0') {
         if (++value3 > value5) {
            return true;
         }

         if ((text.charAt(value3) | ' ') == 120) {
            value3++;
            int value6 = value3;

            int value7;
            for (value7 = value6; value7 <= value5; value7++) {
               char charAt4 = text.charAt(value7);
               if ((charAt4 - '0' & 65535) >= 10 && ((charAt4 | ' ') - 97 & 65535) >= 6) {
                  break;
               }
            }

            int value8 = value7;
            boolean flag2 = value6 != value8;
            byte b2;
            if (value8 > value5) {
               b2 = -1;
            } else {
               boolean flag3 = false;
               if (text.charAt(value8) == '.') {
                  int value9 = ++value8;

                  int value10;
                  for (value10 = value8; value10 <= value5; value10++) {
                     char charAt5 = text.charAt(value10);
                     if ((charAt5 - '0' & 65535) >= 10 && ((charAt5 | ' ') - 97 & 65535) >= 6) {
                        break;
                     }
                  }

                  value8 = value10;
                  flag3 = value9 != value8;
               }

               b2 = (byte)(!flag2 && !flag3 ? -1 : value8);
            }

            value3 = b2;
            if (value3 == -1 || value3 > value5) {
               return false;
            }

            flag = true;
         } else {
            value3--;
         }
      }

      if (!flag) {
         int value11 = value3;

         int value12;
         for (value12 = value11; value12 <= value5; value12++) {
            char charAt6 = text.charAt(value12);
            if ((charAt6 - '0' & 65535) >= 10) {
               break;
            }
         }

         int value13 = value12;
         boolean flag4 = value11 != value13;
         int value14;
         if (value13 > value5) {
            value14 = value13;
         } else {
            boolean flag5 = false;
            if (text.charAt(value13) == '.') {
               int value15 = ++value13;

               int value16;
               for (value16 = value13; value16 <= value5; value16++) {
                  char charAt7 = text.charAt(value16);
                  if ((charAt7 - '0' & 65535) >= 10) {
                     break;
                  }
               }

               value13 = value16;
               flag5 = value15 != value13;
            }

            if (!flag4 && !flag5) {
               String text2 = value5 == value13 + 3 - 1 ? "NaN" : (value5 == value13 + 8 - 1 ? "Infinity" : null);
               value14 = text2 == null ? -1 : (StringsKt.indexOf(text, text2, value13, false) == value13 ? value5 + 1 : -1);
            } else {
               value14 = value13;
            }
         }

         value3 = value14;
         if (value3 == -1) {
            return false;
         }

         if (value3 > value5) {
            return true;
         }
      }

      int value17 = text.charAt(value3++) | ' ';
      if (value17 != (flag ? 112 : 101)) {
         return !flag && (value17 == 102 || value17 == 100) && value3 > value5;
      }

      if (value3 > value5) {
         return false;
      }

      if (text.charAt(value3) == '+' || text.charAt(value3) == '-') {
         if (++value3 > value5) {
            return false;
         }
      }


      int value18;
      for (value18 = value3; value18 <= value5; value18++) {
         char charAt8 = text.charAt(value18);
         if ((charAt8 - '0' & 65535) >= 10) {
            break;
         }
      }

      int value19 = value18;
      if (value19 > value5) {
         return true;
      }

      if (value19 != value5) {
         return false;
      }

      int value20 = text.charAt(value19) | ' ';
      return value20 == 102 || value20 == 100;
   }
}
