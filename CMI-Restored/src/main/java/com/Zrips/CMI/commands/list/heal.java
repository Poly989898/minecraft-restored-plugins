package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Particl.ParticleManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIPlayer;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class heal implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}You have healed {gcs}[playerDisplayName]{gcp}.");
    configReader.get("feedbackMulti", "{gcp}You have healed {gcs}[count] {gcp}players.");
    configReader.get(
        "targetfeedback", "{gcp}You have been healed by {gcs}[senderDisplayName]{gcp}.");
  }

  @CAnnotation(
      info = "Heal player",
      args =
          "[playerName/all] (healamount/healpercent) (-nofeed) (-ignoreffects) (-dontextinguish)",
      tab = {
        "playername%%all",
        "!-nofeed%%!-ignoreffects%%!-dontextinguish",
        "!-nofeed%%!-ignoreffects%%!-dontextinguish",
        "!-nofeed%%!-ignoreffects%%!-dontextinguish"
      },
      regVar = {0, 1, 2, 3, 4, 5, 6},
      consoleVar = {1, 2, 3, 4, 5, 6},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    String string = null;
    boolean silent = false;
    int value = 0;
    boolean healFood = true;
    boolean clearEffects = true;
    boolean resetFire = true;
    ArrayList<Player> arrayList = new ArrayList();
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        flag = true;
      } else if (arg.equalsIgnoreCase("-nofeed")
          || arg.equalsIgnoreCase("-nofeeding")
          || arg.equalsIgnoreCase("-dontfeed")) {
        healFood = false;
      } else if (arg.equalsIgnoreCase("-ignoreffects")) {
        clearEffects = false;
      } else if (arg.equalsIgnoreCase("-dontextinguish")) {
        resetFire = false;
      } else {
        if (string == null) {
          Player player = CMIUser.getOnlinePlayer(commandSender, arg, true);
          if (player != null) {
            string = arg;
            continue;
          }
        }

        label181:
        if (value != 0) {
          string = arg;
        } else if (arg.contains("%")) {
          try {
            value = Integer.parseInt(arg.replace("%", ""));
            silent = true;
          } catch (NumberFormatException numberFormatException) {
          }
        } else {
          try {
            value = Integer.parseInt(arg);
            if (value > 10000) {
              value = 0;
            }
          } catch (NumberFormatException numberFormatException2) {
            break label181;
          }
        }
      }
    }

    if (string != null && (string.equalsIgnoreCase("*") || string.equalsIgnoreCase("all"))) {
      if (!PermissionsManager.CMIPerm.command_heal_all.hasPermission(commandSender, true)) {
        return null;
      }

      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        if (commandSender instanceof Player) {
          if (((Player) commandSender).canSee(onlinePlayer)) {
            arrayList.add(onlinePlayer);
          }
        } else {
          arrayList.add(onlinePlayer);
        }
      }
    } else {
      Player target = cmi.getTarget(commandSender, string, this);
      if (target == null) {
        return null;
      }

      arrayList.add(target);
    }

    if (arrayList.isEmpty()) {
      return null;
    }

    value = CMINumber.abs(value);

    for (Player healTarget : arrayList) {
      double number = CMIPlayer.getMaxHealth(healTarget);
      double clamped = CMINumber.clamp(number, 0.1, Integer.MAX_VALUE);
      double currentHealth = healTarget.getHealth();
      if (!(currentHealth <= 0.0)) {
        if (value != 0) {
          if (silent) {
            value = (int) (clamped * value / 100.0);
          }

          healTarget.setHealth(CMINumber.clamp(currentHealth + value, 0.1, clamped));
        } else {
          healTarget.setHealth(clamped);
        }

        if (resetFire) {
          healTarget.setFireTicks(0);
        }

        if (healFood) {
          if (healTarget.getFoodLevel() < 20) {
            healTarget.setFoodLevel(20);
          }

          if (healTarget.getSaturation() < 10.0F) {
            healTarget.setSaturation(10.0F);
          }
        }

        if (clearEffects) {
          for (PotionEffect potionEffect : healTarget.getActivePotionEffects()) {
            if (potionEffect != null && potionEffect.getType() != null) {
              for (String effectName : cmi.getConfigManager().RemoveNegativeEffects) {
                if (potionEffect.getType().getName().equalsIgnoreCase(effectName)) {
                  healTarget.removePotionEffect(potionEffect.getType());
                }
              }
            }
          }
        }

        cmi.save(healTarget);
        if (ParticleManager.CMIPresetAnimations.Healing.isEnabled()) {
          ParticleManager.CMIPresetAnimations.Healing.show(healTarget);
        }

        Snd snd = new Snd().setSender(commandSender).setTarget(healTarget);
        if (arrayList.size() == 1 && !flag) {
          CMILC.info(this, commandSender, "feedback", snd);
        }

        if (!healTarget.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
          CMILC.info(this, healTarget, "targetfeedback", snd);
        }

        cmi.getNMS().unloadData(healTarget);
      }
    }

    if (arrayList.size() > 1 && !flag) {
      CMILC.info(this, commandSender, "feedbackMulti", "[count]", arrayList.size());
    }

    return true;
  }
}
