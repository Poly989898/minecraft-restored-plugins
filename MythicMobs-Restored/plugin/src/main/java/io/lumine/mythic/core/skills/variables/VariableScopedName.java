package io.lumine.mythic.core.skills.variables;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record VariableScopedName(PlaceholderString name, VariableScope scope) {
   private static final Pattern PLACEHOLDER_SECTION_PATTERN = Pattern.compile("<[^>]*>");

   public static VariableScopedName of(String strKey) {
      if (strKey.contains(".")) {
         Matcher matcher = PLACEHOLDER_SECTION_PATTERN.matcher(strKey);
         StringBuilder tempKey = new StringBuilder();

         while (matcher.find()) {
            String replacement = matcher.group().replace(".", "##DOT##");
            matcher.appendReplacement(tempKey, replacement);
         }

         matcher.appendTail(tempKey);
         String[] split = tempKey.toString().split("\\.", 2);
         if (split.length > 1) {
            String prefixVar = split[0].replace("##DOT##", ".").toUpperCase();

            VariableScope variableScopeDeferredValue;
            try {
               variableScopeDeferredValue = VariableScope.valueOf(prefixVar);
            } catch (IllegalArgumentException e) {
               variableScopeDeferredValue = VariableScope.SKILL;
            }

            return new VariableScopedName(PlaceholderString.of(split[1].replace("##DOT##", ".")), variableScopeDeferredValue);
         }
      }

      return new VariableScopedName(PlaceholderString.of(strKey), VariableScope.SKILL);
   }
}
