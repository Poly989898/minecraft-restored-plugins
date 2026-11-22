package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashMap;
import java.util.HashSet;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class enchant implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "enchanted",
        "{gcp}Enchanted with {gcs}[enchant] {gcp}lvl {gcs}[level]{gcp} for"
            + " {gcs}[playerDisplayName]");
    configReader.get(
        "removed",
        "{gcp}You have removed {gcs}[enchant] {gcp}enchanted from item for"
            + " {gcs}[playerDisplayName]");
    configReader.get("clear", "{gcp}Cleared enchantments from item for {gcs}[playerDisplayName]");
    configReader.get(
        "clearOne",
        "{gcp}Removed {gcs}[enchant] {gcp}enchantment from item for {gcs}[playerDisplayName]");
    configReader.get("noitem", "{gcw}Not holding any item in hand.");
    configReader.get(
        "invalidEnchant",
        "{gcw}You don't have permission to enchant item with invalid enchantment");
    configReader.get(
        "clearedInvalid", "{gcp}Removed {gcs}[amount] {gcp}invalid enchantments from item");
    configReader.get("inccorectId", "{gcp}Incorrect enchant name or id.");
    configReader.get("wrongItem", "{gcp}Wrong item in your hand");
    configReader.get("enchantlimit", "{gcw}You don't have access to this enchantment");
    configReader.get("levellimit", "{gcw}Level is too high, max allowed is {gcp}[level].");
    configReader.get(
        "conflict", "{gcw}Conflicting enchantment {gcs}[enchant1] {gcw}-> {gcs}[enchant2]");
    configReader.get("possible", "{gcp}Possible enchants: ");
    configReader.get("possibleList", "{gcs}[enchant]");
  }

  @CAnnotation(
      info = "Enchant items",
      args =
          "(playerName) [enchant] [level] (-o) (-onlyvalid) (-keeponlyvalid) (-inform) (-s)"
              + " (-i:[itemName(:data)]) (clear)",
      tab = {
        "playername%%limitedEnchant%%clear",
        "limitedEnchant%%maxenchantlevel",
        "maxenchantlevel"
      },
      explanation = "-o will take item from offhand",
      regVar = {0, 1, 2, 3, 4, 5, 6, 7},
      consoleVar = {2, 3, 4, 5, 6, 7},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    Enchantment enchantment = null;
    int value = -1;
    boolean allEnchants = false;
    boolean removeMode = false;
    boolean unsafeMode = false;
    boolean storedMode = false;
    boolean silent = false;
    CMIItemStack cmiItemStack = null;
    if (strings.length == 0) {
      Player player = (Player) commandSender;
      ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
      if (itemStack != null && itemStack.getType() != Material.AIR) {
        HashSet<Enchantment> hashSet = new HashSet();
        Enchantment[] enchantments;
        int enchantCount = (enchantments = CMIEnchantment.values()).length;

        for (int enchantIndex = 0; enchantIndex < enchantCount; enchantIndex++) {
          Enchantment chosenEnchantment = enchantments[enchantIndex];
          if (CMIEnchantment.isEnabled(chosenEnchantment)
              && chosenEnchantment.canEnchantItem(itemStack)) {
            hashSet.add(chosenEnchantment);
          }
        }

        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(CMILC.getIM(this, "possible"));
        String enchantText = "";

        for (Enchantment candidateEnchant : hashSet) {
          CMIEnchantment cmiEnchantment = CMIEnchantment.get(candidateEnchant);
          if (CMIEnchantment.getName(candidateEnchant) != null && cmiEnchantment != null) {
            if (!enchantText.isEmpty()) {
              rawMessage.addText(LC.info_ListSpliter.getLocale(new Object[0]));
            }

            String displayName = CMIText.everyFirstToUpperCase(cmiEnchantment.getKeyName(), false);
            rawMessage
                .addText(CMILC.getIM(this, "possibleList", "[enchant]", displayName))
                .addHover(CMILC.getIM(this, "possibleList", "[enchant]", displayName))
                .addSuggestion(
                    "/cmi enchant "
                        + displayName
                        + " "
                        + cmi.getEnchantManager().getMaxLevel(candidateEnchant));
            enchantText = "s";
          }
        }

        rawMessage.show(player);
        return true;
      } else {
        return false;
      }
    } else {
      String[] levelArgs = strings;
      int argCount = strings.length;

      for (int argIndex = 0; argIndex < argCount; argIndex++) {
        String arg = levelArgs[argIndex];
        if (arg.equalsIgnoreCase("-o")) {
          allEnchants = true;
        } else if (arg.equalsIgnoreCase("-onlyvalid")) {
          removeMode = true;
        } else if (arg.equalsIgnoreCase("-keeponlyvalid")) {
          unsafeMode = true;
        } else if (arg.startsWith("-i:")) {
          cmiItemStack =
              CMILib.getInstance().getItemManager().getItem(arg.substring("-i:".length()));
        } else if (arg.equalsIgnoreCase("-inform")) {
          storedMode = true;
        } else if (arg.equalsIgnoreCase("-s")) {
          if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
            silent = true;
          }
        } else if (arg.equalsIgnoreCase("clear")) {
          flag = true;
        } else {
          if (enchantment == null) {
            enchantment = CMIEnchantment.get(arg);
            if (enchantment != null && !CMIEnchantment.isEnabled(enchantment)) {
              enchantment = null;
            }

            if (enchantment != null) {
              continue;
            }
          }

          if (value == -1) {
            try {
              value = Integer.parseInt(arg);
              continue;
            } catch (NumberFormatException numberFormatException) {
            }
          }

          string = arg;
        }
      }

      if (value < 1) {
        value = 1;
      }

      if (enchantment == null && !flag && !unsafeMode) {
        CMILC.info(this, commandSender, "inccorectId");
        return null;
      }

      Player targetPlayer = cmi.getTarget(commandSender, string, this);
      if (targetPlayer == null) {
        return null;
      }

      if (!canEnchant(commandSender, targetPlayer, flag, enchantment, value, silent, storedMode)) {
        return null;
      }

      ItemStack heldItem =
          allEnchants
              ? CMIItemStack.getItemInOffHand(targetPlayer)
              : CMIItemStack.getItemInMainHand(targetPlayer);
      if (heldItem != null && !heldItem.getType().equals(Material.AIR)) {
        CMIItemStack heldStack = CMILib.getInstance().getItemManager().getItem(heldItem);
        if (cmiItemStack != null && !cmiItemStack.isSimilar(heldItem)) {
          if (!silent) {
            CMILC.info(this, commandSender, "wrongItem");
          }

          if (storedMode
              && !commandSender.getName().equals(targetPlayer.getName())
              && targetPlayer.isOnline()) {
            CMILC.info(this, targetPlayer, "wrongItem");
          }

          return null;
        } else if (heldStack.getValidEnchants().contains(enchantment)
            || !removeMode
                && (!cmi.getEnchantManager().isEnforceValidEnchants()
                    || PermissionsManager.CMIPerm.command_enchant_bypassinvalid.hasPermission(
                        commandSender))) {
          if (heldItem.getType() == Material.AIR) {
            if (!silent) {
              CMILC.info(this, commandSender, "noitem");
            }

            if (storedMode
                && !commandSender.getName().equals(targetPlayer.getName())
                && targetPlayer.isOnline()) {
              CMILC.info(this, targetPlayer, "noitem");
            }

            return false;
          } else {
            if (!flag
                && enchantment != null
                && !PermissionsManager.CMIPerm.command_enchant_bypassinvalid.hasPermission(
                    commandSender)) {
              for (Enchantment existingEnchant : heldItem.getEnchantments().keySet()) {
                if (existingEnchant.conflictsWith(enchantment)) {
                  CMILC.info(
                      this,
                      commandSender,
                      "conflict",
                      "[enchant1]",
                      CMIEnchantment.getTranslatedName(enchantment),
                      "[enchant2]",
                      CMIEnchantment.getTranslatedName(existingEnchant));
                  return null;
                }
              }
            }

            int appliedCount = 0;
            if (unsafeMode) {
              for (var entry : new HashMap<>(heldItem.getEnchantments()).entrySet()) {
                if (!heldStack.getValidEnchants().contains(entry.getKey())) {
                  heldItem.removeEnchantment((Enchantment) entry.getKey());
                  appliedCount++;
                }
              }
            } else {
              if (heldItem.getType() == Material.BOOK) {
                heldItem.setType(Material.ENCHANTED_BOOK);
              }

              if (heldItem.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta enchantmentStorageMeta =
                    (EnchantmentStorageMeta) heldItem.getItemMeta();
                if (!flag) {
                  enchantmentStorageMeta.removeStoredEnchant(enchantment);
                  if (value != 0) {
                    enchantmentStorageMeta.addStoredEnchant(enchantment, value, true);
                  }

                  heldItem.setItemMeta(enchantmentStorageMeta);
                } else {
                  for (var storedEntry : enchantmentStorageMeta.getStoredEnchants().entrySet()) {
                    if (enchantment == null || enchantment.equals(storedEntry.getKey())) {
                      enchantmentStorageMeta.removeStoredEnchant(
                          (Enchantment) storedEntry.getKey());
                    }
                  }

                  heldItem.setItemMeta(enchantmentStorageMeta);
                }
              } else if (!flag) {
                heldItem.removeEnchantment(enchantment);
                if (value != 0) {
                  heldItem.addUnsafeEnchantment(enchantment, value);
                }
              } else {
                for (var itemEntry : heldItem.getEnchantments().entrySet()) {
                  if (enchantment == null || enchantment.equals(itemEntry.getKey())) {
                    heldItem.removeEnchantment((Enchantment) itemEntry.getKey());
                  }
                }
              }
            }

            cmi.save(targetPlayer);
            if (targetPlayer.isOnline()) {
              targetPlayer.updateInventory();
            }

            String enchantLabel = CMIEnchantment.getTranslatedName(enchantment);
            if (enchantLabel != null) {
              enchantLabel = CMIText.everyFirstToUpperCase(enchantLabel);
            }

            Snd snd = new Snd().setSender(commandSender).setTarget(targetPlayer);
            if (!silent) {
              if (!flag) {
                if (value > 0) {
                  if (unsafeMode) {
                    CMILC.info(
                        this, commandSender, "clearedInvalid", "[amount]", appliedCount, snd);
                  } else {
                    CMILC.info(
                        this,
                        commandSender,
                        "enchanted",
                        "[enchant]",
                        enchantLabel,
                        "[level]",
                        value,
                        snd);
                  }
                } else {
                  CMILC.info(this, commandSender, "removed", "[enchant]", enchantLabel, snd);
                }
              } else if (enchantment == null) {
                CMILC.info(this, targetPlayer, "clear", snd);
              } else {
                CMILC.info(this, targetPlayer, "clearOne", snd, "[enchant]", enchantLabel);
              }
            }

            if (storedMode
                && !commandSender.getName().equals(targetPlayer.getName())
                && targetPlayer.isOnline()) {
              if (!flag) {
                if (value > 0) {
                  if (unsafeMode) {
                    CMILC.info(
                        this, commandSender, "clearedInvalid", "[amount]", appliedCount, snd);
                  } else {
                    CMILC.info(
                        this,
                        targetPlayer,
                        "enchanted",
                        "[enchant]",
                        enchantLabel,
                        "[level]",
                        value,
                        snd);
                  }
                } else {
                  CMILC.info(this, targetPlayer, "removed", "[enchant]", enchantLabel, snd);
                }
              } else if (enchantment == null) {
                CMILC.info(this, targetPlayer, "clear", snd);
              } else {
                CMILC.info(this, targetPlayer, "clearOne", snd, "[enchant]", enchantLabel);
              }
            }

            return true;
          }
        } else {
          if (!silent) {
            CMILC.info(this, commandSender, "invalidEnchant");
          }

          if (storedMode
              && !commandSender.getName().equals(targetPlayer.getName())
              && targetPlayer.isOnline()) {
            CMILC.info(this, targetPlayer, "invalidEnchant");
          }

          return null;
        }
      } else {
        return false;
      }
    }
  }

  public static boolean canEnchant(
      CommandSender commandSender,
      Player player,
      boolean flag,
      Enchantment enchantment,
      int value,
      boolean allowUnsafe,
      boolean silent) {
    if (CMI.getInstance().getEnchantManager().isEnchantLimitEnabled()
        && !flag
        && enchantment != null
        && CMI.getInstance().getEnchantManager().getMaxLevel(enchantment) < value
        && !PermissionsManager.CMIPerm.command_enchant_bypasslimit.hasPermission(commandSender)) {
      if (!allowUnsafe) {
        CMILC.info(
            enchant.class,
            commandSender,
            "levellimit",
            "[level]",
            CMI.getInstance().getEnchantManager().getMaxLevel(enchantment));
      }

      if (silent && !commandSender.getName().equals(player.getName()) && player.isOnline()) {
        CMILC.info(
            enchant.class,
            player,
            "levellimit",
            "[level]",
            CMI.getInstance().getEnchantManager().getMaxLevel(enchantment));
      }

      return false;
    } else {
      if (!flag
          && enchantment != null
          && CMI.getInstance().getEnchantManager().isRequireSpecificPermission()
          && !PermissionsManager.CMIPerm.enchantments_$1.hasPermission(
              commandSender, CMIEnchantment.getName(enchantment).replace("_", ""))) {
        CMILC.info(enchant.class, commandSender, "enchantlimit");
        return false;
      }

      if (!flag
          && enchantment != null
          && CMI.getInstance().getEnchantManager().isPermissionLevelLimit()
          && commandSender instanceof Player) {
        PermissionInfo permissionInfo =
            CMI.getInstance()
                .getPermissionsManager()
                .getPermissionInfo(
                    (Player) commandSender,
                    PermissionsManager.CMIPerm.enchantments_$1_$2.getPermission(
                        CMIEnchantment.getName(enchantment).replace("_", ""), ""));
        Double maxLevel = permissionInfo.getMaxValue();
        if (maxLevel == null) {
          maxLevel = 1.0;
        }

        if (maxLevel < value) {
          CMILC.info(enchant.class, commandSender, "levellimit", "[level]", maxLevel.intValue());
          return false;
        }
      }

      return true;
    }
  }

  public static boolean containsInvalidEnchants(ItemStack itemStack, CommandSender commandSender) {
    if (itemStack.getType() == Material.ENCHANTED_BOOK) {
      EnchantmentStorageMeta enchantmentStorageMeta =
          (EnchantmentStorageMeta) itemStack.getItemMeta();

      for (var entry : enchantmentStorageMeta.getStoredEnchants().entrySet()) {
        if (!canEnchant(
            commandSender,
            null,
            false,
            (Enchantment) entry.getKey(),
            (Integer) entry.getValue(),
            true,
            false)) {
          return true;
        }
      }

      itemStack.setItemMeta(enchantmentStorageMeta);
    } else {
      for (var entryItem : itemStack.getEnchantments().entrySet()) {
        if (!canEnchant(
            commandSender,
            null,
            false,
            (Enchantment) entryItem.getKey(),
            (Integer) entryItem.getValue(),
            true,
            false)) {
          return true;
        }
      }
    }

    return false;
  }
}
