package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Vanish.VanishAction;
import com.Zrips.CMI.Modules.Vanish.VanishActionState;
import com.Zrips.CMI.Modules.Vanish.VanishActionStateBoolean;
import com.Zrips.CMI.Modules.Vanish.VanishActionStateInt;
import com.Zrips.CMI.Modules.Vanish.VanishManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class vanishedit implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    VanishAction[] vanishActions;
    int value = (vanishActions = VanishAction.values()).length;

    for (int count = 0; count < value; count++) {
      VanishAction vanishAction = vanishActions[count];
      vanishAction.setDesc(configReader.get(vanishAction.toString(), vanishAction.getDesc()));
    }

    configReader.get("bossbarTitle", "{gcp}Vanished");
    configReader.get("modified", "{gcp}Set {gcs}[type] {gcp}to {gcs}[state]");
  }

  @CAnnotation(
      info = "Edit vanish mode for player",
      args = "(playerName) (action) (true/false)",
      tab = {"playername", "vanishAction", "true%%false"},
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = 666,
      modules = "vanish",
      others = true)
  @Override
  public Boolean perform(final CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    final Player player = (Player) commandSender;
    VanishAction vanishAction = null;
    Boolean flag = null;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String text = parts[count];
      if (vanishAction == null) {
        vanishAction = VanishAction.get(text);
        if (vanishAction != null) {
          continue;
        }
      }

      if (text.equalsIgnoreCase("true")) {
        flag = true;
      } else if (text.equalsIgnoreCase("false")) {
        flag = false;
      } else {
        string = text;
      }
    }

    final CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    if (vanishAction == null) {
      final CMIGui cmiGui = new CMIGui(player);
      cmiGui.addLock(InvType.Gui);
      cmiGui.setTitle(cmiUser.getName());
      VanishAction[] vanishActions;
      int countSecond = (vanishActions = VanishAction.values()).length;

      for (int countThird = 0; countThird < countSecond; countThird++) {
        final VanishAction vanishActionValue = vanishActions[countThird];
        CMIGuiButton cmiGuiButton =
            new CMIGuiButton(
                vanishActionValue.getSlot(),
                VanishManager.is(cmiUser.getUniqueId(), vanishActionValue)
                    ? CMIMaterial.GREEN_WOOL
                    : CMIMaterial.RED_WOOL) {
              public void click(GUIClickType guiClickType) {
                VanishActionState vanishActionState =
                    cmiUser.getVanish().getState(vanishActionValue);
                if (vanishActionState instanceof VanishActionStateBoolean) {
                  cmiUser
                      .getVanish()
                      .set(vanishActionValue, !cmiUser.getVanish().is(vanishActionValue));
                } else if (vanishActionState instanceof VanishActionStateInt) {
                  int countFourth = ((VanishActionStateInt) vanishActionState).getValue();
                  countFourth = countFourth == 0 ? 1 : countFourth + 1;
                  int countFifth =
                      CMINumber.clamp(
                          PermissionsManager.CMIPerm.vanishlevel_$1
                              .getPermissionInfo(player, "")
                              .getMaxValue(1),
                          1,
                          vanishActionValue.getMaxValue());
                  countFourth = countFourth > countFifth ? 1 : countFourth;
                  cmiUser.getVanish().set(vanishActionValue, countFourth);
                }

                if (vanishActionValue.equals(VanishAction.isVanished)
                    || vanishActionValue.equals(VanishAction.level)) {
                  cmiUser.setVanished(cmiUser.getVanish().is(vanishActionValue));
                } else if (vanishActionValue.equals(VanishAction.PrivateMessages)) {
                  CMI.getInstance().getUtilManager().removeMessageReplyTo(cmiUser.getName());
                } else {
                  cmiUser.updateVanishMode();
                }

                cmi.getVanishManager().recheckMobSpawningCheck();
                this.updateLooks();
                this.update(cmiGui);
                cmiUser.addForDelayedSave();
              }

              public void updateLooks() {
                this.setItem(
                    VanishManager.is(cmiUser.getUniqueId(), vanishActionValue)
                        ? CMIMaterial.GREEN_WOOL.newItemStack()
                        : CMIMaterial.RED_WOOL.newItemStack());
                this.setName(CMILC.getIM(vanishedit.class, vanishActionValue.name()));
                this.clearLore();
                VanishActionState vanishActionStateValue =
                    VanishManager.getState(cmiUser.getUniqueId(), vanishActionValue);
                if (vanishActionStateValue instanceof VanishActionStateBoolean) {
                  this.addLore(
                      VanishManager.is(cmiUser.getUniqueId(), vanishActionValue)
                          ? LC.info_variables_True.getLocale(new Object[0])
                          : LC.info_variables_False.getLocale(new Object[0]));
                } else if (vanishActionStateValue instanceof VanishActionStateInt) {
                  int countSixth = ((VanishActionStateInt) vanishActionStateValue).getValue();
                  this.addLore(CMIChatColor.GRAY + String.valueOf(countSixth));
                }
              }
            };
        cmiGui.addButton(cmiGuiButton);
      }

      cmiGui.fillEmptyButtons();
      cmiGui.open();
      return true;
    } else {
      if (vanishAction.equals(VanishAction.isVanished)) {
        cmiUser.setVanished(flag == null ? !cmiUser.getVanish().is(vanishAction) : flag);
      } else {
        cmiUser
            .getVanish()
            .set(vanishAction, flag == null ? !cmiUser.getVanish().is(vanishAction) : flag);
      }

      cmi.getVanishManager().applyVanish(cmiUser);
      CMILC.info(
          this,
          commandSender,
          "modified",
          "[type]",
          vanishAction.toString(),
          "[state]",
          cmiUser.getVanish().is(vanishAction));
      return true;
    }
  }
}
