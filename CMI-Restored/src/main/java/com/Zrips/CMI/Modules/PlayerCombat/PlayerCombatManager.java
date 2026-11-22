package com.Zrips.CMI.Modules.PlayerCombat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Display.CMITextDisplay;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.events.CMIPvEEndEventAsync;
import com.Zrips.CMI.events.CMIPvEStartEventAsync;
import com.Zrips.CMI.events.CMIPvPEndEventAsync;
import com.Zrips.CMI.events.CMIPvPStartEventAsync;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Entities.CMIEntitySubType;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Entities.MobHeadInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerCombatManager {
  private HashMap<CombatDamageType, Set<UUID>> combatMap = new HashMap<>();
  private HashMap<UUID, CMIPlayerCombat> playerCombatMap = new HashMap<>();
  private HashMap<UUID, HashMap<UUID, PlayerKillCount>> playerKills = new HashMap<>();
  private HashMap<UUID, HashMap<EntityType, EntityKillCount>> entityKills = new HashMap<>();
  private HashMap<EntityType, EntityHeadDrop> entityHeadDropChance = new HashMap<>();
  private HashMap<Integer, Double> lootMobBonus = new HashMap<>();
  private HashMap<Integer, Double> lootPlayerBonus = new HashMap<>();
  private CMI plugin;
  private boolean dropHead = false;
  private boolean pvpIncludeVictim = false;
  private boolean pveIncludeVictim = false;
  private boolean PlayerShowBossBar = false;
  private boolean PlayerShowDamageNumbers = false;
  private String PlayerDamageNumbersFormat = "";
  private boolean MobShowBossBar = false;
  private boolean MobIncludeEnvironment = false;
  private boolean MobShowDamageNumbers = false;
  private String MobDamageNumbersFormat = "";
  private boolean PDisableFlight = false;
  private boolean PDisableFallDamage = false;
  private boolean MDisableFallDamage = false;
  private boolean MDisableFlight = false;
  private boolean PlayerBlockCommands = false;
  private List<String> playerWorlds = new ArrayList<>();
  private boolean MobBlockCommands = false;
  private List<String> PlayerAllowedCommands = new ArrayList<>();
  private List<String> MobAllowedCommands = new ArrayList<>();
  private boolean PlayerMakeBlackList = false;
  private boolean MobMakeBlackList = false;
  private boolean maintainName = true;
  private boolean maintainLore = true;
  private boolean maintainEnchants = true;
  private boolean pvpNoGodDamage = false;
  private boolean pvpNoGodDamageInform = false;
  private boolean pveNoGodDamage = false;
  private boolean pveNoGodDamageInform = false;
  private boolean safeZoneDamage = false;
  private boolean dropEntityHead = false;
  private double DropHeadPercentage = 1.0;
  private List<String> mobWorlds = new ArrayList<>();
  private double LowerChanceWithEachKill = 50.0;
  private int CombatTimer = 15;
  private List<String> PlayerHeadLore = new ArrayList<>();
  Pattern lorePattern = Pattern.compile("(lore\\{(\"|'))(.+)((\"|')\\})");
  private CMITask sched = null;
  ConfigurationSection tempSection = null;
  private String fileName = "CustomHeads.yml";

  public HashMap<UUID, PlayerKillCount> getKills(UUID uuid) {
    return this.playerKills.computeIfAbsent(uuid, key -> new HashMap<>());
  }

  public HashMap<EntityType, EntityKillCount> getEntityKills(UUID uuid) {
    return this.entityKills.computeIfAbsent(uuid, key -> new HashMap<>());
  }

  public PlayerCombatManager(CMI cmi) {
    this.plugin = cmi;
    CombatDamageType[] combatDamageTypes;
    int value = (combatDamageTypes = CombatDamageType.values()).length;

    for (int index = 0; index < value; index++) {
      CombatDamageType combatDamageType = combatDamageTypes[index];
      this.combatMap.put(combatDamageType, new HashSet<>());
    }

    this.tasker();
  }

  public void clearCache(UUID uuid) {
    CMIPlayerCombat cmiPlayerCombat = this.playerCombatMap.get(uuid);
    if (cmiPlayerCombat != null && cmiPlayerCombat.getLeftCombatTime() <= 0L) {
      this.playerCombatMap.remove(uuid);
    }

    if (this.playerKills.containsKey(uuid)) {
      this.getKills(uuid)
          .values()
          .removeIf(
              playerKillCount ->
                  System.currentTimeMillis() - playerKillCount.getLastKill() > 3600000L);
      if (this.getKills(uuid).isEmpty()) {
        this.playerKills.remove(uuid);
      }
    }

    if (this.entityKills.containsKey(uuid)) {
      this.getEntityKills(uuid)
          .values()
          .removeIf(
              entityKillCount ->
                  System.currentTimeMillis() - entityKillCount.getLastKill() > 3600000L);
      if (this.getKills(uuid).isEmpty()) {
        this.entityKills.remove(uuid);
      }
    }
  }

  public void stop() {
    if (this.sched != null) {
      this.sched.cancel();
      this.sched = null;
    }
  }

  public void loadConfig() {
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(this.plugin, "Settings" + File.separator + "Combat.yml");
    } catch (Exception exception) {
      exception.printStackTrace();
      return;
    }

    configReader.addComment(
        "Combat.Timer", new String[] {"Defines combat timer to be used in particular features"});
    this.CombatTimer = configReader.get("Combat.Timer", 15);
    configReader.addComment(
        "Combat.safeZoneDamage",
        new String[] {
          "When enabled we will allow for players to be damaged in safe zone if they are tagged for"
              + " pvp"
        });
    this.safeZoneDamage = configReader.get("Combat.safeZoneDamage", false);
    configReader.addComment(
        "Combat.Player.DisableFlight",
        new String[] {
          "If set to true, then player who gets placed into combat mode will get its fly mode"
              + " disabled",
          "This will disable players fly mode which will result in player dropping down and will"
              + " disable option to start flying",
          "This can be bypassed by player performing fly command if he has access to it and"
              + " commands durring combat are not blocked",
          "Can be bypassed with "
              + PermissionsManager.CMIPerm.pvp_PFlyBypass.getPermission()
              + " permission node"
        });
    this.PDisableFlight = configReader.get("Combat.Player.DisableFlight", false);
    configReader.addComment(
        "Combat.Player.IncludeVictim",
        new String[] {
          "If set to true, then attacked player will be included into combat mode even if he doesnt"
              + " fight back",
          "If set to false then only attacker will be marked for pvp mode"
        });
    this.pvpIncludeVictim =
        configReader.get(
            "Combat.Player.IncludeVictim",
            configReader.getC().getBoolean("Combat.IncludeVictim", true));
    configReader.addComment(
        "Combat.Player.DisableFallDamage",
        new String[] {
          "If set to true player whose fly mode got disabled will not suffer fall damage, once"
        });
    this.PDisableFallDamage = configReader.get("Combat.Player.DisableFallDamage", false);
    if (Version.isCurrentHigher(Version.v1_9_R1)) {
      configReader.addComment(
          "Combat.Player.ShowBossBar",
          new String[] {
            "When set to true player will see boss bar message indicating how long until combat"
                + " mode ends",
            "This only applies for pvp type combat"
          });
      this.PlayerShowBossBar = configReader.get("Combat.Player.ShowBossBar", false);
    }

    if (Version.isCurrentHigher(Version.v1_8_R1)) {
      configReader.addComment(
          "Combat.Player.ShowDamageNumbers",
          new String[] {
            "When enabled we will show damage numbers in a form of holograms when damaging players"
          });
      this.PlayerShowDamageNumbers = configReader.get("Combat.Player.ShowDamageNumbers", true);
      this.PlayerDamageNumbersFormat =
          configReader.get("Combat.Player.DamageNumbersFormat", "&c⚔[damage]");
    }

    configReader.addComment(
        "Combat.Player.noGodDamage",
        new String[] {
          "Prevents damage from players with god mode enabled",
          "Can be bypassed with "
              + PermissionsManager.CMIPerm.pvp_godBypass.getPermission()
              + " permission node"
        });
    this.pvpNoGodDamage =
        configReader.get(
            "Combat.Player.noGodDamage",
            configReader.getC().getBoolean("Combat.noGodDamage", false));
    configReader.addComment(
        "Combat.Player.noGodDamageInform",
        new String[] {"Informs player that he cant damage players while in god mode"});
    this.pvpNoGodDamageInform =
        configReader.get(
            "Combat.Player.noGodDamageInform",
            configReader.getC().getBoolean("Combat.noGodDamageInform", false));
    configReader.addComment(
        "Combat.Player.BlockCommands",
        new String[] {
          "When set to true players will be only able to use commands defined in the list",
          "This only applies for pvp type combat"
        });
    this.PlayerBlockCommands = configReader.get("Combat.Player.BlockCommands", false);
    this.PlayerAllowedCommands.clear();
    this.PlayerAllowedCommands =
        configReader.get("Combat.Player.AllowedCommands", Arrays.asList("cmi msg", "cmi reply"));
    if (!this.PlayerBlockCommands) {
      this.PlayerAllowedCommands.clear();
    }

    configReader.addComment(
        "Combat.Player.MakeBlackList",
        new String[] {
          "When set to true AllowedCommands become black list which will define which commands"
              + " player cant use"
        });
    this.PlayerMakeBlackList = configReader.get("Combat.Player.MakeBlackList", false);
    configReader.addComment(
        "Combat.Mob.DisableFlight",
        new String[] {
          "If set to true, then player who gets placed into combat mode will get its fly mode"
              + " disabled",
          "Can be bypassed with "
              + PermissionsManager.CMIPerm.pvp_MFlyBypass.getPermission()
              + " permission node"
        });
    this.MDisableFlight = configReader.get("Combat.Mob.DisableFlight", false);
    configReader.addComment(
        "Combat.Mob.IncludeVictim",
        new String[] {
          "If set to true, then attacked player will be included into combat mode even if he doesnt"
              + " fight back"
        });
    this.pveIncludeVictim = configReader.get("Combat.Mob.IncludeVictim", false);
    configReader.addComment(
        "Combat.Mob.DisableFallDamage",
        new String[] {
          "If set to true player whose fly mode got disabled will not suffer fall damage, once"
        });
    this.MDisableFallDamage = configReader.get("Combat.Mob.DisableFallDamage", false);
    if (Version.isCurrentHigher(Version.v1_9_R1)) {
      configReader.addComment(
          "Combat.Mob.ShowBossBar",
          new String[] {
            "When set to true player will see boss bar message indicating how long until combat"
                + " mode ends",
            "This only applies for pve type combat"
          });
      this.MobShowBossBar = configReader.get("Combat.Mob.ShowBossBar", false);
    }

    configReader.addComment(
        "Combat.Mob.IncludeEnvironment",
        new String[] {
          "When enabled we will include damage from environmental damage into mob combat timer",
          "This can include damage from cactus, magma blocks, suffocation, burning, fall damage and"
              + " similar"
        });
    this.MobIncludeEnvironment = configReader.get("Combat.Mob.IncludeEnvironment", true);
    if (Version.isCurrentHigher(Version.v1_8_R1)) {
      configReader.addComment(
          "Combat.Mob.ShowDamageNumbers",
          new String[] {
            "When enabled we will show damage numbers in a form of holograms when damaging mobs"
          });
      this.MobShowDamageNumbers = configReader.get("Combat.Mob.ShowDamageNumbers", true);
      this.MobDamageNumbersFormat =
          configReader.get("Combat.Mob.DamageNumbersFormat", "&c❤[damage]");
    }

    configReader.addComment(
        "Combat.Mob.noGodDamage",
        new String[] {
          "Prevents damage from players with god mode enabled",
          "Can be bypassed with "
              + PermissionsManager.CMIPerm.pve_godBypass.getPermission()
              + " permission node"
        });
    this.pveNoGodDamage = configReader.get("Combat.Mob.noGodDamage", false);
    configReader.addComment(
        "Combat.Mob.noGodDamageInform",
        new String[] {"Informs player that he cant damage mobs while in god mode"});
    this.pveNoGodDamageInform = configReader.get("Combat.Mob.noGodDamageInform", false);
    configReader.addComment(
        "Combat.Mob.BlockCommands",
        new String[] {
          "When set to true players will be only able to use commands defined in the list",
          "This only applies for pve type combat"
        });
    this.MobBlockCommands = configReader.get("Combat.Mob.BlockCommands", false);
    this.MobAllowedCommands.clear();
    this.MobAllowedCommands =
        configReader.get("Combat.Mob.AllowedCommands", Arrays.asList("cmi msg", "cmi reply"));
    if (!this.MobBlockCommands) {
      this.MobAllowedCommands.clear();
    }

    configReader.addComment(
        "Combat.Mob.MakeBlackList",
        new String[] {
          "When set to true AllowedCommands become black list which will define which commands"
              + " player cant use"
        });
    this.MobMakeBlackList = configReader.get("Combat.Mob.MakeBlackList", false);
    if (configReader.getC().isConfigurationSection("Combat.Heads")) {
      this.tempSection = configReader.getC().getConfigurationSection("Combat.Heads");
    }

    configReader.save();
    this.loadHeads();
  }

  public void loadHeads() {
    Long startTime = System.currentTimeMillis();
    this.plugin.safeRenameFile("customHeads.yml", "Settings" + File.separator + this.fileName);
    ConfigReader configReader = null;

    try {
      configReader =
          new ConfigReader(CMI.getInstance(), "Settings" + File.separator + this.fileName);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      if (!configReader.getC().isConfigurationSection("Player")
          && !configReader.getC().isConfigurationSection("Mob")
          && this.tempSection != null) {
        if (!configReader.getC().isConfigurationSection("Player")) {
          configReader.set("Player", this.tempSection.getConfigurationSection("Player"));
        }

        if (!configReader.getC().isConfigurationSection("Mob")) {
          configReader.set("Mob", this.tempSection.getConfigurationSection("Mob"));
        }

        configReader.save();

        try {
          configReader =
              new ConfigReader(CMI.getInstance(), "Settings" + File.separator + this.fileName);
        } catch (Exception exception2) {
          exception2.printStackTrace();
          return;
        }
      }

      int value = 0;
      this.entityHeadDropChance.clear();
      configReader.addComment(
          "LootMobBonus",
          new String[] {
            "When enabled mob heads will have bigger chance to drop when using tools with looting"
                + " enchantment",
            "Value is in % and it will add appropriate percentage to current drop chance by using"
                + " drop chance itself",
            "For example player who has head drop change of 1% with looting 3 which has 30% bonus"
                + " will have 1.3% as end value (default values)",
            "This only applies for monster heads",
            "You can add as many levels as you want, simply duplicate line and set new number, in"
                + " example",
            "Lvl33: 35.5"
          });
      boolean flag = configReader.get("LootMobBonus.Enabled", true);
      if (configReader.getC().isDouble("LootMobBonus.Lvl1")) {
        for (String string :
            configReader.getC().getConfigurationSection("LootMobBonus").getKeys(false)) {
          if (string.startsWith("Lvl")) {
            Integer integer = 1;

            try {
              integer = Integer.parseInt(string.substring("lvl".length()));
            } catch (Exception exception3) {
              continue;
            }

            this.lootMobBonus.put(
                integer,
                configReader.get("LootMobBonus.Lvl" + integer, integer.intValue() * 1.5) / 100.0);
          }
        }
      } else {
        this.lootMobBonus.put(1, configReader.get("LootMobBonus.Lvl1", 5.0) / 100.0);
        this.lootMobBonus.put(2, configReader.get("LootMobBonus.Lvl2", 15.0) / 100.0);
        this.lootMobBonus.put(3, configReader.get("LootMobBonus.Lvl3", 30.0) / 100.0);
      }

      if (!flag) {
        this.lootMobBonus.clear();
      }

      configReader.addComment(
          "useDefaultHeadLore",
          new String[] {
            "Defines if you want to use default head lore which will be used in case head doesnt"
                + " have defined lore"
          });
      boolean useDefaultLore = configReader.get("useDefaultHeadLore", true);
      configReader.addComment(
          "defaultHeadLore",
          new String[] {"Default lore used when head doesnt have defined head lore set"});
      var list = configReader.get("defaultHeadLore", Arrays.asList("&7Killed by &6[playerName]"));
      configReader.addComment(
          "PlayerHead.UseLore", new String[] {"Defines if you want to use player head lore"});
      Boolean useHeadLore = configReader.get("PlayerHead.UseLore", true);
      configReader.addComment(
          "PlayerHead.Lore",
          new String[] {
            "Lore used for player head drops",
            "Global variables can be used for simple identification who was killed and who killed"
                + " player",
            "Aditionally placeholders can be used too, but we will use victim as source for their"
                + " translation if placeholder requires source player"
          });
      this.PlayerHeadLore =
          configReader.get("PlayerHead.Lore", Arrays.asList("&7Killed by &6[playerName]"));
      if (!useHeadLore) {
        this.PlayerHeadLore.clear();
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_16_R3)) {
        configReader.addComment(
            "HeadHandling.Maintain.Name",
            new String[] {
              "Defines if we should maintain heads custom name when head is placed and then broken",
              "Vanilla minecraft will remove any custom information about head when you place it,"
                  + " this can help out to maintain data"
            });
        this.maintainName = configReader.get("HeadHandling.Maintain.Name", false);
        this.maintainLore = configReader.get("HeadHandling.Maintain.Lore", false);
        this.maintainEnchants = configReader.get("HeadHandling.Maintain.Enchants", false);
      }

      this.dropHead = configReader.get("Player.Drop", false);
      configReader.addComment(
          "Player.Percentage",
          new String[] {
            "Percentage from 0 to 100 for head to be dropped. Decimals are acceptable, like 0.2",
            "100 will mean that head will be dropped every time player kills another player",
            "1 will mean that there is 1% that player will drop head if he is killed by another"
                + " player"
          });
      this.DropHeadPercentage = configReader.get("Player.Percentage", 1.0);
      configReader.addComment(
          "Player.LowerChanceOfterDrop",
          new String[] {
            "Percentage from 0 to 100 for lowering chance in getting second head of same player",
            "This will reset on each server restart"
          });
      this.LowerChanceWithEachKill = configReader.get("Player.LowerChanceOfterDrop", 50.0);
      configReader.addComment(
          "Player.LootBonus",
          new String[] {
            "When enabled player heads will have bigger chance to drop when using tools with"
                + " looting enchantment",
            "Value is in % and it will add appropriate percentage to current drop chance by using"
                + " drop chance itself",
            "For example player who has head drop change of 1% with looting 3 which has 30% bonus"
                + " will have 1.3% as end value (default values)",
            "This only applies for player heads",
            "You can add as many levels as you want, simply duplicate line and set new number, in"
                + " example",
            "Lvl33: 35.5"
          });
      flag = configReader.get("Player.LootBonus.Enabled", true);
      if (configReader.getC().isDouble("Player.LootBonus.Lvl1")) {
        for (String lore :
            configReader.getC().getConfigurationSection("Player.LootBonus").getKeys(false)) {
          if (lore.startsWith("Lvl")) {
            Integer loreLevel = 1;

            try {
              loreLevel = Integer.parseInt(lore.substring("lvl".length()));
            } catch (Exception exception4) {
              continue;
            }

            this.lootPlayerBonus.put(
                loreLevel,
                configReader.get("Player.LootBonus.Lvl" + loreLevel, loreLevel.intValue() * 1.5)
                    / 100.0);
          }
        }
      } else {
        this.lootPlayerBonus.put(1, configReader.get("Player.LootBonus.Lvl1", 5.0) / 100.0);
        this.lootPlayerBonus.put(2, configReader.get("Player.LootBonus.Lvl2", 15.0) / 100.0);
        this.lootPlayerBonus.put(3, configReader.get("Player.LootBonus.Lvl3", 30.0) / 100.0);
      }

      if (!flag) {
        this.lootPlayerBonus.clear();
      }

      configReader.addComment(
          "Player.Worlds",
          new String[] {
            "List of worlds where we should drop player heads. Keep it empty if you want to include"
                + " all possible ones"
          });
      this.playerWorlds = configReader.get("Player.Worlds", new ArrayList());
      CMIList.toLowerCase(this.playerWorlds);
      configReader.addComment(
          "Mob.Drop",
          new String[] {
            "Enables custom mob heads dropping from mobs with particular chance",
            "Check " + this.fileName + " for customization by entityType"
          });
      this.dropEntityHead = configReader.get("Mob.Drop", false);
      configReader.addComment(
          "Mob.Worlds",
          new String[] {
            "List of worlds where we should drop mob heads. Keep it empty if you want to include"
                + " all possible ones"
          });
      this.mobWorlds = configReader.get("Mob.Worlds", new ArrayList());
      CMIList.toLowerCase(this.mobWorlds);
      configReader.addComment(
          "EntityHeads",
          new String[] {
            "Custom heads of every entity",
            "You can have as many custom heads as you want",
            "Format is [dropChance]:[texture]",
            "Drop chance is for that particular head",
            "While you have separate global drop chance for particular entity type",
            "Strongly recommend to have at least one head at 100% as in another case there will be"
                + " a chance to not get a head even if player should",
            "You can find custom textures online",
            "If you have more then one possible head for one entity type, then it will be"
                + " randomized",
            "Custom mob head can be defined by using format like"
                + " [chance]:[HeadName]:c-[options]:lore{\"[lore]\"}:[texture]",
            "More information and some better examples can be found at"
                + " www.zrips.net/cmi/extra/custom-mob-heads/",
            "Optionally you can shorten out texture string by removing first part which is"
                + " eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv"
                + " which by itself will be added automatically if not present"
          });
      EntityType[] entityTypes;
      int typeCount = (entityTypes = EntityType.values()).length;

      for (int typeIndex = 0; typeIndex < typeCount; typeIndex++) {
        EntityType entityType = entityTypes[typeIndex];
        if (entityType != null && entityType.isAlive() && entityType.isSpawnable()) {
          CMIEntityType cmiEntityType = CMIEntityType.getByType(entityType);
          if (cmiEntityType != null && cmiEntityType != CMIEntityType.ARMOR_STAND) {
            Double minValue =
                configReader.get(
                    "EntityHeads." + entityType.toString().toLowerCase() + ".DropChance", 1.0);
            Double maxValue =
                configReader.get(
                    "EntityHeads." + entityType.toString().toLowerCase() + ".LowerWithEachDrop",
                    50.0);
            EntityHeadDrop entityHeadDrop = new EntityHeadDrop(cmiEntityType);
            entityHeadDrop.setBaseDropChance(minValue);
            entityHeadDrop.setBaseLowerAmount(maxValue);
            ArrayList<String> arrayList = new ArrayList();
            HashMap<String, MobHeadInfo> hashMap = new HashMap();

            for (var entry : cmiEntityType.getHeadTexturesMap().entrySet()) {
              StringBuilder stringBuilder = new StringBuilder();

              for (CMIEntitySubType cmiEntitySubType :
                  ((MobHeadInfo) entry.getValue()).getCriterias()) {
                if (!stringBuilder.toString().isEmpty()) {
                  stringBuilder.append(",");
                }

                stringBuilder.append(cmiEntitySubType.toString().toLowerCase());
              }

              arrayList.add(
                  "100:"
                      + (((MobHeadInfo) entry.getValue()).getCustomName() == null
                          ? "Default"
                          : ((MobHeadInfo) entry.getValue()).getCustomName())
                      + ":"
                      + (stringBuilder.toString().isEmpty()
                          ? ""
                          : "c-" + stringBuilder.toString() + ":")
                      + (String) entry.getKey());
            }

            for (String lineEntry :
                configReader.get(
                    "EntityHeads." + entityType.toString().toLowerCase() + ".Heads", arrayList)) {
              if (!lineEntry.contains(":")) {
                CMI.getInstance()
                    .consoleMessage(
                        "&cIncorrect head drop chance format (" + entityType.toString() + ")");
              } else {
                String[] strings = lineEntry.split(":");
                String prefix = strings[0];
                String suffix = strings[strings.length - 1];
                String rest = lineEntry.substring(prefix.length() + 1);
                if (rest.length() > suffix.length()) {
                  rest = rest.substring(0, rest.length() - suffix.length() - 1);
                } else {
                  rest = "";
                }

                MobHeadInfo mobHeadInfo = new MobHeadInfo();
                if (!rest.isEmpty()) {
                  Matcher matcher = this.lorePattern.matcher(rest);
                  if (!matcher.find()) {
                    if (useDefaultLore) {
                      mobHeadInfo.setLore(list);
                    }
                  } else {
                    String typeGroup = matcher.group(3);
                    ArrayList<String> typeList = new ArrayList();
                    String separator = matcher.group(2);
                    String[] typeParts;
                    int typePartCount =
                        (typeParts = typeGroup.split(separator + "," + separator)).length;

                    for (int typePartIndex = 0; typePartIndex < typePartCount; typePartIndex++) {
                      String typePart = typeParts[typePartIndex];
                      if (typePart.startsWith(separator)) {
                        typePart = typePart.substring(1);
                      }

                      if (typePart.endsWith(separator)) {
                        typePart = typePart.substring(0, typePart.length() - 2);
                      }

                      typeList.add(typePart);
                    }

                    mobHeadInfo.setLore(typeList);
                    rest = rest.replace(matcher.group(), "");
                  }

                  String[] subParts;
                  int subPartCount = (subParts = rest.split(":")).length;

                  for (int subPartIndex = 0; subPartIndex < subPartCount; subPartIndex++) {
                    String subPart = subParts[subPartIndex];
                    if (subPart.startsWith("c-")) {
                      String[] levelParts;
                      int levelCount =
                          (levelParts = subPart.substring("c-".length()).split(",")).length;

                      for (int levelIndex = 0; levelIndex < levelCount; levelIndex++) {
                        String levelText = levelParts[levelIndex];
                        CMIEntitySubType subType = CMIEntitySubType.getByName(levelText);
                        if (subType != null) {
                          mobHeadInfo.addCriterias(subType);
                        }
                      }
                    } else {
                      String subTypeName = null;
                      subTypeName = subPart;
                      subTypeName = subTypeName.equalsIgnoreCase("Default") ? null : subTypeName;
                      mobHeadInfo.setCustomName(subTypeName);
                    }
                  }
                }

                if (suffix.length() < 150) {
                  suffix =
                      "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv"
                          + suffix;
                }

                hashMap.put(suffix, mobHeadInfo);
                Double damageBonus = null;

                try {
                  damageBonus = Double.parseDouble(prefix);
                } catch (Exception exception5) {
                  CMI.getInstance()
                      .consoleMessage(
                          "&cCan't determine head drop chance (" + entityType.toString() + ")");
                  continue;
                }

                value++;
                entityHeadDrop.addHead(suffix, damageBonus);
              }
            }

            cmiEntityType.setHeadTextures(hashMap);
            this.entityHeadDropChance.put(entityType, entityHeadDrop);
          }
        }
      }

      configReader.save();
      this.plugin.loadMessage(value, "custom mob heads", System.currentTimeMillis() - startTime);
    }
  }

  private Set<UUID> get(CombatDamageType combatDamageType) {
    return this.combatMap.get(combatDamageType);
  }

  public void removePlayerFromCombat(CMIUser cmiUser) {
    if (this.get(CombatDamageType.Player).remove(cmiUser.getUniqueId())) {
      this.setGotLastDamageFromPlayer(cmiUser.getUniqueId(), 0L);
      CMIScheduler.runTaskAsynchronously(
          this.plugin,
          () -> {
            CMIPvPEndEventAsync cmiPvPEndEventAsync =
                new CMIPvPEndEventAsync(cmiUser.getPlayer(false));
            Bukkit.getServer().getPluginManager().callEvent(cmiPvPEndEventAsync);
          });
    }

    if (this.get(CombatDamageType.Mob).remove(cmiUser.getUniqueId())) {
      this.setGotLastDamageFromMob(cmiUser.getUniqueId(), 0L);
      CMIScheduler.runTaskAsynchronously(
          this.plugin,
          () -> {
            CMIPvEEndEventAsync cmiPvEEndEventAsync =
                new CMIPvEEndEventAsync(cmiUser.getPlayer(false));
            Bukkit.getServer().getPluginManager().callEvent(cmiPvEEndEventAsync);
          });
    }
  }

  public void addPlayerIntoCombat(CMIUser cmiUser) {
    if (this.get(CombatDamageType.Player).add(cmiUser.getUniqueId())) {
      if (cmiUser.isOnline()) {
        CMIScheduler.runTaskAsynchronously(
            this.plugin,
            () -> {
              CMIPvPStartEventAsync cmiPvPStartEventAsync =
                  new CMIPvPStartEventAsync(cmiUser.getPlayer(false));
              Bukkit.getServer().getPluginManager().callEvent(cmiPvPStartEventAsync);
              if (cmiPvPStartEventAsync.isCancelled()) {
                this.get(CombatDamageType.Player).remove(cmiUser.getUniqueId());
              }
            });
      }

      this.tasker();
    }
  }

  public void addPlayerIntoMobCombat(CMIUser cmiUser) {
    if (this.get(CombatDamageType.Mob).add(cmiUser.getUniqueId())) {
      if (cmiUser.isOnline()) {
        CMIScheduler.runTaskAsynchronously(
            this.plugin,
            () -> {
              CMIPvEStartEventAsync cmiPvEStartEventAsync =
                  new CMIPvEStartEventAsync(cmiUser.getPlayer(false));
              Bukkit.getServer().getPluginManager().callEvent(cmiPvEStartEventAsync);
              if (cmiPvEStartEventAsync.isCancelled()) {
                this.get(CombatDamageType.Mob).remove(cmiUser.getUniqueId());
              }
            });
      }

      this.tasker();
    }
  }

  private void tasker() {
    if (this.sched == null) {
      this.sched =
          CMIScheduler.scheduleSyncRepeatingTask(
              this.plugin,
              () -> {
                if (this.get(CombatDamageType.Player).isEmpty()
                    && this.get(CombatDamageType.Mob).isEmpty()) {
                  this.stop();
                } else {
                  for (UUID uuid : new HashSet<>(this.get(CombatDamageType.Player))) {
                    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(uuid);
                    if (cmiUser == null || !cmiUser.isOnline()) {
                      this.get(CombatDamageType.Player).remove(uuid);
                    } else if (!cmiUser.isInCombatWithPlayer()) {
                      this.get(CombatDamageType.Player).remove(uuid);
                      CMIScheduler.runTaskAsynchronously(
                          this.plugin,
                          () -> {
                            if (cmiUser.isOnline()) {
                              CMIPvPEndEventAsync cmiPvPEndEventAsync =
                                  new CMIPvPEndEventAsync(cmiUser.getPlayer(false));
                              Bukkit.getServer().getPluginManager().callEvent(cmiPvPEndEventAsync);
                            }
                          });
                    }
                  }

                  for (UUID playerId : new HashSet<>(this.get(CombatDamageType.Mob))) {
                    CMIUser combatUser = this.plugin.getPlayerManager().getUser(playerId);
                    if (combatUser == null || !combatUser.isOnline()) {
                      this.get(CombatDamageType.Mob).remove(playerId);
                    } else if (!combatUser.isInCombatWithMob()) {
                      this.get(CombatDamageType.Mob).remove(playerId);
                      CMIScheduler.runTaskAsynchronously(
                          this.plugin,
                          () -> {
                            if (combatUser.isOnline()) {
                              CMIPvEEndEventAsync cmiPvEEndEventAsync =
                                  new CMIPvEEndEventAsync(combatUser.getPlayer(false));
                              Bukkit.getServer().getPluginManager().callEvent(cmiPvEEndEventAsync);
                            }
                          });
                    }
                  }
                }
              },
              20L,
              20L);
    }
  }

  public Boolean isDropPlayerHead() {
    return this.dropHead;
  }

  public Boolean isDropEntityHead() {
    return this.dropEntityHead;
  }

  public double getLowerChanceWithEachKill() {
    return this.LowerChanceWithEachKill;
  }

  public double getPlayerDropHeadPercentage() {
    return this.DropHeadPercentage;
  }

  public double getEntityDropHeadPercentage(EntityType entityType) {
    EntityHeadDrop entityHeadDrop = this.entityHeadDropChance.get(entityType);
    return entityHeadDrop == null ? 0.0 : entityHeadDrop.getBaseDropChance();
  }

  public double getEntityHeadDropChanceLowering(EntityType entityType) {
    EntityHeadDrop entityHeadDrop = this.entityHeadDropChance.get(entityType);
    return entityHeadDrop == null ? 0.0 : entityHeadDrop.getBaseLowerAmount();
  }

  @Deprecated
  public ItemStack tryToGetEntityHead(EntityType entityType) {
    EntityHeadDrop entityHeadDrop = this.entityHeadDropChance.get(entityType);
    return entityHeadDrop == null ? null : entityHeadDrop.getHead();
  }

  public ItemStack tryToGetEntityHead(Player player, Entity entity) {
    EntityHeadDrop entityHeadDrop = this.entityHeadDropChance.get(entity.getType());
    return entityHeadDrop == null ? null : entityHeadDrop.getHead(player, entity);
  }

  public HashMap<EntityType, EntityHeadDrop> getEntityHeadDropChance() {
    return this.entityHeadDropChance;
  }

  public int getCombatTimer() {
    return this.CombatTimer;
  }

  public void setCombatTimer(int value) {
    this.CombatTimer = value;
  }

  public boolean isPvPIncludeVictim() {
    return this.pvpIncludeVictim;
  }

  public boolean isPvEIncludeVictim() {
    return this.pveIncludeVictim;
  }

  public HashMap<Integer, Double> getLootMobBonus() {
    return this.lootMobBonus;
  }

  public HashMap<Integer, Double> getLootPlayerBonus() {
    return this.lootPlayerBonus;
  }

  public Boolean isPVPNoGodDamage() {
    return this.pvpNoGodDamage;
  }

  public Boolean isPVPNoGodDamageInform() {
    return this.pvpNoGodDamageInform;
  }

  public Boolean isPVENoGodDamage() {
    return this.pveNoGodDamage;
  }

  public Boolean isPVENoGodDamageInform() {
    return this.pveNoGodDamageInform;
  }

  public Boolean isPDisableFlight() {
    return this.PDisableFlight;
  }

  public Boolean isMDisableFlight() {
    return this.MDisableFlight;
  }

  public boolean isAllowedPlayerCommand(String string) {
    for (String command : this.PlayerAllowedCommands) {
      if (string.equalsIgnoreCase(command.toLowerCase())
          || string.startsWith(command.toLowerCase() + " ")) {
        return !this.PlayerMakeBlackList;
      }
    }

    return this.PlayerMakeBlackList;
  }

  public boolean isAllowedMobCommand(String string) {
    for (String command : this.MobAllowedCommands) {
      if (string.equalsIgnoreCase(command.toLowerCase())
          || string.startsWith(command.toLowerCase() + " ")) {
        return !this.MobMakeBlackList;
      }
    }

    return this.MobMakeBlackList;
  }

  public boolean isPlayerShowBossBar() {
    return this.PlayerShowBossBar;
  }

  public boolean isMobShowBossBar() {
    return this.MobShowBossBar;
  }

  public boolean isPlayerBlockCommands() {
    return this.PlayerBlockCommands;
  }

  public boolean isMobBlockCommands() {
    return this.MobBlockCommands;
  }

  public boolean isPDisableFallDamage() {
    return this.PDisableFallDamage;
  }

  public boolean isMDisableFallDamage() {
    return this.MDisableFallDamage;
  }

  public List<String> getMobWorlds() {
    return this.mobWorlds;
  }

  public List<String> getPlayerWorlds() {
    return this.playerWorlds;
  }

  public boolean isSafeZoneDamage() {
    return this.safeZoneDamage;
  }

  public List<String> getPlayerHeadLore() {
    return new ArrayList<>(this.PlayerHeadLore);
  }

  public void showDamageNumber(Player player, Double damage, Location location, boolean flag) {
    if (damage != null && damage != 0.0) {
      if (Version.isCurrentEqualOrHigher(Version.v1_19_R3)) {
        CMIScheduler.runAtLocation(
            this.plugin,
            location,
            () -> {
              String string = flag ? this.PlayerDamageNumbersFormat : this.MobDamageNumbersFormat;
              if (player.getWorld().equals(location.getWorld())) {
                Location origin = location;
                double number = 1.0;
                double distance = CMILocation.getDistance(player.getLocation(), location);
                if (distance > 5.0) {
                  distance -= 5.0;
                  origin =
                      location.clone().add(0.0, CMINumber.clamp(distance, 1.0, 64) / 32.0, 0.0);
                  number = 1.0 + distance / 5.0;
                }

                number = CMINumber.clamp(number, 1.0, 10);
                CMITextDisplay cmiTextDisplay =
                    this.createTextDisplay(
                        string.replace("[damage]", CMINumber.format2Decimals(damage)), origin);
                cmiTextDisplay.setWidth(number);
                cmiTextDisplay.setHeight(number);
                cmiTextDisplay.show(player);
                CMIScheduler.runAtLocationLater(
                    this.plugin, origin, () -> cmiTextDisplay.destroy(player), 20L);
              }
            });
      }
    }
  }

  private CMITextDisplay createTextDisplay(String string, Location location) {
    CMITextDisplay cmiTextDisplay = new CMITextDisplay(location);
    cmiTextDisplay.setText(string);
    cmiTextDisplay.setShadowed(false);
    cmiTextDisplay.setBrightness(15, 15);

    try {
      Color color = cmiTextDisplay.getBackgroundColor();
      if (color == null) {
        color = Color.BLACK;
      }

      color = color.setAlpha(0);
      cmiTextDisplay.setBackgroundColor(color);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    cmiTextDisplay.setSeeThrough(true);
    return cmiTextDisplay;
  }

  public void show(Player player, String string, Location location) {
    if (Version.isCurrentEqualOrHigher(Version.v1_19_R3)) {
      CMIScheduler.runTaskAsynchronously(
          this.plugin,
          () -> {
            CMITextDisplay cmiTextDisplay = this.createTextDisplay(string, location);
            cmiTextDisplay.show(player);
            CMIScheduler.runAtLocationLater(
                this.plugin, location, () -> cmiTextDisplay.destroy(player), 20L);
          });
    }
  }

  public boolean isPlayerShowDamageNumbers() {
    return this.PlayerShowDamageNumbers;
  }

  public boolean isMobShowDamageNumbers() {
    return this.MobShowDamageNumbers;
  }

  public Long getGotLastDamageAt(UUID uuid) {
    CMIPlayerCombat cmiPlayerCombat = this.playerCombatMap.get(uuid);
    return cmiPlayerCombat == null ? null : cmiPlayerCombat.getGotLastDamageAt();
  }

  public CMIPlayerCombat getCombatRecord(UUID uuid) {
    return this.playerCombatMap.computeIfAbsent(uuid, key -> new CMIPlayerCombat());
  }

  public void setGotLastDamageAt(UUID uuid, Long timestamp) {
    this.getCombatRecord(uuid).setGotLastDamageAt(timestamp);
  }

  public boolean isInCombatWithPlayer(UUID uuid) {
    CMIPlayerCombat cmiPlayerCombat = this.playerCombatMap.get(uuid);
    return cmiPlayerCombat == null ? false : cmiPlayerCombat.isInCombatWithPlayer();
  }

  public Long getGotLastDamageFromPlayer(UUID uuid) {
    CMIPlayerCombat cmiPlayerCombat = this.playerCombatMap.get(uuid);
    return cmiPlayerCombat == null ? 0L : cmiPlayerCombat.getGotLastDamageFromPlayer();
  }

  public void setGotLastDamageFromPlayer(UUID uuid, Long timestamp) {
    if (!this.isInCombatWithPlayer(uuid)) {
      this.addPlayerIntoCombat(CMIUser.getUser(uuid));
    }

    this.getCombatRecord(uuid).setGotLastDamageFromPlayer(timestamp);
  }

  public void setDidLastDamageToPlayer(UUID uuid, Long timestamp) {
    if (!this.isInCombatWithPlayer(uuid)) {
      this.addPlayerIntoCombat(CMIUser.getUser(uuid));
    }

    this.getCombatRecord(uuid).setDidLastDamageToPlayer(timestamp);
  }

  public boolean isInCombatWithMob(UUID uuid) {
    return !this.playerCombatMap.containsKey(uuid)
        ? false
        : this.playerCombatMap.get(uuid).isInCombatWithMob();
  }

  public Long getGotLastDamageFromMob(UUID uuid) {
    CMIPlayerCombat cmiPlayerCombat = this.playerCombatMap.get(uuid);
    return cmiPlayerCombat == null ? 0L : cmiPlayerCombat.getGotLastDamageFromMob();
  }

  public void setGotLastDamageFromMob(UUID uuid, Long timestamp) {
    if (!this.isInCombatWithMob(uuid)) {
      CMI.getInstance().getPlayerCombatManager().addPlayerIntoMobCombat(CMIUser.getUser(uuid));
    }

    this.getCombatRecord(uuid).setGotLastDamageFromMob(timestamp);
  }

  public void setDidLastDamageToEntity(UUID uuid, Long timestamp) {
    if (!this.isInCombatWithMob(uuid)) {
      CMI.getInstance().getPlayerCombatManager().addPlayerIntoMobCombat(CMIUser.getUser(uuid));
    }

    this.getCombatRecord(uuid).setDidLastDamageToMob(timestamp);
  }

  public long getLeftCombatTime(UUID uuid) {
    return !this.playerCombatMap.containsKey(uuid)
        ? 0L
        : this.playerCombatMap.get(uuid).getLeftCombatTime();
  }

  public boolean isMobIncludeEnvironment() {
    return this.MobIncludeEnvironment;
  }

  public boolean isMaintainName() {
    return this.maintainName;
  }

  public boolean isMaintainLore() {
    return this.maintainLore;
  }

  public boolean isMaintainEnchants() {
    return this.maintainEnchants;
  }
}
