package com.Zrips.CMI.Containers;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public final class CMIGameRule<T> {
  private static final HashMap<String, CMIGameRule<?>> registry = new HashMap<>();
  private static CMIGameRule<?>[] rules = new CMIGameRule[0];
  public static final CMIGameRule<Boolean> SPAWN_MOBS =
      new CMIGameRule<>("SPAWN_MOBS", "doMobSpawning", Boolean.class);
  public static final CMIGameRule<Boolean> NATURAL_HEALTH_REGENERATION =
      new CMIGameRule<>("NATURAL_HEALTH_REGENERATION", "naturalRegeneration", Boolean.class);
  public static final CMIGameRule<Boolean> MOB_GRIEFING =
      new CMIGameRule<>("MOB_GRIEFING", "mobGriefing", Boolean.class);
  public static final CMIGameRule<Boolean> MOB_DROPS =
      new CMIGameRule<>("MOB_DROPS", "doMobLoot", Boolean.class);
  public static final CMIGameRule<Boolean> KEEP_INVENTORY =
      new CMIGameRule<>("KEEP_INVENTORY", "keepInventory", Boolean.class);
  public static final CMIGameRule<Boolean> COMMAND_BLOCK_OUTPUT =
      new CMIGameRule<>("COMMAND_BLOCK_OUTPUT", "commandBlockOutput", Boolean.class);
  public static final CMIGameRule<Boolean> BLOCK_DROPS =
      new CMIGameRule<>("BLOCK_DROPS", "doTileDrops", Boolean.class);
  public static final CMIGameRule<Boolean> ADVANCE_WEATHER =
      new CMIGameRule<>("ADVANCE_WEATHER", "doWeatherCycle", Boolean.class);
  public static final CMIGameRule<Boolean> ADVANCE_TIME =
      new CMIGameRule<>("ADVANCE_TIME", "doDaylightCycle", Boolean.class);
  public static final CMIGameRule<Boolean> ENTITY_DROPS =
      new CMIGameRule<>("ENTITY_DROPS", "doEntityDrops", Boolean.class, Version.v1_8_R1);
  public static final CMIGameRule<Boolean> LOG_ADMIN_COMMANDS =
      new CMIGameRule<>("LOG_ADMIN_COMMANDS", "logAdminCommands", Boolean.class, Version.v1_8_R1);
  public static final CMIGameRule<Integer> RANDOM_TICK_SPEED =
      new CMIGameRule<>("RANDOM_TICK_SPEED", Integer.class, Version.v1_8_R1);
  public static final CMIGameRule<Boolean> REDUCED_DEBUG_INFO =
      new CMIGameRule<>("REDUCED_DEBUG_INFO", Boolean.class, Version.v1_8_R1);
  public static final CMIGameRule<Boolean> SHOW_DEATH_MESSAGES =
      new CMIGameRule<>("SHOW_DEATH_MESSAGES", Boolean.class, Version.v1_8_R1);
  public static final CMIGameRule<Boolean> SEND_COMMAND_FEEDBACK =
      new CMIGameRule<>("SEND_COMMAND_FEEDBACK", Boolean.class, Version.v1_8_R1);
  public static final CMIGameRule<Boolean> ELYTRA_MOVEMENT_CHECK =
      new CMIGameRule<>(
          "ELYTRA_MOVEMENT_CHECK", "disableElytraMovementCheck", Boolean.class, Version.v1_9_R1);
  public static final CMIGameRule<Integer> RESPAWN_RADIUS =
      new CMIGameRule<>("RESPAWN_RADIUS", "spawnRadius", Integer.class, Version.v1_9_R1);
  public static final CMIGameRule<Boolean> SPECTATORS_GENERATE_CHUNKS =
      new CMIGameRule<>("SPECTATORS_GENERATE_CHUNKS", Boolean.class, Version.v1_9_R1);
  public static final CMIGameRule<Integer> MAX_ENTITY_CRAMMING =
      new CMIGameRule<>(
          "MAX_ENTITY_CRAMMING", "maxEntityCramming", Integer.class, Version.v1_11_R1);
  public static final CMIGameRule<Boolean> LIMITED_CRAFTING =
      new CMIGameRule<>("LIMITED_CRAFTING", "doLimitedCrafting", Boolean.class, Version.v1_12_R1);
  public static final CMIGameRule<Integer> MAX_COMMAND_SEQUENCE_LENGTH =
      new CMIGameRule<>(
          "MAX_COMMAND_SEQUENCE_LENGTH", "maxCommandChainLength", Integer.class, Version.v1_12_R1);
  public static final CMIGameRule<Boolean> SHOW_ADVANCEMENT_MESSAGES =
      new CMIGameRule<>(
          "SHOW_ADVANCEMENT_MESSAGES", "announceAdvancements", Boolean.class, Version.v1_12_R1);
  public static final CMIGameRule<Boolean> RAIDS =
      new CMIGameRule<>("RAIDS", "disableRaids", Boolean.class, Version.v1_14_R1);
  public static final CMIGameRule<Boolean> DROWNING_DAMAGE =
      new CMIGameRule<>("DROWNING_DAMAGE", "drowningDamage", Boolean.class, Version.v1_15_R1);
  public static final CMIGameRule<Boolean> FALL_DAMAGE =
      new CMIGameRule<>("FALL_DAMAGE", "fallDamage", Boolean.class, Version.v1_15_R1);
  public static final CMIGameRule<Boolean> FIRE_DAMAGE =
      new CMIGameRule<>("FIRE_DAMAGE", "fireDamage", Boolean.class, Version.v1_15_R1);
  public static final CMIGameRule<Boolean> IMMEDIATE_RESPAWN =
      new CMIGameRule<>("IMMEDIATE_RESPAWN", "doImmediateRespawn", Boolean.class, Version.v1_15_R1);
  public static final CMIGameRule<Boolean> SPAWN_PATROLS =
      new CMIGameRule<>("SPAWN_PATROLS", "doPatrolSpawning", Boolean.class, Version.v1_15_R1);
  public static final CMIGameRule<Boolean> SPAWN_PHANTOMS =
      new CMIGameRule<>("SPAWN_PHANTOMS", "doInsomnia", Boolean.class, Version.v1_15_R1);
  public static final CMIGameRule<Boolean> SPAWN_WANDERING_TRADERS =
      new CMIGameRule<>(
          "SPAWN_WANDERING_TRADERS", "doTraderSpawning", Boolean.class, Version.v1_15_R1);
  public static final CMIGameRule<Boolean> FORGIVE_DEAD_PLAYERS =
      new CMIGameRule<>(
          "FORGIVE_DEAD_PLAYERS", "forgiveDeadPlayers", Boolean.class, Version.v1_16_R1);
  public static final CMIGameRule<Boolean> UNIVERSAL_ANGER =
      new CMIGameRule<>("UNIVERSAL_ANGER", Boolean.class, Version.v1_16_R1);
  public static final CMIGameRule<Boolean> FREEZE_DAMAGE =
      new CMIGameRule<>("FREEZE_DAMAGE", "freezeDamage", Boolean.class, Version.v1_17_R1);
  public static final CMIGameRule<Integer> PLAYERS_SLEEPING_PERCENTAGE =
      new CMIGameRule<>("PLAYERS_SLEEPING_PERCENTAGE", Integer.class, Version.v1_17_R1);
  public static final CMIGameRule<Boolean> SPAWN_WARDENS =
      new CMIGameRule<>("SPAWN_WARDENS", "doWardenSpawning", Boolean.class, Version.v1_19_R1);
  public static final CMIGameRule<Boolean> GLOBAL_SOUND_EVENTS =
      new CMIGameRule<>(
          "GLOBAL_SOUND_EVENTS", "globalSoundEvents", Boolean.class, Version.v1_19_R2);
  public static final CMIGameRule<Boolean> LAVA_SOURCE_CONVERSION =
      new CMIGameRule<>(
          "LAVA_SOURCE_CONVERSION", "lavaSourceConversion", Boolean.class, Version.v1_19_R2);
  public static final CMIGameRule<Integer> MAX_SNOW_ACCUMULATION_HEIGHT =
      new CMIGameRule<>(
          "MAX_SNOW_ACCUMULATION_HEIGHT",
          "snowAccumulationHeight",
          Integer.class,
          Version.v1_19_R2);
  public static final CMIGameRule<Boolean> MOB_EXPLOSION_DROP_DECAY =
      new CMIGameRule<>(
          "MOB_EXPLOSION_DROP_DECAY", "mobExplosionDropDecay", Boolean.class, Version.v1_19_R2);
  public static final CMIGameRule<Boolean> BLOCK_EXPLOSION_DROP_DECAY =
      new CMIGameRule<>(
          "BLOCK_EXPLOSION_DROP_DECAY", "blockExplosionDropDecay", Boolean.class, Version.v1_19_R2);
  public static final CMIGameRule<Integer> MAX_BLOCK_MODIFICATIONS =
      new CMIGameRule<>(
          "MAX_BLOCK_MODIFICATIONS",
          "commandModificationBlockLimit",
          Integer.class,
          Version.v1_19_R3);
  public static final CMIGameRule<Boolean> SPREAD_VINES =
      new CMIGameRule<>("SPREAD_VINES", "doVinesSpread", Boolean.class, Version.v1_19_R3);
  public static final CMIGameRule<Boolean> TNT_EXPLOSION_DROP_DECAY =
      new CMIGameRule<>("TNT_EXPLOSION_DROP_DECAY", Boolean.class, Version.v1_19_R3);
  public static final CMIGameRule<Boolean> WATER_SOURCE_CONVERSION =
      new CMIGameRule<>("WATER_SOURCE_CONVERSION", Boolean.class, Version.v1_19_R3);
  public static final CMIGameRule<Boolean> ENDER_PEARLS_VANISH_ON_DEATH =
      new CMIGameRule<>(
          "ENDER_PEARLS_VANISH_ON_DEATH",
          "enderPearlsVanishOnDeath",
          Boolean.class,
          Version.v1_20_R2);
  public static final CMIGameRule<Integer> MAX_COMMAND_FORKS =
      new CMIGameRule<>(
          "MAX_COMMAND_FORKS", "maxCommandForkCount", Integer.class, Version.v1_20_R3);
  public static final CMIGameRule<Integer> PLAYERS_NETHER_PORTAL_CREATIVE_DELAY =
      new CMIGameRule<>("PLAYERS_NETHER_PORTAL_CREATIVE_DELAY", Integer.class, Version.v1_20_R3);
  public static final CMIGameRule<Integer> PLAYERS_NETHER_PORTAL_DEFAULT_DELAY =
      new CMIGameRule<>("PLAYERS_NETHER_PORTAL_DEFAULT_DELAY", Integer.class, Version.v1_20_R3);
  public static final CMIGameRule<Boolean> PROJECTILES_CAN_BREAK_BLOCKS =
      new CMIGameRule<>("PROJECTILES_CAN_BREAK_BLOCKS", Boolean.class, Version.v1_20_R3);
  public static final CMIGameRule<Boolean> PLAYER_MOVEMENT_CHECK =
      new CMIGameRule<>(
          "PLAYER_MOVEMENT_CHECK", "disablePlayerMovementCheck", Boolean.class, Version.v1_21_R1);
  public static final CMIGameRule<Boolean> TNT_EXPLODES =
      new CMIGameRule<>("TNT_EXPLODES", Boolean.class, Version.v1_21_R4);
  public static final CMIGameRule<Boolean> LOCATOR_BAR =
      new CMIGameRule<>("LOCATOR_BAR", "locatorBar", Boolean.class, Version.v1_21_R5);
  public static final CMIGameRule<Boolean> PVP =
      new CMIGameRule<>("PVP", Boolean.class, Version.v1_21_R6);
  public static final CMIGameRule<Boolean> SPAWNER_BLOCKS_WORK =
      new CMIGameRule<>(
          "SPAWNER_BLOCKS_WORK", "spawnerBlocksEnabled", Boolean.class, Version.v1_21_R6);
  public static final CMIGameRule<Boolean> ALLOW_ENTERING_NETHER_USING_PORTALS =
      new CMIGameRule<>("ALLOW_ENTERING_NETHER_USING_PORTALS", Boolean.class, Version.v1_21_R6);
  public static final CMIGameRule<Boolean> COMMAND_BLOCKS_WORK =
      new CMIGameRule<>("COMMAND_BLOCKS_WORK", Boolean.class, Version.v1_21_R6);
  public static final CMIGameRule<Boolean> SPAWN_MONSTERS =
      new CMIGameRule<>("SPAWN_MONSTERS", Boolean.class, Version.v1_21_R6);

  @Deprecated
  public static final CMIGameRule<Boolean> DO_FIRE_TICK =
      new CMIGameRule<>("DO_FIRE_TICK", Boolean.class, null, Version.v1_21_R7);

  @Deprecated
  public static final CMIGameRule<Integer> SPAWN_CHUNK_RADIUS =
      new CMIGameRule<>("SPAWN_CHUNK_RADIUS", Integer.class, Version.v1_20_R4, Version.v1_21_R6);

  public static final CMIGameRule<Integer> FIRE_SPREAD_RADIUS_AROUND_PLAYER =
      new CMIGameRule<>("FIRE_SPREAD_RADIUS_AROUND_PLAYER", Integer.class, Version.v1_21_R7, null);
  private final String name;
  private final String legacyName;
  private final Class<T> type;
  private Object rule;

  static {
    Arrays.sort(rules, Comparator.comparing(CMIGameRule::getName));
  }

  private static String toLowerCamelCase(String string) {
    String[] strings = string.toLowerCase().split("_");
    StringBuilder stringBuilder = new StringBuilder(strings[0]);

    for (int value = 1; value < strings.length; value++) {
      stringBuilder
          .append(Character.toUpperCase(strings[value].charAt(0)))
          .append(strings[value].substring(1));
    }

    return stringBuilder.toString();
  }

  private CMIGameRule(String string, String text, Class<T> classValue) {
    this(string, text, classValue, null, null);
  }

  private CMIGameRule(String string, String text, Class<T> classValue, Version version) {
    this(string, text, classValue, version, null);
  }

  private CMIGameRule(String string, Class<T> classValue, Version version) {
    this(string, toLowerCamelCase(string), classValue, version, null);
  }

  private CMIGameRule(String string, Class<T> classValue, Version version, Version versionValue) {
    this(string, toLowerCamelCase(string), classValue, version, versionValue);
  }

  private CMIGameRule(
      String string, String text, Class<T> classValue, Version version, Version versionValue) {
    this.name = string;
    this.legacyName = text;
    this.type = classValue;
    if (version == null || Version.isCurrentEqualOrHigher(version)) {
      if (versionValue == null || !Version.isCurrentEqualOrHigher(versionValue)) {
        this.resolveRule();
        this.register();
      }
    }
  }

  public Class<T> getType() {
    return this.type;
  }

  private void register() {
    if (this.rule != null) {
      registry.put(this.getName().toLowerCase().replace("_", ""), this);
      registry.put(this.legacyName.toLowerCase().replace("_", ""), this);
      CMIGameRule[] cmiGameRules = Arrays.copyOf(rules, rules.length + 1);
      cmiGameRules[rules.length] = this;
      rules = cmiGameRules;
    }
  }

  @Nullable
  public static CMIGameRule<?> get(String string) {
    return registry.get(string.replace("_", "").toLowerCase());
  }

  private void resolveRule() {
    try {
      if (Version.isCurrentLower(Version.v1_13_R1)) {
        this.rule = this.getLegacyName();
        return;
      }

      var classValue = Class.forName("org.bukkit.GameRule");
      Object[] objects = (Object[]) classValue.getMethod("values").invoke(classValue);
      if (Version.isCurrentEqualOrHigher(Version.v1_21_R7)) {
        Method method = null;
        Object[] objectsValue = objects;
        int value = objects.length;

        for (int count = 0; count < value; count++) {
          Object object = objectsValue[count];
          if (method == null) {
            method = object.getClass().getMethod("getKey");
          }

          NamespacedKey namespacedKey = (NamespacedKey) method.invoke(object);
          if (namespacedKey.getKey().equalsIgnoreCase(this.getName())) {
            this.rule = object;
            return;
          }
        }
      }

      Object[] objectsSecond = objects;
      int countSecond = objects.length;

      for (int countThird = 0; countThird < countSecond; countThird++) {
        Object valueSecond = objectsSecond[countThird];
        if (valueSecond.toString().equalsIgnoreCase(this.legacyName)
            || valueSecond.toString().equalsIgnoreCase(this.getName())) {
          this.rule = valueSecond;
          return;
        }
      }

      objectsSecond = objects;
      countSecond = objects.length;

      for (int countFourth = 0; countFourth < countSecond; countFourth++) {
        Object valueThird = objectsSecond[countFourth];
        if (valueThird.toString().toLowerCase().contains(this.legacyName.toLowerCase())
            || valueThird.toString().toLowerCase().contains(this.getName().toLowerCase())) {
          this.rule = valueThird;
          return;
        }
      }

      CMIMessages.consoleMessage("&e[CMI] Cant find GameRule: " + this.getName());
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public String getLegacyName() {
    return this.legacyName;
  }

  public void set(World world, int value) {
    if (this.getType() == Integer.class) {
      this.set(world, (T) Integer.valueOf(value));
    }
  }

  public void set(World world, boolean flag) {
    if (this.getType() == Boolean.class) {
      this.set(world, (T) Boolean.valueOf(flag));
    }
  }

  public void set(World world, T value) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R7)) {
      if (this.rule != null) {
        GameRule gameRule = (GameRule) this.rule;
        world.setGameRule(gameRule, value);
      }
    } else {
      world.setGameRuleValue(this.legacyName, String.valueOf(value));
    }
  }

  @Nullable
  public T get(World world) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R7)) {
      if (this.rule == null) {
        return null;
      }

      GameRule gameRule = (GameRule) this.rule;
      return (T) world.getGameRuleValue(gameRule);
    } else if (!world.isGameRule(this.legacyName)) {
      return null;
    } else {
      String string = world.getGameRuleValue(this.legacyName);
      if (string == null) {
        return null;
      } else if (this.getType() == Boolean.class) {
        return this.getType().cast(Boolean.valueOf(string));
      } else {
        return this.getType() == Integer.class
            ? this.getType().cast(Integer.valueOf(string))
            : null;
      }
    }
  }

  public String getName() {
    return this.name;
  }

  public static CMIGameRule<?>[] getValues() {
    return rules;
  }
}
