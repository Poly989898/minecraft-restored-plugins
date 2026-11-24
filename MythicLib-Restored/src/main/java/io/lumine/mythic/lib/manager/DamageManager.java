package io.lumine.mythic.lib.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.AttackUnregisteredEvent;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.AttackHandler;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.damage.MeleeAttackMetadata;
import io.lumine.mythic.lib.damage.ProjectileAttackMetadata;
import io.lumine.mythic.lib.entity.ProjectileMetadata;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DamageManager implements Listener, MMOManager {
   private final List<AttackHandler> handlers = new ArrayList<>();
   private final Map<UUID, AttackMetadata> attackMetadatas = new WeakHashMap<>();
   private AttributeModifier noKnockbackModifier;

   @Override
   public void initialize(boolean clearBefore) {
      this.noKnockbackModifier = VersionUtils.attrMod(new NamespacedKey(MythicLib.plugin, "no_knockback"), 100.0, Operation.ADD_NUMBER);
   }

   public void registerHandler(AttackHandler handler) {
      Validate.notNull(handler, "Damage handler cannot be null");
      this.handlers.add(handler);
   }

   @NotNull
   public List<AttackHandler> getHandlers() {
      return this.handlers;
   }

   public void registerAttack(@NotNull AttackMetadata metadata) {
      this.registerAttack(metadata, true, false);
   }

   public void registerAttack(@NotNull AttackMetadata metadata, boolean knockback) {
      this.registerAttack(metadata, knockback, false);
   }

   public void registerAttack(@NotNull AttackMetadata attack, boolean knockback, boolean ignoreImmunity) {
      Validate.notNull(attack.getTarget(), "Target cannot be null");
      this.markAsMetadata(attack);

      try {
         this.applyDamage(
            attack.getDamage().getDamage(),
            attack.getTarget(),
            attack.getAttacker() != null ? attack.getAttacker().getEntity() : null,
            knockback,
            ignoreImmunity
         );
      } catch (Exception exception) {
         MythicLib.plugin.getLogger().log(Level.SEVERE, "Caught an exception (1) while damaging entity '" + attack.getTarget().getUniqueId() + "':");
         exception.printStackTrace();
      } finally {
         this.unmarkAsMetadata(attack.getTarget());
      }
   }

   private void applyDamage(double damage, @NotNull LivingEntity target, @Nullable LivingEntity damager, boolean knockback, boolean ignoreImmunity) {
      if (!knockback) {
         AttributeInstance instance = target.getAttribute(Attribute.KNOCKBACK_RESISTANCE);

         try {
            instance.addModifier(this.noKnockbackModifier);
            this.applyDamage(damage, target, damager, true, ignoreImmunity);
         } catch (Exception anyError) {
            MythicLib.plugin.getLogger().log(Level.SEVERE, "Caught an exception (2) while damaging entity '" + target.getUniqueId() + "':");
            anyError.printStackTrace();
         } finally {
            instance.removeModifier(this.noKnockbackModifier);
         }
      } else if (ignoreImmunity) {
         int noDamageTicks = target.getNoDamageTicks();

         try {
            target.setNoDamageTicks(0);
            this.applyDamage(damage, target, damager, true, false);
         } catch (Exception anyError) {
            MythicLib.plugin.getLogger().log(Level.SEVERE, "Caught an exception (3) while damaging entity '" + target.getUniqueId() + "':");
            anyError.printStackTrace();
         } finally {
            target.setNoDamageTicks(noDamageTicks);
         }
      } else {
         Validate.isTrue(damage > 0.0, "Damage must be strictly positive");
         if (damager == null) {
            target.damage(damage);
         } else {
            target.damage(damage, damager);
         }
      }
   }

   @NotNull
   public AttackMetadata findAttack(EntityDamageEvent event) {
      Validate.isTrue(event.getEntity() instanceof LivingEntity, "Target entity is not living");
      LivingEntity entity = (LivingEntity)event.getEntity();
      AttackMetadata found = this.getRegisteredAttackMetadata(entity);
      if (found != null) {
         return found;
      }

      for (AttackHandler handler : this.handlers) {
         found = handler.getAttack(event);
         if (found != null) {
            this.markAsMetadata(found);
            return found;
         }
      }

      if (event instanceof EntityDamageByEntityEvent) {
         Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
         if (damager instanceof LivingEntity) {
            StatProvider attacker = StatProvider.get((LivingEntity)damager, EquipmentSlot.MAIN_HAND, true);
            AttackMetadata attackMeta = new MeleeAttackMetadata(
               new DamageMetadata(event.getDamage(), this.getVanillaDamageTypes((EntityDamageByEntityEvent)event, EquipmentSlot.MAIN_HAND)), entity, attacker
            );
            this.markAsMetadata(attackMeta);
            return attackMeta;
         }

         if (damager instanceof Projectile) {
            Projectile projectile = (Projectile)damager;
            ProjectileMetadata projectileData = ProjectileMetadata.get(projectile);
            if (projectileData != null) {
               AttackMetadata attackMeta = new ProjectileAttackMetadata(
                  new DamageMetadata(event.getDamage(), DamageType.WEAPON, DamageType.PHYSICAL, DamageType.PROJECTILE),
                  (LivingEntity)event.getEntity(),
                  projectileData.getShooter(),
                  projectile,
                  projectileData
               );
               this.markAsMetadata(attackMeta);
               return attackMeta;
            }

            ProjectileSource source = projectile.getShooter();
            if (source != null && !source.equals(event.getEntity()) && source instanceof LivingEntity) {
               StatProvider attacker = StatProvider.get((LivingEntity)source, EquipmentSlot.MAIN_HAND, true);
               AttackMetadata attackMeta = new ProjectileAttackMetadata(
                  new DamageMetadata(event.getDamage(), DamageType.WEAPON, DamageType.PHYSICAL, DamageType.PROJECTILE),
                  (LivingEntity)event.getEntity(),
                  attacker,
                  projectile
               );
               this.markAsMetadata(attackMeta);
               return attackMeta;
            }
         }
      }

      AttackMetadata vanillaAttack = new AttackMetadata(new DamageMetadata(event.getDamage(), this.getVanillaDamageTypes(event)), entity, null);
      this.markAsMetadata(vanillaAttack);
      return vanillaAttack;
   }

   @Nullable
   public AttackMetadata markAsMetadata(@NotNull AttackMetadata attackMeta) {
      AttackMetadata found = this.attackMetadatas.put(attackMeta.getTarget().getUniqueId(), attackMeta);
      if (found != null) {
         MythicLib.plugin.getLogger().log(Level.WARNING, "Please report this issue to the developer: persistent attack metadata was found.");
      }

      return found;
   }

   @Nullable
   public AttackMetadata unmarkAsMetadata(@NotNull Entity target) {
      return this.attackMetadatas.remove(target.getUniqueId());
   }

   @NotNull
   public DamageType[] getVanillaDamageTypes(EntityDamageEvent event) {
      return this.getVanillaDamageTypes(event.getCause());
   }

   @NotNull
   public DamageType[] getVanillaDamageTypes(DamageCause cause) {
      switch (cause) {
         case MAGIC:
         case DRAGON_BREATH:
            return new DamageType[]{DamageType.MAGIC};
         case POISON:
         case WITHER:
            return new DamageType[]{DamageType.MAGIC, DamageType.DOT};
         case FIRE_TICK:
         case MELTING:
            return new DamageType[]{DamageType.PHYSICAL, DamageType.DOT};
         case STARVATION:
         case DRYOUT:
         case FREEZE:
            return new DamageType[]{DamageType.DOT};
         case FIRE:
         case LAVA:
         case HOT_FLOOR:
         case SONIC_BOOM:
         case LIGHTNING:
         case FALL:
         case THORNS:
         case CONTACT:
         case ENTITY_EXPLOSION:
         case ENTITY_SWEEP_ATTACK:
         case FALLING_BLOCK:
         case FLY_INTO_WALL:
         case BLOCK_EXPLOSION:
         case ENTITY_ATTACK:
         case SUFFOCATION:
         case CRAMMING:
         case DROWNING:
            return new DamageType[]{DamageType.PHYSICAL};
         case PROJECTILE:
            return new DamageType[]{DamageType.PHYSICAL, DamageType.PROJECTILE};
         default:
            return new DamageType[0];
      }
   }

   @NotNull
   public DamageType[] getVanillaDamageTypes(EntityDamageByEntityEvent event, EquipmentSlot hand) {
      Validate.isTrue(event.getDamager() instanceof LivingEntity, "Not an entity attack");
      return this.getVanillaDamageTypes((LivingEntity)event.getDamager(), event.getCause(), hand);
   }

   @NotNull
   public DamageType[] getVanillaDamageTypes(@NotNull LivingEntity damager, @NotNull DamageCause cause, @NotNull EquipmentSlot hand) {
      if (cause != DamageCause.ENTITY_ATTACK && cause != DamageCause.ENTITY_SWEEP_ATTACK) {
         return new DamageType[]{DamageType.PHYSICAL};
      } else {
         ItemStack handItem = UtilityMethods.getHandItem(damager, hand);
         if (this.isAir(handItem)) {
            return new DamageType[]{DamageType.UNARMED, DamageType.PHYSICAL};
         } else {
            return this.isWeapon(handItem.getType()) ? new DamageType[]{DamageType.WEAPON, DamageType.PHYSICAL} : new DamageType[]{DamageType.PHYSICAL};
         }
      }
   }

   @Nullable
   public AttackMetadata getRegisteredAttackMetadata(Entity entity) {
      return this.attackMetadatas.get(entity.getUniqueId());
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void unregisterCustomAttacks(EntityDamageEvent event) {
      if (!UtilityMethods.isFake(event)) {
         if (event.getEntity() instanceof LivingEntity) {
            AttackMetadata attack = this.unmarkAsMetadata(event.getEntity());
            if (attack != null && !event.isCancelled() && event.getFinalDamage() > 0.0) {
               Bukkit.getPluginManager().callEvent(new AttackUnregisteredEvent(event, attack));
            }
         }
      }
   }

   private boolean isWeapon(@NotNull Material mat) {
      return mat.getMaxDurability() > 0;
   }

   private boolean isAir(@Nullable ItemStack item) {
      return item == null || item.getType() == Material.AIR;
   }

   @Deprecated
   public void damage(@NotNull AttackMetadata metadata, @NotNull LivingEntity target) {
      this.damage(metadata, target, true);
   }

   @Deprecated
   public void damage(@NotNull AttackMetadata metadata, @NotNull LivingEntity target, boolean knockback) {
      this.damage(metadata, target, knockback, false);
   }

   @Deprecated
   public void damage(@NotNull AttackMetadata metadata, @NotNull LivingEntity target, boolean knockback, boolean ignoreImmunity) {
      this.registerAttack(new AttackMetadata(metadata.getDamage(), target, metadata.getAttacker()), knockback, ignoreImmunity);
   }

   @Deprecated
   @NotNull
   public DamageMetadata findDamage(EntityDamageEvent event) {
      return this.findAttack(event).getDamage();
   }

   @Deprecated
   public void unmarkAsMetadata(@NotNull AttackMetadata attackMetadata) {
      this.unmarkAsMetadata(attackMetadata.getTarget());
   }
}
