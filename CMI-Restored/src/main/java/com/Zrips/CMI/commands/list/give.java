package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Attributes.AttSlot;
import net.Zrips.CMILib.Attributes.Attribute;
import net.Zrips.CMILib.Attributes.AttributeType;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class give implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}Giving {gcs}[amount] {gcp}of {gcs}[itemname] {gcp}({gcs}[customitemname]{gcp}) to"
            + " {gcs}[playerDisplayName]");
    configReader.get("maxLimit", "{gcw}You can't give more than {gcn}[max] {gcw}items in one go");
    configReader.get("notItem", "{gcw}We can't give you this, its not an item");
    configReader.get(
        "maxLimitResize", "&{gcw}You can't give more than {gcn}[max]. {gcw}Resizing to limit.");
  }

  private static boolean newMethod(String[] strings) {
    boolean flag = true;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (string.equalsIgnoreCase("e")
          || string.equalsIgnoreCase("l")
          || string.equalsIgnoreCase("n")
          || string.equalsIgnoreCase("a")) {
        return false;
      }

      if (string.equalsIgnoreCase("unbreakable")) {
        return false;
      }

      if (string.toLowerCase().contains(":speed-true-true")) {
        return false;
      }
    }

    return flag;
  }

  private static Boolean processNewMethod(final CommandSender commandSender, String[] strings) {
    boolean flag = false;
    int value = -1;
    String string = null;
    Player player = null;
    boolean unstack = false;
    CMIItemStack cmiItemStack = null;
    String itemData = null;
    int amount = 0;

    for (int argIndex = 0; argIndex < strings.length; argIndex++) {
      String arg = strings[argIndex];
      if (string == null) {
        Player onlinePlayer = CMIUser.getOnlinePlayer(arg, false);
        if (onlinePlayer != null) {
          string = onlinePlayer.getName();
          continue;
        }
      }

      if (cmiItemStack == null && (arg.equalsIgnoreCase("hand") || arg.equalsIgnoreCase("h"))) {
        CMIUser cmiUser =
            CMI.getInstance()
                .getUser(
                    commandSender,
                    strings.length <= argIndex + 1 ? null : strings[argIndex + 1],
                    give.class);
        if (cmiUser == null || cmiUser.getPlayer() == null) {
          return false;
        }

        ItemStack itemStack = cmiUser.getItemInHand();
        if (itemStack == null || CMIMaterial.isAir(itemStack.getType())) {
          return false;
        }

        cmiItemStack = CMILib.getInstance().getItemManager().getItem(itemStack);
        if (cmiItemStack == null) {
          return false;
        }
      } else if (itemData == null && CMIItemStack.deserialize(arg) != null) {
        itemData = arg;
      } else if (arg.toLowerCase().startsWith("-slot:")) {
        try {
          value = Integer.parseInt(arg.substring("-slot:".length()));
        } catch (NumberFormatException numberFormatException) {
        }
      } else {
        String rawArg;
        switch ((rawArg = strings[argIndex]).hashCode()) {
          case -277288049:
            if (rawArg.equals("unstack")) {
              unstack = true;
              continue;
            }
            break;
          case 1510:
            if (rawArg.equals("-s")) {
              flag = true;
              continue;
            }
        }

        if (arg.equalsIgnoreCase("@p") && commandSender instanceof Player) {
          player = (Player) commandSender;
        } else {
          try {
            amount = Integer.parseInt(arg);
          } catch (Throwable throwable) {
            if ((!arg.startsWith("{") || !arg.startsWith("[")) && string == null) {
              string = arg;
            }
          }
        }
      }
    }

    if (itemData == null && cmiItemStack == null) {
      CMILC.info(give.class, commandSender, "notItem");
      return null;
    }

    if (player == null) {
      player = CMI.getInstance().getTarget(commandSender, string, give.class);
      if (player == null) {
        return false;
      }
    }

    final Player targetPlayer = player;
    final Integer integer = value;
    final Boolean silent = flag;
    final Boolean unstackValue = unstack;
    CMIAsyncHead cmiAsyncHead =
        new CMIAsyncHead() {
          public void afterAsyncUpdate(ItemStack loadedItem) {
            CMIScheduler.runTask(
                CMI.getInstance(),
                () ->
                    give.processNewItem(
                        new CMIItemStack(loadedItem),
                        commandSender,
                        targetPlayer,
                        integer,
                        silent,
                        unstackValue));
          }
        };
    if (cmiItemStack == null && itemData != null) {
      cmiItemStack = CMIItemStack.deserialize(commandSender, itemData, cmiAsyncHead);
    }

    if (cmiItemStack == null) {
      return false;
    }

    if (amount > 0) {
      cmiItemStack.setAmount(amount);
    }

    return cmiAsyncHead.isAsyncHead()
        ? null
        : processNewItem(cmiItemStack, commandSender, player, value, flag, unstack);
  }

  private static Boolean processNewItem(
      CMIItemStack cmiItemStack,
      CommandSender commandSender,
      Player player,
      int value,
      boolean flag,
      boolean unstack) {
    if (cmiItemStack != null && !cmiItemStack.getType().equals(Material.AIR)) {
      if (commandSender instanceof Player) {
        PermissionInfo permissionInfo =
            CMI.getInstance()
                .getPermissionsManager()
                .getPermissionInfo(
                    (Player) commandSender, PermissionsManager.CMIPerm.command_give_max_$1, 5000L);
        if (permissionInfo != null && permissionInfo.getMaxValue() != null) {
          int maxAmount = permissionInfo.getMaxValue().intValue();
          if (cmiItemStack.getAmount() > maxAmount) {
            CMILC.info(give.class, commandSender, "maxLimit", "[max]", maxAmount);
            return null;
          }
        }
      }

      if (cmiItemStack.getAmount() > 36 * cmiItemStack.getType().getMaxStackSize()) {
        CMILC.info(
            give.class.getSimpleName(),
            commandSender,
            "maxLimitResize",
            "[max]",
            36 * cmiItemStack.getType().getMaxStackSize());
        cmiItemStack.setAmount(36 * cmiItemStack.getType().getMaxStackSize());
      }

      CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);

      try {
        if (!cmiItemStack.getType().isItem()) {
          CMILC.info(give.class, commandSender, "notItem");
          return null;
        }
      } catch (Throwable throwable) {
      }

      int maxStack = cmiItemStack.getItemStack().getMaxStackSize();
      if (Config.CommandsGiveEnchantLimit
          && enchant.containsInvalidEnchants(cmiItemStack.getItemStack(), commandSender)) {
        CMILC.info(enchant.class, commandSender, "enchantlimit");
        return null;
      }

      if (unstack && maxStack < cmiItemStack.getAmount()) {
        int fullStacks = cmiItemStack.getAmount() / maxStack;
        int remainder = cmiItemStack.getAmount() - fullStacks * maxStack;
        ItemStack itemStack = cmiItemStack.getItemStack().clone();
        itemStack.setAmount(maxStack);

        for (int stackIndex = 0; stackIndex < fullStacks; stackIndex++) {
          cmiUser.getInventory().addItem(itemStack, value);
        }

        if (remainder > 0 && remainder < maxStack) {
          itemStack = itemStack.clone();
          itemStack.setAmount(remainder);
          cmiUser.getInventory().addItem(itemStack, value);
        }
      } else {
        cmiUser.getInventory().addItem(cmiItemStack.getItemStack(), value);
      }

      CMI.getInstance().save(player);
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      if (!flag) {
        CMILC.info(
            give.class.getSimpleName(),
            commandSender,
            "feedback",
            "[itemname]",
            cmiItemStack.getRealName(),
            "[customitemname]",
            cmiItemStack.getRealName().equalsIgnoreCase(cmiItemStack.getDisplayName())
                ? ""
                : cmiItemStack.getDisplayName(),
            "[amount]",
            cmiItemStack.getAmount(),
            snd);
      }

      return true;
    } else {
      return null;
    }
  }

  @CAnnotation(
      info = "Give item to player",
      args = "(playerName) [itemdata/hand] (playerName) (-slot:[number]) (unstack) (-s)",
      tab = {"playername%%itemname", "itemname%%hand", "1%%playerName"},
      explanation = {
        "Example: /cmi give diamondsword;{#Gray}My_Uber_Sword;&2Goblin_Slayer!\n"
            + "{#pink}With_Love!;sharpness:3,durability:3;hideenchants",
        "<T>More information at"
            + " www.zrips.net/cmi/commands/icwol/</T><URL>https://www.zrips.net/cmi/commands/icwol/</URL>"
      },
      regVar = -100,
      consoleVar = -100,
      others = true)
  @Override
  public Boolean perform(CMI cmi, final CommandSender commandSender, String[] strings) {
    if (newMethod(strings)) {
      return processNewMethod(commandSender, strings);
    }

    CMIItemStack cmiItemStack = null;
    boolean flag = false;
    Boolean unbreakable = null;
    String string = null;
    int value = -1;
    Integer integer = null;
    Integer data = null;
    final HashMap<Enchantment, Integer> hashMap = new HashMap();
    final ArrayList<Attribute> arrayList = new ArrayList();
    String displayName = null;
    final ArrayList<String> loreLines = new ArrayList();
    String targetName = null;
    String itemName = null;
    Player player = null;
    boolean unstack = false;

    for (int dateIndex = 0; dateIndex < strings.length; dateIndex++) {
      Pattern pattern = Pattern.compile("\\%date/(.*?)\\%");
      Matcher matcher = pattern.matcher(strings[dateIndex]);

      while (matcher.find()) {
        try {
          String formattedDate =
              new SimpleDateFormat(matcher.group(1)).format(Calendar.getInstance().getTime());
          strings[dateIndex] =
              strings[dateIndex].replace("%date/" + matcher.group(1) + "%", formattedDate);
        } catch (Exception exception) {
        }
      }
    }

    for (int randIndex = 0; randIndex < strings.length; randIndex++) {
      Pattern randPattern = Pattern.compile("\\%rand/(.*?)\\%");
      Matcher randMatcher = randPattern.matcher(strings[randIndex]);

      while (randMatcher.find()) {
        if (randMatcher.group(1).contains("-")) {
          Random random = new Random(System.nanoTime());

          try {
            String randMin = randMatcher.group(1).split("-")[0];
            String randMax = randMatcher.group(1).split("-")[1];
            double number = 1.0;
            if (randMin.contains(".")) {
              number = 10 ^ randMin.split("\\.")[1].length();
            }

            int randMinScaled = (int) (Double.parseDouble(randMin) * number);
            int randMaxScaled = (int) (Double.parseDouble(randMax) * number) + 1;
            if (randMinScaled < randMaxScaled) {
              double randValue =
                  (random.nextInt(randMaxScaled - randMinScaled) + randMinScaled) / number;
              DecimalFormat decimalFormat = new DecimalFormat("#0.0");
              if (randValue - (int) randValue > 0.0) {
                strings[randIndex] =
                    strings[randIndex].replace(
                        "%rand/" + randMatcher.group(1) + "%", decimalFormat.format(randValue));
              } else {
                strings[randIndex] =
                    strings[randIndex].replace(
                        "%rand/" + randMatcher.group(1) + "%", String.valueOf((int) randValue));
              }
            }
          } catch (Exception exception2) {
          }
        }
      }
    }

    String joinedArgs = CMIArray.toString(strings);
    String translatedArgs = CMIChatColor.translate(joinedArgs);
    if (translatedArgs.contains("{") && translatedArgs.contains("}")) {
      Pattern bracketPattern = Pattern.compile("(\\{).+(\\})");
      Matcher bracketMatcher = bracketPattern.matcher(joinedArgs);
      if (bracketMatcher.find()) {
        String bracketText = bracketMatcher.group();
        if (!bracketText.startsWith("{#")) {
          joinedArgs = joinedArgs.replace(bracketMatcher.group(), "");
          string = bracketText;
        }
      }

      joinedArgs = joinedArgs.replace("  ", " ");
      strings = joinedArgs.split(" ");
    }

    int argIndex = 0;

    while (true) {
      if (argIndex >= strings.length) {
        if (player == null) {
          player = cmi.getTarget(commandSender, targetName, this);
        }

        if (player == null) {
          return null;
        }

        final Player finalPlayer = player;
        final String finalDisplayName = displayName;
        final String finalBracketText = string;
        final Integer finalSlot = value;
        final Boolean finalUnbreakable = unbreakable;
        final Integer finalAmount = integer;
        final Integer finalData = data;
        final Boolean finalSilent = flag;
        final Boolean finalUnstack = unstack;
        CMIAsyncHead cmiAsyncHead =
            new CMIAsyncHead() {
              public void afterAsyncUpdate(ItemStack itemStack) {
                CMIScheduler.runTask(
                    () ->
                        give.processItem(
                            new CMIItemStack(itemStack),
                            commandSender,
                            finalPlayer,
                            finalDisplayName,
                            loreLines,
                            finalBracketText,
                            finalSlot,
                            finalUnbreakable,
                            finalAmount,
                            finalData,
                            finalSilent,
                            hashMap,
                            arrayList,
                            finalUnstack));
              }
            };
        cmiItemStack = CMILib.getInstance().getItemManager().getItem(itemName, cmiAsyncHead);
        if (cmiItemStack == null) {
          return false;
        }

        if (cmiAsyncHead.isAsyncHead()) {
          return null;
        }

        return processItem(
            cmiItemStack,
            commandSender,
            player,
            displayName,
            loreLines,
            string,
            value,
            unbreakable,
            integer,
            data,
            flag,
            hashMap,
            arrayList,
            unstack);
      }

      label369:
      {
        if (targetName == null) {
          Player foundPlayer = CMIUser.getPlayer(strings[argIndex]);
          if (foundPlayer != null) {
            targetName = foundPlayer.getName();
            break label369;
          }
        }

        label313:
        if (itemName != null
            || !strings[argIndex].equalsIgnoreCase("hand")
                && !strings[argIndex].equalsIgnoreCase("h")) {
          if (itemName == null) {
            CMIItemStack itemFromArg =
                CMILib.getInstance()
                    .getItemManager()
                    .getItem(strings[argIndex], new CMIAsyncHead());
            if (itemFromArg != null) {
              itemName = strings[argIndex];
              break label313;
            }
          }

          if (itemName != null && integer == null) {
            try {
              integer = Integer.parseInt(strings[argIndex]);
              break label313;
            } catch (NumberFormatException numberFormatException) {
            }
          }

          if (itemName != null && integer != null) {
            try {
              data = Integer.parseInt(strings[argIndex]);
              break label313;
            } catch (NumberFormatException numberFormatException2) {
            }
          }

          if (strings[argIndex].toLowerCase().startsWith("-slot:")) {
            try {
              value = Integer.parseInt(strings[argIndex].substring("-slot:".length()));
            } catch (NumberFormatException numberFormatException3) {
            }
          } else {
            label353:
            {
              String switchArg;
              switch ((switchArg = strings[argIndex]).hashCode()) {
                case -1829133216:
                  if (switchArg.equals("unbreakable")) {
                    unbreakable = true;
                    break label353;
                  }
                  break;
                case -277288049:
                  if (switchArg.equals("unstack")) {
                    unstack = true;
                    break label353;
                  }
                  break;
                case 97:
                  if (switchArg.equals("a") && argIndex + 1 < strings.length) {
                    ArrayList<String> attributeArgs = new ArrayList();
                    if (!strings[argIndex + 1].contains(",")) {
                      attributeArgs.add(strings[argIndex + 1]);
                    } else {
                      attributeArgs.addAll(Arrays.asList(strings[argIndex + 1].split(",")));
                    }

                    for (String attributeArg : attributeArgs) {
                      if (attributeArg.contains(":")) {
                        String[] attributeParts = attributeArg.split(":");
                        AttributeType attributeType = null;
                        AttSlot attSlot = AttSlot.Main_hand;
                        double attributeValue = -1.0;
                        String[] partsCopy = attributeParts;
                        int partCount = attributeParts.length;

                        for (int partIndex = 0; partIndex < partCount; partIndex++) {
                          String part = partsCopy[partIndex];
                          if (attributeValue < 0.0) {
                            try {
                              attributeValue = Double.parseDouble(part);
                              continue;
                            } catch (NumberFormatException numberFormatException4) {
                            }
                          }

                          if (attributeType == null) {
                            attributeType = AttributeType.get(part);
                            if (attributeType != null) {
                              continue;
                            }
                          }

                          AttSlot parsedSlot = AttSlot.get(part);
                          if (parsedSlot != null) {
                            attSlot = parsedSlot;
                          }
                        }

                        if (attributeType != null && attributeValue != -1.0) {
                          arrayList.add(new Attribute(attributeType, attSlot, attributeValue));
                        }
                      }
                    }

                    argIndex++;
                    break label353;
                  }
                  break;
                case 101:
                  if (switchArg.equals("e") && argIndex + 1 < strings.length) {
                    ArrayList<String> enchantArgs = new ArrayList();
                    if (!strings[argIndex + 1].contains(",")) {
                      enchantArgs.add(strings[argIndex + 1]);
                    } else {
                      String[] enchantParts;
                      int enchantPartCount =
                          (enchantParts = strings[argIndex + 1].split(",")).length;

                      for (int enchantIndex = 0; enchantIndex < enchantPartCount; enchantIndex++) {
                        String enchantPart = enchantParts[enchantIndex];
                        enchantArgs.add(enchantPart);
                      }
                    }

                    for (String enchantArg : enchantArgs) {
                      if (enchantArg.contains(":")) {
                        int enchantLevel = 0;

                        try {
                          enchantLevel = Integer.parseInt(enchantArg.split(":")[1]);
                        } catch (NumberFormatException numberFormatException5) {
                          continue;
                        }

                        Enchantment enchantment = CMIEnchantment.get(enchantArg.split(":")[0]);
                        if (enchantment != null) {
                          hashMap.put(enchantment, enchantLevel);
                        }
                      }
                    }

                    argIndex++;
                    break label353;
                  }
                  break;
                case 108:
                  if (switchArg.equals("l") && argIndex + 1 < strings.length) {
                    if (!strings[argIndex + 1].contains("/n")) {
                      loreLines.add(CMIChatColor.translate(strings[argIndex + 1]));
                    } else {
                      String[] loreParts;
                      int loreCount = (loreParts = strings[argIndex + 1].split("/n")).length;

                      for (int loreIndex = 0; loreIndex < loreCount; loreIndex++) {
                        String lorePart = loreParts[loreIndex];
                        loreLines.add(CMIChatColor.translate(lorePart));
                      }
                    }

                    argIndex++;
                    break label353;
                  }
                  break;
                case 110:
                  if (switchArg.equals("n") && argIndex + 1 < strings.length) {
                    displayName = CMIChatColor.translate(strings[argIndex + 1]);
                    argIndex++;
                    break label353;
                  }
                  break;
                case 1510:
                  if (switchArg.equals("-s")) {
                    flag = true;
                    break label353;
                  }
              }

              if (strings[argIndex].equalsIgnoreCase("@p") && commandSender instanceof Player) {
                player = (Player) commandSender;
              } else if (!strings[argIndex].startsWith("{") && targetName == null) {
                targetName = strings[argIndex];
              }
            }
          }
        } else {
          CMIUser cmiUser =
              cmi.getUser(
                  commandSender,
                  strings.length <= argIndex + 1 ? null : strings[argIndex + 1],
                  this);
          if (cmiUser == null || cmiUser.getPlayer() == null) {
            break;
          }

          ItemStack handItem = cmiUser.getItemInHand();
          if (handItem == null || CMIMaterial.isAir(handItem.getType())) {
            break;
          }

          cmiItemStack = CMILib.getInstance().getItemManager().getItem(handItem);
          if (cmiItemStack == null) {
            break;
          }
        }
      }

      argIndex++;
    }

    return false;
  }

  private static Boolean processItem(
      CMIItemStack cmiItemStack,
      CommandSender commandSender,
      Player player,
      String string,
      List<String> list,
      String tag,
      int value,
      Boolean unbreakable,
      Integer integer,
      Integer data,
      boolean flag,
      HashMap<Enchantment, Integer> hashMap,
      List<Attribute> attributes,
      boolean unstack) {
    if (cmiItemStack != null && !cmiItemStack.getType().equals(Material.AIR)) {
      if (integer != null) {
        cmiItemStack.setAmount(integer);
      }

      if (data != null) {
        cmiItemStack.setData(data.shortValue());
      }

      if (commandSender instanceof Player) {
        PermissionInfo permissionInfo =
            CMI.getInstance()
                .getPermissionsManager()
                .getPermissionInfo(
                    (Player) commandSender, PermissionsManager.CMIPerm.command_give_max_$1, 5000L);
        if (permissionInfo != null && permissionInfo.getMaxValue() != null) {
          int maxAmount = permissionInfo.getMaxValue().intValue();
          if (cmiItemStack.getAmount() > maxAmount) {
            CMILC.info(give.class, commandSender, "maxLimit", "[max]", maxAmount);
            return null;
          }
        }
      }

      if (cmiItemStack.getAmount() > 36 * cmiItemStack.getType().getMaxStackSize()) {
        CMILC.info(
            give.class.getSimpleName(),
            commandSender,
            "maxLimitResize",
            "[max]",
            36 * cmiItemStack.getType().getMaxStackSize());
        cmiItemStack.setAmount(36 * cmiItemStack.getType().getMaxStackSize());
      }

      string = CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, string);
      if (string != null) {
        string = string.replace("_", " ");
      }

      list = CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, list);
      if (list != null && !list.isEmpty()) {
        for (int loreIndex = 0; loreIndex < list.size(); loreIndex++) {
          list.set(
              loreIndex, CMIChatColor.translate(((String) list.get(loreIndex)).replace("_", " ")));
        }
      }

      if (tag != null) {
        cmiItemStack.setTag(CMIChatColor.translate(tag));
      }

      if (string != null) {
        cmiItemStack.setDisplayName(string);
      }

      if (list != null && !list.isEmpty()) {
        cmiItemStack.setLore(list);
      }

      if (!hashMap.isEmpty()) {
        cmiItemStack.addEnchant(hashMap);
      }

      if (!attributes.isEmpty()) {
        cmiItemStack.addAttributes(attributes);
      }

      if (unbreakable != null) {
        cmiItemStack.setUnbreakable(unbreakable);
      }

      CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);

      try {
        if (!cmiItemStack.getType().isItem()) {
          CMILC.info(give.class, commandSender, "notItem");
          return null;
        }
      } catch (Throwable throwable) {
      }

      int maxStack = cmiItemStack.getItemStack().getMaxStackSize();
      if (unstack && maxStack < cmiItemStack.getAmount()) {
        int fullStacks = cmiItemStack.getAmount() / maxStack;
        int remainder = cmiItemStack.getAmount() - fullStacks * maxStack;
        ItemStack itemStack = cmiItemStack.getItemStack().clone();
        itemStack.setAmount(maxStack);

        for (int stackIndex = 0; stackIndex < fullStacks; stackIndex++) {
          cmiUser.getInventory().addItem(itemStack, value);
        }

        if (remainder > 0 && remainder < maxStack) {
          itemStack = itemStack.clone();
          itemStack.setAmount(remainder);
          cmiUser.getInventory().addItem(itemStack, value);
        }
      } else {
        cmiUser.getInventory().addItem(cmiItemStack.getItemStack(), value);
      }

      CMI.getInstance().save(player);
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      if (!flag) {
        CMILC.info(
            give.class.getSimpleName(),
            commandSender,
            "feedback",
            "[itemname]",
            cmiItemStack.getRealName(),
            "[customitemname]",
            cmiItemStack.getRealName().equalsIgnoreCase(cmiItemStack.getDisplayName())
                ? ""
                : cmiItemStack.getDisplayName(),
            "[amount]",
            cmiItemStack.getAmount(),
            snd);
      }

      return true;
    } else {
      return null;
    }
  }
}
