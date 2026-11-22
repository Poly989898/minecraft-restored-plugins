/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Items.CMIMaterial
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.Version.Version
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.damage.DamageSource
 *  org.bukkit.damage.DamageType
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.inventory.ItemStack
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class suicide implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}Goodbye cruel world...");
    configReader.get("message", "{gcs}[playerDisplayName] {gcp}took their own life");
    configReader.get("totem", "{gcp}Can't use while holding totem");
  }

  @Override
  @CAnnotation(
      priority = 110,
      info = "Kill your self",
      args = "[playerName] (-s)",
      tab = {"playername", "-s"},
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] itemStack) {
    Object object;
    boolean bl = false;
    Object targetName = null;
    String[] args = itemStack;
    int n = ((String[]) args).length;
    int argIndex = 0;
    while (argIndex < n) {
      object = args[argIndex];
      switch (((String) object).toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            targetName = object;
          }
      }
      ++argIndex;
    }
    object = cMI.getUser(commandSender, (String) targetName, this);
    if (object == null) {
      return null;
    }
    if (!((CMIUser) object).isOnline()) {
      LC.info_NotOnline.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    Snd snd = new Snd().setSender(commandSender).setTarget((CMIUser) object);
    EntityDamageEvent entityDamageEvent = null;
    if (Version.isCurrentEqualOrHigher((Version) Version.v1_20_R3)) {
      try {
        entityDamageEvent =
            new EntityDamageEvent(
                (Entity) ((CMIUser) object).getPlayer(),
                EntityDamageEvent.DamageCause.SUICIDE,
                DamageSource.builder((DamageType) DamageType.GENERIC).build(),
                32767.0);
      } catch (Throwable throwable) {
        entityDamageEvent =
            new EntityDamageEvent(
                (Entity) ((CMIUser) object).getPlayer(),
                EntityDamageEvent.DamageCause.SUICIDE,
                32767.0);
      }
    } else {
      entityDamageEvent =
          new EntityDamageEvent(
              (Entity) ((CMIUser) object).getPlayer(),
              EntityDamageEvent.DamageCause.SUICIDE,
              32767.0);
    }
    ((CMIUser) object).getPlayer().setLastDamageCause(entityDamageEvent);
    Bukkit.getServer().getPluginManager().callEvent((Event) entityDamageEvent);
    if (commandSender.getName().equals(((CMIUser) object).getName())) {
      ItemStack handItem = ((CMIUser) object).getItemInHand();
      if (handItem != null
          && CMIMaterial.get((ItemStack) handItem).equals((Object) CMIMaterial.TOTEM_OF_UNDYING)) {
        CMILC.info((Object) this, commandSender, "totem", snd);
        return null;
      }
      ItemStack offHandItem = ((CMIUser) object).getItemInOffHand();
      if (offHandItem != null
          && CMIMaterial.get((ItemStack) offHandItem)
              .equals((Object) CMIMaterial.TOTEM_OF_UNDYING)) {
        CMILC.info((Object) this, commandSender, "totem", snd);
        return null;
      }
    }
    if (!bl && !entityDamageEvent.isCancelled() && entityDamageEvent.getFinalDamage() > 0.0) {
      CMILC.info((Object) this, commandSender, "feedback", snd);
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        if (onlinePlayer.getName().equalsIgnoreCase(commandSender.getName())) continue;
        CMILC.info((Object) this, (CommandSender) onlinePlayer, "message", snd);
      }
    }
    if (((CMIUser) object).getPlayer().isOnline()) {
      ((CMIUser) object).getPlayer().damage(32767.0);
    }
    try {
      if (((CMIUser) object).getPlayer().getHealth() > 0.0
          && ((CMIUser) object).getPlayer().isOnline()) {
        ((CMIUser) object).getPlayer().setHealth(0.0);
      }
    } catch (Exception exception) {
      // empty catch block
    }
    return true;
  }
}
