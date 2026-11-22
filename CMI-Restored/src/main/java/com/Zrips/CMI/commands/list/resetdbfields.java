package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.DataBase.DBDAO;
import com.Zrips.CMI.Modules.Homes.CmiHome;
import com.Zrips.CMI.Modules.Kits.KitsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class resetdbfields implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}Updated {gcs}[amount] {gcp}players");
    configReader.get("nofield", "{gcw}Cant find field by this name");
  }

  @CAnnotation(
      priority = 48,
      info = "Resets particular database columns to default value",
      args = "[columnName] (-w:[worldName]) (-p:[playerName])",
      tab = {"dbusercollumsshort", "%%-w:", "%%-p:"},
      explanation = {},
      regVar = 666,
      consoleVar = {1, 2, 3},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    DBDAO.UserTablesFields userTablesFields = null;
    DBDAO.UserTablesFields[] userTablesFieldses;
    int value = (userTablesFieldses = DBDAO.UserTablesFields.values()).length;
    int fieldIndex = 0;

    label356:
    while (fieldIndex < value) {
      DBDAO.UserTablesFields field = userTablesFieldses[fieldIndex];
      switch (field) {
        default:
          if (field.name().equalsIgnoreCase(strings[0])) {
            userTablesFields = field;
            break label356;
          }
        case player_uuid:
        case username:
          fieldIndex++;
      }
    }

    String string = null;
    String targetName = null;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.startsWith("-w:")) {
        string = arg.substring("-w:".length());
      }

      if (arg.startsWith("-p:")) {
        targetName = arg.substring("-p:".length());
      }
    }

    if (userTablesFields == null) {
      CMILC.info(this, commandSender, "nofield");
      return false;
    }

    HashSet<CMIUser> hashSet = new HashSet();
    if (targetName == null) {
      for (var entry : cmi.getPlayerManager().getAllUsers().entrySet()) {
        hashSet.add((CMIUser) entry.getValue());
      }
    } else {
      CMIUser cmiUser = cmi.getPlayerManager().getUser(targetName);
      if (cmiUser == null) {
        return false;
      }

      hashSet.add(cmiUser);
    }

    int resetCount = 0;

    for (CMIUser resetUser : hashSet) {
      switch (userTablesFields) {
        case player_uuid:
          return false;
        case username:
          return false;
        case nickname:
          if (resetUser.getNickName() == null) {
            continue;
          }

          resetUser.setNickName(null, false);
          break;
        case LogOutLocation:
          if (resetUser.getLogOutLocation() == null) {
            continue;
          }

          if (string == null
              || resetUser.getLogOutLocation().getWorld() == null
              || resetUser.getLogOutLocation().getWorld().getName().equalsIgnoreCase(string)) {
            resetUser.setLogOutLocation((CMILocation) null);
          }
          break;
        case DeathLocation:
          if (resetUser.getDeathLoc() == null) {
            continue;
          }

          if (string == null
              || resetUser.getDeathLoc().getWorld() == null
              || resetUser.getDeathLoc().getWorld().getName().equalsIgnoreCase(string)) {
            resetUser.setDeathLoc(null);
          }
          break;
        case TeleportLocation:
          if (resetUser.getLastTeleportLocation() == null) {
            continue;
          }

          if (string == null
              || resetUser.getLastTeleportLocation().getWorld() == null
              || resetUser
                  .getLastTeleportLocation()
                  .getWorld()
                  .getName()
                  .equalsIgnoreCase(string)) {
            resetUser.setLastTeleportLocation(null);
          }
          break;
        case Homes:
          if (resetUser.getHomes().isEmpty()) {
            continue;
          }

          if (string != null) {
            for (var homeEntry : new LinkedHashMap<>(resetUser.getHomes()).entrySet()) {
              if (((CmiHome) homeEntry.getValue()).getLoc() == null
                  || ((CmiHome) homeEntry.getValue()).getLoc().getWorld() == null
                  || ((CmiHome) homeEntry.getValue())
                      .getLoc()
                      .getWorld()
                      .getName()
                      .equalsIgnoreCase(string)) {
                resetUser.removeHome((CmiHome) homeEntry.getValue());
              }
            }
          } else {
            resetUser.getHomes().clear();
          }
          break;
        case LastLoginTime:
          if (resetUser.getLastLogin(false) == 0L) {
            continue;
          }

          resetUser.setLastLogin(0L);
          break;
        case LastLogoffTime:
          if (resetUser.getLastLogoff(false) == 0L) {
            continue;
          }

          resetUser.setLastLogoff(0L);
          break;
        case TotalPlayTime:
          if (resetUser.getTotalPlayTime(false) < 1L) {
            continue;
          }

          resetUser.setTotalPlayTime(0L);
          break;
        case tFly:
          if (resetUser.getTfly() == 0L) {
            continue;
          }

          resetUser.setTfly(0L);
          break;
        case tGod:
          if (resetUser.getTgod() == 0L) {
            continue;
          }

          resetUser.setTgod(0L);
          break;
        case Glow:
          if (resetUser.getGlow() == null) {
            continue;
          }

          resetUser.setGlow(null, false);
          break;
        case Ips:
          if (resetUser.getIps().isEmpty()) {
            continue;
          }

          resetUser.getIps().clear();
          break;
        case Cuffed:
          if (!resetUser.isCuffed()) {
            continue;
          }

          resetUser.setCuffed(false, false);
          break;
        case AlertUntil:
          if (resetUser.getAlertUntil() == 0L) {
            continue;
          }

          resetUser.setAlertUntil(0L);
          break;
        case AlertReason:
          if (resetUser.getAlertReason() == null) {
            continue;
          }

          resetUser.setAlertReason(null);
          break;
        case JoinedCounter:
          if (resetUser.isJoinedCounter()) {
            continue;
          }

          resetUser.setJoinedCounter(false);
          break;
        case LockedIps:
          if (resetUser.getLockedIps().isEmpty()) {
            continue;
          }

          resetUser.getLockedIps().clear();
          break;
        case pTime:
          if (resetUser.getPlayerTime() < 0L) {
            continue;
          }

          resetUser.setPlayerTime(-1L);
          break;
        case Kits:
          if (KitsManager.getUserKits(resetUser.getUniqueId()).isEmpty()) {
            continue;
          }

          KitsManager.getUserKits(resetUser.getUniqueId()).clear();
          break;
        case Charges:
          resetUser.getPCharge(false).reset(false);
          break;
        case Cooldowns:
          if (resetUser.getCommandCooldowns().getList().isEmpty()) {
            continue;
          }

          resetUser.getCommandCooldowns().getList().clear();
          break;
        case Balance:
          return false;
        case Notes:
          if (resetUser.getNotes().isEmpty()) {
            continue;
          }

          resetUser.getNotes().clear();
          break;
        case Rank:
          if (resetUser.getNullRank() == null) {
            continue;
          }

          resetUser.setRank(null);
          break;
        case BannedUntil:
          if (resetUser.getBanEntry().getExpiration() == null) {
            continue;
          }

          resetUser.getBanEntry().setExpiration(null);
          break;
        case BannedAt:
          if (resetUser.getBanEntry().getCreated() == null) {
            continue;
          }

          resetUser.getBanEntry().setCreated(null);
          break;
        case BannedBy:
          if (resetUser.getBanEntry().getSource() == null) {
            continue;
          }

          resetUser.getBanEntry().setSource(null);
          break;
        case BanReason:
          if (resetUser.getBanEntry().getReason() == null) {
            continue;
          }

          resetUser.getBanEntry().setReason(null);
          break;
        case Ignores:
          if (resetUser.getIgnores().isEmpty()) {
            continue;
          }

          resetUser.getIgnores().clear();
          break;
        case Vanish:
          if (!resetUser.isVanished()) {
            continue;
          }

          resetUser.setVanished(false);
          break;
        case Economy:
          resetUser
              .getEconomyAccount()
              .setBalance(
                  cmi.getEconomyManager().getDefaultGroup(),
                  cmi.getEconomyManager().getDefaultGroup().getStartingAmount());
          break;
        case Mail:
          if (resetUser.getMails().isEmpty()) {
            continue;
          }

          resetUser.getMails().clear();
          break;
        case FlightCharge:
          if (resetUser.getFlightCharge().getCharge() <= 0.0) {
            continue;
          }

          resetUser.getFlightCharge().setCharge(0.0);
          break;
        case UserMeta:
          if (resetUser.getMeta().getMap().isEmpty()) {
            continue;
          }

          resetUser.getMeta().getMap().clear();
          break;
        case Flying:
          if (!resetUser.isFlying()) {
            continue;
          }

          resetUser.setFlying(false);
          break;
        case Votifier:
          if (resetUser.getVotifierVotes() == 0) {
            continue;
          }

          resetUser.setVotifierVotes(0);
          break;
        case Jail:
          if (!resetUser.isJailed()) {
            continue;
          }

          resetUser.unjail();
          break;
        case JailedUntil:
          if (!resetUser.isJailed()) {
            continue;
          }

          resetUser.unjail();
          break;
        case FakeAccount:
          if (resetUser.isFakeAccount()) {
            resetUser.setFakeAccount(false);
            break;
          }
        case PlaytimeOptimized:
          continue;
        case flightChargeEnabled:
          if (!resetUser.getFlightCharge().isEnabled()) {
            continue;
          }

          resetUser.getFlightCharge().setEnabled(false);
          break;
        case JailReason:
          if (!resetUser.isJailed()) {
            continue;
          }

          resetUser.setJailedReason(null);
          break;
        case Skin:
          if (resetUser.getSkin() == null) {
            continue;
          }

          resetUser.setSkin(null);
          break;
        case Collision:
          if (!resetUser.isCollidable()) {
            continue;
          }

          resetUser.setCollision(true);
          break;
        case NamePrefix:
          if (resetUser.getNamePlatePrefix() == null) {
            continue;
          }

          resetUser.setNamePlatePrefix(null);
          break;
        case NameSuffix:
          if (resetUser.getNamePlateSuffix() == null) {
            continue;
          }

          resetUser.setNamePlateSuffix(null);
          break;
        case Warnings:
          if (resetUser.getWarnings() == null) {
            continue;
          }

          resetUser.getWarnings().clear();
          break;
        case NameColor:
          if (resetUser.getNamePlateNameColor() == null) {
            continue;
          }

          resetUser.setNamePlateNameColor(null);
          break;
        case Muted:
          if (!resetUser.isMuted()) {
            continue;
          }

          resetUser.setMutedUntil(0L);
          break;
        case AFRecharge:
          return false;
        case DisplayName:
          return false;
        case Options:
          resetUser.setOptions(new HashMap<>());
          break;
        case ChatColor:
          if (resetUser.getChatMessageFormat() == null) {
            continue;
          }

          resetUser.setChatMessageFormat(null);
      }

      resetCount++;
      resetUser.addForDelayedSave();
    }

    switch (userTablesFields) {
      case Ips:
        if (targetName == null) {
          cmi.getIpManager().clearCache();
        }
      default:
        cmi.getDbManager().startRapidSave();
        cmi.getDbManager().start();
        CMILC.info(this, commandSender, "feedback", "[amount]", resetCount);
        return true;
    }
  }
}
