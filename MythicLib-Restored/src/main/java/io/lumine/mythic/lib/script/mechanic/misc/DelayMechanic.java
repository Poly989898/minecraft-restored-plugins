package io.lumine.mythic.lib.script.mechanic.misc;

import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

@MechanicMetadata
public class DelayMechanic extends Mechanic {
   private final DoubleFormula delay;

   public DelayMechanic(ConfigObject config) {
      config.validateKeys("amount");
      this.delay = new DoubleFormula(config.getString("amount"));
   }

   public long getDelay(SkillMetadata meta) {
      return (long)this.delay.evaluate(meta);
   }

   @Override
   public void cast(SkillMetadata meta) {
      throw new RuntimeException("Cannot run this mechanic");
   }
}
