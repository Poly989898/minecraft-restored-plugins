package io.lumine.mythic.lib.script.mechanic.misc;

import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

@MechanicMetadata
public class ApplyCooldownMechanic extends Mechanic {
   private final String cooldownPath;
   private final DoubleFormula amount;

   public ApplyCooldownMechanic(ConfigObject config) {
      config.validateKeys("path", "amount");
      this.cooldownPath = config.getString("path");
      this.amount = new DoubleFormula(config.getString("amount"));
   }

   @Override
   public void cast(SkillMetadata meta) {
      double amount = this.amount.evaluate(meta);
      if (amount > 0.0) {
         meta.getCaster().getData().getCooldownMap().applyCooldown(this.cooldownPath, amount);
      }
   }
}
