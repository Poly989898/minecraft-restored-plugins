package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Worth.WorthEnchantment;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIButtonLocation;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class setenchantworth implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      priority = 125,
      info = "Change enchantment worth",
      args = "",
      regVar = {0, 1, 2},
      consoleVar = 666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    enchantWorthMenu(player);
    return true;
  }

  private static void enchantWorthMenu(Player player) {
    final CMIGui cmiGui = new CMIGui(player);
    CMIGuiButton cmiGuiButton = null;
    Enchantment[] enchantments;
    int value = (enchantments = Enchantment.values()).length;

    for (int index = 0; index < value; index++) {
      final Enchantment enchantment = enchantments[index];
      if (enchantment != null) {
        cmiGuiButton =
            new CMIGuiButton(CMIMaterial.ENCHANTED_BOOK) {
              public void click(GUIClickType guiClickType) {
                setenchantworth.enchantWorthSubmenu(cmiGui.getPlayer(), enchantment);
              }
            };
        cmiGuiButton.setName("&7" + CMIEnchantment.getName(enchantment));
        ArrayList<String> arrayList = new ArrayList();

        for (int enchantLevel = 0; enchantLevel <= enchantment.getMaxLevel(); enchantLevel++) {
          WorthEnchantment worthEnchantment =
              CMI.getInstance().getWorthManager().getEnchantWorth(enchantment, enchantLevel);
          if (worthEnchantment != null) {
            if (enchantLevel > 0) {
              arrayList.add(
                  "&7"
                      + enchantLevel
                      + "lvl sell for "
                      + CMI.getInstance()
                          .getEconomyManager()
                          .format(worthEnchantment.getSellPrice()));
            } else {
              arrayList.add(
                  "&7Base price "
                      + CMI.getInstance()
                          .getEconomyManager()
                          .format(worthEnchantment.getSellPrice()));
            }
          } else if (enchantLevel > 0) {
            arrayList.add(
                "&7"
                    + enchantLevel
                    + "lvl sell for "
                    + CMI.getInstance().getEconomyManager().format(0.0));
          } else {
            arrayList.add("&7Base price " + CMI.getInstance().getEconomyManager().format(0.0));
          }
        }

        cmiGuiButton.addLore(arrayList);
        cmiGui.addButton(cmiGuiButton);
      }
    }

    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  private static void enchantWorthSubmenu(Player player, final Enchantment enchantment) {
    final CMIGui cmiGui = new CMIGui(player);
    CMIGuiButton cmiGuiButton = null;

    for (int value = 0; value <= enchantment.getMaxLevel(); value++) {
      final int level = value;
      cmiGuiButton =
          new CMIGuiButton(CMIMaterial.ENCHANTED_BOOK) {
            public void click(GUIClickType guiClickType) {
              setenchantworth.enchantWorthPriceEdit(cmiGui.getPlayer(), enchantment, level);
            }
          };
      cmiGuiButton.setName(
          "&f" + CMIEnchantment.getName(enchantment) + (value > 0 ? " level " + level : ""));
      ArrayList<String> arrayList = new ArrayList();
      WorthEnchantment worthEnchantment =
          CMI.getInstance().getWorthManager().getEnchantWorth(enchantment, value);
      if (worthEnchantment != null) {
        arrayList.add(
            "&7Sell for "
                + CMI.getInstance().getEconomyManager().format(worthEnchantment.getSellPrice()));
      } else {
        arrayList.add("&7Sell for " + CMI.getInstance().getEconomyManager().format(0.0));
      }

      if (value == 0) {
        arrayList.add("&7Used when we cant find enchant price by its level");
      }

      cmiGuiButton.addLore(arrayList);
      cmiGui.addButton(cmiGuiButton);
    }

    cmiGuiButton =
        new CMIGuiButton(CMIMaterial.CRAFTING_TABLE) {
          public void click(GUIClickType clickType) {
            setenchantworth.enchantWorthMenu(cmiGui.getPlayer());
          }
        };
    cmiGuiButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGuiButton.setSlot(cmiGui.getSlot(GUIButtonLocation.bottomRight));
    cmiGui.addButton(cmiGuiButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  private static void enchantWorthPriceEdit(
      Player player, final Enchantment enchantment, int value) {
    WorthEnchantment worthEnchantment =
        CMI.getInstance().getWorthManager().getEnchantWorth(enchantment, value);
    if (worthEnchantment == null) {
      worthEnchantment = new WorthEnchantment(enchantment, value, 0.0, null);
      CMI.getInstance().getWorthManager().addEnchantWorth(worthEnchantment);
    }

    final WorthEnchantment editedWorthEnchantment = worthEnchantment;
    final CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            CMI.getInstance().getWorthManager().updatePriceInFile(editedWorthEnchantment);
          }
        };
    cmiGui.setTitle(CMIEnchantment.getName(enchantment) + " level " + value);
    cmiGui.setInvSize(GUIRows.r3);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(9, CMIMaterial.YELLOW_WOOL.newItemStack()) {
          public void click(GUIClickType guiClickType) {
            switch (guiClickType) {
              case Left:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() - 0.01);
                break;
              case LeftShift:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() - 0.1);
                break;
              case Right:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() + 0.01);
                break;
              case RightShift:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() + 0.1);
            }

            if (editedWorthEnchantment.isBuyPriceSet()
                && editedWorthEnchantment.getSellPrice() > editedWorthEnchantment.getBuyPrice()) {
              editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getBuyPrice());
            }

            this.updateLooks();
          }

          public void updateLooks() {
            setenchantworth.updateLore(this.getGui(), editedWorthEnchantment);
          }
        };
    cmiGuiButton.setName(CMILC.getIM("setworth", "setSell", "[amount]", "0.1"));
    cmiGuiButton.setGui(cmiGui);
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(10, CMIMaterial.YELLOW_WOOL.newItemStack()) {
          public void click(GUIClickType clickType) {
            switch (clickType) {
              case Left:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() - 1.0);
                break;
              case LeftShift:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() - 10.0);
                break;
              case Right:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() + 1.0);
                break;
              case RightShift:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() + 10.0);
            }

            if (editedWorthEnchantment.isBuyPriceSet()
                && editedWorthEnchantment.getSellPrice() > editedWorthEnchantment.getBuyPrice()) {
              editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getBuyPrice());
            }

            this.updateLooks();
          }

          public void updateLooks() {
            setenchantworth.updateLore(this.getGui(), editedWorthEnchantment);
          }
        };
    cmiGuiButton.setName(CMILC.getIM("setworth", "setSell", "[amount]", "1"));
    cmiGuiButton.setGui(cmiGui);
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(11, CMIMaterial.YELLOW_WOOL.newItemStack()) {
          public void click(GUIClickType clickType) {
            switch (clickType) {
              case Left:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() - 10.0);
                break;
              case LeftShift:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() - 100.0);
                break;
              case Right:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() + 10.0);
                break;
              case RightShift:
                editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getSellPrice() + 100.0);
            }

            if (editedWorthEnchantment.isBuyPriceSet()
                && editedWorthEnchantment.getSellPrice() > editedWorthEnchantment.getBuyPrice()) {
              editedWorthEnchantment.setSellPrice(editedWorthEnchantment.getBuyPrice());
            }

            this.updateLooks();
          }

          public void updateLooks() {
            setenchantworth.updateLore(this.getGui(), editedWorthEnchantment);
          }
        };
    cmiGuiButton.setName(CMILC.getIM("setworth", "setSell", "[amount]", "10"));
    cmiGuiButton.setGui(cmiGui);
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton = new CMIGuiButton(13, CMIMaterial.ENCHANTED_BOOK);
    cmiGuiButton.setName(CMIEnchantment.getName(enchantment) + " level " + value);
    cmiGuiButton.setGui(cmiGui);
    priceLore(cmiGuiButton, editedWorthEnchantment);
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(CMIMaterial.CRAFTING_TABLE) {
          public void click(GUIClickType clickType) {
            setenchantworth.enchantWorthSubmenu(cmiGui.getPlayer(), enchantment);
          }
        };
    cmiGuiButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGuiButton.setSlot(cmiGui.getSlot(GUIButtonLocation.bottomRight));
    cmiGui.addButton(cmiGuiButton);
    updateLore(cmiGui, editedWorthEnchantment);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  private static void updateLore(CMIGui cmiGui, WorthEnchantment worthEnchantment) {
    if (cmiGui.getButtons().get(9) != null) {
      priceLore(((CMIGuiButton) cmiGui.getButtons().get(9)).clearLore(), worthEnchantment);
    }

    if (cmiGui.getButtons().get(10) != null) {
      priceLore(((CMIGuiButton) cmiGui.getButtons().get(10)).clearLore(), worthEnchantment);
    }

    if (cmiGui.getButtons().get(11) != null) {
      priceLore(((CMIGuiButton) cmiGui.getButtons().get(11)).clearLore(), worthEnchantment);
    }

    if (cmiGui.getButtons().get(13) != null) {
      priceLore(((CMIGuiButton) cmiGui.getButtons().get(13)).clearLore(), worthEnchantment);
    }
  }

  private static void priceLore(CMIGuiButton cmiGuiButton, WorthEnchantment worthEnchantment) {
    if (worthEnchantment != null) {
      Double sellPriceValue = worthEnchantment.getSellPrice();
      cmiGuiButton.addLore(
          CMILC.getIM(
              "setworth",
              "sellPrice",
              "[amount]",
              sellPriceValue == null
                  ? "-"
                  : CMI.getInstance().getEconomyManager().format(sellPriceValue)));
      cmiGuiButton.update();
    }
  }
}
