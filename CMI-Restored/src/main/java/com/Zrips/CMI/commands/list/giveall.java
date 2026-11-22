package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class giveall implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "starting", "{gcp}Starting to give items for all players, this can take some time.");
    configReader.get("running", "{gcs}[amount] {gcp}already got item, still running.");
    configReader.get("feedback", "{gcp}Finished. {gcs}[amount] {gcp}players got item.");
  }

  @CAnnotation(
      info = "Give item for all players",
      args = "[itemname] (amount) (e|l|n|offline)",
      tab = {"itemname", "offline%%n%%l%%e"},
      explanation = {
        "&e- give item name or its id with data value",
        "&e- optionaly provide amount you want to give ",
        "&e- &6n &e- to define itemname ",
        "&e- &6l &e- to define item lore ",
        "&e- &6e &e - to define item enchants ",
        "&e- &6-s &e - wont show feedback message ",
        "&e- &6h &e - followed with player name will give item from its hand ",
        "&e- &6inv &e - followed by player name will give entire inventory for others ",
        "&e- &6offline &e- to include offline players",
        "&ecmi giveall stone 1 n &2Uber_stone l &3Stone_lore offline",
        "&ecmi giveall h Zrips",
        "&ecmi giveall inv Zrips"
      },
      regVar = -100,
      consoleVar = -100,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    int value = 0;
    int slotCount = 0;
    int slotIndex = 1;
    CMIMaterial cmiMaterial = null;
    if (!strings[0].equalsIgnoreCase("h") && !strings[0].equalsIgnoreCase("inv")) {
      try {
        if (strings[0].contains(":")) {
          value = Integer.parseInt(strings[0].split(":")[0]);
        } else {
          value = Integer.parseInt(strings[0]);
        }
      } catch (NumberFormatException numberFormatException) {
        if (strings[0].contains(":")) {
          cmiMaterial = CMIMaterial.get(strings[0].toUpperCase().split(":")[0]);
        } else {
          cmiMaterial = CMIMaterial.get(strings[0].toUpperCase());
        }

        if (cmiMaterial == null) {
          commandSender.sendMessage(LC.info_UseInteger.getLocale(new Object[0]));
          cmi.getCommandManager().sendUsage(commandSender, this.getClass().getSimpleName());
          return null;
        }
      }

      try {
        if (strings[0].contains(":")) {
          slotCount = Integer.parseInt(strings[0].split(":")[1]);
        }
      } catch (NumberFormatException numberFormatException2) {
        commandSender.sendMessage(LC.info_UseInteger.getLocale(new Object[0]));
        cmi.getCommandManager().sendUsage(commandSender, this.getClass().getSimpleName());
        return null;
      }

      if (strings.length >= 2) {
        try {
          slotIndex = Integer.parseInt(strings[1]);
        } catch (NumberFormatException numberFormatException3) {
        }
      }

      if (slotIndex < 1) {
        slotIndex = 1;
      }

      if (cmiMaterial == null) {
        cmiMaterial = CMIMaterial.get(value);
      }

      if (cmiMaterial == null) {
        commandSender.sendMessage(LC.info_UseInteger.getLocale(new Object[0]));
        cmi.getCommandManager().sendUsage(commandSender, this.getClass().getSimpleName());
        return null;
      }
    }

    String string = null;
    ArrayList<String> arrayList = new ArrayList();
    HashMap<Enchantment, Integer> hashMap = new HashMap();
    boolean flag = false;
    ArrayList<ItemStack> itemList = new ArrayList();
    String targetName = null;
    if (strings.length >= 2) {
      label253:
      for (int index = 0; index < strings.length; index++) {
        String itemArg;
        switch ((itemArg = strings[index]).hashCode()) {
          case -1548612125:
            if (!itemArg.equals("offline")) {
              continue;
            }
            break;
          case 101:
            if (!itemArg.equals("e") || index + 1 >= strings.length) {
              continue;
            }

            ArrayList<String> targetNames = new ArrayList();
            if (!strings[index + 1].contains(",")) {
              targetNames.add(strings[index + 1]);
            } else {
              String[] amountParts;
              int amountCount = (amountParts = strings[index + 1].split(",")).length;

              for (int amountIndex = 0; amountIndex < amountCount; amountIndex++) {
                String amountText = amountParts[amountIndex];
                arrayList.add(amountText);
              }
            }

            Iterator iterator = targetNames.iterator();

            while (true) {
              if (!iterator.hasNext()) {
                continue label253;
              }

              String playerName = (String) iterator.next();
              if (playerName.contains(":")) {
                int offlineCount = 0;

                try {
                  offlineCount = Integer.parseInt(playerName.split(":")[1]);
                } catch (NumberFormatException numberFormatException4) {
                  continue;
                }

                Enchantment enchantment = CMIEnchantment.get(playerName.split(":")[0]);
                if (enchantment != null) {
                  hashMap.put(enchantment, offlineCount);
                }
              }
            }
          case 104:
            if (itemArg.equals("h") && index + 1 < strings.length) {
              targetName = strings[index + 1];
              Player player = cmi.getTarget(commandSender, targetName, this);
              if (player == null) {
                return false;
              }

              targetName = player.getName();
              ItemStack itemStack =
                  CMILib.getInstance().getReflectionManager().getItemInMainHand(player);
              if (itemStack == null || itemStack.getType() == Material.AIR) {
                return false;
              }

              itemList.add(itemStack);
            }
            continue;
          case 108:
            if (!itemArg.equals("l") || index + 1 >= strings.length) {
              continue;
            }

            if (!strings[index + 1].contains("/n")) {
              arrayList.add(CMIChatColor.translate(strings[index + 1].replace("_", " ")));
              continue;
            }

            String[] loreParts;
            int loreCount = (loreParts = strings[index + 1].split("/n")).length;
            int loreIndex = 0;

            while (true) {
              if (loreIndex >= loreCount) {
                continue label253;
              }

              String loreText = loreParts[loreIndex];
              arrayList.add(CMIChatColor.translate(loreText.replace("_", " ")));
              loreIndex++;
            }
          case 110:
            if (itemArg.equals("n") && index + 1 < strings.length) {
              string = CMIChatColor.translate(strings[index + 1].replace("_", " "));
            }
            continue;
          case 1506:
            if (!itemArg.equals("-o")) {
              continue;
            }
            break;
          case 104433:
            if (itemArg.equals("inv") && index + 1 < strings.length) {
              targetName = strings[index + 1];
              Player targetPlayer = cmi.getTarget(commandSender, targetName, this);
              if (targetPlayer == null) {
                return false;
              }

              targetName = targetPlayer.getName();
              ItemStack[] itemStacks = targetPlayer.getInventory().getContents();
              int slot = 0;
              ItemStack[] inventoryCopy = itemStacks;
              int inventoryCount = itemStacks.length;

              for (int inventoryIndex = 0; inventoryIndex < inventoryCount; inventoryIndex++) {
                ItemStack inventoryItem = inventoryCopy[inventoryIndex];
                if (++slot > 35) {
                  continue label253;
                }

                if (inventoryItem != null && inventoryItem.getType() != Material.AIR) {
                  itemList.add(inventoryItem);
                }
              }
            }
          default:
            continue;
        }

        flag = true;
      }
    }

    if (cmiMaterial != null) {
      ItemStack itemToGive = null;
      if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        itemToGive = new ItemStack(cmiMaterial.getMaterial(), slotIndex);
      } else {
        itemToGive = new ItemStack(cmiMaterial.getMaterial(), slotIndex, (short) slotCount);
      }

      ItemMeta itemMeta = itemToGive.getItemMeta();
      if (string != null) {
        itemMeta.setDisplayName(string);
      }

      if (!arrayList.isEmpty()) {
        itemMeta.setLore(arrayList);
      }

      if (!hashMap.isEmpty()) {
        for (Entry entry : hashMap.entrySet()) {
          itemMeta.addEnchant((Enchantment) entry.getKey(), (Integer) entry.getValue(), false);
        }
      }

      itemToGive.setItemMeta(itemMeta);
      itemList.add(itemToGive);
    }

    boolean silent = flag;
    int givenCount = 0;

    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      if (onlinePlayer != null
          && (targetName == null || !targetName.equals(onlinePlayer.getName()))) {
        for (ItemStack itemEntry : itemList) {
          CMIUser cmiUser = cmi.getPlayerManager().getUser(onlinePlayer);
          cmiUser.getInventory().addItem(itemEntry);
        }

        givenCount++;
      }
    }

    int countCopy = givenCount;
    String targetLabel = targetName;
    if (silent) {
      giveall giveall = this;
      CMIScheduler.runTaskAsynchronously(
          cmi,
          () -> {
            CMILC.info(giveall, commandSender, "starting");
            int countA = countCopy;
            int countB = countCopy;
            OfflinePlayer[] offlinePlayers;
            int offlineTotal = (offlinePlayers = Bukkit.getOfflinePlayers()).length;

            for (int offlineIndex = 0; offlineIndex < offlineTotal; offlineIndex++) {
              OfflinePlayer offlinePlayer = offlinePlayers[offlineIndex];
              CMIUser offlineUser = cmi.getPlayerManager().getUser(offlinePlayer, false);
              if (offlineUser != null) {
                Player offlinePlayerRef = offlineUser.getPlayer();
                if (offlinePlayerRef != null
                    && !offlinePlayerRef.isOnline()
                    && (targetLabel == null || !targetLabel.equals(offlinePlayer.getName()))) {
                  for (ItemStack remainingItem : itemList) {
                    offlinePlayerRef.getInventory().addItem(new ItemStack[] {remainingItem});
                  }

                  CMI.getInstance().save(offlinePlayerRef);
                  countA++;
                  if (++countB >= 500) {
                    countB = 0;
                    CMILC.info(giveall, commandSender, "running", "[amount]", countA);
                  }
                }
              }
            }

            CMILC.info(giveall, commandSender, "feedback", "[amount]", countA);
          });
    } else {
      CMILC.info(this, commandSender, "feedback", "[amount]", givenCount);
    }

    return true;
  }
}
