package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.command.CommandSender;

public class removeuser implements Cmd {
  private CMITask task = null;
  private Integer total = null;
  private Integer done = 0;

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("removed", "{gcp}User ({gcs}[uuid]{gcp}) removed");
    configReader.get("removedDuplicate", "{gcs}[amount]{gcp}/{gcs}[max] {gcp}duplicates removed");
  }

  @CAnnotation(
      priority = 115,
      info = "Removes user and its data",
      args = "[uuid/duplicates]",
      tab = "duplicates",
      explanation = {},
      regVar = 666,
      consoleVar = 1,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    UUID uuid = null;
    if (!strings[0].equalsIgnoreCase("duplicates")) {
      try {
        uuid = UUID.fromString(strings[0]);
      } catch (Exception exception) {
        cmi.consoleMessage("&cProvide valid UUID");
        return null;
      }

      if (uuid == null) {
        return false;
      }

      boolean flag = remove(commandSender, cmi.getPlayerManager().getUser(uuid), true);
      if (flag) {
        CMILC.info(this, commandSender, "removed", "[uuid]", uuid);
      }
    } else {
      this.total = CMI.getInstance().getPlayerManager().getDuplicatedUsers().size();
      this.tasker(commandSender);
    }

    return true;
  }

  private void tasker(CommandSender commandSender) {
    if (this.task == null) {
      this.task =
          CMIScheduler.scheduleSyncRepeatingTask(
              CMI.getInstance(),
              () -> {
                int value = 0;
                var iterator =
                    CMI.getInstance().getPlayerManager().getDuplicatedUsers().entrySet().iterator();

                label46:
                while (iterator.hasNext()) {
                  Entry entry = (Entry) iterator.next();
                  CMIUser cmiUser = null;
                  this.done = this.done + 1;
                  if (value > 50) {
                    break;
                  }

                  for (CMIUser candidate : (List<CMIUser>) entry.getValue()) {
                    value++;
                    if (cmiUser == null) {
                      cmiUser = candidate;
                    } else {
                      if (cmiUser.getLastLogoffClean() < candidate.getLastLogoffClean()) {
                        remove(commandSender, cmiUser, false);
                        cmiUser = candidate;
                      } else {
                        remove(commandSender, candidate, false);
                      }

                      if (value > 50) {
                        break label46;
                      }
                    }
                  }

                  iterator.remove();
                }

                if (this.total != null && this.done >= this.total) {
                  this.task.cancel();
                  this.task = null;
                  CMILC.info(
                      "removeuser",
                      commandSender,
                      "removedDuplicate",
                      "[amount]",
                      this.total,
                      "[max]",
                      this.total);
                } else {
                  if (this.total != null) {
                    CMILC.info(
                        "removeuser",
                        commandSender,
                        "removedDuplicate",
                        "[amount]",
                        this.done,
                        "[max]",
                        this.total);
                  }

                  if (this.total == null) {
                    this.task.cancel();
                    this.task = null;
                  }
                }
              },
              0L,
              20L);
    }
  }

  private static boolean remove(CommandSender commandSender, CMIUser cmiUser, boolean flag) {
    if (cmiUser == null) {
      return false;
    }

    CMI cmi = CMI.getInstance();
    boolean removed = false;
    if (!cmi.getPurgeManager().purgePlayerDataFiles(cmiUser.getUniqueId(), false)) {
      if (flag) {
        if (cmi.getPurgeManager().isPlayerDataEnabled()) {
          CMILC.sendMessage(
              commandSender,
              "Did not removed player data file. Most likely data file don't exist or world path is"
                  + " set incorrectly. Check config under purge section. Or simply ignore this"
                  + " message.");
        } else {
          CMILC.sendMessage(
              commandSender,
              "Could not remove player data file. Check config file under purge section to enable"
                  + " PlayerData removal");
        }
      }

      removed = false;
    } else {
      removed = true;
    }

    if (!cmi.getPurgeManager().purgePlayerStatsFiles(cmiUser.getUniqueId(), false)) {
      if (flag) {
        if (cmi.getPurgeManager().isPlayerStatsEnabled()) {
          CMILC.sendMessage(
              commandSender,
              "Did not removed player stats file. Most likely data file don't exist or world path"
                  + " is set incorrectly. Check config under purge section. Or simply ignore this"
                  + " message.");
        } else {
          CMILC.sendMessage(
              commandSender,
              "Could not remove player stats file. Check config file under purge section to enable"
                  + " PlayerStats removal");
        }
      }

      removed = false;
    } else {
      removed = true;
    }

    if (!cmi.getPurgeManager().purgePlayerAdvancementsFiles(cmiUser.getUniqueId(), false)) {
      removed = false;
    } else {
      removed = true;
    }

    cmi.getPlayerManager().softRemoveUser(cmiUser, false);
    if (!cmi.getDbManager().getDB().removeUser(cmiUser.getId())) {
      if (flag) {
        CMILC.sendMessage(
            commandSender,
            "Could not remove player from database. Most likely data entry don't exist");
      }

      removed = false;
    } else {
      removed = true;
    }

    return removed;
  }
}
