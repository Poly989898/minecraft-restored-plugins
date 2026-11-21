package io.lumine.mythic.bukkit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractBiome;
import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractBlockMatcher;
import io.lumine.mythic.api.adapters.AbstractBossBar;
import io.lumine.mythic.api.adapters.AbstractEnchantment;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractFirework;
import io.lumine.mythic.api.adapters.AbstractItemMatcher;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractPotionEffect;
import io.lumine.mythic.api.adapters.AbstractTeleportData;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.adapters.ServerInterface;
import io.lumine.mythic.api.adapters.items.components.AbstractItemConsumableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemDeathProtectionComponent;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.bukkit.adapters.BukkitBlock;
import io.lumine.mythic.bukkit.adapters.BukkitBlockMatcher;
import io.lumine.mythic.bukkit.adapters.BukkitEnchantment;
import io.lumine.mythic.bukkit.adapters.BukkitFirework;
import io.lumine.mythic.bukkit.adapters.BukkitItemMatcher;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.adapters.BukkitPotionEffect;
import io.lumine.mythic.bukkit.adapters.BukkitWorld;
import io.lumine.mythic.bukkit.adapters.attributes.BukkitAttributable;
import io.lumine.mythic.bukkit.adapters.bossbars.AdventureBossBar;
import io.lumine.mythic.bukkit.adapters.item.NbtBukkitItemStack;
import io.lumine.mythic.bukkit.entities.BukkitBabyPigZombie;
import io.lumine.mythic.bukkit.entities.BukkitBabyPigZombieVillager;
import io.lumine.mythic.bukkit.entities.BukkitBabyPiglin;
import io.lumine.mythic.bukkit.entities.BukkitBabyZombie;
import io.lumine.mythic.bukkit.entities.BukkitBabyZombieVillager;
import io.lumine.mythic.bukkit.entities.BukkitPigZombie;
import io.lumine.mythic.bukkit.entities.BukkitPigZombieVillager;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.listeners.ChunkListeners;
import io.lumine.mythic.bukkit.listeners.EggListeners;
import io.lumine.mythic.bukkit.listeners.MobListeners;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.InvalidDrop;
import io.lumine.mythic.core.drops.droppables.CustomDrop;
import io.lumine.mythic.core.drops.droppables.DropTableDrop;
import io.lumine.mythic.core.drops.droppables.VanillaItemDrop;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.items.RandomTableItem;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BukkitBootstrap extends ReloadableModule<MythicBukkit> implements ServerInterface {
   private BukkitPlatformBridge platformBridge;
   private BukkitEntityLookup entityLookup;
   private MobListeners mobListeners;
   private ChunkListeners chunkListeners;
   private EggListeners eggListeners;
   private Map<EntityType, MythicEntityType> typeMap;

   public BukkitBootstrap(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      this.entityLookup = new BukkitEntityLookup(plugin);
      this.bind(this.mobListeners = new MobListeners(plugin));
      this.bind(this.chunkListeners = new ChunkListeners(plugin));
      this.bind(this.eggListeners = new EggListeners(plugin));

      try {
         if (ServerVersion.isPaper()) {
            Class<?> clazz = Class.forName("io.lumine.mythic.paper.PaperPlatformBridge");
            this.platformBridge = (BukkitPlatformBridge)clazz.getConstructor(BukkitBootstrap.class).newInstance(this);
         } else {
            Class<?> clazz = Class.forName("io.lumine.mythic.spigot.SpigotPlatformBridge");
            this.platformBridge = (BukkitPlatformBridge)clazz.getConstructor(BukkitBootstrap.class).newInstance(this);
         }
      } catch (Error | Exception e) {
         e.printStackTrace();
      }
   }

   @Override
   public void initialize() {
      this.typeMap = Maps.newEnumMap(EntityType.class);

      for (EntityType type : EntityType.values()) {
         MythicEntityType mythicType = MythicEntityType.get(type.toString());
         if (mythicType != null) {
            this.typeMap.put(type, mythicType);
         }
      }

      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_4)) {
         this.typeMap.forEach((typex, met) -> {
            if (typex != EntityType.UNKNOWN) {
               if (this.getPlugin().getVolatileCodeHandler().getEntityHandler().hasDefaultAttributes(typex)) {
                  BukkitAttributable attributable = this.getPlugin().getVolatileCodeHandler().getEntityHandler().getDefaultAttributes(typex);
                  met.initializeDefaultValues(attributable);
               }
            }
         });
      } else {
         Schedulers.sync().runLater(this::initializeMagicEntityValues, 1L);
      }
   }

   @Override
   public void unload() {
   }

   public BukkitItemStack wrapItem(ItemStack item) {
      if (item == null) {
         return null;
      } else {
         return (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? this.platformBridge.wrapItem(item) : new NbtBukkitItemStack(item));
      }
   }

   @Override
   public List<AbstractWorld> getWorlds() {
      List<AbstractWorld> wl = new ArrayList<>();

      for (World w : Bukkit.getWorlds()) {
         wl.add(new BukkitWorld(w));
      }

      return wl;
   }

   @Override
   public void dispatchCommand(String command) {
      Schedulers.ensureSync(() -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
   }

   @Override
   public AbstractPlayer getPlayer(UUID uuid) {
      Player player = Bukkit.getPlayer(uuid);
      return player == null ? null : BukkitAdapter.adapt(player);
   }

   @Override
   public AbstractPlayer getPlayer(String name) {
      Player player = Bukkit.getPlayerExact(name);
      return player == null ? null : BukkitAdapter.adapt(player);
   }

   @Override
   public MythicEntityType getMythicEntityType(EntityType type) {
      return this.typeMap.get(type);
   }

   @Override
   public AbstractEntity getEntity(UUID uuid) {
      return this.entityLookup.getEntity(uuid);
   }

   @Override
   public Collection<AbstractEntity> getEntities(AbstractWorld world) {
      if (!Bukkit.isPrimaryThread()) {
         return this.getPlugin().getVolatileCodeHandler().getWorldHandler().getEntities(world);
      } else {
         Collection<AbstractEntity> ret = Lists.newArrayList();
         World bukkitWorld = BukkitAdapter.adapt(world);

         for (Entity entity : bukkitWorld.getEntities()) {
            ret.add(BukkitAdapter.adapt(entity));
         }

         return ret;
      }
   }

   @Override
   public Collection<AbstractEntity> getLivingEntities(AbstractWorld world) {
      if (!Bukkit.isPrimaryThread()) {
         return this.getPlugin().getVolatileCodeHandler().getWorldHandler().getLivingEntities(world);
      } else {
         Collection<AbstractEntity> ret = Lists.newArrayList();
         World bukkitWorld = BukkitAdapter.adapt(world);

         for (LivingEntity entity : bukkitWorld.getLivingEntities()) {
            ret.add(BukkitAdapter.adapt(entity));
         }

         return ret;
      }
   }

   @Override
   public Collection<AbstractPlayer> getPlayers(AbstractWorld world) {
      if (!Bukkit.isPrimaryThread()) {
         return this.getPlugin().getVolatileCodeHandler().getWorldHandler().getPlayers(world);
      } else {
         Collection<AbstractPlayer> ret = Lists.newArrayList();
         World bukkitWorld = BukkitAdapter.adapt(world);

         for (Player entity : bukkitWorld.getPlayers()) {
            ret.add(BukkitAdapter.adapt(entity));
         }

         return ret;
      }
   }

   @Override
   public Collection<AbstractEntity> getEntities(Collection<UUID> uuids) {
      return this.entityLookup.getEntities(uuids);
   }

   @Override
   public AbstractWorld getWorld(UUID uuid) {
      World world = Bukkit.getWorld(uuid);
      return world == null ? null : BukkitAdapter.adapt(world);
   }

   @Override
   public AbstractWorld getWorld(String name) {
      World world = Bukkit.getWorld(name);
      return world == null ? null : new BukkitWorld(world);
   }

   @Override
   public AbstractBiome getBiome(String biome) {
      return this.getPlugin().getVolatileCodeHandler().getWorldHandler().getBiome(biome).orElseGet(null);
   }

   @Override
   public AbstractBiome getBiome(AbstractLocation location) {
      return this.getPlugin().getVolatileCodeHandler().getWorldHandler().getBiome(location);
   }

   @Override
   public Collection<AbstractBiome> getBiomes(String biomeSearch) {
      List<AbstractBiome> biomes = Lists.newArrayList();
      if (!biomeSearch.contains(":") && !biomeSearch.startsWith("*")) {
         biomeSearch = "minecraft:" + biomeSearch;
      }

      for (AbstractBiome biome : this.getPlugin().getVolatileCodeHandler().getWorldHandler().getBiomes()) {
         if (biome.matches(biomeSearch)) {
            biomes.add(biome);
         }
      }

      return biomes;
   }

   @Override
   public AbstractBlock getBlock(String name) {
      String nameUpper = name.toUpperCase(Locale.ROOT);
      if (nameUpper.startsWith("MMOITEMS")) {
         if (this.getPlugin().getCompatibility().getMMOItems().isEmpty()) {
            return new AbstractBlock.InvalidBlock();
         } else {
            String type = name.substring(9);
            int id = Integer.parseInt(type);
            return this.getPlugin().getCompatibility().getMMOItems().get().getCustomMMOBlock(id);
         }
      } else {
         try {
            BlockData blockData = Bukkit.createBlockData(name.toLowerCase());
            return new BukkitBlock(blockData);
         } catch (Error | Exception e) {
            return (this.getPlugin().getCompatibility().getCrucible().isPresent() && this.getPlugin().getItemManager().getItem(name).isPresent()
               ? this.getPlugin().getCompatibility().getCrucible().get().getCustomBlock(name)
               : new AbstractBlock.InvalidBlock());
         }
      }
   }

   @Override
   public AbstractBlock getBlock(AbstractLocation location) {
      return new BukkitBlock(BukkitAdapter.adapt(location).getBlock().getBlockData());
   }

   @Override
   public AbstractBlockMatcher getBlockMatcher(String name) {
      return new BukkitBlockMatcher(name);
   }

   @Override
   public AbstractBlockMatcher getBlockMatcher(Collection<String> name) {
      return new BukkitBlockMatcher(name);
   }

   @Override
   public List<AbstractPlayer> getOnlinePlayers() {
      List<AbstractPlayer> pl = new ArrayList<>();

      for (Player p : Bukkit.getServer().getOnlinePlayers()) {
         pl.add(BukkitAdapter.adapt(p));
      }

      return pl;
   }

   @Override
   public int getOnlinePlayerCount() {
      return Bukkit.getServer().getOnlinePlayers().size();
   }

   @Override
   public AbstractLocation newLocation(AbstractWorld w, double x, double y, double z) {
      Location l = new Location(BukkitAdapter.adapt(w), x, y, z);
      return BukkitAdapter.adapt(l);
   }

   @Override
   public AbstractLocation newLocation(AbstractWorld w, double x, double y, double z, float yaw, float pitch) {
      Location l = new Location(BukkitAdapter.adapt(w), x, y, z, yaw, pitch);
      return BukkitAdapter.adapt(l);
   }

   @Override
   public boolean isValidBiome(Object o) {
      return false;
   }

   @Override
   public AbstractBossBar createBossBar(String title, AbstractBossBar.BarColor color, AbstractBossBar.BarStyle style) {
      return new AdventureBossBar(title, 1.0F, color, style);
   }

   @Override
   public AbstractItemStack createItem(String type) {
      if (this.plugin.getCompatibility().getMMOItems().isPresent() && type.toLowerCase(Locale.ROOT).startsWith("mmoitems.")) {
         return BukkitAdapter.adapt(this.plugin.getCompatibility().getMMOItems().get().getCustomItem(type.substring(9)));
      } else {
         Optional<MythicItem> mythicItem = this.plugin.getItemManager().getItem(type);
         if (mythicItem.isPresent()) {
            return mythicItem.get().generateItemStack(1);
         } else {
            Material material = Material.matchMaterial(type.toUpperCase(Locale.ROOT));
            if (material != null) {
               return BukkitAdapter.adapt(material);
            } else {
               IDrop drop = this.plugin.getDropManager().getDrop("", type);
               if (drop != null && !(drop instanceof InvalidDrop)) {
                  if (drop instanceof CustomDrop customDrop) {
                     if (customDrop.getDrop().isEmpty()) {
                        throw new IllegalArgumentException("Could not adapt item stack of invalid custom drop type '" + type + "'");
                     }

                     drop = customDrop.getDrop().get();
                  }

                  if (drop instanceof VanillaItemDrop vanillaItemDrop) {
                     return vanillaItemDrop.getDrop(new DropMetadataImpl(null, null), 1.0);
                  } else if (drop instanceof DropTableDrop dropTableDrop) {
                     return new RandomTableItem(dropTableDrop.getDropTable());
                  } else {
                     throw new IllegalArgumentException("Could not adapt item stack of unknown or invalid type '" + type + "'");
                  }
               } else {
                  throw new IllegalArgumentException("Could not adapt item stack of unknown drop type '" + type + "'");
               }
            }
         }
      }
   }

   @Override
   public AbstractItemMatcher getItemMatcher(MythicLineConfig config, String conditionVar) {
      return new BukkitItemMatcher(config, conditionVar);
   }

   @Override
   public AbstractItemMatcher getItemMatcher(String type, boolean strict) {
      return new BukkitItemMatcher(type, strict);
   }

   @Override
   public AbstractItemMatcher getItemMatcher(Collection<String> types, boolean strict) {
      return new BukkitItemMatcher(types, strict);
   }

   @Override
   public AbstractEnchantment createEnchantment(String enchantmentName) {
      return new BukkitEnchantment(enchantmentName);
   }

   @Override
   public AbstractPotionEffect createPotionEffect(String effectType, int duration, int level, boolean ambient, boolean particles, boolean icon) {
      return new BukkitPotionEffect(effectType, duration, level, ambient, particles, icon);
   }

   @Override
   public MythicEntityType getMythicEntityType(Entity e) {
      String type = e.getType().toString();
      switch (type) {
         case "PIG_ZOMBIE":
         case "ZOMBIFIED_PIGLIN":
            if (BukkitBabyPigZombie.isInstanceOf(e)) {
               return MythicEntityType.BABY_PIG_ZOMBIE;
            }

            if (BukkitBabyPigZombieVillager.isInstanceOf(e)) {
               return MythicEntityType.BABY_PIG_ZOMBIE_VILLAGER;
            }

            if (BukkitPigZombie.isInstanceOf(e)) {
               return MythicEntityType.ZOMBIFIED_PIGLIN;
            }

            if (BukkitPigZombieVillager.isInstanceOf(e)) {
               return MythicEntityType.ZOMBIFIED_PIGLIN_VILLAGER;
            }
            break;
         case "PIGLIN":
            if (BukkitBabyPiglin.isInstanceOf(e)) {
               return MythicEntityType.BABY_PIGLIN;
            }
         case "ZOMBIE":
            if (BukkitBabyZombie.isInstanceOf(e)) {
               return MythicEntityType.BABY_ZOMBIE;
            }

            if (BukkitBabyZombieVillager.isInstanceOf(e)) {
               return MythicEntityType.BABY_ZOMBIE_VILLAGER;
            }
      }

      return MythicEntityType.get(type);
   }

   @Override
   public void handleMobDespawnEvent(ActiveMob mob) {
      Schedulers.of(mob.getEntity().getBukkitEntity()).run(() -> {
         MythicMobDespawnEvent mythicDespawnEvent = new MythicMobDespawnEvent(mob);
         Events.call(mythicDespawnEvent);
      });
   }

   @Override
   public AbstractFirework createFirework(String type, int duration, Collection<Color> colors, Collection<Color> fadeColors, boolean flicker, boolean trail) {
      return new BukkitFirework(type, duration, colors, fadeColors, flicker, trail);
   }

   public void initializeMagicEntityValues() {
      for (MythicEntityType mythicEntityType : MythicEntityType.values()) {
         if (mythicEntityType != MythicEntityType.CUSTOM) {
            try {
               mythicEntityType.initializeDefaultValues();
            } catch (Throwable e) {
               MythicLogger.error("Error initializing default entity magic values. Please report this.");
               e.printStackTrace();
            }
         }
      }
   }

   @Override
   public AbstractItemConsumableComponent createItemConsumableComponent(MythicItem mythicItem, MythicConfig config) {
      return this.platformBridge.createItemConsumableComponent(mythicItem, config);
   }

   @Override
   public AbstractItemDeathProtectionComponent createItemDeathProtectionComponent(MythicItem mythicItem, MythicConfig config) {
      return this.platformBridge.createItemDeathProtectionComponent(mythicItem, config);
   }

   @Override
   public AbstractTeleportData createTeleportData(MythicLineConfig mlc) {
      return this.platformBridge.createTeleportData(mlc);
   }
}
