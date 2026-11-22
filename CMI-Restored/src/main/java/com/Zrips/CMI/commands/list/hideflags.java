package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.HashSet;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class hideflags implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback", "{gcp}You have added new flag to item for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "cleared", "{gcp}Cleared {gcs}ALL {gcp}flags from item for {gcs}[playerDisplayName]{gcp}.");
    configReader.get("noitem", "&cNot holding any item in hand.");
    configReader.get("inccorectId", "{gcp}Incorrect flag name.");
  }

  @CAnnotation(
      info = "Hides item flags",
      args = "(playerName) [flagName/clear] (flagName)",
      tab = {"playerName%%itemFlag%%clear", "itemFlag%%clear"},
      regVar = {1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    ArrayList<ItemFlag> arrayList = new ArrayList();
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("clear")) {
        flag = true;
      } else {
        ItemFlag itemFlag = getitemFlag(arg);
        if (itemFlag != null) {
          arrayList.add(itemFlag);
        } else {
          string = arg;
        }
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    if (arrayList.isEmpty() && !flag) {
      CMILC.info(this, commandSender, "inccorectId");
      return null;
    }

    ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
    if (itemStack != null && itemStack.getType() != Material.AIR) {
      byte b = 0;
      if (!flag) {
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          ItemMeta itemMeta = itemStack.getItemMeta();

          for (ItemFlag flagToAdd : arrayList) {
            itemMeta.addItemFlags(new ItemFlag[] {flagToAdd});
          }

          itemStack.setItemMeta(itemMeta);
        } else {
          for (ItemFlag activeFlag : arrayList) {
            b |= getBitModifier(activeFlag);
          }

          for (ItemFlag existingFlag : itemStack.getItemMeta().getItemFlags()) {
            b |= getBitModifier(existingFlag);
          }

          CMIItemStack.setItemInMainHand(player, CMINBT.HideFlag(itemStack, b));
        }
      } else {
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          ItemMeta clearedMeta = itemStack.getItemMeta();

          for (ItemFlag flagToRemove : new HashSet<ItemFlag>(clearedMeta.getItemFlags())) {
            clearedMeta.removeItemFlags(new ItemFlag[] {flagToRemove});
          }

          itemStack.setItemMeta(clearedMeta);
        }

        CMIItemStack.setItemInMainHand(player, itemStack);
      }

      cmi.save(player);
      if (player.isOnline()) {
        player.updateInventory();
      }

      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      if (!flag) {
        CMILC.info(this, commandSender, "feedback", snd);
      } else {
        CMILC.info(this, commandSender, "cleared", snd);
      }

      return true;
    } else {
      CMILC.info(this, commandSender, "noitem");
      return null;
    }
  }

  private static byte getBitModifier(ItemFlag itemFlag) {
    return (byte) (1 << itemFlag.ordinal());
  }

  private static ItemFlag getitemFlag(String string) {
    ItemFlag[] itemFlags;
    int value = (itemFlags = ItemFlag.values()).length;

    for (int index = 0; index < value; index++) {
      ItemFlag itemFlag = itemFlags[index];
      if (itemFlag.name().equalsIgnoreCase(string)) {
        return itemFlag;
      }
    }

    return null;
  }
}
