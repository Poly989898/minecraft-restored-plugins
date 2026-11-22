/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Entities.CMIEntityType
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Sittable
 *  org.bukkit.entity.Tameable
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.UUID;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;

public class releasepet implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("released", "{gcp}You have released {gcs}[type] {gcp}into wilderness!");
  }

  @Override
  @CAnnotation(
      info = "Release your tamed pet",
      args = "(-s)",
      tab = {"-s"},
      regVar = {0, 1},
      consoleVar = {666},
      others = false)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] args) {
    boolean bl = false;
    for (String arg : args) {
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
          }
      }
    }
    Entity entity = cMI.getUtilManager().getTargetEntity((Player) commandSender);
    if (entity == null) {
      LC.info_lookAtEntity.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    if (!(entity instanceof Tameable)) {
      CMILC.info(giftpet.class, commandSender, "notTameable", new Object[0]);
      return null;
    }
    CMIUser cMIUser = CMIUser.getUser((Player) commandSender);
    Tameable tameable = (Tameable) entity;
    if (!tameable.isTamed()) {
      CMILC.info(giftpet.class, commandSender, "notTamed", new Object[0]);
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
    if (!(PermissionsManager.CMIPerm.command_releasepet_bypass.hasPermission(commandSender)
        || owner != null && owner.equals(cMIUser))) {
      CMILC.info(giftpet.class, commandSender, "notOwner", new Object[0]);
      return null;
    }
    tameable.setOwner(null);
    if (tameable instanceof Sittable sittable && sittable.isSitting()) {
      sittable.setSitting(false);
    }
    if (!bl) {
      CMILC.info(
          (Object) this,
          commandSender,
          "released",
          "[type]",
          CMIEntityType.getRealNameByType((EntityType) tameable.getType()));
    }
    return true;
  }
}
