package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class ProjectileReboundAuraComponent implements AuraComponent {
   private static final String REBOUND_COUNT_KEY = "projectile-rebound-count";
   private final AuraMechanic mechanic;
   private final Aura.AuraTracker tracker;
   private final Optional<Skill> onReboundSkill;
   private final ProjectileReboundAuraComponent.ReturnMode returnMode;
   private final boolean reparseFaction;
   private final boolean cancelIncoming;
   private final boolean reboundVanilla;
   private final boolean reboundMythic;
   private final PlaceholderDouble speedFactor;
   private final PlaceholderDouble powerFactor;
   private final PlaceholderDouble reboundDistance;
   private final int maxRebounds;
   private final List<SkillCondition> reboundConditions;

   public ProjectileReboundAuraComponent(
      AuraMechanic mechanic,
      Aura.AuraTracker tracker,
      Optional<Skill> onReboundSkill,
      ProjectileReboundAuraComponent.ReturnMode returnMode,
      boolean reparseFaction,
      boolean cancelIncoming,
      boolean reboundVanilla,
      boolean reboundMythic,
      PlaceholderDouble speedFactor,
      PlaceholderDouble powerFactor,
      PlaceholderDouble reboundDistance,
      int maxRebounds,
      List<SkillCondition> reboundConditions
   ) {
      this.mechanic = mechanic;
      this.tracker = tracker;
      this.onReboundSkill = onReboundSkill;
      this.returnMode = returnMode;
      this.reparseFaction = reparseFaction;
      this.cancelIncoming = cancelIncoming;
      this.reboundVanilla = reboundVanilla;
      this.reboundMythic = reboundMythic;
      this.speedFactor = speedFactor;
      this.powerFactor = powerFactor;
      this.reboundDistance = reboundDistance;
      this.maxRebounds = maxRebounds;
      this.reboundConditions = reboundConditions;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         AbstractEntity defender = maybeEntity.get();
         if (this.reboundVanilla) {
            this.tracker
               .registerAuraComponent(
                  Events.subscribe(EntityDamageByEntityEvent.class, EventPriority.HIGH)
                     .filter(event -> !event.isCancelled())
                     .filter(event -> event.getDamager() instanceof Projectile)
                     .filter(event -> this.matchesEntity(defender, event.getEntity().getUniqueId()))
                     .handler(event -> this.handleVanillaRebound((Projectile)event.getDamager(), defender, event::setCancelled))
               );
            this.tracker
               .registerAuraComponent(
                  Events.subscribe(ProjectileHitEvent.class)
                     .filter(event -> !event.isCancelled())
                     .filter(event -> event.getHitEntity() != null)
                     .filter(event -> this.matchesEntity(defender, event.getHitEntity().getUniqueId()))
                     .handler(event -> this.handleVanillaRebound(event.getEntity(), defender, event::setCancelled))
               );
         }

         if (this.reboundMythic) {
            this.tracker
               .registerAuraComponent(
                  Events.subscribe(MythicProjectileHitEvent.class)
                     .filter(event -> event.getEntity() != null)
                     .filter(event -> this.matchesEntity(defender, event.getEntity().getUniqueId()))
                     .handler(event -> this.handleMythicRebound(event, defender))
               );
         }
      }
   }

   private void handleVanillaRebound(Projectile projectile, AbstractEntity defender, Consumer<Boolean> cancelIncomingAction) {
      if (projectile != null && projectile.isValid()) {
         AbstractEntity shooter = this.resolveShooter(projectile.getShooter());
         AbstractLocation origin = BukkitAdapter.adapt(projectile.getLocation());
         SkillMetadata meta = this.buildReboundMeta(origin, shooter);
         if (this.passesReboundConditions(meta)) {
            int rebounds = this.getVanillaReboundCount(projectile);
            if (this.maxRebounds <= 0 || rebounds < this.maxRebounds) {
               if (!this.isVanillaReboundOnCooldown(projectile)) {
                  double speedScale = Math.max(0.0, this.speedFactor.get(meta, defender));
                  double powerScale = Math.max(0.0, this.powerFactor.get(meta, defender));
                  AbstractVector beforeVelocity = BukkitAdapter.adapt(projectile.getVelocity()).clone();
                  AbstractVector afterVelocity = this.computeReboundVelocity(beforeVelocity, origin, shooter, speedScale);
                  if (this.cancelIncoming) {
                     cancelIncomingAction.accept(true);
                  }

                  if (this.reparseFaction && defender.getBukkitEntity() instanceof ProjectileSource source) {
                     projectile.setShooter(source);
                  }

                  this.applyVanillaPower(projectile, powerScale);
                  this.applyVanillaVelocity(projectile, afterVelocity, this.reboundDistance.get(meta, defender));
                  int reboundCount = rebounds + 1;
                  this.setVanillaReboundCount(projectile, reboundCount);
                  this.markVanillaRebound(projectile);
                  this.emitReboundSkill(
                     meta, shooter, origin, false, beforeVelocity, afterVelocity, speedScale, powerScale, reboundCount, null, BukkitAdapter.adapt(projectile)
                  );
                  this.tracker.consumeCharge();
                  MythicLogger.debug(
                     MythicLogger.DebugLevel.MECHANIC,
                     "ProjectileRebound aura (vanilla) rebounded projectile for {0}, count={1}",
                     defender.getUniqueId(),
                     reboundCount
                  );
               }
            }
         }
      }
   }

   private void handleMythicRebound(MythicProjectileHitEvent event, AbstractEntity defender) {
      io.lumine.mythic.core.skills.projectiles.Projectile.ProjectileTracker projectile = event.getProjectile();
      if (projectile != null && !projectile.getCancelled()) {
         SkillMetadata projectileMeta = projectile.getData();
         if (projectileMeta != null && projectileMeta.getCaster() != null) {
            AbstractEntity shooter = projectileMeta.getCaster().getEntity();
            AbstractLocation origin = projectile.getCurrentLocation().clone();
            SkillMetadata meta = this.buildReboundMeta(origin, shooter);
            if (this.passesReboundConditions(meta)) {
               int rebounds = this.getMythicReboundCount(projectile);
               if (this.maxRebounds <= 0 || rebounds < this.maxRebounds) {
                  double speedScale = Math.max(0.0, this.speedFactor.get(meta, defender));
                  double powerScale = Math.max(0.0, this.powerFactor.get(meta, defender));
                  if (projectile.getCurrentVelocity() != null) {
                     AbstractVector beforeVelocity = projectile.getCurrentVelocity().clone();
                     AbstractVector afterVelocity = this.computeReboundVelocity(beforeVelocity, origin, shooter, speedScale);
                     if (this.cancelIncoming) {
                        event.setCancelled(true);
                     }

                     if (this.reparseFaction) {
                        SkillCaster reboundCaster = this.mechanic.getManager().getCaster(defender);
                        if (reboundCaster != null) {
                           projectileMeta.setCaster(reboundCaster);
                        }
                     }

                     if (powerScale != 1.0) {
                        projectile.multiplyPower((float)powerScale);
                     }

                     projectile.setCurrentVelocity(afterVelocity);
                     this.applyMythicReboundOffset(projectile, afterVelocity, this.reboundDistance.get(meta, defender));
                     int reboundCount = rebounds + 1;
                     this.setMythicReboundCount(projectile, reboundCount);
                     this.emitReboundSkill(meta, shooter, origin, true, beforeVelocity, afterVelocity, speedScale, powerScale, reboundCount, projectile, null);
                     this.tracker.consumeCharge();
                     MythicLogger.debug(
                        MythicLogger.DebugLevel.MECHANIC,
                        "ProjectileRebound aura (mythic) rebounded projectile for {0}, count={1}",
                        defender.getUniqueId(),
                        reboundCount
                     );
                  }
               }
            }
         }
      }
   }

   private SkillMetadata buildReboundMeta(AbstractLocation reboundOrigin, AbstractEntity shooter) {
      SkillMetadata meta = this.tracker.getSkillMetadata().deepCloneNoTargets();
      meta.setOrigin(reboundOrigin.clone());
      meta.setLocationTarget(reboundOrigin.clone());
      if (shooter != null) {
         meta.setTrigger(shooter);
      }

      return meta;
   }

   private boolean passesReboundConditions(SkillMetadata meta) {
      if (this.reboundConditions == null) {
         return true;
      } else {
         for (SkillCondition condition : this.reboundConditions) {
            if (!condition.evaluateCaster(meta)) {
               return false;
            }
         }

         return true;
      }
   }

   private AbstractVector computeReboundVelocity(AbstractVector incoming, AbstractLocation origin, AbstractEntity shooter, double speedScale) {
      AbstractVector direction = null;
      if (this.returnMode == ProjectileReboundAuraComponent.ReturnMode.ORIGINAL_CASTER && shooter != null && shooter.getLocation() != null) {
         AbstractVector towardShooter = shooter.getLocation().toVector().subtract(origin.toVector());
         if (towardShooter.lengthSquared() > 0.0) {
            direction = towardShooter.normalize();
         }
      }

      if (direction == null) {
         if (incoming == null || incoming.lengthSquared() <= 0.0) {
            return new AbstractVector(0, 0, 0);
         }

         direction = incoming.clone().multiply(-1).normalize();
      }

      double magnitude = (incoming == null ? 0.0 : incoming.length()) * speedScale;
      return direction.multiply(magnitude);
   }

   private void applyVanillaPower(Projectile projectile, double powerScale) {
      if (powerScale != 1.0 && !(powerScale < 0.0)) {
         if (projectile instanceof AbstractArrow arrow) {
            arrow.setDamage(Math.max(0.0, arrow.getDamage() * powerScale));
         }
      }
   }

   private void applyVanillaVelocity(Projectile projectile, AbstractVector velocity, double pushDistance) {
      Vector bukkitVelocity = BukkitAdapter.adapt(velocity);
      if (pushDistance > 0.0 && bukkitVelocity.lengthSquared() > 0.0) {
         Vector push = bukkitVelocity.clone().normalize().multiply(pushDistance);
         Location newLocation = projectile.getLocation().add(push);
         if (ServerVersion.isFolia()) {
            projectile.teleportAsync(newLocation);
         } else {
            projectile.teleport(newLocation);
         }
      }

      projectile.setVelocity(bukkitVelocity);
      Schedulers.sync().runLater(() -> {
         if (projectile.isValid()) {
            projectile.setVelocity(bukkitVelocity);
         }
      }, 1L);
   }

   private void applyMythicReboundOffset(
      io.lumine.mythic.core.skills.projectiles.Projectile.ProjectileTracker projectile, AbstractVector velocity, double pushDistance
   ) {
      if (!(pushDistance <= 0.0) && !(velocity.lengthSquared() <= 0.0)) {
         AbstractVector push = velocity.clone().normalize().multiply(pushDistance);
         projectile.getCurrentLocation().add(push);
         if (projectile.getPreviousLocation() != null) {
            projectile.getPreviousLocation().add(push);
         }
      }
   }

   private void emitReboundSkill(
      SkillMetadata meta,
      AbstractEntity shooter,
      AbstractLocation origin,
      boolean mythic,
      AbstractVector beforeVelocity,
      AbstractVector afterVelocity,
      double speedScale,
      double powerScale,
      int reboundCount,
      io.lumine.mythic.core.skills.projectiles.Projectile.ProjectileTracker mythicProjectile,
      AbstractEntity vanillaProjectile
   ) {
      if (mythicProjectile != null) {
         meta.setCallingEvent(mythicProjectile);
         meta.setMetadata("rebound-projectile", mythicProjectile);
      } else if (vanillaProjectile != null) {
         meta.setMetadata("rebound-projectile", vanillaProjectile);
      }

      VariableRegistry vars = meta.getVariables();
      vars.putInt("rebound-count", reboundCount);
      vars.putInt("rebound-max", this.maxRebounds);
      vars.putInt("rebound-is-mythic", mythic ? 1 : 0);
      vars.putDouble("rebound-speed-before", beforeVelocity.length());
      vars.putDouble("rebound-speed-after", afterVelocity.length());
      vars.putDouble("rebound-speed-factor", speedScale);
      vars.putDouble("rebound-power-factor", powerScale);
      vars.putLocation("rebound-origin", origin.clone());
      vars.putVector("rebound-velocity-before", beforeVelocity.clone());
      vars.putVector("rebound-velocity-after", afterVelocity.clone());
      if (mythicProjectile != null) {
         vars.putObject("rebound-projectile", mythicProjectile);
      } else if (vanillaProjectile != null) {
         vars.putObject("rebound-projectile", vanillaProjectile);
      }

      if (shooter != null) {
         vars.putObject("rebound-shooter", shooter);
      }

      if (shooter != null) {
         this.tracker.executeTargetedAuraSkill(this.onReboundSkill, meta, shooter);
      } else {
         this.tracker.executeAuraSkill(this.onReboundSkill, meta);
      }
   }

   private AbstractEntity resolveShooter(ProjectileSource source) {
      return source instanceof Entity bukkitEntity ? BukkitAdapter.adapt(bukkitEntity) : null;
   }

   private int getVanillaReboundCount(Projectile projectile) {
      Integer count = (Integer)projectile.getPersistentDataContainer().get(MobKeys.REBOUND_COUNT, PersistentDataType.INTEGER);
      return count == null ? 0 : count;
   }

   private void setVanillaReboundCount(Projectile projectile, int count) {
      projectile.getPersistentDataContainer().set(MobKeys.REBOUND_COUNT, PersistentDataType.INTEGER, count);
   }

   private boolean isVanillaReboundOnCooldown(Projectile projectile) {
      Long last = (Long)projectile.getPersistentDataContainer().get(MobKeys.REBOUND_LAST_AT, PersistentDataType.LONG);
      if (last == null) {
         return false;
      } else {
         long elapsed = System.currentTimeMillis() - last;
         return elapsed >= 0L && elapsed < 50L;
      }
   }

   private void markVanillaRebound(Projectile projectile) {
      projectile.getPersistentDataContainer().set(MobKeys.REBOUND_LAST_AT, PersistentDataType.LONG, System.currentTimeMillis());
   }

   private int getMythicReboundCount(io.lumine.mythic.core.skills.projectiles.Projectile.ProjectileTracker projectile) {
      SkillMetadata data = projectile.getData();
      return data == null ? 0 : data.getMetadata(Integer.class, "projectile-rebound-count").orElse(0);
   }

   private void setMythicReboundCount(io.lumine.mythic.core.skills.projectiles.Projectile.ProjectileTracker projectile, int count) {
      SkillMetadata data = projectile.getData();
      if (data != null) {
         data.setMetadata("projectile-rebound-count", count);
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

   @MythicAuraComponent(
      name = "projectilerebound",
      aliases = {"projectilereflect", "reboundprojectiles", "reboundingprojectiles"},
      description = "Rebounds incoming projectiles away from the aura holder"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final String onReboundSkillName;
      private Optional<Skill> onReboundSkill = Optional.empty();
      private final ProjectileReboundAuraComponent.ReturnMode returnMode;
      private final boolean reparseFaction;
      private final boolean cancelIncoming;
      private final boolean reboundVanilla;
      private final boolean reboundMythic;
      private final PlaceholderDouble speedFactor;
      private final PlaceholderDouble powerFactor;
      private final PlaceholderDouble reboundDistance;
      private final int maxRebounds;
      private final String reboundConditionsString;
      private List<SkillCondition> reboundConditions;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.onReboundSkillName = mlc.getString(new String[]{"onreboundskill", "onrebound", "orb", "onreflect", "ondeflect"});
         String mode = mlc.getString(new String[]{"returnmode", "mode"}, ProjectileReboundAuraComponent.ReturnMode.ORIGINAL_CASTER.name());

         ProjectileReboundAuraComponent.ReturnMode parsedMode;
         try {
            parsedMode = ProjectileReboundAuraComponent.ReturnMode.valueOf(mode.toUpperCase());
         } catch (Exception e) {
            parsedMode = ProjectileReboundAuraComponent.ReturnMode.ORIGINAL_CASTER;
            MythicLogger.errorMechanicConfig(mechanic, mlc, "Invalid returnMode '" + mode + "' for projectilerebound");
         }

         this.returnMode = parsedMode;
         this.reparseFaction = mlc.getBoolean(new String[]{"reparsefaction", "reparse", "rf", "changeshooter", "cs"}, true);
         this.cancelIncoming = mlc.getBoolean(new String[]{"cancelincoming", "cancelhit", "cancel", "cancelevent", "ce"}, true);
         this.reboundVanilla = mlc.getBoolean(new String[]{"vanillaprojectiles", "vanilla", "vp"}, true);
         this.reboundMythic = mlc.getBoolean(new String[]{"mythicprojectiles", "mythic", "mp"}, true);
         this.speedFactor = mlc.getPlaceholderDouble(new String[]{"speedfactor", "speed", "sf"}, 1.0);
         this.powerFactor = mlc.getPlaceholderDouble(new String[]{"powerfactor", "power", "pf"}, 1.0);
         this.reboundDistance = mlc.getPlaceholderDouble(new String[]{"rebounddistance", "distance", "offset", "push"}, 0.6);
         this.maxRebounds = mlc.getInteger(new String[]{"maxrebounds", "max", "mr"}, 1);
         this.reboundConditionsString = mlc.getString(new String[]{"reboundconditions", "conditions", "cond", "c"}, null);
         mechanic.getManager().queueSecondPass(() -> {
            if (this.onReboundSkillName != null) {
               this.onReboundSkill = mechanic.getManager().getSkill(mechanic.getFile(), mechanic, this.onReboundSkillName);
            }

            if (this.reboundConditionsString != null) {
               this.reboundConditions = mechanic.getManager().getConditions(this.reboundConditionsString);
            }
         });
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new ProjectileReboundAuraComponent(
            this.mechanic,
            tracker,
            this.onReboundSkill,
            this.returnMode,
            this.reparseFaction,
            this.cancelIncoming,
            this.reboundVanilla,
            this.reboundMythic,
            this.speedFactor,
            this.powerFactor,
            this.reboundDistance,
            this.maxRebounds,
            this.reboundConditions
         );
      }
   }

   private static enum ReturnMode {
      MIRROR,
      ORIGINAL_CASTER;
   }
}
