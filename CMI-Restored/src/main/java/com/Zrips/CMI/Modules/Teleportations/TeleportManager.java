package com.Zrips.CMI.Modules.Teleportations;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.events.CMIPlayerTeleportRequestEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeleportManager {
  private CMI plugin;
  private TreeMap<UUID, Set<TpInfo>> tpRequests = new TreeMap<>();
  private TreeMap<UUID, Set<TpInfo>> blockedRequests = new TreeMap<>();
  private boolean SafeLocationDownThenUp;
  private int defaultTeleportInvulnerability = 2;
  private HashMap<String, Integer> TeleportInvulnerability = new HashMap<>();
  private boolean DisableInvOnBlockBreakPlace = true;
  private boolean DisableInvOnInteraction = true;
  private boolean TeleportSwitchPlaces;
  private boolean TpaCurrentLoc;
  private boolean TpahereCurrentLoc;
  private int TeleportJumpDefault;
  private int TeleportTpaTime;
  private int TeleportTpaWarmup;
  private boolean TeleportTpaMove;
  private int TeleportTpaBlock;
  private boolean TeleportDenyConfirm;
  private int TeleportTpaMaxDistance;
  private int TeleportTpaHereMaxDistance;
  private int TeleportTpBypass;
  private int BackMinDistance = 5;
  private boolean BackWithWE = true;
  private List<String> BackBlackList = new ArrayList<>();
  private List<String> DBackBlackList = new ArrayList<>();
  private int BackExpiration = 0;
  private int DeathBackExpiration = 0;
  private HashMap<TeleportManager.TpAction, Boolean> BlackListedItemsEnabledFor = new HashMap<>();
  private HashMap<Material, Integer> blockedItems = new HashMap<>();

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Optimizations.Teleport.SafeLocationDownThenUp",
        new String[] {
          "Players need to have "
              + PermissionsManager.CMIPerm.safeteleport.getPermission()
              + " permision node to be placed in a safe location when teleporting to target"
              + " location",
          "When true then while checking for safe location, we will try to determin it going down"
              + " and if it fails, then up from target location",
          "When set to false, then first of all location above target location will be checked,"
              + " then down"
        });
    this.SafeLocationDownThenUp =
        configReader.get("Optimizations.Teleport.SafeLocationDownThenUp", false);
    configReader.addComment(
        "Optimizations.Teleport.Invulnerability",
        new String[] {
          "Time in seconds player should be invulnerable after teleporting",
          "This will prevent player who teleported to damage others for defined time",
          "Set this to 0 if you want to disable it",
          "Max 60 seconds"
        });
    this.defaultTeleportInvulnerability =
        CMINumber.clamp(configReader.get("Optimizations.Teleport.Invulnerability", 5), 0, 60);
    configReader.addComment(
        "Optimizations.Teleport.InvulnerabilityPerWorld",
        new String[] {
          "Time in seconds player should be invulnerable after teleporting to this world",
          "If world isn't defined then default timer will be used"
        });

    for (String string :
        configReader.get(
            "Optimizations.Teleport.InvulnerabilityPerWorld", Arrays.asList("testWorld-5"))) {
      if (string.contains("-")) {
        String[] strings = string.split("-", 2);

        try {
          this.TeleportInvulnerability.put(
              strings[0].toLowerCase().replace("_", ""),
              CMINumber.clamp(Integer.parseInt(strings[1]), 0, 6000));
        } catch (Throwable throwable) {
          CMIMessages.consoleMessage("Incorrectly set world invulnerability (" + string + ")");
        }
      }
    }

    configReader.addComment(
        "Optimizations.Teleport.DisableInvulnerability",
        new String[] {"Situations where teleport invulnerability gets disabled"});
    configReader.addComment(
        "Optimizations.Teleport.DisableInvulnerability.OnBlockBreakPlace",
        new String[] {"Disables if player breaks or places block"});
    this.DisableInvOnBlockBreakPlace =
        configReader.get("Optimizations.Teleport.DisableInvulnerability.OnBlockBreakPlace", true);
    configReader.addComment(
        "Optimizations.Teleport.DisableInvulnerability.OnInteraction",
        new String[] {"Disables if player interacts with a block or entity"});
    this.DisableInvOnInteraction =
        configReader.get("Optimizations.Teleport.DisableInvulnerability.OnInteraction", true);
    configReader.addComment(
        "Optimizations.Teleport.SwitchPlaces",
        new String[] {
          "Set to true if you want to use tp commands as /cmi tp [WhoYouWantToTeleport]"
              + " [WhereToTeleport] when its false, its /cmi tp [whereToTeleport]"
              + " [WhoYouWantToTeleport]"
        });
    this.TeleportSwitchPlaces = configReader.get("Optimizations.Teleport.SwitchPlaces", true);
    configReader.addComment(
        "Optimizations.Teleport.CurrentLoc.tpa",
        new String[] {
          "Applies for tpa, tpahere and tpaall only",
          "If set to true then player will be teleported to current player position after accepting"
              + " teleport request",
          "If set to false then player will be teleported to player at which teleport request was"
              + " issued"
        });
    this.TpaCurrentLoc = configReader.get("Optimizations.Teleport.CurrentLoc.tpa", true);
    configReader.addComment(
        "Optimizations.Teleport.CurrentLoc.tpahere", new String[] {"Applies for tpahere only"});
    this.TpahereCurrentLoc =
        configReader.get(
            "Optimizations.Teleport.CurrentLoc.tpahere",
            configReader.getC().getBoolean("Optimizations.Teleport.CurrentLoc", true));
    configReader.addComment(
        "Optimizations.Teleport.JumpDefault",
        new String[] {
          "Default distance for jump command. Can be overriden with cmi.command.jump.[amount]"
              + " permission node"
        });
    this.TeleportJumpDefault = configReader.get("Optimizations.Teleport.JumpDefault", 50);
    configReader.addComment(
        "Optimizations.Teleport.Tpa.Time",
        new String[] {"Defines time in seconds for accepting tpa or tpahere requests"});
    this.TeleportTpaTime =
        configReader.get(
            "Optimizations.Teleport.Tpa.Time",
            configReader.getC().getInt("Optimizations.Teleport.TpaTime", 60));
    configReader.addComment(
        "Optimizations.Teleport.Tpa.Warmup",
        new String[] {
          "Defines time in seconds for player being teleported after tpa or tpahere is being"
              + " accepted"
        });
    this.TeleportTpaWarmup =
        configReader.get(
            "Optimizations.Teleport.Tpa.Warmup",
            configReader.getC().getInt("Optimizations.Teleport.TpaWarmup", 3));
    configReader.addComment(
        "Optimizations.Teleport.Tpa.Move",
        new String[] {"Defines if player can move when tpa or tpahere is being accepted"});
    this.TeleportTpaMove =
        configReader.get(
            "Optimizations.Teleport.Tpa.Move",
            configReader.getC().getBoolean("Optimizations.Teleport.TpaMove", false));
    configReader.addComment(
        "Optimizations.Teleport.Tpa.Block",
        new String[] {
          "Defines time in seconds for blocking player teleport offers after denying their request"
        });
    this.TeleportTpaBlock =
        configReader.get(
            "Optimizations.Teleport.Tpa.Block",
            configReader.getC().getInt("Optimizations.Teleport.TpaBlock", 120));
    configReader.addComment(
        "Optimizations.Teleport.Tpa.MaxDistance",
        new String[] {
          "Distance in blocks for max teleportation range",
          "Set to 0 if you want to disable it",
          "Having it at any positive number will prevent players requesting teleportations to"
              + " player in other wolds in adition to limiting it by range between players",
          "Can be bypassed by player having "
              + PermissionsManager.CMIPerm.command_tpa_max_$1.getPermission("[blockCount]")
              + " permission node"
        });
    this.TeleportTpaMaxDistance = configReader.get("Optimizations.Teleport.Tpa.MaxDistance", 0);
    configReader.addComment(
        "Optimizations.Teleport.TpaHere.MaxDistance",
        new String[] {
          "Distance in blocks for max teleportation range",
          "Set to 0 if you want to disable it",
          "Can be bypassed by player having "
              + PermissionsManager.CMIPerm.command_tpahere_max_$1.getPermission("[blockCount]")
              + " permission node"
        });
    this.TeleportTpaHereMaxDistance =
        configReader.get("Optimizations.Teleport.TpaHere.MaxDistance", 0);
    configReader.addComment(
        "Optimizations.Teleport.Tp.Bypass",
        new String[] {
          "Defines time in seconds for bypassing prevented teleportation to unsafe location"
        });
    this.TeleportTpBypass =
        configReader.get(
            "Optimizations.Teleport.Tp.Bypass",
            configReader.getC().getInt("Optimizations.Teleport.TpBypass", 15));
    configReader.addComment(
        "Optimizations.Teleport.DenyConfirm",
        new String[] {
          "When set to true adds accept and deny buttons when sending tpa or tpahere requests"
        });
    this.TeleportDenyConfirm = configReader.get("Optimizations.Teleport.DenyConfirm", true);
    configReader.addComment(
        "Optimizations.Teleport.BlackListedItems.Enabled",
        new String[] {
          "Option to prevent player teleportation when he has blacklisted items in his inventory."
              + " Can be bypassed with cmi.teleport.bypassblacklist"
        });
    Boolean flag = configReader.get("Optimizations.Teleport.BlackListedItems.Enabled", false);
    this.BlackListedItemsEnabledFor.put(
        TeleportManager.TpAction.tp,
        configReader.get("Optimizations.Teleport.BlackListedItems.EnabledFor.tp", true));
    this.BlackListedItemsEnabledFor.put(
        TeleportManager.TpAction.tpa,
        configReader.get("Optimizations.Teleport.BlackListedItems.EnabledFor.tpa", true));
    this.BlackListedItemsEnabledFor.put(
        TeleportManager.TpAction.tpahere,
        configReader.get("Optimizations.Teleport.BlackListedItems.EnabledFor.tpahere", true));
    this.BlackListedItemsEnabledFor.put(
        TeleportManager.TpAction.warp,
        configReader.get("Optimizations.Teleport.BlackListedItems.EnabledFor.warp", true));
    this.BlackListedItemsEnabledFor.put(
        TeleportManager.TpAction.home,
        configReader.get("Optimizations.Teleport.BlackListedItems.EnabledFor.home", true));
    this.BlackListedItemsEnabledFor.put(
        TeleportManager.TpAction.spawn,
        configReader.get("Optimizations.Teleport.BlackListedItems.EnabledFor.spawn", true));
    configReader.addComment(
        "Optimizations.Teleport.BlackListedItems.List",
        new String[] {
          "Item and amount (if not defined, defaults to 0) we want to protect",
          "Separate amount with : in example IronOre:5 what will limit ironOre block in players"
              + " inventory up to 5, more than that and player cant teleport"
        });
    var list =
        configReader.get(
            "Optimizations.Teleport.BlackListedItems.List",
            Arrays.asList("Diamond", "DiamondBlock", "DiamondOre", "ironore:5"));
    if (flag) {
      Iterator iterator = list.iterator();

      breakSecond:
      while (true) {
        Material material;
        int value;
        while (true) {
          if (!iterator.hasNext()) {
            break breakSecond;
          }

          String text = (String) iterator.next();
          material = CMILib.getInstance().getItemManager().getMaterial(text);
          if (material != null) {
            value = 0;

            try {
              if (text.contains(":")) {
                value = Integer.parseInt(text.split(":")[1]);
              }
              break;
            } catch (Exception exception) {
            }
          }
        }

        this.blockedItems.put(material, value);
      }
    }

    configReader.addComment(
        "Optimizations.Teleport.Back.MinDistance",
        new String[] {
          "Back location will not be triggered if player teleports closer than defined amount of"
              + " blocks"
        });
    this.BackMinDistance =
        configReader.get(
            "Optimizations.Teleport.Back.MinDistance",
            configReader.getC().getInt("Optimizations.Teleport.BackMinDistance", 5));
    configReader.addComment(
        "Optimizations.Teleport.Back.WithWE",
        new String[] {
          "When set to false we will not include teleport location when teleporting around with"
              + " worldedit compass",
          "When set to true we will include each teleport action, even if you simply jumped around"
              + " with compass"
        });
    this.BackWithWE =
        configReader.get(
            "Optimizations.Teleport.Back.WithWE",
            configReader.getC().getBoolean("Optimizations.Teleport.BackWithWE", true));
    this.BackBlackList.clear();
    configReader.addComment(
        "Optimizations.Teleport.Back.BlackList",
        new String[] {"List of worlds to which players can't return using the /back command"});
    this.BackBlackList =
        (List<String>)
            configReader.get(
                "Optimizations.Teleport.Back.BlackList",
                configReader
                    .getC()
                    .getList("Optimizations.Teleport.BackBlackList", Arrays.asList("TeztWorldz")));
    CMIList.toLowerCase(this.BackBlackList);
    configReader.addComment(
        "Optimizations.Teleport.Back.Expiration",
        new String[] {"Time in seconds /back command should expire. Set to 0 to disable"});
    this.BackExpiration =
        configReader.get(
            "Optimizations.Teleport.Back.Expiration",
            configReader.getC().getInt("Optimizations.Teleport.BackExpiration", 0));
    this.DBackBlackList.clear();
    configReader.addComment(
        "Optimizations.Teleport.DBack.BlackList",
        new String[] {"List of worlds to which players can't return using the /dback command"});
    this.DBackBlackList =
        (List<String>)
            configReader.get(
                "Optimizations.Teleport.DBack.BlackList",
                configReader
                    .getC()
                    .getList("Optimizations.Teleport.Back.BlackList", Arrays.asList("TeztWorldz")));
    CMIList.toLowerCase(this.DBackBlackList);
    configReader.addComment(
        "Optimizations.Teleport.DBack.Expiration",
        new String[] {"Time in seconds /dback command should expire. Set to 0 to disable"});
    this.DeathBackExpiration =
        configReader.get(
            "Optimizations.Teleport.DBack.Expiration",
            configReader.getC().getInt("Optimizations.Teleport.DBackExpiration", 0));
  }

  public TeleportManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void clearRequests(Player player) {
    this.tpRequests.remove(player.getUniqueId());
  }

  private void clearRequests() {
    var treeMap = new TreeMap<>(this.tpRequests);

    for (Entry entry : treeMap.entrySet()) {
      HashSet<TpInfo> hashSet = new HashSet();

      for (TpInfo tpInfo : (Set<TpInfo>) entry.getValue()) {
        if (tpInfo.getWhen() + this.TeleportTpaTime * 1000 > System.currentTimeMillis()) {
          hashSet.add(tpInfo);
        }
      }

      this.tpRequests.put((UUID) entry.getKey(), hashSet);
    }
  }

  private void clearBlocks() {
    var treeMap = new TreeMap<>(this.blockedRequests);

    for (Entry entry : treeMap.entrySet()) {
      HashSet<TpInfo> hashSet = new HashSet();

      for (TpInfo tpInfo : (Set<TpInfo>) entry.getValue()) {
        if (tpInfo.getWhen() + this.TeleportTpaBlock * 1000 > System.currentTimeMillis()) {
          hashSet.add(tpInfo);
        }
      }

      this.blockedRequests.put((UUID) entry.getKey(), hashSet);
    }
  }

  public void addTpBlock(Player player, TpInfo tpInfo) {
    var set = this.blockedRequests.get(player.getUniqueId());
    if (set == null) {
      set = new HashSet();
    }

    set.add(tpInfo);
    this.blockedRequests.put(player.getUniqueId(), set);
  }

  public boolean isBlockedRequest(
      Player player, Player playerSecond, TeleportManager.TpAction tpAction) {
    this.clearBlocks();
    var set = this.blockedRequests.get(playerSecond.getUniqueId());
    if (set != null && !set.isEmpty()) {
      for (TpInfo tpInfo : set) {
        if (tpInfo.getAction() == tpAction
            && tpInfo.getWhoAccepts().getUniqueId().equals(playerSecond.getUniqueId())
            && tpInfo.getWhoOffers().getUniqueId().equals(player.getUniqueId())) {
          return true;
        }
      }

      return false;
    } else {
      return false;
    }
  }

  public long getBlockTime(Player player, Player playerSecond, TeleportManager.TpAction tpAction) {
    this.clearRequests();
    var set = this.blockedRequests.get(playerSecond.getUniqueId());
    if (set != null && !set.isEmpty()) {
      for (TpInfo tpInfo : set) {
        if (tpInfo.getAction() == tpAction
            && tpInfo.getWhoAccepts().getUniqueId().equals(playerSecond.getUniqueId())
            && tpInfo.getWhoOffers().getUniqueId().equals(player.getUniqueId())) {
          long longValue = tpInfo.getWhen() + this.TeleportTpaBlock * 1000 + 1500L;
          return longValue - System.currentTimeMillis();
        }
      }

      return 0L;
    } else {
      return 0L;
    }
  }

  public boolean addTpRequest(
      Player player, Player playerSecond, TeleportManager.TpAction tpAction) {
    return this.addTpRequest(player, playerSecond, tpAction, true);
  }

  public boolean addTpRequest(
      Player player, Player playerSecond, TeleportManager.TpAction tpAction, boolean flag) {
    CMIPlayerTeleportRequestEvent cmiPlayerTeleportRequestEvent =
        new CMIPlayerTeleportRequestEvent(player, playerSecond, tpAction);
    Bukkit.getServer().getPluginManager().callEvent(cmiPlayerTeleportRequestEvent);
    if (cmiPlayerTeleportRequestEvent.isCancelled()) {
      return false;
    }

    var set = this.tpRequests.get(player.getUniqueId());
    TpInfo tpInfo = new TpInfo(player, playerSecond, tpAction);
    if (set == null) {
      set = new HashSet();
    }

    if (!flag) {
      switch (tpAction) {
        case tpa:
          if (!this.isTpaCurrentLoc()) {
            tpInfo.setLoc(playerSecond.getLocation());
          }
          break;
        case tpahere:
          if (!this.isTpahereCurrentLoc()) {
            tpInfo.setLoc(player.getLocation());
          }
          break;
        case tpaall:
          if (!this.isTpaCurrentLoc()) {
            tpInfo.setLoc(playerSecond.getLocation());
          }
        case tp:
        case warp:
        case home:
        case spawn:
      }
    }

    set.add(tpInfo);
    this.tpRequests.put(player.getUniqueId(), set);
    return true;
  }

  public boolean removeTpRequest(Player player, Player playerSecond) {
    var set = this.tpRequests.get(player.getUniqueId());
    if (set == null) {
      return true;
    }

    for (TpInfo tpInfo : ((Iterable<TpInfo>) new HashSet(set))) {
      if (tpInfo.getWhoAccepts().equals(playerSecond)) {
        set.remove(tpInfo);
        return true;
      }
    }

    return false;
  }

  public boolean isAlreadyInRequest(
      Player player, Player playerSecond, TeleportManager.TpAction tpAction) {
    this.clearRequests();
    var set = this.tpRequests.get(player.getUniqueId());
    if (set != null && !set.isEmpty()) {
      for (TpInfo tpInfo : set) {
        if (tpInfo.getAction() == tpAction
            && tpInfo.getWhoAccepts().getUniqueId().equals(playerSecond.getUniqueId())
            && tpInfo.getWhoOffers().getUniqueId().equals(player.getUniqueId())) {
          return true;
        }
      }

      return false;
    } else {
      return false;
    }
  }

  @Deprecated
  public TpInfo getTeleportInfo(Player player, Player playerSecond) {
    return this.getTeleportInfo(player, playerSecond, null);
  }

  public TpInfo getTeleportInfo(
      Player player, Player playerSecond, TeleportManager.TpAction tpAction) {
    this.clearRequests();
    TpInfo tpInfo = null;
    if (playerSecond != null) {
      if (!playerSecond.isOnline()) {
        return null;
      }

      var set = this.tpRequests.get(playerSecond.getUniqueId());
      if (set == null) {
        return null;
      }

      for (TpInfo tpInfoValue : set) {
        if (tpInfoValue != null
            && (tpAction == null || tpInfoValue.getAction().equals(tpAction))
            && tpInfoValue.getWhoAccepts().getUniqueId().equals(player.getUniqueId())
            && tpInfoValue.getWhoOffers().getUniqueId().equals(playerSecond.getUniqueId())) {
          set.remove(tpInfoValue);
          this.tpRequests.put(playerSecond.getUniqueId(), set);
          return tpInfoValue;
        }
      }
    }

    long longValue = 0L;

    for (var entry : this.tpRequests.entrySet()) {
      for (TpInfo tpInfoSecond : (Set<TpInfo>) entry.getValue()) {
        if (tpInfoSecond.getWhoAccepts().getUniqueId().equals(player.getUniqueId())
            && tpInfoSecond.getWhen() >= longValue
            && (tpAction == null || tpAction.equals(tpInfoSecond.getAction()))) {
          longValue = tpInfoSecond.getWhen();
          tpInfo = tpInfoSecond;
        }
      }
    }

    if (tpInfo != null) {
      this.tpRequests.remove(tpInfo.getWhoOffers().getUniqueId());
    }

    return tpInfo;
  }

  public TreeMap<UUID, Set<TpInfo>> getTeleportRequests() {
    return this.tpRequests;
  }

  public Set<TpInfo> getTeleportRequests(UUID uuid) {
    return this.tpRequests.get(uuid);
  }

  public TreeMap<UUID, Set<TpInfo>> getBlockedRequests() {
    return this.blockedRequests;
  }

  public Set<TpInfo> getBlockedRequests(UUID uuid) {
    return this.blockedRequests.get(uuid);
  }

  public HashMap<Material, Integer> getBlockedItems() {
    return this.blockedItems;
  }

  public Boolean isBlackListedItemsEnabledFor(TeleportManager.TpAction tpAction) {
    return !this.BlackListedItemsEnabledFor.containsKey(tpAction)
        ? false
        : this.BlackListedItemsEnabledFor.get(tpAction);
  }

  public boolean isSwitchTeleportPlaces() {
    return this.TeleportSwitchPlaces;
  }

  public int getTpaWarmupTime() {
    return this.TeleportTpaWarmup;
  }

  public boolean isTpaMove() {
    return this.TeleportTpaMove;
  }

  public boolean isTpaCurrentLoc() {
    return this.TpaCurrentLoc;
  }

  public boolean isTpahereCurrentLoc() {
    return this.TpahereCurrentLoc;
  }

  public int getJumpDefaultDistance() {
    return this.TeleportJumpDefault;
  }

  public int getBackMinDistance() {
    return this.BackMinDistance;
  }

  public boolean isSafeLocationDownThenUp() {
    return this.SafeLocationDownThenUp;
  }

  public void setSafeLocationDownThenUp(boolean flag) {
    this.SafeLocationDownThenUp = flag;
  }

  public List<String> getBackBlackList() {
    return this.BackBlackList;
  }

  public List<String> getDBackBlackList() {
    return this.DBackBlackList;
  }

  public int getTpaMaxDistance() {
    return this.TeleportTpaMaxDistance;
  }

  public int getTpaHereMaxDistance() {
    return this.TeleportTpaHereMaxDistance;
  }

  public int getInvulnerabilityTime() {
    return this.defaultTeleportInvulnerability;
  }

  public int getInvulnerabilityTime(World world) {
    return world == null
        ? this.getInvulnerabilityTime()
        : this.getInvulnerabilityTime(world.getName());
  }

  public int getInvulnerabilityTime(String string) {
    return this.TeleportInvulnerability.getOrDefault(
        string.toLowerCase().replace("_", ""), this.getInvulnerabilityTime());
  }

  public boolean isDisbleInvulnerabilityOnBlockBreakPlace() {
    return this.DisableInvOnBlockBreakPlace;
  }

  public boolean isDisbleInvulnerabilityOnInteract() {
    return this.DisableInvOnInteraction;
  }

  public boolean isDenyConfirm() {
    return this.TeleportDenyConfirm;
  }

  public int getTpBypassTime() {
    return this.TeleportTpBypass;
  }

  public boolean isBackWithWE() {
    return this.BackWithWE;
  }

  public int getTpaTime() {
    return this.TeleportTpaTime;
  }

  public int getBackExpiration() {
    return this.BackExpiration;
  }

  public int getDeathBackExpiration() {
    return this.DeathBackExpiration;
  }

  public enum TpAction {
    tpa,
    tpahere,
    tpaall,
    tp,
    warp,
    home,
    spawn;

    public static TeleportManager.TpAction get(String string) {
      TeleportManager.TpAction[] tpActions;
      int value = (tpActions = values()).length;

      for (int count = 0; count < value; count++) {
        TeleportManager.TpAction tpAction = tpActions[count];
        if (tpAction.toString().equalsIgnoreCase(string)) {
          return tpAction;
        }
      }

      return null;
    }
  }
}
