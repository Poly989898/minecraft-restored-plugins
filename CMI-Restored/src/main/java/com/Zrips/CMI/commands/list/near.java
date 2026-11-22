package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIDirection;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class near implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("nope", "{gcp}Nobody is near you");
    configReader.get("list", "{gcp}[place]. {gcs}[playerDisplayName] {gcp}- {gcs}[distance]m");
    configReader.get(
        "aproxList", "{gcp}[place]. {gcs}[playerDisplayName] {gcp}- {gcs}[dir] {gcn}~[distance]m");
  }

  @CAnnotation(
      priority = 48,
      info = "Check who is near you",
      args = "(distance)",
      tab = "100%%",
      explanation = {},
      regVar = {0, 1},
      consoleVar = 666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    int value = Config.NearDefaultDistance;
    String[] parts = strings;
    int count = strings.length;

    for (int countSecond = 0; countSecond < count; countSecond++) {
      String string = parts[countSecond];

      try {
        value = Integer.parseInt(string);
      } catch (Exception exception) {
      }
    }

    Player player = (Player) commandSender;
    if (value < 1) {
      value = 1;
    }

    PermissionInfo permissionInfo =
        cmi.getPermissionsManager()
            .getPermissionInfo(player, PermissionsManager.CMIPerm.command_near_max_$1, 5000L);
    if (permissionInfo != null
        && permissionInfo.getMaxValue() != null
        && value > permissionInfo.getMaxValue()) {
      value = permissionInfo.getMaxValue().intValue();
    }

    TreeMap<Double, Player> treeMap = new TreeMap();
    Location location = player.getLocation();

    for (Player playerSecond : Bukkit.getOnlinePlayers()) {
      if (location.getWorld().equals(playerSecond.getLocation().getWorld())
          && !playerSecond.getUniqueId().equals(player.getUniqueId())) {
        double number = location.distance(playerSecond.getLocation());
        if (!(value < number)
            && player.canSee(playerSecond)
            && (player.getGameMode().name().equals("SPECTATOR")
                || !playerSecond.getGameMode().name().equals("SPECTATOR"))
            && !PermissionsManager.CMIPerm.command_near_hide.hasSetPermission(playerSecond)) {
          treeMap.put(number, playerSecond);
        }
      }
    }

    if (!treeMap.isEmpty()) {
      int countThird = 0;
      Snd snd = new Snd();
      snd.setSender(player);
      boolean flag =
          PermissionsManager.CMIPerm.command_$1.hasPermission(
              commandSender, point.class.getSimpleName());

      for (Entry entry : treeMap.entrySet()) {
        boolean flagSecond = false;
        if (Config.NearHideInvisible) {
          PotionEffect potionEffect =
              ((Player) entry.getValue()).getPotionEffect(PotionEffectType.INVISIBILITY);
          if (potionEffect != null) {
            if (!Config.NearHideObfuscate) {
              continue;
            }

            flagSecond = true;
          }
        }

        if (++countThird == 1) {
          LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        }

        if (!flagSecond) {
          snd.resetTarget();
          snd.setTarget((Player) entry.getValue());
        } else {
          snd.resetTarget();
          snd.setTargetName("&k********");
        }

        RawMessage rawMessage = new RawMessage();
        if (Config.NearCommand) {
          int countFourth = ((Double) entry.getKey()).intValue();
          if ((Double) entry.getKey() < 10.0) {
            countFourth = ((Double) entry.getKey()).intValue();
          } else if ((Double) entry.getKey() < 100.0) {
            countFourth = (int) ((Double) entry.getKey() / 10.0) * 10;
          } else if ((Double) entry.getKey() < 1000.0) {
            countFourth = (int) ((Double) entry.getKey() / 100.0) * 100;
          } else {
            countFourth = (int) ((Double) entry.getKey() / 1000.0) * 1000;
          }

          rawMessage.addText(
              CMILC.getIM(
                  this,
                  "aproxList",
                  "[place]",
                  countThird,
                  snd,
                  "[distance]",
                  countFourth,
                  "[dir]",
                  CMIDirection.getFromLocations(
                          ((Player) entry.getValue()).getLocation(), player.getLocation())
                      .getTranslatedName()));
        } else {
          rawMessage.addText(
              CMILC.getIM(
                  this,
                  "list",
                  "[place]",
                  countThird,
                  snd,
                  "[distance]",
                  (int) ((Double) entry.getKey() * 100.0) / 100.0,
                  "[dir]",
                  CMIDirection.getFromLocations(
                          ((Player) entry.getValue()).getLocation(), player.getLocation())
                      .getTranslatedName()));
        }

        if (flag) {
          rawMessage.addHover(Config.NearDirection ? LC.info_Show.getLocale(new Object[0]) : null);
          rawMessage.addCommand(
              Config.NearDirection
                  ? CommandsHandler.getLabel()
                      + " "
                      + point.class.getSimpleName()
                      + " "
                      + cmi.getPlayerManager()
                          .convertLocToString(((Player) entry.getValue()).getLocation())
                  : null);
        }

        rawMessage.show(commandSender);
        if (countThird >= Config.NearCommandCount) {
          break;
        }
      }

      if (countThird == 0) {
        CMILC.info(this, commandSender, "nope");
      }
    } else {
      CMILC.info(this, commandSender, "nope");
    }

    return true;
  }
}
