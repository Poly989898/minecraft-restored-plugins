/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMIArray
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Items.CMIItemStack
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.NBT.CMINBT
 *  net.Zrips.CMILib.RawMessages.RawMessage
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIItemRepair;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.AttachedCommands.CustomNBTManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class repair implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have repaired {gcs}[amount]{gcp} items for {gcs}[playerDisplayName]{gcp}.");
    configReader.get("nothing", "{gcp}Nothing to repair");
    configReader.get("cant", "{gcp}Can't repair this item");
    configReader.get(
        "feedbackMoney",
        "{gcp}You have repaired {gcs}[amount]{gcp} items for {gcs}[playerDisplayName]{gcp}. Cost:"
            + " {gcs}[cost]");
    configReader.get("itemOwner", "{gcp}Last repaired by: {gcs}[owner]");
    configReader.get(
        "cantUse", "!title!{gcw}Can't use this item!!subtitle!{gcp}Repair it your self!");
  }

  @Override
  @CAnnotation(
      info = "Repair items",
      args = "[hand/offhand/armor/all] (playerName)",
      multiTab = {"hand,offhand,armor,all [playername]"},
      explanation = {
        "cmi.command.repair.hand - allows a user to repair items in their hand",
        "cmi.command.repair.offhand - allows a user to repair items in their offhand",
        "cmi.command.repair.armor - allows a user to repair items in armor slots",
        "cmi.command.repair.all - allows a user to repair their whole inventory",
        "cmi.command.repair.repairshare.bypass - allows a user to repair items without adding"
            + " repair share protection"
      },
      regVar = {0, 1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] args) {
    Object repairMode = repairType.hand;
    String targetName = null;
    boolean bl = false;
    for (String arg : args) {
      switch (arg.toLowerCase()) {
        case "-c":
          {
            bl = true;
            break;
          }
        default:
          {
            Object found = repairType.get(arg);
            if (found != null) {
              repairMode = found;
              break;
            }
            targetName = arg;
          }
      }
    }
    Player target = cMI.getTarget(commandSender, targetName, this);
    if (target == null
        || !PermissionsManager.CMIPerm.command_repair_$1.hasPermission(
            commandSender, true, ((Enum) repairMode).toString())) {
      return null;
    }
    List<ItemStack> repairItems = ((repairType) repairMode).getItems(target);
    Iterator<ItemStack> iterator = repairItems.iterator();
    while (iterator.hasNext()) {
      ItemStack item = iterator.next();
      if (cMI.getUtilManager().canRepair(item)) continue;
      iterator.remove();
    }
    if (repairItems.isEmpty()) {
      CMILC.info((Object) this, commandSender, "nothing", new Object[0]);
      return null;
    }
    UUID shareId = null;
    if (commandSender instanceof Player
        && !PermissionsManager.CMIPerm.command_repair_repairshare_bypass.hasPermission(
            commandSender)) {
      shareId = target.getUniqueId();
    }
    if (shareId == null && !(commandSender instanceof Player)) {
      shareId = target.getUniqueId();
    }
    int repaired = 0;
    double cost = CMIItemRepair.getRepairCost(repairItems);
    if (cost > 0.0
        && cMI.getRepairManager().isRepairCostsMoney()
        && target.isOnline()
        && !PermissionsManager.CMIPerm.command_repair_bypass.hasPermission(
            (CommandSender) target)) {
      CMIUser cmiUser =
          cMI.getPlayerManager()
              .getUser(
                  (Player) (commandSender instanceof Player ? (Player) commandSender : target));
      if (cmiUser != null && !cmiUser.hasMoney(cost)) {
        LC.econ_notEnoughMoney.sendMessage(
            (Object) commandSender,
            new Object[] {"[amount]", cMI.getEconomyManager().format(cost)});
        return null;
      }
      if (cMI.getRepairManager().isRepairConfirmation() && !bl && commandSender instanceof Player) {
        RawMessage rawMessage = new RawMessage();
        rawMessage
            .addText(
                CMILC.info_repairConfirm.getLocale(
                    "[items]", repairItems.size(), "[cost]", cMI.getEconomyManager().format(cost)))
            .addHover(LC.info_Click)
            .addCommand(
                String.valueOf(CommandsHandler.getLabel())
                    + " "
                    + this.getClass().getSimpleName()
                    + " "
                    + CMIArray.toString(args)
                    + " -c");
        rawMessage.show(commandSender);
        return null;
      }
      if (cmiUser != null) {
        cmiUser.withdraw(cost);
      }
    }
    for (ItemStack damagedItem : repairItems) {
      if (!cMI.getUtilManager().canRepair(damagedItem)) continue;
      ItemStack repairedItem = damagedItem;
      if (cMI.getRepairManager().isRSProtectCommandRepair() && shareId != null) {
        ItemStack slotItem;
        repairedItem =
            (ItemStack)
                new CMINBT(damagedItem)
                    .setString(
                        CustomNBTManager.CustomNBTType.CMIRepairMan.name(), shareId.toString());
        ItemStack[] contents = target.getInventory().getContents();
        ItemStack[] armorContents = target.getInventory().getArmorContents();
        boolean inContents = false;
        boolean inArmor = false;
        int index = 0;
        while (index < contents.length) {
          slotItem = contents[index];
          if (slotItem != null && slotItem.equals((Object) damagedItem)) {
            repaired++;
            cMI.getUtilManager().repairItem(repairedItem);
            contents[index] = repairedItem;
            inContents = true;
            cMI.getCustomNBTManager().updateRepairLore(repairedItem, null);
            break;
          }
          index++;
        }
        if (!inContents) {
          index = 0;
          while (index < armorContents.length) {
            slotItem = armorContents[index];
            if (slotItem != null && slotItem.equals((Object) damagedItem)) {
              repaired++;
              cMI.getUtilManager().repairItem(repairedItem);
              armorContents[index] = repairedItem;
              inArmor = true;
              cMI.getCustomNBTManager().updateRepairLore(repairedItem, null);
              break;
            }
            index++;
          }
        }
        target.getInventory().setContents(contents);
        if (!inArmor) continue;
        target.getInventory().setArmorContents(armorContents);
        continue;
      }
      repairedItem = cMI.getUtilManager().repairItem(repairedItem);
      if (repairedItem == null) continue;
      repaired++;
    }
    if (target.isOnline()) {
      target.updateInventory();
    } else {
      cMI.save(target);
    }
    Snd snd = new Snd().setSender(commandSender).setTarget(target);
    if (cost > 0.0 && cMI.getRepairManager().isRepairCostsMoney()) {
      CMILC.info(
          (Object) this,
          commandSender,
          "feedbackMoney",
          "[amount]",
          repaired,
          "[cost]",
          cMI.getEconomyManager().format(cost),
          snd);
    } else {
      CMILC.info((Object) this, commandSender, "feedback", "[amount]", repaired, snd);
    }
    return repaired == 0 ? null : Boolean.valueOf(true);
  }

  private static List<ItemStack> filterItems(ItemStack[] itemStackArray) {
    ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
    ItemStack[] items = itemStackArray;
    int n = itemStackArray.length;
    int index = 0;
    while (index < n) {
      ItemStack itemStack = items[index];
      if (itemStack != null
          && itemStack.getType() != Material.AIR
          && CMI.getInstance().getUtilManager().canRepair(itemStack)) {
        arrayList.add(itemStack);
      }
      ++index;
    }
    return arrayList;
  }

  public static enum repairType {
    hand {

      @Override
      public List<ItemStack> getItems(Player player) {
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        ItemStack itemStack = CMIItemStack.getItemInMainHand((Player) player);
        if (!CMI.getInstance().getUtilManager().canRepair(itemStack)) {
          return arrayList;
        }
        arrayList.add(itemStack);
        return arrayList;
      }
    },
    offhand {

      @Override
      public List<ItemStack> getItems(Player player) {
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        ItemStack itemStack = CMIItemStack.getItemInOffHand((Player) player);
        if (!CMI.getInstance().getUtilManager().canRepair(itemStack)) {
          return arrayList;
        }
        arrayList.add(itemStack);
        return arrayList;
      }
    },
    armor {

      @Override
      public List<ItemStack> getItems(Player player) {
        return repair.filterItems(player.getInventory().getArmorContents());
      }
    },
    all {

      @Override
      public List<ItemStack> getItems(Player player) {
        return repair.filterItems(player.getInventory().getContents());
      }
    };

    public static repairType get(String string) {
      repairType[] repairTypeArray = repairType.values();
      int n = repairTypeArray.length;
      int index = 0;
      while (index < n) {
        repairType candidate = repairTypeArray[index];
        if (candidate.name().equalsIgnoreCase(string)) {
          return candidate;
        }
        ++index;
      }
      return null;
    }

    public List<ItemStack> getItems(Player player) {
      return new ArrayList<ItemStack>();
    }
  }
}
