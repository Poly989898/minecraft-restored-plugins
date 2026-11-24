package io.lumine.mythic.lib.script.condition.generic;

import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

public class StringEqualsCondition extends Condition {
   private final String first;
   private final String second;
   private final boolean ignoreCase;

   public StringEqualsCondition(ConfigObject config) {
      super(config);
      config.validateKeys("first", "second");
      this.first = config.getString("first");
      this.second = config.getString("second");
      this.ignoreCase = config.getBoolean("ignore_case", false);
   }

   @Override
   public boolean isMet(SkillMetadata meta) {
      return this.ignoreCase
         ? meta.parseString(this.first).equalsIgnoreCase(meta.parseString(this.second))
         : meta.parseString(this.first).equals(meta.parseString(this.second));
   }
}
