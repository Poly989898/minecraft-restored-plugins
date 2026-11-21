package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
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
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.skills.stats.StatModifierType;
import io.lumine.mythic.core.skills.triggers.meta.DamagedMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

public class OnDamagedAuraComponent implements AuraComponent {
   private final Aura.AuraTracker tracker;
   private final AuraMechanic mechanic;
   private final Optional<Skill> onDamagedSkill;
   private final boolean cancelDamage;
   private final boolean modDamage;
   private final boolean deflect;
   private final PlaceholderString modDamageType;
   private final PlaceholderDouble damageSub;
   private final PlaceholderDouble damageMult;
   private final PlaceholderString damageModifiers;
   private final List<SkillCondition> deflectConditions;
   private final List<SkillCondition> triggerConditions;
   private final List<SkillCondition> conditions;

   public OnDamagedAuraComponent(
      AuraMechanic mechanic,
      Aura.AuraTracker tracker,
      Optional<Skill> onDamagedSkill,
      boolean cancelDamage,
      boolean modDamage,
      boolean deflect,
      PlaceholderString modDamageType,
      PlaceholderDouble damageSub,
      PlaceholderDouble damageMult,
      PlaceholderString damageModifiers,
      List<SkillCondition> deflectConditions,
      List<SkillCondition> triggerConditions,
      List<SkillCondition> conditions
   ) {
      this.mechanic = mechanic;
      this.tracker = tracker;
      this.onDamagedSkill = onDamagedSkill;
      this.cancelDamage = cancelDamage;
      this.modDamage = modDamage;
      this.deflect = deflect;
      this.modDamageType = modDamageType;
      this.damageSub = damageSub;
      this.damageMult = damageMult;
      this.damageModifiers = damageModifiers;
      this.deflectConditions = deflectConditions;
      this.triggerConditions = triggerConditions;
      this.conditions = conditions;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         AbstractEntity entity = maybeEntity.get();
         if (this.deflect) {
            this.tracker
               .registerAuraComponent(
                  Events.subscribe(ProjectileHitEvent.class)
                     .filter(event -> !event.isCancelled())
                     .filter(event -> event.getHitEntity() != null)
                     .filter(event -> this.matchesEntity(entity, event.getHitEntity().getUniqueId()))
                     .handler(event -> {
                        SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura (projectileHit) listener called for {0}", entity.getUniqueId());
                        if (this.deflectConditions != null) {
                           for (SkillCondition condition : this.deflectConditions) {
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
            this.tracker
               .registerAuraComponent(
                  Events.subscribe(MythicProjectileHitEvent.class)
                     .filter(event -> event.getEntity() != null)
                     .filter(event -> this.matchesEntity(entity, event.getEntity().getUniqueId()))
                     .handler(event -> {
                        SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                        MythicLogger.debug(
                           MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura (mythicProjectileHit) listener called for {0}", entity.getUniqueId()
                        );
                        if (this.deflectConditions != null) {
                           for (SkillCondition condition : this.deflectConditions) {
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
                     })
               );
         }

         this.tracker
            .registerAuraComponent(
               Events.subscribe(EntityDamageByEntityEvent.class, EventPriority.HIGH)
                  .filter(event -> !event.isCancelled())
                  .filter(event -> event.getFinalDamage() != 0.0)
                  .filter(this::matchesDamageType)
                  .filter(event -> this.matchesEntity(entity, event.getEntity().getUniqueId()))
                  .handler(event -> {
                     SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                     AbstractEntity damaged = BukkitAdapter.adapt(event.getEntity());
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

                     if (meta instanceof SkillMetadataImpl impl) {
                        DamageMetadata damageData = (DamageMetadata)damaged.getMetadata("skill-damage").orElse(null);
                        impl.setTriggerMetadata(new EntityAttackMetadata(event, damageData, damager, damaged));
                     }

                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura (byEntity) listener called for {0}", entity.getUniqueId());
                     boolean cancel = this.cancelDamage && this.passesConditions(meta);
                     if (this.tracker.executeTargetedAuraSkill(this.onDamagedSkill, meta, damaged)) {
                        this.tracker.consumeCharge();
                        if (cancel) {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura cancelling damage");
                           event.setCancelled(true);
                        } else if (this.modDamage) {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura modifying damage");
                           this.calculateDamage(meta, entity, event);
                        } else {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura executed");
                        }
                     }
                  })
            );
         this.tracker
            .registerAuraComponent(
               Events.subscribe(EntityDamageEvent.class)
                  .filter(event -> !event.isCancelled())
                  .filter(event -> event.getDamage() != 0.0)
                  .filter(this::matchesDamageType)
                  .filter(event -> this.matchesEntity(entity, event.getEntity().getUniqueId()))
                  .handler(event -> {
                     SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                     AbstractEntity damaged = BukkitAdapter.adapt(event.getEntity());
                     meta.setEntityTarget(damaged);
                     BukkitTriggerMetadata.apply(meta, event);
                     if (meta instanceof SkillMetadataImpl impl) {
                        DamageMetadata damageData = (DamageMetadata)damaged.getMetadata("skill-damage").orElse(null);
                        impl.setTriggerMetadata(damageData == null ? new DamagedMetadata(event, damaged) : new DamagedMetadata(event, damageData, damaged));
                     }

                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura (nonEntity) listener called for {0}", entity.getUniqueId());
                     if (this.triggerConditions != null) {
                        for (SkillCondition condition : this.triggerConditions) {
                           if (!condition.evaluateCaster(meta)) {
                              MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Condition {0} failed", condition);
                              return;
                           }
                        }
                     }

                     boolean cancel = this.cancelDamage && this.passesConditions(meta);
                     if (this.tracker.executeAuraSkill(this.onDamagedSkill, meta)) {
                        this.tracker.consumeCharge();
                        if (cancel) {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura cancelling damage");
                           event.setCancelled(true);
                        } else if (this.modDamage) {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura modifying damage");
                           this.calculateDamage(meta, entity, event);
                        } else {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura executed");
                        }
                     }
                  })
            );
         this.tracker
            .registerAuraComponent(
               Events.subscribe(EntityDamageByBlockEvent.class, EventPriority.HIGH)
                  .filter(event -> !event.isCancelled())
                  .filter(event -> event.getDamage() != 0.0)
                  .filter(this::matchesDamageType)
                  .filter(event -> this.matchesEntity(entity, event.getEntity().getUniqueId()))
                  .handler(event -> {
                     SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                     AbstractEntity damaged = BukkitAdapter.adapt(event.getEntity());
                     meta.setEntityTarget(damaged);
                     BukkitTriggerMetadata.apply(meta, event);
                     if (meta instanceof SkillMetadataImpl impl) {
                        DamageMetadata damageData = (DamageMetadata)damaged.getMetadata("skill-damage").orElse(null);
                        impl.setTriggerMetadata(damageData == null ? new DamagedMetadata(event, damaged) : new DamagedMetadata(event, damageData, damaged));
                     }

                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura (nonEntity) listener called for {0}", entity.getUniqueId());
                     if (this.triggerConditions != null) {
                        for (SkillCondition condition : this.triggerConditions) {
                           if (!condition.evaluateCaster(meta)) {
                              MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Condition {0} failed", condition);
                              return;
                           }
                        }
                     }

                     boolean cancel = this.cancelDamage && this.passesConditions(meta);
                     if (this.tracker.executeAuraSkill(this.onDamagedSkill, meta)) {
                        this.tracker.consumeCharge();
                        if (cancel) {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura cancelling damage");
                           event.setCancelled(true);
                        } else if (this.modDamage) {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura modifying damage");
                           this.calculateDamage(meta, entity, event);
                        } else {
                           MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura executed");
                        }
                     }
                  })
            );
      }
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
      double mod = 1.0;
      if (this.damageModifiers.isPresent()) {
         String targetType = damageType.toUpperCase();

         for (String dm : this.damageModifiers.get(meta, entity).split(",")) {
            try {
               String[] split = dm.trim().split("\\s+");
               String type = split[0];
               double value = Double.parseDouble(split[1]);
               if (type.equals(targetType)) {
                  mod = value;
               }
            } catch (ArrayIndexOutOfBoundsException e) {
               MythicLogger.errorMechanicConfig(this.mechanic, this.mechanic.getConfig(), "Invalid syntax for DamageModifier");
            } catch (Exception ex) {
               MythicLogger.errorMechanicConfig(this.mechanic, this.mechanic.getConfig(), "Invalid syntax for DamageModifier");
               ex.printStackTrace();
            }
         }
      }

      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "OnDamaged aura: damageMod {0} has multiplier {1}", damageType, mod);
      event.setDamage((damage - this.damageSub.get(meta, entity)) * this.damageMult.get(meta, entity) * mod);
   }

   private void modSkillDamage(SkillMetadata meta, AbstractEntity entity, EntityDamageEvent event, DamageMetadata data) {
      double baseDamage = event.getDamage();
      String baseDamageType = event.getCause().toString();
      double mod = 1.0;
      if (this.damageModifiers.isPresent()) {
         String targetType = baseDamageType.toUpperCase();

         for (String dm : this.damageModifiers.get(meta, entity).split(",")) {
            try {
               OnDamagedAuraComponent.DamageMod modifier = new OnDamagedAuraComponent.DamageMod(dm);
               if (modifier.getType().equals(targetType)) {
                  mod = modifier.getValue();
               }

               modifier.applySkill(data);
            } catch (ArrayIndexOutOfBoundsException e) {
               MythicLogger.errorMechanicConfig(this.mechanic, this.mechanic.getConfig(), "Invalid syntax for DamageModifier");
            } catch (Exception ex) {
               MythicLogger.errorMechanicConfig(this.mechanic, this.mechanic.getConfig(), "Invalid syntax for DamageModifier");
               ex.printStackTrace();
            }
         }
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

   private boolean passesConditions(SkillMetadata meta) {
      if (this.conditions == null) {
         return true;
      } else {
         for (SkillCondition condition : this.conditions) {
            if (!condition.evaluateCaster(meta)) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Condition {0} failed", condition);
               return false;
            }
         }

         return true;
      }
   }

   private boolean matchesDamageType(EntityDamageEvent event) {
      if (this.modDamageType == null) {
         return true;
      } else {
         AbstractEntity target = this.tracker.getEntity().orElse(null);
         if (target == null) {
            return true;
         } else {
            String required = this.modDamageType.get(this.tracker.getSkillMetadata(), target);
            return required != null && !required.isEmpty() ? event.getCause().name().equalsIgnoreCase(required.trim()) : true;
         }
      }
   }

   private boolean matchesEntity(AbstractEntity entity, UUID candidateId) {
      if (candidateId.equals(entity.getUniqueId())) {
         return true;
      } else {
         Optional<AbstractModelEngineSupport> maybeSupport = this.mechanic.getPlugin().getCompatibility().getModelEngine();
         if (maybeSupport.isPresent()) {
            AbstractModelEngineSupport megSupport = maybeSupport.get();
            return megSupport.isBoundToSubHitbox(candidateId, entity.getUniqueId());
         } else {
            return false;
         }
      }
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
         } else if (!(o instanceof OnDamagedAuraComponent.DamageMod other)) {
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
         return other instanceof OnDamagedAuraComponent.DamageMod;
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

   @MythicAuraComponent(
      name = "ondamaged",
      aliases = {"ondamage"},
      description = "Triggers a skill when the aura holder takes damage"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final String onDamagedSkillName;
      private Optional<Skill> onDamagedSkill = Optional.empty();
      private final boolean cancelDamage;
      private final boolean modDamage;
      private final boolean deflect;
      private final PlaceholderString modDamageType;
      private final PlaceholderDouble damageSub;
      private final PlaceholderDouble damageMult;
      private final PlaceholderString damageModifiers;
      private final String deflectConditionsString;
      private List<SkillCondition> deflectConditions;
      private final List<SkillCondition> triggerConditions;
      private final String conditionsString;
      private List<SkillCondition> conditions;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.onDamagedSkillName = mlc.getString(new String[]{"ondamagedskill", "ondamaged", "od", "onhitskill", "onhit", "oh"});
         this.modDamageType = mlc.getPlaceholderString(new String[]{"modDamageType", "damagetype"}, null);
         this.cancelDamage = mlc.getBoolean(new String[]{"cancelevent", "ce", "canceldamage"}, false);
         String damageSubRaw = mlc.getString(new String[]{"damagesub", "sub", "s"}, null);
         String damageMultRaw = mlc.getString(new String[]{"damagemultiplier", "multiplier", "m"}, null);
         this.deflect = mlc.getBoolean(new String[]{"deflectProjectiles", "deflect", "reflect"}, false);
         this.damageSub = damageSubRaw == null ? PlaceholderDouble.of("0") : PlaceholderDouble.of(damageSubRaw);
         this.damageMult = damageMultRaw == null ? PlaceholderDouble.of("1") : PlaceholderDouble.of(damageMultRaw);
         this.damageModifiers = mlc.getPlaceholderString(new String[]{"damagemodifiers", "damagemods", "damagemod"}, "");
         this.modDamage = damageSubRaw != null || damageMultRaw != null || this.damageModifiers.isPresent();
         this.deflectConditionsString = mlc.getString(new String[]{"deflectconditions", "dconditions"}, null);
         this.triggerConditions = mechanic.getConditions();
         this.conditionsString = mlc.getString(new String[]{"conditions", "cond", "c"}, null);
         mechanic.getManager().queueSecondPass(() -> {
            if (this.onDamagedSkillName != null) {
               this.onDamagedSkill = mechanic.getManager().getSkill(mechanic.getFile(), mechanic, this.onDamagedSkillName);
            }

            if (this.deflectConditionsString != null) {
               this.deflectConditions = mechanic.getManager().getConditions(this.deflectConditionsString);
            }

            if (this.conditionsString != null) {
               this.conditions = mechanic.getManager().getConditions(this.conditionsString);
            }
         });
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new OnDamagedAuraComponent(
            this.mechanic,
            tracker,
            this.onDamagedSkill,
            this.cancelDamage,
            this.modDamage,
            this.deflect,
            this.modDamageType,
            this.damageSub,
            this.damageMult,
            this.damageModifiers,
            this.deflectConditions,
            this.triggerConditions,
            this.conditions
         );
      }
   }
}
