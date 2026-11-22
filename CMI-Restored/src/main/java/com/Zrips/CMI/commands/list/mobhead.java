package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerCombat.EntityHeadDrop;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Entities.CMIEntitySubType;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Entities.MobHeadInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mobhead implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("got", "{gcp}Got {gcs}[type] {gcp}mob head");
    configReader.get("base", "{gcp}Base drop chance: {gcs}[chance]%");
    configReader.get("personal", "{gcp}Drop chance for you: {gcs}[chance]%");
    configReader.get("headSpecific", "{gcp}Head specific drop chance: {gcs}[chance]%");
    configReader.get("criteria", "{gcp}Criteria: {gcs}[criteria]");
    configReader.get("title", "{gcs}[current]{gcp}/{gcs}[total]");
  }

  @CAnnotation(
      priority = 250,
      info = "Get mob head",
      args = "[mobType] (entryNumber) (playerName) (-s)",
      tab = {"mobtype", "playername"},
      explanation = {},
      regVar = {0, 1, 2, 3, 4, 5},
      consoleVar = {2, 3, 4},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (strings.length != 0 && !strings[0].startsWith("-p:")) {
      CMIEntityType cmiEntityType = null;
      String string = null;
      boolean flag = false;
      Integer integer = null;
      String[] parts = strings;
      int value = strings.length;

      for (int countSecond = 0; countSecond < value; countSecond++) {
        String text = parts[countSecond];
        if (integer == null) {
          try {
            integer = Integer.parseInt(text);
            continue;
          } catch (Exception exception) {
          }
        }

        if (cmiEntityType == null) {
          cmiEntityType = CMIEntityType.get(text);
          if (cmiEntityType != null) {
            continue;
          }
        }

        String textSecond;
        switch ((textSecond = text.toLowerCase()).hashCode()) {
          case 1510:
            if (textSecond.equals("-s")) {
              if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
                flag = true;
              }
              break;
            }
          default:
            string = text;
        }
      }

      CMIUser cmiUser = cmi.getUser(commandSender, string, this);
      if (cmiUser == null) {
        return null;
      }

      if (cmiEntityType == null) {
        LC.info_cantDetermineMobType.sendMessage(
            commandSender, new Object[] {"[type]", strings[0]});
        return null;
      }

      ItemStack itemStack = cmiEntityType.getHead(integer);
      if (itemStack == null) {
        return false;
      }

      if (!flag) {
        CMILC.info(this, cmiUser, "got", "[type]", cmiEntityType.getName());
      }

      cmiUser.getInventory().addItem(itemStack);
      return true;
    } else {
      Integer count = 1;
      if (strings.length > 0) {
        try {
          count = Integer.parseInt(strings[0].substring("-p:".length()));
        } catch (Exception exception2) {
          return false;
        }
      }

      this.openGui((Player) commandSender, count);
      return true;
    }
  }

  private void openGui(final Player player, int value) {
    CMIGui cmiGui =
        new CMIGui(player) {
          public void pageChange(int count) {
            mobhead.this.openGui(player, count);
          }
        };
    var hashMap = CMI.getInstance().getPlayerCombatManager().getEntityHeadDropChance();
    TreeMap<String, EntityType> treeMap = new TreeMap();

    for (EntityType entityType : hashMap.keySet()) {
      treeMap.put(entityType.toString(), entityType);
    }

    int countSecond = 0;

    for (Entry entry : hashMap.entrySet()) {
      countSecond += ((EntityHeadDrop) entry.getValue()).getHeads().size();
    }

    final CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
    PageInfo pageInfo = new PageInfo(countSecond > 54 ? 45 : 54, countSecond, value);

    breakSecond:
    for (Entry entrySecond : treeMap.entrySet()) {
      EntityHeadDrop entityHeadDrop = (EntityHeadDrop) hashMap.get(entrySecond.getValue());
      if (entityHeadDrop != null) {
        for (var varSecond : entityHeadDrop.getHeads().entrySet()) {
          if (!pageInfo.isContinue()) {
            if (pageInfo.isBreak()) {
              break breakSecond;
            }

            final ItemStack itemStack =
                entityHeadDrop.getType().getHead((String) varSecond.getKey());
            CMIItemStack cmiItemStack = new CMIItemStack(itemStack.clone());
            cmiItemStack.addLore(
                CMILC.getIM(
                    this,
                    "base",
                    "[chance]",
                    CMI.getInstance()
                        .getPlayerCombatManager()
                        .getEntityDropHeadPercentage((EntityType) entrySecond.getValue())));
            cmiItemStack.addLore(
                CMILC.getIM(
                    this,
                    "personal",
                    "[chance]",
                    cmiUser.getEntityHeadDropChance((EntityType) entrySecond.getValue(), true)));
            cmiItemStack.addLore(
                CMILC.getIM(this, "headSpecific", "[chance]", varSecond.getValue()));
            MobHeadInfo mobHeadInfo =
                (MobHeadInfo) entityHeadDrop.getType().getHeadTexturesMap().get(varSecond.getKey());
            if (!mobHeadInfo.getCriterias().isEmpty()) {
              String string = "";

              for (CMIEntitySubType cmiEntitySubType : mobHeadInfo.getCriterias()) {
                if (!string.isEmpty()) {
                  string = string + ", ";
                }

                string = string + CMIText.firstToUpperCase(cmiEntitySubType.toString());
              }

              cmiItemStack.addLore(CMILC.getIM(this, "criteria", "[criteria]", string));
            }

            CMIGuiButton cmiGuiButton =
                new CMIGuiButton(cmiItemStack.getItemStack().clone()) {
                  public void click(GUIClickType guiClickType) {
                    cmiUser.getInventory().addItem(itemStack.clone());
                  }
                };
            cmiGui.addButton(cmiGuiButton);
          }
        }
      }
    }

    cmiGui.setTitle(
        CMILC.getIM(
            mobhead.class.getSimpleName(),
            "title",
            "[current]",
            pageInfo.getCurrentPage(),
            "[total]",
            pageInfo.getTotalPages()));
    cmiGui.addPagination(pageInfo);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
    cmiUser.getPlayer().updateInventory();
  }
}
