/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.ActionBar.CMIActionBar
 *  net.Zrips.CMILib.CMILib
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Items.CMIMaterial
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.Chunk
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.itemInfo;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Region.CMIRegion;
import com.Zrips.CMI.Modules.Region.WorldInfo;
import com.Zrips.CMI.Modules.ReplaceBlock.BRInfo;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.Arrays;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class replaceblock implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("startScanning", "{gcp}Found {gcs}[amount] {gcp}chunks to check. Starting!");
    configReader.get(
        "paused", "{gcp}Replacing paused, to continue use {gcs}/cmi blockreplace continue.");
    configReader.get("continuing", "{gcp}Replacing continuing");
    configReader.get("stoped", "{gcp}Replacing stopped.");
    configReader.get("speedChange", "{gcp}Speed changed to [amount]");
    configReader.get("autospeedChange", "{gcp}AutoSpeed changed to [state]");
    configReader.get("messagesChange", "{gcp}Message state changed to [state]");
    configReader.get(
        "infoShow",
        "{gcs}TPS: &3[tps] {gcs}Left {gcp}[left] {gcs}({gcp}[time]{gcs}) {gcs}Speed: {gcp}[speed]"
            + " {gcs}Replaced: {gcp}[found]");
    configReader.get(
        "finished", "{gcp}Finished!!! Replaced: {gcs}[amount] {gcp}blocks! In {gcs}[time]{gcp}.");
  }

  @Override
  @CAnnotation(
      priority = 286,
      info = "Replaces blocks in current world around you",
      args =
          "id [blockName:data/id:data] w [blockName:data/id:data] r [range in chunks/g] y [max"
              + " height]",
      explanation = {
        "SubCommands:",
        "- pause &e- pause replacing",
        "- continue &e- continue replacing",
        "- stop &e- stop replacing",
        "- speed [amount] &e- set current replace speed",
        "- autospeed [true/false] &e- set autospeed turned off or on",
        "- messages [true/false] &e- set message output to off or on",
        "Example:",
        "/cmi replaceblock id 52 w stone r 10",
        "/cmi replaceblock id 52,gold_block w stone r 15 y 100",
        "/cmi replaceblock id 52 w air r g y 100",
        "/cmi replaceblock id iron_ore%75 w stone%90,dirt%5 r g"
      },
      regVar = {-66},
      consoleVar = {666},
      others = false)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    Integer n;
    BRInfo bRInfo;
    Player player;
    block77:
    {
      player = (Player) commandSender;
      if (stringArray.length == 1) {
        if (stringArray[0].equalsIgnoreCase("pause") || stringArray[0].equalsIgnoreCase("p")) {
          if (cMI.getReplaceBlockManager().BRInfo.containsKey(player.getName())
              && cMI.getReplaceBlockManager().BRInfo.get(player.getName()).isRunning()) {
            CMIActionBar.send((Player) player, (String) CMILC.getIM(this, "paused", new Object[0]));
            cMI.getReplaceBlockManager().BRInfo.get(player.getName()).setRunning(false);
            return true;
          }
        } else if (stringArray[0].equalsIgnoreCase("continue")
            || stringArray[0].equalsIgnoreCase("c")) {
          if (cMI.getReplaceBlockManager().BRInfo.containsKey(player.getName())
              && !cMI.getReplaceBlockManager().BRInfo.get(player.getName()).isRunning()) {
            CMIActionBar.send(
                (Player) player, (String) CMILC.getIM(this, "continuing", new Object[0]));
            cMI.getReplaceBlockManager().BRInfo.get(player.getName()).setRunning(true);
            cMI.getReplaceBlockManager().BRInfo.get(player.getName()).setStartTime();
            cMI.getReplaceBlockManager()
                .start(cMI.getReplaceBlockManager().BRInfo.get(player.getName()));
            return true;
          }
        } else if ((stringArray[0].equalsIgnoreCase("stop") || stringArray[0].equalsIgnoreCase("s"))
            && cMI.getReplaceBlockManager().BRInfo.containsKey(player.getName())
            && cMI.getReplaceBlockManager().BRInfo.get(player.getName()).isRunning()) {
          CMILC.info((Object) this, (CommandSender) player, "stoped", new Object[0]);
          cMI.getReplaceBlockManager().BRInfo.get(player.getName()).setRunning(false);
          return true;
        }
      }
      if (stringArray.length == 2) {
        if (stringArray[0].equalsIgnoreCase("speed")
            && PermissionsManager.CMIPerm.command_replaceblock_speed.hasPermission(
                (CommandSender) player, true, new String[0])) {
          int replaceSpeed = 16;
          try {
            replaceSpeed = Integer.parseInt(stringArray[1]);
          } catch (NumberFormatException numberFormatException) {
            commandSender.sendMessage(LC.info_UseInteger.getLocale(new Object[0]));
            cMI.getCommandManager().sendUsage(commandSender, this.getClass().getSimpleName());
            return true;
          }
          if (cMI.getReplaceBlockManager().BRInfo.containsKey(player.getName())) {
            BRInfo speedInfo = cMI.getReplaceBlockManager().BRInfo.get(player.getName());
            if (speedInfo.getSpeed() < 1) {
              speedInfo.setSpeed(1);
            } else if (speedInfo.getSpeed() > 100) {
              speedInfo.setSpeed(100);
            } else {
              speedInfo.setSpeed(replaceSpeed);
            }
            CMILC.info(
                (Object) this, (CommandSender) player, "speedChange", "[amount]", replaceSpeed);
          }
          return true;
        }
        if (stringArray[0].equalsIgnoreCase("autospeed")) {
          boolean bl = true;
          if (!stringArray[1].equalsIgnoreCase("true") && !stringArray[1].equalsIgnoreCase("yes")) {
            if (stringArray[1].equalsIgnoreCase("false") || stringArray[1].equalsIgnoreCase("no")) {
              bl = false;
            } else {
              LC.info_UseBoolean.sendMessage((Object) commandSender, new Object[0]);
              cMI.getCommandManager().sendUsage(commandSender, this.getClass().getSimpleName());
              return true;
            }
          }
          if (cMI.getReplaceBlockManager().BRInfo.containsKey(player.getName())) {
            BRInfo autoSpeedInfo = cMI.getReplaceBlockManager().BRInfo.get(player.getName());
            autoSpeedInfo.setAutoSpeed(bl);
            CMILC.info((Object) this, (CommandSender) player, "autospeedChange", "[state]", bl);
          }
          return true;
        }
        if (stringArray[0].equalsIgnoreCase("messages")) {
          boolean bl = true;
          if (!stringArray[1].equalsIgnoreCase("true") && !stringArray[1].equalsIgnoreCase("yes")) {
            if (stringArray[1].equalsIgnoreCase("false") || stringArray[1].equalsIgnoreCase("no")) {
              bl = false;
            } else {
              LC.info_UseBoolean.sendMessage((Object) commandSender, new Object[0]);
              cMI.getCommandManager().sendUsage(commandSender, this.getClass().getSimpleName());
              return true;
            }
          }
          if (cMI.getReplaceBlockManager().BRInfo.containsKey(player.getName())) {
            BRInfo messageInfo = cMI.getReplaceBlockManager().BRInfo.get(player.getName());
            messageInfo.setMessages(bl);
            CMILC.info((Object) this, (CommandSender) player, "messagesChange", "[state]", bl);
          }
          return true;
        }
      }
      bRInfo = new BRInfo();
      n = cMI.getScanManager().getDefaultRange();
      if (stringArray.length < 1) break block77;
      int argPos = 0;
      String[] commandArgs = stringArray;
      int argCount = stringArray.length;
      int argIndex = 0;
      while (argIndex < argCount) {
        object = commandArgs[argIndex];
        switch (((String) object).toLowerCase()) {
          case "id":
            {
              itemInfo dropInfo;
              CMIMaterial cMIMaterial;
              if (argPos + 1 >= stringArray.length) break;
              ArrayList<String> arrayList = new ArrayList();
              if (stringArray[argPos + 1].contains(",")) {
                arrayList.addAll(Arrays.asList(stringArray[argPos + 1].split(",")));
              } else {
                arrayList.add(stringArray[argPos + 1]);
              }
              for (String string : arrayList) {
                cMIMaterial = null;
                int dropChance = 100;
                try {
                  if (string.contains("%")) {
                    try {
                      dropChance = Integer.parseInt(string.split("%")[1]);
                    } catch (Exception exception) {
                      // empty catch block
                    }
                    string = string.split("%")[0];
                  }
                  cMIMaterial =
                      !string.contains(":")
                          ? CMIMaterial.get((String) string)
                          : CMIMaterial.get((String) string.split(":")[0]);
                } catch (NumberFormatException numberFormatException) {
                  cMIMaterial = CMIMaterial.get((String) string);
                }
                if (cMIMaterial == null || cMIMaterial == CMIMaterial.NONE) {
                  LC.info_UseMaterial.sendMessage((Object) player, new Object[0]);
                  cMI.getCommandManager().sendUsage(commandSender, this.getClass().getSimpleName());
                  return true;
                }
                dropInfo = new itemInfo(cMIMaterial);
                dropInfo.setChance(dropChance);
                bRInfo.addItemInfo(dropInfo);
              }
              break;
            }
          case "w":
            {
              itemInfo replaceInfo;
              CMIMaterial cMIMaterial;
              if (argPos + 1 >= stringArray.length) break;
              ArrayList<String> arrayList = new ArrayList<String>();
              if (stringArray[argPos + 1].contains(",")) {
                arrayList.addAll(Arrays.asList(stringArray[argPos + 1].split(",")));
              } else {
                arrayList.add(stringArray[argPos + 1]);
              }
              for (String string : arrayList) {
                cMIMaterial = null;
                int replaceChance = 100;
                try {
                  if (string.contains("%")) {
                    try {
                      replaceChance = Integer.parseInt(string.split("%")[1]);
                    } catch (Exception exception) {
                      // empty catch block
                    }
                    string = string.split("%")[0];
                  }
                  cMIMaterial =
                      !string.contains(":")
                          ? CMIMaterial.get((String) string)
                          : CMIMaterial.get((String) string.split(":")[0]);
                } catch (NumberFormatException numberFormatException) {
                  cMIMaterial = CMIMaterial.get((String) string);
                }
                if (cMIMaterial == null || cMIMaterial == CMIMaterial.NONE) {
                  LC.info_UseMaterial.sendMessage((Object) player, new Object[0]);
                  cMI.getCommandManager().sendUsage(commandSender, this.getClass().getSimpleName());
                  return true;
                }
                replaceInfo = new itemInfo(cMIMaterial);
                replaceInfo.setChance(replaceChance);
                bRInfo.addReplaceInfo(replaceInfo);
              }
              break;
            }
          case "y":
            {
              if (argPos + 1 >= stringArray.length) break;
              int yLevel = 0;
              try {
                yLevel = Integer.parseInt(stringArray[argPos + 1]);
              } catch (NumberFormatException numberFormatException) {
                LC.info_UseInteger.sendMessage((Object) commandSender, new Object[0]);
                cMI.getCommandManager().sendUsage(commandSender, this.getClass().getSimpleName());
                return true;
              }
              bRInfo.setY(yLevel);
              break;
            }
          case "h":
          case "hand":
            {
              ItemStack itemStack =
                  CMILib.getInstance().getReflectionManager().getItemInMainHand(player);
              CMIMaterial cMIMaterial = CMIMaterial.get((ItemStack) itemStack);
              if (cMIMaterial == null || cMIMaterial == CMIMaterial.NONE) {
                LC.info_nothingInHand.sendMessage((Object) commandSender, new Object[0]);
                return true;
              }
              itemInfo newItemInfo = new itemInfo(cMIMaterial);
              bRInfo.addItemInfo(newItemInfo);
              break;
            }
          case "r":
          case "range":
            {
              if (argPos + 1 >= stringArray.length) break;
              if (stringArray[argPos + 1].equalsIgnoreCase("g")) {
                n = null;
                break;
              }
              try {
                n = Integer.parseInt(stringArray[argPos + 1]);
                break;
              } catch (NumberFormatException numberFormatException) {
                LC.info_UseInteger.sendMessage((Object) commandSender, new Object[0]);
                cMI.getCommandManager().sendUsage(commandSender, this.getClass().getSimpleName());
                return true;
              }
            }
        }
        ++argPos;
        ++argIndex;
      }
    }
    Chunk chunk = player.getLocation().getChunk();
    object = cMI.getRegionManager().getWorldInfoInRange(player.getWorld(), new CMIRegion(chunk), n);
    if (object == null) {
      CMILC.info_ChunksLoading.sendMessage(player, new Object[0]);
      return true;
    }
    bRInfo.setRI((WorldInfo) object);
    CMIActionBar.send(
        (Player) player,
        (String)
            CMILC.getIM(this, "startScanning", "[amount]", ((WorldInfo) object).getTotalChunks()));
    bRInfo.setPlayer(player);
    bRInfo.setStartTime();
    bRInfo.setRunning(true);
    bRInfo.setShowInfo(System.currentTimeMillis());
    if (cMI.getReplaceBlockManager().BRInfo.containsKey(player.getName())
        && cMI.getReplaceBlockManager().BRInfo.get(player.getName()).getScheduleTask() != null) {
      cMI.getReplaceBlockManager().BRInfo.get(player.getName()).getScheduleTask().cancel();
    }
    cMI.getReplaceBlockManager().BRInfo.put(player.getName(), bRInfo);
    cMI.getReplaceBlockManager().start(bRInfo);
    return true;
  }
}
