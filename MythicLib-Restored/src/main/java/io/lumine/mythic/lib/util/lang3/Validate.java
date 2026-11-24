package io.lumine.mythic.lib.util.lang3;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/** Argument validation helpers (API compatible with the commons-lang3 methods MythicLib plugins use). */
public class Validate {
   private static final String DEFAULT_IS_TRUE_EX_MESSAGE = "The validated expression is false";
   private static final String DEFAULT_NOT_NAN_EX_MESSAGE = "The validated value is not a number";
   private static final String DEFAULT_FINITE_EX_MESSAGE = "The value is invalid: %f";
   private static final String DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE = "The value %s is not in the specified exclusive range of %s to %s";
   private static final String DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE = "The value %s is not in the specified inclusive range of %s to %s";
   private static final String DEFAULT_MATCHES_PATTERN_EX = "The string %s does not match the pattern %s";
   private static final String DEFAULT_IS_NULL_EX_MESSAGE = "The validated object is null";
   private static final String DEFAULT_IS_INSTANCE_OF_EX_MESSAGE = "Expected type: %s, actual: %s";
   private static final String DEFAULT_IS_ASSIGNABLE_EX_MESSAGE = "Cannot assign a %s to a %s";
   private static final String DEFAULT_NO_NULL_ELEMENTS_ARRAY_EX_MESSAGE = "The validated array contains null element at index: %d";
   private static final String DEFAULT_NO_NULL_ELEMENTS_COLLECTION_EX_MESSAGE = "The validated collection contains null element at index: %d";
   private static final String DEFAULT_NOT_BLANK_EX_MESSAGE = "The validated character sequence is blank";
   private static final String DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE = "The validated array is empty";
   private static final String DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE = "The validated character sequence is empty";
   private static final String DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE = "The validated collection is empty";
   private static final String DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE = "The validated map is empty";
   private static final String DEFAULT_VALID_INDEX_ARRAY_EX_MESSAGE = "The validated array index is invalid: %d";
   private static final String DEFAULT_VALID_INDEX_CHAR_SEQUENCE_EX_MESSAGE = "The validated character sequence index is invalid: %d";
   private static final String DEFAULT_VALID_INDEX_COLLECTION_EX_MESSAGE = "The validated collection index is invalid: %d";
   private static final String DEFAULT_VALID_STATE_EX_MESSAGE = "The validated state is false";

   private static String fmt(String message, Object... values) {
      return String.format(message, values);
   }

   public static String ClassUtils_getName(Class<?> cls, String valueIfNull) {
      return cls == null ? valueIfNull : cls.getName();
   }

   public static String ClassUtils_getName(Object object) {
      return ClassUtils_getName(object, "");
   }

   public static String ClassUtils_getName(Object object, String valueIfNull) {
      return object == null ? valueIfNull : object.getClass().getName();
   }

   @SafeVarargs
   public static <T> T[] ArrayUtils__addAll(T[] array1, T... array2) {
      if (array1 == null) {
         return array2 == null ? null : array2.clone();
      }
      if (array2 == null) {
         return array1.clone();
      }
      T[] joined = java.util.Arrays.copyOf(array1, array1.length + array2.length);
      System.arraycopy(array2, 0, joined, array1.length, array2.length);
      return joined;
   }

   @SuppressWarnings("unchecked")
   public static <T> T[] ArrayUtils__newInstance(Class<T> componentType, int length) {
      return (T[])java.lang.reflect.Array.newInstance(componentType, length);
   }

   public static void isTrue(boolean expression, String message, long value) {
      if (!expression) {
         throw new IllegalArgumentException(fmt(message, value));
      }
   }

   public static void isTrue(boolean expression, String message, double value) {
      if (!expression) {
         throw new IllegalArgumentException(fmt(message, value));
      }
   }

   public static void isTrue(boolean expression, String message, Object... values) {
      if (!expression) {
         throw new IllegalArgumentException(fmt(message, values));
      }
   }

   public static void isTrue(boolean expression) {
      if (!expression) {
         throw new IllegalArgumentException(DEFAULT_IS_TRUE_EX_MESSAGE);
      }
   }

   public static <T> T notNull(T object) {
      return notNull(object, DEFAULT_IS_NULL_EX_MESSAGE);
   }

   public static <T> T notNull(T object, String message, Object... values) {
      if (object == null) {
         throw new NullPointerException(fmt(message, values));
      }
      return object;
   }

   public static <T> T[] notEmpty(T[] array, String message, Object... values) {
      notNull(array, message, values);
      if (array.length == 0) {
         throw new IllegalArgumentException(fmt(message, values));
      }
      return array;
   }

   public static <T> T[] notEmpty(T[] array) {
      return notEmpty(array, DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE);
   }

   public static <T extends Collection<?>> T notEmpty(T collection, String message, Object... values) {
      notNull(collection, message, values);
      if (collection.isEmpty()) {
         throw new IllegalArgumentException(fmt(message, values));
      }
      return collection;
   }

   public static <T extends Collection<?>> T notEmpty(T collection) {
      return notEmpty(collection, DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE);
   }

   public static <T extends Map<?, ?>> T notEmpty(T map, String message, Object... values) {
      notNull(map, message, values);
      if (map.isEmpty()) {
         throw new IllegalArgumentException(fmt(message, values));
      }
      return map;
   }

   public static <T extends Map<?, ?>> T notEmpty(T map) {
      return notEmpty(map, DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE);
   }

   public static <T extends CharSequence> T notEmpty(T chars, String message, Object... values) {
      notNull(chars, message, values);
      if (chars.length() == 0) {
         throw new IllegalArgumentException(fmt(message, values));
      }
      return chars;
   }

   public static <T extends CharSequence> T notEmpty(T chars) {
      return notEmpty(chars, DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE);
   }

   public static <T extends CharSequence> T notBlank(T chars, String message, Object... values) {
      notNull(chars, message, values);
      if (chars.toString().isBlank()) {
         throw new IllegalArgumentException(fmt(message, values));
      }
      return chars;
   }

   public static <T extends CharSequence> T notBlank(T chars) {
      return notBlank(chars, DEFAULT_NOT_BLANK_EX_MESSAGE);
   }

   public static <T> T[] noNullElements(T[] array, String message, Object... values) {
      notNull(array);
      for (int i = 0; i < array.length; i++) {
         if (array[i] == null) {
            Object[] formatArgs = ArrayUtils__addAll(values, i);
            throw new IllegalArgumentException(fmt(message, formatArgs));
         }
      }
      return array;
   }

   public static <T> T[] noNullElements(T[] array) {
      return noNullElements(array, DEFAULT_NO_NULL_ELEMENTS_ARRAY_EX_MESSAGE);
   }

   public static <T extends Iterable<?>> T noNullElements(T iterable, String message, Object... values) {
      notNull(iterable);
      int i = 0;
      for (java.util.Iterator<?> it = iterable.iterator(); it.hasNext(); i++) {
         if (it.next() == null) {
            Object[] formatArgs = ArrayUtils__addAll(values, i);
            throw new IllegalArgumentException(fmt(message, formatArgs));
         }
      }
      return iterable;
   }

   public static <T extends Iterable<?>> T noNullElements(T iterable) {
      return noNullElements(iterable, DEFAULT_NO_NULL_ELEMENTS_COLLECTION_EX_MESSAGE);
   }

   public static <T> T[] validIndex(T[] array, int index, String message, Object... values) {
      notNull(array);
      if (index < 0 || index >= array.length) {
         throw new IndexOutOfBoundsException(fmt(message, values));
      }
      return array;
   }

   public static <T> T[] validIndex(T[] array, int index) {
      return validIndex(array, index, DEFAULT_VALID_INDEX_ARRAY_EX_MESSAGE, index);
   }

   public static <T extends Collection<?>> T validIndex(T collection, int index, String message, Object... values) {
      notNull(collection);
      if (index < 0 || index >= collection.size()) {
         throw new IndexOutOfBoundsException(fmt(message, values));
      }
      return collection;
   }

   public static <T extends Collection<?>> T validIndex(T collection, int index) {
      return validIndex(collection, index, DEFAULT_VALID_INDEX_COLLECTION_EX_MESSAGE, index);
   }

   public static <T extends CharSequence> T validIndex(T chars, int index, String message, Object... values) {
      notNull(chars);
      if (index < 0 || index >= chars.length()) {
         throw new IndexOutOfBoundsException(fmt(message, values));
      }
      return chars;
   }

   public static <T extends CharSequence> T validIndex(T chars, int index) {
      return validIndex(chars, index, DEFAULT_VALID_INDEX_CHAR_SEQUENCE_EX_MESSAGE, index);
   }

   public static void validState(boolean expression) {
      if (!expression) {
         throw new IllegalStateException(DEFAULT_VALID_STATE_EX_MESSAGE);
      }
   }

   public static void validState(boolean expression, String message, Object... values) {
      if (!expression) {
         throw new IllegalStateException(fmt(message, values));
      }
   }

   public static void matchesPattern(CharSequence input, String pattern) {
      if (!Pattern.matches(pattern, input)) {
         throw new IllegalArgumentException(fmt(DEFAULT_MATCHES_PATTERN_EX, input, pattern));
      }
   }

   public static void matchesPattern(CharSequence input, String pattern, String message, Object... values) {
      if (!Pattern.matches(pattern, input)) {
         throw new IllegalArgumentException(fmt(message, values));
      }
   }

   public static void notNaN(double value) {
      notNaN(value, DEFAULT_NOT_NAN_EX_MESSAGE);
   }

   public static void notNaN(double value, String message, Object... values) {
      if (Double.isNaN(value)) {
         throw new IllegalArgumentException(fmt(message, values));
      }
   }

   public static void finite(double value) {
      finite(value, DEFAULT_FINITE_EX_MESSAGE, value);
   }

   public static void finite(double value, String message, Object... values) {
      if (Double.isNaN(value) || Double.isInfinite(value)) {
         throw new IllegalArgumentException(fmt(message, values));
      }
   }

   public static <T> void inclusiveBetween(T start, T end, Comparable<T> value) {
      if (value.compareTo(start) < 0 || value.compareTo(end) > 0) {
         throw new IllegalArgumentException(fmt(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
      }
   }

   public static <T> void inclusiveBetween(T start, T end, Comparable<T> value, String message, Object... values) {
      if (value.compareTo(start) < 0 || value.compareTo(end) > 0) {
         throw new IllegalArgumentException(fmt(message, values));
      }
   }

   public static void inclusiveBetween(long start, long end, long value) {
      if (value < start || value > end) {
         throw new IllegalArgumentException(fmt(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
      }
   }

   public static void inclusiveBetween(long start, long end, long value, String message) {
      if (value < start || value > end) {
         throw new IllegalArgumentException(message);
      }
   }

   public static void inclusiveBetween(double start, double end, double value) {
      if (value < start || value > end) {
         throw new IllegalArgumentException(fmt(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
      }
   }

   public static void inclusiveBetween(double start, double end, double value, String message) {
      if (value < start || value > end) {
         throw new IllegalArgumentException(message);
      }
   }

   public static <T> void exclusiveBetween(T start, T end, Comparable<T> value) {
      if (value.compareTo(start) <= 0 || value.compareTo(end) >= 0) {
         throw new IllegalArgumentException(fmt(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
      }
   }

   public static <T> void exclusiveBetween(T start, T end, Comparable<T> value, String message, Object... values) {
      if (value.compareTo(start) <= 0 || value.compareTo(end) >= 0) {
         throw new IllegalArgumentException(fmt(message, values));
      }
   }

   public static void exclusiveBetween(long start, long end, long value) {
      if (value <= start || value >= end) {
         throw new IllegalArgumentException(fmt(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
      }
   }

   public static void exclusiveBetween(long start, long end, long value, String message) {
      if (value <= start || value >= end) {
         throw new IllegalArgumentException(message);
      }
   }

   public static void exclusiveBetween(double start, double end, double value) {
      if (value <= start || value >= end) {
         throw new IllegalArgumentException(fmt(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
      }
   }

   public static void exclusiveBetween(double start, double end, double value, String message) {
      if (value <= start || value >= end) {
         throw new IllegalArgumentException(message);
      }
   }

   public static void isInstanceOf(Class<?> type, Object obj) {
      if (!type.isInstance(obj)) {
         throw new IllegalArgumentException(fmt(DEFAULT_IS_INSTANCE_OF_EX_MESSAGE, type.getName(), ClassUtils_getName(obj, "null")));
      }
   }

   public static void isInstanceOf(Class<?> type, Object obj, String message, Object... values) {
      if (!type.isInstance(obj)) {
         throw new IllegalArgumentException(fmt(message, values));
      }
   }

   public static void isAssignableFrom(Class<?> superType, Class<?> type) {
      if (type == null || !superType.isAssignableFrom(type)) {
         throw new IllegalArgumentException(fmt(DEFAULT_IS_ASSIGNABLE_EX_MESSAGE, ClassUtils_getName(type, "null"), superType.getName()));
      }
   }

   public static void isAssignableFrom(Class<?> superType, Class<?> type, String message, Object... values) {
      if (type == null || !superType.isAssignableFrom(type)) {
         throw new IllegalArgumentException(fmt(message, values));
      }
   }
}
