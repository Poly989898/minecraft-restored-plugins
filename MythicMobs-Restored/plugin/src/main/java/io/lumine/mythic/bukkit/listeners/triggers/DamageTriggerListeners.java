package io.lumine.mythic.bukkit.listeners.triggers;

import com.destroystokyo.paper.event.player.PlayerAttackEntityCooldownResetEvent;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.entities.BukkitBee;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.plugin.PluginModule;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.EventExecutor;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.DamagedMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;

public class DamageTriggerListeners extends PluginModule<MythicBukkit> {
   private static final Map<UUID, DamageTriggerListeners.PreAttackCooldown> preAttackCooldown = new ConcurrentHashMap<>();
   private static final long PRE_ATTACK_MAX_AGE_NANOS = 100000000L;
   private final Map<EntityDamageByEntityEvent, Runnable> immunityCooldownResets = new ConcurrentHashMap<>();

   static void capturePreAttackCooldown(UUID playerId, double cooldown) {
      preAttackCooldown.put(playerId, new DamageTriggerListeners.PreAttackCooldown(cooldown, System.nanoTime()));
   }

   public static Double consumePreAttackCooldown(UUID playerId) {
      return ifFresh(preAttackCooldown.remove(playerId));
   }

   public static Double peekPreAttackCooldown(UUID playerId) {
      return ifFresh(preAttackCooldown.get(playerId));
   }

   public static void applyPreAttackCooldown(SkillMetadata meta, AbstractEntity attacker) {
      Double preAttackCd = peekPreAttackCooldown(attacker.getUniqueId());
      if (preAttackCd != null) {
         meta.getVariables().putDouble("hit-attack-cooldown", preAttackCd);
      } else {
         meta.getVariables().remove("hit-attack-cooldown");
      }
   }

   private static Double ifFresh(DamageTriggerListeners.PreAttackCooldown captured) {
      return captured != null && System.nanoTime() - captured.capturedAtNanos() <= 100000000L ? captured.cooldown() : null;
   }

   public DamageTriggerListeners(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      Events.subscribe(EntityDamageByEntityEvent.class, EventPriority.HIGHEST)
         .handleSubclasses()
         .filter(event -> !event.isCancelled())
         .handler(this::onCombatTrigger)
         .bindWith(this);
      Events.subscribe(EntityDamageByEntityEvent.class, EventPriority.MONITOR).handleSubclasses().handler(this::resetImmunityIfCancelled).bindWith(this);
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v26_1)) {
         try {
            Events.subscribe(PlayerAttackEntityCooldownResetEvent.class)
               .handler(event -> capturePreAttackCooldown(event.getPlayer().getUniqueId(), event.getPlayer().getAttackCooldown()))
               .bindWith(this);
            Events.subscribe(PlayerQuitEvent.class).handler(event -> preAttackCooldown.remove(event.getPlayer().getUniqueId())).bindWith(this);
         } catch (Throwable e) {
         }
      }

      Events.subscribe(EntityDamageByBlockEvent.class, EventPriority.HIGHEST)
         .filter(event -> !event.isCancelled())
         .handler(this::onDamagedTrigger)
         .bindWith(this);
      Events.subscribe(EntityDamageEvent.class, EventPriority.HIGHEST).filter(event -> !event.isCancelled()).handler(this::onDamagedTrigger).bindWith(this);
      Events.subscribe(PlayerTeleportEvent.class, EventPriority.HIGHEST).filter(event -> !event.isCancelled()).handler(this::onTeleportTrigger).bindWith(this);
   }

   @Override
   public void unload() {
   }

   protected EventExecutor eventBus() {
      return this.getPlugin().getSkillManager().getEventBus();
   }

   public void onCombatTrigger(EntityDamageByEntityEvent event) {
      if (MythicLogger.isDebugEnabled(MythicLogger.DebugLevel.EVENT)) {
         MythicLogger.debug(
            MythicLogger.DebugLevel.EVENT, "EntityDamageByEntityEvent[eventName={0}] fired for {1}", event.getEventName(), event.getFinalDamage()
         );
      }

      Double preAttackCd = event.getDamager() instanceof Player ? consumePreAttackCooldown(event.getDamager().getUniqueId()) : null;
      if (event.getEntity() instanceof LivingEntity) {
         if (event.getDamage() <= 0.0 && event.getClass() != EntityDamageByEntityEvent.class) {
            MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "Is ZeroDamage subclass, skipping");
         } else {
            SkillTrigger attackTrigger = SkillTriggers.ATTACK;
            AbstractEntity damager;
            if (event.getDamager() instanceof LivingEntity) {
               damager = BukkitAdapter.adapt(event.getDamager());
            } else if (event.getDamager() instanceof Projectile projectile) {
               ProjectileSource ps = projectile.getShooter();
               if (projectile instanceof Trident) {
                  attackTrigger = SkillTriggers.PROJECTILE_HIT;
               } else {
                  attackTrigger = SkillTriggers.BOW_HIT;
               }

               if (ps instanceof LivingEntity livingEntity) {
                  damager = BukkitAdapter.adapt(livingEntity);
               } else {
                  damager = null;
               }
            } else {
               damager = null;
            }

            AbstractEntity damaged;
            if (event.getEntity() instanceof LivingEntity) {
               damaged = BukkitAdapter.adapt(event.getEntity());
            } else {
               damaged = null;
            }

            SkillCaster damagedCaster = damaged == null ? null : this.getPlugin().getSkillManager().getCasterNullable(damaged);
            SkillCaster damagerCaster = damager == null ? null : this.getPlugin().getSkillManager().getCasterNullable(damager);
            if (damagedCaster != null || damagerCaster != null) {
               StatRegistry damagedStatRegistry = damagedCaster == null ? null : damagedCaster.getStatRegistry();
               StatRegistry damagerStatRegistry = damagerCaster == null ? null : damagerCaster.getStatRegistry();
               Optional<Object> maybeData = damaged.getMetadata("skill-damage");
               EntityAttackMetadata triggerMetadata;
               if (maybeData.isPresent()) {
                  DamageMetadata damageData = (DamageMetadata)maybeData.get();
                  if (damageData.getDamageCause() != DamageCause.ENTITY_ATTACK || !damageData.getBoolean("melee") && !damageData.getBoolean("trigger_skills")) {
                     attackTrigger = damageData.getBoolean("projectile") ? SkillTriggers.BOW_HIT : SkillTriggers.SKILL_HIT;
                  }

                  triggerMetadata = new EntityAttackMetadata(event, damageData, damager, damaged);
               } else {
                  triggerMetadata = new EntityAttackMetadata(event, null, damager, damaged);
               }

               AbstractLocation origin = damaged != null ? damaged.getLocation() : BukkitAdapter.adapt(event.getEntity().getLocation());
               if (MythicLogger.isDebugEnabled(MythicLogger.DebugLevel.EVENT)) {
                  MythicLogger.debug(
                     MythicLogger.DebugLevel.EVENT,
                     "[+] Damage Trigger {0} received for {1} by {2}",
                     attackTrigger.name(),
                     triggerMetadata.getFinalDamage(),
                     damaged == null ? "null" : damaged.getName()
                  );
               }

               boolean preventVanillaDamage = false;
               if (damager != null && damagerCaster instanceof ActiveMob am) {
                  if (am.getType().getPreventVanillaDamage() && maybeData.isEmpty()) {
                     preventVanillaDamage = true;
                     event.setCancelled(true);
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - PreventVanillaDamage");
                  }

                  if (am.getOwnerUUID().isPresent() && am.getOwnerUUID().get().equals(damaged.getUniqueId())) {
                     event.setCancelled(true);
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Hit Owner");
                     return;
                  }

                  if (event.getDamager() instanceof Creeper && event.getCause() == DamageCause.ENTITY_EXPLOSION) {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Setting Creeper Custom Damage");
                     if (am.getDamage() != -1.0) {
                        triggerMetadata.setDamage(am.getDamage());
                        triggerMetadata.putBoolean("explosion", true);
                     }
                  }

                  if (am.getType().getMythicEntity() instanceof BukkitBee bukkitBee
                     && bukkitBee.isPreventStingerLoss()
                     && event.getDamager() instanceof Bee bee) {
                     Schedulers.of(event.getEntity()).runLater(() -> bee.setHasStung(false), 1L);
                  }
               }

               SkillMetadata damagerSkillData = damagerCaster == null
                  ? null
                  : this.eventBus().buildSkillMetadata(attackTrigger, triggerMetadata, damagerCaster, damaged, origin, true);
               SkillMetadata damagedSkillData = damagedCaster == null
                  ? null
                  : this.eventBus().buildSkillMetadata(SkillTriggers.DAMAGED, triggerMetadata, damagedCaster, damager, origin, true);
               if (damagerSkillData != null && preAttackCd != null) {
                  damagerSkillData.getVariables().putDouble("hit-attack-cooldown", preAttackCd);
               }

               if (this.getPlugin().getCompatibility().getModelEngine().isPresent()) {
                  Optional<Object> maybeHitboxData = damaged.getMetadata("hitbox");
                  String megHitbox = null;
                  if (maybeHitboxData.isPresent()) {
                     megHitbox = (String)maybeHitboxData.get();
                  } else if (maybeData.isPresent()) {
                     SkillMetadata skillMeta = ((DamageMetadata)maybeData.get()).getMetadata();
                     if (skillMeta != null && skillMeta.getVariables().has("meg-hitbox")) {
                        megHitbox = skillMeta.getVariables().getString("meg-hitbox");
                     }
                  }

                  if (megHitbox != null) {
                     if (damagerSkillData != null) {
                        damagerSkillData.getVariables().putString("hitbox", megHitbox);
                     }

                     if (damagedSkillData != null) {
                        damagedSkillData.getVariables().putString("hitbox", megHitbox);
                     }
                  }
               }

               if (damaged != null && !preventVanillaDamage) {
                  if (damagedCaster instanceof ActiveMob am) {
                     if (damager != null && damagerCaster instanceof ActiveMob am2) {
                        if (am.getUniqueId().equals(am2.getUniqueId())) {
                           if (am.isUsingDamageSkill()) {
                              MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Mob Hit Self");
                              return;
                           }
                        } else if (am.getFaction() != null && am2.getFaction() != null && am.getFaction().equals(am2.getFaction())) {
                           event.setCancelled(true);
                           MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Same Faction");
                           return;
                        }
                     }

                     int maxAttackableRange = am.getType().getMaxAttackableRange();
                     if (maxAttackableRange > 0 && damager != null) {
                        AbstractLocation damagedLoc = damaged.getLocation();
                        AbstractLocation damagerLoc = damager.getLocation();
                        if (!damagedLoc.getWorld().equals(damagerLoc.getWorld())) {
                           MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Damager is out of MaxCombatRange: different world, cancelling damage.");
                           event.setCancelled(true);
                           return;
                        }

                        if (damagedLoc.distanceSquared(damagerLoc) > (double)maxAttackableRange * maxAttackableRange) {
                           MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Damager is out of MaxCombatRange, cancelling damage.");
                           event.setCancelled(true);
                           return;
                        }
                     } else if (maxAttackableRange == 0) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "MythicMob is not attackable, cancelling damage.");
                        event.setCancelled(true);
                        return;
                     }

                     if (am.hasImmunityTable()) {
                        if (am.getImmunityTable().onCooldown(damager)) {
                           MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Immunity Table on Cooldown");
                           event.setCancelled(true);
                           return;
                        }

                        if (event.getFinalDamage() > 0.0) {
                           ActiveMob.ImmunityTable immunityTable = am.getImmunityTable();
                           immunityTable.setCooldown(damager);
                           this.immunityCooldownResets.put(event, () -> immunityTable.clearCooldown(damager));
                        }

                        Schedulers.of(damaged.getBukkitEntity()).runLater(() -> damaged.setNoDamageTicks(0), 1L);
                     }

                     if (am.getType().getShowNameOnDamaged()) {
                        event.getEntity().setCustomNameVisible(true);
                     }

                     if (am.getType().getDamageModifiers() != null) {
                        String damageType = triggerMetadata.getDamageCause().toUpperCase();
                        double mod = am.getType().getDamageModifiers().getOrDefault(damageType, 1.0);
                        double damageModifier = triggerMetadata.getDamageModifier() * mod;
                        MythicLogger.debug(
                           MythicLogger.DebugLevel.SKILL_CHECK,
                           "DamageModifierStat for type {0} is {1}, setting damage modifier to {2}",
                           damageType,
                           mod,
                           damageModifier
                        );
                        if (triggerMetadata.getBonusDamage() != null) {
                           for (Entry<String, Double> entry : triggerMetadata.getBonusDamage().entrySet()) {
                              double modb = am.getType().getDamageModifiers().getOrDefault(entry.getKey(), 1.0);
                              entry.setValue(entry.getValue() * modb);
                           }
                        }

                        if (triggerMetadata.getTags() != null) {
                           for (String tag : triggerMetadata.getTags()) {
                              double modb = am.getType().getDamageModifiers().getOrDefault(tag, 1.0);
                              damageModifier *= modb;
                              MythicLogger.debug(
                                 MythicLogger.DebugLevel.SKILL_CHECK,
                                 "DamageModifier for tag {0} is {1}, setting damage modifier to {2}",
                                 tag,
                                 modb,
                                 damageModifier
                              );
                           }
                        }

                        triggerMetadata.setDamageModifier(damageModifier);
                     }

                     if (am.getType().getEntityDamageModifiers() != null) {
                        double damage = triggerMetadata.getDamage();
                        double modx = am.getType().getEntityDamageModifiers().getOrDefault(event.getDamager().getType().toString(), 1.0);
                        if (modx != 1.0) {
                           damage *= modx;
                        }

                        triggerMetadata.setDamage(damage);
                     }
                  }

                  if (damagerStatRegistry != null && this.cancelledByPreStats(damagerSkillData, triggerMetadata, damagerStatRegistry)) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Cancelled by Damager's Stats");
                     event.setCancelled(true);
                     return;
                  }

                  if (damagedCaster != null && damagedStatRegistry != null && this.cancelledByPreStats(damagedSkillData, triggerMetadata, damagedStatRegistry)) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Cancelled by Damaged's Stats");
                     event.setCancelled(true);
                     return;
                  }

                  if (damaged.getBukkitEntity() instanceof ArmorStand) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Damaged ArmorStand - Returning");
                     return;
                  }
               }

               if (preventVanillaDamage) {
                  triggerMetadata.setDamage(0.0);
               }

               if (damager != null) {
                  boolean triggerSkills = triggerMetadata.getBoolean("trigger_skills");
                  boolean skillAttackWithoutTrigger = maybeData.isPresent() && attackTrigger == SkillTriggers.ATTACK && !triggerSkills;
                  if (damagerCaster != null && !skillAttackWithoutTrigger && (!damagerCaster.isUsingDamageSkill() || triggerSkills)) {
                     TriggeredSkill ts = this.eventBus().processTriggerMechanics(damagerSkillData, triggerMetadata);
                     if (ts.getCancelled()) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Cancelled by mechanics");
                        event.setCancelled(true);
                     }

                     if (attackTrigger == SkillTriggers.ATTACK && triggerMetadata.getBoolean("trigger_skill_damage")) {
                        SkillTrigger skillDamageTrigger = triggerMetadata.getBoolean("projectile") ? SkillTriggers.BOW_HIT : SkillTriggers.SKILL_HIT;
                        SkillMetadata skillDamageData = this.eventBus()
                           .buildSkillMetadata(skillDamageTrigger, triggerMetadata, damagerCaster, damaged, origin, true);
                        TriggeredSkill tsd = this.eventBus().processTriggerMechanics(skillDamageData, triggerMetadata);
                        if (tsd.getCancelled()) {
                           MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Cancelled by skill damage mechanics");
                           event.setCancelled(true);
                        }
                     }
                  }
               }

               if (damagedCaster != null) {
                  TriggeredSkill tsx = this.eventBus().processTriggerMechanics(damagedSkillData, triggerMetadata);
                  if (tsx.getCancelled()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Cancelled by Damaged's Skills");
                     event.setCancelled(true);
                     return;
                  }
               }

               if (damager != null && !preventVanillaDamage) {
                  if (damagerStatRegistry != null) {
                     this.eventBus().processStatsPost(damagerSkillData, triggerMetadata, damagerStatRegistry);
                  }

                  if (damagedStatRegistry != null) {
                     this.eventBus().processStatsPost(damagedSkillData, triggerMetadata, damagedStatRegistry);
                  }
               }

               if (preventVanillaDamage) {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Vanilla Damage Prevented");
               } else {
                  double damage = triggerMetadata.getFinalDamage();
                  if (damage > 0.0) {
                     event.setDamage(damage);
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Final Damage] is {0}", damage);
                     if (damagedCaster instanceof ActiveMob am) {
                        Schedulers.of(damaged.getBukkitEntity()).runLater(() -> am.signalDamaged(), 1L);
                        if (damager != null) {
                           if (am.getType().usesThreatTable()
                              && !am.getEntity().getUniqueId().equals(damager.getUniqueId())
                              && am.getType().getThreatTableUseDamageTaken()) {
                              am.getThreatTable().threatGain(damager, damage);
                           }

                           if (damager.isPlayer() && am.getType().getDamageLeaderboard()) {
                              am.recordPlayerDamage(damager.getUniqueId(), damage);
                           }
                        }
                     }
                  } else if (damage == 0.0) {
                     if (this.plugin.getConfiguration().isCancelDamageIfZero()) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Zero Damage and configured to cancel");
                        event.setCancelled(true);
                        event.setDamage(0.0);
                     } else {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Final Damage] is Zero");
                        event.setDamage(0.0);
                     }
                  } else if (damage < 0.0) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Less Than Zero, Healing");
                     event.setDamage(0.0);
                     event.setCancelled(true);
                     if (damaged.getHealth() - damage > damaged.getMaxHealth()) {
                        damaged.setHealth(damaged.getMaxHealth());
                     } else {
                        damaged.setHealth(damaged.getHealth() - damage);
                     }
                  }

                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "---------- [END] ----------");
               }
            }
         }
      }
   }

   private void resetImmunityIfCancelled(EntityDamageByEntityEvent event) {
      Runnable reset = this.immunityCooldownResets.remove(event);
      if (reset != null && event.isCancelled()) {
         reset.run();
      }
   }

   public void onDamagedTrigger(EntityDamageByBlockEvent event) {
      MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "EntityDamageByBlockEvent fired for {0}", event.getFinalDamage());
      if (event.getEntity() instanceof LivingEntity) {
         if (!this.getPlugin().getMobManager().isIgnoredEntity(event.getEntity().getUniqueId())) {
            AbstractEntity damaged = BukkitAdapter.adapt(event.getEntity());
            SkillCaster damagedCaster = damaged == null ? null : this.getPlugin().getSkillManager().getCasterNullable(damaged);
            this.processSkillDamageTrigger(event, damaged);
            if (damagedCaster != null) {
               StatRegistry damagedStatRegistry = damagedCaster == null ? null : damagedCaster.getStatRegistry();
               DamagedMetadata triggerMetadata = new DamagedMetadata(event, damaged);
               AbstractLocation origin = damaged != null ? damaged.getLocation() : BukkitAdapter.adapt(event.getEntity().getLocation());
               SkillMetadata damagedSkillData = damagedCaster == null
                  ? null
                  : this.eventBus().buildSkillMetadata(SkillTriggers.DAMAGED, triggerMetadata, damagedCaster, null, origin, true);
               if (this.getPlugin().getCompatibility().getModelEngine().isPresent()) {
                  Optional<Object> maybeHitboxData = damaged.getMetadata("hitbox");
                  if (maybeHitboxData.isPresent()) {
                     String megHitbox = (String)maybeHitboxData.get();
                     if (damagedSkillData != null) {
                        damagedSkillData.getVariables().putString("hitbox", megHitbox);
                     }
                  }
               }

               if (damagedCaster instanceof ActiveMob am) {
                  MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "MythicMob " + am.getType().getInternalName() + " took damage!");
                  if (am.getType().getIsInvincible()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "MythicMob is Invincible, canceling damage.");
                     event.setCancelled(true);
                     return;
                  }

                  if (am.hasImmunityTable()) {
                     if (am.getImmunityTable().onCooldown(null)) {
                        event.setCancelled(true);
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "MythicMob is currently immune to damage from non-player sources!");
                        return;
                     }

                     am.getImmunityTable().setCooldown(null);
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Setting MythicMob immune to damage from non-player sources!");
                  }

                  if (am.getType().getDamageModifiers() != null) {
                     String damageType = event.getCause().toString();
                     double mod = am.getType().getDamageModifiers().getOrDefault(damageType.toUpperCase(), 1.0);
                     if (mod != 1.0) {
                        double newdamage = triggerMetadata.getDamage() * mod;
                        MythicLogger.debug(
                           MythicLogger.DebugLevel.SKILL_CHECK,
                           "DamageModifierStat for type {0} is {1}, setting damage to {2}",
                           am.getMobType(),
                           mod,
                           newdamage
                        );
                        triggerMetadata.setDamage(newdamage);
                     }
                  }

                  if (am.hasImmunityTable()) {
                     Schedulers.of(damaged.getBukkitEntity()).runLater(() -> damaged.setNoDamageTicks(0), 1L);
                  } else if (am.getNoDamageTicks() != 20) {
                     Schedulers.of(damaged.getBukkitEntity()).runLater(() -> damaged.setNoDamageTicks(am.getNoDamageTicks()), 1L);
                  }
               }

               if (damagedStatRegistry != null && this.cancelledByPreStats(damagedSkillData, triggerMetadata, damagedStatRegistry)) {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Cancelled by Damaged's Stats");
                  event.setCancelled(true);
               } else {
                  TriggeredSkill ts = this.eventBus().processTriggerMechanics(damagedSkillData, triggerMetadata);
                  if (ts.getCancelled()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Cancelled by Damaged's Skills");
                     event.setCancelled(true);
                  } else {
                     if (damagedStatRegistry != null) {
                        this.eventBus().processStatsPost(damagedSkillData, triggerMetadata, damagedStatRegistry);
                     }

                     double damage = triggerMetadata.getFinalDamage();
                     if (damage > 0.0) {
                        event.setDamage(damage);
                        if (damagedCaster instanceof ActiveMob am) {
                           Schedulers.of(damaged.getBukkitEntity()).runLater(() -> am.signalDamaged(), 1L);
                        }
                     } else if (damage == 0.0) {
                        event.setDamage(0.0);
                        if (this.plugin.getConfiguration().isCancelDamageIfZero()) {
                           event.setCancelled(true);
                        }
                     } else if (damage < 0.0) {
                        event.setDamage(0.0);
                        event.setCancelled(true);
                        if (damaged.getHealth() - damage > damaged.getMaxHealth()) {
                           damaged.setHealth(damaged.getMaxHealth());
                        } else {
                           damaged.setHealth(damaged.getHealth() - damage);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void onDamagedTrigger(EntityDamageEvent event) {
      if (MythicLogger.isDebugEnabled(MythicLogger.DebugLevel.EVENT)) {
         MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "EntityDamageEvent fired for {0}", event.getFinalDamage());
      }

      if (event.getEntity() instanceof LivingEntity) {
         if (!this.getPlugin().getMobManager().isIgnoredEntity(event.getEntity().getUniqueId())) {
            AbstractEntity damaged = BukkitAdapter.adapt(event.getEntity());
            SkillCaster damagedCaster = damaged == null ? null : this.getPlugin().getSkillManager().getCasterNullable(damaged);
            this.processSkillDamageTrigger(event, damaged);
            if (damagedCaster != null) {
               StatRegistry damagedStatRegistry = damagedCaster == null ? null : damagedCaster.getStatRegistry();
               DamagedMetadata triggerMetadata = new DamagedMetadata(event, damaged);
               AbstractLocation origin = damaged != null ? damaged.getLocation() : BukkitAdapter.adapt(event.getEntity().getLocation());
               SkillMetadata damagedSkillData = damagedCaster == null
                  ? null
                  : this.eventBus().buildSkillMetadata(SkillTriggers.DAMAGED, triggerMetadata, damagedCaster, null, origin, true);
               if (this.getPlugin().getCompatibility().getModelEngine().isPresent()) {
                  Optional<Object> maybeHitboxData = damaged.getMetadata("hitbox");
                  if (maybeHitboxData.isPresent()) {
                     String megHitbox = (String)maybeHitboxData.get();
                     if (damagedSkillData != null) {
                        damagedSkillData.getVariables().putString("hitbox", megHitbox);
                     }
                  }
               }

               if (damagedCaster instanceof ActiveMob am) {
                  MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "MythicMob " + am.getType().getInternalName() + " took damage!");
                  if (am.getType().getIsInvincible()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "MythicMob is Invincible, canceling damage.");
                     event.setCancelled(true);
                     return;
                  }

                  if (am.hasImmunityTable()) {
                     if (am.getImmunityTable().onCooldown(null)) {
                        event.setCancelled(true);
                        MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "MythicMob is currently immune to damage from non-player sources!");
                        return;
                     }

                     am.getImmunityTable().setCooldown(null);
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Setting MythicMob immune to damage from non-player sources!");
                  }

                  if (am.getType().getDamageModifiers() != null) {
                     String damageType = event.getCause().toString();
                     double mod = am.getType().getDamageModifiers().getOrDefault(damageType.toUpperCase(), 1.0);
                     if (mod != 1.0) {
                        double newdamage = triggerMetadata.getDamage() * mod;
                        MythicLogger.debug(
                           MythicLogger.DebugLevel.SKILL_CHECK,
                           "DamageModifierStat for type {0} is {1}, setting damage to {2}",
                           am.getMobType(),
                           mod,
                           newdamage
                        );
                        triggerMetadata.setDamage(newdamage);
                     }
                  }

                  if (am.hasImmunityTable()) {
                     Schedulers.of(damaged.getBukkitEntity()).runLater(() -> damaged.setNoDamageTicks(0), 1L);
                  } else if (am.getNoDamageTicks() != 20) {
                     Schedulers.of(damaged.getBukkitEntity()).runLater(() -> damaged.setNoDamageTicks(am.getNoDamageTicks()), 1L);
                  }

                  if (am.getType().getDigOutOfGround() && event.getCause() == DamageCause.SUFFOCATION) {
                     damaged.teleport(damaged.getLocation().add(0.0, 2.0, 0.0));
                     event.setCancelled(true);
                     return;
                  }
               }

               if (damagedStatRegistry != null && this.cancelledByPreStats(damagedSkillData, triggerMetadata, damagedStatRegistry)) {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Cancelled by Damaged's Stats");
                  event.setCancelled(true);
               } else {
                  TriggeredSkill ts = this.eventBus().processTriggerMechanics(damagedSkillData, triggerMetadata);
                  if (ts.getCancelled()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Cancelled by Damaged's Skills");
                     event.setCancelled(true);
                  } else {
                     if (damagedStatRegistry != null) {
                        this.eventBus().processStatsPost(damagedSkillData, triggerMetadata, damagedStatRegistry);
                     }

                     double damage = triggerMetadata.getFinalDamage();
                     if (damage > 0.0) {
                        event.setDamage(damage);
                        if (damagedCaster instanceof ActiveMob am) {
                           Schedulers.of(damaged.getBukkitEntity()).runLater(() -> am.signalDamaged(), 1L);
                        }
                     } else if (damage == 0.0) {
                        event.setDamage(0.0);
                        if (this.plugin.getConfiguration().isCancelDamageIfZero()) {
                           event.setCancelled(true);
                        }
                     } else if (damage < 0.0) {
                        event.setDamage(0.0);
                        event.setCancelled(true);
                        if (damaged.getHealth() - damage > damaged.getMaxHealth()) {
                           damaged.setHealth(damaged.getMaxHealth());
                        } else {
                           damaged.setHealth(damaged.getHealth() - damage);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean cancelledByPreStats(SkillMetadata data, SkillTriggerMetadata triggerMetadata, StatRegistry statRegistry) {
      TriggeredSkill preStats = new TriggeredSkill(data);
      this.eventBus().processStatsPre(data, triggerMetadata, statRegistry);
      return preStats.getCancelled();
   }

   private void processSkillDamageTrigger(EntityDamageEvent event, AbstractEntity damaged) {
      if (damaged != null) {
         Optional<Object> maybeData = damaged.getMetadata("skill-damage");
         if (!maybeData.isEmpty()) {
            DamageMetadata damageData = (DamageMetadata)maybeData.get();
            SkillCaster damagerCaster = damageData.getDamager();
            if (damagerCaster != null) {
               if (!damagerCaster.isUsingDamageSkill() || damageData.getBoolean("trigger_skills")) {
                  SkillTrigger trigger = damageData.getBoolean("projectile") ? SkillTriggers.BOW_HIT : SkillTriggers.SKILL_HIT;
                  DamagedMetadata triggerMetadata = new DamagedMetadata(event, damageData, damaged);
                  SkillMetadata skillData = this.eventBus().buildSkillMetadata(trigger, triggerMetadata, damagerCaster, damaged, damaged.getLocation(), true);
                  MythicLogger.debug(
                     MythicLogger.DebugLevel.EVENT,
                     "[+] Damage Trigger {0} received for {1} by {2}",
                     trigger.name(),
                     triggerMetadata.getFinalDamage(),
                     damaged.getName()
                  );
                  TriggeredSkill ts = this.eventBus().processTriggerMechanics(skillData, triggerMetadata);
                  if (ts.getCancelled()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Cancelled by skill damage mechanics");
                     event.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   public void onTeleportTrigger(PlayerTeleportEvent event) {
      Player player = event.getPlayer();
      AbstractPlayer aPlayer = BukkitAdapter.adapt(player);
      SkillCaster caster = this.getPlugin().getSkillManager().getCaster(aPlayer);
      AbstractLocation origin = BukkitAdapter.adapt(event.getTo());
      if (caster != null) {
         SkillMetadata meta = this.eventBus().buildSkillMetadata(SkillTriggers.TELEPORT, caster, caster.getEntity(), origin, true);
         meta.setMetadata("teleport-cause", event.getCause());
         TriggeredSkill ts = this.eventBus().processTriggerMechanics(meta);
         if (ts.getCancelled()) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Damage] - Cancelled by Damaged's Skills");
            event.setCancelled(true);
         }
      }
   }

   private record PreAttackCooldown(double cooldown, long capturedAtNanos) {
   }
}
