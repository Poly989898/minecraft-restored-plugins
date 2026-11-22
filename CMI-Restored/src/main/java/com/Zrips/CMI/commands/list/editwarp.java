package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Warps.CmiWarp;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class editwarp implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "exists",
        "{gcw}Warp by this name ({gcs}[newName]{gcw}) already exists. Pick different one");
    configReader.get("updated", "{gcp}Updated name from {gcs}[oldName]{gcp} into {gcs}[newName]");
    configReader.get("placeItem", "{gcp}Place item to set custom icon");
    configReader.get("placeOffItem", "{gcp}Place item to set custom off icon");
    configReader.get("autoLore", "{gcp}Auto lore generation: [state]");
    configReader.get("group", "{gcp}Group: {gcs}[group]");
    configReader.get("newGroup", "{gcp}Enter new group name");
    configReader.get("displayName", "{gcp}DisplayName: {gcs}[displayName]");
    configReader.get("newDisplayName", "{gcp}Enter new display name");
    configReader.get("randomizeYaw", "{gcp}Randomize direction: [state]");
    configReader.get("permission", "{gcp}Requires permission to use: [state]");
    configReader.get("reqPermission", "{gcp}Show without permission: [state]");
    configReader.get("hidden", "{gcp}Hidden: [state]");
    configReader.get("primaryLocation", "{gcp}Primary location");
    configReader.get("secondaryLocation", "{gcp}Extra locations");
    configReader.get("uniqueLocation", "{gcp}Repeating locations: [state]");
    configReader.get("slot", "{gcp}Slot: {gcs}[slot]");
    configReader.get("page", "{gcp}Page: {gcs}[page]");
    configReader.get("overllaps", "{gcp}Overllaps with:");
    configReader.get("notSet", "{gcp}Not set");
  }

  @CAnnotation(
      info = "Edit warps",
      args = "(warpName) (newName)",
      tab = "playerwarps",
      regVar = {0, 1, 2},
      consoleVar = 2,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String newName = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string == null) {
        string = arg;
      } else {
        newName = arg;
      }
    }

    CmiWarp cmiWarp = cmi.getWarpManager().getWarp(string);
    if (cmiWarp == null) {
      return false;
    }

    if (commandSender instanceof Player
        && !cmiWarp.getCreator().equals(((Player) commandSender).getUniqueId())
        && !PermissionsManager.CMIPerm.command_$1_others.hasPermission(
            commandSender, editwarp.class.getSimpleName())) {
      LC.info_cantEdit.sendMessage(commandSender, new Object[0]);
      return null;
    }

    string = cmiWarp.getName();
    if (newName != null) {
      CmiWarp existingWarp = cmi.getWarpManager().getWarp(newName);
      if (existingWarp != null) {
        CMILC.info(this, commandSender, "exists", "[newName]", existingWarp.getName());
        return null;
      } else {
        cmi.getWarpManager().remove(cmiWarp);
        cmiWarp.setName(newName);
        cmi.getWarpManager().addWarp(cmiWarp);
        CMILC.info(
            this, commandSender, "updated", "[oldName]", string, "[newName]", cmiWarp.getName());
        return true;
      }
    } else {
      cmi.getWarpManager().openWarpEditor((Player) commandSender, cmiWarp);
      return true;
    }
  }
}
