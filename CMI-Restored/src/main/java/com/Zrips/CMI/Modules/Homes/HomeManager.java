package com.Zrips.CMI.Modules.Homes;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Warps.CmiWarp;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.back;
import com.Zrips.CMI.commands.list.dback;
import com.Zrips.CMI.commands.list.home;
import com.Zrips.CMI.commands.list.homes;
import com.Zrips.CMI.commands.list.removehome;
import com.Zrips.CMI.commands.list.sethome;
import com.Zrips.CMI.utils.SpawnUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.MaterialPicker;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeManager {
  private CMI plugin;
  HashMap<String, Integer> homeGroups = new HashMap<>();
  private HashMap<String, HomeWorldLimit> homeLimits = new HashMap<>();
  private boolean checkBlockbreak = false;
  private boolean pickRealBlock = false;
  private boolean homesWorldLimits = false;
  private boolean confirmation = true;
  private String defaultHomeName = "Home";
  private String defaultBedHomeName = "Home";
  private String homeNameRegex = "/([\\p{L}-]+)/ug";
  private int respawnImmortality = 0;
  private int HomesMaxGuiSlots = 99;
  private boolean HomesGui = true;
  private boolean HomesGuiComplex = true;
  private boolean guiExpanded = false;
  private CMIItemStack guiBorder = null;
  private CMIItemStack guiInfill = null;
  private boolean guiInfoButton = true;
  private int guiInfoButtonSlot = 45;
  private CMIItemStack guiInfoButtonItem = null;
  private List<String> guiInfoButtonCommands = null;
  private boolean guiCloseButton = true;
  private int guiCloseButtonSlot = 45;
  private CMIItemStack guiCloseButtonItem = null;
  private List<String> guiCloseButtonCommands = null;
  private boolean guiBedButton = true;
  private int guiBedButtonSlot = 45;
  private CMIItemStack guiBedButtonItem = null;
  private boolean guiHomeButton = true;
  private int guiHomeButtonSlot = 45;
  private CMIItemStack guiHomeButtonItem = null;
  private boolean guiDeathButton = true;
  private boolean guiBackButton = true;
  private int guiDeathButtonSlot = 45;
  private CMIItemStack guiDeathButtonItem = null;
  private int guiBackButtonSlot = 45;
  private CMIItemStack guiBackButtonItem = null;
  private int guiPrevButtonSlot = 49;
  private int guiMiddleButtonSlot = 50;
  private int guiNextButtonSlot = 51;
  private boolean homesBedInteraction = true;
  private boolean onlyShiftBed = false;
  private boolean removeBedLocationOnBedBreak = true;
  private HashMap<String, List<String>> ReSpawnPriorityOrder = new HashMap<>();
  private HashMap<String, HashMap<CMIUser, CmiHome>> bedHomes = new HashMap<>();
  private boolean limitsBlockExtra = false;
  private boolean limitsNotifyOnLogin = false;

  public HomeManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void addHomeGroup(String string, int value) {
    this.homeGroups.put(string, value);
  }

  public boolean canUseHomes(CommandSender commandSender) {
    if (!(commandSender instanceof Player)) {
      return true;
    }

    if (!this.isLimitsBlockExtra()) {
      return true;
    }

    int value = this.getMaxHomes(commandSender);
    int homeCount = CMIUser.getUser((Player) commandSender).getValidHomeCount();
    return homeCount <= value;
  }

  public int getMaxHomes(CommandSender commandSender) {
    return commandSender instanceof Player ? this.getMaxHomes((Player) commandSender) : 999;
  }

  public int getMaxHomes(Player player) {
    int value = 1;
    if (player != null && player.isOnline()) {
      if (PermissionsManager.CMIPerm.command_sethome_unlimited.hasPermission(player)) {
        return 9999;
      }

      for (var entry : this.homeGroups.entrySet()) {
        if (PermissionsManager.CMIPerm.command_sethome_$1.hasPermission(
                player, (String) entry.getKey())
            && (Integer) entry.getValue() > value) {
          value = (Integer) entry.getValue();
        }
      }

      PermissionInfo permissionInfo =
          this.plugin
              .getPermissionsManager()
              .getPermissionInfo(player, PermissionsManager.CMIPerm.command_sethome_$1);
      if (permissionInfo.getMaxValue() != null && permissionInfo.getMaxValue() > value) {
        value = permissionInfo.getMaxValue().intValue();
      }

      return value;
    } else {
      return value;
    }
  }

  public void loadConfig() {
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(this.plugin, "Settings" + File.separator + "Homes.yml");
    } catch (Exception exception) {
      exception.printStackTrace();
      return;
    }

    ConfigReader homeConfig = this.plugin.getConfigManager().getConfig();
    if (homeConfig.getC().isConfigurationSection("Homes")
        && !configReader.getC().isConfigurationSection("Homes")) {
      configReader.set("Homes", homeConfig.getC().getConfigurationSection("Homes"));
      configReader.save();

      try {
        configReader = new ConfigReader(this.plugin, "Settings" + File.separator + "Homes.yml");
      } catch (Exception exception2) {
        exception2.printStackTrace();
        return;
      }
    }

    configReader.addComment(
        "Homes.Confirmation",
        new String[] {
          "When enabled we will require player to click on ingame chat to confirm home location"
              + " override in case home with same name already exist",
          "This can be dirrectly confirmed with /cmi sethome [homeName] -override"
        });
    this.confirmation = configReader.get("Homes.Confirmation", true);
    configReader.addComment(
        "Homes.MaxGuiSlots",
        new String[] {
          "This defines max slot number player could provide while creating home with /sethome"
              + " [homeName] [slotNumber]"
        });
    this.HomesMaxGuiSlots = configReader.get("Homes.MaxGuiSlots", 99);
    configReader.addComment(
        "Homes.Gui.Use",
        new String[] {
          "Open gui when using /cmi home command if you have more then one home in your saved list"
        });
    this.HomesGui =
        configReader.get("Homes.Gui.Use", configReader.getC().getBoolean("Homes.Gui", true));
    configReader.addComment(
        "Homes.Gui.Complex",
        new String[] {
          "When enabled we will create more complex home UI with extra buttons and options while"
              + " keeping this disabled we will simply list homes in a row"
        });
    this.HomesGuiComplex = configReader.get("Homes.Gui.Complex", true);
    configReader.addComment(
        "Homes.Gui.Options.Expanded",
        new String[] {"When enabled we will expand area for homes from 10 to 28"});
    this.guiExpanded = configReader.get("Homes.Gui.Options.Expanded", false);
    configReader.addComment(
        "Homes.Gui.Options.Border",
        new String[] {"Keep it empty if you want to use global infill material"});
    this.guiBorder =
        CMILib.getInstance()
            .getItemManager()
            .getItem(configReader.get("Homes.Gui.Options.Border", ""));
    configReader.addComment(
        "Homes.Gui.Options.Infill",
        new String[] {"Material around homes when its not in expanded state"});
    this.guiInfill =
        CMILib.getInstance()
            .getItemManager()
            .getItem(configReader.get("Homes.Gui.Options.Infill", "WHITE_STAINED_GLASS_PANE"));
    String string = "Homes.Gui.Options.InfoButton.";
    configReader.addComment(
        string + "Use",
        new String[] {
          "Extra button to be used in case you want to provide any aditional information when"
              + " clicking on it"
        });
    this.guiInfoButton = configReader.get(string + "Use", false);
    this.guiInfoButtonSlot = configReader.get(string + "Slot", 1) - 1;
    this.guiInfoButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                configReader.get(
                    string + "Material",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZiYTYzMzQ0ZjQ5ZGQxYzRmNTQ4OGU5MjZiZjNkOWUyYjI5OTE2YTZjNTBkNjEwYmI0MGE1MjczZGM4YzgyIn19fQ=="));
    this.guiInfoButtonCommands = configReader.get(string + "Commands", Arrays.asList("closeinv!"));
    string = "Homes.Gui.Options.CloseButton.";
    this.guiCloseButton = configReader.get(string + "Use", true);
    this.guiCloseButtonSlot = configReader.get(string + "Slot", 9) - 1;
    this.guiCloseButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                configReader.get(
                    string + "Material",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNjYmY5ODgzZGQzNTlmZGYyMzg1YzkwYTQ1OWQ3Mzc3NjUzODJlYzQxMTdiMDQ4OTVhYzRkYzRiNjBmYyJ9fX0="));
    this.guiCloseButtonCommands = configReader.get(string + "Commands", Arrays.asList("closeinv!"));
    string = "Homes.Gui.Options.bedButton.";
    this.guiBedButton = configReader.get(string + "Use", true);
    this.guiBedButtonSlot = configReader.get(string + "Slot", 4) - 1;
    this.guiBedButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(configReader.get(string + "Material", "LIGHT_BLUE_BED"));
    string = "Homes.Gui.Options.homeButton.";
    this.guiHomeButton = configReader.get(string + "Use", true);
    this.guiHomeButtonSlot = configReader.get(string + "Slot", 5) - 1;
    this.guiHomeButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                configReader.get(
                    string + "Material",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTJkN2E3NTFlYjA3MWUwOGRiYmM5NWJjNWQ5ZDY2ZTVmNTFkYzY3MTI2NDBhZDJkZmEwM2RlZmJiNjhhN2YzYSJ9fX0="));
    string = "Homes.Gui.Options.deathButton.";
    this.guiDeathButton = configReader.get(string + "Use", true);
    this.guiDeathButtonSlot = configReader.get(string + "Slot", 6) - 1;
    this.guiDeathButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(configReader.get(string + "Material", "TOTEM_OF_UNDYING"));
    string = "Homes.Gui.Options.backButton.";
    this.guiBackButton = configReader.get(string + "Use", true);
    this.guiBackButtonSlot = configReader.get(string + "Slot", 7) - 1;
    this.guiBackButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(configReader.get(string + "Material", "BONE"));
    this.guiPrevButtonSlot = configReader.get("Homes.Gui.Options.prevButton.Slot", 46) - 1;
    this.guiMiddleButtonSlot = configReader.get("Homes.Gui.Options.middleButton.Slot", 50) - 1;
    this.guiNextButtonSlot = configReader.get("Homes.Gui.Options.nextButton.Slot", 54) - 1;
    configReader.addComment(
        "Homes.Groups",
        new String[] {
          "Homes groups to define max amount homes player can have. Permission:"
              + " cmi.command.sethome.[groupname]",
          "If players home group if not find then amount will be set to 1",
          "To have unlimited homes use cmi.command.sethome.unlimited permission",
          "In addition to this you can use simple numeric permission node"
              + " cmi.command.sethome.[number] which will define max number of homes player can"
              + " have"
        });
    if (!configReader.getC().isConfigurationSection("Homes.Groups")) {
      this.addHomeGroup("newbie", configReader.get("Homes.Groups.Newbie", 2));
      this.addHomeGroup("advanced", configReader.get("Homes.Groups.Advanced", 3));
      this.addHomeGroup("vip", configReader.get("Homes.Groups.Vip", 5));
    } else {
      try {
        var map = configReader.getC().getConfigurationSection("Homes.Groups").getValues(true);

        for (Entry entry : map.entrySet()) {
          this.addHomeGroup(
              ((String) entry.getKey()).toLowerCase(),
              configReader.get(
                  "Homes.Groups." + (String) entry.getKey(), (Integer) entry.getValue()));
        }
      } catch (Exception exception3) {
        exception3.printStackTrace();
      }
    }

    configReader.addComment(
        "Homes.Limits.BlockExtraHomes",
        new String[] {
          "Blocks homes usage when the limit is exceeded",
          "Player will not be allowed to teleport to homes if he owns more homes than his max limit"
        });
    this.limitsBlockExtra = configReader.get("Homes.Limits.BlockExtraHomes", true);
    configReader.addComment(
        "Homes.Limits.NotifyOnLogin",
        new String[] {"Notifies the player on login when they exceed the limit"});
    this.limitsNotifyOnLogin = configReader.get("Homes.Limits.NotifyOnLogin", true);
    configReader.addComment(
        "Homes.BedInteraction",
        new String[] {
          "Requires cmi.bedhome permission node",
          "When set to true by interacting with bed, regular home location will be created",
          "When set to false vanilla bed home location will be set"
        });
    this.homesBedInteraction = configReader.get("Homes.BedInteraction", true);
    configReader.addComment(
        "Homes.onlyShiftBed",
        new String[] {
          "When set to true, you can only set home location when you shift click a bed"
        });
    this.onlyShiftBed = configReader.get("Homes.onlyShiftBed", false);
    configReader.addComment(
        "Homes.RemoveBedLocationOnBedBreak",
        new String[] {
          "Removes bed location when home location was created by clicking on that bed"
        });
    this.removeBedLocationOnBedBreak = configReader.get("Homes.RemoveBedLocationOnBedBreak", true);
    configReader.addComment(
        "Homes.CheckBlockBreak",
        new String[] {
          "When enabled, plugin will check if player can break block in that area before creating"
              + " new home location",
          "This allows to prevent setting homes in protected areas",
          "Can be bypassed with cmi.command.sethome.bypass"
        });
    this.checkBlockbreak = configReader.get("Homes.CheckBlockBreak", false);
    configReader.addComment(
        "Homes.DefaultName",
        new String[] {"Name for default home location. By default its \"Home\""});
    this.defaultHomeName = configReader.get("Homes.DefaultName", "Home").replace(" ", "");
    configReader.addComment(
        "Homes.DefaultBedName",
        new String[] {
          "Name for bed location. By default its \"Home\"",
          "Different name can be used to identify home location set by interacting with a bed from"
              + " home location created with command"
        });
    this.defaultBedHomeName =
        configReader
            .get("Homes.DefaultBedName", configReader.getC().getString("Homes.DefaultName", "Home"))
            .replace(" ", "");
    configReader.addComment(
        "Homes.ValidNameRegex",
        new String[] {"Defines what characters you can use in your home names"});
    this.homeNameRegex = configReader.get("Homes.ValidNameRegex", "[^a-zA-Z0-9\\-\\_]");
    if (Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
      configReader.addComment(
          "Homes.PickRealBlock",
          new String[] {
            "When set to true home icons in GUI will try to pick real blocks from that location for"
                + " easy way to see which is which",
            "When set to false, then random wool block will be used"
          });
      this.pickRealBlock = configReader.get("Homes.PickRealBlock", true);
    } else {
      this.pickRealBlock = false;
    }

    configReader.addComment(
        "Homes.WorldLimits.Use",
        new String[] {
          "When enabled home creation will be limited by defined criteria",
          "This can be bypassed if you have "
              + PermissionsManager.CMIPerm.command_sethome_bypasslimit.getPermission()
              + " permission node"
        });
    this.homesWorldLimits = configReader.get("Homes.WorldLimits.Use", false);
    this.homeLimits.clear();
    if (!configReader.getC().isConfigurationSection("Homes.WorldLimits.Groups")) {
      String groupPrefix = "Homes.WorldLimits.Groups.";
      configReader.get(groupPrefix + "Example1.Amount", 3);
      configReader.get(groupPrefix + "Example1.Worlds", Arrays.asList("testWorld"));
      configReader.get(groupPrefix + "Example2.Amount", 5);
      configReader.get(groupPrefix + "Example2.Worlds", Arrays.asList("testWorld2", "testWorld3"));
    } else {
      var set =
          configReader.getC().getConfigurationSection("Homes.WorldLimits.Groups").getKeys(false);
      boolean flag = false;

      for (String groupName : set) {
        if (!flag) {
          configReader.addComment(
              "Homes.WorldLimits.Groups." + groupName + ".Worlds",
              new String[] {
                "World list where this limit should apply to. Multiple worlds can be defined if you"
                    + " want to group them up"
              });
          configReader.addComment(
              "Homes.WorldLimits.Groups." + groupName + ".Amount",
              new String[] {
                "Amount of homes you can have in this world home group. Setting to 0 would"
                    + " basically disable home creation in this world. Setting to lower than 0 will"
                    + " ignore this world group entirely"
              });
        }

        flag = true;
        int value = configReader.get("Homes.WorldLimits.Groups." + groupName + ".Amount", 3);
        var list =
            configReader.get(
                "Homes.WorldLimits.Groups." + groupName + ".Worlds", Arrays.asList(""));
        HomeWorldLimit homeWorldLimit = new HomeWorldLimit();
        homeWorldLimit.setWorlds(list);
        homeWorldLimit.setLimit(value);

        for (String worldName : list) {
          this.homeLimits.put(worldName.toLowerCase(), homeWorldLimit);
        }
      }
    }

    configReader.save();
    homeConfig.addComment(
        "ReSpawn.Immortality",
        new String[] {
          "Time in seconds to make player immortal after he respawns",
          "Can be used to prevent respawn camping",
          "Set to 0 if you want to disable it",
          "Max 60 seconds"
        });
    this.respawnImmortality = homeConfig.get("ReSpawn.Immortality", 3);
    if (this.respawnImmortality > 60) {
      this.respawnImmortality = 60;
    }

    homeConfig.addComment(
        "ReSpawn.Enabled",
        new String[] {
          "If you want 3rd party plugin to handle player respawning, simply set this to false and"
              + " reload plugin"
        });
    boolean respawnEnabled = homeConfig.get("ReSpawn.Enabled", true);
    this.ReSpawnPriorityOrder.clear();
    homeConfig.addComment(
        "ReSpawn.Global.PriorityOrder",
        new String[] {
          "Defines respawn order if defined world is not present in Specific list",
          "Possible respawn locations: "
              + HomeManager.RespawnPriority.getAsString()
              + ", warp![warpName]",
          "Spawn is preset spawnlocation with /cmi setspawn command, that location should have"
              + " RespawnLocation set to true",
          "bedLocation is location set by interacting with bed, BedInteraction should be set to"
              + " false and players requires cmi.bedhome to set bed location",
          "homeLocation is location set by player which is with default (Home) name, if that one"
              + " doesnt exist then first in the list will be used if possible",
          "worldSpawn is location preset to this world, this is not CMI location but default world"
              + " spawn location",
          "anchor is location defined by interacting with respawn anchor. This in general will only"
              + " apply when you die in nether world, otherwise bed location is used",
          "warp![warpName] can be any valid warp you set for players to be teleported, they will"
              + " bypass any requirements for that warp"
        });
    var worldGroups =
        homeConfig.get(
            "ReSpawn.Global.PriorityOrder", HomeManager.RespawnPriority.getAsStringList());
    if (respawnEnabled) {
      this.ReSpawnPriorityOrder.put("defaultSet", worldGroups);
    }

    homeConfig.addComment(
        "ReSpawn.Specific",
        new String[] {
          "Defines respawn order for defined worlds",
          "Set respawn priority to [] or to random respawn criteria if you want to leave respawn"
              + " handling for server or 3rd party plugin"
        });
    if (!homeConfig.getC().isConfigurationSection("ReSpawn.Specific")) {
      for (World world : Bukkit.getWorlds()) {
        worldGroups =
            homeConfig.get(
                "ReSpawn.Specific." + world.getName(),
                HomeManager.RespawnPriority.getAsStringList());
        if (respawnEnabled) {
          this.ReSpawnPriorityOrder.put(world.getName(), worldGroups);
        }
      }
    } else {
      for (String groupWorld :
          homeConfig.getC().getConfigurationSection("ReSpawn.Specific").getKeys(false)) {
        worldGroups =
            homeConfig.get(
                "ReSpawn.Specific." + groupWorld, HomeManager.RespawnPriority.getAsStringList());
        if (respawnEnabled) {
          this.ReSpawnPriorityOrder.put(groupWorld, worldGroups);
        }
      }
    }
  }

  public boolean isHomesBedInteraction() {
    return this.homesBedInteraction;
  }

  public Location getReSpawnLocation(Player player) {
    return this.getReSpawnLocation(player, null);
  }

  public Location getReSpawnLocation(Player player, Location location) {
    if (player == null) {
      return null;
    }

    World world = player.getWorld();
    List<String> list = null;
    if (world != null) {
      list = this.ReSpawnPriorityOrder.get(world.getName());
    }

    if (list == null) {
      list = this.ReSpawnPriorityOrder.get("defaultSet");
    }

    if (list != null && !list.isEmpty() && !CMIUser.getUser(player).isJailed()) {
      for (String string : list) {
        if (!string.startsWith("warp!")) {
          HomeManager.RespawnPriority respawnPriority =
              HomeManager.RespawnPriority.getByName(string);
          if (respawnPriority != null) {
            switch (respawnPriority) {
              case anchor:
                try {
                  if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
                    Location respawnLocation = player.getRespawnLocation();
                    if (respawnLocation != null) {
                      return respawnLocation;
                    }
                  } else if (player.getBedSpawnLocation() != null
                      && player
                          .getBedSpawnLocation()
                          .getWorld()
                          .getEnvironment()
                          .equals(Environment.NETHER)
                      && location != null
                      && location
                          .clone()
                          .add(location.getDirection())
                          .getBlock()
                          .getType()
                          .equals(Material.RESPAWN_ANCHOR)) {
                    RespawnAnchor respawnAnchor =
                        (RespawnAnchor)
                            location.clone().add(location.getDirection()).getBlock().getBlockData();
                    if (respawnAnchor.getCharges() > 0) {
                      return null;
                    }
                  }
                } catch (Throwable throwable) {
                }
                break;
              case bedLocation:
                try {
                  if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
                    Location bedLocation = player.getRespawnLocation();
                    if (bedLocation != null) {
                      return bedLocation;
                    }
                  } else if (player.getBedSpawnLocation() != null
                      && !player
                          .getBedSpawnLocation()
                          .getWorld()
                          .getEnvironment()
                          .equals(Environment.NETHER)
                      && player.getBedSpawnLocation() != null) {
                    return player.getBedSpawnLocation();
                  }
                } catch (Throwable throwable2) {
                }
                break;
              case spawn:
                CMILocation cmiLocation = SpawnUtil.getGroupReSpawnPoint(player);
                if (cmiLocation != null) {
                  return cmiLocation;
                }

                cmiLocation = SpawnUtil.getSpawnPoint(player);
                if (cmiLocation != null) {
                  return cmiLocation;
                }
                break;
              case homeLocation:
                CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
                if (cmiUser == null) {
                  return null;
                }

                CmiHome cmiHome =
                    cmiUser.getHome(CMI.getInstance().getHomeManager().getDefaultHomeName());
                var arrayList = cmiUser.getHomesList();
                if (cmiHome == null && !cmiUser.getHomesList().isEmpty()) {
                  for (String homeName : arrayList) {
                    cmiHome = cmiUser.getHome(homeName);
                    if (cmiHome.getLoc().isValid()) {
                      break;
                    }
                  }
                }

                if (cmiHome != null && cmiHome.getLoc() != null) {
                  return cmiHome.getLoc();
                }
                break;
              case worldSpawn:
                Location spawnLocation = player.getWorld().getSpawnLocation();
                if (spawnLocation != null) {
                  return spawnLocation;
                }
            }
          }
        } else {
          String warpName = string.substring("warp!".length());
          CmiWarp cmiWarp = this.plugin.getWarpManager().getWarp(warpName);
          if (cmiWarp != null) {
            return cmiWarp.getLocIncRand();
          }
        }
      }

      return null;
    } else {
      return null;
    }
  }

  public boolean isCheckBlockbreak() {
    return this.checkBlockbreak;
  }

  public String getDefaultHomeName() {
    return this.defaultHomeName;
  }

  public String getDefaultBedHomeName() {
    return this.defaultBedHomeName;
  }

  public HashMap<String, HashMap<CMIUser, CmiHome>> getBedHomes() {
    return this.bedHomes;
  }

  public void addBedHome(CMIUser cmiUser, CmiHome cmiHome) {
    String string = this.plugin.getPlayerManager().convertBlockLocToString(cmiHome.getLoc());
    HashMap<CMIUser, CmiHome> hashMap = new HashMap();
    hashMap.put(cmiUser, cmiHome);
    cmiHome.setBed(true);
    this.bedHomes.put(string, hashMap);
  }

  public boolean removeBedHome(Location location) {
    String string = this.plugin.getPlayerManager().convertBlockLocToString(location);
    var hashMap = this.bedHomes.remove(string);
    if (hashMap == null) {
      return false;
    }

    for (Entry entry : hashMap.entrySet()) {
      ((CMIUser) entry.getKey()).removeHome((CmiHome) entry.getValue());

      for (var homeEntry : new HashMap<>(((CMIUser) entry.getKey()).getHomes()).entrySet()) {
        if (this.plugin
            .getPlayerManager()
            .convertBlockLocToString(((CmiHome) homeEntry.getValue()).getLoc())
            .equals(string)) {
          ((CMIUser) entry.getKey()).removeHome((CmiHome) homeEntry.getValue());
        }
      }

      ((CMIUser) entry.getKey()).addForDelayedSave();
    }

    return false;
  }

  public boolean isRemoveBedLocationOnBedBreak() {
    return this.removeBedLocationOnBedBreak;
  }

  public boolean openHomeGui(Player player, CMIUser cmiUser, int value) {
    if (value < 1) {
      value = 1;
    }

    return this.isHomesGuiComplex()
        ? this.openComplexHomeGui(player, cmiUser, value)
        : this.openSimpleHomeGui(player, cmiUser, value);
  }

  public boolean openComplexHomeGui(final Player player, final CMIUser cmiUser, final int value) {
    if (cmiUser != null && player != null && player.isOnline()) {
      Snd snd = new Snd();
      snd.setTarget(cmiUser);
      snd.setSource(cmiUser);
      CMIGui cmiGui =
          new CMIGui(player) {
            public void pageChange(int page) {
              HomeManager.this.openComplexHomeGui(player, cmiUser, page);
            }
          };
      cmiGui.setInvSize(GUIRows.r6);
      int columns = this.guiExpanded ? 7 : 5;
      int slotsPerPage = this.guiExpanded ? columns * 4 : columns * 2;
      int startRow = this.guiExpanded ? 2 : 3;
      int currentRow = startRow;
      int startSlot = startRow - 1 + (currentRow - 1) * 9;
      if (!this.guiExpanded) {
        int pageRows = slotsPerPage / columns;

        for (int colIndex = startRow - 1; colIndex <= startRow + columns; colIndex++) {
          for (int rowIndex = currentRow - 1; rowIndex <= currentRow + pageRows; rowIndex++) {
            if (rowIndex == currentRow + pageRows
                || rowIndex == currentRow - 1
                || colIndex == startRow - 1
                || colIndex == startRow + columns) {
              int slotIndex = (rowIndex - 1) * 9 + colIndex - 1;
              CMIGuiButton cmiGuiButton = new CMIGuiButton(slotIndex, this.guiInfill);
              cmiGuiButton.setName(" ");

              try {
                cmiGuiButton.hideToltip();
              } catch (Throwable throwable) {
              }

              cmiGui.addButton(cmiGuiButton);
            }
          }
        }
      }

      TreeMap<Integer, CmiHome> treeMap = new TreeMap();
      boolean flag = false;
      LinkedHashSet<CmiHome> linkedHashSet = new LinkedHashSet();
      if (value == 1) {
        for (CmiHome cmiHome : cmiUser.getHomes().values()) {
          if (cmiHome.isFavorite()) {
            flag = true;
            linkedHashSet.add(cmiHome);
            if (linkedHashSet.size() == slotsPerPage) {
              break;
            }
          }
        }
      } else {
        for (CmiHome homeItem : cmiUser.getHomes().values()) {
          if (homeItem.isFavorite()) {
            flag = true;
          }
        }
      }

      if (!flag || value > 1 && flag) {
        linkedHashSet.addAll(cmiUser.getHomes().values());
      }

      if (value > 1 && flag) {
        for (int homeIndex = 0; homeIndex < slotsPerPage; homeIndex++) {
          treeMap.put(homeIndex, null);
        }
      }

      final PageInfo pageInfo =
          new PageInfo(slotsPerPage, cmiUser.getLastHomeSlot() + (flag ? 1 : 0), value);
      if (flag) {
        pageInfo.setTotalPages(pageInfo.getTotalPages() + 1);
      }

      for (CmiHome sortedHome : linkedHashSet) {
        if (sortedHome.getSlot() != null
            && sortedHome.getSlot() != 0
            && (value <= 1 || !sortedHome.isFavorite())) {
          int slotCandidate = sortedHome.getSlot() - 1;
          if (treeMap.containsKey(slotCandidate)) {
            int slotCounter = 0;

            for (int slotScanIndex = 0;
                slotScanIndex < treeMap.size() + linkedHashSet.size();
                slotScanIndex++) {
              if (!treeMap.containsKey(slotCandidate + slotScanIndex)) {
                slotCounter = slotScanIndex;
                break;
              }
            }

            treeMap.put(slotCandidate + slotCounter, sortedHome);
          } else {
            treeMap.put(slotCandidate, sortedHome);
          }
        }
      }

      int freeSlotCount = 0;

      for (CmiHome freeHome : linkedHashSet) {
        if ((freeHome.getSlot() == null || freeHome.getSlot() == 0)
            && (value <= 1 || !freeHome.isFavorite())) {
          if (treeMap.containsKey(freeSlotCount)) {
            int occupiedCount = 0;

            for (int scanIndex = 0;
                scanIndex < treeMap.size() + linkedHashSet.size();
                scanIndex++) {
              if (!treeMap.containsKey(freeSlotCount + scanIndex)) {
                occupiedCount = scanIndex;
                break;
              }
            }

            treeMap.put(freeSlotCount + occupiedCount, freeHome);
            freeSlotCount += occupiedCount;
          } else {
            treeMap.put(freeSlotCount, freeHome);
            freeSlotCount++;
          }
        }
      }

      TreeMap<Integer, CmiHome> treeMapCopy = new TreeMap<>(treeMap);
      treeMap.clear();

      for (Entry<Integer, CmiHome> entry : treeMapCopy.entrySet()) {
        if (pageInfo.getStart() <= (Integer) entry.getKey()) {
          if (pageInfo.getStart() + slotsPerPage - 1 < (Integer) entry.getKey()) {
            break;
          }

          treeMap.put(
              (Integer) entry.getKey() - (pageInfo.getCurrentPage() - 1) * slotsPerPage,
              (CmiHome) entry.getValue());
        }
      }

      for (int pageSlotIndex = 0; pageSlotIndex < slotsPerPage; pageSlotIndex++) {
        if (treeMap.get(pageSlotIndex) == null) {
          treeMap.put(pageSlotIndex, null);
        }
      }

      String string =
          cmiUser.getName().equals(player.getName()) && !this.canUseHomes(player) ? "{gcw}" : "";
      cmiGui.setTitle(
          CMILC.getIM(
              home.class,
              "guiTitle",
              "[current]",
              string + cmiUser.getHomes().size(),
              "[max]",
              this.plugin.getHomeManager().getMaxHomes(cmiUser.getPlayer(false)),
              snd));
      final CmiHome bedHome = cmiUser.getBedHome();
      final boolean isOwner = player.getUniqueId().equals(cmiUser.getUniqueId());
      final boolean canEdit =
          PermissionsManager.CMIPerm.command_sethome_$1.hasPermission(player, "others");
      boolean canTeleport =
          PermissionsManager.CMIPerm.command_$1.hasPermission(
              player, removehome.class.getSimpleName());
      boolean homesDisabled = !this.canUseHomes(player);

      for (Entry slotEntry : treeMap.entrySet()) {
        int homeSlot = (Integer) slotEntry.getKey();
        if (++homeSlot < 0) {
          homeSlot = startSlot;
        }

        homeSlot = startSlot + homeSlot + (homeSlot - 1) / columns * (9 - columns);
        homeSlot--;
        if (slotEntry.getValue() == null) {
          CMIGuiButton homeButton = new CMIGuiButton(homeSlot);
          homeButton.setName("&6");
          homeButton.lockField();
          cmiGui.addButton(homeButton, slotsPerPage);
        } else {
          final CmiHome slotHome = (CmiHome) slotEntry.getValue();
          CMILocation cmiLocation = slotHome.getLoc();
          boolean visible = true;
          if (cmiLocation == null || cmiLocation.getWorld() == null) {
            visible = false;
          }

          final boolean visibleFinal = visible;
          CMIItemStack cmiItemStack = slotHome.getIconMaterial().newCMIItemStack();
          if (slotHome.getName().equals(this.defaultHomeName) && slotHome.getMaterial() == null) {
            cmiItemStack = this.guiHomeButtonItem;
          }

          if (bedHome != null && slotHome.equals(bedHome) && slotHome.getMaterial() == null) {
            cmiItemStack = this.guiBedButtonItem;
          }

          if (!visible && Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
            cmiItemStack = CMIMaterial.BARRIER.newCMIItemStack();
          }

          CMIGuiButton expandButton =
              new CMIGuiButton(homeSlot, cmiItemStack) {
                public void click(GUIClickType guiClickType) {
                  switch (guiClickType) {
                    case Left:
                    case Right:
                      if (visibleFinal) {
                        HomeManager.this.plugin.performCommand(
                            player,
                            CommandsHandler.getLabel()
                                + " "
                                + home.class.getSimpleName()
                                + " "
                                + slotHome.getName()
                                + " "
                                + cmiUser.getName());
                      }
                    case LeftShift:
                    case QControl:
                    default:
                      break;
                    case RightShift:
                      if (!isOwner && !canEdit) {
                        return;
                      }

                      HomeManager.this.plugin.performCommand(
                          player,
                          CommandsHandler.getLabel()
                              + " "
                              + removehome.class.getSimpleName()
                              + " "
                              + slotHome.getName()
                              + " "
                              + cmiUser.getName());
                      if (!cmiUser.getHomes().isEmpty()) {
                        HomeManager.this.openComplexHomeGui(
                            player, cmiUser, pageInfo.getCurrentPage());
                      } else {
                        player.closeInventory();
                      }
                      break;
                    case MiddleMouse:
                    case Q:
                      if (!isOwner && !canEdit) {
                        return;
                      }

                      HomeManager.this.homeEditor(player, cmiUser, slotHome, value);
                  }
                }
              };
          expandButton.hideItemFlags();
          expandButton.setName(
              "&r"
                  + (slotHome.isPrivate()
                      ? CMILC.getIM(home.class, "listPrivate")
                      : CMILC.getIM(home.class, "listNonePrivate"))
                  + slotHome.getName());
          if (visible) {
            expandButton.addLore(CMILC.getIM(home.class, "click", "[home]", slotHome.getName()));
          } else {
            expandButton.addLore(
                CMILC.getIM(
                    home.class, "notAccessible", "[worldName]", slotHome.getLoc().getWorldName()));
          }

          if (isOwner || canEdit) {
            expandButton.addLore(CMILC.getIM(homes.class, "edit"));
            if (canTeleport) {
              expandButton.addLore(CMILC.getIM(home.class, "remove"));
            }

            if (homesDisabled) {
              expandButton.addLore(CMILC.getIM(home.class, "limitReached"));
            }
          }

          cmiGui.addButton(expandButton, slotsPerPage);
        }
      }

      if (this.guiCloseButton) {
        CMIGuiButton previousButton =
            new CMIGuiButton(this.guiCloseButtonSlot, this.guiCloseButtonItem) {
              public void click(GUIClickType clickType) {
                Snd clickSound = new Snd();
                clickSound.setTarget(player);
                clickSound.setSender(player);
                clickSound.setSource(player);
                HomeManager.this
                    .plugin
                    .getSpecializedCommandManager()
                    .processCmds(
                        HomeManager.this
                            .plugin
                            .getLM()
                            .updateSnd(
                                clickSound,
                                new ArrayList<>(HomeManager.this.guiCloseButtonCommands)),
                        player);
              }
            };
        previousButton.setName(CMILC.getIM(homes.class, "closeButton"));
        cmiGui.addButton(previousButton);
      }

      if (this.guiInfoButton) {
        CMIGuiButton nextButton =
            new CMIGuiButton(this.guiInfoButtonSlot, this.guiInfoButtonItem) {
              public void click(GUIClickType clickType) {
                Snd clickSound = new Snd();
                clickSound.setTarget(player);
                clickSound.setSender(player);
                clickSound.setSource(player);
                HomeManager.this
                    .plugin
                    .getSpecializedCommandManager()
                    .processCmds(
                        HomeManager.this
                            .plugin
                            .getLM()
                            .updateSnd(
                                clickSound,
                                new ArrayList<>(HomeManager.this.guiInfoButtonCommands)),
                        player);
              }
            };
        nextButton.setName(CMILC.getIM(homes.class, "infoButton"));
        cmiGui.addButton(nextButton);
      }

      if (this.guiBedButton && bedHome != null) {
        CMIGuiButton deleteButton =
            new CMIGuiButton(this.guiBedButtonSlot, this.guiBedButtonItem) {
              public void click(GUIClickType clickType) {
                HomeManager.this.plugin.performCommand(
                    player,
                    CommandsHandler.getLabel()
                        + " "
                        + home.class.getSimpleName()
                        + " "
                        + bedHome.getName()
                        + " "
                        + player.getName());
              }
            };
        deleteButton.setName(CMILC.getIM(homes.class, "bedButton"));
        deleteButton.addLore(CMILC.getIM(homes.class, "lore", bedHome.getLoc().getBukkitLoc()));
        cmiGui.addButton(deleteButton);
      }

      if (this.guiHomeButton) {
        final CmiHome defaultHome = cmiUser.getHome(this.defaultHomeName);
        if (defaultHome != null) {
          CMIGuiButton defaultButton =
              new CMIGuiButton(this.guiHomeButtonSlot, this.guiHomeButtonItem) {
                public void click(GUIClickType clickType) {
                  HomeManager.this.plugin.performCommand(
                      player,
                      CommandsHandler.getLabel()
                          + " "
                          + home.class.getSimpleName()
                          + " "
                          + defaultHome.getName()
                          + " "
                          + player.getName());
                }
              };
          defaultButton.setName(CMILC.getIM(homes.class, "homeButton"));
          defaultButton.addLore(
              CMILC.getIM(homes.class, "lore", defaultHome.getLoc().getBukkitLoc()));
          cmiGui.addButton(defaultButton);
        }
      }

      if (this.guiDeathButton
          && PermissionsManager.CMIPerm.command_$1.hasPermission(
              player, dback.class.getSimpleName())) {
        Location location = cmiUser.getDeathLoc();
        if (location != null) {
          CMIGuiButton actionButton =
              new CMIGuiButton(this.guiDeathButtonSlot, this.guiDeathButtonItem) {
                public void click(GUIClickType clickType) {
                  HomeManager.this.plugin.performCommand(
                      player,
                      CommandsHandler.getLabel()
                          + " "
                          + dback.class.getSimpleName()
                          + " "
                          + player.getName());
                }
              };
          actionButton.setName(CMILC.getIM(homes.class, "deathButton"));
          actionButton.addLore(CMILC.getIM(homes.class, "lore", location));
          cmiGui.addButton(actionButton);
        }
      }

      if (this.guiBackButton
          && PermissionsManager.CMIPerm.command_$1.hasPermission(
              player, back.class.getSimpleName())) {
        Location lastTeleportLocation = cmiUser.getLastTeleportLocation();
        if (lastTeleportLocation != null) {
          CMIGuiButton lastTeleportButton =
              new CMIGuiButton(this.guiBackButtonSlot, this.guiBackButtonItem) {
                public void click(GUIClickType clickType) {
                  HomeManager.this.plugin.performCommand(
                      player,
                      CommandsHandler.getLabel()
                          + " "
                          + back.class.getSimpleName()
                          + " "
                          + player.getName());
                }
              };
          lastTeleportButton.setName(CMILC.getIM(homes.class, "backButton"));
          lastTeleportButton.addLore(CMILC.getIM(homes.class, "lore", lastTeleportLocation));
          cmiGui.addButton(lastTeleportButton);
        }
      }

      cmiGui.setPagePrevButtons(new Integer[] {this.guiPrevButtonSlot});
      cmiGui.setPageMiddleButtons(new Integer[] {this.guiMiddleButtonSlot});
      cmiGui.setPageNextButtons(new Integer[] {this.guiNextButtonSlot});
      cmiGui.addPagination(pageInfo);
      if (this.guiBorder != null && this.guiBorder.getCMIType() != CMIMaterial.NONE) {
        cmiGui.setFiller(this.guiBorder.getCMIType());
      }

      cmiGui.fillEmptyButtons();
      cmiGui.open();
      return true;
    } else {
      return false;
    }
  }

  private void homeEditor(
      final Player player, final CMIUser cmiUser, final CmiHome cmiHome, final int value) {
    final CMIUser viewerUser = CMIUser.getUser(player);
    CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            cmiUser.addForDelayedSave();
          }
        };
    final boolean flag =
        PermissionsManager.CMIPerm.command_sethome_iconpicker.hasPermission(player);
    cmiGui.setTitle(cmiHome.getName());
    cmiGui.setInvSize(GUIRows.r6);
    if (flag) {
      CMIGuiButton cmiGuiButton =
          new CMIGuiButton(19, cmiHome.getIconMaterial()) {
            public void click(GUIClickType guiClickType) {
              if (player.isOnline() || !flag) {
                MaterialPicker materialPicker =
                    new MaterialPicker(player) {
                      public void pickedMaterial(GUIClickType clickType, CMIMaterial cmiMaterial) {
                        cmiHome.setMaterial(cmiMaterial);
                        CMILC.info(
                            sethome.class.getSimpleName(),
                            player,
                            "setIcon",
                            "[material]",
                            cmiMaterial.getName());
                        HomeManager.this.homeEditor(player, cmiUser, cmiHome, value);
                        cmiUser.addForDelayedSave();
                      }
                    };
                materialPicker.openPicker(1, LC.info_pickIcon.getLocale(new Object[0]));
              }
            }
          };
      cmiGuiButton.addLore(
          CMILC.getIM(homes.class, "picker", "[worldName]", cmiHome.getLoc().getWorldName()));
      cmiGui.addButton(cmiGuiButton);
    }

    CMIGuiButton iconButton =
        new CMIGuiButton(
            21, cmiHome.isPrivate() ? CMIMaterial.BLUE_WOOL : CMIMaterial.LIGHT_BLUE_WOOL) {
          public void click(GUIClickType clickType) {
            cmiHome.setPrivate(!cmiHome.isPrivate());
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setItem(cmiHome.isPrivate() ? CMIMaterial.BLUE_WOOL : CMIMaterial.LIGHT_BLUE_WOOL);
            this.setName(CMILC.getIM(homes.class, "private", "[state]", cmiHome.isPrivate()));
          }
        };
    cmiGui.addButton(iconButton);
    iconButton =
        new CMIGuiButton(23, CMIMaterial.LADDER) {
          public void click(GUIClickType clickType) {
            switch (clickType) {
              case Left:
              case LeftShift:
                cmiHome.setSlot(cmiHome.getSlot() + 1);
                break;
              case Right:
              case RightShift:
                cmiHome.setSlot(cmiHome.getSlot() - 1);
            }

            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setName(CMILC.getIM(homes.class, "slot", "[slot]", cmiHome.getSlot()));
          }
        };
    cmiGui.addButton(iconButton);
    iconButton =
        new CMIGuiButton(25, CMIMaterial.ENCHANTED_GOLDEN_APPLE) {
          public void click(GUIClickType clickType) {
            if (!cmiHome.isFavorite()
                && viewerUser.getTotalFavoriteHomeCount()
                    >= (HomeManager.this.guiExpanded ? 28 : 10)) {
              CMILC.info(
                  sethome.class,
                  player,
                  "maxFavorites",
                  "[amount]",
                  HomeManager.this.guiExpanded ? 28 : 10);
            } else {
              cmiHome.setFavorite(!cmiHome.isFavorite());
              this.updateLooks();
              this.update();
            }
          }

          public void updateLooks() {
            this.setName(CMILC.getIM(homes.class, "favorite", "[state]", cmiHome.isFavorite()));
          }
        };
    cmiGui.addButton(iconButton);
    iconButton =
        new CMIGuiButton(8, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
          public void click(GUIClickType clickType) {
            if (player.isOnline()) {
              HomeManager.this.openHomeGui(player, viewerUser, value);
            }
          }
        };
    iconButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGui.addButton(iconButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  public boolean openSimpleHomeGui(final Player player, final CMIUser cmiUser, int value) {
    if (cmiUser != null && player != null && player.isOnline()) {
      Snd snd = new Snd();
      snd.setTarget(cmiUser);
      snd.setSource(cmiUser);
      CMIGui cmiGui =
          new CMIGui(player) {
            public void pageChange(int page) {
              HomeManager.this.openSimpleHomeGui(player, cmiUser, page);
            }
          };
      if (value < 1) {
        value = 1;
      }

      int slotsPerPage = cmiUser.getLastHomeSlot() > 54 ? 45 : 54;
      final PageInfo pageInfo = new PageInfo(slotsPerPage, cmiUser.getLastHomeSlot(), value);
      cmiGui.setTitle(
          CMILC.getIM(
              home.class,
              "guiTitle",
              "[current]",
              cmiUser.getHomes().size(),
              "[max]",
              this.plugin.getHomeManager().getMaxHomes(cmiUser.getPlayer(false)),
              snd));

      for (int pageStart = slotsPerPage * (pageInfo.getCurrentPage() - 1);
          pageStart <= slotsPerPage * (pageInfo.getCurrentPage() - 1) + slotsPerPage;
          pageStart++) {
        var set = cmiUser.getHomesBySlot(pageStart);
        if (!set.isEmpty()) {
          for (final CmiHome cmiHome : set) {
            CMILocation cmiLocation = cmiHome.getLoc();
            boolean flag = true;
            if (cmiLocation == null || cmiLocation.getWorld() == null) {
              flag = false;
            }

            final boolean finalFlag = flag;
            CMIMaterial cmiMaterial = cmiHome.getIconMaterial();
            if (!flag && Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
              cmiMaterial = CMIMaterial.BARRIER;
            }

            int slotOnPage =
                pageStart - 1 > slotsPerPage ? (pageStart - 1) % slotsPerPage : pageStart - 1;
            if (slotOnPage < 0) {
              slotOnPage = 0;
            }

            CMIGuiButton cmiGuiButton =
                new CMIGuiButton(slotOnPage, cmiMaterial) {
                  public void click(GUIClickType guiClickType) {
                    switch (guiClickType) {
                      case Left:
                      case Right:
                        if (finalFlag) {
                          HomeManager.this.plugin.performCommand(
                              player,
                              CommandsHandler.getLabel()
                                  + " "
                                  + home.class.getSimpleName()
                                  + " "
                                  + cmiHome.getName()
                                  + " "
                                  + cmiUser.getName());
                        }
                      case LeftShift:
                      default:
                        break;
                      case RightShift:
                        HomeManager.this.plugin.performCommand(
                            player,
                            CommandsHandler.getLabel()
                                + " "
                                + removehome.class.getSimpleName()
                                + " "
                                + cmiHome.getName()
                                + " "
                                + cmiUser.getName());
                        if (cmiUser.getLastHomeSlot() > 1) {
                          HomeManager.this.plugin.performCommand(
                              player,
                              CommandsHandler.getLabel()
                                  + " "
                                  + homes.class.getSimpleName()
                                  + " "
                                  + cmiUser.getName()
                                  + " -p:"
                                  + pageInfo.getCurrentPage());
                        } else {
                          player.closeInventory();
                        }
                    }
                  }
                };
            cmiGuiButton.hideItemFlags();
            cmiGuiButton.setName(
                "&r"
                    + (cmiHome.isPrivate()
                        ? CMILC.getIM(home.class, "listPrivate")
                        : CMILC.getIM(home.class, "listNonePrivate"))
                    + cmiHome.getName());
            if (flag) {
              cmiGuiButton.addLore(CMILC.getIM(home.class, "click", "[home]", cmiHome.getName()));
            } else {
              cmiGuiButton.addLore(
                  CMILC.getIM(
                      home.class, "notAccessible", "[worldName]", cmiHome.getLoc().getWorldName()));
            }

            cmiGuiButton.addLore(CMILC.getIM(home.class, "remove"));
            cmiGui.addButton(cmiGuiButton, slotsPerPage);
          }
        }
      }

      for (final var entry : cmiUser.getHomes().entrySet()) {
        if (!pageInfo.isContinue()) {
          if (pageInfo.isBreak()) {
            break;
          }

          CMILocation homeLocation = ((CmiHome) entry.getValue()).getLoc();
          boolean visible = true;
          if (homeLocation == null || homeLocation.getWorld() == null) {
            visible = false;
          }

          final boolean visibleFinal = visible;
          if (((CmiHome) entry.getValue()).getSlot() == null) {
            CMIMaterial homeIcon = ((CmiHome) entry.getValue()).getIconMaterial();
            if (!visible && Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
              homeIcon = CMIMaterial.BARRIER;
            }

            CMIGuiButton homeButton =
                new CMIGuiButton(homeIcon) {
                  public void click(GUIClickType clickType) {
                    switch (clickType) {
                      case Left:
                      case Right:
                        if (visibleFinal) {
                          HomeManager.this.plugin.performCommand(
                              player,
                              CommandsHandler.getLabel()
                                  + " "
                                  + home.class.getSimpleName()
                                  + " "
                                  + (String) entry.getKey()
                                  + " "
                                  + cmiUser.getName());
                        }
                      case LeftShift:
                      default:
                        break;
                      case RightShift:
                        HomeManager.this.plugin.performCommand(
                            player,
                            CommandsHandler.getLabel()
                                + " "
                                + removehome.class.getSimpleName()
                                + " "
                                + (String) entry.getKey()
                                + " "
                                + cmiUser.getName());
                        if (cmiUser.getLastHomeSlot() > 1) {
                          HomeManager.this.plugin.performCommand(
                              player,
                              CommandsHandler.getLabel()
                                  + " "
                                  + homes.class.getSimpleName()
                                  + " "
                                  + cmiUser.getName()
                                  + " -p:"
                                  + pageInfo.getCurrentPage());
                        } else {
                          player.closeInventory();
                        }
                    }
                  }
                };
            homeButton.hideItemFlags();
            homeButton.setName(
                "&r"
                    + (((CmiHome) entry.getValue()).isPrivate()
                        ? CMILC.getIM(home.class, "listPrivate")
                        : CMILC.getIM(home.class, "listNonePrivate"))
                    + (String) entry.getKey());
            if (visible) {
              homeButton.addLore(
                  CMILC.getIM(
                      home.class, "click", "[home]", ((CmiHome) entry.getValue()).getName()));
            } else {
              homeButton.addLore(
                  CMILC.getIM(
                      home.class,
                      "notAccessible",
                      "[worldName]",
                      ((CmiHome) entry.getValue()).getLoc().getWorldName()));
            }

            homeButton.addLore(CMILC.getIM(home.class, "remove"));
            cmiGui.addButton(homeButton, slotsPerPage);
          }
        }
      }

      cmiGui.addPagination(pageInfo);
      cmiGui.fillEmptyButtons();
      cmiGui.open();
      return true;
    } else {
      return false;
    }
  }

  public boolean isHomesGui() {
    return this.HomesGui;
  }

  public int getRespawnImmortality() {
    return this.respawnImmortality;
  }

  public String getHomeNameRegex() {
    return this.homeNameRegex;
  }

  public void setHomeNameRegex(String string) {
    this.homeNameRegex = string;
  }

  public boolean isPickRealBlock() {
    return this.pickRealBlock;
  }

  public boolean isOnlyShiftBed() {
    return this.onlyShiftBed;
  }

  public boolean isConfirmation() {
    return this.confirmation;
  }

  public boolean isHomesWorldLimits() {
    return this.homesWorldLimits;
  }

  public void setHomesWorldLimits(boolean flag) {
    this.homesWorldLimits = flag;
  }

  public int getWorldLimit(String string) {
    HomeWorldLimit homeWorldLimit = this.getWorldLimitObject(string);
    return homeWorldLimit == null ? -1 : homeWorldLimit.getLimit();
  }

  public HomeWorldLimit getWorldLimitObject(String string) {
    return this.homeLimits.get(string.toLowerCase());
  }

  public boolean isHomesGuiComplex() {
    return this.HomesGuiComplex;
  }

  public int getGuiCloseButtonSlot() {
    return this.guiCloseButtonSlot;
  }

  public CMIItemStack getGuiCloseButtonItem() {
    return this.guiCloseButtonItem;
  }

  public int getHomesMaxGuiSlots() {
    return this.HomesMaxGuiSlots;
  }

  public boolean isLimitsBlockExtra() {
    return this.limitsBlockExtra;
  }

  public boolean isLimitsNotifyOnLogin() {
    return this.limitsNotifyOnLogin;
  }

  public enum RespawnPriority {
    anchor,
    bedLocation,
    spawn,
    homeLocation,
    worldSpawn;

    public static HomeManager.RespawnPriority getByName(String string) {
      HomeManager.RespawnPriority[] respawnPrioritys;
      int value = (respawnPrioritys = values()).length;

      for (int index = 0; index < value; index++) {
        HomeManager.RespawnPriority respawnPriority = respawnPrioritys[index];
        if (respawnPriority.name().equalsIgnoreCase(string)) {
          return respawnPriority;
        }
      }

      return null;
    }

    public static List<String> getAsStringList() {
      ArrayList<String> arrayList = new ArrayList();
      HomeManager.RespawnPriority[] respawnPrioritys;
      int value = (respawnPrioritys = values()).length;

      for (int index = 0; index < value; index++) {
        HomeManager.RespawnPriority respawnPriority = respawnPrioritys[index];
        arrayList.add(respawnPriority.name());
      }

      return arrayList;
    }

    public static String getAsString() {
      String string = "";
      HomeManager.RespawnPriority[] respawnPrioritys;
      int value = (respawnPrioritys = values()).length;

      for (int index = 0; index < value; index++) {
        HomeManager.RespawnPriority respawnPriority = respawnPrioritys[index];
        if (!string.isEmpty()) {
          string = string + ", ";
        }

        string = string + respawnPriority.name();
      }

      return string;
    }
  }
}
