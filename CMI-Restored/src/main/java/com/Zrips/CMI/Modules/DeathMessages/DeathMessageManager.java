package com.Zrips.CMI.Modules.DeathMessages;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.utils.FileDownloader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class DeathMessageManager {
  private CMI plugin;
  HashMap<String, DeathMessages> messages = new HashMap<>();
  private List<String> playerHover = new ArrayList<>();
  private List<String> killerHover = new ArrayList<>();
  private List<String> disabledWorlds = new ArrayList<>();
  private List<String> mutedWorlds = new ArrayList<>();
  private List<String> ignoredPlayers = new ArrayList<>();
  private String prefix = "";
  private long spamTimeRange = 30000L;
  private int spamCount = 3;
  private HashMap<UUID, List<Long>> spamProtection = new HashMap<>();
  private boolean enabled = false;
  private int autoHideFrom = -1;
  private int range = -1;
  private String destination = "plain";

  public DeathMessageManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void relocateDeathMessageLocaleFiles() {
    File file =
        new File(this.plugin.getDataFolder(), "Translations" + File.separator + "DeathMessages");
    if (!file.isDirectory()) {
      file.mkdir();
    }

    File messagesFile =
        new File(this.plugin.getDataFolder(), "Settings" + File.separator + "DeathMessages");
    if (messagesFile.isDirectory()) {
      File[] files;
      int value = (files = messagesFile.listFiles()).length;

      for (int index = 0; index < value; index++) {
        File messageFile = files[index];
        File copyFile =
            new File(
                file,
                messageFile.getName().substring(0, 1).toUpperCase()
                    + messageFile.getName().substring(1, messageFile.getName().length()));
        if (!copyFile.isFile()) {
          messageFile.renameTo(copyFile);
        }
      }

      messagesFile.delete();
    }
  }

  public void defaultLocaleDownloader() {
    try {
      var list = Arrays.asList("EN", "ES", "CN", "FR");
      String string = null;
      boolean flag = true;

      try {
        File file = new File(this.plugin.getDataFolder(), "config.yml");
        if (file.isFile()) {
          YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
          flag = yamlConfiguration.getBoolean("LanguageDownload", true);
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }

      if (!flag) {
        return;
      }

      for (String key : list) {
        File targetFile =
            new File(
                this.plugin.getDataFolder()
                    + File.separator
                    + "Translations"
                    + File.separator
                    + "DeathMessages",
                "Locale_" + key + ".yml");
        if (!targetFile.isFile()) {
          string = key;
        }
      }

      final String messageKey = string;

      for (final String entryKey : list) {
        File entryFile =
            new File(
                this.plugin.getDataFolder()
                    + File.separator
                    + "Translations"
                    + File.separator
                    + "DeathMessages",
                "Locale_" + entryKey + ".yml");
        if (!entryFile.isFile()) {
          FileDownloader fileDownloader =
              new FileDownloader() {
                @Override
                public void afterDownload() {
                  CMI.getInstance()
                      .consoleMessage("Downloaded Death Messages Locale_" + entryKey + ".yml file");
                  if (messageKey != null && entryKey.equalsIgnoreCase(messageKey)) {
                    CMI.getInstance().getConfigManager().reloadLanguage();
                  }
                }
              };
          fileDownloader.downloadUsingStream(
              "https://raw.githubusercontent.com/Zrips/CMI/master/Settings/DeathMessages/Locale_"
                  + entryKey
                  + ".yml",
              entryFile.getPath());
        }
      }
    } catch (Throwable throwable2) {
      throwable2.printStackTrace();
    }
  }

  public void load() {
    this.loadConfig();
    this.loadLocale();
  }

  private void loadConfig() {
    ConfigReader configReader = null;

    try {
      configReader =
          new ConfigReader(this.plugin, "Settings" + File.separator + "DeathMessages.yml");
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      ConfigReader deathConfig = this.plugin.getConfigManager().getConfig();
      if (deathConfig.getC().isInt("Messages.DeathMessage.AutoHideFrom")
          && !configReader.getC().isInt("AutoHideFrom")) {
        configReader.set(
            "AutoHideFrom", deathConfig.getC().getInt("Messages.DeathMessage.AutoHideFrom"));
        configReader.save();

        try {
          configReader =
              new ConfigReader(this.plugin, "Settings" + File.separator + "DeathMessages.yml");
        } catch (Exception exception2) {
          exception2.printStackTrace();
          return;
        }
      }

      configReader.addComment(
          "EnableCustom",
          new String[] {
            "Enable or disable custom death messages",
            "Check DeathMessages dirrectory for message options",
            "",
            "Not all death situations might be included and future updates might be needed",
            "Report any missing death situation you encountered to github issue section with exact"
                + " way to reproduce death situation"
          });
      this.enabled = configReader.get("EnableCustom", false);
      configReader.addComment(
          "AutoHideFrom",
          new String[] {
            "Defines number of players from which to automatically start hiding death messages",
            "Set to -1 to disable this"
          });
      this.autoHideFrom = configReader.get("AutoHideFrom", -1);
      configReader.addComment(
          "Range",
          new String[] {
            "Range in blocks we should broadcast players death message",
            "Setting this to any range will automatically exclude players in other worlds"
          });
      this.range = configReader.get("Range", -1);
      configReader.addComment(
          "Destination",
          new String[] {"Where should we should death message", "Options: plain, actionBar"});
      this.destination = configReader.get("Destination", "plain");
      configReader.addComment(
          "DisabledWorlds",
          new String[] {"List of worlds where deaths from them are never announced"});
      this.disabledWorlds = configReader.get("DisabledWorlds", Arrays.asList(""));
      CMIList.toLowerCase(this.disabledWorlds);
      configReader.addComment(
          "MutedWorlds", new String[] {"List of worlds where deaths are not announced to players"});
      this.mutedWorlds = configReader.get("MutedWorlds", Arrays.asList(""));
      CMIList.toLowerCase(this.mutedWorlds);
      configReader.addComment(
          "IgnoredPlayers",
          new String[] {"List of player names we should ignore and not show their death messages"});
      this.ignoredPlayers = configReader.get("IgnoredPlayers", Arrays.asList(""));
      CMIList.toLowerCase(this.ignoredPlayers);
      configReader.addComment(
          "AntiSpam.TimeRange",
          new String[] {
            "Time range in seconds we should check when preventing death message spam",
            "Set to -1 if you want to disable it",
            "Keep in mind that player who dies will continue to see his own death messages even if"
                + " its over the limit"
          });
      this.spamTimeRange = configReader.get("AntiSpam.TimeRange", 30) * 1000L;
      configReader.addComment(
          "AntiSpam.Count",
          new String[] {
            "How many death messages we should broadcast from player in previously defined time"
                + " frame"
          });
      this.spamCount = configReader.get("AntiSpam.Count", 3);
      configReader.save();
    }
  }

  private void loadLocale() {
    this.defaultLocaleDownloader();
    File file =
        new File(
            CMI.getInstance().getDataFolder(), "Translations" + File.separator + "DeathMessages");
    if (!file.isDirectory()) {
      file.mkdir();
    }

    ConfigReader configReader = null;

    try {
      configReader =
          new ConfigReader(
              CMI.getInstance(),
              "Translations"
                  + File.separator
                  + "DeathMessages"
                  + File.separator
                  + "Locale_"
                  + this.plugin.getConfigManager().Lang
                  + ".yml");
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    if (configReader == null && !this.plugin.getConfigManager().Lang.equalsIgnoreCase("EN")) {
      this.plugin.consoleMessage(
          "Failed to load death message ("
              + this.plugin.getConfigManager().Lang
              + ") locale file. Trying to default to EN version");

      try {
        configReader =
            new ConfigReader(
                CMI.getInstance(),
                "Translations"
                    + File.separator
                    + "DeathMessages"
                    + File.separator
                    + "Locale_EN.yml");
      } catch (Throwable throwable2) {
        throwable2.printStackTrace();
      }
    }

    if (configReader == null) {
      this.plugin.consoleMessage("Failed to load death message locale file");
    } else {
      configReader.addHeaderComments(
          Arrays.asList(
              "[mobName] - monsters name when its applicable. This can be translated.",
              "[prevMobName] - monsters name when its applicable. This can be translated.",
              "[item] - items display name which was used to kill player. If display name isint"
                  + " present then items material name will be used. This can be translated.",
              "[itemName] - items material name which was used to kill player. This can be"
                  + " translated.",
              "[playerName] - players who died original name",
              "[playerDisplayName] - players who died modified name",
              "[sourceName] - players who caused damage original name",
              "[sourceDisplayName] - players who caused damage modified name"));
      configReader.addComment("Prefix", new String[] {"Defines prefix for all death messages"});
      this.prefix = configReader.get("Prefix", "\ud83d\udc80");
      configReader.addComment(
          "PlayerHover",
          new String[] {
            "Defines hover over infomation about player who died. Use basic placeholders like"
                + " %cmi_user_name% to include any information you would want",
            "Set this like to '' if you want to avoid including hover over message"
          });
      this.playerHover = configReader.get("PlayerHover", Arrays.asList(""));
      if (this.playerHover.size() == 1 && this.playerHover.get(0).isEmpty()) {
        this.playerHover.clear();
      }

      configReader.addComment(
          "KillerHover",
          new String[] {
            "Defines hover over infomation about player who killed. Use basic placeholders like"
                + " %cmi_user_name% to include any information you would want",
            "Set this like to '' if you want to avoid including hover over message"
          });
      this.killerHover = configReader.get("KillerHover", Arrays.asList(""));
      if (this.killerHover.size() == 1 && this.killerHover.get(0).isEmpty()) {
        this.killerHover.clear();
      }

      this.messages.clear();
      String string = "Escaping";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList(
                  " &7while escaping &2[sourceDisplayName]",
                  " &7while fighting &2[sourceDisplayName]")));
      string = "RunningAway";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList(
                  " &7while running away from &2[prevMobName]",
                  " &7while fighting &2[prevMobName]")));
      string = "Player.General";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList(
                  "&2[playerDisplayName] &7was killed by &2[sourceDisplayName]",
                  "&2[playerDisplayName] &7died by &2[sourceDisplayName] &7mighty hand",
                  "&2[playerDisplayName] &7was punched to death by &2[sourceDisplayName]")));
      string = "Player.Item";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList(
                  "&2[playerDisplayName] &7died by &2[sourceDisplayName] &7hand who used"
                      + " &2[item]")));
      string = CMIDeathType.Player + ".Projectile";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList(
                  "&2[playerDisplayName] &7was shot by &2[sourceDisplayName] &7with &2[type]")));
      string = CMIDeathType.Player + ".Fireball";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList("&2[playerDisplayName] &7was fireballed by &2[sourceDisplayName]")));
      string = CMIDeathType.Player + ".Firework";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList(
                  "&2[playerDisplayName] &7went off with a bang due to &2[sourceDisplayName]")));
      string = CMIDeathType.Player + ".Tnt";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList("&2[playerDisplayName] &7was blown up by &2[sourceDisplayName]")));
      string = CMIDeathType.FallingBlocks + ".Anvil.General";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList(
                  "&2[playerDisplayName] &7was squashed by a falling anvil",
                  "&2[playerDisplayName] &7was crushed by anvil",
                  "&2[playerDisplayName] &7hit anvil with his head")));
      string = CMIDeathType.FallingBlocks + ".Pointed_dripstone.General";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList("&2[playerDisplayName] &7was skewered by a falling stalactite")));
      string = CMIDeathType.Block + ".Cactus.General";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList(
                  "&2[playerDisplayName] &7was pricked to death",
                  "&2[playerDisplayName] &7tried to hug cactus")));
      string = CMIDeathType.Block + ".Dispenser.Arrow";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList("&2[playerDisplayName] &7was killed by dispenser with arrows...")));
      string = CMIDeathType.Block + ".Dispenser.Splash_potion";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList("&2[playerDisplayName] &7was splashed all over by dispenser...")));
      string = CMIDeathType.Block + ".Magma_block.General";
      this.addMessages(
          string,
          configReader.get(
              string, Arrays.asList("&2[playerDisplayName] &7discovered the floor was lava")));
      string = CMIDeathType.Block + ".Sweet_berry_bush.General";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList("&2[playerDisplayName] &7was poked to death by a sweet berry bush")));
      string = CMIDeathType.Block + ".Campfire.General";
      this.addMessages(
          string,
          configReader.get(
              string, Arrays.asList("&2[playerDisplayName] &7got roasted on campfire")));
      string = CMIDeathType.Block + ".Soul_campfire.General";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7soul was taken away")));
      string = CMIDeathType.Block + ".Lava.General";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7tried to swim in lava")));
      string = CMIDeathType.Block + ".Pointed_dripstone.General";
      this.addMessages(
          string,
          configReader.get(
              string, Arrays.asList("&2[playerDisplayName] &7was impaled on stalagmite")));
      string = CMIDeathType.Custom + ".Block_explosion.General";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList("&2[playerDisplayName] &7was killed by [Intentional Game Design]")));
      string = CMIDeathType.Custom + ".World_border.General";
      this.addMessages(
          string,
          configReader.get(
              string, Arrays.asList("&2[playerDisplayName] &7left the confines of this world")));
      string = CMIDeathType.Custom + ".Starvation.General";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7starved to death")));
      string = CMIDeathType.Projectile + ".Arrow";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7was shot by arrow")));
      string = CMIDeathType.Projectile + ".Spectral_arrow";
      this.addMessages(
          string,
          configReader.get(
              string, Arrays.asList("&2[playerDisplayName] &7was shot by spectral arrow")));
      string = CMIDeathType.Projectile + ".Tipped_arrow";
      this.addMessages(
          string,
          configReader.get(
              string, Arrays.asList("&2[playerDisplayName] &7was shot by tipped arrow")));
      string = CMIDeathType.Projectile + ".Fireball";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7was fireballed")));
      string = CMIDeathType.Projectile + ".Firework";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7went off with a bang")));
      string = CMIDeathType.Custom + ".Fall.Low.General";
      this.addMessages(
          string,
          configReader.get(
              string, Arrays.asList("&2[playerDisplayName] &7hit the ground too hard")));
      string = CMIDeathType.Custom + ".Fall.High.General";
      this.addMessages(
          string,
          configReader.get(
              string, Arrays.asList("&2[playerDisplayName] &7fell from a high place")));
      string = CMIDeathType.Custom + ".Fire.General";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7went up in flames")));
      string = CMIDeathType.Custom + ".Fire_tick.General";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7burned to death")));
      string = CMIDeathType.Custom + ".Lightning.General";
      this.addMessages(
          string,
          configReader.get(
              string, Arrays.asList("&2[playerDisplayName] &7was struck by lightning")));
      string = CMIDeathType.Custom + ".Suffocation.General";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7suffocated in a wall")));
      string = CMIDeathType.Custom + ".Custom.General";
      this.addMessages(
          string, configReader.get(string, Arrays.asList("&2[playerDisplayName] &7died")));
      string = CMIDeathType.Custom + ".Drowning.General";
      this.addMessages(
          string, configReader.get(string, Arrays.asList("&2[playerDisplayName] &7drowned")));
      string = CMIDeathType.Custom + ".Freeze.General";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7frozen to death")));
      string = CMIDeathType.Custom + ".Suicide.General";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7took their own life")));
      string = CMIDeathType.Custom + ".Void.General";
      this.addMessages(
          string,
          configReader.get(string, Arrays.asList("&2[playerDisplayName] &7fell out of the world")));
      string = CMIDeathType.Custom + ".EndCrystal.General";
      this.addMessages(
          string,
          configReader.get(
              string, Arrays.asList("&2[playerDisplayName] &7shattered like a crystal")));
      string = CMIDeathType.Custom + ".Wither.General";
      this.addMessages(
          string, configReader.get(string, Arrays.asList("&2[playerDisplayName] &7withered away")));
      string = CMIDeathType.Custom + ".Fly_into_wall.General";
      this.addMessages(
          string,
          configReader.get(
              string,
              Arrays.asList(
                  "&2[playerDisplayName] &7experienced kinetic energy",
                  "&2[playerDisplayName] &7learned what is conservation of momentum")));
      string = CMIDeathType.Mob + ".Primed_tnt.General";
      this.addMessages(
          string, configReader.get(string, Arrays.asList("&2[playerDisplayName] &7was blown up")));
      string = CMIDeathType.Mob + ".Tnt_minecart.General";
      this.addMessages(
          string, configReader.get(string, Arrays.asList("&2[playerDisplayName] &7blew up")));
      string = CMIDeathType.Mob + ".Area_effect_cloud.General";
      this.addMessages(
          string,
          configReader.get(
              string, Arrays.asList("&2[playerDisplayName] &7was killed by magic cloud")));
      EntityType[] entityTypes;
      int value = (entityTypes = EntityType.values()).length;

      for (int index = 0; index < value; index++) {
        EntityType entityType = entityTypes[index];
        if (entityType.isAlive() && entityType.isSpawnable()) {
          String entityName = CMIText.firstToUpperCase(entityType.toString()).replace(" ", "_");
          string = CMIDeathType.Mob + "." + entityName + ".General";
          this.addMessages(
              string,
              configReader.get(
                  string, Arrays.asList("&2[playerDisplayName] &7killed by &2[mobName]")));
          string = CMIDeathType.Mob + "." + entityName + ".Item";
          this.addMessages(
              string,
              configReader.get(
                  string,
                  Arrays.asList("&2[playerDisplayName] &7killed by &2[mobName] &7with &2[item]")));
        }
      }

      configReader.save();
    }
  }

  private void addMessages(String string, List<String> list) {
    this.messages.put(string, new DeathMessages(string, list));
  }

  @Nonnull
  public String getDeathMessage(String string) {
    string = string.replace(" ", "_");
    DeathMessages deathMessages = this.messages.get(string);
    return deathMessages != null ? deathMessages.getRandom() : "";
  }

  private List<Player> getPlayersFromRange(Location location, int value) {
    ArrayList<Player> arrayList = new ArrayList();

    for (Player player : Bukkit.getOnlinePlayers()) {
      Location playerLocation = player.getLocation();
      if (value < 0) {
        if (!this.isInMutedWorld(player.getWorld())) {
          arrayList.add(player);
        }
      } else if (playerLocation.getWorld() == location.getWorld()
          && playerLocation.getBlockX() <= location.getBlockX() + value
          && playerLocation.getBlockX() >= location.getBlockX() - value
          && playerLocation.getBlockY() <= location.getBlockY() + value
          && playerLocation.getBlockY() >= location.getBlockY() - value
          && playerLocation.getBlockZ() <= location.getBlockZ() + value
          && playerLocation.getBlockZ() >= location.getBlockZ() - value) {
        arrayList.add(player);
      }
    }

    return arrayList;
  }

  public boolean broadcastDeathMessage(Player player, RawMessage rawMessage) {
    rawMessage.show(Bukkit.getConsoleSender());
    if (!this.isInDisabledWorld(player.getWorld())
        && !this.ignoredPlayers.contains(player.getName().toLowerCase())) {
      this.addSpamRecord(player.getUniqueId());
      if (!CMIUser.getUser(player).getOptionState(PlayerOption.deathMessages)) {
        return false;
      }

      HashSet<Player> hashSet = new HashSet();
      boolean flag = this.isUnderSpamCooldown(player.getUniqueId());
      if (!flag) {
        hashSet.add(player);
      } else {
        hashSet.addAll(this.getPlayersFromRange(player.getLocation(), this.range));
      }

      if (this.autoHideFrom > 0 && this.autoHideFrom <= hashSet.size()) {
        return false;
      }

      if (this.destination.equalsIgnoreCase("plain")) {
        rawMessage.show(hashSet);
      } else {
        CMIActionBar.send(new ArrayList(hashSet), rawMessage.getTextOnly());
      }

      return !flag;
    } else {
      return false;
    }
  }

  private void addSpamRecord(UUID uuid) {
    if (this.spamTimeRange >= 0L) {
      var list = this.spamProtection.getOrDefault(uuid, new ArrayList<>());
      list.add(System.currentTimeMillis());
      this.spamProtection.put(uuid, list);
    }
  }

  private boolean isUnderSpamCooldown(UUID uuid) {
    if (this.spamTimeRange < 0L) {
      return true;
    }

    var list = this.spamProtection.getOrDefault(uuid, new ArrayList<>());
    if (list.isEmpty()) {
      return true;
    }

    Iterator iterator = list.iterator();

    while (iterator.hasNext()) {
      Long lastTimestamp = (Long) iterator.next();
      if (lastTimestamp + this.spamTimeRange < System.currentTimeMillis()) {
        iterator.remove();
      }
    }

    return list.size() <= this.spamCount;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public int getAutoHideFrom() {
    return this.autoHideFrom;
  }

  public List<String> getPlayerHover() {
    return new ArrayList<>(this.playerHover);
  }

  public List<String> getKillerHover() {
    return new ArrayList<>(this.killerHover);
  }

  private boolean isInMutedWorld(World world) {
    return world != null && world.getName() != null ? this.isInMutedWorld(world.getName()) : false;
  }

  private boolean isInMutedWorld(String string) {
    return this.mutedWorlds.contains(string.toLowerCase());
  }

  private boolean isInDisabledWorld(World world) {
    return world != null && world.getName() != null
        ? this.isInDisabledWorld(world.getName())
        : false;
  }

  private boolean isInDisabledWorld(String string) {
    return this.disabledWorlds.contains(string.toLowerCase());
  }

  public String getPrefix() {
    return this.prefix;
  }
}
