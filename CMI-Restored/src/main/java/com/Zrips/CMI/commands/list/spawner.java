package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEvent;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

public class spawner implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}Spawner is set to {gcs}[type] {gcp}type");
    configReader.get("wrongEntityIdName", "{gcw}Wrong entity id or name");
    configReader.get("cantSet", "{gcw}Can't set this type spawner");
    configReader.get("cantDetect", "{gcw}Can't detect spawner block");
    configReader.get("pickOne", "{gcp}Pick spawner type");
  }

  @CAnnotation(
      priority = 100,
      info = "Sets spawner",
      args = "[EntityType]",
      tab = "EntityType",
      regVar = 1,
      consoleVar = 666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    EntityType entityType = null;
    if (strings.length > 0) {
      EntityType[] entityTypes;
      int value = (entityTypes = EntityType.values()).length;

      for (int index = 0; index < value; index++) {
        EntityType candidateType = entityTypes[index];
        if (candidateType.name().equalsIgnoreCase(strings[0])) {
          entityType = candidateType;
          break;
        }
      }
    }

    Integer integer = null;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String string = args[argIndex];
      if (string.startsWith("-p:")) {
        try {
          integer = Integer.parseInt(string.substring("-p:".length()));
        } catch (Exception exception) {
        }
      }
    }

    if (integer != null) {
      cmi.getSpawnerChargesManager().openSpawnerPickerUI(player, integer);
      return null;
    }

    if (entityType == null) {
      return false;
    }

    if (!PermissionsManager.CMIPerm.command_spawner_$1.hasPermission(
        commandSender, true, entityType.name().toLowerCase())) {
      return null;
    }

    Block block = cmi.getUtilManager().getTargetBlock(player, 10);
    if (block != null
        && (CMIMaterial.SPAWNER.equals(block.getState().getType())
            || CMIMaterial.TRIAL_SPAWNER.equals(block.getState().getType()))) {
      BlockPlaceEvent blockPlaceEvent = CMIEvent.placeBlockEvent(block, player);
      Bukkit.getServer().getPluginManager().callEvent(blockPlaceEvent);
      if (blockPlaceEvent.isCancelled()) {
        return null;
      }

      blockPlaceEvent.setCancelled(true);
      if (cmi.getSpawnerChargesManager().changeSpawnerType(block, entityType)) {
        CMILC.info(this, commandSender, "feedback", "[type]", entityType.name().toLowerCase());
      } else {
        CMILC.info(this, commandSender, "cantSet");
      }

      return true;
    } else {
      CMILC.info(this, commandSender, "cantDetect");
      return null;
    }
  }
}
