package com.Zrips.CMI.Modules.AttachedCommands;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.SpecializedCommands.SpecializedCommandManager;
import com.Zrips.CMI.commands.list.attachcommand;
import com.Zrips.CMI.commands.list.repair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomNBTManager {
  private CMI plugin;
  public static final String cdSpliter = ":";
  public static final String leftClick = "!left!";
  public static final String rightClick = "!right!";
  public static final String consumeAction = "!consume!";
  public static final String silentAction = "!silent!";
  public static final String ignoreAction = "!ignore!";

  public CustomNBTManager(CMI cmi) {
    this.plugin = cmi;
  }

  public ItemStack updateUsesLore(ItemStack itemStack) {
    Integer integer =
        new CMINBT(itemStack).getInt(CustomNBTManager.CustomNBTType.CMILimitedUseMax.name());
    return integer == null ? itemStack : this.updateUsesLore(itemStack, integer);
  }

  public ItemStack clear(ItemStack itemStack) {
    itemStack =
        (ItemStack)
            new CMINBT(itemStack)
                .setInt(CustomNBTManager.CustomNBTType.CMILimitedUseMax.name(), null);
    itemStack =
        (ItemStack)
            new CMINBT(itemStack)
                .setInt(CustomNBTManager.CustomNBTType.CMISafeLimitedUse.name(), null);
    itemStack =
        (ItemStack)
            new CMINBT(itemStack)
                .setInt(CustomNBTManager.CustomNBTType.CMILimitedUseCurrent.name(), null);
    itemStack =
        (ItemStack)
            new CMINBT(itemStack).setInt(CustomNBTManager.CustomNBTType.Cooldown.name(), null);
    itemStack =
        (ItemStack)
            new CMINBT(itemStack).setInt(CustomNBTManager.CustomNBTType.RandomId.name(), null);
    itemStack =
        (ItemStack)
            new CMINBT(itemStack).setInt(CustomNBTManager.CustomNBTType.CooldownId.name(), null);
    itemStack =
        (ItemStack)
            new CMINBT(itemStack).setInt(CustomNBTManager.CustomNBTType.ItemCooldown.name(), null);
    if (Version.isMojangMappings()) {
      itemStack =
          (ItemStack)
              new CMINBT(itemStack)
                  .setStringList(CustomNBTManager.CustomNBTType.NBTCommands.name(), null);
    } else {
      itemStack =
          this.plugin
              .getNMS()
              .setNBTList(itemStack, CustomNBTManager.CustomNBTType.NBTCommands.name(), null);
    }

    return itemStack;
  }

  public ItemStack updateRepairLore(ItemStack itemStack, UUID uuid) {
    CMIUser cmiUser = null;
    if (uuid != null) {
      cmiUser = this.plugin.getPlayerManager().getUser(uuid);
    }

    if (!itemStack.hasItemMeta()) {
      return itemStack;
    }

    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta == null) {
      return itemStack;
    }

    List list = null;
    if (itemMeta.hasLore()) {
      list = itemMeta.getLore();
    } else {
      list = new ArrayList();
    }

    for (String string : ((Iterable<String>) new ArrayList(list))) {
      if (CMIChatColor.stripColor(string)
          .startsWith(
              CMIChatColor.stripColor(CMILC.getIM(repair.class, "itemOwner", "[owner]", "")))) {
        list.remove(string);
        break;
      }
    }

    if (cmiUser != null) {
      Snd snd = new Snd().setSender(cmiUser).setTarget(cmiUser);
      list.add(CMILC.getIM(repair.class, "itemOwner", "[owner]", cmiUser.getDisplayName(), snd));
    }

    itemMeta.setLore(list);
    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }

  public ItemStack updateUsesLore(ItemStack itemStack, int value) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    var list = itemMeta.getLore();
    if (list == null) {
      list = new ArrayList();
    }

    ArrayList arrayList = new ArrayList(list);
    String string =
        CMIChatColor.stripColor(CMILC.getIM(attachcommand.class, "uses"))
            .split("\\[amount\\]", 2)[0];

    for (String listItem : ((Iterable<String>) arrayList)) {
      if (CMIChatColor.stripColor(listItem).startsWith(string)) {
        list.remove(listItem);
      }

      if (CMIChatColor.stripColor(listItem)
          .contains("Missing locale for command.Class.info.usesLeft")) {
        list.remove(listItem);
      }

      if (CMIChatColor.stripColor(listItem)
          .contains("Missing locale for command.Class.info.uses")) {
        list.remove(listItem);
      }
    }

    if (value > 0) {
      list.add(CMILC.getIM(attachcommand.class, "uses", "[amount]", value));
    }

    itemMeta.setLore(list);
    itemStack.setItemMeta(itemMeta);

    try {
      if (value > 0) {
        itemStack =
            (ItemStack)
                new CMINBT(itemStack)
                    .setInt(
                        CustomNBTManager.CustomNBTType.RandomId.name(),
                        new Random().nextInt(Integer.MAX_VALUE));
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    return itemStack;
  }

  public String handleConsoleCommand(Player player, String string) {
    if (!string.toLowerCase().startsWith("!cc!")) {
      if (string.toLowerCase().startsWith("delay!")) {
        return string;
      }

      if (!string.contains(SpecializedCommandManager.specialisedCommand.asPlayer.getFormat())
          && !string.contains(SpecializedCommandManager.specialisedCommand.asConsole.getFormat())) {
        string = SpecializedCommandManager.specialisedCommand.asPlayer.getFormat() + " " + string;
      }

      return string;
    } else {
      string = string.substring("!cc!".length());
      string = SpecializedCommandManager.specialisedCommand.asConsole.getFormat() + " " + string;
      Snd snd = new Snd(player, player);
      string = this.plugin.getLM().updateSnd(snd, string);
      return !string.toLowerCase().startsWith("op")
              && !string.toLowerCase().startsWith("minecraft:op")
              && !string.toLowerCase().startsWith("deop")
              && !string.toLowerCase().startsWith("minecraft:deop")
          ? string
          : "";
    }
  }

  public enum CustomNBTType {
    NBTCommands,
    CMILimitedUseCurrent,
    CMILimitedUseMax,
    CMISafeLimitedUse,
    CMIRepairMan,
    CMICheque,
    @Deprecated
    MoneyCheque,
    RandomId,
    @Deprecated
    CooldownId,
    Cooldown,
    ItemCooldown;
  }
}
