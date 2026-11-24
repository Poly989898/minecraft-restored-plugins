package io.lumine.mythic.lib.script.mechanic.offense;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.TargetMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@MechanicMetadata
public class PotionMechanic extends TargetMechanic {
   private final PotionEffectType effect;
   private final DoubleFormula duration;
   private final DoubleFormula level;
   private final boolean ambient;
   private final boolean particles;
   private final boolean icon;

   public PotionMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("effect", "duration");
      this.effect = PotionEffectType.getByName(UtilityMethods.enumName(config.getString("effect")));
      this.level = new DoubleFormula(config.getString("level", "1"));
      this.duration = new DoubleFormula(config.getString("duration"));
      this.ambient = config.getBoolean("ambient", true);
      this.particles = config.getBoolean("particles", true);
      this.icon = config.getBoolean("icon", true);
   }

   @Override
   public void cast(SkillMetadata meta, Entity target) {
      Validate.isTrue(target instanceof LivingEntity, "Cannot add a potion effect to a non living entity");
      ((LivingEntity)target)
         .addPotionEffect(
            new PotionEffect(this.effect, (int)this.duration.evaluate(meta), (int)this.level.evaluate(meta), this.ambient, this.particles, this.icon)
         );
   }
}
