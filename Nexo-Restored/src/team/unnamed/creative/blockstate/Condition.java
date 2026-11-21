package team.unnamed.creative.blockstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface Condition {
   Condition NONE = new Condition() {
      public @NotNull String toString() {
         return "Condition.NONE";
      }
   };

   static Condition and(Condition... conditions) {
      return new And(Arrays.asList(conditions));
   }

   static Condition and(List<Condition> conditions) {
      return new And(new ArrayList(conditions));
   }

   static Condition or(Condition... conditions) {
      return new Or(Arrays.asList(conditions));
   }

   static Condition or(List<Condition> conditions) {
      return new Or(new ArrayList(conditions));
   }

   static Condition match(String key, Object value) {
      return new Match(key, value);
   }

   public static class And implements Condition {
      private final List<Condition> conditions;

      private And(List<Condition> conditions) {
         this.conditions = conditions;
      }

      public List<Condition> conditions() {
         return this.conditions;
      }

      public @NotNull String toString() {
         String var10000 = this.getClass().getSimpleName();
         return var10000 + "{conditions=" + String.valueOf(this.conditions) + "}";
      }
   }

   public static class Or implements Condition {
      private final List<Condition> conditions;

      private Or(List<Condition> conditions) {
         this.conditions = conditions;
      }

      public List<Condition> conditions() {
         return this.conditions;
      }

      public @NotNull String toString() {
         String var10000 = this.getClass().getSimpleName();
         return var10000 + "{conditions=" + String.valueOf(this.conditions) + "}";
      }
   }

   public static class Match implements Condition {
      private final String key;
      private final Object value;

      private Match(String key, Object value) {
         this.key = key;
         this.value = value;
      }

      public String key() {
         return this.key;
      }

      public Object value() {
         return this.value;
      }

      public @NotNull String toString() {
         String var10000 = this.getClass().getSimpleName();
         return var10000 + "{key=" + this.key + ", value=" + String.valueOf(this.value) + "}";
      }
   }
}
