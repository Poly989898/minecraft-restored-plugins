package io.lumine.mythic.core.volatilecode.v26_2;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class VolatileDamageSource extends DamageSource {
   private final DamageSource baseSource;
   private final Map<TagKey<DamageType>, Boolean> damageAttributes = Maps.newConcurrentMap();

   public static VolatileDamageSource of(DamageSource source) {
      return source.getDirectEntity() != null
         ? new VolatileDamageSource(source, source.getDirectEntity(), source.getEntity())
         : new VolatileDamageSource(source, source.getSourcePosition());
   }

   protected VolatileDamageSource(DamageSource source, Entity directEntity, Entity causingEntity) {
      super(source.typeHolder(), directEntity, causingEntity);
      this.baseSource = source;
   }

   protected VolatileDamageSource(DamageSource source, Vec3 damageSourcePosition) {
      super(source.typeHolder(), damageSourcePosition);
      this.baseSource = source;
   }

   public void ignoreArmor(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.BYPASSES_ARMOR, bool);
   }

   public void ignoreEnchantments(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.BYPASSES_ENCHANTMENTS, bool);
   }

   public void ignoreInvulnerability(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.BYPASSES_INVULNERABILITY, bool);
   }

   public void noAnger(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.NO_ANGER, bool);
   }

   public void damagesHelmet(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.DAMAGES_HELMET, bool);
   }

   public void ignoreShield(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.BYPASSES_SHIELD, bool);
   }

   public void ignoreCooldown(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.BYPASSES_COOLDOWN, bool);
   }

   public void noImpact(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.NO_IMPACT, bool);
   }

   public void ignoreEffects(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.BYPASSES_EFFECTS, bool);
   }

   public void ignoreResistance(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.BYPASSES_RESISTANCE, bool);
   }

   public boolean is(TagKey<DamageType> tagkey) {
      return this.damageAttributes.containsKey(tagkey) ? this.damageAttributes.get(tagkey) : this.typeHolder().is(tagkey);
   }

   public boolean is(ResourceKey<DamageType> resourcekey) {
      return this.typeHolder().is(resourcekey);
   }

   public DamageCause knownCause() {
      return this.baseSource.knownCause();
   }

   public Component getLocalizedDeathMessage(LivingEntity entity) {
      return this.baseSource != null ? this.baseSource.getLocalizedDeathMessage(entity) : null;
   }
}
