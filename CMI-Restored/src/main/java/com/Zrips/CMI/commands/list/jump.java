package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.Modules.Worlds.CMIWorld;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class jump implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "tooFar", "{gcd}No block in sight or it's too far (max {gcs}[max] {gcd}blocks)");
  }

  @CAnnotation(info = "Jump to target block", regVar = 0, consoleVar = 666, others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    PermissionInfo permissionInfo =
        cmi.getPermissionsManager()
            .getPermissionInfo(player, PermissionsManager.CMIPerm.command_jump_$1, 1000L);
    int value =
        permissionInfo != null && permissionInfo.getMaxValue() != null
            ? permissionInfo.getMaxValue().intValue()
            : cmi.getTeleportManager().getJumpDefaultDistance();
    Block block = cmi.getUtilManager().getTargetBlock(player, value, false);
    if (block != null && !block.getType().equals(Material.AIR)) {
      var completableFuture =
          cmi.getTeleportHandler()
              .getSafeLocation(
                  player, block.getLocation().clone().add(0.5, 0.0, 0.5), value, 1, false);
      completableFuture.thenAccept(
          object -> {
            Location location = object.getLocation();
            if (location != null && object.isGood()) {
              location.setPitch(player.getLocation().getPitch());
              location.setYaw(player.getLocation().getYaw());
              CMIWorld cmiWorld = new CMIWorld(location.getWorld());
              if (!cmiWorld.insideWorldBorder(location)) {
                CMILC.Information_BadArea.sendMessage(commandSender);
              } else {
                cmi.getTeleportHandler()
                    .teleportPlayer(player, location, false, CMITeleportType.Jump)
                    .thenAccept(cmiTeleportFeedback -> {});
              }
            } else {
              if (!object.isGood()) {
                LC.info_nolocation.sendMessage(commandSender, new Object[0]);
              } else {
                CMILC.info(this, commandSender, "tooFar", "[max]", value);
              }
            }
          });
      return true;
    } else {
      CMILC.info(this, commandSender, "tooFar", "[max]", value);
      return null;
    }
  }
}
