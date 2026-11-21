package io.lumine.mythic.bukkit.adapters;

import com.destroystokyo.paper.event.entity.CreeperIgniteEvent;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.volatilecode.VolatileEntityType;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.entities.BukkitCreeper;
import io.lumine.mythic.bukkit.entities.BukkitTNT;
import io.lumine.mythic.bukkit.entities.properties.TameableEntityType;
import io.lumine.mythic.bukkit.entities.properties.TameableProperty;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobInteractEvent;
import io.lumine.mythic.bukkit.events.MythicMobLootDropEvent;
import io.lumine.mythic.bukkit.listeners.triggers.DamageTriggerListeners;
import io.lumine.mythic.bukkit.listeners.triggers.HealTriggerListener;
import io.lumine.mythic.bukkit.listeners.triggers.PlayerInputTriggerListener;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.plugin.PluginModule;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.tracker.DamageRecord;
import io.lumine.mythic.core.skills.EventExecutor;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EffectApplyMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EffectRemoveMetadata;
import io.lumine.mythic.core.skills.triggers.meta.SimpleEventSkillMetadata;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.adventure.PaperHelper;
import io.papermc.paper.event.player.PlayerTradeEvent;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;

public class BukkitSkillTriggers extends PluginModule<MythicBukkit> implements Listener {
   private final DamageTriggerListeners damageTriggerListeners;
   private final HealTriggerListener healTriggerListener;
   private final Set<UUID> pendingInteractCancels = ConcurrentHashMap.newKeySet();
   private static final EnumMap<EntityType, Function<EntityType, Boolean>> shooterProjectileContemplatedMap = new EnumMap<>(EntityType.class);

   public BukkitSkillTriggers(MythicBukkit plugin) {
      super(plugin);
      this.damageTriggerListeners = new DamageTriggerListeners(plugin);
      this.healTriggerListener = new HealTriggerListener(plugin);
   }

   public void load(MythicBukkit plugin) {
      Events.subscribe(EntityShootBowEvent.class).filter(event -> !event.isCancelled()).handler(this::onShootTrigger).bindWith(this);
      Events.subscribe(ProjectileLaunchEvent.class)
         .filter(event -> !event.isCancelled())
         .filter(event -> event.getEntity().getShooter() != null)
         .filter(event -> event.getEntity().getShooter() instanceof Entity)
         .filter(event -> plugin.getMobManager().isActiveMob(((Entity)event.getEntity().getShooter()).getUniqueId()))
         .handler(this::onShootProjectileTrigger)
         .bindWith(this);
      Events.subscribe(EntityDeathEvent.class, EventPriority.HIGHEST).handler(this::onDeathTrigger).bindWith(this);
      Events.subscribe(EntityResurrectEvent.class).handler(this::onTotemPopTrigger).bindWith(this);
      Events.subscribe(CreeperPowerEvent.class).handler(this::onCreeperChargedTrigger).bindWith(this);
      Events.subscribe(EntityBreedEvent.class).handler(this::onEntityBreed).bindWith(this);
      Events.subscribe(EntityPortalEvent.class).handler(this::onWorldChange).bindWith(this);
      Events.subscribe(PlayerBucketEntityEvent.class).handler(this::onBucketEntityFill).bindWith(this);
      Events.subscribe(PlayerBucketFillEvent.class).handler(this::onBucketFill).bindWith(this);
      Events.subscribe(PlayerBucketEmptyEvent.class).handler(this::onBucketFill).bindWith(this);
      Events.subscribe(PlayerShearEntityEvent.class).filter(event -> !event.isCancelled()).handler(this::onShearEntity).bindWith(this);
      Events.subscribe(PlayerLeashEntityEvent.class, EventPriority.HIGHEST).filter(event -> !event.isCancelled()).handler(this::onLeashEntity).bindWith(this);
      Events.subscribe(EntityUnleashEvent.class, EventPriority.HIGHEST)
         .handleSubclasses()
         .filter(event -> !event.isCancelled())
         .handler(this::onUnleashEntity)
         .bindWith(this);
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_2)) {
         Events.subscribe(EntityMountEvent.class).handler(this::onMounted).bindWith(this);
         Events.subscribe(EntityDismountEvent.class).handler(this::onDismounted).bindWith(this);
      } else {
         this.subscribeLegacyMount("org.spigotmc.event.entity.EntityMountEvent", SkillTriggers.MOUNT, "getMount");
         this.subscribeLegacyMount("org.spigotmc.event.entity.EntityDismountEvent", SkillTriggers.DISMOUNTED, "getDismounted");
      }

      Events.subscribe(EntityPotionEffectEvent.class).filter(event -> !event.isCancelled()).handler(this::onEffectTrigger).bindWith(this);
      if (ServerVersion.isPaper()) {
         try {
            Events.subscribe(CreeperIgniteEvent.class).handler(this::onCreeperPrimeTrigger).bindWith(this);
            Events.subscribe(PlayerTradeEvent.class).handler(this::onMerchantTrade).bindWith(this);
         } catch (Throwable e) {
         }

         try {
            new PlayerInputTriggerListener(plugin);
         } catch (Throwable ex) {
         }
      }
   }

   @Override
   public void unload() {
   }

   protected EventExecutor eventBus() {
      return this.getPlugin().getSkillManager().getEventBus();
   }

   public void onTotemPopTrigger(EntityResurrectEvent event) {
      if (!event.isCancelled()) {
         LivingEntity triggerEntity = event.getEntity();
         if (triggerEntity instanceof LivingEntity) {
            if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(triggerEntity)) {
               AbstractEntity triggerEntityx = BukkitAdapter.adapt(triggerEntity);
               SkillCaster caster = this.getPlugin().getSkillManager().getCaster(triggerEntityx);
               if (caster != null) {
                  SimpleEventSkillMetadata<EntityResurrectEvent> triggerMetadata = SkillTriggerMetadata.simpleEvent((ignored, datax) -> {});
                  SkillMetadata data = this.eventBus()
                     .buildSkillMetadata(
                        SkillTriggers.TOTEM_POP, event, triggerMetadata, caster, triggerEntityx, BukkitAdapter.adapt(triggerEntity.getLocation()), true
                     );
                  TriggeredSkill ts = this.eventBus().processTriggerMechanics(data, triggerMetadata);
                  if (ts.getCancelled()) {
                     event.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   void onEffectTrigger(EntityPotionEffectEvent event) {
      if (((org.bukkit.event.entity.EntityEvent)event).getEntity() instanceof LivingEntity living) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(living)) {
            AbstractEntity triggerEntity = BukkitAdapter.adapt(living);
            SkillCaster caster = this.getPlugin().getSkillManager().getCaster(triggerEntity);
            if (caster != null) {
               AbstractLocation origin = BukkitAdapter.adapt(living.getLocation());
               switch (event.getAction()) {
                  case ADDED:
                     this.runEffectApplyTrigger(event, caster, triggerEntity, origin);
                     break;
                  case CHANGED:
                     if (event.getOldEffect() != null) {
                        this.runEffectRemoveTrigger(event, caster, triggerEntity, origin);
                        if (event.isCancelled()) {
                           return;
                        }
                     }

                     if (event.getNewEffect() != null) {
                        this.runEffectApplyTrigger(event, caster, triggerEntity, origin);
                     }
                     break;
                  case REMOVED:
                  case CLEARED:
                     this.runEffectRemoveTrigger(event, caster, triggerEntity, origin);
               }
            }
         }
      }
   }

   void onEffectApplyTrigger(EntityPotionEffectEvent event) {
      Action action = event.getAction();
      if (action == Action.ADDED || action == Action.CHANGED) {
         if (event.getNewEffect() != null) {
            if (((org.bukkit.event.entity.EntityEvent)event).getEntity() instanceof LivingEntity living) {
               if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(living)) {
                  AbstractEntity triggerEntity = BukkitAdapter.adapt(living);
                  SkillCaster caster = this.getPlugin().getSkillManager().getCaster(triggerEntity);
                  if (caster != null) {
                     AbstractLocation origin = BukkitAdapter.adapt(living.getLocation());
                     this.runEffectApplyTrigger(event, caster, triggerEntity, origin);
                  }
               }
            }
         }
      }
   }

   void onEffectRemoveTrigger(EntityPotionEffectEvent event) {
      Action action = event.getAction();
      if (action == Action.REMOVED || action == Action.CLEARED || action == Action.CHANGED) {
         if (event.getOldEffect() != null) {
            if (((org.bukkit.event.entity.EntityEvent)event).getEntity() instanceof LivingEntity living) {
               if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(living)) {
                  AbstractEntity triggerEntity = BukkitAdapter.adapt(living);
                  SkillCaster caster = this.getPlugin().getSkillManager().getCaster(triggerEntity);
                  if (caster != null) {
                     AbstractLocation origin = BukkitAdapter.adapt(living.getLocation());
                     this.runEffectRemoveTrigger(event, caster, triggerEntity, origin);
                  }
               }
            }
         }
      }
   }

   private void runEffectApplyTrigger(EntityPotionEffectEvent event, SkillCaster caster, AbstractEntity triggerEntity, AbstractLocation origin) {
      EffectApplyMetadata triggerMetadata = new EffectApplyMetadata(event, triggerEntity);
      SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.EFFECT_APPLY, triggerMetadata, caster, triggerEntity, origin, true);
      TriggeredSkill ts = this.eventBus().processTriggerMechanics(data, triggerMetadata);
      if (ts.getCancelled()) {
         event.setCancelled(true);
      }
   }

   private void runEffectRemoveTrigger(EntityPotionEffectEvent event, SkillCaster caster, AbstractEntity triggerEntity, AbstractLocation origin) {
      EffectRemoveMetadata triggerMetadata = new EffectRemoveMetadata(event, triggerEntity);
      SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.EFFECT_REMOVE, triggerMetadata, caster, triggerEntity, origin, true);
      TriggeredSkill ts = this.eventBus().processTriggerMechanics(data, triggerMetadata);
      if (ts.getCancelled()) {
         event.setCancelled(true);
      }
   }

   void onBucketEntityFill(PlayerBucketEntityEvent event) {
      this.getPlugin().getMobManager().getActiveMob(event.getEntity().getUniqueId()).ifPresent(mob -> {
         SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.BUCKET, mob, mob.getEntity(), mob.getLocation(), false);
         TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
         if (ts.getCancelled()) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
         }
      });
   }

   void onMounted(EntityMountEvent event) {
      this.handleMountTrigger(SkillTriggers.MOUNT, event, event.getEntity(), event.getMount());
   }

   void onDismounted(EntityDismountEvent event) {
      this.handleMountTrigger(SkillTriggers.DISMOUNTED, event, event.getEntity(), event.getDismounted());
   }

   private void handleMountTrigger(SkillTrigger trigger, Cancellable event, Entity rider, Entity vehicle) {
      this.getPlugin().getMobManager().getActiveMob(vehicle.getUniqueId()).ifPresent(mob -> {
         SkillMetadata data = this.eventBus().buildSkillMetadata(trigger, mob, BukkitAdapter.adapt(rider), mob.getLocation(), false);
         if (this.eventBus().processTriggerMechanics(data).getCancelled()) {
            event.setCancelled(true);
         }
      });
   }

   private void subscribeLegacyMount(String className, SkillTrigger trigger, String vehicleGetter) {
      Class<? extends EntityEvent> eventClass;
      try {
         eventClass = Class.forName(className).asSubclass(EntityEvent.class);
      } catch (ClassNotFoundException e) {
         return;
      }

      Events.subscribe(eventClass).handler(event -> {
         Entity vehicle;
         try {
            vehicle = (Entity)event.getClass().getMethod(vehicleGetter).invoke(event);
         } catch (ReflectiveOperationException var6x) {
            throw new RuntimeException("Failed to read " + vehicleGetter + "() from " + event.getClass().getName(), var6x);
         }

         this.handleMountTrigger(trigger, (Cancellable)event, event.getEntity(), vehicle);
      }).bindWith(this);
   }

   void onBucketFill(PlayerBucketEvent event) {
      this.getPlugin().getMobManager().getActiveMob(event.getPlayer().getUniqueId()).ifPresent(mob -> {
         SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.BUCKET, mob, mob.getEntity(), mob.getLocation(), false);
         TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
         if (ts.getCancelled()) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
         }
      });
      Entity targetEntity = event.getPlayer().getTargetEntity(5, true);
      if (targetEntity == null) {
         Location pos = event.getBlock().getLocation();
         Iterator<Entity> entityIterator = pos.getWorld()
            .getNearbyEntities(pos, 3.0, 3.0, 3.0, entity -> entity instanceof Cow || entity instanceof MushroomCow)
            .iterator();
         if (entityIterator.hasNext()) {
            targetEntity = entityIterator.next();
         }
      }

      if (targetEntity != null) {
         this.getPlugin().getMobManager().getActiveMob(targetEntity.getUniqueId()).ifPresent(mob -> {
            AbstractPlayer player = BukkitAdapter.adapt(event.getPlayer());
            SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.BUCKET, mob, player, mob.getLocation(), false);
            TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
            if (ts.getCancelled()) {
               event.setCancelled(true);
               event.getPlayer().updateInventory();
            }
         });
      }
   }

   void onShearEntity(PlayerShearEntityEvent event) {
      if (event.getEntity() instanceof Sheep sheep) {
         this.getPlugin()
            .getMobManager()
            .getActiveMob(sheep.getUniqueId())
            .ifPresent(
               mob -> {
                  SkillMetadata data = this.eventBus()
                     .buildSkillMetadata(SkillTriggers.SHEARED, mob, BukkitAdapter.adapt(event.getPlayer()), mob.getLocation(), false);
                  TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
                  if (ts.getCancelled()) {
                     event.setCancelled(true);
                     event.getPlayer().updateInventory();
                  }
               }
            );
      }
   }

   void onLeashEntity(PlayerLeashEntityEvent event) {
      this.getPlugin().getMobManager().getActiveMob(event.getEntity().getUniqueId()).ifPresent(mob -> {
         SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.LEASH, mob, BukkitAdapter.adapt(event.getPlayer()), mob.getLocation(), true);
         if (this.eventBus().processTriggerMechanics(data).getCancelled()) {
            event.setCancelled(true);
         }
      });
   }

   void onUnleashEntity(EntityUnleashEvent event) {
      this.getPlugin().getMobManager().getActiveMob(event.getEntity().getUniqueId()).ifPresent(mob -> {
         AbstractPlayer trigger = event instanceof PlayerUnleashEntityEvent playerEvent ? BukkitAdapter.adapt(playerEvent.getPlayer()) : null;
         SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.UNLEASH, mob, trigger, mob.getLocation(), true);
         if (this.eventBus().processTriggerMechanics(data).getCancelled()) {
            event.setCancelled(true);
         }
      });
   }

   void onWorldChange(EntityPortalEvent event) {
      this.getPlugin()
         .getMobManager()
         .getActiveMob(event.getEntity().getUniqueId())
         .ifPresent(
            mob -> {
               if (event.getTo() != null) {
                  SkillMetadata data = this.eventBus()
                     .buildSkillMetadata(SkillTriggers.CHANGE_WORLD, event, BukkitTriggerMetadata.entityPortal(), mob, mob.getEntity(), null, true);
                  TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
                  if (ts.getCancelled()) {
                     event.setCancelled(true);
                  }
               }
            }
         );
   }

   private void onMerchantTrade(PlayerTradeEvent event) {
      this.getPlugin().getMobManager().getActiveMob(event.getVillager().getUniqueId()).ifPresent(am -> {
         SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.TRADE, am, BukkitAdapter.adapt(event.getPlayer()), null, false);
         TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
         if (ts.getCancelled()) {
            event.setCancelled(true);
         }
      });
   }

   public void onEntityBreed(EntityBreedEvent event) {
      AbstractEntity mother = BukkitAdapter.adapt(event.getMother());
      AbstractEntity father = BukkitAdapter.adapt(event.getFather());
      AbstractEntity breeder = BukkitAdapter.adapt(event.getBreeder());
      if (this.plugin.getMobManager().isActiveMob(mother)) {
         ActiveMob aMother = this.plugin.getMobManager().getMythicMobInstance(mother);
         if (aMother == null) {
            return;
         }

         SkillMetadata data = this.eventBus()
            .buildSkillMetadata(SkillTriggers.BREED, event, BukkitTriggerMetadata.entityBreed(), aMother, breeder, aMother.getLocation(), false);
         TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
         if (ts.getCancelled()) {
            event.setCancelled(true);
         }
      }

      if (this.plugin.getMobManager().isActiveMob(father)) {
         ActiveMob aFather = this.plugin.getMobManager().getMythicMobInstance(father);
         if (aFather == null) {
            return;
         }

         SkillMetadata data = this.eventBus()
            .buildSkillMetadata(SkillTriggers.BREED, event, BukkitTriggerMetadata.entityBreed(), aFather, breeder, aFather.getLocation(), false);
         TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
         if (ts.getCancelled()) {
            event.setCancelled(true);
         }
      }
   }

   public void onCreeperPrimeTrigger(CreeperIgniteEvent event) {
      MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "CreeperIgniteEvent fired");
      Entity l = event.getEntity();
      if (this.getPlugin().getMobManager().isActiveMob(l.getUniqueId())) {
         ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(l));
         SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.PRIME, am, am.getEntity(), null, true);
         TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
         if (ts.getCancelled()) {
            event.setCancelled(true);
         }
      }
   }

   public void onCreeperChargedTrigger(CreeperPowerEvent event) {
      MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "CreeperPowerEvent fired");
      Entity l = event.getEntity();
      if (this.getPlugin().getMobManager().isActiveMob(l.getUniqueId())) {
         ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(l));
         SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.CREEPER_CHARGE, am, am.getEntity(), null, true);
         TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
         if (ts.getCancelled()) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onInteractTrigger(PlayerInteractAtEntityEvent e) {
      MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "PlayerInteractAtEntityEvent fired");
      Entity clickedEntity = e.getRightClicked();
      if (MythicBukkit.inst().getMobManager().isActiveMob(clickedEntity.getUniqueId())) {
         ActiveMob am = this.getPlugin().getMobManager().getActiveMob(clickedEntity.getUniqueId()).get();
         Player player = e.getPlayer();
         if (e.getHand() == EquipmentSlot.HAND) {
            MythicMobInteractEvent event = new MythicMobInteractEvent(e.getPlayer(), am);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.INTERACT, am, BukkitAdapter.adapt(player), null, true);
               if (this.getPlugin().getCompatibility().getModelEngine().isPresent() && clickedEntity.hasMetadata("hitbox")) {
                  Optional<Object> maybeHitboxData = BukkitAdapter.adapt(clickedEntity).getMetadata("hitbox");
                  if (maybeHitboxData.isPresent()) {
                     String megHitbox = (String)maybeHitboxData.get();
                     data.getVariables().putString("hitbox", megHitbox);
                  }
               }

               TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
               if (ts.getCancelled() || !am.getType().getIsInteractable()) {
                  e.setCancelled(true);
                  UUID playerId = player.getUniqueId();
                  this.pendingInteractCancels.add(playerId);
                  Schedulers.sync().runLater(() -> this.pendingInteractCancels.remove(playerId), 2L);
                  if (e.getRightClicked().getType().equals(EntityType.VILLAGER)) {
                     Villager v = (Villager)e.getRightClicked();
                     if (player.getOpenInventory().getTopInventory().equals(v.getInventory())) {
                        player.getOpenInventory().close();
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onInteractTriggerCancel(PlayerInteractEntityEvent e) {
      if (!(e instanceof PlayerInteractAtEntityEvent)) {
         if (e.getHand() == EquipmentSlot.HAND) {
            if (MythicBukkit.inst().getMobManager().isActiveMob(e.getRightClicked().getUniqueId())) {
               if (this.pendingInteractCancels.remove(e.getPlayer().getUniqueId())) {
                  e.setCancelled(true);
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onTameTrigger(EntityTameEvent event) {
      MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "EntityTameEvent fired");
      LivingEntity entity = event.getEntity();
      if (this.getPlugin().getMobManager().isActiveMob(entity.getUniqueId())) {
         Player player = (Player)event.getOwner();
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(entity));
         if (am.getType().getMythicEntity() instanceof TameableEntityType) {
            TameableProperty prop = ((TameableEntityType)am.getType().getMythicEntity()).getTameableProperty();
            if (!prop.isTameable()) {
               event.setCancelled(true);
               Text.sendMessage(player, "<red>That mob cannot be tamed!");
               player.playSound(player.getLocation(), "entity.villager.no", 1.0F, 1.0F);
               return;
            }
         }

         SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.TAME, am, BukkitAdapter.adapt(player), null, false);
         TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
         if (ts.getCancelled()) {
            event.setCancelled(true);
         } else {
            Schedulers.of(entity).runLater(() -> {
               am.getType().applyMobOptions(am, am.getLevel());
               am.getType().applyMobVolatileOptions(am);
            }, 5L);
         }
      }
   }

   public void onDeathTrigger(EntityDeathEvent event) {
      if (!ServerVersion.isPaper() || !event.isCancelled()) {
         LivingEntity bukkitEntiity = event.getEntity();
         if (bukkitEntiity == null) {
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!                                                  !!!!!");
            MythicLogger.error("!!!!!     EntityDeathEvent.getEntity() is null????     !!!!!");
            MythicLogger.error("!!!!!     Executing rm -rf                             !!!!!");
            MythicLogger.error("!!!!!                                                  !!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            MythicLogger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
         } else {
            boolean dropLoot = true;
            AbstractEntity killedEntity = BukkitAdapter.adapt(bukkitEntiity);
            ActiveMob resolved = this.getPlugin().getMobManager().getMythicMobInstance(killedEntity);
            if (resolved == null) {
               resolved = this.getPlugin().getMobManager().reconstructForDeath(killedEntity).orElse(null);
               if (resolved == null) {
                  applyOrphanPreventOtherDrops(
                     event, this.getPlugin().getMobManager().getMythicType(bukkitEntiity).orElse(null), this.getPlugin().getMobManager()::getMythicMob
                  );
               }
            }

            ActiveMob am = resolved;
            if (am != null) {
               MythicMob mm = am.getType();
               LivingEntity killer = this.getPlugin().getDropManager().getKiller(event);
               AbstractEntity aKiller = killer == null ? null : BukkitAdapter.adapt(killer);
               SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.DEATH, am, aKiller, null, true);
               TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
               if (ts.getCancelled() && ServerVersion.isPaper()) {
                  event.setReviveHealth(am.getType().getReviveHealth(am));
                  event.setCancelled(true);
                  return;
               }

               if (killer != null && !(killer instanceof Player)) {
                  ActiveMob amkiller = this.getPlugin().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(killer));
                  if (amkiller != null && !killedEntity.isPlayer() && amkiller.getType().getPreventMobKillDrops()) {
                     event.getDrops().clear();
                     dropLoot = false;
                  }
               }

               if (mm.getPreventOtherDrops()) {
                  event.getDrops().clear();
                  event.setDroppedExp(0);
               }

               if (mm.getDropTable() != null) {
                  if (!am.getType().isDropsPerPlayer()) {
                     DropMetadata metadata = new DropMetadataImpl(am, aKiller);
                     LootBag lootBag = mm.getDropTable().generate(metadata);
                     MythicMobLootDropEvent mythicLootEvent = new MythicMobLootDropEvent(am, killer, lootBag);
                     MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Calling MythicMobLootDropEvent for " + am.getType().getInternalName());
                     Bukkit.getServer().getPluginManager().callEvent(mythicLootEvent);
                     if (dropLoot) {
                        this.getPlugin().getDropManager().handleEntityLootDrops(event, lootBag, metadata, am);
                     } else {
                        event.setDroppedExp(0);
                        event.getDrops().clear();
                     }
                  } else {
                     DamageRecord record = am.getDamageRecord();
                     if (record != null) {
                        record.getDamagers().forEach(uuid -> {
                           Player damager = Bukkit.getPlayer(uuid);
                           if (damager != null) {
                              DropMetadata metadata = new DropMetadataImpl(am, BukkitAdapter.adapt(damager));
                              LootBag lootBag = mm.getDropTable().generate(metadata);
                              MythicMobLootDropEvent mythicLootEventx = new MythicMobLootDropEvent(am, damager, lootBag);
                              MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Calling MythicMobLootDropEvent for " + am.getType().getInternalName());
                              Bukkit.getServer().getPluginManager().callEvent(mythicLootEventx);
                              this.getPlugin().getDropManager().handleEntityLootDrops(event, lootBag, metadata, am);
                           }
                        });
                     } else {
                        event.setDroppedExp(0);
                        event.getDrops().clear();
                     }
                  }
               }

               MythicMobDeathEvent mythicDeathEvent = new MythicMobDeathEvent(am, killer, event.getDrops());
               MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Calling MythicMobDeathEvent for " + am.getType().getInternalName());
               Bukkit.getServer().getPluginManager().callEvent(mythicDeathEvent);
               am.setDead();
               MythicSpawner deadSpawner = am.getSpawner();
               if (deadSpawner != null) {
                  deadSpawner.markMobDead(am);
               }
            } else {
               LivingEntity killerLE = this.getPlugin().getDropManager().getKiller(event);
               if (killerLE != null) {
                  ActiveMob amkiller = this.getPlugin().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(killerLE));
                  if (amkiller != null && !killedEntity.isPlayer() && amkiller.getType().getPreventMobKillDrops()) {
                     event.getDrops().clear();
                     event.setDroppedExp(0);
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Killed by entity with PreventKillDrops: true, cancelling drops!");
                  }
               }
            }

            this.getPlugin().getMobManager().getActiveMobsInCombat().forEach(mob -> Schedulers.runOnRegion(mob.getEntity().getBukkitEntity(), () -> {
               if (mob.getEntity().isValid() && mob.getType().usesThreatTable()) {
                  mob.getThreatTable().observeDeath(killedEntity);
               }
            }));
         }
      }
   }

   public void onShootTrigger(EntityShootBowEvent event) {
      LivingEntity shooter = event.getEntity();
      if (!(shooter instanceof Player)) {
         if (!this.getPlugin().getMobManager().isIgnoredEntity(shooter.getUniqueId())) {
            if (this.getPlugin().getMobManager().isMythicMob(shooter)) {
               ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(shooter);
               if (am != null) {
                  MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "MythicMob {0} shot a projectile!", am.getType().getInternalName());
                  SkillMetadata metadata = this.eventBus()
                     .buildSkillMetadata(SkillTriggers.SHOOT, BukkitTriggerMetadata.entityShootBow(), am, am.getEntity(), am.getLocation(), false);
                  TriggeredSkill ts = this.eventBus().processTriggerMechanics(metadata);
                  if (ts.getCancelled()) {
                     event.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   private static boolean isShooterProjectileContemplated(EntityType shooter, EntityType projectile) {
      Function<EntityType, Boolean> check = shooterProjectileContemplatedMap.get(shooter);
      return check == null ? false : check.apply(projectile);
   }

   public void onShootProjectileTrigger(ProjectileLaunchEvent event) {
      Projectile projectile = event.getEntity();
      EntityType projType = event.getEntityType();
      Entity shooter = (Entity)event.getEntity().getShooter();
      ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(shooter));
      if (isShooterProjectileContemplated(shooter.getType(), projType)) {
         if (am != null) {
            MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "MythicMob {0} shot a projectile!", am.getType().getInternalName());
            SimpleEventSkillMetadata<ProjectileLaunchEvent> triggerData = BukkitTriggerMetadata.projectileLaunch();
            SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.SHOOT, triggerData, am, am.getEntity(), am.getLocation(), false);
            TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
            if (ts.getCancelled()) {
               event.setCancelled(true);
            }
         }
      }
   }

   public void onProjectileHit(ProjectileHitEvent event) {
      Projectile projectile = event.getEntity();
      EntityType projType = event.getEntityType();
      Entity shooter = (Entity)event.getEntity().getShooter();
      if (isShooterProjectileContemplated(shooter.getType(), projType)) {
         SkillTrigger skillTrigger;
         SimpleEventSkillMetadata triggerData;
         if (event.getHitEntity() != null) {
            skillTrigger = SkillTriggers.PROJECTILE_HIT;
            triggerData = BukkitTriggerMetadata.projectileHit();
         } else {
            if (event.getHitBlock() == null) {
               return;
            }

            skillTrigger = SkillTriggers.PROJECTILE_LAND;
            triggerData = BukkitTriggerMetadata.projectileHit();
         }

         SkillCaster caster = this.getPlugin().getSkillManager().getCaster(BukkitAdapter.adapt(shooter));
         if (caster != null) {
            SkillMetadata data = this.eventBus().buildSkillMetadata(skillTrigger, triggerData, caster, caster.getEntity(), caster.getLocation(), false);
            TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
            if (ts.getCancelled()) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onChangeWorld(PlayerChangedWorldEvent event) {
      AbstractEntity entity = BukkitAdapter.adapt(event.getPlayer());
      MythicBukkit.inst().getMobManager().getActiveMobsInCombat().forEach(mob -> {
         if (mob.getEntity().isValid() && mob.getType().usesThreatTable()) {
            mob.getThreatTable().observeChangeWorld(entity);
         }
      });
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onPlayerKillTrigger(PlayerDeathEvent event) {
      MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "PlayerDeathEvent fired");
      Player p = event.getEntity();
      Entity k = p.getKiller();
      AbstractPlayer player = BukkitAdapter.adapt(p);
      if (this.getPlugin().getConfiguration().getKillMessagePrefix() != null && event.getDeathMessage() != null) {
         if (ServerVersion.isPaper()) {
            event.deathMessage(PaperHelper.parse(this.getPlugin().getConfiguration().getKillMessagePrefix()).append(event.deathMessage()));
         } else {
            event.setDeathMessage(this.getPlugin().getConfiguration().getKillMessagePrefix() + event.getDeathMessage());
         }
      }

      if (k == null) {
         EntityDamageEvent entityDamageEvent = event.getEntity().getLastDamageCause();
         if (entityDamageEvent != null && !entityDamageEvent.isCancelled() && entityDamageEvent instanceof EntityDamageByEntityEvent) {
            k = ((EntityDamageByEntityEvent)entityDamageEvent).getDamager();
         }
      }

      if (k instanceof LivingEntity killer) {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(killer));
         if (am != null) {
            SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.KILLPLAYER, am, player, am.getLocation(), false);
            TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
            am.incrementPlayerKills();
            if (am.getType().hasKillMessages()) {
               PlaceholderString km = am.getType().getKillMessage();
               String message = km.get(ts.getData(), player);
               if (ServerVersion.isPaper()) {
                  event.deathMessage(PaperHelper.parse(message));
               } else {
                  event.setDeathMessage(message);
               }
            }
         }
      } else if (k instanceof Projectile && ((Projectile)k).getShooter() instanceof LivingEntity shooter) {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(shooter));
         if (am != null) {
            SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.KILLPLAYER, am, player, am.getLocation(), false);
            TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
            am.incrementPlayerKills();
            if (am.getType().hasKillMessages()) {
               PlaceholderString km = am.getType().getKillMessage();
               String message = km.get(ts.getData(), player);
               if (ServerVersion.isPaper()) {
                  event.deathMessage(PaperHelper.parse(message));
               } else {
                  event.setDeathMessage(message);
               }
            }
         }
      }
   }

   @EventHandler
   public void onEntityExplode(EntityExplodeEvent e) {
      MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "EntityExplodeEvent fired");
      if (e.getEntity() != null) {
         if (MythicBukkit.inst().getMobManager().isActiveMob(e.getEntity().getUniqueId())) {
            ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(e.getEntity()));
            if (am.getType().getMythicEntity() instanceof BukkitCreeper) {
               LivingEntity l = (LivingEntity)e.getEntity();
               if (((BukkitCreeper)am.getType().getMythicEntity()).preventSuicide()) {
                  MythicLogger.debug(MythicLogger.DebugLevel.INFO, "MythicCreeper has preventSuicide == true, cloning creeper...");
                  ActiveMob replacement = am.getType().spawn(am.getEntity().getLocation(), am.getLevel(), SpawnReason.OTHER);
                  LivingEntity ll = (LivingEntity)BukkitAdapter.adapt(replacement.getEntity());
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

                  ActiveMob am2 = MythicBukkit.inst().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(ll));
                  if (am2 == null) {
                     MythicLogger.debug(MythicLogger.DebugLevel.INFO, "! Something prevented Creeper from cloning! PreventSuicide failed :(");
                     return;
                  }

                  am2.setStance(am.getStance());
                  am2.importPlayerKills(am.getPlayerKills());
                  am2.importThreatTable(am.getThreatTable());
                  SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.EXPLODE, am, null, null, false);
                  TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
                  if (ts.getCancelled()) {
                     e.setCancelled(true);
                  }
               } else {
                  SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.EXPLODE, am, null, null, false);
                  TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
                  if (ts.getCancelled()) {
                     e.setCancelled(true);
                  }
               }
            } else if (am.getType().getMythicEntity() instanceof BukkitTNT) {
               SkillMetadata data = this.eventBus().buildSkillMetadata(SkillTriggers.EXPLODE, am, null, null, false);
               TriggeredSkill ts = this.eventBus().processTriggerMechanics(data);
               if (ts.getCancelled()) {
                  e.setCancelled(true);
               }

               am.setDead();
               MythicBukkit.inst().getMobManager().unregisterActiveMob(am);
            }
         }
      }
   }

   static void applyOrphanPreventOtherDrops(EntityDeathEvent event, String typeName, Function<String, Optional<MythicMob>> typeResolver) {
      if (typeName != null) {
         typeResolver.apply(typeName).ifPresent(mm -> {
            if (mm.getPreventOtherDrops()) {
               event.getDrops().clear();
               event.setDroppedExp(0);
            }
         });
      }
   }

   static {
      shooterProjectileContemplatedMap.put(EntityType.BLAZE, projType -> projType == EntityType.SMALL_FIREBALL);
      shooterProjectileContemplatedMap.put(EntityType.ENDER_DRAGON, projType -> projType == EntityType.DRAGON_FIREBALL);
      shooterProjectileContemplatedMap.put(EntityType.GHAST, projType -> projType == EntityType.FIREBALL);
      shooterProjectileContemplatedMap.put(EntityType.LLAMA, projType -> projType == EntityType.LLAMA_SPIT);
      shooterProjectileContemplatedMap.put(EntityType.WITHER, projType -> projType == EntityType.WITHER_SKULL);
      shooterProjectileContemplatedMap.put(EntityType.SHULKER, projType -> projType == EntityType.SHULKER_BULLET);
      shooterProjectileContemplatedMap.put(EntityType.DROWNED, projType -> projType == EntityType.TRIDENT);
      shooterProjectileContemplatedMap.put(VolatileEntityType.SNOW_GOLEM, projType -> projType == EntityType.SNOWBALL);
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21)) {
         shooterProjectileContemplatedMap.put(EntityType.BREEZE, projType -> projType == EntityType.BREEZE_WIND_CHARGE || projType == EntityType.WIND_CHARGE);
      }
   }
}
