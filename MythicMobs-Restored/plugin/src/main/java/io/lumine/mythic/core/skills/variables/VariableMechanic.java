package io.lumine.mythic.core.skills.variables;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import java.io.File;

public class VariableMechanic extends SkillMechanic {
   protected PlaceholderString key;
   protected VariableScope scope;
   protected long duration;

   public VariableMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      String strKey = mlc.getString(new String[]{"name", "n", "variable", "var", "key", "k"}, null);
      if (strKey == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "Variable name must be set.");
      } else {
         VariableScopedName context = VariableScopedName.of(strKey);
         this.key = context.name();
         String strScope = mlc.getString(new String[]{"scope", "s"}, context.scope().toString());

         try {
            this.scope = VariableScope.valueOf(strScope.toUpperCase());
         } catch (Exception e) {
            MythicLogger.errorMechanicConfig(this, mlc, "'" + strScope + "' is not a valid variable scope.");
         }

         boolean shouldSave = mlc.getBoolean("save", false);
         long duration = mlc.getLong(new String[]{"duration", "d", "expire", "e"}, 0L);
         if (duration == 0L) {
            this.duration = shouldSave ? 0L : 1L;
         } else {
            this.duration = duration * 50L;
         }
      }
   }

   public VariableManager getVariableManager() {
      return this.getPlugin().getVariableManager();
   }

   protected void handleDuration(Variable var) {
      if (this.duration > 1L) {
         var.setExpirationTime(System.currentTimeMillis() + this.duration);
      }
   }
}
