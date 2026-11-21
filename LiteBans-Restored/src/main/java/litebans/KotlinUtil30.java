package litebans;

import java.util.Collection;
import java.util.List;

public class KotlinUtil30 {
   private static final Throwable sanitizeStackTrace(Throwable error) {
      return Intrinsics.sanitizeStackTraceFrom(error, KotlinUtil30.class.getName());
   }

   public static void throwCce(Object obj, String text) {
      String text2 = obj == null ? "null" : obj.getClass().getName();
      throwCceMessage(text2 + " cannot be cast to " + text);
   }

   public static void throwCceMessage(String text) {
      throw castException(new ClassCastException(text));
   }

   public static ClassCastException castException(ClassCastException classCastException) {
      throw (ClassCastException)sanitizeStackTrace(classCastException);
   }

   public static Iterable asMutableIterable(Object obj) {
      if (obj instanceof KMappedMarker && !(obj instanceof KotlinKMappedMarkerSub)) {
         throwCce(obj, "kotlin.collections.MutableIterable");
      }

      return castToIterable(obj);
   }

   public static Iterable castToIterable(Object obj) {
      try {
         return (Iterable)obj;
      } catch (ClassCastException e) {
         throw castException(e);
      }
   }

   public static Collection asMutableCollection(Object obj) {
      if (obj instanceof KMappedMarker && !(obj instanceof KotlinMarkerInterface3)) {
         throwCce(obj, "kotlin.collections.MutableCollection");
      }

      return castToCollection(obj);
   }

   public static Collection castToCollection(Object obj) {
      try {
         return (Collection)obj;
      } catch (ClassCastException e) {
         throw castException(e);
      }
   }

   public static List asMutableList(Object obj) {
      if (obj instanceof KMappedMarker && !(obj instanceof KotlinMarkerInterface5)) {
         throwCce(obj, "kotlin.collections.MutableList");
      }

      return castToList(obj);
   }

   public static List castToList(Object obj) {
      try {
         return (List)obj;
      } catch (ClassCastException e) {
         throw castException(e);
      }
   }
}
