/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.GUI.CMIGui
 *  net.Zrips.CMILib.GUI.CMIGuiButton
 *  net.Zrips.CMILib.GUI.GUIManager$GUIClickType
 *  net.Zrips.CMILib.GUI.GUIManager$GUIRows
 *  net.Zrips.CMILib.GUI.GUIManager$InvType
 *  net.Zrips.CMILib.Items.CMIMaterial
 *  net.Zrips.CMILib.Items.CMITrimMaterial
 *  net.Zrips.CMILib.Items.CMITrimPattern
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.Version.Version
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ArmorMeta
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.trim.ArmorTrim
 *  org.bukkit.inventory.meta.trim.TrimMaterial
 *  org.bukkit.inventory.meta.trim.TrimPattern
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Items.CMITrimMaterial;
import net.Zrips.CMILib.Items.CMITrimPattern;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class trim implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("title", "{gcp}Modify trim on armor");
  }

  @Override
  @CAnnotation(
      info = "Modify trim on armor",
      args = "(playerName) (-s) (clear)",
      multiTab = {"[playername],clear", "[playername] [trimpattern],clear [trimmaterial]"},
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {1, 2, 3, 4},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    if (Version.isCurrentLower((Version) Version.v1_20_R1)) {
      LC.info_versionNotSupported.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    Object targetName = null;
    CMITrimMaterial cMITrimMaterial = null;
    CMITrimPattern cMITrimPattern = null;
    boolean bl = false;
    boolean silent = false;
    String[] commandArgs = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      object = commandArgs[argIndex];
      switch (((String) object).toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            silent = true;
            break;
          }
        case "clear":
          {
            bl = true;
            break;
          }
        default:
          {
            if (cMITrimMaterial == null
                    && (cMITrimMaterial = trim.getMaterial((String) object)) != null
                || cMITrimPattern == null
                    && (cMITrimPattern = trim.getPattern((String) object)) != null) break;
            if (((String) object).startsWith("-p:")) {
              targetName = ((String) object).substring("-p:".length());
              break;
            }
            if (targetName != null) break;
            targetName = object;
          }
      }
      ++argIndex;
    }
    object = cMI.getUser(commandSender, (String) targetName, this);
    if (object == null) {
      return null;
    }
    if (!((CMIUser) object).isOnline()) {
      LC.info_NotOnline.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    Player player = ((CMIUser) object).getPlayer(false);
    if (player == null) {
      return null;
    }
    ItemStack itemStack = player.getInventory().getItemInMainHand();
    if (itemStack == null) {
      LC.info_nothingInHand.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    if (!CMIMaterial.isValidItem((Material) itemStack.getType())) {
      LC.info_nothingInHand.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    if (!CMIMaterial.isArmor((Material) itemStack.getType())) {
      LC.info_nothingInHand.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    if (bl) {
      trim.apply(itemStack, null, null);
      return true;
    }
    if (cMITrimMaterial != null && cMITrimPattern != null) {
      trim.apply(itemStack, cMITrimMaterial, cMITrimPattern);
      return true;
    }
    this.openGUI(player, itemStack);
    return true;
  }

  private static CMITrimMaterial getMaterial(String string) {
    CMITrimMaterial[] cMITrimMaterialArray = CMITrimMaterial.values();
    int n = cMITrimMaterialArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      CMITrimMaterial cMITrimMaterial = cMITrimMaterialArray[argIndex];
      if (cMITrimMaterial.toString().equalsIgnoreCase(string)) {
        return cMITrimMaterial;
      }
      ++argIndex;
    }
    return null;
  }

  private static CMITrimPattern getPattern(String string) {
    CMITrimPattern[] cMITrimPatternArray = CMITrimPattern.values();
    int n = cMITrimPatternArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      CMITrimPattern cMITrimPattern = cMITrimPatternArray[argIndex];
      if (cMITrimPattern.toString().equalsIgnoreCase(string)) {
        return cMITrimPattern;
      }
      ++argIndex;
    }
    return null;
  }

  private static boolean isMaterial(ItemStack itemStack, CMITrimMaterial cMITrimMaterial) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (!(itemMeta instanceof ArmorMeta)) {
      return false;
    }
    ArmorMeta armorMeta = (ArmorMeta) itemMeta;
    if (armorMeta.getTrim() == null) {
      return false;
    }
    return cMITrimMaterial.get().equals(armorMeta.getTrim().getMaterial());
  }

  private static boolean isPattern(ItemStack itemStack, CMITrimPattern cMITrimPattern) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (!(itemMeta instanceof ArmorMeta)) {
      return false;
    }
    ArmorMeta armorMeta = (ArmorMeta) itemMeta;
    if (armorMeta.getTrim() == null) {
      return false;
    }
    return cMITrimPattern.get().equals(armorMeta.getTrim().getPattern());
  }

  private static void apply(
      ItemStack itemStack, CMITrimMaterial cMITrimMaterial, CMITrimPattern cMITrimPattern) {
    try {
      ArmorMeta armorMeta = (ArmorMeta) itemStack.getItemMeta();
      if (cMITrimMaterial == null && cMITrimPattern == null) {
        armorMeta.setTrim(null);
        itemStack.setItemMeta((ItemMeta) armorMeta);
        return;
      }
      ArmorTrim armorTrim = armorMeta.getTrim();
      TrimPattern trimPattern = null;
      TrimMaterial trimMaterial = null;
      if (cMITrimPattern == null && armorTrim != null) {
        trimPattern = armorTrim.getPattern();
      }
      if (cMITrimMaterial == null && armorTrim != null) {
        trimMaterial = armorTrim.getMaterial();
      }
      TrimMaterial resolvedMaterial =
          trimMaterial == null && cMITrimMaterial != null
              ? cMITrimMaterial.get()
              : (trimMaterial = trimMaterial == null ? TrimMaterial.AMETHYST : trimMaterial);
      TrimPattern resolvedPattern =
          trimPattern == null && cMITrimPattern != null
              ? cMITrimPattern.get()
              : (trimPattern = trimPattern == null ? TrimPattern.BOLT : trimPattern);
      if (trimMaterial == null || trimPattern == null) {
        return;
      }
      ArmorTrim newTrim = new ArmorTrim(trimMaterial, trimPattern);
      armorMeta.setTrim(newTrim);
      itemStack.setItemMeta((ItemMeta) armorMeta);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  private void openGUI(final Player player, final ItemStack itemStack) {
    CMIMaterial cMIMaterial;
    int n;
    CMITrimMaterial cMITrimMaterial;
    CMIGui cMIGui =
        new CMIGui(player) {

          public void onClose() {}

          public boolean click(int n, GUIManager.GUIClickType gUIClickType, ItemStack itemStack) {
            return true;
          }
        };
    cMIGui.addLock(GUIManager.InvType.Main);
    cMIGui.setInvSize(GUIManager.GUIRows.r6);
    cMIGui.setTitle(CMILC.getIM(trim.class, "title", new Object[0]));
    ArrayList<Integer> arrayList = new ArrayList<Integer>();
    int row = 0;
    while (row < 6) {
      int column = 0;
      while (column <= 8) {
        arrayList.add(row * 9 + column);
        ++column;
      }
      ++row;
    }
    int slotIndex = 9;
    CMITrimMaterial[] cMITrimMaterialArray = CMITrimMaterial.values();
    int materialCount = cMITrimMaterialArray.length;
    int materialIndex = 0;
    while (materialIndex < materialCount) {
      cMITrimMaterial = cMITrimMaterialArray[materialIndex];
      final CMITrimMaterial trimMaterialFinal = cMITrimMaterial;
      if (slotIndex >= arrayList.size()) break;
      n = (Integer) arrayList.get(slotIndex);
      cMIMaterial =
          CMIMaterial.get((String) (String.valueOf(cMITrimMaterial.toString()) + "ingot"));
      if (cMIMaterial.isNone()) {
        cMIMaterial =
            CMIMaterial.get((String) (String.valueOf(cMITrimMaterial.toString()) + "lazuli"));
      }
      if (cMIMaterial.isNone()) {
        cMIMaterial = CMIMaterial.get((String) cMITrimMaterial.toString());
      }
      if (cMIMaterial.isNone()) {
        cMIMaterial =
            CMIMaterial.get((String) (String.valueOf(cMITrimMaterial.toString()) + "shard"));
      }
      if (cMIMaterial.isNone()) {
        cMIMaterial =
            CMIMaterial.get((String) (String.valueOf(cMITrimMaterial.toString()) + "ore"));
      }
      CMIGuiButton cMIGuiButton =
          new CMIGuiButton(n, cMIMaterial) {

            public void click(GUIManager.GUIClickType gUIClickType) {
              trim.apply(itemStack, trimMaterialFinal, null);
              trim.this.openGUI(player, itemStack);
            }

            public void updateLooks() {
              if (trim.isMaterial(itemStack, trimMaterialFinal)) {
                this.setGlowing();
              } else {
                this.removeGlowing();
              }
            }
          };
      cMIGui.addButton(cMIGuiButton);
      if (++slotIndex == 18) {
        slotIndex += 4;
      }
      ++materialIndex;
    }
    slotIndex = 27;
    CMITrimPattern[] cMITrimPatternArray = CMITrimPattern.values();
    materialCount = cMITrimPatternArray.length;
    materialIndex = 0;
    while (materialIndex < materialCount) {
      final CMITrimPattern cMITrimPattern = cMITrimPatternArray[materialIndex];
      if (slotIndex >= arrayList.size()) break;
      n = (Integer) arrayList.get(slotIndex);
      cMIMaterial =
          CMIMaterial.get(
              (String)
                  (String.valueOf(cMITrimPattern.toString()) + "_ARMOR_TRIM_SMITHING_TEMPLATE"));
      CMIGuiButton cMIGuiButton =
          new CMIGuiButton(n, cMIMaterial) {
            public void click(GUIManager.GUIClickType gUIClickType) {
              trim.apply(itemStack, null, cMITrimPattern);
              trim.this.openGUI(player, itemStack);
            }

            public void updateLooks() {
              if (trim.isPattern(itemStack, cMITrimPattern)) {
                this.setGlowing();
              } else {
                this.removeGlowing();
              }
            }
          };
      cMIGui.addButton(cMIGuiButton);
      if (++slotIndex == 36) {
        ++slotIndex;
      }
      ++materialIndex;
    }
    CMIGuiButton cMIGuiButton =
        new CMIGuiButton(49, itemStack.getType()) {

          public void click(GUIManager.GUIClickType gUIClickType) {
            try {
              trim.apply(itemStack, null, null);
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }
            trim.this.openGUI(player, itemStack);
          }

          public void updateLooks() {}
        };
    cMIGuiButton.setName(LC.info_clear.getLocale(new Object[0]));
    cMIGuiButton.hideItemFlags();
    cMIGui.addButton(cMIGuiButton);
    cMIGui.fillEmptyButtons();
    cMIGui.open();
  }
}
