package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Ranks.CMIRank;
import com.Zrips.CMI.Modules.Ranks.RankManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;

public class rankinfo implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("distanceCM", "[value]cm");
    configReader.get("distanceM", "[value]m");
    configReader.get("distanceKm", "[value]km");
    configReader.get("requiredMoney", " {gcp}Money: [color][current]{gcp}/{gcs}[needed]");
    configReader.get("requiredExp", " {gcp}Exp: [color][current]{gcp}/{gcs}[needed]");
    configReader.get("requiredVotes", " {gcp}Votes: [color][current]{gcp}/{gcs}[needed]");
    configReader.get("requiredPerm", " {gcp}Perm: ");
    configReader.get("requiredPlaceholder", " {gcp}Custom: ");
    configReader.get("requiredPlaceholderList", "[color][message]");
    configReader.get("requiredPlaceholderHover", "{gcp}[color][current]{gcp}/{gcs}[needed]");
    configReader.get("requiredMcmmo", " {gcp}Mcmmo: ");
    configReader.get("requiredMcmmoList", "{gcp}[color][name]");
    configReader.get("requiredMcmmoListHover", " {gcp}[color][current]{gcp}/{gcs}[needed]");
    configReader.get("requiredMcmmoPower", "Power");
    configReader.get("requiredAurelium", " {gcp}Aurelium: ");
    configReader.get("requiredAureliumList", "{gcp}[color][name]");
    configReader.get("requiredAureliumListHover", " {gcp}[color][current]{gcp}/{gcs}[needed]");
    configReader.get("requiredAuraSkills", " {gcp}AuraSkills: ");
    configReader.get("requiredAuraSkillsList", "{gcp}[color][name]");
    configReader.get("requiredAuraSkillsListHover", " {gcp}[color][current]{gcp}/{gcs}[needed]");
    configReader.get("requiredJobs", " {gcp}Jobs: ");
    configReader.get("requiredJobsList", " {gcp}[color][name]");
    configReader.get("requiredJobsListHover", " {gcp}[color][current]{gcp}/{gcs}[needed]");
    configReader.get("requiredJobsTotalLevel", "TotalLevel");
    configReader.get("requiredItem", " {gcp}Items: ");
    configReader.get("requiredItemsList", "{gcp}[color][name]");
    configReader.get("requiredItemListHover", "{gcp}[color][current]{gcp}/{gcs}[needed]");
    configReader.get("currentRank", " {gcp}Your current rank: {gcs}[rankName]");
    configReader.get(
        "elseCurrentRank", " {gcs}[playerDisplayName] {gcp}current rank: {gcs}[rankName]");
    configReader.get("rankNameLine", " {gcp}------ {gcs}[rankName] {gcp}[rankupState] {gcp}------");
    configReader.get("canRankup", " {gcu}Can rankup!");
    configReader.get("cantRankup", "");
    configReader.get(
        "requiredStats", " {gcp}[name][extra]{gcp}: [color][current]{gcp}/{gcs}[needed]");
    configReader.get("requiredStatsExtra", " ({gcs}[type]{gcp})");
    configReader.get("nextRanks", " {gcp}Next ranks: {gcs}");
    configReader.get("finalRank", " {gcp}This is last rank");
    configReader.get("noRank", " {gcp}Cant find rank");
    configReader.get("check", "{gcp}Check {gcs}[rank] {gcp}rank");
    configReader.get("progressBarHover", "{gcp}Progress [percent]%");
  }

  @CAnnotation(
      info = "Your rank information",
      args = "(playerName) (rankName)",
      tab = "rankname%%playername",
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      modules = "ranks",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMIRank cmiRank = null;
    String string = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (cmi.getRankManager().getRank(arg) != null) {
        cmiRank = cmi.getRankManager().getRank(arg);
      } else if (arg.equalsIgnoreCase("-next")) {
        flag = true;
      } else {
        string = arg;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    cmiUser.recalculateRank();
    if (cmiRank == null) {
      cmiRank = cmiUser.getRank();
    }

    if (cmiRank == null) {
      CMILC.info(this, commandSender, "noRank");
      return null;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
    if (cmiUser.getRank() != null && cmiUser.getRank().equals(cmiRank)) {
      if (commandSender.getName().equalsIgnoreCase(cmiUser.getName())) {
        CMILC.info(this, commandSender, "currentRank", "[rankName]", cmiRank.getDisplayName());
      } else {
        CMILC.info(
            this, commandSender, "elseCurrentRank", "[rankName]", cmiRank.getDisplayName(), snd);
      }
    }

    if (flag) {
      for (CMIRank nextRank : cmiRank.getNextRanks()) {
        this.showRankInfo(cmi, nextRank, commandSender, cmiUser);
      }
    } else {
      this.showRankInfo(cmi, cmiRank, commandSender, cmiUser);
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    return true;
  }

  private void showRankInfo(
      CMI cmi, CMIRank cmiRank, CommandSender commandSender, CMIUser cmiUser) {
    CMILC.info(
        this,
        commandSender,
        "rankNameLine",
        "[rankName]",
        cmiRank.getDisplayName(),
        "[rankupState]",
        cmiRank.canRankup(cmiUser) == RankManager.rankupFailType.None
            ? CMILC.getIM(this, "canRankup")
            : CMILC.getIM(this, "cantRankup"));
    cmi.getRankManager().listMoneyRequirements(commandSender, cmiUser, cmiRank);
    cmi.getRankManager().listExpRequirements(commandSender, cmiUser, cmiRank);
    cmi.getRankManager().listVoteRequirements(commandSender, cmiUser, cmiRank);
    cmi.getRankManager().listStatsRequirements(commandSender, cmiUser, cmiRank);
    cmi.getRankManager().listPermRequirements(commandSender, cmiUser, cmiRank);
    cmi.getRankManager().listPlaceholderRequirements(commandSender, cmiUser, cmiRank);
    cmi.getRankManager().listMcmmoRequirements(commandSender, cmiUser, cmiRank);
    cmi.getRankManager().listAureliumRequirements(commandSender, cmiUser, cmiRank);
    cmi.getRankManager().listAuraSkillsRequirements(commandSender, cmiUser, cmiRank);
    cmi.getRankManager().listJobsRequirements(commandSender, cmiUser, cmiRank);
    cmi.getRankManager().listItemRequirements(commandSender, cmiUser, cmiRank);
    if (cmi.getRankManager().isProgressBar()) {
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(
          " "
              + cmi.getUtilManager()
                  .createTextProbgressBar(
                      100,
                      100.0F,
                      cmi.getRankManager().getOverallDonePercentage(cmiUser, cmiRank).intValue()));
      rawMessage.addHover(
          CMILC.getIM(
              this,
              "progressBarHover",
              "[percent]",
              (int) (cmi.getRankManager().getOverallDonePercentage(cmiUser, cmiRank) * 100.0)
                  / 100.0));
      rawMessage.build();
      rawMessage.show(commandSender);
    }

    RawMessage nextMessage = new RawMessage();
    String string = CMILC.getIM(this, "nextRanks");
    nextMessage.addText(CMILC.getIM(this, "nextRanks"));
    String separator = "";

    for (CMIRank nextRank : cmiRank.getNextRanks()) {
      nextMessage
          .addText(separator + nextRank.getDisplayName())
          .addHover(CMILC.getIM(this, "check", "[rank]", nextRank.getDisplayName()))
          .addCommand("cmi rankinfo " + cmiUser.getName() + " " + nextRank.getName());
      separator = LC.info_ListSpliter.getLocale(new Object[0]);
    }

    if (!string.isEmpty() && !cmiRank.getNextRanks().isEmpty()) {
      nextMessage.show(commandSender);
    } else {
      CMILC.info(this, commandSender, "finalRank");
    }
  }
}
