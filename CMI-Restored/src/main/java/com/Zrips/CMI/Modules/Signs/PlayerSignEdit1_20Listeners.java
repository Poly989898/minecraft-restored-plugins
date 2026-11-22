package com.Zrips.CMI.Modules.Signs;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Dialogs.CMIDialog;
import com.Zrips.CMI.Modules.Dialogs.CMIDialogButton;
import com.Zrips.CMI.Modules.Dialogs.CMIDialogInputText;
import com.Zrips.CMI.Modules.Dialogs.CMIDialogInputValue;
import com.Zrips.CMI.Modules.Dialogs.CMIDialogItem;
import com.Zrips.CMI.Modules.Dialogs.CMIDialogText;
import com.Zrips.CMI.Modules.DynamicSigns.CMISign;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.InteractiveCommand.CMIInteractiveCommand;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.dsign;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.CMIMC;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.bukkit.event.player.PlayerSignOpenEvent.Cause;

public class PlayerSignEdit1_20Listeners implements Listener {
  static Set<UUID> signEditCache = new HashSet<>();

  public PlayerSignEdit1_20Listeners(CMI cmi) {}

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerSignOpenEvent(PlayerSignOpenEvent playerSignOpenEvent) {
    if (playerSignOpenEvent.getCause().equals(Cause.INTERACT)) {
      Player player = playerSignOpenEvent.getPlayer();
      if (shouldCancel(player, playerSignOpenEvent.getSign(), playerSignOpenEvent.getSide())) {
        playerSignOpenEvent.setCancelled(true);
      }
    }
  }

  public static boolean shouldCancel(Player player, Sign sign, Side side) {
    CMIUser cmiUser = CMIUser.getUser(player);
    if (cmiUser == null) {
      return false;
    }

    if (!PermissionsManager.CMIPerm.signs_shiftedit.hasPermission(player)
        || !cmiUser.getOptionState(PlayerOption.shiftSignEdit)) {
      CMIInteractiveCommand cmiInteractiveCommand =
          CMI.getInstance().getInteractiveCommandManager().getByLoc(sign.getLocation());
      if (cmiInteractiveCommand == null) {
        LC.info_cantEdit.sendMessage(player, new Object[0]);
      }

      return true;
    } else {
      if (CMI.getInstance().getSignManager().isSignEditRequireSneaking() && !player.isSneaking()) {
        return true;
      }

      SignSide signSide = sign.getSide(side);
      if (CMI.getInstance()
          .getConfigManager()
          .getElevatorIndicators()
          .contains(signSide.getLine(0).toLowerCase())) {
        LC.info_cantEdit.sendMessage(player, new Object[0]);
        return true;
      }

      if (CMI.getInstance()
          .getSignManager()
          .getSignEditBlackList()
          .contains(CMIChatColor.stripColor(signSide.getLine(0).toLowerCase()))) {
        LC.info_cantEdit.sendMessage(player, new Object[0]);
        return true;
      }

      boolean flag = false;
      if (CMIHookType.NoCheatPlus.isEnabled()) {
        if (NCPExemptionManager.isExempted(player, CheckType.BLOCKPLACE_AUTOSIGN)) {
          flag = true;
        }

        if (!flag) {
          signEditCache.add(player.getUniqueId());
          NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKPLACE_AUTOSIGN);
        }
      }

      CMISign cmiSign = CMI.getInstance().getSignManager().getByLoc(sign.getLocation());
      if (cmiSign != null) {
        if (PermissionsManager.CMIPerm.command_$1.hasPermission(player, "dsign")) {
          disableNCP(player, flag);
          CMI.getInstance()
              .performCommand(
                  player,
                  CommandsHandler.getLabel()
                      + " "
                      + dsign.class.getSimpleName()
                      + " "
                      + CMI.getInstance()
                          .getPlayerManager()
                          .convertLocToString(sign.getLocation()));
          return true;
        } else {
          LC.info_cantEdit.sendMessage(player, new Object[0]);
          return true;
        }
      } else {
        return false;
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerSignOpenEventEditor(PlayerSignOpenEvent playerSignOpenEvent) {
    if (playerSignOpenEvent.getCause().equals(Cause.INTERACT)) {
      Player player = playerSignOpenEvent.getPlayer();
      if (CMI.getInstance().getSignManager().isSignDialogEditor()) {
        openSignDialog(player, playerSignOpenEvent.getSign(), playerSignOpenEvent.getSide());
        playerSignOpenEvent.setCancelled(true);
      }
    }
  }

  public static void openSignDialog(Player player, Sign sign, Side side) {
    CMIDialog cmiDialog = new CMIDialog(player, CMILC.dialog_signEditor.getLocale());
    CMIMaterial cmiMaterial = CMIMaterial.get(sign.getBlock().getType());
    if (cmiMaterial.containsCriteria(CMIMC.WALLSIGN)) {
      cmiMaterial = CMIMaterial.get(cmiMaterial.toString().replace("_WALL", ""));
    }

    CMIDialogItem cmiDialogItem = new CMIDialogItem(cmiMaterial.newItemStack());
    cmiDialogItem.setText(new CMIDialogText(cmiMaterial.getTranslatedName()));
    cmiDialogItem.setTooltip(false);
    cmiDialogItem.setDecorations(false);
    cmiDialog.addItem(cmiDialogItem);
    SignSide signSide = sign.getSide(side);
    String[] strings = signSide.getLines();

    for (int value = 0; value < signSide.getLines().length; value++) {
      int lineIndex = value;
      CMIDialogInputText cmiDialogInputText =
          new CMIDialogInputText()
              .setInitialText(
                  new CMIDialogInputValue(CMIChatColor.deColorize(signSide.getLine(lineIndex))))
              .setMaxLength(new CMIDialogInputValue(256))
              .setMaxLines(new CMIDialogInputValue(5))
              .setHeight(new CMIDialogInputValue(36));
      cmiDialogInputText.setLabel(CMILC.dialog_line.getLocale("[line]", value + 1));
      cmiDialogInputText.setWidth(256);
      cmiDialog.addTextInput(
          cmiDialogInputText,
          string -> {
            string = CMITextCleaner.cleanFromColorCodes(player, string, CMIColorTypes.signs, true);
            signSide.setLine(lineIndex, CMIChatColor.translate(string));
          });
    }

    cmiDialog.addSaveButton(
        new CMIDialogButton(CMILC.dialog_update.getLocale()),
        ignored -> {
          SignChangeEvent signChangeEvent =
              new SignChangeEvent(sign.getBlock(), player, signSide.getLines(), side);
          Bukkit.getPluginManager().callEvent(signChangeEvent);
          if (!signChangeEvent.isCancelled()) {
            sign.update();
            openSignDialog(player, sign, side);
          } else {
            for (int slot = 0; slot < strings.length; slot++) {
              signSide.setLine(slot, strings[slot]);
            }

            cmiDialog.close(player);
          }
        });
    cmiDialog.addExitButton();
    cmiDialog.show(player);
  }

  private static void disableNCP(Player player, boolean flag) {
    if (CMIHookType.NoCheatPlus.isEnabled() && !flag) {
      NCPExemptionManager.unexempt(player, CheckType.BLOCKPLACE_AUTOSIGN);
    }
  }
}
