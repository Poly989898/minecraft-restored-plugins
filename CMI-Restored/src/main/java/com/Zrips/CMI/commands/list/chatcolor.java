package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Chat.CMIPlayerMessageColor;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.LinkedHashSet;
import java.util.Set;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class chatcolor implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("reset", "{gcp}Reset");
  }

  @CAnnotation(
      info = "Pick chat color",
      args = "(playerName)",
      tab = "playername",
      others = true,
      consoleVar = 666)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string == null) {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player != null && player.isOnline()) {
      this.colorPicker((Player) commandSender, CMIUser.getUser(player), 1);
      return true;
    } else {
      return false;
    }
  }

  private Set<CMIPlayerMessageColor> getValidColors(Player player) {
    LinkedHashSet<CMIPlayerMessageColor> linkedHashSet = new LinkedHashSet();

    for (CMIPlayerMessageColor cmiPlayerMessageColor :
        CMI.getInstance().getChatManager().getChatColors()) {
      String string =
          CMITextCleaner.cleanFromColorCodes(
              player,
              "" + cmiPlayerMessageColor.getColor(),
              CMIColorTypes.publicmessage,
              true,
              true);
      if (!string.isEmpty()) {
        linkedHashSet.add(cmiPlayerMessageColor);
      }
    }

    return linkedHashSet;
  }

  private void colorPicker(final Player player, final CMIUser cmiUser, final int value) {
    CMIGui cmiGui =
        new CMIGui(player) {
          public void pageChange(int page) {
            chatcolor.this.colorPicker(player, cmiUser, page);
          }

          public void onClose() {
            cmiUser.addForDelayedSave();
          }
        };
    cmiGui.setTitle(cmiUser.getDisplayName());
    cmiGui.addLock(InvType.Main);
    var set = this.getValidColors(player);
    PageInfo pageInfo = new PageInfo(set.size() > 45 ? 36 : 45, set.size(), value);
    CMIItemStack cmiItemStack = CMIMaterial.LEATHER_CHESTPLATE.newCMIItemStack();
    cmiItemStack.setDisplayName(CMILC.getIM(chatcolor.class, "reset"));
    ItemStack itemStack = cmiItemStack.getItemStack();
    LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
    leatherArmorMeta.setColor(
        cmiUser.getChatMessageFormat() == null
            ? CMIChatColor.WHITE.getRGBColor()
            : cmiUser.getChatMessageFormat().getColor().getRGBColor());
    itemStack.setItemMeta(leatherArmorMeta);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(4, itemStack) {
          public void click(GUIClickType guiClickType) {
            cmiUser.setChatMessageFormat(null);
            chatcolor.this.colorPicker(player, cmiUser, value);
          }
        };
    cmiGuiButton.hideItemFlags();
    cmiGui.addButton(cmiGuiButton);

    for (int slot = 0; slot < 9; slot++) {
      if (slot != 4) {
        cmiGuiButton =
            new CMIGuiButton(slot, CMILib.getInstance().getConfigManager().getGUIEmptyField());
        cmiGui.addButton(cmiGuiButton);
      }
    }

    for (final CMIPlayerMessageColor cmiPlayerMessageColor : set) {
      if (!pageInfo.isContinue()) {
        if (pageInfo.isBreak()) {
          break;
        }

        cmiItemStack = CMIMaterial.LEATHER_CHESTPLATE.newCMIItemStack();
        CMIChatColor cmiChatColor = cmiPlayerMessageColor.getColor();
        String object = cmiPlayerMessageColor.formatsToString();
        CMIChatColor resolvedColor =
            CMIChatColor.getClosest(
                cmiChatColor.getHex() != null
                    ? cmiChatColor.getHex()
                    : cmiChatColor.getBukkitColorCode());
        if (resolvedColor == null) {
          resolvedColor = CMIChatColor.getByCustomName(cmiChatColor.getCleanName());
        }

        cmiItemStack.setDisplayName(
            cmiPlayerMessageColor
                + object
                + (resolvedColor != null
                    ? CMIText.firstToUpperCase(resolvedColor.getName().replace("_", " "))
                    : cmiChatColor.getName().replace("_", " ")));
        itemStack = cmiItemStack.getItemStack();
        leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        leatherArmorMeta.setColor(cmiChatColor.getRGBColor());
        itemStack.setItemMeta(leatherArmorMeta);
        cmiGuiButton =
            new CMIGuiButton(itemStack) {
              public void click(GUIClickType clickType) {
                cmiUser.setChatMessageFormat(cmiPlayerMessageColor);
                chatcolor.this.colorPicker(player, cmiUser, value);
              }
            };
        cmiGuiButton.hideItemFlags();
        cmiGui.addButton(cmiGuiButton);
      }
    }

    cmiGui.addPagination(pageInfo);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }
}
