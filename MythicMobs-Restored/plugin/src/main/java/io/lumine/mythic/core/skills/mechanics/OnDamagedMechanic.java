package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitTriggerMetadata;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.stats.StatModifierType;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

@MythicMechanic(
   author = "Ashijin",
   name = "ondamaged",
   description = "Applies an aura to the target that triggers a skill when they take damage",
   deprecated = true
)
public class OnDamagedMechanic extends Aura implements ITargetedEntitySkill {
   protected Optional<Skill> onDamagedSkill = Optional.empty();
   protected String onAttackSkillName;
   @MythicField(
      name = "cancelEvent",
      aliases = {"ce"},
      defValue = "false",
      version = "4.6",
      description = "Whether or not to cancel the event that triggered the aura"
   )
   protected boolean cancelDamage;
   protected boolean modDamage = false;
   protected boolean deflect = false;
   @MythicField(
      name = "modDamageType",
      aliases = {"dt"},
      defValue = "0",
      version = "4.6",
      description = ""
   )
   protected PlaceholderString modDamageType;
   @MythicField(
      name = "damageSub",
      aliases = {"s"},
      defValue = "0",
      version = "4.6",
      description = ""
   )
   protected PlaceholderDouble damageSub;
   @MythicField(
      name = "damageMultiplier",
      aliases = {"m"},
      defValue = "1",
      version = "4.6",
      description = ""
   )
   protected PlaceholderDouble damageMult;
   @MythicField(
      name = "damageModifiers",
      aliases = {"damagemods", "damageMod"},
      defValue = "",
      version = "4.6",
      description = ""
   )
   private PlaceholderString damageModifiers;
   protected String deflectConditionsString;
   @MythicField(
      name = "conditions",
      aliases = {"cond", "c"},
      defValue = "NONE",
      version = "5.1",
      description = "Conditions applied to the caster when triggered"
   )
   protected List<SkillCondition> deflectConditions = null;

   public OnDamagedMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.onAttackSkillName = mlc.getString(new String[]{"ondamagedskill", "ondamaged", "od", "onhitskill", "onhit", "oh"});
      this.modDamageType = mlc.getPlaceholderString(new String[]{"modDamageType", "damagetype"}, null);
      this.cancelDamage = mlc.getBoolean(new String[]{"cancelevent", "ce", "canceldamage"}, false);
      String damageSub = mlc.getString(new String[]{"damagesub", "sub", "s"}, null);
      String damageMult = mlc.getString(new String[]{"damagemultiplier", "multiplier", "m"}, null);
      this.deflect = mlc.getBoolean(new String[]{"deflectProjectiles", "deflect", "reflect"}, false);
      if (damageSub == null) {
         this.damageSub = PlaceholderDouble.of("0");
      } else {
         this.damageSub = PlaceholderDouble.of(damageSub);
      }

      if (damageMult == null) {
         this.damageMult = PlaceholderDouble.of("1");
      } else {
         this.damageMult = PlaceholderDouble.of(damageMult);
      }

      this.damageModifiers = mlc.getPlaceholderString(new String[]{"damagemodifiers", "damagemods", "damagemod"}, "");
      if (damageSub != null || damageMult != null || this.damageModifiers.isPresent()) {
         this.modDamage = true;
      }

      this.deflectConditionsString = mlc.getString(new String[]{"deflectconditions", "dconditions"}, null);
      this.getManager().queueSecondPass(() -> {
         if (this.onAttackSkillName != null) {
            this.onDamagedSkill = this.getManager().getSkill(file, this, this.onAttackSkillName);
         }

         if (this.deflectConditionsString != null) {
            this.deflectConditions = this.getManager().getConditions(this.deflectConditionsString);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new OnDamagedMechanic.Tracker(data, target);
      return SkillResult.SUCCESS;
   }

   protected void calculateDamage(SkillMetadata meta, AbstractEntity entity, EntityDamageEvent event) {
      Optional<Object> maybeData = entity.getMetadata("skill-damage");
      if (maybeData.isPresent()) {
         DamageMetadata damageData = (DamageMetadata)maybeData.get();
         this.modSkillDamage(meta, entity, event, damageData);
      } else {
         this.modVanillaDamage(meta, entity, event);
      }
   }

   private void modVanillaDamage(SkillMetadata meta, AbstractEntity entity, EntityDamageEvent event) {
      double damage = event.getFinalDamage();
      String damageType = event.getCause().toString();
      double mod;
      if (this.damageModifiers.isPresent()) {
         Map<String, Double> damageModifiers = Maps.newHashMap();

         for (String dm : this.damageModifiers.get(meta, entity).split(",")) {
            try {
               String[] split = dm.trim().split("\\s+");
               String type = split[0];
               damageModifiers.put(type, Double.valueOf(split[1]));
            } catch (ArrayIndexOutOfBoundsException e) {
               MythicLogger.errorMechanicConfig(this, this.getConfig(), "Invalid syntax for DamageModifier");
            } catch (Exception ex) {
               MythicLogger.errorMechanicConfig(this, this.getConfig(), "Invalid syntax for DamageModifier");
               ex.printStackTrace();
            }
         }

         mod = damageModifiers.getOrDefault(damageType.toUpperCase(), 1.0);
      } else {
         mod = 1.0;
      }

      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura: damageMod {0} has multiplier {1}", damageType, mod);
      event.setDamage((damage - this.damageSub.get(meta, entity)) * this.damageMult.get(meta, entity) * mod);
   }

   private void modSkillDamage(SkillMetadata meta, AbstractEntity entity, EntityDamageEvent event, DamageMetadata data) {
      double baseDamage = event.getDamage();
      String baseDamageType = event.getCause().toString();
      double mod;
      if (this.damageModifiers.isPresent()) {
         Map<String, Double> damageModifiers = Maps.newHashMap();

         for (String dm : this.damageModifiers.get(meta, entity).split(",")) {
            try {
               OnDamagedMechanic.DamageMod modifier = new OnDamagedMechanic.DamageMod(dm);
               damageModifiers.put(modifier.getType(), modifier.getValue());
               modifier.applySkill(data);
            } catch (ArrayIndexOutOfBoundsException e) {
               MythicLogger.errorMechanicConfig(this, this.getConfig(), "Invalid syntax for DamageModifier");
            } catch (Exception ex) {
               MythicLogger.errorMechanicConfig(this, this.getConfig(), "Invalid syntax for DamageModifier");
               ex.printStackTrace();
            }
         }

         mod = damageModifiers.getOrDefault(baseDamageType.toUpperCase(), 1.0);
      } else {
         mod = 1.0;
      }

      double damageSubtract = this.damageSub.get(meta, entity);
      double damageMultiplier = this.damageMult.get(meta, entity);
      double damageBaseAmount = (baseDamage - damageSubtract) * mod;
      double damageMultiplied = damageBaseAmount * damageMultiplier;
      data.setAmount(damageBaseAmount);
      data.setMultiplier(data.getMultiplier() * damageMultiplier);
      event.setDamage(damageMultiplied);
      MythicLogger.debug(
         MythicLogger.DebugLevel.MECHANIC,
         "OnDamaged aura: damageMod modified damage to {0} with base {1} multiplier {2} mod {3} sub {4}",
         damageMultiplied,
         damageBaseAmount,
         damageMultiplier,
         mod,
         damageSubtract
      );
   }

   @Getter
   @Setter
   @ToString
   private static class DamageMod {
      private String type;
      private double value;
      private StatModifierType modifierType = StatModifierType.ADDITIVE_MULTIPLIER;

      public DamageMod(String mod) {
         String[] parts = mod.trim().split("\\s+");
         if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid format for mod string. Expected format: 'type value [modifierType]'");
         } else {
            this.type = parts[0];

            try {
               this.value = Double.parseDouble(parts[1]);
            } catch (NumberFormatException e) {
               throw new IllegalArgumentException("Value is not a valid number: " + parts[1]);
            }

            if (parts.length > 2) {
               try {
                  this.modifierType = StatModifierType.valueOf(parts[2].toUpperCase());
               } catch (IllegalArgumentException ex) {
                  throw new IllegalArgumentException("Invalid modifierType: " + parts[2]);
               }
            }
         }
      }

      public void applySkill(DamageMetadata damageMetadata) {
         switch (this.modifierType) {
            case ADDITIVE:
               damageMetadata.getBonusDamage().compute(this.type, (k, v) -> {
                  if (v == null) {
                     return this.value;
                  } else {
                     v = v - this.value;
                     return v < 0.0 ? 0.0 : v;
                  }
               });
               break;
            case ADDITIVE_MULTIPLIER:
               damageMetadata.getBonusDamageModifiers().compute(this.type, (k, v) -> v == null ? this.value : v + this.value);
               break;
            case COMPOUND_MULTIPLIER:
               damageMetadata.getBonusDamageModifiers().compute(this.type, (k, v) -> v == null ? this.value : v * this.value);
               break;
            case SETTER:
               damageMetadata.getBonusDamageModifiers().compute(this.type, (k, v) -> this.value);
         }
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof OnDamagedMechanic.DamageMod other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (Double.compare(this.getValue(), other.getValue()) != 0) {
            return false;
         } else {
            Object this$type = this.getType();
            Object other$type = other.getType();
            if (this$type == null ? other$type == null : this$type.equals(other$type)) {
               Object this$modifierType = this.getModifierType();
               Object other$modifierType = other.getModifierType();
               return this$modifierType == null ? other$modifierType == null : this$modifierType.equals(other$modifierType);
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof OnDamagedMechanic.DamageMod;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         long $value = Double.doubleToLongBits(this.getValue());
         result = result * 59 + (int)($value >>> 32 ^ $value);
         Object $type = this.getType();
         result = result * 59 + ($type == null ? 43 : $type.hashCode());
         Object $modifierType = this.getModifierType();
         return result * 59 + ($modifierType == null ? 43 : $modifierType.hashCode());
      }
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         this.start();
      }

      private boolean matchesDamageType(EntityDamageEvent event) {
         if (OnDamagedMechanic.this.modDamageType == null) {
            return true;
         } else {
            AbstractEntity target = this.entity.orElse(null);
            if (target == null) {
               return true;
            } else {
               String required = OnDamagedMechanic.this.modDamageType.get(this.skillMetadata, target);
               return required != null && !required.isEmpty() ? event.getCause().name().equalsIgnoreCase(required.trim()) : true;
            }
         }
      }

      @Override
      public void auraStart() {
         if (OnDamagedMechanic.this.deflect) {
            this.registerAuraComponent(
               Events.subscribe(ProjectileHitEvent.class).filter(event -> !event.isCancelled()).filter(event -> event.getHitEntity() != null).filter(event -> {
                  if (event.getHitEntity().getUniqueId().equals(this.entity.get().getUniqueId())) {
                     return true;
                  } else {
                     Optional<AbstractModelEngineSupport> maybeSupport = OnDamagedMechanic.this.getPlugin().getCompatibility().getModelEngine();
                     if (maybeSupport.isPresent()) {
                        AbstractModelEngineSupport megSupport = maybeSupport.get();
                        return megSupport.isBoundToSubHitbox(event.getEntity().getUniqueId(), this.entity.get().getUniqueId());
                     } else {
                        return false;
                     }
                  }
               }).handler(event -> {
                  SkillMetadata meta = this.skillMetadata.deepClone();
                  MythicLogger.debug(
                     MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura (projectileHit) listener called for {0}", this.entity.get().getUniqueId()
                  );
                  if (OnDamagedMechanic.this.conditions != null) {
                     for (SkillCondition condition : OnDamagedMechanic.this.deflectConditions) {
                        if (!condition.evaluateCaster(meta)) {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Condition {0} failed", condition);
                           return;
                        }
                     }
                  }

                  event.setCancelled(true);
                  Projectile proj = event.getEntity();
                  Vector velocity = proj.getVelocity();
                  velocity.setX(velocity.getX() * -1.0);
                  velocity.setZ(velocity.getZ() * -1.0);
                  velocity.setY(velocity.getY() * -1.0);
                  Location location = proj.getLocation();
                  location.add(velocity.getX(), velocity.getY(), velocity.getZ());
                  if (ServerVersion.isFolia()) {
                     proj.teleportAsync(location);
                  } else {
                     proj.teleport(location);
                  }

                  proj.setVelocity(velocity);
                  Schedulers.sync().runLater(() -> proj.setVelocity(velocity), 1L);
               })
            );
            this.registerAuraComponent(
               Events.subscribe(MythicProjectileHitEvent.class)
                  .filter(event -> event.getEntity() != null)
                  .filter(event -> {
                     if (event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId())) {
                        return true;
                     } else {
                        Optional<AbstractModelEngineSupport> maybeSupport = OnDamagedMechanic.this.getPlugin().getCompatibility().getModelEngine();
                        if (maybeSupport.isPresent()) {
                           AbstractModelEngineSupport megSupport = maybeSupport.get();
                           return megSupport.isBoundToSubHitbox(event.getEntity().getUniqueId(), this.entity.get().getUniqueId());
                        } else {
                           return false;
                        }
                     }
                  })
                  .handler(
                     event -> {
                        SkillMetadata meta = this.skillMetadata.deepClone();
                        MythicLogger.debug(
                           MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura (mythicProjectileHit) listener called for {0}", this.entity.get().getUniqueId()
                        );
                        if (OnDamagedMechanic.this.conditions != null) {
                           for (SkillCondition condition : OnDamagedMechanic.this.deflectConditions) {
                              if (!condition.evaluateCaster(meta)) {
                                 MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Condition {0} failed", condition);
                                 return;
                              }
                           }
                        }

                        event.setCancelled(true);
                        io.lumine.mythic.core.skills.projectiles.Projectile.ProjectileTracker projectile = event.getProjectile();
                        AbstractVector velocity = projectile.getCurrentVelocity();
                        velocity.setX(velocity.getX() * -1.25);
                        velocity.setZ(velocity.getZ() * -1.25);
                        velocity.setY(velocity.getY() * -1.25);
                        projectile.setCurrentVelocity(velocity);
                     }
                  )
            );
         }

         this.registerAuraComponent(
            Events.subscribe(EntityDamageByEntityEvent.class, EventPriority.HIGH)
               .filter(event -> !event.isCancelled())
               .filter(event -> event.getFinalDamage() != 0.0)
               .filter(this::matchesDamageType)
               .filter(event -> {
                  if (event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId())) {
                     return true;
                  } else {
                     Optional<AbstractModelEngineSupport> maybeSupport = OnDamagedMechanic.this.getPlugin().getCompatibility().getModelEngine();
                     if (maybeSupport.isPresent()) {
                        AbstractModelEngineSupport megSupport = maybeSupport.get();
                        return megSupport.isBoundToSubHitbox(event.getEntity().getUniqueId(), this.entity.get().getUniqueId());
                     } else {
                        return false;
                     }
                  }
               })
               .handler(event -> {
                  SkillMetadata meta = this.skillMetadata.deepClone();
                  BukkitTriggerMetadata.apply(meta, event);
                  AbstractEntity damager;
                  if (event.getDamager() instanceof LivingEntity) {
                     damager = BukkitAdapter.adapt(event.getDamager());
                  } else if (event.getDamager() instanceof Projectile projectile) {
                     if (projectile.getShooter() instanceof LivingEntity livingEntity) {
                        damager = BukkitAdapter.adapt(livingEntity);
                     } else {
                        damager = null;
                     }
                  } else {
                     damager = null;
                  }

                  if (damager != null) {
                     meta.setTrigger(damager);
                  }

                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura (byEntity) listener called for {0}", this.entity.get().getUniqueId());
                  if (this.executeTargetedAuraSkill(OnDamagedMechanic.this.onDamagedSkill, meta, BukkitAdapter.adapt(event.getEntity()))) {
                     this.consumeCharge();
                     if (OnDamagedMechanic.this.cancelDamage) {
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura cancelling damage");
                        event.setCancelled(true);
                     } else if (OnDamagedMechanic.this.modDamage) {
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura modifying damage");
                        OnDamagedMechanic.this.calculateDamage(meta, this.entity.get(), event);
                     } else {
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura executed");
                     }
                  }
               })
         );
         this.registerAuraComponent(
            Events.subscribe(EntityDamageEvent.class)
               .filter(event -> !event.isCancelled())
               .filter(event -> event.getDamage() != 0.0)
               .filter(this::matchesDamageType)
               .filter(event -> {
                  if (event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId())) {
                     return true;
                  } else {
                     Optional<AbstractModelEngineSupport> maybeSupport = OnDamagedMechanic.this.getPlugin().getCompatibility().getModelEngine();
                     if (maybeSupport.isPresent()) {
                        AbstractModelEngineSupport megSupport = maybeSupport.get();
                        return megSupport.isBoundToSubHitbox(event.getEntity().getUniqueId(), this.entity.get().getUniqueId());
                     } else {
                        return false;
                     }
                  }
               })
               .handler(event -> {
                  SkillMetadata meta = this.skillMetadata.deepClone();
                  meta.setEntityTarget(BukkitAdapter.adapt(event.getEntity()));
                  BukkitTriggerMetadata.apply(meta, event);
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura (nonEntity) listener called for {0}", this.entity.get().getUniqueId());
                  if (OnDamagedMechanic.this.conditions != null) {
                     for (SkillCondition condition : OnDamagedMechanic.this.conditions) {
                        if (!condition.evaluateCaster(meta)) {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Condition {0} failed", condition);
                           return;
                        }
                     }
                  }

                  if (this.executeAuraSkill(OnDamagedMechanic.this.onDamagedSkill, meta)) {
                     this.consumeCharge();
                     if (OnDamagedMechanic.this.cancelDamage) {
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura cancelling damage");
                        event.setCancelled(true);
                     } else if (OnDamagedMechanic.this.modDamage) {
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura modifying damage");
                        OnDamagedMechanic.this.calculateDamage(meta, this.entity.get(), event);
                     } else {
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura executed");
                     }
                  }
               })
         );
         this.registerAuraComponent(
            Events.subscribe(EntityDamageByBlockEvent.class, EventPriority.HIGH)
               .filter(event -> !event.isCancelled())
               .filter(event -> event.getDamage() != 0.0)
               .filter(this::matchesDamageType)
               .filter(event -> {
                  if (event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId())) {
                     return true;
                  } else {
                     Optional<AbstractModelEngineSupport> maybeSupport = OnDamagedMechanic.this.getPlugin().getCompatibility().getModelEngine();
                     if (maybeSupport.isPresent()) {
                        AbstractModelEngineSupport megSupport = maybeSupport.get();
                        return megSupport.isBoundToSubHitbox(event.getEntity().getUniqueId(), this.entity.get().getUniqueId());
                     } else {
                        return false;
                     }
                  }
               })
               .handler(event -> {
                  SkillMetadata meta = this.skillMetadata.deepClone();
                  meta.setEntityTarget(BukkitAdapter.adapt(event.getEntity()));
                  BukkitTriggerMetadata.apply(meta, event);
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura (nonEntity) listener called for {0}", this.entity.get().getUniqueId());
                  if (OnDamagedMechanic.this.conditions != null) {
                     for (SkillCondition condition : OnDamagedMechanic.this.conditions) {
                        if (!condition.evaluateCaster(meta)) {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Condition {0} failed", condition);
                           return;
                        }
                     }
                  }

                  if (this.executeAuraSkill(OnDamagedMechanic.this.onDamagedSkill, meta)) {
                     this.consumeCharge();
                     if (OnDamagedMechanic.this.cancelDamage) {
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura cancelling damage");
                        event.setCancelled(true);
                     } else if (OnDamagedMechanic.this.modDamage) {
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura modifying damage");
                        OnDamagedMechanic.this.calculateDamage(meta, this.entity.get(), event);
                     } else {
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura executed");
                     }
                  }
               })
         );
         this.executeAuraSkill(OnDamagedMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}
