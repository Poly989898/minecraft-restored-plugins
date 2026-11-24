package io.lumine.mythic.lib.script.condition;

import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

public abstract class Condition {
   private final boolean reversed;

   public Condition(ConfigObject config) {
      this.reversed = config.getBoolean("reverse", false);
   }

   public boolean checkIfMet(SkillMetadata meta) {
      return this.reversed ? !this.isMet(meta) : this.isMet(meta);
   }

   public abstract boolean isMet(SkillMetadata var1);
}
