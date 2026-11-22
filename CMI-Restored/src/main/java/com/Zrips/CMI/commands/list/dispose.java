package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.AttachedCommands.CustomNBTManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.Util;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.NBT.CMINBT;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class dispose implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("title", "{gcw}Place all items you don't need");
    configReader.get("cant", "{gcw}You can't dispose this item");
  }

  @CAnnotation(
      info = "Dispose of unneeded items",
      args = "(playerName)",
      tab = "playerName",
      regVar = {0, 1},
      consoleVar = 1,
      customAlias = "trash",
      alias = true,
      others = true)
  @Override
  public Boolean perform(final CMI cmi, final CommandSender commandSender, String[] strings) {
    String string = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      string = arg;
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player != null && player.isOnline()) {
      String guiTitle = CMILC.getIM(this, "title");
      if (guiTitle.length() > 32) {
        guiTitle = guiTitle.substring(0, 31) + "~";
      }

      CMIGui cmiGui =
          new CMIGui(player) {
            public boolean click(int slot, GUIClickType guiClickType, ItemStack itemStack) {
              if (itemStack == null) {
                return true;
              }

              if ((cmi.getConfigManager().DisposeCustomModelData
                      || Util.getCustomModelData(itemStack) == null)
                  && (cmi.getConfigManager().DisposeAttachedCommands
                      || new CMINBT(itemStack)
                              .getList(CustomNBTManager.CustomNBTType.NBTCommands.name())
                          == null)
                  && (cmi.getConfigManager().DisposeMaterials.isEmpty()
                      || !cmi.getConfigManager()
                          .DisposeMaterials
                          .contains(CMIMaterial.get(itemStack)))) {
                return true;
              }

              CMILC.info(dispose.class.getSimpleName(), commandSender, "cant");
              return false;
            }
          };
      cmiGui.setInvSize(cmi.getConfigManager().DisposeUILines * 9);
      cmiGui.setTitle(guiTitle);
      cmiGui.setAllowShift(true);

      try {
        cmiGui.setAllowMoveAll(true);
        cmiGui.setAllowPickUpAll(true);
      } catch (Throwable throwable) {
      }

      cmiGui.open();
      return true;
    } else {
      return null;
    }
  }
}
