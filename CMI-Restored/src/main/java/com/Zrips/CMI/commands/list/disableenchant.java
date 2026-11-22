package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class disableenchant implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("title", "Enchant disabler");
    configReader.get("feedback", "{gcs}[enchantName] {gcp}has been {gcs}[state]");
  }

  @CAnnotation(
      info = "Disable enchantment",
      args = "(enchant/id) (disable/enable)",
      tab = {"enchant", "disable%%enable"},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (strings.length == 0) {
      Player player = (Player) commandSender;
      CMIGui cmiGui = new CMIGui(player);
      cmiGui.addLock(InvType.Gui);
      cmiGui.addLock(InvType.Main);
      cmiGui.setTitle(CMILC.getIM(this, "title"));
      int value = 0;
      Enchantment[] enchantments;
      int enchantmentCount = (enchantments = CMIEnchantment.values()).length;

      for (int index = 0; index < enchantmentCount; index++) {
        Enchantment enchantment = enchantments[index];
        if (enchantment != null && enchantment.getName() != null) {
          boolean flag = CMIEnchantment.isEnabled(enchantment);
          cmiGui.addButton(
              new CMIGuiButton(
                      value,
                      flag ? CMIMaterial.GREEN_WOOL : CMIMaterial.RED_WOOL,
                      "&r&7"
                          + CMIText.everyFirstToUpperCase(CMIEnchantment.getName(enchantment))
                              .replace("_", " "))
                  .addCommand(
                      "cmi disableenchant "
                          + enchantment.getName()
                          + " "
                          + (flag ? "disable" : "enable"))
                  .addCommand("cmi disableenchant"));
          value++;
        }
      }

      cmiGui.autoResize();
      cmiGui.open();
      return true;
    } else {
      Enchantment targetEnchantment = null;
      boolean enabled = true;
      String[] args = strings;
      int argCount = strings.length;

      for (int argIndex = 0; argIndex < argCount; argIndex++) {
        String string = args[argIndex];
        if (string.equalsIgnoreCase("enable")) {
          enabled = false;
        } else if (string.equalsIgnoreCase("disable")) {
          enabled = true;
        } else if (targetEnchantment == null) {
          targetEnchantment = CMIEnchantment.get(string);
        }
      }

      if (targetEnchantment == null) {
        CMILC.info("enchant", commandSender, "inccorectId");
        return true;
      }

      cmi.getReflectionManager().manageEnchantment(targetEnchantment, enabled);
      if (enabled) {
        cmi.getEnchantManager().addDisabled(targetEnchantment);
      } else {
        cmi.getEnchantManager().removeDisabled(targetEnchantment);
      }

      boolean notRegistered = true;
      Enchantment[] allEnchantments;
      int allCount = (allEnchantments = Enchantment.values()).length;

      for (int allIndex = 0; allIndex < allCount; allIndex++) {
        Enchantment candidate = allEnchantments[allIndex];
        if (candidate.getName() != null
            && targetEnchantment.getName() != null
            && candidate.equals(targetEnchantment)) {
          notRegistered = false;
        }
      }

      CMILC.info(
          this,
          commandSender,
          "feedback",
          "[enchantName]",
          CMIEnchantment.getName(targetEnchantment),
          "[state]",
          notRegistered ? LC.info_variables_Disabled : LC.info_variables_Enabled);
      cmi.getEnchantManager().updateConfig();
      return true;
    }
  }
}
