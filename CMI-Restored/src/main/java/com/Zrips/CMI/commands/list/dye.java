/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Colors.CMIChatColor
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Items.CMIMaterial
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.NBT.CMINBT
 *  org.bukkit.Color
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.LeatherArmorMeta
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Animations.AnimationManager;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class dye implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("color", "{gcp}Current color - {gcs}[red],[green],[blue] {gcp}Hex:&6#[hex]");
    configReader.get("custom", "{gcp}Enabled custom color mode: {gcs}[mode]");
    configReader.get("bound", "{gcp}SoulBound to: {gcs}");
    configReader.get("boundName", "[playerName]");
  }

  @Override
  @CAnnotation(
      info = "Dye leather armor",
      args =
          "(playerName) (red,green,blue/hexCode/colorName/random/clear/rainbow/day/biome/health)"
              + " (-s)",
      tab = {"playername%%random%%clear%%colors%%rainbow%%day%%_biome%%health"},
      regVar = {0, 1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] args) {
    boolean bl = false;
    String lastArg = null;
    org.bukkit.Color dyeColor = null;
    boolean clearLore = false;
    AnimationManager.LeatherAnimationType leatherAnimationType = null;
    Pattern pattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    for (int argIndex = 0; argIndex < args.length; argIndex++) {
      String arg = args[argIndex];
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            if (arg.equalsIgnoreCase("random")) {
              Random random = new Random(System.nanoTime());
              dyeColor =
                  org.bukkit.Color.fromRGB(
                      random.nextInt(255), random.nextInt(255), random.nextInt(255));
              break;
            }
            if (arg.equalsIgnoreCase("clear")) {
              dyeColor = org.bukkit.Color.fromRGB(160, 101, 64);
              clearLore = true;
              break;
            }
            if (leatherAnimationType == null
                && (leatherAnimationType = AnimationManager.LeatherAnimationType.getByName(arg))
                    != null) break;
            if (dyeColor == null) {
              try {
                if (arg.contains(",")) {
                  String[] parts = arg.split(",");
                  dyeColor =
                      org.bukkit.Color.fromRGB(
                          Integer.parseInt(parts[0]),
                          Integer.parseInt(parts[1]),
                          Integer.parseInt(parts[2]));
                }
                if (dyeColor == null) {
                  if (pattern.matcher(arg).matches()) {
                    if (!arg.startsWith("#")) {
                      arg = "#" + arg;
                    }
                    java.awt.Color awtColor = java.awt.Color.decode(arg);
                    if (awtColor != null) {
                      dyeColor =
                          org.bukkit.Color.fromRGB(
                              awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
                    }
                  }
                  if (dyeColor == null) {
                    CMIChatColor chatColor = CMIChatColor.getColor(arg);
                    if (chatColor != null) {
                      dyeColor = chatColor.getRGBColor();
                    }
                  }
                }
                if (dyeColor == null) {
                  CMIChatColor namedColor = CMIChatColor.getColor(arg);
                  if (namedColor != null) {
                    dyeColor =
                        org.bukkit.Color.fromRGB(
                            namedColor.getRed(), namedColor.getGreen(), namedColor.getBlue());
                  }
                }
                if (dyeColor != null) {
                  break;
                }
              } catch (NumberFormatException numberFormatException) {
                numberFormatException.printStackTrace();
              }
            }
            lastArg = arg;
          }
      }
    }
    CMIUser cmiUser = cMI.getUser(commandSender, lastArg, this);
    if (cmiUser == null) {
      return null;
    }
    ItemStack itemStack =
        cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.MainHand);
    if (itemStack == null || !(itemStack.getItemMeta() instanceof LeatherArmorMeta)) {
      LC.info_nothingInHandLeather.sendMessage((Object) commandSender, new Object[0]);
      return false;
    }
    if (clearLore) {
      itemStack = (ItemStack) new CMINBT(itemStack).setInt("CMIRainbowArmor", null);
      if ((itemStack = (ItemStack) new CMINBT(itemStack).setString("CMISoulBound", null))
          .hasItemMeta()) {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        if (leatherArmorMeta.hasLore()) {
          int loreIndex = 0;
          while (loreIndex < leatherArmorMeta.getLore().size()) {
            if (((String) leatherArmorMeta.getLore().get(loreIndex))
                .startsWith(CMILC.getIM(this, "bound", new Object[0]))) {
              leatherArmorMeta.getLore().set(loreIndex, "");
            }
            ++loreIndex;
          }
        }
        leatherArmorMeta.setColor(dyeColor);
        itemStack.setItemMeta((ItemMeta) leatherArmorMeta);
        cmiUser.getInventory().setItem(CMIPlayerInventory.CMIInventorySlot.MainHand, itemStack);
      }
      return true;
    }
    if (dyeColor == null && leatherAnimationType == null) {
      LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
      org.bukkit.Color currentColor = leatherArmorMeta.getColor();
      if (!bl) {
        CMILC.info(
            (Object) this,
            commandSender,
            "color",
            "[red]",
            currentColor.getRed(),
            "[green]",
            currentColor.getGreen(),
            "[blue]",
            currentColor.getBlue(),
            "[hex]",
            String.format(
                    "%02x%02x%02x",
                    currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue())
                .toUpperCase());
      }
      return true;
    }
    if (dyeColor != null) {
      LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
      leatherArmorMeta.setColor(dyeColor);
      itemStack.setItemMeta((ItemMeta) leatherArmorMeta);
      if (!bl) {
        CMILC.info(
            (Object) this,
            commandSender,
            "color",
            "[red]",
            dyeColor.getRed(),
            "[green]",
            dyeColor.getGreen(),
            "[blue]",
            dyeColor.getBlue(),
            "[hex]",
            String.format(
                    "%02x%02x%02x", dyeColor.getRed(), dyeColor.getGreen(), dyeColor.getBlue())
                .toUpperCase());
      }
      return true;
    }
    if (leatherAnimationType != null) {
      ItemStack dyedItem;
      if (!CMIModule.coloredArmor.isEnabled()) {
        LC.info_ModuleNotEnabled.sendMessage((Object) commandSender, new Object[0]);
        return null;
      }
      if (!CMIMaterial.isLeatherArmor((Material) itemStack.getType())) {
        LC.info_nothingInHandLeather.sendMessage((Object) commandSender, new Object[0]);
      }
      if ((dyedItem =
              dye.setCustom(
                  commandSender, cmiUser.getPlayer(false), itemStack, leatherAnimationType))
          == null) {
        return null;
      }
      cmiUser.getInventory().setItem(CMIPlayerInventory.CMIInventorySlot.MainHand, dyedItem);
      if (!bl) {
        CMILC.info(
            (Object) this, commandSender, "custom", new Object[] {"[mode]", leatherAnimationType});
      }
    }
    return true;
  }

  public static ItemStack setCustom(
      CommandSender commandSender,
      Player player,
      ItemStack itemStack,
      AnimationManager.LeatherAnimationType leatherAnimationType) {
    if (!CMIModule.coloredArmor.isEnabled()) {
      LC.info_ModuleNotEnabled.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    ItemStack dyedItem =
        (ItemStack) new CMINBT(itemStack).setInt("CMIRainbowArmor", leatherAnimationType.getId());
    if (Config.DyeBoundToPlayer && player != null) {
      ItemMeta itemMeta =
          (dyedItem =
                  (ItemStack)
                      new CMINBT(dyedItem)
                          .setString("CMISoulBound", player.getUniqueId().toString()))
              .getItemMeta();
      List<String> arrayList = itemMeta.getLore();
      if (arrayList == null) {
        arrayList = new ArrayList<String>();
      }
      for (String loreLine : arrayList) {
        if (!CMIChatColor.stripColor((String) loreLine)
            .contains(
                CMIChatColor.stripColor((String) CMILC.getIM(dye.class, "bound", new Object[0]))))
          continue;
        arrayList.remove(loreLine);
        break;
      }
      Snd snd = new Snd();
      snd.setSender(player);
      snd.setTarget(player);
      arrayList.add(
          String.valueOf(CMILC.getIM(dye.class, "bound", new Object[0]))
              + CMILC.getIM(dye.class, "boundName", snd));
      itemMeta.setLore(arrayList);
      dyedItem.setItemMeta(itemMeta);
    }
    return dyedItem;
  }
}
