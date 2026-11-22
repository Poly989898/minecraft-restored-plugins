package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class saveall implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "saving",
        "[symbol] {gcp}checked: {gcs}[checked]{gcp} from {gcs}[total]{gcp} saved: {gcs}[saved]"
            + " {gcp}skipped: {gcs}[skipped] [simbol]");
    configReader.get(
        "saved", "{gcp}Save finished. Saved: {gcs}[saved] {gcp}Skipped: {gcs}[skipped]");
  }

  @CAnnotation(
      priority = 205,
      info = "Saves every player inventory",
      args = "(daysRange/-online)",
      tab = "-online%%1",
      explanation = "-online variable can be used to save only online player inventories",
      regVar = {0, 1},
      consoleVar = {0, 1},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    saveall saveall = this;
    CMIScheduler.runTaskAsynchronously(
        cmi,
        () -> {
          long longValue = System.currentTimeMillis();
          int value = 1;
          int count = 0;
          boolean flag = false;
          if (strings.length == 1) {
            try {
              count = Integer.parseInt(strings[0]);
            } catch (NumberFormatException numberFormatException) {
              if (!strings[0].equalsIgnoreCase("-online")) {
                LC.info_UseInteger.sendMessage(commandSender, new Object[0]);
                return;
              }

              flag = true;
            }
          }

          long time = System.currentTimeMillis() - count * 24 * 60 * 60 * 1000;
          if (!flag && !Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
              cmi.getSavedInventoryManager().saveInv(player, null, null);
            }

            OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
            int countSecond = 0;
            int countThird = 0;

            for (int countFourth = 0; countFourth < offlinePlayers.length; countFourth++) {
              if (!offlinePlayers[countFourth].isOnline()) {
                CMIUser cmiUser =
                    cmi.getPlayerManager().getUser(offlinePlayers[countFourth], false);
                if (cmiUser != null) {
                  Player playerSecond = null;

                  try {
                    playerSecond = cmiUser.getPlayer();
                  } catch (Throwable throwable) {
                    countThird++;
                    continue;
                  }

                  if (playerSecond != null) {
                    try {
                      playerSecond.loadData();
                    } catch (Exception exception) {
                      countThird++;
                      continue;
                    }

                    if (longValue + 1000L < System.currentTimeMillis()) {
                      longValue = System.currentTimeMillis();
                      if (commandSender instanceof Player) {
                        value =
                            cmi.getSavedInventoryManager()
                                .showTimer(
                                    (Player) commandSender,
                                    value,
                                    countSecond,
                                    offlinePlayers.length,
                                    countThird,
                                    countFourth);
                      }
                    }

                    if (count != 0 && time > playerSecond.getLastPlayed()) {
                      countThird++;
                    } else {
                      countSecond++;
                      cmi.getSavedInventoryManager().saveInv(playerSecond, null, null);
                    }
                  }
                }
              }
            }

            CMILC.info(
                saveall, commandSender, "saved", "[saved]", countSecond, "[skipped]", countThird);
          } else {
            int countFifth = 0;
            byte b = 0;
            ArrayList<Player> arrayList = new ArrayList();

            for (Player playerThird : Bukkit.getOnlinePlayers()) {
              arrayList.add(playerThird);
            }

            int countSixth = 0;

            for (Player playerFourth : arrayList) {
              countSixth++;
              CMIUser cMIUserSecond = cmi.getPlayerManager().getUser(playerFourth);
              if (cMIUserSecond != null && playerFourth != null) {
                if (longValue + 1000L < System.currentTimeMillis()) {
                  longValue = System.currentTimeMillis();
                  if (commandSender instanceof Player) {
                    value =
                        cmi.getSavedInventoryManager()
                            .showTimer(
                                (Player) commandSender,
                                value,
                                countFifth,
                                arrayList.size(),
                                b,
                                countSixth);
                  }
                }

                countFifth++;
                cmi.getSavedInventoryManager().saveInv(playerFourth, null, null);
              }
            }

            CMILC.info(
                saveall,
                commandSender,
                "saved",
                "[saved]",
                countFifth,
                "[skipped]",
                Integer.valueOf(b));
          }
        });
    return true;
  }
}
