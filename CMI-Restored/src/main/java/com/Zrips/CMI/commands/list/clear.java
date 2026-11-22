package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class clear implements Cmd {
  HashMap<UUID, Long> map = new HashMap<>();

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "clearFeedback",
        "{gcp}You have removed {gcp}[amount] {gcp}items from {gcp}[playerDisplayName]{gcp}"
            + " inventory.");
    configReader.get(
        "target",
        "{gcp}[amount] {gcp}items got removed from your inventory by"
            + " {gcp}[senderDisplayName]{gcp}.");
    configReader.get(
        "confirm", "{gcu}Click to confirm inventory clear up. &cAll items will be removed!");
  }

  @Override
  @CAnnotation(
      priority = 125,
      info = "Clear players inventory",
      args = "(playerName) (item(:amount)(;data)) (-s) (+clearType) -exact",
      multiTab = {
        "[playerName]"
            + " +quickbar,+inventory,+partinventory,+weapons,+armorslots,+tools,+armors,+mainhand,+offhand,-confirmed,-exact"
      },
      explanation = {
        "Possible clearTypes: quickbar, inventory, partinventory, weapons, armorslots, tools,"
            + " armors, mainhand, offhand",
        "Should start with + sign",
        "Multiple types can be used",
        "cmi clear +armors - will remove armor type items",
        "cmi clear +armorslots - will remove items from armorslots"
      },
      regVar = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
      consoleVar = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
      customAlias = {"ci"},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] args) {
    boolean bl = false;
    String targetName = null;
    boolean confirmedClear = false;
    CMIItemStack cMIItemStack = null;
    boolean fullStack = true;
    boolean exact = false;
    HashMap<clearTypes, Boolean> hashMap = new HashMap<clearTypes, Boolean>();
    for (String arg : args) {
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        case "-confirmed":
          {
            confirmedClear = true;
            break;
          }
        case "-exact":
          {
            exact = true;
            break;
          }
        default:
          {
            clearTypes type = clearTypes.getByName(arg);
            if (type != null) {
              hashMap.put(type, !arg.startsWith("-"));
              break;
            }
            if (cMI.getPlayerManager().getUser(arg) != null) {
              targetName = arg;
              break;
            }
            if (cMIItemStack == null) {
              cMIItemStack = CMIItemStack.deserialize(commandSender, arg);
              if (cMIItemStack != null) {
                if (cMIItemStack.getAmount() <= 1
                    && !arg.split(";")[0].contains("-")
                    && !arg.split(";")[0].contains(":")) break;
                fullStack = false;
                break;
              }
              LC.info_IncorrectMaterial.sendMessage((Object) commandSender, new Object[0]);
              return null;
            }
            try {
              int amount = Integer.parseInt(arg);
              if (amount <= 0) break;
              cMIItemStack.setAmount(amount);
              fullStack = false;
              break;
            } catch (Throwable ignored) {
              // not a number
            }
          }
      }
    }
    Player target = cMI.getTarget(commandSender, targetName, this);
    if (target == null) {
      return null;
    }
    if (Config.CommandsClearConfirmation) {
      Long lastClear = this.map.get(target.getUniqueId());
      if (lastClear != null && lastClear + 10000L > System.currentTimeMillis()) {
        confirmedClear = true;
      }
      if (!confirmedClear && commandSender instanceof Player) {
        String typeString = "";
        for (Map.Entry<clearTypes, Boolean> entry : hashMap.entrySet()) {
          if (!typeString.isEmpty()) {
            typeString = typeString + " ";
          }
          typeString = typeString + (entry.getValue() ? "+" : "-") + entry.getKey().toString();
        }
        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(CMILC.getIM(this, "confirm", new Object[0]));
        rawMessage.addHover(LC.info_Click.getLocale(new Object[0]));
        rawMessage.addCommand(
            String.valueOf(CommandsHandler.getLabel())
                + " "
                + this.getClass().getSimpleName()
                + " "
                + target.getName()
                + " -confirmed"
                + (bl ? " -s" : "")
                + (cMIItemStack == null
                    ? ""
                    : " "
                        + cMIItemStack.getType()
                        + (!fullStack ? "-" + cMIItemStack.getAmount() : ""))
                + (typeString.isEmpty() ? "" : " " + typeString));
        rawMessage.show(commandSender);
        this.map.put(target.getUniqueId(), System.currentTimeMillis());
        return null;
      }
    }
    Snd snd = new Snd().setSender(commandSender).setTarget(target);
    CMIUser cmiUser = cMI.getPlayerManager().getUser(target);
    ItemStack[] contents = cmiUser.getPlayer().getInventory().getContents();
    int beforeAmount = 0;
    for (ItemStack content : contents) {
      if (content != null) {
        beforeAmount += content.getAmount();
      }
    }
    CMIPlayerInventory.itemCheckType[] checkTypes = null;
    if (cMIItemStack != null) {
      if (fullStack) {
        cMIItemStack.setAmount(cMIItemStack.getItemStack().getMaxStackSize() * 4 * 9);
      }
      ArrayList<CMIPlayerInventory.itemCheckType> checks =
          new ArrayList<CMIPlayerInventory.itemCheckType>();
      checks.add(CMIPlayerInventory.itemCheckType.material);
      if (cMIItemStack.getItemStack().hasItemMeta()
          && cMIItemStack.getItemStack().getItemMeta().hasDisplayName()) {
        checks.add(CMIPlayerInventory.itemCheckType.name);
      }
      if (cMIItemStack.getItemStack().hasItemMeta()
          && cMIItemStack.getItemStack().getItemMeta().hasLore()) {
        checks.add(CMIPlayerInventory.itemCheckType.lore);
      }
      if (Util.getCustomModelData(cMIItemStack.getItemStack()) != null) {
        checks.add(CMIPlayerInventory.itemCheckType.custommodel);
      }
      checkTypes = new CMIPlayerInventory.itemCheckType[checks.size()];
      checks.toArray(checkTypes);
    }
    if (exact
        && (checkTypes == null
            || checkTypes.length == 1
                && checkTypes[0] == CMIPlayerInventory.itemCheckType.material)) {
      checkTypes =
          new CMIPlayerInventory.itemCheckType[] {
            CMIPlayerInventory.itemCheckType.material,
            CMIPlayerInventory.itemCheckType.name,
            CMIPlayerInventory.itemCheckType.lore,
            CMIPlayerInventory.itemCheckType.custommodel,
            CMIPlayerInventory.itemCheckType.nbt
          };
    }
    if (hashMap.isEmpty()) {
      if (cMIItemStack != null) {
        cmiUser.getInventory().removeItemByCriteria(cMIItemStack.getItemStack(), exact, checkTypes);
      } else {
        for (int index = 0; index < target.getInventory().getContents().length; index++) {
          ItemStack item = target.getInventory().getContents()[index];
          if (!clear.isExcluded(item)) {
            target.getInventory().setItem(index, null);
          }
        }
      }
    } else {
      HashSet<Integer> slots = new HashSet<Integer>();
      block27:
      for (Map.Entry<clearTypes, Boolean> entry : hashMap.entrySet()) {
        switch (entry.getKey()) {
          case armors:
            {
              for (int index = 0; index < target.getInventory().getContents().length; index++) {
                ItemStack item = target.getInventory().getContents()[index];
                if (CMIMaterial.get(item).isArmor()) {
                  slots.add(index);
                }
              }
              continue block27;
            }
          case armorslots:
            {
              slots.addAll(CMIPlayerInventory.CMIInventorySlot.Armor.getSlots());
              break;
            }
          case quickbar:
            {
              slots.addAll(CMIPlayerInventory.CMIInventorySlot.QuickBar.getSlots());
              break;
            }
          case partinventory:
            {
              slots.addAll(CMIPlayerInventory.CMIInventorySlot.PartInventory.getSlots());
              break;
            }
          case inventory:
            {
              slots.addAll(CMIPlayerInventory.CMIInventorySlot.MainInventory.getSlots());
              break;
            }
          case mainhand:
            {
              if (cMIItemStack == null) {
                CMILib.getInstance().getReflectionManager().setItemInMainHand(target, null);
                break;
              }
              if (!CMIPlayerInventory.matches(
                  CMIItemStack.getItemInMainHand(target),
                  cMIItemStack.getItemStack(),
                  exact,
                  checkTypes)) break;
              CMIItemStack.setItemInMainHand(target, null);
              break;
            }
          case offhand:
            {
              slots.addAll(CMIPlayerInventory.CMIInventorySlot.OffHand.getSlots());
              break;
            }
          case tools:
            {
              for (int index = 0; index < target.getInventory().getContents().length; index++) {
                ItemStack item = target.getInventory().getContents()[index];
                if (!clear.isExcluded(item) && CMIMaterial.get(item).isTool()) {
                  slots.add(index);
                }
              }
              continue block27;
            }
          case weapons:
            {
              for (int index = 0; index < target.getInventory().getContents().length; index++) {
                ItemStack item = target.getInventory().getContents()[index];
                if (!clear.isExcluded(item) && CMIMaterial.get(item).isWeapon()) {
                  slots.add(index);
                }
              }
              continue block27;
            }
        }
      }
      for (Integer slot : slots) {
        if (cMIItemStack == null) {
          target.getInventory().setItem(slot.intValue(), null);
          continue;
        }
        ItemStack inSlot = target.getInventory().getContents()[slot];
        if (inSlot == null
            || !CMIPlayerInventory.matches(inSlot, cMIItemStack.getItemStack(), exact, checkTypes))
          continue;
        target.getInventory().setItem(slot.intValue(), null);
      }
    }
    cMI.save(target);
    int afterAmount = 0;
    ItemStack[] afterContents = cmiUser.getPlayer().getInventory().getContents();
    for (ItemStack content : afterContents) {
      if (content != null) {
        afterAmount += content.getAmount();
      }
    }
    int removed = beforeAmount - afterAmount;
    if (!bl) {
      CMILC.info((Object) this, commandSender, "clearFeedback", "[amount]", removed, snd);
    }
    if (!target.getName().equalsIgnoreCase(commandSender.getName()) && !bl && removed > 0) {
      CMILC.info((Object) this, (CommandSender) target, "target", "[amount]", removed, snd);
    }
    this.map.remove(target.getUniqueId());
    return true;
  }

  private static boolean isExcluded(ItemStack itemStack) {
    CMINBT cMINBT = new CMINBT(itemStack);
    for (String string : Config.CommandsClearExclude) {
      if (!string.startsWith("NBT:") || !cMINBT.hasNBT(string.substring("NBT:".length()))) continue;
      return true;
    }
    return false;
  }

  static enum clearTypes {
    quickbar,
    inventory,
    partinventory,
    weapons,
    armorslots,
    tools,
    armors,
    mainhand,
    offhand;

    public static clearTypes getByName(String string) {
      if (string.startsWith("-") || string.startsWith("+")) {
        string = string.substring(1);
      }
      for (clearTypes type : clearTypes.values()) {
        if (type.toString().equalsIgnoreCase(string)) {
          return type;
        }
      }
      return null;
    }
  }
}
