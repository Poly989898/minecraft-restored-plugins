package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashSet;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class afkcheck implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcs}[playerDisplayName]{gcp}: {gcs}[status]");
    configReader.get("feedbackInactive", "{gcp}Inactive for past {gcs}[time]");
    configReader.get(
        "feedbackAll", "{gcs}[place]{gcp}. {gcs}[playerDisplayName]{gcp}: {gcs}[status]");
    configReader.get("feedbackReason", "{gcn}[reason]");
  }

  @CAnnotation(
      info = "Check players afk status",
      args = "[playerName/all]",
      tab = "playername%%all",
      regVar = 1,
      consoleVar = 1,
      modules = "afk",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String text = parts[count];
      string = text;
    }

    if (string != null && string.equalsIgnoreCase("all")) {
      HashSet<CMIUser> hashSet = new HashSet();

      for (Player player : Bukkit.getOnlinePlayers()) {
        boolean flag =
            !(commandSender instanceof Player) ? true : ((Player) commandSender).canSee(player);
        CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
        if (cmiUser.isOnline()
            && (!cmiUser.isOnline() || !cmiUser.isVanished() || flag)
            && cmiUser.isAfk()) {
          hashSet.add(cmiUser);
        }
      }

      int countSecond = 1;

      for (CMIUser cMIUserSecond : hashSet) {
        Snd snd = new Snd();
        snd.setSender(commandSender);
        snd.setTarget(cMIUserSecond);
        String textSecond =
            cMIUserSecond.getAfkReason() != null && !cMIUserSecond.getAfkReason().isEmpty()
                ? CMILC.getIM(this, "feedbackReason", "[reason]", cMIUserSecond.getAfkReason())
                : "";
        CMILC.info(
            this,
            commandSender,
            "feedbackAll",
            "[place]",
            countSecond,
            "[status]",
            !cMIUserSecond.isAfk()
                ? CMILC.afk_off.getLocale()
                : CMILC.getIM(
                        this,
                        "feedbackInactive",
                        "[time]",
                        CMITimeManager.to24hourShort(
                            System.currentTimeMillis() - cMIUserSecond.getAfkInfo().getAfkFrom()))
                    + textSecond,
            snd);
        countSecond++;
      }

      if (hashSet.isEmpty()) {
        LC.info_nothingToShow.sendMessage(commandSender, new Object[0]);
      }
    } else {
      CMIUser cMIUserThird = cmi.getUser(commandSender, string, this, false);
      if (cMIUserThird == null) {
        return false;
      }

      if (!cMIUserThird.isOnline()) {
        LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
        return null;
      }

      boolean flagSecond =
          !(commandSender instanceof Player)
              ? true
              : ((Player) commandSender).canSee(cMIUserThird.getPlayer(false));
      if (cMIUserThird.isOnline() && cMIUserThird.isVanished() && !flagSecond) {
        LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
        return null;
      }

      Snd sndSecond = new Snd();
      sndSecond.setSender(commandSender);
      sndSecond.setTarget(cMIUserThird);
      String textThird =
          cMIUserThird.getAfkReason() != null && !cMIUserThird.getAfkReason().isEmpty()
              ? CMILC.getIM(this, "feedbackReason", "[reason]", cMIUserThird.getAfkReason())
              : "";
      String textFourth =
          cMIUserThird.isAfk()
              ? ""
              : CMITimeManager.to24hourShort(
                  cmi.getAfkManager().getAwayTrigerTime()
                      - (System.currentTimeMillis()
                          - cmi.getAfkManager().getLastAction(cMIUserThird)));
      if (PermissionsManager.CMIPerm.command_afkcheck_admin.hasPermission(commandSender)
          && !textFourth.isEmpty()) {
        CMILC.info(afkcheck.class, commandSender, "AfkInHover", sndSecond, "[time]", textFourth);
      }

      CMILC.info(
          this,
          commandSender,
          "feedback",
          sndSecond,
          "[status]",
          !cMIUserThird.isAfk()
              ? CMILC.afk_off.getLocale()
              : CMILC.getIM(
                      this,
                      "feedbackInactive",
                      "[time]",
                      CMITimeManager.to24hourShort(
                          System.currentTimeMillis() - cMIUserThird.getAfkInfo().getAfkFrom()))
                  + textThird);
    }

    return true;
  }
}
