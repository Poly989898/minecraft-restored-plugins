package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Anvil.CMIAnvilGUI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.MinecraftPlatform;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class anvil implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}You have opened anvil for {gcs}[playerDisplayName]{gcp}.");
    configReader.get("targetfeedback", "{gcs}[senderDisplayName]{gcp} opened anvil for you.");
  }

  @CAnnotation(
      priority = 121,
      info = "Open anvil",
      args = "(playerName) (-s)",
      tab = {"playername", "%%-s"},
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentEqualOrHigher(Version.v1_18_R1) && !Version.isPaperBranch()) {
      LC.info_spigotNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (Version.isCurrentEqualOrLower(Version.v1_12_R1)
        && Version.getPlatform().equals(MinecraftPlatform.mohist)) {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    String string = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        flag = true;
      } else {
        string = arg;
      }
    }

    final Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    if (!player.isOnline()) {
      LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (!flag) {
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      CMILC.info(this, commandSender, "feedback", snd);
      if (!player.getName().equalsIgnoreCase(commandSender.getName())) {
        CMILC.info(this, player, "targetfeedback", snd);
      }
    }

    CMIAnvilGUI cmiAnvilGUI =
        new CMIAnvilGUI(
            player,
            new CMIAnvilGUI.AnvilClickEventHandler() {
              @Override
              public void onAnvilClick(CMIAnvilGUI.AnvilClickEvent anvilClickEvent) {}

              @Override
              public void onAnvilClose(InventoryCloseEvent inventoryCloseEvent) {
                ItemStack[] itemStacks = inventoryCloseEvent.getInventory().getContents();
                inventoryCloseEvent.getInventory().clear();
                CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);

                for (int index = 0; index < itemStacks.length; index++) {
                  ItemStack itemStack = itemStacks[index];
                  if (itemStack != null && !CMIMaterial.isAir(itemStack.getType())) {
                    cmiUser
                        .getInventory()
                        .addItem(itemStack, CMIPlayerInventory.CMIFullInventoryAction.Drop);
                  }
                }
              }
            });
    if (PermissionsManager.CMIPerm.anvil_colors.hasPermission(commandSender)) {
      cmiAnvilGUI.setColorRename(true);
    }

    cmiAnvilGUI.open();
    return true;
  }
}
