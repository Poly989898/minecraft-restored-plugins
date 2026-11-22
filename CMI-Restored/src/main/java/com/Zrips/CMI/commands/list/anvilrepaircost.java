/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.CMILib
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.Repairable
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class anvilrepaircost implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("current", "{gcp}Current items base repair cost: {gcs}[cost]{gcp}.");
    configReader.get("noitem", "{gcw}Not holding any item in hand.");
    configReader.get("cantDetermineCost", "{gcw}Can't determine base repair cost for this item.");
    configReader.get(
        "selffeedback",
        "{gcp}Base repair cost was set to {gcs}[amount] {gcp}for {gcs}[playerDisplayName]{gcp} item"
            + " in hand.");
    configReader.get(
        "targetfeedback",
        "{gcp}Your item repair cost was set to {gcs}[amount]{gcp} by"
            + " {gcs}[senderDisplayName]{gcp}.");
  }

  @Override
  @CAnnotation(
      priority = 280,
      info = "Set items repair cost",
      args = "(playerName) [amount]",
      tab = {"playername"},
      explanation = {
        "Example:",
        "/repaircost 10",
        "/repaircost set 10",
        "/repaircost add 10",
        "/repaircost take 10",
        "/repaircost Zrips clear",
        "/repaircost Zrips add 10"
      },
      regVar = {0, 1, 2, 3},
      consoleVar = {2, 3},
      alias = true,
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] args) {
    boolean bl = false;
    Action action = Action.None;
    int amount = 0;
    String targetName = null;
    for (String arg : args) {
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        case "add":
          {
            action = Action.Add;
            break;
          }
        case "clear":
          {
            action = Action.Clear;
            break;
          }
        case "set":
          {
            action = Action.Set;
            break;
          }
        case "take":
          {
            action = Action.Take;
            break;
          }
        default:
          {
            try {
              amount = Integer.parseInt(arg);
              break;
            } catch (NumberFormatException numberFormatException) {
              targetName = arg;
            }
          }
      }
    }
    if (amount < 0) {
      amount = 0;
      LC.info_NoLessThan.sendMessage((Object) commandSender, new Object[] {"[amount]", 0});
    }
    Player player = cMI.getTarget(commandSender, targetName, this);
    if (player == null) {
      return null;
    }
    ItemStack itemStack = CMILib.getInstance().getReflectionManager().getItemInMainHand(player);
    if (itemStack == null || itemStack.getType() == Material.AIR) {
      CMILC.info((Object) this, commandSender, "noitem", new Object[0]);
      return null;
    }
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta == null || !(itemMeta instanceof Repairable)) {
      CMILC.info((Object) this, commandSender, "cantDetermineCost", new Object[0]);
      return null;
    }
    Repairable repairable = (Repairable) itemMeta;
    if (args.length == 0 || action == Action.None) {
      CMILC.info((Object) this, commandSender, "current", "[cost]", repairable.getRepairCost());
      return true;
    }
    switch (action) {
      case Add:
        {
          amount = repairable.getRepairCost() + amount;
          break;
        }
      case Take:
        {
          amount = repairable.getRepairCost() - amount;
          break;
        }
      case Clear:
        {
          amount = 0;
          break;
        }
      case Set:
        {
          break;
        }
    }
    if (amount < 0) {
      amount = 0;
    }
    repairable.setRepairCost(amount);
    itemStack.setItemMeta((ItemMeta) repairable);
    cMI.save(player);
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    CMILC.info(
        (Object) this, commandSender, "selffeedback", snd, "[amount]", repairable.getRepairCost());
    if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !bl) {
      CMILC.info(
          (Object) this,
          (CommandSender) player,
          "targetfeedback",
          snd,
          "[amount]",
          repairable.getRepairCost());
    }
    return true;
  }

  private static enum Action {
    Set,
    Add,
    Clear,
    Take,
    None;
  }
}
