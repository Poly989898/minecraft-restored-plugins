/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.Time.CMITimeManager
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.RandomTeleport;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class rt implements Cmd {
  private static ConcurrentHashMap<UUID, Map<String, Long>> map = new ConcurrentHashMap();

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("possible", "{gcp}Possible worlds: {gcs}[worlds]");
    configReader.get(
        "tpLimit", "{gcp}You can't teleport so fast, please wait {gcs}[time] {gcp}and try again");
    configReader.get("cantDo", "{gcp}You can't use random teleport from different world");
    configReader.get(
        "IncorrectLocation",
        "{gcs}Could not find correct teleport location, please wait {gcp}[time] {gcs}and try"
            + " again.");
    configReader.get("teleported", "!actionbar!{gcs}Whoosh!!!");
    configReader.get(
        "ongoingSearch",
        "!actionbar!{gcn}[dots]Searching ({gcs}[current]{gcn}/{gcs}[max]{gcn})[dots]");
  }

  @Override
  @CAnnotation(
      priority = 40,
      info = "Teleports to random location",
      args = "(playerName) (worldName) (-s)",
      tab = {"playername", "worlds", "-s"},
      explanation = {},
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {1, 2, 3, 5},
      others = true,
      customAlias = {"rtp"})
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    RandomTeleport randomTeleport;
    Object object;
    World targetWorld = null;
    Object targetName = null;
    boolean bl = false;
    String[] args = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      object = args[argIndex];
      switch (((String) object).toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            if (targetWorld == null
                && (targetWorld = cMI.getUtilManager().getWorld((String) object)) != null) break;
            targetName = object;
          }
      }
      ++argIndex;
    }
    object = cMI.getUser(commandSender, (String) targetName, this);
    if (object == null || !((CMIUser) object).isOnline()) {
      return null;
    }
    if (targetWorld == null) {
      targetWorld = ((CMIUser) object).getLocation().getWorld();
    }
    if ((randomTeleport = cMI.getRandomTeleportationManager().getRandomTeleport(targetWorld))
        != null) {
      if (!randomTeleport.isAllowFromAnotherWorld()
          && !((CMIUser) object).getWorld().equals(targetWorld)) {
        if (!bl) {
          CMILC.info((Object) this, commandSender, "cantDo", new Object[0]);
        }
        return null;
      }
      if (randomTeleport.isRequireWorldPermission()
          && !PermissionsManager.CMIPerm.randomteleport_$1.hasPermission(
              (CommandSender) ((CMIUser) object).getPlayer(),
              false,
              targetWorld.getName().toLowerCase())) {
        if (!bl) {
          CMILC.info_noWorldAccess.sendMessage(commandSender, new Object[0]);
        }
        return null;
      }
    }
    if (cMI.getRandomTeleportationManager().getRandomTeleport(targetWorld) == null) {
      StringBuilder stringBuilder = new StringBuilder();
      for (Map.Entry entry : cMI.getRandomTeleportationManager().getRandomTeleports().entrySet()) {
        if (!((RandomTeleport) entry.getValue()).isEnabled()
            || ((RandomTeleport) entry.getValue()).getCenter().getWorld() == null) continue;
        if (!stringBuilder.toString().isEmpty()) {
          stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
        }
        if (!((RandomTeleport) entry.getValue()).isAllowFromAnotherWorld()
                && !((CMIUser) object)
                    .getWorld()
                    .equals(((RandomTeleport) entry.getValue()).getCenter().getWorld())
            || !((RandomTeleport) entry.getValue()).isRequireWorldPermission()
                && !PermissionsManager.CMIPerm.randomteleport_$1.hasPermission(
                    (CommandSender) ((CMIUser) object).getPlayer(),
                    false,
                    targetWorld.getName().toLowerCase())) continue;
        stringBuilder.append((String) entry.getKey());
      }
      if (!stringBuilder.toString().isEmpty()) {
        CMILC.info((Object) this, commandSender, "possible", "[worlds]", stringBuilder);
      }
      return null;
    }
    n =
        randomTeleport == null
            ? cMI.getRandomTeleportationManager().getRandomTeleportCooldown()
            : randomTeleport.getCooldownWithDefault();
    Long l = rt.getUsedAt(((CMIUser) object).getUniqueId(), targetWorld.getName());
    if (l != null
        && !PermissionsManager.CMIPerm.randomteleport_cooldownbypass.hasPermission(
            commandSender, false, new String[0])
        && l + (long) (n * 1000) > System.currentTimeMillis()) {
      CMILC.info(
          (Object) this,
          commandSender,
          "tpLimit",
          "[time]",
          CMITimeManager.to24hourShort(
              (Long) ((long) (n * 1000) - (System.currentTimeMillis() - l) + 1000L)));
      return null;
    }
    boolean silent = bl;
    CompletableFuture<Boolean> completableFuture =
        cMI.getRandomTeleportationManager()
            .randomTeleport(((CMIUser) object).getPlayer(), targetWorld);
    String string = targetWorld.getName();
    final CMIUser rtUser = (CMIUser) object;
    final int rtSeconds = n;
    completableFuture.thenAcceptAsync(
        success -> {
          rt.addUsedAt(rtUser.getUniqueId(), string, System.currentTimeMillis());
          if (!success.booleanValue()) {
            CMILC.info(
                "rt",
                commandSender,
                "IncorrectLocation",
                "[time]",
                CMITimeManager.to24hourShort((Long) ((long) rtSeconds * 1000L)));
          } else if (rtUser.isOnline() && !silent) {
            String teleportedMessage =
                cMI.getPlaceholderAPIManager()
                    .updatePlaceHolders(
                        rtUser.getPlayer(), CMILC.getIM(rt.class, "teleported", new Object[0]));
            CMILC.sendMessage(commandSender, teleportedMessage);
          }
        });
    return true;
  }

  public static Long getUsedAt(UUID uUID, String string) {
    Map<String, Long> map = rt.map.get(uUID);
    if (map == null) {
      return null;
    }
    RandomTeleport randomTeleport =
        CMI.getInstance().getRandomTeleportationManager().getRandomTeleport(string);
    if (randomTeleport == null) {
      return null;
    }
    if (randomTeleport.getCooldown() < 1) {
      return map.get("_GLOBAL_");
    }
    return map.get(string);
  }

  public static void addUsedAt(UUID playerUuid, String string, long l) {
    RandomTeleport randomTeleport =
        CMI.getInstance().getRandomTeleportationManager().getRandomTeleport(string);
    if (randomTeleport == null) {
      return;
    }
    if (randomTeleport.getCooldown() < 1) {
      map.computeIfAbsent(playerUuid, uUID -> new HashMap()).put("_GLOBAL_", l);
    } else {
      map.computeIfAbsent(playerUuid, uUID -> new HashMap()).put(string, l);
    }
  }
}
