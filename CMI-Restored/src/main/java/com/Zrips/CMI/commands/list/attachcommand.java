package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.AttachedCommands.CustomNBTManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.SpecializedCommands.SpecializedCommandManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.CMIEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class attachcommand implements Cmd {
  public static final String seperator = ";;";
  static Pattern limitedPattern = Pattern.compile("(!)(limiteduse:)(\\d+)(!)");
  static Pattern safeLimitedPattern = Pattern.compile("(!)(safelimiteduse:)(\\d+)(!)");
  static Pattern cooldownPattern = Pattern.compile("(!)(cooldown:)(\\d+)(!)");

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", " {gcp}[id]. {gcp}[cmd]");
    configReader.get("uses", "{gcp}Item usages: {gcp}[amount]");
    configReader.get("usesLeft", "!actionbar!{gcp}Item uses left: {gcp}[itemUses]");
    configReader.get("cooldown", "{gcp}Item is on cooldown. Wait {gcp}[time]");
  }

  @CAnnotation(
      priority = 125,
      info = "Attaches command to item",
      args = "[command/-clear]",
      multiTab = "-clear,[attachedCommand]",
      explanation = {
        "Separate commands with ;;",
        "You can define to run command on left or right mouse click separately with",
        "!left! to perform command on left mouse click",
        "!right! to perform command on right click",
        "!limiteduse:[amount]! defines how many times you can use this item",
        "!safelimiteduse:[amount]! will not remove item after all usages are used",
        "!cc! performs command from console, requires cmi.command.attachcommand.cc permission",
        "!cooldownn:[amount]! adds cooldown on item usage.",
        "[interactedPlayer] inserts name of player you interacted while holding item",
        "Global variables working in addition with [itemUses]"
      },
      regVar = -666,
      consoleVar = 666,
      alias = true,
      customAlias = "!powertool",
      modules = "attachedcommands",
      others = false)
  @Override
  public Boolean perform(CMI cmi, final CommandSender commandSender, String[] strings) {
    final Player player = (Player) commandSender;
    String string = "";
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (!string.isEmpty()) {
        string = string + " ";
      }

      string = string + arg;
    }

    ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
    if (itemStack == null || itemStack.getType() == Material.AIR) {
      LC.info_nothingInHand.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (strings.length == 0) {
      List<String> arrayList =
          new CMINBT(itemStack).getList(CustomNBTManager.CustomNBTType.NBTCommands.name());
      if (arrayList == null) {
        arrayList = new ArrayList();
      } else {
        arrayList = new ArrayList(arrayList);
      }

      boolean flag = false;

      for (int index = 0; index < arrayList.size(); index++) {
        String decoded = CMIEncoder.decode((String) arrayList.get(index));
        if (decoded == null) {
          decoded = (String) arrayList.get(index);
          flag = true;
        }

        arrayList.set(index, decoded);
      }

      if (flag) {
        List list = CMIEncoder.encode(new ArrayList<>(arrayList));
        if (!list.isEmpty()) {
          CMIItemStack.setItemInMainHand(
              player,
              cmi.getNMS()
                  .setNBTList(itemStack, CustomNBTManager.CustomNBTType.NBTCommands.name(), list));
        }

        player.updateInventory();
        itemStack = CMIItemStack.getItemInMainHand(player);
      }

      final ArrayList commandList = new ArrayList(arrayList);
      CMINBT cminbt = new CMINBT(itemStack);
      Integer integer = cminbt.getInt(CustomNBTManager.CustomNBTType.CMILimitedUseMax.name());
      Integer limit = cminbt.getInt(CustomNBTManager.CustomNBTType.CMISafeLimitedUse.name());
      if (integer != null && integer > 0) {
        commandList.add(0, "!limiteduse:" + integer + "!");
      }

      if (limit != null && limit > 0) {
        commandList.add(0, "!safelimiteduse:" + limit + "!");
      }

      var cooldowns = this.processCooldowns(itemStack);
      if (!cooldowns.isEmpty()) {
        for (String cooldownLine : cooldowns) {
          commandList.add(0, cooldownLine);
        }
      }

      PageInfo pageInfo = new PageInfo(10, commandList.size(), 1);
      LC.info_Spliter.sendMessage(commandSender, new Object[0]);
      ChatMessageListEdit chatMessageListEdit =
          new ChatMessageListEdit(player, commandList, ChatEditType.String) {
            public void onUpdate() {
              String lore = "";

              for (String commandText : ((Iterable<String>) commandList)) {
                if (!lore.isEmpty()) {
                  lore = lore + ";;";
                }

                lore = lore + commandText;
              }

              ItemStack heldItem = CMIItemStack.getItemInMainHand(player);
              if (heldItem != null && heldItem.getType() != Material.AIR) {
                heldItem = CMI.getInstance().getCustomNBTManager().clear(heldItem);
                attachcommand.process(commandSender, lore, heldItem, player);
              } else {
                LC.info_nothingInHand.sendMessage(commandSender, new Object[0]);
              }
            }
          };
      chatMessageListEdit.print();
      pageInfo.autoPagination(
          commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName());
      return true;
    } else {
      if (!string.equalsIgnoreCase("-c") && !string.equalsIgnoreCase("-clear")) {
        return process(commandSender, string, itemStack, player);
      }

      itemStack = CMI.getInstance().getCustomNBTManager().clear(itemStack);
      CMILib.getInstance().getReflectionManager().setItemInMainHand(player, itemStack);
      player.updateInventory();
      return true;
    }
  }

  private static Boolean process(
      CommandSender commandSender, String string, ItemStack itemStack, Player player) {
    ArrayList arrayList = new ArrayList();
    if (!string.isEmpty()) {
      arrayList.addAll(Arrays.asList(string.split(";;")));
    }

    ArrayList commandList = new ArrayList(arrayList);

    for (String line : ((Iterable<String>) commandList)) {
      if ((line.toLowerCase().startsWith("!cc!")
              || line.toLowerCase()
                  .contains(
                      SpecializedCommandManager.specialisedCommand
                          .asConsole
                          .getFormat()
                          .toLowerCase())
              || line.toLowerCase()
                  .contains(
                      SpecializedCommandManager.specialisedCommand
                          .asFakeOp
                          .getFormat()
                          .toLowerCase()))
          && !PermissionsManager.CMIPerm.command_attachcommand_cc.hasPermission(
              commandSender, true)) {
        return null;
      }

      if (line.toLowerCase().contains("!silent!")
          && !PermissionsManager.CMIPerm.command_attachcommand_silent.hasPermission(
              commandSender, true)) {
        return null;
      }
    }

    for (String entry : ((Iterable<String>) commandList)) {
      Matcher matcher = limitedPattern.matcher(entry);
      Matcher safeLimitMatcher = safeLimitedPattern.matcher(entry);
      boolean flag = safeLimitMatcher.find();
      if (matcher.find() || flag) {
        String safeLimitValue = null;
        if (!flag) {
          safeLimitValue = matcher.group(3);
        } else {
          safeLimitValue = safeLimitMatcher.group(3);
        }

        arrayList.remove(entry);
        int value = 1;

        try {
          value = Integer.parseInt(safeLimitValue);
        } catch (Exception exception) {
          exception.printStackTrace();
        }

        if (value < 1) {
          value = 1;
        }

        itemStack = CMI.getInstance().getCustomNBTManager().updateUsesLore(itemStack, value);
        CMILib.getInstance()
            .getReflectionManager()
            .setItemInMainHand(
                player,
                (ItemStack)
                    new CMINBT(itemStack)
                        .setInt(CustomNBTManager.CustomNBTType.CMILimitedUseMax.name(), value));
        itemStack = CMILib.getInstance().getReflectionManager().getItemInMainHand(player);
        if (flag) {
          CMILib.getInstance()
              .getReflectionManager()
              .setItemInMainHand(
                  player,
                  (ItemStack)
                      new CMINBT(itemStack)
                          .setInt(CustomNBTManager.CustomNBTType.CMISafeLimitedUse.name(), 1));
          itemStack = CMILib.getInstance().getReflectionManager().getItemInMainHand(player);
        }

        CMILib.getInstance()
            .getReflectionManager()
            .setItemInMainHand(
                player,
                (ItemStack)
                    new CMINBT(itemStack)
                        .setInt(CustomNBTManager.CustomNBTType.CMILimitedUseCurrent.name(), value));
        itemStack = CMILib.getInstance().getReflectionManager().getItemInMainHand(player);
      }
    }

    ArrayList<String> cooldownLines = new ArrayList();

    for (String cooldownEntry : ((Iterable<String>) commandList)) {
      Matcher cooldownMatcher = cooldownPattern.matcher(cooldownEntry);
      if (cooldownMatcher.find()) {
        String cooldownValue = cooldownMatcher.group(3);
        arrayList.remove(cooldownEntry);
        int counter = 1;

        try {
          counter =
              Integer.parseInt(
                  cooldownValue.contains(":") ? cooldownValue.split(":")[0] : cooldownValue);
        } catch (Exception exception2) {
          exception2.printStackTrace();
        }

        if (counter < 1) {
          counter = 1;
        }

        String suffix = "";
        if (cooldownEntry.toLowerCase().startsWith("!left!")) {
          suffix = "!left!";
        } else if (cooldownEntry.toLowerCase().startsWith("!right!")) {
          suffix = "!right!";
        }

        cooldownLines.add(
            counter
                + ":"
                + new Random().nextInt(Integer.MAX_VALUE)
                + (suffix.isEmpty() ? "" : ":" + suffix));
      }
    }

    if (!cooldownLines.isEmpty()) {
      itemStack =
          CMI.getInstance()
              .getNMS()
              .setNBTList(
                  itemStack, CustomNBTManager.CustomNBTType.ItemCooldown.name(), cooldownLines);
      CMILib.getInstance().getReflectionManager().setItemInMainHand(player, itemStack);
      itemStack = CMILib.getInstance().getReflectionManager().getItemInMainHand(player);
    }

    if (!arrayList.isEmpty()) {
      List list = CMIEncoder.encode(arrayList);
      itemStack =
          CMI.getInstance()
              .getNMS()
              .setNBTList(itemStack, CustomNBTManager.CustomNBTType.NBTCommands.name(), list);
    }

    CMIItemStack.setItemInMainHand(player, itemStack);
    player.updateInventory();
    return true;
  }

  private List<String> processCooldowns(ItemStack itemStack) {
    var list = new CMINBT(itemStack).getList(CustomNBTManager.CustomNBTType.ItemCooldown.name());
    ArrayList<String> arrayList = new ArrayList();
    if (list == null) {
      return arrayList;
    }

    for (String string : list) {
      boolean flag = false;
      boolean hasLimit = false;
      if (string.contains(":")) {
        String[] strings = string.split(":");
        if (strings.length > 2) {
          if (strings[2].equalsIgnoreCase("!left!")) {
            flag = true;
          }

          if (strings[2].equalsIgnoreCase("!right!")) {
            hasLimit = true;
          }
        }

        try {
          arrayList.add(
              (flag ? "!left!" : (hasLimit ? "!right!" : ""))
                  + "!cooldown:"
                  + Integer.parseInt(strings[0])
                  + "!");
        } catch (Exception exception) {
        }
      }
    }

    return arrayList;
  }
}
