package litebans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

class KotlinUtil8 extends KotlinUtil7 {
   public static final String trim(@NotNull String text, @NotNull char[] chars) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      Intrinsics.checkParameterIsNotNull(chars, "chars");
      String text2 = text;
      CharSequence message = text2;
      int value = 0;
      int value2 = message.length();

      CharSequence message2;
      while (true) {
         if (value >= value2) {
            message2 = "";
            break;
         }

         char charAt2 = message.charAt(value);
         if (!ArraysKt.contains(chars, charAt2)) {
            message2 = message.subSequence(value, message.length());
            break;
         }

         value++;
      }

      return message2.toString();
   }

   public static final boolean isBlank(@NotNull CharSequence message) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      CharSequence message2 = message;
      int value = 0;

      boolean flag;
      while (true) {
         if (value >= message2.length()) {
            flag = true;
            break;
         }

         char charAt2 = message2.charAt(value);
         if (!KotlinStub13.checkCharValue(charAt2)) {
            flag = false;
            break;
         }

         value++;
      }

      return flag;
   }

   public static final int getLastIndex(@NotNull CharSequence message) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      return message.length() - 1;
   }

   public static final String substring(@NotNull CharSequence message, @NotNull KotlinIntProgressionSub kotlinIntProgressionSub) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      Intrinsics.checkParameterIsNotNull(kotlinIntProgressionSub, "range");
      return message.subSequence(kotlinIntProgressionSub.obtainInteger(), kotlinIntProgressionSub.obtainInteger2() + 1).toString();
   }

   public static final String substringBefore(@NotNull String text, char c, @NotNull String text2) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      Intrinsics.checkParameterIsNotNull(text2, "missingDelimiterValue");
      int indexOf = StringsKt.indexOfWithDefaults((CharSequence)text, c, (int)0, false, 6, null);
      String text3;
      if (indexOf == -1) {
         text3 = text2;
      } else {
         String text4 = text;
         byte b = 0;
         text3 = text4.substring(b, indexOf);
      }

      return text3;
   }

   public static String substringBeforeWithDefaults(String text, char c, String text2, int value, Object obj) {
      if ((value & 2) != 0) {
         text2 = text;
      }

      return StringsKt.substringBefore(text, c, text2);
   }

   public static final String substringAfter(@NotNull String text, char c, @NotNull String text2) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      Intrinsics.checkParameterIsNotNull(text2, "missingDelimiterValue");
      int indexOf = StringsKt.indexOfWithDefaults((CharSequence)text, c, (int)0, false, 6, null);
      String text3;
      if (indexOf == -1) {
         text3 = text2;
      } else {
         String text4 = text;
         int value = indexOf + 1;
         int value2 = text.length();
         text3 = text4.substring(value, value2);
      }

      return text3;
   }

   public static String substringAfterWithDefaults(String text, char c, String text2, int value, Object obj) {
      if ((value & 2) != 0) {
         text2 = text;
      }

      return StringsKt.substringAfter(text, c, text2);
   }

   public static final String substringAfter(@NotNull String text, @NotNull String text2, @NotNull String text3) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      Intrinsics.checkParameterIsNotNull(text2, "delimiter");
      Intrinsics.checkParameterIsNotNull(text3, "missingDelimiterValue");
      int indexOf = StringsKt.indexOfWithDefaults(text, text2, 0, false, 6, null);
      String text4;
      if (indexOf == -1) {
         text4 = text3;
      } else {
         String text5 = text;
         int value = indexOf + text2.length();
         int value2 = text.length();
         text4 = text5.substring(value, value2);
      }

      return text4;
   }

   public static String substringAfterWithDefaults(String text, String text2, String text3, int value, Object obj) {
      if ((value & 2) != 0) {
         text3 = text;
      }

      return StringsKt.substringAfter(text, text2, text3);
   }

   public static final String substringBeforeLast(@NotNull String text, char c, @NotNull String text2) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      Intrinsics.checkParameterIsNotNull(text2, "missingDelimiterValue");
      int lastIndexOf = StringsKt.lastIndexOfWithDefaults(text, c, 0, false, 6, null);
      String text3;
      if (lastIndexOf == -1) {
         text3 = text2;
      } else {
         String text4 = text;
         byte b = 0;
         text3 = text4.substring(b, lastIndexOf);
      }

      return text3;
   }

   public static String substringBeforeLastWithDefaults(String text, char c, String text2, int value, Object obj) {
      if ((value & 2) != 0) {
         text2 = text;
      }

      return StringsKt.substringBeforeLast(text, c, text2);
   }

   public static final String substringAfterLast(@NotNull String text, char c, @NotNull String text2) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      Intrinsics.checkParameterIsNotNull(text2, "missingDelimiterValue");
      int lastIndexOf = StringsKt.lastIndexOfWithDefaults(text, c, 0, false, 6, null);
      String text3;
      if (lastIndexOf == -1) {
         text3 = text2;
      } else {
         String text4 = text;
         int value = lastIndexOf + 1;
         int value2 = text.length();
         text3 = text4.substring(value, value2);
      }

      return text3;
   }

   public static String substringAfterLastWithDefaults(String text, char c, String text2, int value, Object obj) {
      if ((value & 2) != 0) {
         text2 = text;
      }

      return StringsKt.substringAfterLast(text, c, text2);
   }

   public static final boolean regionMatchesImpl(@NotNull CharSequence message, int value, @NotNull CharSequence message2, int value2, int value3, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      Intrinsics.checkParameterIsNotNull(message2, "other");
      if (value2 >= 0 && value >= 0 && value <= message.length() - value3 && value2 <= message2.length() - value3) {
         for (int i = 0; i < value3; i++) {
            if (!KotlinStub13.checkCharValue(message.charAt(value + i), message2.charAt(value2 + i), flag)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static final boolean startsWith(@NotNull CharSequence message, char c, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      return message.length() > 0 && KotlinStub13.checkCharValue(message.charAt(0), c, flag);
   }

   public static boolean startsWithWithDefaults(CharSequence message, char c, boolean flag, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = false;
      }

      return StringsKt.startsWith(message, c, flag);
   }

   public static final boolean endsWith(@NotNull CharSequence message, char c, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      return message.length() > 0 && KotlinStub13.checkCharValue(message.charAt(StringsKt.getLastIndex(message)), c, flag);
   }

   public static boolean endsWithWithDefaults(CharSequence message, char c, boolean flag, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = false;
      }

      return StringsKt.endsWith(message, c, flag);
   }

   public static final boolean startsWith(@NotNull CharSequence message, @NotNull CharSequence message2, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      Intrinsics.checkParameterIsNotNull(message2, "prefix");
      return !flag && message instanceof String && message2 instanceof String
         ? StringsKt.checkText((String)message, (String)message2, false, 2, null)
         : StringsKt.regionMatchesImpl((CharSequence)message, 0, (CharSequence)message2, 0, message2.length(), flag);
   }

   public static boolean startsWithWithDefaults(CharSequence message, CharSequence message2, boolean flag, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = false;
      }

      return StringsKt.startsWith((CharSequence)message, (CharSequence)message2, flag);
   }

   public static final boolean endsWith(@NotNull CharSequence message, @NotNull CharSequence message2, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      Intrinsics.checkParameterIsNotNull(message2, "suffix");
      return !flag && message instanceof String && message2 instanceof String
         ? StringsKt.checkText2((String)message, (String)message2, false, 2, null)
         : StringsKt.regionMatchesImpl((CharSequence)message, message.length() - message2.length(), (CharSequence)message2, 0, message2.length(), flag);
   }

   public static boolean endsWithWithDefaults(CharSequence message, CharSequence message2, boolean flag, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = false;
      }

      return StringsKt.endsWith((CharSequence)message, (CharSequence)message2, flag);
   }

   public static final int indexOfAny(@NotNull CharSequence message, @NotNull char[] chars, int value, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      Intrinsics.checkParameterIsNotNull(chars, "chars");
      if (!flag && chars.length == 1 && message instanceof String) {
         char single2 = ArraysKt.single(chars);
         return ((String)message).indexOf(single2, value);
      }

      int value2 = KotlinRangesKtSub.coerceAtLeast(value, 0);
      int lastIndex = StringsKt.getLastIndex(message);
      if (value2 <= lastIndex) {
         while (true) {
            char charAt2 = message.charAt(value2);
            char[] chars2 = chars;
            int value3 = 0;
            int length2 = chars2.length;

            boolean flag2;
            while (true) {
               if (value3 >= length2) {
                  flag2 = false;
                  break;
               }

               char c2 = chars2[value3];
               if (KotlinStub13.checkCharValue(c2, charAt2, flag)) {
                  flag2 = true;
                  break;
               }

               value3++;
            }

            if (flag2) {
               return value2;
            }

            if (value2 == lastIndex) {
               break;
            }

            value2++;
         }
      }

      return -1;
   }

   public static final int lastIndexOfAny(@NotNull CharSequence message, @NotNull char[] chars, int value, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      Intrinsics.checkParameterIsNotNull(chars, "chars");
      if (!flag && chars.length == 1 && message instanceof String) {
         char single2 = ArraysKt.single(chars);
         return ((String)message).lastIndexOf(single2, value);
      }

      for (int i = KotlinRangesKtSub.coerceAtMost(value, StringsKt.getLastIndex(message)); -1 < i; i--) {
         char charAt2 = message.charAt(i);
         char[] chars2 = chars;
         int value2 = 0;
         int length2 = chars2.length;

         boolean flag2;
         while (true) {
            if (value2 >= length2) {
               flag2 = false;
               break;
            }

            char c = chars2[value2];
            if (KotlinStub13.checkCharValue(c, charAt2, flag)) {
               flag2 = true;
               break;
            }

            value2++;
         }

         if (flag2) {
            return i;
         }
      }

      return -1;
   }

   private static final int indexOfImpl(CharSequence message, CharSequence message2, int value, int value2, boolean flag, boolean flag2) {
      IntProgression intProgression = !flag2 ? new KotlinIntProgressionSub(KotlinRangesKtSub.coerceAtLeast(value, 0), KotlinRangesKtSub.coerceAtMost(value2, message.length())) : KotlinRangesKtSub.downTo(KotlinRangesKtSub.coerceAtMost(value, StringsKt.getLastIndex(message)), KotlinRangesKtSub.coerceAtLeast(value2, 0));
      if (message instanceof String && message2 instanceof String) {
         int value3 = intProgression.getFirst();
         int value4 = intProgression.getLast();
         int value5 = intProgression.getStep();
         if (value5 > 0 && value3 <= value4 || value5 < 0 && value4 <= value3) {
            while (true) {
               if (StringsKt.checkText((String)message2, 0, (String)message, value3, ((String)message2).length(), flag)) {
                  return value3;
               }

               if (value3 == value4) {
                  break;
               }

               value3 += value5;
            }
         }
      } else {
         int value6 = intProgression.getFirst();
         int value7 = intProgression.getLast();
         int value8 = intProgression.getStep();
         if (value8 > 0 && value6 <= value7 || value8 < 0 && value7 <= value6) {
            while (true) {
               if (StringsKt.regionMatchesImpl((CharSequence)message2, 0, (CharSequence)message, value6, message2.length(), flag)) {
                  return value6;
               }

               if (value6 == value7) {
                  break;
               }

               value6 += value8;
            }
         }
      }

      return -1;
   }

   static int indexOfImplWithDefaults(CharSequence message, CharSequence message2, int value, int value2, boolean flag, boolean flag2, int value3, Object obj) {
      if ((value3 & 16) != 0) {
         flag2 = false;
      }

      return indexOfImpl(message, message2, value, value2, flag, flag2);
   }

   private static final Pair findAnyOf(CharSequence message, Collection collection, int value, boolean flag, boolean flag2) {
      if (!flag && collection.size() == 1) {
         String single2 = (String)CollectionsKt.single(collection);
         int value2 = !flag2 ? StringsKt.indexOfWithDefaults(message, single2, value, false, 4, null) : StringsKt.lastIndexOfWithDefaults(message, single2, value, false, 4, null);
         return value2 < 0 ? null : TuplesKt.computePair(value2, single2);
      }

      IntProgression intProgression = !flag2 ? new KotlinIntProgressionSub(KotlinRangesKtSub.coerceAtLeast(value, 0), message.length()) : KotlinRangesKtSub.downTo(KotlinRangesKtSub.coerceAtMost(value, StringsKt.getLastIndex(message)), 0);
      if (message instanceof String) {
         int value3 = intProgression.getFirst();
         int value4 = intProgression.getLast();
         int value5 = intProgression.getStep();
         if (value5 > 0 && value3 <= value4 || value5 < 0 && value4 <= value3) {
            while (true) {
               Iterable iterable = collection;
               Iterator iterator2 = iterable.iterator();

               Object obj;
               while (true) {
                  if (!iterator2.hasNext()) {
                     obj = null;
                     break;
                  }

                  Object obj2 = iterator2.next();
                  String string = (String)obj2;
                  if (StringsKt.checkText(string, 0, (String)message, value3, string.length(), flag)) {
                     obj = obj2;
                     break;
                  }
               }

               String string2 = (String)obj;
               if (string2 != null) {
                  return TuplesKt.computePair(value3, string2);
               }

               if (value3 == value4) {
                  break;
               }

               value3 += value5;
            }
         }
      } else {
         int value6 = intProgression.getFirst();
         int value7 = intProgression.getLast();
         int value8 = intProgression.getStep();
         if (value8 > 0 && value6 <= value7 || value8 < 0 && value7 <= value6) {
            while (true) {
               Iterable iterable2 = collection;
               Iterator iterator3 = iterable2.iterator();

               Object obj3;
               while (true) {
                  if (!iterator3.hasNext()) {
                     obj3 = null;
                     break;
                  }

                  Object obj4 = iterator3.next();
                  String string3 = (String)obj4;
                  if (StringsKt.regionMatchesImpl((CharSequence)string3, 0, (CharSequence)message, value6, string3.length(), flag)) {
                     obj3 = obj4;
                     break;
                  }
               }

               String string4 = (String)obj3;
               if (string4 != null) {
                  return TuplesKt.computePair(value6, string4);
               }

               if (value6 == value7) {
                  break;
               }

               value6 += value8;
            }
         }
      }

      return null;
   }

   public static final int indexOf(@NotNull CharSequence message, char c, int value, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      int value2;
      if (!flag && message instanceof String) {
         value2 = ((String)message).indexOf(c, value);
      } else {
         char[] chars = new char[]{c};
         value2 = StringsKt.indexOfAny(message, chars, value, flag);
      }

      return value2;
   }

   public static int indexOfWithDefaults(CharSequence message, char c, int value, boolean flag, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         value = 0;
      }

      if ((value2 & 4) != 0) {
         flag = false;
      }

      return StringsKt.indexOf((CharSequence)message, c, (int)value, flag);
   }

   public static final int indexOf(@NotNull CharSequence message, @NotNull String text, int value, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      Intrinsics.checkParameterIsNotNull(text, "string");
      return !flag && message instanceof String ? ((String)message).indexOf(text, value) : indexOfImplWithDefaults(message, text, value, message.length(), flag, false, 16, null);
   }

   public static int indexOfWithDefaults(CharSequence message, String text, int value, boolean flag, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         value = 0;
      }

      if ((value2 & 4) != 0) {
         flag = false;
      }

      return StringsKt.indexOf(message, text, value, flag);
   }

   public static final int lastIndexOf(@NotNull CharSequence message, char c, int value, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      int value2;
      if (!flag && message instanceof String) {
         value2 = ((String)message).lastIndexOf(c, value);
      } else {
         char[] chars = new char[]{c};
         value2 = StringsKt.lastIndexOfAny(message, chars, value, flag);
      }

      return value2;
   }

   public static int lastIndexOfWithDefaults(CharSequence message, char c, int value, boolean flag, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         value = StringsKt.getLastIndex(message);
      }

      if ((value2 & 4) != 0) {
         flag = false;
      }

      return StringsKt.lastIndexOf(message, c, value, flag);
   }

   public static final int lastIndexOf(@NotNull CharSequence message, @NotNull String text, int value, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      Intrinsics.checkParameterIsNotNull(text, "string");
      return !flag && message instanceof String ? ((String)message).lastIndexOf(text, value) : indexOfImpl(message, text, value, 0, flag, true);
   }

   public static int lastIndexOfWithDefaults(CharSequence message, String text, int value, boolean flag, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         value = StringsKt.getLastIndex(message);
      }

      if ((value2 & 4) != 0) {
         flag = false;
      }

      return StringsKt.lastIndexOf(message, text, value, flag);
   }

   public static final boolean contains(@NotNull CharSequence message, @NotNull CharSequence message2, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      Intrinsics.checkParameterIsNotNull(message2, "other");
      return message2 instanceof String ? StringsKt.indexOfWithDefaults(message, (String)message2, 0, flag, 2, null) >= 0 : indexOfImplWithDefaults(message, message2, 0, message.length(), flag, false, 16, null) >= 0;
   }

   public static boolean containsWithDefaults(CharSequence message, CharSequence message2, boolean flag, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = false;
      }

      return StringsKt.contains((CharSequence)message, (CharSequence)message2, flag);
   }

   public static final boolean contains(@NotNull CharSequence message, char c, boolean flag) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      return StringsKt.indexOfWithDefaults((CharSequence)message, c, (int)0, flag, 2, null) >= 0;
   }

   public static boolean containsWithDefaults(CharSequence message, char c, boolean flag, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = false;
      }

      return StringsKt.contains(message, c, flag);
   }

   private static final KotlinAInterface3 rangesDelimitedBy(CharSequence message, char[] chars, int value, boolean flag, int value2) {
      StringsKt.requireNonNegativeLimit(value2);
      return new KotlinHelper20(message, value, value2, (obj, obj2) -> {
         CharSequence message2 = (CharSequence) obj;
         Intrinsics.checkParameterIsNotNull(message2, "$this$DelimitedRangesSequence");
         int indexOfAny2 = StringsKt.indexOfAny(message2, chars, ((Integer) obj2), flag);
         return indexOfAny2 < 0 ? null : TuplesKt.computePair(indexOfAny2, 1);
      });
   }

   static KotlinAInterface3 rangesDelimitedByWithDefaults(CharSequence message, char[] chars, int value, boolean flag, int value2, int value3, Object obj) {
      if ((value3 & 2) != 0) {
         value = 0;
      }

      if ((value3 & 4) != 0) {
         flag = false;
      }

      if ((value3 & 8) != 0) {
         value2 = 0;
      }

      return rangesDelimitedBy(message, chars, value, flag, value2);
   }

   private static final KotlinAInterface3 rangesDelimitedBy(CharSequence message, String[] texts, int value, boolean flag, int value2) {
      StringsKt.requireNonNegativeLimit(value2);
      List list = ArraysKt.computeList(texts);
      return new KotlinHelper20(message, value, value2, (obj, obj2) -> {
         CharSequence message2 = (CharSequence) obj;
         Intrinsics.checkParameterIsNotNull(message2, "$this$DelimitedRangesSequence");
         Pair anyOf = findAnyOf(message2, list, ((Integer) obj2), flag, false);
         if (anyOf != null) {
            Pair pair = anyOf;
            anyOf = TuplesKt.computePair(pair.getFirst(), ((String)pair.getSecond()).length());
         } else {
            anyOf = null;
         }

         return anyOf;
      });
   }

   static KotlinAInterface3 rangesDelimitedByWithDefaults(CharSequence message, String[] texts, int value, boolean flag, int value2, int value3, Object obj) {
      if ((value3 & 2) != 0) {
         value = 0;
      }

      if ((value3 & 4) != 0) {
         flag = false;
      }

      if ((value3 & 8) != 0) {
         value2 = 0;
      }

      return rangesDelimitedBy(message, texts, value, flag, value2);
   }

   public static final void requireNonNegativeLimit(int value) {
      if (value < 0) {
         String text = "Limit must be non-negative, but was " + value;
         throw new IllegalArgumentException(text.toString());
      }
   }

   public static final List split(@NotNull CharSequence message, @NotNull String[] texts, boolean flag, int value) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      Intrinsics.checkParameterIsNotNull(texts, "delimiters");
      if (texts.length == 1) {
         String text = texts[0];
         if (text.length() != 0) {
            return splitSingle(message, text, flag, value);
         }
      }

      Iterable iterable = KotlinStub9.computeIterable(rangesDelimitedByWithDefaults(message, texts, 0, flag, value, 2, null));
      var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

      for (Object obj : iterable) {
         KotlinIntProgressionSub kotlinIntProgressionSub = (KotlinIntProgressionSub)obj;
         arrayList.add(StringsKt.substring(message, kotlinIntProgressionSub));
      }

      return arrayList;
   }

   public static List splitWithDefaults(CharSequence message, String[] texts, boolean flag, int value, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         flag = false;
      }

      if ((value2 & 4) != 0) {
         value = 0;
      }

      return StringsKt.split(message, texts, flag, value);
   }

   public static final List split(@NotNull CharSequence message, @NotNull char[] chars, boolean flag, int value) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      Intrinsics.checkParameterIsNotNull(chars, "delimiters");
      if (chars.length == 1) {
         return splitSingle(message, String.valueOf(chars[0]), flag, value);
      }

      Iterable iterable = KotlinStub9.computeIterable(rangesDelimitedByWithDefaults(message, chars, 0, flag, value, 2, null));
      var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

      for (Object obj : iterable) {
         KotlinIntProgressionSub kotlinIntProgressionSub = (KotlinIntProgressionSub)obj;
         arrayList.add(StringsKt.substring(message, kotlinIntProgressionSub));
      }

      return arrayList;
   }

   public static List splitWithDefaults(CharSequence message, char[] chars, boolean flag, int value, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         flag = false;
      }

      if ((value2 & 4) != 0) {
         value = 0;
      }

      return StringsKt.split(message, chars, flag, value);
   }

   private static final List splitSingle(CharSequence message, String text, boolean flag, int value) {
      StringsKt.requireNonNegativeLimit(value);
      int value2 = 0;
      int indexOf2 = StringsKt.indexOf(message, text, value2, flag);
      if (indexOf2 != -1 && value != 1) {
         boolean flag2 = value > 0;
         ArrayList arrayList = new ArrayList(flag2 ? KotlinRangesKtSub.coerceAtMost(value, 10) : 10);

         do {
            arrayList.add(message.subSequence(value2, indexOf2).toString());
            value2 = indexOf2 + text.length();
            if (flag2 && arrayList.size() == value - 1) {
               break;
            }

            indexOf2 = StringsKt.indexOf(message, text, value2, flag);
         } while (indexOf2 != -1);

         arrayList.add(message.subSequence(value2, message.length()).toString());
         return arrayList;
      } else {
         return CollectionsKt.computeList(message.toString());
      }
   }

}
