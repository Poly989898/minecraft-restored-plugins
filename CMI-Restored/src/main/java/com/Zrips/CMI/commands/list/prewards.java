package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayTimeRewards.PTROneTime;
import com.Zrips.CMI.Modules.PlayTimeRewards.PTRRepeat;
import com.Zrips.CMI.Modules.PlayTimeRewards.PlayTimeRewardsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;

public class prewards implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", "{gcs}[place]{gcp}. {gcs}[rewardName] {gcp}reward in {gcs}[time]");
    configReader.get("listNoStack", "{gcs}[place]{gcp}. {gcs}[rewardName]");
    configReader.get("listOneTime", "{gcs}[place]{gcp}. {gcs}[rewardName]");
    configReader.get("claim", " {gcu}Claim!");
    configReader.get("claimAmount", " {gcu}Claim(x[amount])!");
    configReader.get("claimHover", "{gcu}Click to Claim reward!");
    configReader.get(
        "inform", "!actionbar!{gcu}You have unclaimed rewards waiting for you! {gcs}/prewards");
  }

  @CAnnotation(
      priority = 48,
      info = "Check playtime rewards",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (!cmi.getPlayTimeRewardManager().isEnabled()) {
      LC.info_FeatureNotEnabled.sendMessage(commandSender, new Object[0]);
      return null;
    }

    String string = null;
    String playerName = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (string == null) {
        string = arg;
      } else if (arg.startsWith("-c:")) {
        playerName = arg.substring("-c:".length());
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    long longValue = cmiUser.getTotalPlayTime(false);
    if (playerName != null) {
      if (!cmiUser.getName().equals(commandSender.getName())
          && !PermissionsManager.CMIPerm.command_$1_others_$2.hasPermission(
              commandSender, true, "prewards", "claim")) {
        return null;
      }

      PTROneTime ptrOneTime = cmi.getPlayTimeRewardManager().getOneTimeRewardByName(playerName);
      PTRRepeat ptrRepeat = cmi.getPlayTimeRewardManager().getRepeatingRewardByName(playerName);
      if ((ptrOneTime != null || ptrRepeat != null) && cmiUser.isOnline()) {
        if (ptrOneTime != null) {
          if (cmiUser.isClaimedReward(ptrOneTime) || ptrOneTime.getPayFor() > longValue) {
            return false;
          }

          ptrOneTime.performCommands(cmiUser.getPlayer());
          cmiUser.addOneTimeReward(ptrOneTime.getName());
          cmi.getDbManager().addForPlayTimeRewardSave(cmiUser);
        } else if (ptrRepeat != null) {
          if (ptrRepeat.isAutoClaim()) {
            return false;
          }

          Long repeatTime = cmiUser.getRepeatableRewardTime(ptrRepeat.getName());
          if (repeatTime == null) {
            cmiUser.addRepeatableReward(ptrRepeat.getName(), longValue);
            return true;
          }

          long elapsed = 0L;
          if (cmi.getPlayTimeRewardManager().isExcludeAfk() && cmiUser.isAfk()) {
            Long lastActionTime = cmi.getAfkManager().getLastAction(cmiUser);
            if (lastActionTime != null) {
              Long afkRewardTime = cmi.getPlayTimeRewardManager().getAfkAction(cmiUser);
              if (afkRewardTime != null) {
                elapsed = System.currentTimeMillis() - afkRewardTime;
              } else {
                elapsed = System.currentTimeMillis() - lastActionTime;
              }

              cmi.getPlayTimeRewardManager().updateAfkAction(cmiUser);
            } else if (cmiUser.isAfk()) {
              cmi.getAfkManager().updateUser(cmiUser);
            }
          }

          if (cmi.getPlayTimeRewardManager().isExcludeAfk() && cmiUser.isAfk()) {
            repeatTime = repeatTime + elapsed;
            if (repeatTime > longValue) {
              repeatTime = longValue;
            }

            cmiUser.addRepeatableReward(ptrRepeat.getName(), repeatTime);
          }

          long sinceRepeat = longValue - repeatTime;
          int payCount = (int) (sinceRepeat / ptrRepeat.getPayEvery().doubleValue());
          if (payCount < 1) {
            return false;
          }

          long payInterval = ptrRepeat.getPayEvery();
          if (!ptrRepeat.isStackRewards()) {
            cmiUser.addRepeatableReward(ptrRepeat.getName(), longValue);
          } else {
            payInterval =
                repeatTime + payInterval > longValue ? longValue : repeatTime + payInterval;
            cmiUser.addRepeatableReward(ptrRepeat.getName(), payInterval);
          }

          cmi.getDbManager().addForPlayTimeRewardSave(cmiUser);
          ptrRepeat.performCommands(cmiUser.getPlayer());
        }
      }
    }

    int repeatCount = 0;
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    long totalPlayTime = cmiUser.getTotalPlayTime(false);
    long playtimeRaw = 0L;
    if (cmi.getPlayTimeRewardManager().isExcludeAfk() && cmiUser.isAfk()) {
      Long lastActionAlt = cmi.getAfkManager().getLastAction(cmiUser);
      if (lastActionAlt != null) {
        Long afkRewardTimeAlt = cmi.getPlayTimeRewardManager().getAfkAction(cmiUser);
        if (afkRewardTimeAlt != null) {
          playtimeRaw = System.currentTimeMillis() - afkRewardTimeAlt;
        } else {
          playtimeRaw = System.currentTimeMillis() - lastActionAlt;
        }

        cmi.getPlayTimeRewardManager().updateAfkAction(cmiUser);
      } else if (cmiUser.isAfk()) {
        cmi.getAfkManager().updateUser(cmiUser);
      }
    }

    for (var entry : cmi.getPlayTimeRewardManager().getRepeatableRewards().entrySet()) {
      Long repeatTimeAlt = cmiUser.getRepeatableRewardTime((String) entry.getKey());
      if (!PlayTimeRewardsManager.RequiresPermission
          || PermissionsManager.CMIPerm.prewards_$1.hasPermission(
              commandSender, ((String) entry.getKey()).toLowerCase())) {
        if (repeatTimeAlt == null) {
          cmiUser
              .getRepeatableRewards()
              .put(((PTRRepeat) entry.getValue()).getName().toLowerCase(), longValue);
          repeatTimeAlt = longValue;
        }

        repeatCount++;
        RawMessage rawMessage = new RawMessage();
        PTRRepeat repeat = (PTRRepeat) entry.getValue();
        Long repeatTimeNext = cmiUser.getRepeatableRewardTime(repeat.getName());
        if (repeatTimeNext == null) {
          cmiUser.addRepeatableReward(repeat.getName(), totalPlayTime);
        } else {
          if (cmi.getPlayTimeRewardManager().isExcludeAfk() && cmiUser.isAfk()) {
            repeatTimeNext = repeatTimeNext + playtimeRaw;
            if (repeatTimeNext > longValue) {
              repeatTimeNext = longValue;
            }

            cmiUser.addRepeatableReward(repeat.getName(), repeatTimeNext);
          }

          long sinceRepeatNext = totalPlayTime - repeatTimeNext;
          int payCountNext = (int) (sinceRepeatNext / repeat.getPayEvery().doubleValue());
          long payIntervalNext =
              repeat.getPayEvery()
                  - (totalPlayTime - (repeatTimeNext + payCountNext * repeat.getPayEvery()));
          if (!((PTRRepeat) entry.getValue()).isAutoClaim() && payCountNext >= 1) {
            if (((PTRRepeat) entry.getValue()).isStackRewards()) {
              rawMessage
                  .addText(
                      CMILC.getIM(
                          this,
                          "list",
                          "[place]",
                          repeatCount,
                          "[rewardName]",
                          ((PTRRepeat) entry.getValue()).getDisplayName(),
                          "[time]",
                          CMITimeManager.to24hourShort(payIntervalNext)))
                  .addHover(((PTRRepeat) entry.getValue()).getDescription());
            } else {
              rawMessage
                  .addText(
                      CMILC.getIM(
                          this,
                          "listNoStack",
                          "[place]",
                          repeatCount,
                          "[rewardName]",
                          ((PTRRepeat) entry.getValue()).getDisplayName(),
                          "[time]",
                          CMITimeManager.to24hourShort(payIntervalNext)))
                  .addHover(((PTRRepeat) entry.getValue()).getDescription());
            }

            if (payCountNext > 1 && ((PTRRepeat) entry.getValue()).isStackRewards()) {
              rawMessage
                  .addText(CMILC.getIM(this, "claimAmount", "[amount]", payCountNext))
                  .addHover(CMILC.getIM(this, "claimHover"))
                  .addCommand(
                      CommandsHandler.getLabel()
                          + " "
                          + this.getClass().getSimpleName()
                          + " "
                          + cmiUser.getName()
                          + " "
                          + "-c:"
                          + (String) entry.getKey());
            } else if (payCountNext > 0) {
              rawMessage
                  .addText(CMILC.getIM(this, "claim"))
                  .addHover(CMILC.getIM(this, "claimHover"))
                  .addCommand(
                      CommandsHandler.getLabel()
                          + " "
                          + this.getClass().getSimpleName()
                          + " "
                          + cmiUser.getName()
                          + " "
                          + "-c:"
                          + (String) entry.getKey());
            }
          } else {
            rawMessage
                .addText(
                    CMILC.getIM(
                        this,
                        "list",
                        "[place]",
                        repeatCount,
                        "[rewardName]",
                        ((PTRRepeat) entry.getValue()).getDisplayName(),
                        "[time]",
                        CMITimeManager.to24hourShort(payIntervalNext)))
                .addHover(((PTRRepeat) entry.getValue()).getDescription());
          }

          rawMessage.show(commandSender);
        }
      }
    }

    int oneTimeCount = 0;

    for (var oneTimeEntry : cmi.getPlayTimeRewardManager().getOneTimeRewards().entrySet()) {
      if (!cmiUser.isClaimedReward((PTROneTime) oneTimeEntry.getValue())
          && (!PlayTimeRewardsManager.RequiresPermission
              || PermissionsManager.CMIPerm.prewards_$1.hasPermission(
                  commandSender, ((PTROneTime) oneTimeEntry.getValue()).getName().toLowerCase()))) {
        repeatCount++;
        RawMessage message = new RawMessage();
        if ((Long) oneTimeEntry.getKey() - longValue > 0L) {
          if (oneTimeCount < cmi.getPlayTimeRewardManager().getOneTimeAmount()) {
            message
                .addText(
                    CMILC.getIM(
                        this,
                        "list",
                        "[place]",
                        repeatCount,
                        "[rewardName]",
                        ((PTROneTime) oneTimeEntry.getValue()).getDisplayName(),
                        "[time]",
                        CMITimeManager.to24hourShort((Long) oneTimeEntry.getKey() - longValue)))
                .addHover(((PTROneTime) oneTimeEntry.getValue()).getDescription());
          }

          oneTimeCount++;
        } else if (!((PTROneTime) oneTimeEntry.getValue()).isAutoClaim()
            || ((PTROneTime) oneTimeEntry.getValue()).isAutoClaim()
                && !cmiUser.isClaimedReward((PTROneTime) oneTimeEntry.getValue())) {
          message
              .addText(
                  CMILC.getIM(
                      this,
                      "listOneTime",
                      "[place]",
                      repeatCount,
                      "[rewardName]",
                      ((PTROneTime) oneTimeEntry.getValue()).getDisplayName(),
                      "[time]",
                      CMITimeManager.to24hourShort((Long) oneTimeEntry.getKey() - longValue)))
              .addHover(((PTROneTime) oneTimeEntry.getValue()).getDescription());
          message
              .addText(CMILC.getIM(this, "claim"))
              .addHover(CMILC.getIM(this, "claimHover"))
              .addCommand(
                  CommandsHandler.getLabel()
                      + " "
                      + this.getClass().getSimpleName()
                      + " "
                      + cmiUser.getName()
                      + " "
                      + "-c:"
                      + ((PTROneTime) oneTimeEntry.getValue()).getName());
        }

        message.show(commandSender);
      }
    }

    if (repeatCount == 0 && oneTimeCount == 0) {
      LC.info_nothingToShow.sendMessage(commandSender, new Object[0]);
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    return true;
  }
}
