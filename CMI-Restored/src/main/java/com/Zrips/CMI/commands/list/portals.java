package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.BungeeCord.BungeeCordServer;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Portals.CMIPortal;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import com.Zrips.CMI.Modules.Selection.CMISelectionVisualizer;
import com.Zrips.CMI.Modules.Selection.SelectionManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.ArrayList;
import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Effects.CMIEffectPicker;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class portals implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("collides", "{gcw}Collides with {gcs}[portalName] {gcw}portal");
    configReader.get("exist", "{gcw}Portal with this name already exist");
    configReader.get("notExist", "{gcw}Portal with this name doesn't exist");
    configReader.get(
        "locationSet", "{gcp}Portal location set to your position for {gcs}[portalName]");
    configReader.get("removed", "{gcp}Portal {gcs}[portalName] {gcp}removed");
    configReader.get(
        "click",
        "{gcp}Click to set tp location for {gcs}[portalName] {gcp}to your current position");
    configReader.get(
        "bungeeSet", "{gcp}Travel to position and click on this message to set teleport location");
    configReader.get(
        "bungeeSetConfirmed",
        "{gcp}Bungee portal ({gcs}[portal]{gcp}) location set from {gcs}[sourceServer] {gcp}server"
            + " to {gcp}[location]");
    configReader.get(
        "bungeeSetEmpty",
        "{gcp}Initiated portal location set at {gcs}[serverName] {gcp}server, but no one is online"
            + " in this server. This action will not complete until some one joins it");
    configReader.get("List", "{gcp}[color][portalName]");
    configReader.get("redefine", "{gcp}Redefine area from current selection");
    configReader.get("particles", "{gcp}Particles: {gcs}[state]");
    configReader.get("particleType", "{gcp}Particle type: {gcs}[type]");
    configReader.get("particleAmount", "{gcp}Particle amount: {gcs}[amount]");
    configReader.get("particleOrigin", "{gcp}Particle origin: {gcs}[value]");
    configReader.get(
        "particlePercent", "{gcp}Percentage of sides shrinked particles: {gcs}[amount]%");
    configReader.get("activationRange", "{gcp}Activation range: {gcs}[amount]");
    configReader.get("particlePermission", "{gcp}Show particles without permission: {gcs}[state]");
    configReader.get("bungeeTeleport", "{gcp}Teleport to exact bungee location: [state]");
    configReader.get(
        "requiresPermission", "{gcp}Requires {gcs}[perm] {gcp}permission to use: [state]");
    configReader.get("kickBack", "{gcp}Prevent enter without permission: [state]");
    configReader.get("informPermission", "{gcp}Inform on missing permission: [state]");
    configReader.get("cancelWarmup", "{gcp}Cancel command warmup on leave: [state]");
    configReader.get("teleportToLocation", "{gcp}Teleport to portal location");
    configReader.get("teleportToTarget", "{gcp}Teleport to portal target location");
    configReader.get(
        "performCommandsWithoutTp", "{gcp}Perform commands without valid teleport location");
    configReader.get("editCommands", "{gcp}Edit portal commands");
    configReader.get("setTeleportLocation", "{gcp}Set teleport location");
    configReader.get("setSafeLocation", "{gcp}Set safe outside location");
    configReader.get("save", "{gcp}Save changes to file");
    configReader.get("removePortal", "{gcp}REMOVE PORTAL");
    configReader.get("cantUsePortal", "{gcw}You can't use this portal");
  }

  @CAnnotation(
      priority = 125,
      info = "Set portals",
      args = "(new/nearest/forceupdate/setlocation/enabled) (portalName) (world:x:y:z:yaw:pitch)",
      multiTab = {
        "new,nearest",
        "forceupdate,setlocation,edit,commands [portals]",
        "enabled [allportals] true,false"
      },
      explanation = {},
      regVar = -666,
      consoleVar = {2, 3, 4},
      modules = "portals",
      others = false)
  @Override
  public Boolean perform(CMI cmi, final CommandSender commandSender, String[] strings) {
    Player player = null;
    if (commandSender instanceof Player) {
      player = (Player) commandSender;
    }

    Player senderPlayer = player;
    Integer integer = null;
    portals.Action action = null;
    boolean flag = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = argsCopy[argIndex];
      if (string.startsWith("-p:") && integer == null) {
        try {
          integer = Integer.parseInt(string.substring("-p:".length()));
          continue;
        } catch (Exception exception) {
        }
      }

      if (string.startsWith("-confirmed")) {
        flag = true;
      } else if (action == null) {
        action = portals.Action.getByName(string);
      }
    }

    if (integer == null) {
      integer = 1;
    }

    if (action == null) {
      action = portals.Action.list;
    }

    if (action.equals(portals.Action.forceupdate)) {
      if (strings.length < 2) {
        return false;
      }

      CMIPortal cmiPortal = cmi.getPortalManager().getByName(strings[1]);
      if (cmiPortal == null) {
        return false;
      }

      cmi.getPortalManager().forceUpdate(cmiPortal);
      return true;
    } else if (action.equals(portals.Action.setlocation)) {
      if (strings.length < 3) {
        return false;
      } else {
        CMIPortal portalForLocation = cmi.getPortalManager().getByName(strings[1]);
        if (portalForLocation == null) {
          return false;
        } else {
          CMILocation cmiLocation = CMILocation.fromString(strings[2]);
          if (cmiLocation != null && cmiLocation.isValid()) {
            portalForLocation.setTpLoc(cmiLocation);
            CMILC.info(
                this, commandSender, "locationSet", "[portalName]", portalForLocation.getName());
            cmi.getPortalManager().savePortals();
            return true;
          } else {
            LC.info_IncorrectLocation.sendMessage(commandSender, new Object[0]);
            return null;
          }
        }
      }
    } else if (action.equals(portals.Action.enabled)) {
      if (strings.length < 3) {
        return false;
      }

      CMIPortal portalToToggle = cmi.getPortalManager().getByName(strings[1]);
      if (portalToToggle == null) {
        return false;
      }

      Boolean enabledValue = null;
      if (strings[2].equalsIgnoreCase("true")) {
        enabledValue = true;
      } else if (strings[2].equalsIgnoreCase("false")) {
        enabledValue = false;
      }

      if (enabledValue == null) {
        return false;
      }

      portalToToggle.setEnabled(enabledValue);
      if (enabledValue) {
        LC.info_variables_Enabled.sendMessage(commandSender, new Object[0]);
      } else {
        LC.info_variables_Disabled.sendMessage(commandSender, new Object[0]);
      }

      cmi.getPortalManager().savePortals();
      return true;
    } else if (action.equals(portals.Action.list)) {
      LC.info_Spliter.sendMessage(commandSender, new Object[0]);
      var list =
          cmi.getPortalManager()
              .getPortalsByDistance(senderPlayer == null ? null : senderPlayer.getLocation());
      PageInfo pageInfo = new PageInfo(10, list.size(), integer);
      ChatMessageObjectEdit chatMessageObjectEdit =
          new ChatMessageObjectEdit(commandSender, pageInfo) {
            public void newAdd(String portalName) {
              CMI.getInstance()
                  .performCommand(
                      commandSender,
                      CommandsHandler.getLabel()
                          + " "
                          + portals.class.getSimpleName()
                          + " new "
                          + portalName);
            }
          };

      for (final CMIPortal listedPortal : list) {
        if (pageInfo.isBreak()) {
          break;
        }

        if (pageInfo.isEntryOk()) {
          ArrayList<String> arrayList = new ArrayList();
          arrayList.add(
              LC.Selection_Area.getLocale(
                  new Object[] {
                    "[world]",
                    listedPortal.getWorldName(),
                    "[x1]",
                    listedPortal.getArea().getLowPoint().getBlockX(),
                    "[y1]",
                    listedPortal.getArea().getLowPoint().getBlockY(),
                    "[z1]",
                    listedPortal.getArea().getLowPoint().getBlockZ(),
                    "[x2]",
                    listedPortal.getArea().getHighPoint().getBlockX(),
                    "[y2]",
                    listedPortal.getArea().getHighPoint().getBlockY(),
                    "[z2]",
                    listedPortal.getArea().getHighPoint().getBlockZ()
                  }));
          if (listedPortal.getPerformCommandsWithoutTp()) {
            arrayList.add(CMILC.getIM(this, "performCommandsWithoutTp"));
          }

          if (listedPortal.isRequiresPermission()) {
            arrayList.add(
                CMILC.getIM(
                    "portals",
                    "requiresPermission",
                    "[state]",
                    true,
                    "[perm]",
                    PermissionsManager.CMIPerm.command_portal_$1.getPermission(
                        listedPortal.getName().toLowerCase())));
          }

          if (listedPortal.isKickBack()) {
            arrayList.add(CMILC.getIM("portals", "kickBack", "[state]", true));
          }

          ChatEditorObject chatEditorObject =
              new ChatEditorObject(
                  CMILC.getIM(
                      this,
                      "List",
                      "[color]",
                      listedPortal.isEnabled()
                          ? LC.modify_enabledColor.getLocale(new Object[0])
                          : LC.modify_disabledColor.getLocale(new Object[0]),
                      "[number]",
                      pageInfo.getPositionForOutput(),
                      "[portalName]",
                      listedPortal.getName())) {
                public void onDelete() {
                  CMI.getInstance()
                      .performCommand(
                          commandSender,
                          CommandsHandler.getLabel()
                              + " "
                              + portals.class.getSimpleName()
                              + " edit "
                              + listedPortal.getName()
                              + " remove");
                }

                public void onClick() {
                  CMI.getInstance()
                      .performCommand(
                          commandSender,
                          CommandsHandler.getLabel()
                              + " "
                              + portals.class.getSimpleName()
                              + " edit "
                              + listedPortal.getName());
                }
              };
          chatEditorObject.setHover(arrayList);
          chatMessageObjectEdit.addline(chatEditorObject);
        }
      }

      chatMessageObjectEdit.print();
      pageInfo.autoPagination(
          commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName(), "-p:");
      return true;
    } else if (action.equals(portals.Action.nearest)) {
      var nearbyPortals =
          cmi.getPortalManager()
              .getPortalsByDistance(senderPlayer == null ? null : senderPlayer.getLocation());
      String nearestPortalName =
          nearbyPortals.isEmpty() ? null : ((CMIPortal) nearbyPortals.get(0)).getName();
      if (nearestPortalName != null) {
        cmi.performCommand(
            commandSender, "cmi " + this.getClass().getSimpleName() + " edit " + nearestPortalName);
      } else {
        cmi.performCommand(commandSender, "cmi " + this.getClass().getSimpleName() + " list");
      }

      return true;
    } else if (action.equals(portals.Action.createnew) && strings.length > 1) {
      CMISelectionVisualizer cmiSelectionVisualizer =
          SelectionManager.getPlayerSelection((Player) commandSender);
      if (cmiSelectionVisualizer.getSelectedCuboid() != null
          && cmiSelectionVisualizer.getSelectedCuboid().valid()) {
        CuboidArea cuboidArea =
            new CuboidArea(
                cmiSelectionVisualizer.getSelectedCuboid().getWorld(),
                cmiSelectionVisualizer.getSelectedCuboid().getLowPoint(),
                cmiSelectionVisualizer.getSelectedCuboid().getHighPoint());
        if (cuboidArea != null && cuboidArea.valid()) {
          cuboidArea = cuboidArea.clone();
          CMIPortal collidingPortal = cmi.getPortalManager().collidesWithPortal(cuboidArea);
          if (collidingPortal != null) {
            CMILC.info(this, commandSender, "collides", "[portalName]", collidingPortal.getName());
            return null;
          }

          CMIPortal existingPortal = cmi.getPortalManager().getByName(strings[1]);
          if (existingPortal != null) {
            CMILC.info(this, commandSender, "exist");
            return null;
          }

          CMIPortal newPortal = new CMIPortal();
          newPortal.setArea(cuboidArea, true);
          newPortal.setName(strings[1]);
          newPortal.setWorld(cuboidArea.getWorld());
          cmi.getPortalManager().addPortal(newPortal);
          if (cmi.getBungeeCordManager().isBungeeCord()) {
            RawMessage rawMessage = new RawMessage();
            rawMessage
                .addText(CMILC.getIM(this, "bungeeSet"))
                .addHover(
                    "&e"
                        + cmi.getBungeeCordManager().getThisServerName()
                        + ": {gcs}"
                        + newPortal.getName())
                .addSuggestion(
                    "/"
                        + CommandsHandler.getLabel()
                        + " "
                        + portals.class.getSimpleName()
                        + " bungeeLoc "
                        + newPortal.getName()
                        + " bungee:"
                        + cmi.getBungeeCordManager().getThisServerNameOneWord());
            rawMessage.show(commandSender);
          } else {
            RawMessage clickMessage = new RawMessage();
            clickMessage
                .addText(CMILC.getIM(this, "click", "[portalName]", newPortal.getName()))
                .addHover(LC.info_Click)
                .addCommand("cmi portals edit " + newPortal.getName() + " location true");
            clickMessage.show(commandSender);
            cmi.performCommand(
                commandSender,
                "cmi " + this.getClass().getSimpleName() + " edit " + newPortal.getName());
          }

          cmi.getPortalManager().handlePortalVisualizerUpdates();
          cmi.getPortalManager().savePortals();
          return true;
        } else {
          LC.Selection_SelectPoints.sendMessage(
              commandSender,
              new Object[] {
                "[tool]", CMI.getInstance().getConfigManager().getSelectionTool().getRealName()
              });
          return null;
        }
      } else {
        LC.Selection_SelectPoints.sendMessage(
            commandSender,
            new Object[] {
              "[tool]", CMI.getInstance().getConfigManager().getSelectionTool().getRealName()
            });
        return null;
      }
    } else {
      if (strings.length == 2 && action.equals(portals.Action.edit) && senderPlayer != null) {
        CMIPortal editPortal = cmi.getPortalManager().getByName(strings[1]);
        return this.openUI(senderPlayer, editPortal);
      }

      if (strings.length == 2 && action.equals(portals.Action.showbungeemsg)) {
        String bungeeMessageName = strings[1];
        RawMessage bungeeMessage = new RawMessage();
        bungeeMessage
            .addText(CMILC.getIM(this, "bungeeSet"))
            .addHover(
                "&e"
                    + cmi.getBungeeCordManager().getThisServerName()
                    + ": {gcs}"
                    + bungeeMessageName)
            .addSuggestion(
                "/"
                    + CommandsHandler.getLabel()
                    + " "
                    + portals.class.getSimpleName()
                    + " bungeeLoc "
                    + bungeeMessageName
                    + " bungee:"
                    + cmi.getBungeeCordManager().getThisServerNameOneWord())
            .addInsertion(
                "/cmi portals bungeeLoc "
                    + bungeeMessageName
                    + " bungee:"
                    + cmi.getBungeeCordManager().getThisServerNameOneWord());
        bungeeMessage.show(commandSender);
        return true;
      }

      if (strings.length == 3 && action.equals(portals.Action.bungeeloc) && senderPlayer != null) {
        CMIScheduler.runTaskLater(
            cmi,
            () -> {
              String bungeeServerName =
                  strings[2].substring("bungee:".length(), strings[2].length());
              String portalNameArg = strings[1];
              if (bungeeServerName.equalsIgnoreCase(
                  CMI.getInstance().getBungeeCordManager().getThisServerNameOneWord())) {
                CMIPortal bungeePortal = CMI.getInstance().getPortalManager().getByName(strings[1]);
                if (bungeePortal != null) {
                  bungeePortal.setBungeeLocation(null);
                  bungeePortal.setBungeeServer(null);
                }

                CMI.getInstance()
                    .performCommand(
                        commandSender,
                        CommandsHandler.getLabel()
                            + " portals edit "
                            + strings[1]
                            + " location true");
              } else {
                BungeeCordServer bungeeCordServer =
                    cmi.getBungeeCordManager().getServer(bungeeServerName);
                if (bungeeCordServer != null) {
                  bungeeCordServer.update();
                  CMI.getInstance()
                      .getBungeeCordManager()
                      .sendPortalSetLocationToBungee(
                          commandSender.getName(),
                          bungeeServerName,
                          portalNameArg,
                          senderPlayer.getLocation());
                  if (bungeeCordServer.getCurrentPlayers() <= 0) {
                    CMILC.info(
                        portals.class.getSimpleName(),
                        commandSender,
                        "bungeeSetEmpty",
                        "[serverName]",
                        bungeeServerName,
                        "[portal]",
                        portalNameArg);
                  }
                }
              }
            },
            5L);
        return true;
      }

      if ((strings.length == 3 || strings.length == 4) && action.equals(portals.Action.edit)) {
        CMIPortal portal = cmi.getPortalManager().getByName(strings[1]);
        if (portal == null) {
          CMILC.info(this, commandSender, "notExist");
          return true;
        }

        String editAction;
        switch ((editAction = strings[2].toLowerCase()).hashCode()) {
          case -1903848958:
            if (editAction.equals("particlefade")) {
              int newPercentToHide = portal.getPercentToHide();

              try {
                newPercentToHide = Integer.parseInt(strings[3]);
              } catch (Exception exception2) {
                exception2.printStackTrace();
                return false;
              }

              portal.setPercentToHide(newPercentToHide);
              cmi.getPortalManager().savePortals();
            }
            break;
          case -1800314195:
            if (editAction.equals("particles")) {

              boolean showParticles;
              try {
                showParticles = Boolean.parseBoolean(strings[3]);
              } catch (Exception exception3) {
                exception3.printStackTrace();
                return false;
              }

              portal.setShowParticles(showParticles);
              cmi.getPortalManager().savePortals();
            }
            break;
          case -1609594047:
            if (editAction.equals("enabled")) {
              boolean enabled;
              try {
                enabled = Boolean.parseBoolean(strings[3]);
              } catch (Exception exception4) {
                exception4.printStackTrace();
                return false;
              }

              portal.setEnabled(enabled);
              cmi.getPortalManager().savePortals();
            }
            break;
          case -1427034695:
            if (editAction.equals("prevparticle")) {
              CMIParticle cmiParticle = portal.getCMIEffect().getParticle().getPrevParticleEffect();
              if (cmiParticle != null) {
                portal.setEffect(cmiParticle);
              }
            }
            break;
          case -1360201941:
            if (editAction.equals("teleport")) {
              if (senderPlayer == null) {
                return false;
              }

              Location location = portal.getTpLoc();
              if (location != null) {
                cmi.getTeleportHandler()
                    .teleportPlayer(senderPlayer, location, CMITeleportType.Portal)
                    .thenAccept(
                        cmiTeleportFeedback -> {
                          if (!cmiTeleportFeedback.isSuccess()) {
                            cmiTeleportFeedback.showFeedbackMessage(commandSender, senderPlayer);
                          } else {
                            CMIUser.closeInventory(senderPlayer);
                          }
                        });
              }
            }
            break;
          case -934610812:
            if (editAction.equals("remove")) {
              CMIUser.closeInventory(senderPlayer);
              cmi.getPortalManager().removePortal(portal);
              CMILC.info(this, commandSender, "removed", "[portalName]", portal.getName());
              cmi.getPortalManager().handlePortalVisualizerUpdates();
              cmi.getPortalManager().savePortals();
              cmi.performCommand(commandSender, "cmi " + this.getClass().getSimpleName() + " list");
              return true;
            }
            break;
          case -780192338:
            if (editAction.equals("redefine")) {
              CMISelectionVisualizer redefineSelection =
                  SelectionManager.getPlayerSelection((Player) commandSender);
              CuboidArea redefinedArea =
                  new CuboidArea(
                      redefineSelection.getSelectedCuboid().getWorld(),
                      redefineSelection.getSelectedCuboid().getLowPoint(),
                      redefineSelection.getSelectedCuboid().getHighPoint());
              if (redefinedArea == null || !redefinedArea.valid()) {
                LC.Selection_SelectPoints.sendMessage(
                    commandSender,
                    new Object[] {
                      "[tool]",
                      CMI.getInstance().getConfigManager().getSelectionTool().getRealName()
                    });
                return true;
              }

              redefinedArea = redefinedArea.clone();
              portal.setArea(redefinedArea, false);
              cmi.getPortalManager().recalculateChunks(portal);
              cmi.getPortalManager().handlePortalVisualizerUpdates();
              cmi.getPortalManager().savePortals();
            }
            break;
          case -74497442:
            if (editAction.equals("particleamount")) {
              int newParticleAmount = portal.getParticleAmount();

              try {
                newParticleAmount = Integer.parseInt(strings[3]);
              } catch (Exception exception5) {
                exception5.printStackTrace();
                return false;
              }

              portal.setParticleAmount(newParticleAmount);
              cmi.getPortalManager().savePortals();
            }
            break;
          case 3522941:
            if (editAction.equals("save")) {
              cmi.getPortalManager().savePortals();
            }
            break;
          case 81483385:
            if (editAction.equals("nextparticle")) {
              CMIParticle nextParticle =
                  portal.getCMIEffect().getParticle().getNextPartcileEffect();
              if (nextParticle != null) {
                portal.setEffect(nextParticle);
              }
            }
            break;
          case 146211049:
            if (editAction.equals("performcommands")) {
              boolean performCommands;
              try {
                performCommands = Boolean.parseBoolean(strings[3]);
              } catch (Exception exception6) {
                exception6.printStackTrace();
                return false;
              }

              portal.setPerformCommandsWithoutTp(performCommands);
              cmi.getPortalManager().savePortals();
            }
            break;
          case 621249090:
            if (editAction.equals("safelocation")) {
              boolean safeLocation;

              try {
                safeLocation = Boolean.parseBoolean(strings[3]);
              } catch (Exception exception7) {
                exception7.printStackTrace();
                return false;
              }

              if (senderPlayer == null) {
                return false;
              }

              if (safeLocation) {
                portal.setSafeLoc(senderPlayer.getLocation());
                CMILC.info(this, commandSender, "locationSet", "[portalName]", portal.getName());
              } else {
                portal.setSafeLoc(null);
              }

              cmi.getPortalManager().savePortals();
            }
            break;
          case 1304229351:
            if (editAction.equals("activationrange")) {
              int newActivationRange = portal.getParticleAmount();

              try {
                newActivationRange = Integer.parseInt(strings[3]);
              } catch (Exception exception8) {
                exception8.printStackTrace();
                return false;
              }

              portal.setActivationRange(newActivationRange);
              cmi.getPortalManager().recalculateChunks();
              cmi.getPortalManager().savePortals();
            }
            break;
          case 1657071642:
            if (editAction.equals("teleportoutside")) {
              if (senderPlayer == null) {
                return false;
              }

              Location outsideLoc = portal.getArea().getOutsideFreeLoc();
              if (outsideLoc != null) {
                cmi.getTeleportHandler()
                    .teleportPlayer(senderPlayer, outsideLoc, CMITeleportType.Portal)
                    .thenAccept(
                        outsideFeedback -> {
                          if (!outsideFeedback.isSuccess()) {
                            outsideFeedback.showFeedbackMessage(commandSender, senderPlayer);
                          } else {
                            CMIUser.closeInventory(senderPlayer);
                          }
                        });
              }
            }
            break;
          case 1901043637:
            if (editAction.equals("location")) {
              boolean locationFlag;

              try {
                locationFlag = Boolean.parseBoolean(strings[3]);
              } catch (Exception exception9) {
                exception9.printStackTrace();
                return false;
              }

              if (senderPlayer == null) {
                return false;
              }

              if (locationFlag) {
                portal.setTpLoc(senderPlayer.getLocation());
                CMILC.info(this, commandSender, "locationSet", "[portalName]", portal.getName());
              } else {
                portal.setTpLoc(null);
              }

              portal.setBungeeLocation(null);
              portal.setBungeeServer(null);
              cmi.getPortalManager().savePortals();
            }
        }

        return true;
      } else {
        if (strings.length > 1) {
          CMIPortal listPortal = cmi.getPortalManager().getByName(strings[1]);
          if (listPortal == null) {
            CMILC.info(this, commandSender, "notExist");
            return null;
          }

          ChatMessageListEdit chatMessageListEdit =
              new ChatMessageListEdit(senderPlayer, listPortal.getCommands(), ChatEditType.String) {
                public void onUpdate() {
                  CMI.getInstance().getPortalManager().savePortals();
                }
              };
          chatMessageListEdit.print();
        }

        return true;
      }
    }
  }

  private Boolean openUI(final Player player, final CMIPortal cmiPortal) {
    final CMI cmi = CMI.getInstance();
    if (cmiPortal == null) {
      CMILC.info(this, player, "notExist");
      return null;
    }

    final CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            cmi.getPortalManager().savePortals();
          }
        };
    cmiGui.setTitle("&8" + cmiPortal.getName());
    cmiGui.addLock(InvType.Gui);
    cmiGui.addLock(InvType.Main);
    cmiGui.setInvSize(GUIRows.r6);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(
            0,
            cmiPortal.isEnabled() ? CMIMaterial.GREEN_WOOL : CMIMaterial.RED_WOOL,
            cmiPortal.isEnabled()
                ? LC.info_variables_Enabled.getLocale(new Object[0])
                : LC.info_variables_Disabled.getLocale(new Object[0])) {
          public void click(GUIClickType guiClickType) {
            cmiPortal.setEnabled(!cmiPortal.isEnabled());
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setItem(
                cmiPortal.isEnabled()
                    ? CMIMaterial.GREEN_WOOL.newItemStack()
                    : CMIMaterial.RED_WOOL.newItemStack());
            this.setName(
                cmiPortal.isEnabled()
                    ? LC.info_variables_Enabled.getLocale(new Object[0])
                    : LC.info_variables_Disabled.getLocale(new Object[0]));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            2,
            cmiPortal.isShowParticles() ? CMIMaterial.GREEN_WOOL : CMIMaterial.RED_WOOL,
            CMILC.getIM(
                this,
                "particles",
                "[state]",
                cmiPortal.isShowParticles()
                    ? LC.info_variables_Enabled.getLocale(new Object[0])
                    : LC.info_variables_Disabled.getLocale(new Object[0]))) {
          public void click(GUIClickType clickType) {
            cmiPortal.setShowParticles(!cmiPortal.isShowParticles());
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setItem(
                cmiPortal.isShowParticles()
                    ? CMIMaterial.GREEN_WOOL.newItemStack()
                    : CMIMaterial.RED_WOOL.newItemStack());
            this.setName(
                CMILC.getIM(
                    portals.class,
                    "particles",
                    "[state]",
                    cmiPortal.isShowParticles()
                        ? LC.info_variables_Enabled.getLocale(new Object[0])
                        : LC.info_variables_Disabled.getLocale(new Object[0])));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            3,
            cmiPortal.getCMIEffect().getParticle().getIcon(),
            CMILC.getIM(
                this, "particleType", "[type]", cmiPortal.getCMIEffect().getParticle().getName())) {
          public void click(GUIClickType clickType) {
            CMIEffectPicker cmiEffectPicker =
                new CMIEffectPicker(player, cmiPortal.getCMIEffect()) {
                  public void onGoingBack() {
                    portals.this.openUI(player, cmiPortal);
                  }

                  public void onUIClose() {
                    cmi.getPortalManager().savePortals();
                  }

                  public void onSerializeClick() {}

                  public void onUpdate() {
                    cmiPortal.setCMIEffect(this.getEffect());
                  }
                };
            cmiEffectPicker.setShowBackButton(true);
            cmiEffectPicker.open();
          }

          public void updateLooks() {
            this.setItem(
                CMIMaterial.get(cmiPortal.getCMIEffect().getParticle().getIcon()).newItemStack());
            this.setName(
                CMILC.getIM(
                    portals.class,
                    "particleType",
                    "[type]",
                    cmiPortal.getCMIEffect().getParticle().getName()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            4,
            CMIMaterial.BLACK_WOOL,
            CMILC.getIM(this, "particleAmount", "[amount]", cmiPortal.getParticleAmount())) {
          public void click(GUIClickType clickType) {
            byte b = 0;
            switch (clickType) {
              case Left:
                b = 1;
                break;
              case LeftShift:
                b = 10;
                break;
              case Right:
                b = -1;
                break;
              case RightShift:
                b = -10;
            }

            cmiPortal.setParticleAmount(cmiPortal.getParticleAmount() + b);
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM(
                    portals.class, "particleAmount", "[amount]", cmiPortal.getParticleAmount()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            5,
            CMIMaterial.BROWN_WOOL,
            CMILC.getIM(this, "particlePercent", "[amount]", cmiPortal.getPercentToHide())) {
          public void click(GUIClickType clickType) {
            byte percentStep = 0;
            switch (clickType) {
              case Left:
                percentStep = 1;
                break;
              case LeftShift:
                percentStep = 10;
                break;
              case Right:
                percentStep = -1;
                break;
              case RightShift:
                percentStep = -10;
            }

            cmiPortal.setPercentToHide(cmiPortal.getPercentToHide() + percentStep);
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM(
                    portals.class, "particlePercent", "[amount]", cmiPortal.getPercentToHide()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            14,
            CMIMaterial.PINK_WOOL,
            CMILC.getIM(this, "particleOrigin", "[value]", cmiPortal.getParticleOrigin())) {
          public void click(GUIClickType clickType) {
            cmiPortal.setParticleOrigin(cmiPortal.getParticleOrigin().getNext());
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM(
                    portals.class, "particleOrigin", "[value]", cmiPortal.getParticleOrigin()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            6,
            CMIMaterial.COMPASS,
            CMILC.getIM(this, "activationRange", "[amount]", cmiPortal.getActivationRange())) {
          public void click(GUIClickType clickType) {
            byte rangeStep = 0;
            switch (clickType) {
              case Left:
                rangeStep = 1;
                break;
              case LeftShift:
                rangeStep = 10;
                break;
              case Right:
                rangeStep = -1;
                break;
              case RightShift:
                rangeStep = -10;
            }

            cmiPortal.setActivationRange(cmiPortal.getActivationRange() + rangeStep);
            cmi.getPortalManager().recalculateChunks();
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM(
                    portals.class, "activationRange", "[amount]", cmiPortal.getActivationRange()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(7) {
          public void click(GUIClickType clickType) {
            cmiPortal.setParticlesByPermission(!cmiPortal.isParticlesByPermission());

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
              cmi.getPortalManager().removeNearPortal(onlinePlayer.getUniqueId(), cmiPortal);
              cmi.getPortalManager().removeLastPortalInRange(onlinePlayer.getUniqueId());
              cmi.getPortalManager().handlePortalVisualizerUpdates();
            }

            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setItem(
                !cmiPortal.isParticlesByPermission()
                    ? CMIMaterial.GREEN_WOOL.newItemStack()
                    : CMIMaterial.PINK_WOOL.newItemStack());
            this.setName(
                CMILC.getIM(
                    portals.class.getSimpleName(),
                    "particlePermission",
                    "[state]",
                    !cmiPortal.isParticlesByPermission()));
            this.addLore(
                "{gcs}"
                    + PermissionsManager.CMIPerm.command_portal_$1.getPermission(
                        cmiPortal.getName()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    if (cmiPortal.getBungeeServer() != null) {
      cmiGuiButton =
          new CMIGuiButton(
              38,
              cmiPortal.isToExactBungeeLocation() ? CMIMaterial.GREEN_WOOL : CMIMaterial.RED_WOOL,
              CMILC.getIM(this, "bungeeTeleport", "[state]", cmiPortal.isToExactBungeeLocation())) {
            public void click(GUIClickType clickType) {
              cmiPortal.setToExactBungeeLocation(!cmiPortal.isToExactBungeeLocation());
            }
          };
      cmiGuiButton.addCommand("cmi portals edit " + cmiPortal.getName());
      cmiGui.addButton(cmiGuiButton);
    }

    cmiGuiButton =
        new CMIGuiButton(
            27,
            cmiPortal.getPerformCommandsWithoutTp() ? CMIMaterial.GREEN_WOOL : CMIMaterial.RED_WOOL,
            CMILC.getIM(this, "performCommandsWithoutTp")) {
          public void click(GUIClickType clickType) {
            cmiPortal.setPerformCommandsWithoutTp(!cmiPortal.getPerformCommandsWithoutTp());
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setItem(
                cmiPortal.getPerformCommandsWithoutTp()
                    ? CMIMaterial.GREEN_WOOL.newItemStack()
                    : CMIMaterial.RED_WOOL.newItemStack());
            this.setName(CMILC.getIM(portals.class, "performCommandsWithoutTp"));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(36, CMIMaterial.ENCHANTED_BOOK, CMILC.getIM(this, "editCommands"));
    cmiGuiButton.addCommand("cmi portals commands " + cmiPortal.getName());
    cmiGuiButton.setCloseInv(true);
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            45,
            CMIMaterial.ACACIA_DOOR,
            CMILC.getIM(this, "cancelWarmup", "[state]", cmiPortal.isCancelCmdWarmup())) {
          public void click(GUIClickType clickType) {
            cmiPortal.setCancelCmdWarmup(!cmiPortal.isCancelCmdWarmup());
            this.updateLooks();
            this.update(cmiGui);
            cmi.getPortalManager().savePortals();
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM("portals", "cancelWarmup", "[state]", cmiPortal.isCancelCmdWarmup()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(39) {
          public void click(GUIClickType clickType) {
            cmiPortal.setRequiresPermission(!cmiPortal.isRequiresPermission());
            this.updateLooks();
            this.update(cmiGui);
            cmi.getPortalManager().savePortals();
          }

          public void updateLooks() {
            this.setItem(
                cmiPortal.isRequiresPermission()
                    ? CMIMaterial.GREEN_WOOL.newItemStack()
                    : CMIMaterial.RED_WOOL.newItemStack());
            this.setName(
                CMILC.getIM(
                    "portals",
                    "requiresPermission",
                    "[state]",
                    cmiPortal.isRequiresPermission(),
                    "[perm]",
                    PermissionsManager.CMIPerm.command_portal_$1.getPermission(
                        cmiPortal.getName().toLowerCase())));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(40) {
          public void click(GUIClickType clickType) {
            cmiPortal.setInformOnMissingPerm(!cmiPortal.isInformOnMissingPerm());
            this.updateLooks();
            this.update(cmiGui);
            cmi.getPortalManager().savePortals();
          }

          public void updateLooks() {
            this.setItem(
                cmiPortal.isInformOnMissingPerm()
                    ? CMIMaterial.GREEN_STAINED_GLASS.newItemStack()
                    : CMIMaterial.PINK_STAINED_GLASS.newItemStack());
            this.setName(
                CMILC.getIM(
                    "portals", "informPermission", "[state]", cmiPortal.isInformOnMissingPerm()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(41) {
          public void click(GUIClickType clickType) {
            cmiPortal.setKickBack(!cmiPortal.isKickBack());
            this.updateLooks();
            this.update(cmiGui);
            cmi.getPortalManager().savePortals();
          }

          public void updateLooks() {
            this.setItem(
                cmiPortal.isKickBack()
                    ? CMIMaterial.GREEN_STAINED_GLASS.newItemStack()
                    : CMIMaterial.PINK_STAINED_GLASS.newItemStack());
            this.setName(CMILC.getIM("portals", "kickBack", "[state]", cmiPortal.isKickBack()));
            this.addLore(
                "{gcs}"
                    + PermissionsManager.CMIPerm.command_portal_$1.getPermission(
                        cmiPortal.getName()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(47) {
          public void click(GUIClickType clickType) {
            switch (clickType) {
              case Left:
              case LeftShift:
                cmiPortal.setSafeLoc(player.getLocation());
                break;
              case Right:
              case RightShift:
                cmiPortal.setSafeLoc(null);
            }

            this.updateLooks();
            this.update(cmiGui);
          }

          public void updateLooks() {
            this.setItem(
                cmiPortal.getSafeLoc() != null
                    ? CMIMaterial.GREEN_WOOL.newItemStack()
                    : CMIMaterial.RED_WOOL.newItemStack());
            this.setName(CMILC.getIM(portals.class, "setSafeLocation"));
            if (cmiPortal.getSafeLoc() != null) {
              this.addLore(
                  "{gcs}" + LC.Location_Full.getLocale(new Object[] {cmiPortal.getSafeLoc()}));
            }
          }
        };
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            48,
            cmiPortal.getTpLoc() == null
                    && (!cmi.getBungeeCordManager().isBungeeCord()
                        || cmiPortal.getBungeeLocation() == null)
                ? CMIMaterial.RED_WOOL
                : CMIMaterial.GREEN_WOOL,
            CMILC.getIM(this, "setTeleportLocation"));
    if (cmi.getBungeeCordManager().isBungeeCord()) {
      cmiGuiButton.addLore(
          LC.info_bungee_server.getLocale(
              new Object[] {
                "[name]",
                cmiPortal.getBungeeServer() == null ? "notSet" : cmiPortal.getBungeeServer()
              }));
      cmiGuiButton.addLore(
          LC.Location_OneLiner.getLocale(
              new Object[] {
                "[location]",
                cmiPortal.getBungeeLocation() == null ? "notSet" : cmiPortal.getBungeeLocation()
              }));
      cmiGuiButton.addCommand(
          GUIClickType.Left, "cmi portals showbungeemsg " + cmiPortal.getName());
      cmiGuiButton.addCommand(
          GUIClickType.Right, "cmi portals edit " + cmiPortal.getName() + " location false");
      cmiGuiButton.setCloseInv(true);
    } else {
      cmiGuiButton.addLore(
          LC.Location_OneLiner.getLocale(
              new Object[] {
                "[location]", cmi.getPlayerManager().convertLocToStringShort(cmiPortal.getTpLoc())
              }));
      cmiGuiButton.addCommand(
          GUIClickType.Left, "cmi portals edit " + cmiPortal.getName() + " location true");
      cmiGuiButton.addCommand(
          GUIClickType.Right, "cmi portals edit " + cmiPortal.getName() + " location false");
      cmiGuiButton.addCommand("cmi portals edit " + cmiPortal.getName());
    }

    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(49, CMIMaterial.FILLED_MAP, CMILC.getIM(this, "teleportToTarget"));
    cmiGuiButton.addCommand("cmi portals edit " + cmiPortal.getName() + " teleport");
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton = new CMIGuiButton(50, CMIMaterial.MAP, CMILC.getIM(this, "teleportToLocation"));
    cmiGuiButton.addCommand("cmi portals edit " + cmiPortal.getName() + " teleportoutside");
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(51, CMIMaterial.OAK_FENCE.getMaterial(), CMILC.getIM(this, "redefine"));
    cmiGuiButton.addCommand("cmi portals edit " + cmiPortal.getName() + " redefine");
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton = new CMIGuiButton(53, CMIMaterial.RED_WOOL, CMILC.getIM(this, "removePortal"));
    cmiGuiButton.addCommand("cmi portals edit " + cmiPortal.getName() + " remove");
    cmiGui.addButton(cmiGuiButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
    return true;
  }

  private enum Action {
    edit,
    showbungeemsg,
    bungeeloc,
    createnew,
    commands,
    list,
    nearest,
    moveup,
    movedown,
    add,
    remove,
    forceupdate,
    setlocation,
    enabled;

    public static portals.Action getByName(String string) {
      if (string.equalsIgnoreCase("new")) {
        return createnew;
      }

      portals.Action[] actions;
      int value = (actions = values()).length;

      for (int argIndex = 0; argIndex < value; argIndex++) {
        portals.Action action = actions[argIndex];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
