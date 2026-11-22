package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIPotionEffectType;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class effect implements Cmd {
  HashMap<UUID, effect.freezeTimer> freezeSched = new HashMap<>();

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}Potion effect {gcs}[effect] {gcp}added to {gcs}[playerDisplayName]{gcp} for"
            + " {gcs}[sec]sec{gcp} with power of {gcs}[power].");
    configReader.get(
        "cleared", "{gcp}Potion effects have been cleared for {gcs}[playerDisplayName]&e.");
  }

  @CAnnotation(
      priority = 115,
      info = "Adds potion effect to player. use clear to remove all effects",
      args = "[playername/all] [effect/clear] (duration/infinite) (multiplier) (-s) (-visual)",
      multiTab = "[playername],clear [potioneffect],freeze,clear 60,infinite 1",
      explanation = {
        "-visual will add visible bubbles and icon on top right corner",
        "Examples:",
        "/cmi effect zrips nightvision 60 1 - will give 60 sec night vision for Zrips",
        "/cmi effect zrips nightvision +10 - will add 10 sec to current night vision time",
        "/cmi effect zrips nightvision -10 - will take out 10 sec from current night vision time",
        "/cmi effect all health_boost 60 1 - will boost hp by 1 for everyone online"
      },
      regVar = {1, 2, 3, 4, 5, 6},
      consoleVar = {1, 2, 3, 4, 5, 6},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    PotionEffectType potionEffectType = null;
    int value = 0;
    effect.durationAction action = effect.durationAction.def;
    int seconds = 0;
    boolean flag = false;
    boolean applyToAll = false;
    String string = null;
    boolean removeMode = false;
    boolean silentMode = false;
    boolean hideEffects = false;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("clear")) {
        flag = true;
      } else if (arg.equalsIgnoreCase("all")) {
        hideEffects = true;
      } else if (arg.equalsIgnoreCase("-visual")) {
        silentMode = true;
      } else if (arg.equalsIgnoreCase("freeze")) {
        applyToAll = true;
      } else if (arg.equalsIgnoreCase("-s")) {
        removeMode = true;
      } else {
        if (potionEffectType == null && !applyToAll) {
          potionEffectType = CMIPotionEffectType.get(arg);
          if (potionEffectType != null) {
            continue;
          }
        }

        if (string == null) {
          CMIUser cmiUser = cmi.getPlayerManager().getUserByAproxName(arg);
          if (cmiUser != null) {
            string = cmiUser.getName();
            continue;
          }
        }

        if (value == 0) {
          if (arg.equalsIgnoreCase("infinite")) {
            value = 107374182;
          }

          try {
            if (arg.startsWith("+")) {
              value = Integer.parseInt(arg.replace("+", ""));
              action = effect.durationAction.add;
            } else if (arg.startsWith("-")) {
              value = Integer.parseInt(arg.replace("-", ""));
              action = effect.durationAction.take;
            } else {
              value = Integer.parseInt(arg);
            }
            continue;
          } catch (NumberFormatException numberFormatException) {
          }
        }

        if (value != 0 && seconds == 0) {
          try {
            seconds = Integer.parseInt(arg);
            continue;
          } catch (NumberFormatException numberFormatException2) {
          }
        }

        try {
          Integer.parseInt(arg);
        } catch (NumberFormatException numberFormatException3) {
        }
      }
    }

    if (potionEffectType == null && !flag && !applyToAll) {
      return false;
    }

    if (value == 0 && !flag) {
      value = 60;
    }

    if (--seconds < 0) {
      seconds = 0;
    }

    ArrayList<Player> arrayList = new ArrayList();
    if (hideEffects) {
      if (PermissionsManager.CMIPerm.command_$1_others.hasPermission(
          commandSender, this.getClass().getSimpleName())) {
        for (Player player : Bukkit.getOnlinePlayers()) {
          arrayList.add(player);
        }
      }
    } else {
      Player targetPlayer = cmi.getTarget(commandSender, string, this);
      if (targetPlayer == null) {
        return null;
      }

      arrayList.add(targetPlayer);
    }

    for (Player listedPlayer : arrayList) {
      Snd snd = new Snd().setSender(commandSender).setTarget(listedPlayer);
      PotionEffect potionEffect =
          new PotionEffect(
              potionEffectType == null ? PotionEffectType.ABSORPTION : potionEffectType,
              value * 20,
              seconds,
              true,
              silentMode);
      switch (action) {
        case add:
          if (Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
            try {
              Player currentPlayer = listedPlayer;
              Collection<PotionEffect> collection =
                  (Collection<PotionEffect>)
                      currentPlayer
                          .getClass()
                          .getMethod("getActivePotionEffects")
                          .invoke(currentPlayer);
              PotionEffect foundEffect = null;

              for (PotionEffect candidateEffect : collection) {
                if (candidateEffect.getType() == potionEffectType) {
                  foundEffect = candidateEffect;
                  break;
                }
              }

              if (foundEffect != null) {
                if (potionEffect.getAmplifier() != foundEffect.getAmplifier()) {
                  return null;
                }

                potionEffect =
                    new PotionEffect(
                        potionEffectType == null ? PotionEffectType.ABSORPTION : potionEffectType,
                        CMINumber.clamp(
                            value * 20 + foundEffect.getDuration(), 1, Integer.MAX_VALUE),
                        foundEffect.getAmplifier(),
                        true,
                        silentMode);
              }
            } catch (Exception | Error value5) {
              value5.printStackTrace();
            }
          } else if (applyToAll) {
            if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
              int freezeTicks = listedPlayer.getFreezeTicks();
              listedPlayer.setFreezeTicks(freezeTicks + value * 20);
            }
          } else {
            PotionEffect freezeEffect =
                listedPlayer.getPotionEffect(
                    potionEffectType == null ? PotionEffectType.ABSORPTION : potionEffectType);
            if (freezeEffect != null) {
              if (potionEffect.getAmplifier() != freezeEffect.getAmplifier()) {
                return null;
              }

              potionEffect =
                  new PotionEffect(
                      potionEffectType == null ? PotionEffectType.ABSORPTION : potionEffectType,
                      CMINumber.clamp(
                          value * 20 + freezeEffect.getDuration(), 1, Integer.MAX_VALUE),
                      freezeEffect.getAmplifier(),
                      true,
                      silentMode);
            }
          }
          break;
        case take:
          if (Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
            try {
              Player playerCopy = listedPlayer;
              Collection<PotionEffect> effects =
                  (Collection<PotionEffect>)
                      playerCopy.getClass().getMethod("getActivePotionEffects").invoke(playerCopy);
              PotionEffect existingEffect = null;

              for (PotionEffect effectEntry : effects) {
                if (effectEntry.getType() == potionEffectType) {
                  existingEffect = effectEntry;
                  break;
                }
              }

              if (existingEffect == null) {
                return null;
              }

              int remainingTicks = existingEffect.getDuration() - value * 20;
              if (remainingTicks < 1) {
                flag = true;
              } else {
                if (potionEffect.getAmplifier() != existingEffect.getAmplifier()) {
                  return null;
                }

                potionEffect =
                    new PotionEffect(
                        potionEffectType == null ? PotionEffectType.ABSORPTION : potionEffectType,
                        remainingTicks,
                        existingEffect.getAmplifier(),
                        true,
                        silentMode);
              }
            } catch (Exception | Error value8) {
              value8.printStackTrace();
            }
          } else if (applyToAll) {
            if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
              int freezeTicksCopy = listedPlayer.getFreezeTicks();
              freezeTicksCopy -= value * 20;
              freezeTicksCopy = freezeTicksCopy < 0 ? 0 : freezeTicksCopy;
              listedPlayer.setFreezeTicks(freezeTicksCopy);
            }
          } else {
            PotionEffect updatedEffect =
                listedPlayer.getPotionEffect(
                    potionEffectType == null ? PotionEffectType.ABSORPTION : potionEffectType);
            if (updatedEffect == null) {
              return null;
            }

            int updatedTicks = updatedEffect.getDuration() - value * 20;
            if (updatedTicks < 1) {
              flag = true;
            } else {
              if (potionEffect.getAmplifier() != updatedEffect.getAmplifier()) {
                return null;
              }

              potionEffect =
                  new PotionEffect(
                      potionEffectType == null ? PotionEffectType.ABSORPTION : potionEffectType,
                      updatedTicks,
                      updatedEffect.getAmplifier(),
                      true,
                      silentMode);
            }
          }
        case def:
      }

      if (!flag) {
        if (applyToAll) {
          if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
            this.freezePlayer(listedPlayer, value * 20);
          }
        } else if (listedPlayer.isOnline()) {
          listedPlayer.removePotionEffect(potionEffect.getType());
          listedPlayer.addPotionEffect(potionEffect);
        } else {
          cmi.getNMS().setPotionEffects(listedPlayer, potionEffect);
        }

        if (!removeMode && !hideEffects) {
          CMILC.info(
              this,
              commandSender,
              "feedback",
              snd,
              "[effect]",
              applyToAll
                  ? "freeze"
                  : CMIPotionEffectType.get(potionEffect.getType()).getTranslatedName(),
              "[sec]",
              potionEffect.getDuration() / 20,
              "[power]",
              potionEffect.getAmplifier() + 1);
        }
      } else {
        if (listedPlayer.isOnline()) {
          for (PotionEffect activeEffect : listedPlayer.getActivePotionEffects()) {
            listedPlayer.removePotionEffect(activeEffect.getType());
            if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
              listedPlayer.setFreezeTicks(0);
            }
          }

          if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
            listedPlayer.setFreezeTicks(0);
          }
        } else {
          cmi.getNMS().clearPotionEffects(listedPlayer);
        }

        if (!removeMode && !hideEffects) {
          CMILC.info(this, commandSender, "cleared", snd);
        }
      }
    }

    return true;
  }

  private void freezePlayer(Player player, long longValue) {
    effect.freezeTimer existingTimer = this.freezeSched.remove(player.getUniqueId());
    if (existingTimer != null) {
      existingTimer.getTask().cancel();
    }

    effect.freezeTimer newTimer = new effect.freezeTimer(longValue, null);
    UUID uuid = player.getUniqueId();
    this.freezeSched.put(
        player.getUniqueId(),
        newTimer.setTask(
            CMIScheduler.scheduleSyncRepeatingTask(
                CMI.getInstance(),
                () -> {
                  if (newTimer.getUntil() < System.currentTimeMillis()) {
                    newTimer.getTask().cancel();
                  } else if (player.isOnline()) {
                    int value = player.getFreezeTicks();
                    int maxFreeze = player.getMaxFreezeTicks();
                    if (maxFreeze > value) {
                      player.setFreezeTicks(value + 10);
                    }

                    if (player.getFreezeTicks() >= player.getMaxFreezeTicks()
                        && newTimer.getUntil() >= System.currentTimeMillis()) {
                      int addedTicks =
                          (int) ((newTimer.getUntil() - System.currentTimeMillis()) / 50L);
                      int totalFreeze = player.getFreezeTicks() + addedTicks;
                      if (totalFreeze < 0) {
                        totalFreeze = Integer.MAX_VALUE;
                      }

                      player.setFreezeTicks(totalFreeze);
                      this.freezeSched.remove(uuid);
                      newTimer.getTask().cancel();
                    }
                  }
                },
                0L,
                1L)));
  }

  private enum durationAction {
    add,
    take,
    def;
  }

  class freezeTimer {
    private long until;
    private CMITask task;

    public freezeTimer(long longValue, CMITask cmiTask) {
      this.until = System.currentTimeMillis() + longValue * 50L;
      this.task = cmiTask;
    }

    public CMITask getTask() {
      return this.task;
    }

    public effect.freezeTimer setTask(CMITask cmiTask) {
      this.task = cmiTask;
      return this;
    }

    public long getUntil() {
      return this.until;
    }

    public void setUntil(long longValue) {
      this.until = longValue;
    }
  }
}
