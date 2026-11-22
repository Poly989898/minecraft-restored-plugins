package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIDamageCause;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class kill implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "cantKill",
        "{gcp}Can't kill, protected by another plugin, game/god mode. Use {gcs}-forced"
            + " {gcp}variable");
    configReader.get("lackOfAccess", "{gcp}Can't use force kill without permission.");
    configReader.get("killed", "{gcp}Killed {gcs}[playerDisplayName]");
  }

  @CAnnotation(
      info = "Kill player",
      args = "[playerName] (-forced) (damageCause) (-s) (-lightning)",
      multiTab =
          "[playerName] -forced,[damageCause],-s,-lightning -forced,-s,-lightning"
              + " -forced,-s,-lightning")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    boolean silent = false;
    boolean forced = false;
    String string = null;
    CMIDamageCause cmiDamageCause = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        silent = true;
      } else if (arg.equalsIgnoreCase("-lightning")) {
        flag = true;
      } else if (!arg.equalsIgnoreCase("-forced") && !arg.equalsIgnoreCase("-force")) {
        if (cmiDamageCause == null) {
          cmiDamageCause = CMIDamageCause.getByName(arg);
          if (cmiDamageCause != null) {
            continue;
          }
        }

        string = arg;
      } else {
        forced = true;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player != null && player.isOnline()) {
      if (cmiDamageCause == null) {
        cmiDamageCause =
            commandSender.getName().equals(player.getName())
                ? CMIDamageCause.SUICIDE
                : CMIDamageCause.CUSTOM;
      }

      if (cmiDamageCause.getCause() == null) {
        return false;
      }

      Object object = null;
      EntityDamageEvent entityDamageEvent;
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
        try {
          entityDamageEvent =
              new EntityDamageEvent(
                  player,
                  cmiDamageCause.getCause(),
                  DamageSource.builder(DamageType.GENERIC).build(),
                  32767.0);
        } catch (Throwable throwable) {
          entityDamageEvent = new EntityDamageEvent(player, cmiDamageCause.getCause(), 32767.0);
        }
      } else {
        entityDamageEvent = new EntityDamageEvent(player, cmiDamageCause.getCause(), 32767.0);
      }

      Bukkit.getPluginManager().callEvent(entityDamageEvent);
      if (entityDamageEvent.isCancelled()
          && (forced
                  && !PermissionsManager.CMIPerm.command_kill_byforce.hasPermission(commandSender)
              || !forced)) {
        if (forced
            && !PermissionsManager.CMIPerm.command_kill_byforce.hasPermission(
                commandSender, true)) {
          CMILC.info(this, commandSender, "lackOfAccess");
        } else {
          CMILC.info(this, commandSender, "cantKill");
        }

        return null;
      } else {
        this.process(player, entityDamageEvent, flag, silent, commandSender);
        return true;
      }
    } else {
      LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }

  private void process(
      Player player,
      EntityDamageEvent entityDamageEvent,
      boolean flag,
      boolean silent,
      CommandSender commandSender) {
    CMIScheduler.runAtEntity(
        CMI.getInstance(),
        player,
        () -> {
          player.setLastDamageCause(entityDamageEvent);
          player.setHealth(0.0);
          if (flag) {
            player.getWorld().strikeLightningEffect(player.getLocation());
          }

          if (!silent && !commandSender.getName().equalsIgnoreCase(player.getName())) {
            CMILC.info(this, commandSender, "killed", player);
          }
        });
  }
}
