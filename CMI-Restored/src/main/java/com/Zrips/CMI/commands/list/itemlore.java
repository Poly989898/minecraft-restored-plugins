package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class itemlore implements Cmd {
  public static final String indicator = "ModifiedLore";

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have changed lore line ({gcs}[line]{gcp}) to '&r{gcs}[loreLine]{gcp}' for"
            + " {gcs}[playerDisplayName]{gcp}.");
    configReader.get("noitem", "{gcd}Not holding any item in hand.");
    configReader.get(
        "removed",
        "{gcp}Item lore line {gcs}[line] {gcp}was removed for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "inserted",
        "{gcp}Item lore line {gcs}[line] {gcp}was inserted for {gcs}[playerDisplayName]{gcp}.");
    configReader.get("maxLines", "{gcp}There is only {gcs}[lines] {gcp}lines of lore.");
    configReader.get("blocked", "{gcp}This item is blocked from re-loring.");
    configReader.get("nomodification", "{gcw}You can't modify existing lore lines");
  }

  @CAnnotation(
      info = "Change items lore",
      args = "(-p:[playerName]) [linenumber/*] [remove/insert/ new lore line]",
      multiTab =
          "-p:,[loreLine],*,[currentItemLore] [loreLine],*,![currentItemLore] ![currentItemLore]",
      regVar = -100,
      consoleVar = -100,
      alias = true,
      customAlias = "!ilore",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    int value = -1;
    String string = null;
    boolean flag = false;
    int loreIndex = 0;
    int lineCount = 0;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = argsCopy[argIndex];
      loreIndex++;
      if (arg.equalsIgnoreCase("*")) {
        flag = true;
        lineCount++;
      } else {
        if (value == -1) {
          try {
            value = Integer.parseInt(arg);
            lineCount++;
            continue;
          } catch (NumberFormatException numberFormatException) {
          }
        }

        if (arg.startsWith("-p:") && value == -1 && loreIndex == 1) {
          lineCount++;
          string = arg.replace("-p:", "");
        }
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null && commandSender instanceof Player) {
      lineCount--;
      player = (Player) commandSender;
    }

    if (player == null) {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
      return null;
    }

    var list = Arrays.asList(Arrays.copyOfRange(strings, lineCount, strings.length));
    StringBuilder stringBuilder = new StringBuilder();
    int position = 0;

    for (String loreLine : list) {
      position++;
      stringBuilder.append(loreLine);
      if (position != list.size()) {
        stringBuilder.append(" ");
      }
    }

    if (!player.getName().equalsIgnoreCase(commandSender.getName())
        && !PermissionsManager.CMIPerm.command_$1_others.hasPermission(
            commandSender, true, this.getClass().getSimpleName())) {
      return null;
    }

    String joinedLore = CMIChatColor.translate(stringBuilder.toString());
    ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
    if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
      CMILC.info(this, commandSender, "noitem");
      return null;
    }

    if (cmi.getConfigManager().getItemLoreTypeBlackList().contains(itemStack.getType())) {
      CMILC.info(this, commandSender, "blocked");
      return null;
    }

    if (Config.ItemLoreMaxLength > 0
        && CMIChatColor.stripColor(joinedLore).length() > Config.ItemLoreMaxLength) {
      CMILC.info("itemname", commandSender, "maxLength", "[length]", Config.ItemLoreMaxLength);
      return null;
    }

    joinedLore =
        CMITextCleaner.cleanFromColorCodes(commandSender, joinedLore, CMIColorTypes.itemlore, true);
    ItemMeta itemMeta = null;
    if (itemStack.hasItemMeta()) {
      itemMeta = itemStack.getItemMeta();
    } else {
      itemMeta = new ItemStack(Material.STONE).getItemMeta();
    }

    if (value == -1) {
      if (itemMeta.hasLore()) {
        value = itemMeta.getLore().size() + 1;
      } else {
        value = 1;
      }
    }

    if (value >= 1 && value <= 20) {
      int loreSize = value;
      List newLore = new ArrayList();
      List<String> loreBuffer = new ArrayList();
      if (itemMeta.hasLore()) {
        newLore = itemMeta.getLore();
      }

      boolean replaceMode = false;
      if (itemMeta.hasLore() && joinedLore.equalsIgnoreCase("remove")) {
        if (newLore.size() < loreSize) {
          CMILC.info(this, commandSender, "maxLines", "[lines]", newLore.size());
          return null;
        }

        if (!flag) {
          newLore.remove(loreSize - 1);
        } else {
          newLore.clear();
        }

        replaceMode = true;
        loreBuffer = newLore;
      } else if (joinedLore.equalsIgnoreCase("insert")) {
        loreBuffer = newLore;
        if (loreBuffer.size() < loreSize - 1) {
          for (int index = 0; index <= loreSize - 1 - loreBuffer.size(); index++) {
            loreBuffer.add("");
          }
        }

        loreBuffer.add(loreSize - 1, "");
      } else if (newLore.size() >= loreSize) {
        for (int removeIndex = 0; removeIndex < newLore.size(); removeIndex++) {
          if (removeIndex == loreSize - 1) {
            loreBuffer.add(joinedLore);
          } else {
            loreBuffer.add((String) newLore.get(removeIndex));
          }
        }
      } else {
        for (int insertIndex = 0; insertIndex < loreSize; insertIndex++) {
          if (insertIndex == loreSize - 1) {
            loreBuffer.add(joinedLore);
          } else if (insertIndex < newLore.size()) {
            loreBuffer.add((String) newLore.get(insertIndex));
          } else {
            loreBuffer.add("");
          }
        }
      }

      loreBuffer = cmi.getPlaceholderAPIManager().updatePlaceHolders(player, loreBuffer);

      for (int scanIndex = 0; scanIndex < newLore.size(); scanIndex++) {
        String loreEntry = (String) newLore.get(scanIndex);
        if (!loreBuffer.contains(loreEntry)) {
          replaceMode = true;
          break;
        }
      }

      if (replaceMode
          && !PermissionsManager.CMIPerm.command_itemlore_modification.hasPermission(
              commandSender)) {
        return null;
      }

      itemMeta.setLore(loreBuffer);
      itemStack.setItemMeta(itemMeta);
      CMIItemStack cmiItemStack = new CMIItemStack(itemStack);
      if (cmi.getConfigManager().isItemLoreMarkChanged()) {
        cmiItemStack.setNbt("ModifiedLore", true);
      }

      if (player.isOnline()) {
        player.updateInventory();
      }

      cmi.save(player);
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      if (joinedLore.equalsIgnoreCase("remove")) {
        CMILC.info(this, commandSender, "removed", "[line]", loreSize, snd);
      } else if (joinedLore.equalsIgnoreCase("insert")) {
        CMILC.info(this, commandSender, "inserted", "[line]", loreSize, snd);
      } else {
        CMILC.info(
            this, commandSender, "feedback", "[line]", loreSize, "[loreLine]", joinedLore, snd);
      }

      return true;
    } else {
      return false;
    }
  }
}
