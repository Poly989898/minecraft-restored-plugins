package io.lumine.mythic.lib.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.api.stat.handler.AttributeStatHandler;
import io.lumine.mythic.lib.api.stat.handler.DelegateStatHandler;
import io.lumine.mythic.lib.api.stat.handler.MovementSpeedStatHandler;
import io.lumine.mythic.lib.api.stat.handler.StatHandler;
import io.lumine.mythic.lib.util.ConfigFile;
import io.lumine.mythic.lib.version.VMaterial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatManager {
   private final Map<String, StatHandler> handlers = new HashMap<>();
   private final Map<Attribute, Double> playerDefaultBaseValues = new HashMap<>();

   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.handlers.clear();
      } else {
         UtilityMethods.loadDefaultFile("", "stats.yml");
      }

      ConfigurationSection statsConfig = new ConfigFile("stats").getConfig();

      try {
         this.registerStat(new AttributeStatHandler(statsConfig, Attribute.ARMOR, "ARMOR", 0.0, Material.IRON_CHESTPLATE, "Armor bonus of an Entity."));
         this.registerStat(
            new AttributeStatHandler(
               statsConfig, Attribute.ARMOR_TOUGHNESS, "ARMOR_TOUGHNESS", 0.0, Material.GOLDEN_CHESTPLATE, "Armor toughness bonus of an Entity."
            )
         );
         this.registerStat(
            new AttributeStatHandler(statsConfig, Attribute.ATTACK_DAMAGE, "ATTACK_DAMAGE", 1.0, Material.IRON_SWORD, "Attack damage of an Entity.")
         );
         this.registerStat(
            new AttributeStatHandler(statsConfig, Attribute.ATTACK_SPEED, "ATTACK_SPEED", 4.0, Material.LIGHT_GRAY_DYE, "Attack speed of an Entity.")
         );
         this.registerStat(
            new AttributeStatHandler(
               statsConfig, Attribute.KNOCKBACK_RESISTANCE, "KNOCKBACK_RESISTANCE", 0.0, Material.TNT_MINECART, "Resistance of an Entity to knockback."
            )
         );
         this.registerStat(new AttributeStatHandler(statsConfig, Attribute.LUCK, "LUCK", 0.0, VMaterial.GRASS_BLOCK.get(), "Luck bonus of an Entity."));
         this.registerStat(
            new AttributeStatHandler(statsConfig, Attribute.MAX_HEALTH, "MAX_HEALTH", 20.0, Material.APPLE, "Maximum health of an Entity.")
         );
         StatHandler msStatHandler = new MovementSpeedStatHandler(statsConfig);
         this.registerStat(msStatHandler);
         this.registerStat(new DelegateStatHandler(statsConfig, "SPEED_MALUS_REDUCTION", msStatHandler));
         if (MythicLib.plugin.getVersion().isAbove(1, 20, 2)) {
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig, Attribute.MAX_ABSORPTION, "MAX_ABSORPTION", 0.0, Material.GOLDEN_APPLE, "Max amount of absorption hearts."
               )
            );
         }

         if (MythicLib.plugin.getVersion().isAbove(1, 20, 5)) {
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig, Attribute.BLOCK_BREAK_SPEED, "BLOCK_BREAK_SPEED", 1.0, Material.IRON_PICKAXE, "Speed of breaking blocks."
               )
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig,
                  Attribute.BLOCK_INTERACTION_RANGE,
                  "BLOCK_INTERACTION_RANGE",
                  4.5,
                  Material.SPYGLASS,
                  "How far players may break or interact with blocks."
               )
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig,
                  Attribute.ENTITY_INTERACTION_RANGE,
                  "ENTITY_INTERACTION_RANGE",
                  3.0,
                  Material.SPYGLASS,
                  "How far players may hit or interact with entities."
               )
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig,
                  Attribute.FALL_DAMAGE_MULTIPLIER,
                  "FALL_DAMAGE_MULTIPLIER",
                  1.0,
                  Material.GOLDEN_APPLE,
                  "Max amount of absorption hearts."
               )
            );
            this.registerStat(new AttributeStatHandler(statsConfig, Attribute.GRAVITY, "GRAVITY", 0.08, Material.STONE, "How strong gravity is."));
            this.registerStat(
               new AttributeStatHandler(statsConfig, Attribute.JUMP_STRENGTH, "JUMP_STRENGTH", 0.42, Material.FEATHER, "How high you can jump.")
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig,
                  Attribute.SAFE_FALL_DISTANCE,
                  "SAFE_FALL_DISTANCE",
                  3.0,
                  Material.RED_BED,
                  "How high you can drop from without fall damage."
               )
            );
            this.registerStat(new AttributeStatHandler(statsConfig, Attribute.SCALE, "SCALE", 1.0, Material.GUARDIAN_SPAWN_EGG, "Size of an entity."));
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig, Attribute.STEP_HEIGHT, "STEP_HEIGHT", 0.6, Material.OAK_SLAB, "How high you can climb blocks when walking."
               )
            );
         }

         if (MythicLib.plugin.getVersion().isAbove(1, 21)) {
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig, Attribute.BURNING_TIME, "BURNING_TIME", 1.0, Material.COOKED_BEEF, "A factor for increasing/reducing mining speed"
               )
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig,
                  Attribute.EXPLOSION_KNOCKBACK_RESISTANCE,
                  "EXPLOSION_KNOCKBACK_RESISTANCE",
                  0.0,
                  Material.OBSIDIAN,
                  "Resistance to knockback due to explosions."
               )
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig,
                  Attribute.MINING_EFFICIENCY,
                  "MINING_EFFICIENCY",
                  0.0,
                  Material.IRON_PICKAXE,
                  "A factor for increasing/reducing mining speed"
               )
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig,
                  Attribute.MOVEMENT_EFFICIENCY,
                  "MOVEMENT_EFFICIENCY",
                  0.0,
                  Material.SOUL_SAND,
                  "Movement speed factor when walking on blocks that slow down movement."
               )
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig,
                  Attribute.OXYGEN_BONUS,
                  "OXYGEN_BONUS",
                  0.0,
                  Material.GLASS_BOTTLE,
                  "Determines the chance not to use up air when underwater."
               )
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig, Attribute.SNEAKING_SPEED, "SNEAKING_SPEED", 0.3, Material.LEATHER_BOOTS, "Movement speed when sneaking."
               )
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig,
                  Attribute.SUBMERGED_MINING_SPEED,
                  "SUBMERGED_MINING_SPEED",
                  0.2,
                  Material.IRON_PICKAXE,
                  "Mining speed factor when submerged."
               )
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig,
                  Attribute.SWEEPING_DAMAGE_RATIO,
                  "SWEEPING_DAMAGE_RATIO",
                  0.0,
                  Material.IRON_SWORD,
                  "Damage ratio when performing sweep melee attacks."
               )
            );
            this.registerStat(
               new AttributeStatHandler(
                  statsConfig,
                  Attribute.WATER_MOVEMENT_EFFICIENCY,
                  "WATER_MOVEMENT_EFFICIENCY",
                  0.0,
                  Material.WATER_BUCKET,
                  "Movement speed factor when submerged."
               )
            );
         }
      } catch (Exception exception) {
         MythicLib.plugin.getLogger().log(Level.WARNING, "Could not load default stat handlers:");
         exception.printStackTrace();
      }

      for (String key : this.collectReferencedStats(statsConfig)) {
         try {
            String stat = UtilityMethods.enumName(key);
            this.handlers.putIfAbsent(stat, new StatHandler(statsConfig, stat));
         } catch (RuntimeException exception) {
            MythicLib.plugin.getLogger().log(Level.WARNING, "Could not load stat handler '" + key + "': " + exception.getMessage());
         }
      }
   }

   @NotNull
   private Iterable<String> collectReferencedStats(ConfigurationSection config) {
      List<String> keys = new ArrayList<>();

      for (String key : config.getKeys(false)) {
         keys.addAll(config.getConfigurationSection(key).getKeys(false));
      }

      return keys;
   }

   @NotNull
   public static String format(String stat, MMOPlayerData player) {
      return player.getStatMap().getInstance(stat).formatFinal();
   }

   @NotNull
   public static String format(String stat, double value) {
      StatHandler handler = MythicLib.plugin.getStats().handlers.get(stat);
      return (handler == null ? MythicLib.plugin.getMMOConfig().decimal : handler.getDecimalFormat()).format(value);
   }

   public void registerStat(@NotNull StatHandler handler, String... aliases) {
      Validate.notNull(handler, "StatHandler cannot be null");
      this.handlers.put(handler.getStat(), handler);

      for (String alias : aliases) {
         this.handlers.put(alias, handler);
      }

      if (handler instanceof AttributeStatHandler) {
         this.playerDefaultBaseValues.put(((AttributeStatHandler)handler).getAttribute(), ((AttributeStatHandler)handler).getPlayerDefaultBase());
      }
   }

   @Nullable
   public Double getPlayerDefaultBaseValue(@NotNull Attribute attribute) {
      return this.playerDefaultBaseValues.get(attribute);
   }

   @NotNull
   public Optional<StatHandler> getHandler(String stat) {
      return Optional.ofNullable(this.handlers.get(stat));
   }

   public boolean isRegistered(String stat) {
      return this.handlers.containsKey(stat);
   }

   @NotNull
   public Set<String> getRegisteredStats() {
      return this.handlers.keySet();
   }

   @NotNull
   public Collection<StatHandler> getHandlers() {
      return this.handlers.values();
   }

   public void clearRegisteredStats(Predicate<StatHandler> filter) {
      Iterator<StatHandler> ite = this.handlers.values().iterator();

      while (ite.hasNext()) {
         if (filter.test(ite.next())) {
            ite.remove();
         }
      }
   }

   @Deprecated
   public void runUpdate(StatMap map, String stat) {
      map.getInstance(stat).update();
   }

   @Deprecated
   public void runUpdates(@NotNull StatMap map) {
      for (StatInstance ins : map.getInstances()) {
         ins.update();
      }
   }

   @Deprecated
   public void runUpdate(@NotNull StatInstance instance) {
      instance.update();
   }

   @Deprecated
   public double getBaseValue(String stat, StatMap map) {
      StatHandler handler = this.handlers.get(stat);
      return handler == null ? 0.0 : handler.getBaseValue(map.getInstance(stat));
   }

   @Deprecated
   public double getBaseValue(StatInstance instance) {
      return instance.getBase();
   }

   @Deprecated
   public double getTotalValue(String stat, StatMap map) {
      return map.getStat(stat);
   }

   @Deprecated
   public double getTotalValue(StatInstance instance) {
      return instance.getTotal();
   }

   @Deprecated
   public void registerStat(String stat, StatHandler handler) {
      Validate.notNull(stat, "Stat cannot be null");
      Validate.notNull(handler, "StatHandler cannot be null");
      this.handlers.put(stat, handler);
   }
}
