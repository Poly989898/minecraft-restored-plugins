package io.lumine.mythic.bukkit.listeners;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.BukkitSilverfish;
import io.lumine.mythic.bukkit.entities.BukkitSnowman;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.pdc.DataType;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.DespawnMode;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.mobs.MobType;
import io.lumine.mythic.core.mobs.PersistentMobData;
import io.lumine.mythic.core.mobs.WorldScaling;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import io.lumine.mythic.core.skills.mechanics.HideMechanic;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import io.lumine.mythic.core.spawning.random.RandomSpawnPoint;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

public class MobListeners extends ReloadableModule<MythicBukkit> implements Listener {
   public MobListeners(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      Events.subscribe(PlayerArmorStandManipulateEvent.class).handler(event -> {
         MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "PlayerArmorStandManipulateEvent fired");
         Optional<ActiveMob> maybeMob = this.getPlugin().getMobManager().getActiveMob(event.getRightClicked().getUniqueId());
         if (maybeMob.isPresent()) {
            ActiveMob am = maybeMob.get();
            if (!am.getType().getIsInteractable()) {
               event.setCancelled(true);
            }
         }
      }).bindWith(this);
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_19_4)) {
         Events.subscribe(EntityDamageByEntityEvent.class)
            .filter(event -> event.getEntity() instanceof Interaction)
            .handler(
               event -> this.getPlugin()
                  .getMobManager()
                  .getActiveMob(event.getEntity().getUniqueId())
                  .ifPresent(
                     am -> {
                        AbstractEntity damager = BukkitAdapter.adapt(event.getDamager());
                        AbstractLocation origin = am.getLocation();
                        SkillMetadata damageMetadata = this.getPlugin()
                           .getSkillManager()
                           .getEventBus()
                           .buildSkillMetadata(SkillTriggers.DAMAGED, am, damager, origin, true);
                        EntityAttackMetadata triggerMetadata = new EntityAttackMetadata(event, null, damager, am.getEntity());
                        TriggeredSkill ts = this.getPlugin().getSkillManager().getEventBus().processTriggerMechanics(damageMetadata, triggerMetadata);
                        if (ts.getCancelled()) {
                           event.setCancelled(true);
                        }
                     }
                  )
            )
            .bindWith(this);
      }

      Events.subscribe(SlimeSplitEvent.class, EventPriority.HIGH).handler(event -> {
         var entity = ((org.bukkit.event.entity.EntityEvent)event).getEntity();
         if (entity.getPersistentDataContainer().has(PersistentMobData.SLIME_NO_SPLIT)) {
            event.setCancelled(true);
            event.setCount(0);
         }
      }).bindWith(this);
      Events.subscribe(CreatureSpawnEvent.class, EventPriority.HIGHEST)
         .filter(event -> !event.isCancelled())
         .handler(
            event -> {
               if (!this.getPlugin().getConfiguration().isDebugMode()) {
                  if (!this.getPlugin().getCompatibility().getWorldGuard().isPresent()
                     || this.getPlugin().getCompatibility().getWorldGuard().get().getLocationAllowsMobSpawning(event.getLocation())) {
                     MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "CreatureSpawnEvent fired");
                     LivingEntity entity = event.getEntity();
                     AbstractEntity aEntity = BukkitAdapter.adapt(entity);
                     if (event.getSpawnReason() != SpawnReason.NATURAL && event.getSpawnReason() != SpawnReason.CHUNK_GEN) {
                        if (event.getSpawnReason() == SpawnReason.MOUNT) {
                           List<Entity> passengers = event.getEntity().getPassengers();
                           if (!passengers.isEmpty()) {
                              MobExecutor mobExecutor = this.getPlugin().getMobManager();
                              Schedulers.of(entity)
                                 .runLater(
                                    () -> {
                                       if (mobExecutor.isActiveMob(passengers.get(0).getUniqueId())
                                          && !mobExecutor.isActiveMob(aEntity)
                                          && mobExecutor.getMythicMobInstance(passengers.get(0)).getType().getPreventMounts()) {
                                          aEntity.remove();
                                       }
                                    },
                                    1L
                                 );
                           }
                        }
                     } else if (this.getPlugin().getConfiguration().isVanillaSpawnsDisabled() && !BukkitEntityType.getIsSpawning()) {
                        event.setCancelled(true);
                     }

                     RandomSpawnPoint rsp = new RandomSpawnPoint(
                        BukkitAdapter.adapt(event.getEntity()), BukkitAdapter.adapt(event.getLocation()), event.getSpawnReason()
                     );
                     Entity ee = BukkitEntityType.getIsSpawning()
                        ? null
                        : BukkitAdapter.adapt(this.getPlugin().getRandomSpawningManager().handleSpawnEvent(rsp));
                     if (ee != null) {
                        event.setCancelled(true);
                     } else if (event.getSpawnReason() != SpawnReason.BREEDING && !BukkitEntityType.getIsSpawning()) {
                        if (entity.getPersistentDataContainer().has(MobKeys.TYPE)) {
                           String mobType = entity.getPersistentDataContainer().get(MobKeys.TYPE, PersistentDataType.STRING);
                           Optional<MythicMob> maybeMob = this.getPlugin().getMobManager().getMythicMob(mobType);
                           if (maybeMob.isPresent()) {
                              MythicMob mm = maybeMob.get();
                              double level = WorldScaling.getLevelBonus(aEntity.getLocation());
                              ActiveMob am = this.getPlugin().getMobManager().registerActiveMob(aEntity, mm, level);
                              mm.applyMobOptions(am, am.getLevel());
                              mm.applySpawnModifiers(am);
                              return;
                           }
                        }

                        MythicEntityType met = this.getPlugin().getBootstrap().getMythicEntityType(event.getEntity());
                        Optional<MythicMob> maybeType = this.getPlugin().getMobManager().getVanillaType(met);
                        if (maybeType.isPresent()) {
                           MythicMob mm = maybeType.get();
                           double level = WorldScaling.getLevelBonus(aEntity.getLocation());
                           ActiveMob am = this.getPlugin().getMobManager().registerActiveMob(aEntity, mm, level);
                           BukkitEntityType entityType = mm.getMythicEntity();
                           if (entityType.compare(event.getEntity())) {
                              mm.getMythicEntity().applySpawnOptions(event.getEntity());
                           } else {
                              MythicLogger.error(
                                 event.getEntity().getType().toString() + " found with mobtype '" + mm.getInternalName() + "' but does not match."
                              );
                           }

                           mm.applyMobOptions(am, am.getLevel());
                           mm.applyMobVolatileOptions(am);
                           mm.applySpawnModifiers(am);
                        } else {
                           WorldScaling worldScaling = WorldScaling.get(aEntity.getLocation().getWorld().getName());
                           if (worldScaling.getScaleVanillaMobs()) {
                              double level = Math.max(1.0, WorldScaling.getLevelBonus(aEntity.getLocation()));
                              if (this.getPlugin().getConfiguration().getScalingEquationHealth() != null) {
                                 double newHealth = this.getPlugin()
                                    .getConfiguration()
                                    .getScalingEquationHealth()
                                    .setVariable("v", aEntity.getHealth())
                                    .setVariable("l", level)
                                    .evaluate();
                                 aEntity.setMaxHealth(newHealth);
                                 aEntity.setHealth(newHealth);
                              }

                              if (this.getPlugin().getConfiguration().getScalingEquationDamage() != null && aEntity.getDamageBase() > 0.0) {
                                 double newDamage = this.getPlugin()
                                    .getConfiguration()
                                    .getScalingEquationDamage()
                                    .setVariable("v", aEntity.getDamageBase())
                                    .setVariable("l", level)
                                    .evaluate();
                                 aEntity.setDamage(newDamage);
                              }
                           }
                        }
                     }

                     if (event.getSpawnReason() == SpawnReason.CUSTOM) {
                        if (plugin.getConfiguration().getUseCompatibilityMode()) {
                           LivingEntity l = event.getEntity();
                           Schedulers.of(l)
                              .runLater(
                                 () -> {
                                    if (!this.getPlugin().getMobManager().isActiveMob(aEntity.getUniqueId())) {
                                       MythicLogger.debug(
                                          MythicLogger.DebugLevel.INFO,
                                          "Compatibility mode enabled and found custom mob spawn! Checking for MythicMob '" + l.getCustomName() + "'..."
                                       );
                                       MythicMob mm = this.getPlugin().getMobManager().getMythicMobByDisplayCompat(aEntity);
                                       if (mm != null) {
                                          this.getPlugin().getMobManager().SetupMythicMobCompat(l, mm);
                                       } else {
                                          this.getPlugin().getMobManager().setIgnoreEntity(l.getUniqueId());
                                       }
                                    }
                                 },
                                 10L
                              );
                        } else {
                           LivingEntity l = event.getEntity();
                           Schedulers.of(l).runLater(() -> {
                              if (!this.getPlugin().getMobManager().isActiveMob(l.getUniqueId())) {
                                 this.getPlugin().getMobManager().setIgnoreEntity(l.getUniqueId());
                              }
                           }, 20L);
                        }
                     }
                  }
               }
            }
         )
         .bindWith(this);
      Events.subscribe(EntitiesLoadEvent.class, EventPriority.HIGHEST).handler(event -> {
         for (Entity ee : event.getEntities()) {
            if (!(ee instanceof Player) && ee instanceof LivingEntity livingEntity && !this.getPlugin().getMobManager().isMythicMob(ee)) {
               AbstractEntity ae = BukkitAdapter.adapt(ee);
               MythicEntityType met = this.getPlugin().getBootstrap().getMythicEntityType(ee);
               Optional<MythicMob> maybeType = this.getPlugin().getMobManager().getVanillaType(met);
               if (maybeType.isPresent()) {
                  MythicMob mm = maybeType.get();
                  if (mm.getMythicEntity().compare(ee)) {
                     double level = 1.0 + WorldScaling.getLevelBonus(ae.getLocation());
                     ActiveMob am = this.getPlugin().getMobManager().registerActiveMob(ae, mm, level);
                     mm.getMythicEntity().applySpawnOptions(ee);
                     mm.applyMobOptions(am, am.getLevel());
                     mm.applyMobVolatileOptions(am);
                     mm.applySpawnModifiers(am);
                  }
               }
            }
         }
      }).bindWith(this);
      Events.subscribe(ChunkLoadEvent.class, EventPriority.HIGHEST)
         .filter(event -> event.isNewChunk())
         .handler(
            event -> {
               if (!this.getPlugin().getConfiguration().isDebugMode()) {
                  MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "ChunkLoadEvent (New Chunk) fired");

                  for (Entity entity : event.getChunk().getEntities()) {
                     if (entity.getType() != EntityType.PLAYER
                        && (
                           !this.getPlugin().getCompatibility().getWorldGuard().isPresent()
                              || this.getPlugin().getCompatibility().getWorldGuard().get().getLocationAllowsMobSpawning(entity.getLocation())
                        )) {
                        AbstractEntity spawn = BukkitAdapter.adapt(entity);
                        if (this.getPlugin().getMobManager().isMythicMob(entity)) {
                           ActiveMob activeMob = this.getPlugin().getMobManager().getMythicMobInstance(spawn);
                           activeMob.setSpawnLocation(spawn.getLocation());
                        } else {
                           RandomSpawnPoint rsp = new RandomSpawnPoint(spawn, spawn.getLocation(), SpawnReason.NATURAL);
                           Entity ee = BukkitAdapter.adapt(this.getPlugin().getRandomSpawningManager().handleSpawnEvent(rsp));
                           if (ee != null) {
                              entity.remove();
                           } else if (this.getPlugin().getConfiguration().isVanillaSpawnsDisabled() && !BukkitEntityType.getIsSpawning()) {
                              entity.remove();
                           }
                        }
                     }
                  }
               }
            }
         )
         .bindWith(this);
      Events.subscribe(EntityTransformEvent.class).handler(event -> {
         if (this.getPlugin().getMobManager().isActiveMob(event.getEntity().getUniqueId())) {
            ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(event.getEntity());
            if (am.getType().getPreventTransformation()) {
               event.setCancelled(true);
            }
         }
      }).bindWith(this);
      Events.subscribe(CreatureSpawnEvent.class, EventPriority.HIGHEST)
         .filter(event -> !event.isCancelled())
         .filter(event -> event.getSpawnReason() == SpawnReason.BEEHIVE)
         .handler(event -> {
            LivingEntity entity = event.getEntity();
            if (this.getPlugin().getMobManager().isMythicMob(entity)) {
               this.getPlugin().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(event.getEntity()));
            }
         })
         .bindWith(this);
      Events.subscribe(EntityCombustEvent.class, EventPriority.HIGHEST)
         .filter(event -> !event.isCancelled())
         .filter(event -> !(event instanceof EntityCombustByBlockEvent) && !(event instanceof EntityCombustByEntityEvent))
         .filter(event -> event.getEntity().getWorld().getEnvironment() == Environment.NORMAL)
         .handler(event -> {
            Entity entity = event.getEntity();
            if (this.getPlugin().getMobManager().isActiveMob(entity.getUniqueId())) {
               ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(entity);
               if (am.getType().getPreventSunburn()) {
                  event.setDuration(0);
                  event.setCancelled(true);
               }
            }
         })
         .bindWith(this);
      Events.subscribe(EntityDamageEvent.class, EventPriority.LOWEST)
         .filter(event -> !event.isCancelled())
         .handler(this::damagePassthroughEvent)
         .bindWith(this);
      Events.subscribe(EntityDamageByEntityEvent.class, EventPriority.LOWEST)
         .filter(event -> !event.isCancelled())
         .handler(this::damagePassthroughEvent)
         .bindWith(this);
      Events.subscribe(EntityDamageByBlockEvent.class, EventPriority.LOWEST)
         .filter(event -> !event.isCancelled())
         .handler(this::damagePassthroughEvent)
         .bindWith(this);
      Events.subscribe(PlayerInteractEntityEvent.class, EventPriority.HIGH).handler(this::onTagEntity).bindWith(this);
      Events.subscribe(PlayerLeashEntityEvent.class, EventPriority.HIGH).handler(this::onLeashEntity).bindWith(this);
      Events.subscribe(EntityChangeBlockEvent.class, EventPriority.HIGH).handler(this::onChangeBlockEvent).bindWith(this);
      Events.subscribe(EntityBlockFormEvent.class, EventPriority.HIGH).handler(this::onBlockForm).bindWith(this);
      Events.subscribe(EntityTeleportEvent.class, EventPriority.HIGH).handler(this::onTeleportEvent).bindWith(this);
      Events.subscribe(MythicDamageEvent.class, EventPriority.HIGH).filter(event -> !event.isCancelled()).handler(this::onMythicDamage).bindWith(this);
      Events.subscribe(EntityDamageEvent.class, EventPriority.MONITOR)
         .handleSubclasses()
         .filter(event -> !event.isCancelled())
         .handler(this::captureSkillDamageAmount)
         .bindWith(this);
      if (ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
         Events.subscribe(EntityKnockbackEvent.class, EventPriority.HIGH).handleSubclasses().handler(this::onKnockbackEntity).bindWith(this);
      }

      Events.subscribe(PlayerJoinEvent.class).handler(event -> this.restoreHidesForPlayer(event.getPlayer())).bindWith(this);
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_4)) {
         Events.subscribe(EntityRemoveEvent.class).handler(event -> {
            Entity entity = event.getEntity();
            UUID entityUuid = entity.getUniqueId();
            if (this.getPlugin().getMobManager().getAudienceTrackedEntities().contains(entityUuid)) {
               if (ServerVersion.isFolia()) {
                  Schedulers.of(entity).runLater(() -> {
                     Entity current = Bukkit.getEntity(entityUuid);
                     if (current == null || !current.isValid()) {
                        this.getPlugin().getMobManager().unregisterAudienceTrackedEntity(entityUuid);
                     }
                  }, 5L);
               } else {
                  Schedulers.sync().runLater(() -> {
                     Entity current = Bukkit.getEntity(entityUuid);
                     if (current == null || !current.isValid()) {
                        this.getPlugin().getMobManager().unregisterAudienceTrackedEntity(entityUuid);
                     }
                  }, 5L);
               }
            }
         }).bindWith(this);
      }
   }

   @Override
   public void unload() {
   }

   private void restoreHidesForPlayer(Player player) {
      UUID playerUuid = player.getUniqueId();

      for (ActiveMob am : this.getPlugin().getMobManager().getActiveMobs()) {
         AbstractEntity entity = am.getEntity();
         if (entity != null && entity.isValid()) {
            PersistentDataContainer pdc = entity.getDataContainer();
            if (HideMechanic.getHiddenPlayers(pdc).contains(playerUuid) && ServerVersion.isAfterOrEq(MinecraftVersions.v1_18)) {
               player.hideEntity(this.getPlugin(), BukkitAdapter.adapt(entity));
            }
         }
      }

      this.restoreAudienceHidesForPlayer(player);
   }

   private void restoreAudienceHidesForPlayer(Player player) {
      UUID playerUuid = player.getUniqueId();
      Set<UUID> tracked = this.getPlugin().getMobManager().getAudienceTrackedEntities();
      if (!tracked.isEmpty()) {
         for (UUID entityUuid : new HashSet<>(tracked)) {
            Entity bukkitEntity = Bukkit.getEntity(entityUuid);
            if (bukkitEntity != null && !bukkitEntity.isDead() && bukkitEntity.isValid()) {
               this.getPlugin().getMobManager().getActiveMob(entityUuid).ifPresent(am -> {
                  if (am.getType() instanceof MobType mobType) {
                     mobType.refreshExecutionAudienceForJoin(am, playerUuid);
                  }
               });
               Set<UUID> audience = this.readExecutionAudience(bukkitEntity);
               if (audience == null) {
                  this.getPlugin().getMobManager().unregisterAudienceTrackedEntity(entityUuid);
               } else if (!audience.contains(playerUuid) && ServerVersion.isAfterOrEq(MinecraftVersions.v1_18)) {
                  player.hideEntity(this.getPlugin(), bukkitEntity);
               }
            } else {
               this.getPlugin().getMobManager().unregisterAudienceTrackedEntity(entityUuid);
            }
         }
      }
   }

   private Set<UUID> readExecutionAudience(Entity bukkitEntity) {
      PersistentDataContainer pdc = bukkitEntity.getPersistentDataContainer();
      if (!pdc.has(MobKeys.EXECUTION_AUDIENCE, DataType.UUID_ARRAY)) {
         return null;
      } else {
         UUID[] arr = pdc.get(MobKeys.EXECUTION_AUDIENCE, DataType.UUID_ARRAY);
         return arr == null ? null : new HashSet<>(Arrays.asList(arr));
      }
   }

   public void damagePassthroughEvent(EntityDamageEvent event) {
      if (event.getEntity() instanceof Damageable damaged) {
         if (this.getPlugin().getMobManager().isActiveMob(damaged.getUniqueId())) {
            ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(damaged);
            if (am.getType().getPassthroughDamage() && am.getParent().isPresent()) {
               MythicLogger.debug(MythicLogger.DebugLevel.TRIGGER, "Passing damage to parent");
               event.setCancelled(true);
               LivingEntity parent = (LivingEntity)am.getParent().get().getBukkitEntity();
               double damage = event.getFinalDamage();
               if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
                  Entity damager = entityDamageByEntityEvent.getDamager();
                  if (damager instanceof Projectile projectileEntity) {
                     MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Damage passed from arrow");
                     ProjectileSource shooter = projectileEntity.getShooter();
                     if (shooter instanceof LivingEntity) {
                        parent.damage(damage, (Entity)projectileEntity.getShooter());
                     } else {
                        parent.damage(damage);
                     }

                     if (!(damager instanceof Trident)) {
                        damager.remove();
                     }
                  } else {
                     parent.damage(damage, damager);
                  }
               } else {
                  parent.damage(damage, event.getDamageSource().getCausingEntity());
               }
            }
         }
      }
   }

   public void onTagEntity(PlayerInteractEntityEvent e) {
      if (e.getRightClicked() instanceof LivingEntity) {
         LivingEntity l = (LivingEntity)e.getRightClicked();
         if (this.getPlugin().getMobManager().isActiveMob(l.getUniqueId())) {
            EntityEquipment ee = e.getPlayer().getEquipment();
            if (ee == null || ee.getItemInMainHand().getType() == Material.NAME_TAG) {
               ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(l);
               if (am.getType().getPreventRename()) {
                  e.setCancelled(true);
                  e.getPlayer().sendMessage("\u00a7cThis mob can't be renamed");
               } else {
                  am.setDespawnMode(DespawnMode.PERSISTENT);
                  Schedulers.of(l).runLater(() -> am.setDisplayName(l.getName()), 1L);
               }
            }
         }
      }
   }

   public void onLeashEntity(PlayerLeashEntityEvent e) {
      if (e.getEntity() instanceof LivingEntity) {
         LivingEntity l = (LivingEntity)e.getEntity();
         if (this.getPlugin().getMobManager().isActiveMob(l.getUniqueId())) {
            ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(l);
            if (am.getType().getPreventLeashing()) {
               e.setCancelled(true);
               e.getPlayer().sendMessage(ChatColor.RED + "That mob can't be leashed!");
            }
         }
      }
   }

   private void onKnockbackEntity(EntityKnockbackEvent event) {
      if (this.getPlugin().getMobManager().isMythicMob(event.getEntity())) {
         ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(event.getEntity());
         if (am != null && am.getType().getPreventKnockback()) {
            event.setCancelled(true);
         }
      }
   }

   public void onBlockForm(EntityBlockFormEvent e) {
      if (this.getPlugin().getMobManager().isActiveMob(e.getEntity().getUniqueId())) {
         ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(e.getEntity());
         if (am.getType().getMythicEntity() instanceof BukkitSnowman && ((BukkitSnowman)am.getType().getMythicEntity()).getPreventSnowFormation()) {
            e.setCancelled(true);
         }
      } else if (e.getEntity() instanceof Snowman) {
         this.getPlugin().getMobManager().getMythicType(e.getEntity()).flatMap(type -> this.getPlugin().getMobManager().getMythicMob(type)).ifPresent(mm -> {
            if (mm.getMythicEntity() instanceof BukkitSnowman && ((BukkitSnowman)mm.getMythicEntity()).getPreventSnowFormation()) {
               e.setCancelled(true);
            }
         });
      }
   }

   public void onTeleportEvent(EntityTeleportEvent e) {
      if (e.getEntity() instanceof LivingEntity livingEntity) {
         ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(e.getEntity());
         if (am != null) {
            if (!am.getEntity().hasMetadata("teleporting")) {
               if (am.getType().getPreventEndermanTeleport()) {
                  am.getEntity().setMetadata("teleporting", true);
                  if (ServerVersion.isFolia()) {
                     e.getEntity().teleportAsync(e.getFrom());
                  } else {
                     e.getEntity().teleport(e.getFrom());
                  }

                  am.getEntity().removeMetadata("teleporting");
                  e.setCancelled(true);
               } else {
                  SkillMetadata data = this.getPlugin().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.TELEPORT, am, null, null, false);
                  TriggeredSkill ts = this.getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
                  if (ts.getCancelled()) {
                     e.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   public void onChangeBlockEvent(EntityChangeBlockEvent e) {
      if (this.getPlugin().getMobManager().isActiveMob(e.getEntity().getUniqueId())) {
         ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(e.getEntity());
         if (e.getEntityType() == EntityType.FALLING_BLOCK) {
            SkillMetadata data = this.getPlugin().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.DEATH, am, null, null, true);
            TriggeredSkill ts = this.getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
            if (ts.getCancelled()) {
               e.setCancelled(true);
            }
         }

         if (am.getType().getMythicEntity() instanceof BukkitSilverfish && ((BukkitSilverfish)am.getType().getMythicEntity()).getPreventBlockInfection()) {
            e.setCancelled(true);
            LivingEntity l = (LivingEntity)e.getEntity();
            ActiveMob amN = am.getType().spawn(am.getEntity().getLocation(), am.getLevel(), io.lumine.mythic.api.mobs.entities.SpawnReason.OTHER);
            LivingEntity ll = (LivingEntity)BukkitAdapter.adapt(amN.getEntity());
            ll.setMaxHealth(l.getMaxHealth());
            ll.setHealth(l.getHealth());
            if (l.getPassenger() != null) {
               ll.setPassenger(l.getPassenger());
            }

            if (l.getVehicle() != null) {
               l.getVehicle().setPassenger(ll);
            }

            for (PotionEffect p : l.getActivePotionEffects()) {
               ll.addPotionEffect(p);
            }

            ActiveMob am2 = this.getPlugin().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(ll));
            if (am2 == null) {
               MythicLogger.debug(MythicLogger.DebugLevel.INFO, "-- Something prevented Silverfish from respawning! PreventBlockInfection failed :(");
               return;
            }

            am2.setStance(am.getStance());
            am2.importPlayerKills(am.getPlayerKills());
            am2.importThreatTable(am.getThreatTable());
            l.remove();
            am.setDead();
         }
      }
   }

   public void onMythicDamage(MythicDamageEvent e) {
      if (!this.plugin.getVolatileCodeHandler().getEntityHandler().doDamage(e.getDamageMetadata(), e.getTarget())) {
         e.setCancelled(true);
      }
   }

   public void captureSkillDamageAmount(EntityDamageEvent e) {
      for (MetadataValue value : e.getEntity().getMetadata("skill-damage")) {
         if (value.getOwningPlugin().equals(this.plugin)) {
            SkillMetadata data = ((DamageMetadata)value.value()).getMetadata();
            if (data != null) {
               data.getVariables().putDouble("damage-dealt", e.getFinalDamage());
            }

            return;
         }
      }
   }
}
