package io.lumine.mythic.lib.script.mechanic.buff;

import io.lumine.mythic.lib.player.cooldown.CooldownInfo;
import io.lumine.mythic.lib.script.mechanic.type.TargetMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.function.BiConsumer;
import org.bukkit.entity.Entity;

public class ReduceCooldownMechanic extends TargetMechanic {
   private final DoubleFormula value;
   private final ReduceCooldownMechanic.ReductionType type;
   private final String cooldownPath;

   public ReduceCooldownMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("value", "path");
      this.cooldownPath = config.getString("path");
      this.type = config.contains("type")
         ? ReduceCooldownMechanic.ReductionType.valueOf(config.getString("type").toUpperCase())
         : ReduceCooldownMechanic.ReductionType.FLAT;
      this.value = new DoubleFormula(config.getString("value"));
   }

   @Override
   public void cast(SkillMetadata meta, Entity target) {
      CooldownInfo info = meta.getCaster().getData().getCooldownMap().getInfo(this.cooldownPath);
      if (info != null && !info.hasEnded()) {
         this.type.apply(info, this.value.evaluate(meta));
      }
   }

   public enum ReductionType {
      FLAT((info, value) -> info.reduceFlat(value)),
      INITIAL((info, value) -> info.reduceInitialCooldown(value)),
      REMAINING((info, value) -> info.reduceRemainingCooldown(value));

      private final BiConsumer<CooldownInfo, Double> effect;

      ReductionType(BiConsumer<CooldownInfo, Double> effect) {
         this.effect = effect;
      }

      public void apply(CooldownInfo info, double value) {
         this.effect.accept(info, value);
      }
   }
}
