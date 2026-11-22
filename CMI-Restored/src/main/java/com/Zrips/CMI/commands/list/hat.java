package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIShulkerBox;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIArmorChangeEvent;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.ArmorTypes;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMC;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class hat implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "!actionbar!{gcp}You have successfully changed hat!");
    configReader.get(
        "targetfeedback", "{gcp}Your hat was changed by {gcs}[senderDisplayName]{gcp}!");
    configReader.get(
        "fullinv", "{gcs}[playerDisplayName] {gcp}inventory is full, can't change hat!");
    configReader.get(
        "incorrect",
        "{gcs}Incorrect item in head slot, cant replace with new one. Remove it manually");
    configReader.get("cantUse", "{gcs}Can't use this item as a hat");
  }

  @CAnnotation(
      info = "Place item like hat",
      args = "(playerName)",
      tab = "playername",
      regVar = {0, 1, 2},
      consoleVar = 666,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    boolean flag = false;
    String string = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        flag = true;
      } else {
        string = arg;
      }
    }

    Player targetPlayer = cmi.getTarget(commandSender, string, this);
    if (targetPlayer == null) {
      return null;
    }

    ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
    if (itemStack == null) {
      return false;
    }

    ItemStack helmet = targetPlayer.getInventory().getHelmet();
    if (targetPlayer.getInventory().firstEmpty() == -1 && helmet != null) {
      CMILC.info(this, player, "fullinv", targetPlayer);
      return null;
    }

    Boolean canWear = checkHatItem(player, itemStack);
    if (canWear != null && !canWear) {
      return null;
    }

    if (itemStack.getAmount() > 1) {
      itemStack.setAmount(itemStack.getAmount() - 1);
      CMIItemStack.setItemInMainHand(player, itemStack);
    } else {
      CMIItemStack.setItemInMainHand(player, null);
    }

    if (itemStack.getType() != Material.AIR || helmet != null && helmet.getType() != Material.AIR) {
      player.updateInventory();
      ItemStack helmetCopy = itemStack.clone();
      helmetCopy.setAmount(1);
      if (helmet != null) {
        targetPlayer.getInventory().addItem(new ItemStack[] {helmet});
        targetPlayer.getInventory().setHelmet(null);
      }

      targetPlayer.getInventory().setHelmet(helmetCopy);

      try {
        targetPlayer.updateInventory();
      } catch (Exception exception) {
      }

      CMIArmorChangeEvent cmiArmorChangeEvent =
          new CMIArmorChangeEvent(
              player,
              CMIArmorChangeEvent.EquipMethod.SHIFT_CLICK,
              ArmorTypes.HELMET,
              helmet,
              helmetCopy);
      Bukkit.getServer().getPluginManager().callEvent(cmiArmorChangeEvent);
      cmi.save(targetPlayer);
      Snd snd = new Snd().setSender(commandSender).setTarget(targetPlayer);
      CMILC.info(this, player, "feedback", snd);
      if (!targetPlayer.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
        CMILC.info(this, targetPlayer, "targetfeedback", snd);
      }

      return true;
    } else {
      LC.info_nothingInHand.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }

  public static Boolean checkHatItem(Player player, ItemStack itemStack) {
    if (itemStack == null) {
      return null;
    }

    boolean flag = false;
    Boolean canWear = null;
    CMIMaterial cmiMaterial = CMIMaterial.get(itemStack);
    if (!isHelmet(itemStack)) {
      flag =
          CMI.getInstance().getConfigManager().hatIgnoreLored
              && itemStack.hasItemMeta()
              && itemStack.getItemMeta().hasLore()
              && !itemStack.getItemMeta().getLore().isEmpty();
      if (!cmiMaterial.isAir()
          && CMI.getInstance().getConfigManager().hatBlockArmorItems
          && !cmiMaterial.isMonsterHead()
          && !cmiMaterial.isPlayerHead()
          && !CMI.getInstance().getConfigManager().hatWhiteList.contains(cmiMaterial)) {
        canWear = true;
      }

      if (!cmiMaterial.isAir()
          && CMI.getInstance().getConfigManager().hatBlockNoneHatEnchanted
          && !itemStack.getEnchantments().isEmpty()
          && !cmiMaterial.isMonsterHead()
          && !cmiMaterial.isPlayerHead()) {
        canWear = true;
      }

      if (!cmiMaterial.isAir()
          && canWear == null
          && !CMI.getInstance().getConfigManager().hatWhiteList.isEmpty()) {
        canWear = !CMI.getInstance().getConfigManager().hatWhiteList.contains(cmiMaterial);
      }
    }

    if ((flag || canWear != null && canWear)
        && !PermissionsManager.CMIPerm.inventoryhat_bypass.hasPermission(player)) {
      CMILC.info(hat.class, player, "cantUse");
      return false;
    }

    CMINBT cminbt = new CMINBT(itemStack);
    if (cminbt.getString("GadgetsMenu") != null) {
      CMILC.info(hat.class, player, "incorrect");
      return false;
    }

    if (flag && !PermissionsManager.CMIPerm.inventoryhat_bypass.hasPermission(player)) {
      CMILC.info(hat.class, player, "cantUse");
      return false;
    }

    if (CMIMaterial.isShulkerBox(itemStack.getType())) {
      Inventory inventory = CMIShulkerBox.getShulkerInv(itemStack);
      if (inventory != null) {
        ItemStack[] itemStacks;
        int value = (itemStacks = inventory.getContents()).length;

        for (int index = 0; index < value; index++) {
          ItemStack itemAt = itemStacks[index];
          if (itemAt != null && !itemAt.getType().equals(Material.AIR)) {
            CMILC.info(hat.class, player, "cantUse");
            return false;
          }
        }
      }
    }

    return !isHelmet(itemStack)
            && (!CMI.getInstance().getConfigManager().hatAllowMobHeads
                || !cmiMaterial.containsCriteria(CMIMC.SKULL)
                    && !cmiMaterial.containsCriteria(CMIMC.MONSTERHEAD)
                    && !cmiMaterial.isPlayerHead())
            && !PermissionsManager.CMIPerm.inventoryhat_bypass.hasPermission(player)
        ? null
        : true;
  }

  public static boolean isHelmet(ItemStack itemStack) {
    if (itemStack == null) {
      return false;
    }

    CMIMaterial cmiMaterial = CMIMaterial.get(itemStack);
    return cmiMaterial.isHelmet()
        || CMI.getInstance().getConfigManager().hatAllowMobHeads
            && (cmiMaterial.containsCriteria(CMIMC.SKULL)
                || cmiMaterial.containsCriteria(CMIMC.MONSTERHEAD)
                || cmiMaterial.isPlayerHead())
        || cmiMaterial.equals(CMIMaterial.CARVED_PUMPKIN);
  }
}
