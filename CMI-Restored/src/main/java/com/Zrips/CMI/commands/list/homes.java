package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIDirection;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Homes.CmiHome;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Map.Entry;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class homes implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "nearList",
        "{gcs}[number]. {gcs}[playerDisplayName] {gcn}[homeName] {gcp}[distance] blocks away"
            + " {gcs}[direction]");
    configReader.get("closeButton", "{gcp}Close");
    configReader.get("infoButton", "{gcp}Information");
    configReader.get("bedButton", "{gcp}Bed location");
    configReader.get("homeButton", "{gcp}Home location");
    configReader.get("deathButton", "{gcp}Death location");
    configReader.get("backButton", "{gcp}Back location");
    configReader.get("slot", "{gcp}Slot: {gcs}[slot]");
    configReader.get("private", "{gcp}Private: {gcs}[state]");
    configReader.get("favorite", "{gcp}Favorite: {gcs}[state]");
    configReader.get("lore", "{gcp}[world]\n{gcs}[x]{gcp}:{gcs}[y]{gcp}:{gcs}[z]");
    configReader.get("picker", "{gcp}Click to change icon");
    configReader.get("edit", "{gcp}Click Q to edit");
  }

  @CAnnotation(
      info = "A list of homes that you can click to teleport to.",
      args = "(playerName/near:[range])",
      tab = "playername",
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    int value = 1;
    Integer integer = null;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.startsWith("near:")) {
        try {
          integer = CMINumber.clamp(Integer.parseInt(arg.substring("near:".length())), 1, 200000);
        } catch (Exception exception) {
        }
      } else if (arg.startsWith("-p:")) {
        try {
          value = Integer.parseInt(arg.substring("-p:".length()));
        } catch (Exception exception2) {
        }
      } else if (string == null) {
        string = arg;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    if (cmiUser.getHomes().isEmpty()) {
      CMILC.info("home", commandSender, "noHomes");
      return null;
    }

    if (commandSender instanceof Player && integer != null) {
      if (!PermissionsManager.CMIPerm.command_homes_range.hasPermission(commandSender, true)) {
        return null;
      }

      int page = integer;
      CMIScheduler.runTaskAsynchronously(
          cmi,
          () -> {
            Player player = (Player) commandSender;
            Location location =
                player.getLocation().clone().add(-page, CMINumber.clamp(-page, -64, 320), -page);
            if (location.getY() < -64.0) {
              location.setY(-64.0);
            }

            Location homeLocation =
                player.getLocation().clone().add(page, CMINumber.clamp(page, -64, 320), page);
            if (homeLocation.getY() > 320.0) {
              homeLocation.setY(320.0);
            }

            CuboidArea cuboidArea = new CuboidArea(location, homeLocation);
            int count = 0;
            Snd snd = new Snd();
            snd.setSender(commandSender);

            label50:
            for (var entry : cmi.getPlayerManager().getAllUsers().entrySet()) {
              var iterator = ((CMIUser) entry.getValue()).getHomes().entrySet().iterator();
              String ownerName = ((CMIUser) entry.getValue()).getName();
              if (ownerName != null) {
                boolean flag = ownerName.equals(commandSender.getName());

                while (iterator.hasNext()) {
                  Entry entryCopy = (Entry) iterator.next();
                  if (cuboidArea.containsLoc(((CmiHome) entryCopy.getValue()).getLoc())
                      && !(page
                          < cmiUser
                              .getLocation()
                              .distance(((CmiHome) entryCopy.getValue()).getLoc()))) {
                    if (++count >= 10) {
                      break label50;
                    }

                    snd.setTarget((CMIUser) entry.getValue());
                    RawMessage rawMessage = new RawMessage();
                    rawMessage
                        .addText(
                            CMILC.getIM(
                                homes.class.getSimpleName(),
                                "nearList",
                                "[number]",
                                count,
                                "[homeName]",
                                ((CmiHome) entryCopy.getValue()).getName(),
                                "[distance]",
                                (int)
                                        (cmiUser
                                                .getLocation()
                                                .distance(((CmiHome) entryCopy.getValue()).getLoc())
                                            * 100.0)
                                    / 100.0,
                                "[direction]",
                                CMIDirection.getFromLocations(
                                        ((CmiHome) entryCopy.getValue()).getLoc(),
                                        player.getLocation())
                                    .getTranslatedName(),
                                snd))
                        .addHover(LC.info_Click.getLocale(new Object[0]))
                        .addSuggestion(
                            "/"
                                + CommandsHandler.getCommandPrefix(home.class.getSimpleName())
                                + " "
                                + ((CmiHome) entryCopy.getValue()).getName()
                                + " "
                                + ownerName
                                + " ");
                    if (flag
                        || PermissionsManager.CMIPerm.command_$1_others.hasPermission(
                            commandSender, removehome.class.getSimpleName())) {
                      rawMessage
                          .addText(LC.modify_deleteSymbol.getLocale(new Object[0]))
                          .addHover(
                              LC.modify_deleteSymbolHover.getLocale(
                                  new Object[] {
                                    "[text]", ((CmiHome) entryCopy.getValue()).getName()
                                  }))
                          .addSuggestion(
                              "/"
                                  + CommandsHandler.getCommandPrefix(
                                      removehome.class.getSimpleName())
                                  + " "
                                  + ((CmiHome) entryCopy.getValue()).getName()
                                  + " "
                                  + ownerName);
                    }

                    CMIScheduler.runTask(cmi, () -> rawMessage.show(commandSender));
                  }
                }
              }
            }

            if (count == 0) {
              CMILC.info("home", commandSender, "noHomes");
            }
          });
      return true;
    } else {
      if (cmi.getHomeManager().isHomesGui() && commandSender instanceof Player) {
        cmi.getHomeManager().openHomeGui((Player) commandSender, cmiUser, value);
        return null;
      }

      RawMessage message = new RawMessage();
      message.addText(CMILC.getIM("home", "list"));
      String line = "";

      for (var homeEntry : cmiUser.getHomes().entrySet()) {
        if (commandSender instanceof Player) {
          message.addText(!line.isEmpty() ? LC.info_ListSpliter.getLocale(new Object[0]) : "");
        }

        String homeText =
            ((CmiHome) homeEntry.getValue()).isPrivate()
                ? CMILC.getIM("home", "listPrivate")
                : CMILC.getIM("home", "listNonePrivate");
        String suffix = "";
        if (!(commandSender instanceof Player)) {
          suffix =
              " "
                  + LC.Location_Full.getLocale(
                      new Object[] {((CmiHome) homeEntry.getValue()).getLoc()});
          homeText = "\n" + homeText;
        }

        message
            .addText(homeText + (String) homeEntry.getKey() + suffix)
            .addHover(CMILC.getIM("home", "click", "[home]", homeEntry.getKey()))
            .addCommand("cmi home " + (String) homeEntry.getKey() + " " + cmiUser.getName());
        line = line + "s";
      }

      message.show(commandSender);
      return null;
    }
  }
}
