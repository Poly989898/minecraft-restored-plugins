package io.lumine.mythic.core.skills.variables;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;

public abstract class VariableCondition extends SkillCondition {
   protected PlaceholderString key;
   protected VariableScope scope = VariableScope.SKILL;

   public VariableCondition(String line, MythicLineConfig mlc) {
      super(line);
      String key = mlc.getString(new String[]{"name", "n", "variable", "var", "key", "k"}, this.conditionVar);
      if (key == null) {
         MythicLogger.errorConditionConfig(this, mlc, "Variable name must be set.");
      } else {
         String prefixVar = VariableScope.SKILL.toString();
         if (key.contains(".")) {
            String[] split = key.split("\\.", 2);
            prefixVar = split[0].toUpperCase();
            this.key = PlaceholderString.of(split[1]);
         } else {
            this.key = PlaceholderString.of(key);
         }

         String strScope = mlc.getString(new String[]{"scope", "s"}, prefixVar);

         try {
            this.scope = VariableScope.valueOf(strScope.toUpperCase());
         } catch (Exception e) {
            MythicLogger.errorConditionConfig(this, mlc, "'" + strScope + "' is not a valid variable scope.");
         }
      }
   }
}
