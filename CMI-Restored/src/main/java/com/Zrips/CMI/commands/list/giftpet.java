/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Entities.CMIEntityType
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.AnimalTamer
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Tameable
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.UUID;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

public class giftpet implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("notTameable", "{gcp}This entity isin't tameable");
    configReader.get("notTamed", "{gcp}This entity isin't tamed");
    configReader.get("notOwner", "{gcp}You are not owner of this entity");
    configReader.get(
        "gifted", "{gcp}You gifted {gcs}[type] {gcp}entity {gcp}to {gcs}[playerDisplayName]");
    configReader.get(
        "received", "{gcp}You received {gcs}[type] {gcp}entity {gcp}from {gcs}[sourceDisplayName]");
  }

  @Override
  @CAnnotation(
      info = "Gift your tamed pet to another player",
      args = "[playerName] (-s)",
      tab = {"playername", "-s"},
      regVar = {1, 2},
      consoleVar = {666},
      others = false)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] args) {
    String targetName = null;
    boolean bl = false;
    for (String arg : args) {
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            targetName = arg;
          }
      }
    }
    Entity entity = cMI.getUtilManager().getTargetEntity((Player) commandSender);
    if (entity == null) {
      LC.info_lookAtEntity.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    if (!(entity instanceof Tameable)) {
      CMILC.info((Object) this, commandSender, "notTameable", new Object[0]);
      return null;
    }
    CMIUser cMIUser = CMIUser.getUser((Player) commandSender);
    CMIUser targetUser = cMI.getUser(commandSender, targetName, this, false);
    if (targetUser == null) {
      return null;
    }
    Tameable tameable = (Tameable) entity;
    boolean hasBypass =
        PermissionsManager.CMIPerm.command_giftpet_bypass.hasPermission(commandSender);
    if (!tameable.isTamed() && !hasBypass) {
      CMILC.info((Object) this, commandSender, "notTamed", new Object[0]);
      return null;
    }
    CMIUser owner = null;
    if (tameable.isTamed()) {
      try {
        UUID ownerId = tameable.getOwner().getUniqueId();
        owner = cMI.getPlayerManager().getUser(ownerId);
      } catch (Exception exception) {
        owner = cMI.getPlayerManager().getUser(tameable.getOwner().getName());
      }
    }
    if (!(hasBypass || owner != null && owner.equals(cMIUser))) {
      CMILC.info((Object) this, commandSender, "notOwner", new Object[0]);
      return null;
    }
    Snd snd = new Snd();
    snd.setTarget(targetUser);
    snd.setSender(cMIUser);
    snd.setSource(cMIUser);
    if (!hasBypass && !targetUser.getOptionState(PlayerOption.receivePets).booleanValue()) {
      LC.info_cantDoForPlayer.sendMessage((Object) commandSender, new Object[] {snd});
      return null;
    }
    tameable.setOwner((AnimalTamer) targetUser.getOfflinePlayer());
    if (!bl) {
      CMILC.info(
          (Object) this,
          commandSender,
          "gifted",
          snd,
          "[type]",
          CMIEntityType.getRealNameByType((EntityType) tameable.getType()));
      if (targetUser.isOnline()) {
        CMILC.info(
            (Object) this,
            targetUser,
            "received",
            snd,
            "[type]",
            CMIEntityType.getRealNameByType((EntityType) tameable.getType()));
      }
    }
    return true;
  }
}
