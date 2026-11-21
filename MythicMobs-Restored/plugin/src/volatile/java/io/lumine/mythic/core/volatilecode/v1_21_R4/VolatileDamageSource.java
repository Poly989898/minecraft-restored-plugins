package io.lumine.mythic.core.volatilecode.v1_21_R4;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class VolatileDamageSource extends DamageSource {
   private final DamageSource baseSource;
   private final Map<TagKey<DamageType>, Boolean> damageAttributes = Maps.newConcurrentMap();

   public static VolatileDamageSource of(DamageSource source) {
      return source.c() != null ? new VolatileDamageSource(source, source.c(), source.d()) : new VolatileDamageSource(source, source.i());
   }

   protected VolatileDamageSource(DamageSource source, Entity directEntity, Entity causingEntity) {
      super(source.l(), directEntity, causingEntity);
      this.baseSource = source;
   }

   protected VolatileDamageSource(DamageSource source, Vec3D damageSourcePosition) {
      super(source.l(), damageSourcePosition);
      this.baseSource = source;
   }

   public void ignoreArmor(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.b, bool);
   }

   public void ignoreEnchantments(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.h, bool);
   }

   public void ignoreInvulnerability(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.d, bool);
   }

   public void noAnger(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.q, bool);
   }

   public void damagesHelmet(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.a, bool);
   }

   public void ignoreShield(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.c, bool);
   }

   public void ignoreCooldown(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.e, bool);
   }

   public void noImpact(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.r, bool);
   }

   public void ignoreEffects(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.f, bool);
   }

   public void ignoreResistance(boolean bool) {
      this.damageAttributes.put(DamageTypeTags.g, bool);
   }

   public boolean a(TagKey<DamageType> tagkey) {
      return this.damageAttributes.containsKey(tagkey) ? this.damageAttributes.get(tagkey) : this.l().a(tagkey);
   }

   public boolean a(ResourceKey<DamageType> resourcekey) {
      return this.l().a(resourcekey);
   }

   public DamageCause knownCause() {
      return this.baseSource.knownCause();
   }

   public IChatBaseComponent a(EntityLiving entity) {
      return this.baseSource != null ? this.baseSource.a(entity) : null;
   }
}
