package io.lumine.mythic.lib.script.condition.generic;

import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

public class InBetweenCondition extends Condition {
   private final DoubleFormula first;
   private final DoubleFormula second;
   private final DoubleFormula third;

   public InBetweenCondition(ConfigObject config) {
      super(config);
      config.validateKeys("first", "second", "third");
      this.first = new DoubleFormula(config.getString("first"));
      this.second = new DoubleFormula(config.getString("second"));
      this.third = new DoubleFormula(config.getString("third"));
   }

   @Override
   public boolean isMet(SkillMetadata meta) {
      double middle = this.second.evaluate(meta);
      return this.first.evaluate(meta) <= middle && middle < this.third.evaluate(meta);
   }
}
