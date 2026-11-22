package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class itemframe implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcs}[count] {gcp}item frames are invisible around you");
  }

  @CAnnotation(
      info = "Manage item frames",
      args = "(invisible/fixed/invulnerable/all)",
      tab = "invisible%%fixed%%invulnerable%%all",
      regVar = {0, 1, 2},
      consoleVar = 666)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    if (strings.length == 0) {
      var list = player.getNearbyEntities(20.0, 20.0, 20.0);
      int value = 0;

      for (Entity entity : list) {
        if (entity.getType() == EntityType.ITEM_FRAME
            || entity.getType().toString().equals("GLOW_ITEM_FRAME")) {
          ItemFrame itemFrame = (ItemFrame) entity;
          if (!itemFrame.isVisible()) {
            BlockFace blockFace = itemFrame.getAttachedFace();
            Location location = entity.getLocation().clone();
            switch (blockFace) {
              case UP:
              case DOWN:
                location.add(0.0, 0.0, -0.39);
                break;
              default:
                location.add(0.0, -0.39, 0.0);
            }

            value++;
            CMIScheduler.runTaskAsynchronously(cmi, () -> {});
          }
        }
      }

      CMILC.info(this, commandSender, "feedback", "[count]", value);
      return true;
    } else {
      Entity targetEntity = CMI.getInstance().getUtilManager().getTargetEntity(player, 0.0);
      if (targetEntity != null
          && (targetEntity.getType() == EntityType.ITEM_FRAME
              || targetEntity.getType().toString().equals("GLOW_ITEM_FRAME"))) {
        itemframe.Action action = itemframe.Action.getByName(strings[0]);
        if (action == null) {
          return false;
        }

        PlayerInteractEntityEvent playerInteractEntityEvent =
            new PlayerInteractEntityEvent(player, targetEntity);
        Bukkit.getPluginManager().callEvent(playerInteractEntityEvent);
        if (playerInteractEntityEvent.isCancelled()) {
          LC.info_cantEdit.sendMessage(commandSender, new Object[0]);
          return null;
        }

        if (!PermissionsManager.CMIPerm.command_itemframe_$1.hasPermission(
            commandSender, true, action.toString())) {
          return null;
        }

        if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
          ItemFrame frame = (ItemFrame) targetEntity;
          switch (action) {
            case Invisible:
              frame.setVisible(!frame.isVisible());
              break;
            case Fixed:
              frame.setFixed(!frame.isFixed());
              break;
            case Invulnerable:
              frame.setInvulnerable(!frame.isInvulnerable());
              break;
            case All:
              frame.setVisible(!frame.isVisible());
              frame.setFixed(!frame.isFixed());
              frame.setInvulnerable(!frame.isInvulnerable());
          }

          return true;
        } else {
          if (action.equals(itemframe.Action.All)) {
            CMINBT cminbt = new CMINBT(targetEntity);
            Short flagValue = cminbt.getShort(itemframe.Action.Invisible.toString());
            if (flagValue == null) {
              flagValue = (short) 0;
            }

            cmi.getNMS()
                .setEntityTag(
                    targetEntity,
                    itemframe.Action.Invisible.toString(),
                    Byte.valueOf((byte) (flagValue == 1 ? 0 : 1)));
            flagValue = cminbt.getShort(itemframe.Action.Fixed.toString());
            if (flagValue == null) {
              flagValue = (short) 0;
            }

            cmi.getNMS()
                .setEntityTag(
                    targetEntity,
                    itemframe.Action.Fixed.toString(),
                    Byte.valueOf((byte) (flagValue == 1 ? 0 : 1)));
            flagValue = cminbt.getShort(itemframe.Action.Invulnerable.toString());
            if (flagValue == null) {
              flagValue = (short) 0;
            }

            cmi.getNMS()
                .setEntityTag(
                    targetEntity,
                    itemframe.Action.Invulnerable.toString(),
                    Byte.valueOf((byte) (flagValue == 1 ? 0 : 1)));
            CMILC.info(
                this,
                player,
                flagValue == 1
                    ? LC.modify_disabled.getLocale(new Object[0])
                    : LC.modify_enabled.getLocale(new Object[0]));
          } else {
            Short actionValue = new CMINBT(targetEntity).getShort(action.toString());
            if (actionValue == null) {
              actionValue = (short) 0;
            }

            cmi.getNMS()
                .setEntityTag(
                    targetEntity,
                    action.toString(),
                    Byte.valueOf((byte) (actionValue == 1 ? 0 : 1)));
            CMILC.info(
                this,
                player,
                actionValue == 1
                    ? LC.modify_disabled.getLocale(new Object[0])
                    : LC.modify_enabled.getLocale(new Object[0]));
          }

          return true;
        }
      } else {
        return false;
      }
    }
  }

  private enum Action {
    Invisible,
    Fixed,
    Invulnerable,
    All;

    public static itemframe.Action getByName(String string) {
      itemframe.Action[] actions;
      int value = (actions = values()).length;

      for (int index = 0; index < value; index++) {
        itemframe.Action action = actions[index];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
