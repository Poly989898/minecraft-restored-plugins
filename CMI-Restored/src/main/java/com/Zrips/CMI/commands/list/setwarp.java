package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Warps.CmiWarp;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class setwarp implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("newSet", "{gcp}New warp (&6[warp]{gcp}) set");
    configReader.get("noSet", "{gcw}Warp with this name already exists");
    configReader.get("noMore", "{gcw}You have reached max allowed warp limit. ({gcps}[max]{gcp})");
  }

  @CAnnotation(
      info = "Sets warp location",
      args =
          "[warpName] (reqPermission) (hand/head/head:[playerName]) (slot) (autoLore)"
              + " (-g:[groupName]) (world;x;y;z;yaw;pitch) (-confirm)",
      explanation = {
        "Examples:",
        "/cmi setwarp spawn - simple warp to spawn",
        "/cmi setwarp spawn true - creates warp and will require cmi.command.warp.[warpname]"
            + " permission node to use it",
        "/cmi setwarp spawn hand - creates warp will take item from hand to display in gui for this"
            + " warp",
        "/cmi setwarp spawn 13 - creates warp and sets gui slot to be used in gui (1-54)",
        "/cmi setwarp spawn true hand 13 - creates warp with icon from hand, slot 13 and requires"
            + " permission",
        "/cmi setwarp spawn true hand 13 false - same as previous, but doesn't generate lore"
      },
      regVar = {1, 2, 3, 4, 5, 6, 7, 8},
      consoleVar = {2, 3, 4, 5, 6, 7, 8},
      others = false)
  @Override
  public Boolean perform(final CMI cmi, final CommandSender commandSender, String[] strings) {
    String string = null;
    String warpName = null;
    Boolean hasFlag = null;
    ItemStack itemStack = null;
    Integer integer = null;
    Boolean flagAlt = null;
    CMILocation cmiLocation = null;
    boolean flag = false;
    String group = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("-confirm")) {
        flag = true;
      } else if (arg.startsWith("-g:")) {
        warpName = arg.substring("-g:".length());
      } else if (string == null) {
        string = arg;
      } else if (hasFlag == null && arg.equalsIgnoreCase("true")) {
        hasFlag = true;
      } else if (hasFlag == null && arg.equalsIgnoreCase("false")) {
        hasFlag = false;
      } else if (arg.equalsIgnoreCase("hand") && commandSender instanceof Player) {
        itemStack =
            CMILib.getInstance().getReflectionManager().getItemInMainHand((Player) commandSender);
      } else if (arg.toLowerCase().startsWith("head:")) {
        group = arg.substring("head:".length(), arg.length());
      } else if (arg.equalsIgnoreCase("head") && commandSender instanceof Player) {
        group = commandSender.getName();
      } else {
        try {
          integer = Integer.parseInt(arg) - 1;
        } catch (Exception exception) {
        }

        if (flagAlt == null && arg.equalsIgnoreCase("true")) {
          flagAlt = true;
        } else if (flagAlt == null && arg.equalsIgnoreCase("false")) {
          flagAlt = false;
        } else if (cmiLocation == null) {
          cmiLocation = CMILocation.fromString(arg);
          if (cmiLocation != null && !cmiLocation.isValid()) {
            cmiLocation = null;
          }
        }
      }
    }

    if (cmiLocation != null
        && !PermissionsManager.CMIPerm.command_warp_specificlocation.hasPermission(
            commandSender, true)) {
      return null;
    }

    string = CMIChatColor.stripColor(string);
    if (cmi.getWarpManager().getWarp(string) != null) {
      if (!PermissionsManager.CMIPerm.command_warp_redefine.hasPermission(commandSender)) {
        CMILC.info(this, commandSender, "noSet");
        return null;
      }

      if (!flag) {
        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(
            CMILC.getIM(this, "noSet") + "\n" + LC.info_confirmRedefine.getLocale(new Object[0]));
        rawMessage.addHover(LC.info_Click.getLocale(new Object[0]));
        rawMessage.addCommand(
            CommandsHandler.getLabel()
                + " "
                + this.getClass().getSimpleName()
                + " "
                + string
                + " -confirm"
                + (warpName == null ? "" : " -g:" + warpName)
                + (hasFlag == null ? "" : " " + hasFlag)
                + (itemStack == null ? "" : " hand")
                + (integer == null ? "" : " " + integer)
                + (flagAlt == null ? "" : " " + flagAlt)
                + (cmiLocation == null ? "" : " " + CMILocation.toString(cmiLocation))
                + (group == null ? "" : " head:" + group));
        rawMessage.show(commandSender);
        return null;
      }
    }

    if (string.length() < cmi.getWarpManager().getMinLength()) {
      LC.info_valueToShort.sendMessage(
          commandSender, new Object[] {"[min]", cmi.getWarpManager().getMinLength()});
      return null;
    }

    if (string.length() > cmi.getWarpManager().getMaxLength()) {
      LC.info_valueToLong.sendMessage(
          commandSender, new Object[] {"[max]", cmi.getWarpManager().getMaxLength()});
      return null;
    }

    if (commandSender instanceof Player) {
      Player player = (Player) commandSender;
      int warpCount = cmi.getWarpManager().getWarpCount(player);
      value = cmi.getWarpManager().getMaxWarps(player);
      if (warpCount >= value) {
        CMILC.info(this, commandSender, "noMore", "[max]", value);
        return null;
      }
    }

    CmiWarp cmiWarp = cmi.getWarpManager().getWarp(string);
    if (cmiWarp == null) {
      cmiWarp = new CmiWarp(string);
      if (hasFlag == null) {
        hasFlag = cmi.getWarpManager().isWarpRequirePerm();
      }
    }

    final CmiWarp warpCopy = cmiWarp;
    if (hasFlag != null) {
      warpCopy.setReqPerm(hasFlag);
    }

    if (cmiLocation == null && commandSender instanceof Player) {
      cmiLocation = new CMILocation(((Player) commandSender).getLocation());
    }

    if (cmiLocation != null && cmiLocation.isValid()) {
      warpCopy.setLoc(cmiLocation);
      if (integer != null) {
        warpCopy.setSlot(integer);
      }

      if (flagAlt != null) {
        warpCopy.setAutoLore(flagAlt);
      }

      if (warpName != null) {
        warpCopy.setGroup(warpName);
      }

      warpCopy.setCreator(
          commandSender instanceof Player
              ? ((Player) commandSender).getUniqueId()
              : cmi.getServerUUID());
      if (itemStack != null && itemStack.getType() != Material.AIR) {
        warpCopy.setItem(itemStack);
      }

      if (group != null) {
        CMIAsyncHead cmiAsyncHead =
            new CMIAsyncHead() {
              public void afterAsyncUpdate(ItemStack warpItem) {
                CMIScheduler.runTask(
                    cmi,
                    () -> {
                      warpCopy.setItem(warpItem);
                      cmi.getWarpManager().addWarp(warpCopy);
                      if (cmi.getWarpManager().isGUIOnCreation()
                          && commandSender instanceof Player) {
                        cmi.getWarpManager().openWarpEditor((Player) commandSender, warpCopy);
                      }
                    });
              }
            };
        cmiAsyncHead.setForce(true);
        warpCopy.setItem(
            CMILib.getInstance()
                .getItemManager()
                .getItem("head:" + group, cmiAsyncHead)
                .getItemStack());
      }

      cmi.getWarpManager().addWarp(warpCopy);
      CMILC.info(this, commandSender, "newSet", "[warp]", cmiWarp.getName());
      if (cmi.getWarpManager().isGUIOnCreation() && commandSender instanceof Player) {
        cmi.getWarpManager().openWarpEditor((Player) commandSender, warpCopy);
      }

      return true;
    } else {
      LC.info_IncorrectLocation.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
