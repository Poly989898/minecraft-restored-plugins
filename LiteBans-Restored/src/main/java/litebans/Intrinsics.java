package litebans;

import java.util.Arrays;

public class Intrinsics {
   private Intrinsics() {
   }

   public static void checkNotNull(Object obj) {
      if (obj == null) {
         throwNpe();
      }
   }

   public static void checkNotNullExpressionValue(Object obj, String text) {
      if (obj == null) {
         throwNpeWithMessage(text);
      }
   }

   public static void throwNpe() {
      throw (NullPointerException)sanitizeStackTrace(new NullPointerException());
   }

   public static void throwNpeWithMessage(String text) {
      throw (NullPointerException)sanitizeStackTrace(new NullPointerException(text));
   }

   public static void throwLiteMessage(String text) {
      throw (LiteMessageException)sanitizeStackTrace(new LiteMessageException(text));
   }

   public static void throwUninitializedProperty(String text) {
      throwLiteMessage("lateinit property " + text + " has not been initialized");
   }

   public static void checkNotNullField(Object obj, String text) {
      if (obj == null) {
         throw (NullPointerException)sanitizeStackTrace(new NullPointerException(text + " must not be null"));
      }
   }

   public static void checkParameterIsNotNull(Object obj, String text) {
      if (obj == null) {
         throwParameterIsNull(text);
      }
   }

   private static final void throwParameterIsNull(String text) {
      throw (NullPointerException)sanitizeStackTrace(new NullPointerException(createParameterMessage(text)));
   }

   private static final String createParameterMessage(String text) {
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      String name = Intrinsics.class.getName();
      int value = 0;

      while (!stackTrace[value].getClassName().equals(name)) {
         value++;
      }

      while (stackTrace[value].getClassName().equals(name)) {
         value++;
      }

      StackTraceElement stackTraceElement = stackTrace[value];
      String className = stackTraceElement.getClassName();
      String methodName = stackTraceElement.getMethodName();
      return "Parameter specified as non-null is null: method " + className + "." + methodName + ", parameter " + text;
   }

   public static int compare(long value, long value2) {
      return value < value2 ? -1 : (value == value2 ? 0 : 1);
   }

   public static int compare(int value, int value2) {
      return value < value2 ? -1 : (value == value2 ? 0 : 1);
   }

   public static boolean areEqual(Object obj, Object obj2) {
      return obj == null ? obj2 == null : obj.equals(obj2);
   }

   private static final Throwable sanitizeStackTrace(Throwable error) {
      return sanitizeStackTraceFrom(error, Intrinsics.class.getName());
   }

   static Throwable sanitizeStackTraceFrom(Throwable error, String text) {
      StackTraceElement[] stackTrace = error.getStackTrace();
      int length2 = stackTrace.length;
      int value = -1;

      for (int i = 0; i < length2; i++) {
         if (text.equals(stackTrace[i].getClassName())) {
            value = i;
         }
      }

      StackTraceElement[] copyOfRange2 = Arrays.copyOfRange(stackTrace, value + 1, length2);
      error.setStackTrace(copyOfRange2);
      return error;
   }
}
