package io.lumine.mythic.lib.script.mechanic.offense;

import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.TargetMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.entity.Entity;

@MechanicMetadata
public class SetOnFireMechanic extends TargetMechanic {
   private final DoubleFormula ticks;
   private final boolean stack;
   private final boolean min;
   private final boolean max;

   public SetOnFireMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("ticks");
      this.stack = config.getBoolean("stack", false);
      this.min = config.getBoolean("min", false);
      this.max = config.getBoolean("max", false);
      this.ticks = config.getDoubleFormula("ticks");
   }

   @Override
   public void cast(SkillMetadata meta, Entity target) {
      int ticks = (int)this.ticks.evaluate(meta);
      if (this.stack) {
         ticks += target.getFireTicks();
      } else if (this.max) {
         ticks = Math.max(ticks, target.getFireTicks());
      } else if (this.min) {
         ticks = Math.min(ticks, target.getFireTicks());
      }

      target.setFireTicks(ticks);
   }
}
