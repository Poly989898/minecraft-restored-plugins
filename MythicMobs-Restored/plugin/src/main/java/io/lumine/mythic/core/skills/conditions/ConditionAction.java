package io.lumine.mythic.core.skills.conditions;

import java.util.HashSet;

public enum ConditionAction {
   REQUIRED(false, "TRUE"),
   CANCEL(false, "FALSE"),
   POWER(true),
   CAST(true),
   CASTINSTEAD(true),
   ORELSECAST(true),
   LEVEL(true);

   private final boolean hasOption;
   private final HashSet<String> aliases = new HashSet<>();

   private ConditionAction(boolean hasOption, String... aliases) {
      this.hasOption = hasOption;
      if (aliases != null && aliases.length > 0) {
         for (String alias : aliases) {
            this.aliases.add(alias);
         }
      }
   }

   public static boolean isAction(String type) {
      String typeUpperCase = type.toUpperCase();

      for (ConditionAction action : values()) {
         if (action.toString().equals(typeUpperCase)) {
            return true;
         }

         for (String alias : action.aliases) {
            if (alias.toUpperCase().equals(typeUpperCase)) {
               return true;
            }
         }
      }

      return false;
   }

   public static ConditionAction get(String type) {
      if (type == null) {
         return REQUIRED;
      } else {
         String typeUpperCase = type.toUpperCase();

         for (ConditionAction action : values()) {
            if (action.toString().equals(typeUpperCase)) {
               return action;
            }

            for (String alias : action.aliases) {
               if (alias.toUpperCase().equals(typeUpperCase)) {
                  return action;
               }
            }
         }

         return REQUIRED;
      }
   }
}
