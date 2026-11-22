package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Homes.CmiHome;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class home implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", "{gcp}Homes: {gcs}");
    configReader.get("listNonePrivate", "{gcn}");
    configReader.get("listPrivate", "{gcu}");
    configReader.get("click", "{gcp}Click to teleport to {gcs}[home]");
    configReader.get("teleported", "{gcp}Teleported to home: {gcs}[home]");
    configReader.get("noHomes", "{gcd}There is no saved homes");
    configReader.get("private", "{gcd}his home location is private!");
    configReader.get("blacklisted", "{gce}You have black listed items in your inventory.");
    configReader.get("limitReached", "{gce}You have too many homes, please remove some");
    configReader.get("remove", "{gcd}Shift+Right click to remove home");
    configReader.get("missingBed", "{gcd}Missing bed for your home location");
    configReader.get(
        "notAccessible", "{gcd}Home location world ({gcs}[worldName]{gcd}) doesn't exist!");
    configReader.get(
        "guiTitle", "{gcs}[playerDisplayName] {gcp}homes {gcs}[current]{gcp}/{gcs}[max]");
  }

  @CAnnotation(
      info = "Teleport to home location",
      args = "(homeName) (playerName) (whoTeleport)",
      tab = {"homes", "playername", "playername"},
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {2, 3, 4},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String homeName = null;
    String ownerName = null;
    Player player = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (homeName == null) {
        homeName = arg;
      } else if (string == null) {
        string = arg;
      } else if (ownerName == null) {
        ownerName = arg;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    player =
        cmi.getTarget(
            commandSender,
            ownerName == null && !(commandSender instanceof Player) ? string : ownerName,
            this);
    CmiHome cmiHome = cmiUser.getHome(homeName);
    if (cmiHome == null && homeName != null) {
      LC.info_invalidName.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (cmiHome == null) {
      return cmi.getCommandManager()
          .performCMICommand(commandSender, homes.class, cmiUser.getName());
    }

    cmiHome.teleportPlayerTo(commandSender, player, cmiUser);
    return true;
  }
}
