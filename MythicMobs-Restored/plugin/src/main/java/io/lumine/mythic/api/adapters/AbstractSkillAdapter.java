package io.lumine.mythic.api.adapters;

import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.core.skills.mechanics.ShootFireballMechanic;
import io.lumine.mythic.core.skills.mechanics.ShootMechanic;
import io.lumine.mythic.core.skills.mechanics.ShootShulkerMechanic;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public interface AbstractSkillAdapter {
   void strikeLightning(AbstractLocation abstractLocation);

   boolean doDamage(DamageMetadata damageMetadata, AbstractEntity abstractEntity);

   AbstractVector throwSkill(AbstractLocation abstractLocation, AbstractEntity abstractEntity, float value, float value2);

   void itemSprayEffect(
      AbstractLocation abstractLocation, ItemStack itemStack, int value, int value2, double value3, double value4, double value5, double value6, boolean flag, boolean flag2
   );

   void strikeLightningEffect(AbstractLocation abstractLocation);

   void playSmokeEffect(AbstractLocation abstractLocation, int value);

   void shootFireball(
      SkillCaster skillCaster,
      ShootFireballMechanic.FireballType fireballType,
      AbstractLocation abstractLocation,
      AbstractLocation abstractLocation2,
      float value,
      float value2,
      boolean flag,
      int value3,
      boolean flag2,
      boolean flag3,
      AbstractItemStack abstractItemStack
   );

   void pushButton(SkillCaster skillCaster, AbstractLocation abstractLocation);

   void toggleLever(SkillCaster skillCaster, AbstractLocation abstractLocation, int value);

   AbstractEntity shootProjectile(
      SkillCaster skillCaster, AbstractLocation abstractLocation, AbstractLocation abstractLocation2, Class<? extends Projectile> clazz, AbstractVector abstractVector, ShootMechanic shootMechanic
   );

   AbstractEntity shootArcProjectile(SkillCaster skillCaster, AbstractLocation abstractLocation, AbstractLocation abstractLocation2, Class<? extends Projectile> clazz, float value, boolean flag);

   void executeVolley(SkillCaster skillCaster, AbstractLocation abstractLocation, int value, float value2, float value3, int value4, int value5, boolean flag);

   AbstractEntity rainProjectile(SkillCaster skillCaster, AbstractLocation abstractLocation, Class<? extends Projectile> clazz, float value);

   AbstractEntity shootShulkerBullet(SkillCaster skillCaster, AbstractEntity abstractEntity, AbstractLocation abstractLocation, ShootShulkerMechanic shootShulkerMechanic);
}
