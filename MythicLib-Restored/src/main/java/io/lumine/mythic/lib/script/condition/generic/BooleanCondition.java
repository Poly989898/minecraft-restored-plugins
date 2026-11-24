package io.lumine.mythic.lib.script.condition.generic;

import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import io.lumine.mythic.lib.util.formula.BooleanExpression;

public class BooleanCondition extends Condition {
   private final String formula;

   public BooleanCondition(ConfigObject config) {
      super(config);
      config.validateKeys("formula");
      this.formula = config.getString("formula");
   }

   @Override
   public boolean isMet(SkillMetadata meta) {
      return BooleanExpression.eval(meta.parseString(this.formula));
   }
}
