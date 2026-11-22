package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import com.Zrips.CMI.Modules.Selection.CMISelectionVisualizer;
import com.Zrips.CMI.Modules.Selection.SelectionManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMISelectionEvent;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class select implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Manage selection area",
      args = "(pos1/pos2/shift/expand/contract/clear) (amount)",
      multiTab = {"pos1,pos2,clear", "shift,expand,contract 1"},
      explanation = {},
      consoleVar = 666,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    select.Actions actions = null;
    Integer integer = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (actions == null) {
        actions = select.Actions.getByName(string);
        if (actions != null) {
          continue;
        }
      }

      if (integer == null) {
        try {
          integer = Integer.parseInt(string);
        } catch (Exception exception) {
        }
      }
    }

    if (actions == null) {
      actions = select.Actions.info;
    }

    Player player = (Player) commandSender;
    Location location = player.getLocation();
    switch (actions) {
      case pos1:
        cmi.getSelectionManager().hideSelection(player);
        cmi.getSelectionManager().placeLoc1(player, location, true);
        LC.Selection_PrimaryPoint.sendMessage(
            player,
            new Object[] {
              "[point]",
              LC.Selection_CoordsTop.getLocale(
                  new Object[] {location.getBlockX(), location.getBlockY(), location.getBlockZ()})
            });
        break;
      case pos2:
        cmi.getSelectionManager().hideSelection(player);
        cmi.getSelectionManager().placeLoc2(player, location, true);
        LC.Selection_SecondaryPoint.sendMessage(
            player,
            new Object[] {
              "[point]",
              LC.Selection_CoordsBottom.getLocale(
                  new Object[] {location.getBlockX(), location.getBlockY(), location.getBlockZ()})
            });
        break;
      case shift:
        if (!cmi.getSelectionManager().hasPlacedBoth(player) || integer == null) {
          return false;
        }

        CMISelectionVisualizer cmiSelectionVisualizer =
            SelectionManager.getPlayerSelection((Player) commandSender);
        cmiSelectionVisualizer.shift(integer);
        cmiSelectionVisualizer.showFull();
        break;
      case expand:
        if (!cmi.getSelectionManager().hasPlacedBoth(player) || integer == null) {
          return false;
        }

        CMISelectionVisualizer expandVisualizer =
            SelectionManager.getPlayerSelection((Player) commandSender);
        expandVisualizer.expand(integer);
        expandVisualizer.showFull();
        break;
      case contract:
        if (!cmi.getSelectionManager().hasPlacedBoth(player) || integer == null) {
          return false;
        }

        CMISelectionVisualizer contractVisualizer =
            SelectionManager.getPlayerSelection((Player) commandSender);
        contractVisualizer.contract(integer);
        contractVisualizer.showFull();
        break;
      case clear:
        cmi.getSelectionManager().hideSelection(player);
        cmi.getSelectionManager().clearSelection(player);
        break;
      case info:
        if (!cmi.getSelectionManager().hasPlacedBoth(player)) {
          return false;
        }
        break;
      default:
        return null;
    }

    CMISelectionVisualizer selectionVisualizer =
        SelectionManager.getPlayerSelection((Player) commandSender);
    if (selectionVisualizer.placedBothPoints()) {
      CuboidArea cuboidArea =
          new CuboidArea(
              selectionVisualizer.getSelectedCuboid().getWorld(),
              selectionVisualizer.getSelectedCuboid().getLowPoint(),
              selectionVisualizer.getSelectedCuboid().getHighPoint());
      CMISelectionEvent cmiSelectionEvent = new CMISelectionEvent(player, cuboidArea);
      Bukkit.getPluginManager().callEvent(cmiSelectionEvent);
    }

    return true;
  }

  private enum Actions {
    pos1,
    pos2,
    shift,
    expand,
    contract,
    clear,
    info;

    public static select.Actions getByName(String string) {
      select.Actions[] actionses;
      int value = (actionses = values()).length;

      for (int index = 0; index < value; index++) {
        select.Actions actions = actionses[index];
        if (actions.name().equalsIgnoreCase(string)) {
          return actions;
        }
      }

      return null;
    }
  }
}
