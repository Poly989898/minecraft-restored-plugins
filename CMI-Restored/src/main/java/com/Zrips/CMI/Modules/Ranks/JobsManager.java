package com.Zrips.CMI.Modules.Ranks;

import com.Zrips.CMI.Locale.CMILC;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import org.bukkit.entity.Player;

public class JobsManager {
  public static boolean isJobCorrect(String string) {
    try {
      for (Job value : ((Iterable<Job>) Jobs.getJobs())) {
        if (value.getName().equalsIgnoreCase(string)) {
          return true;
        }
      }
    } catch (Exception | NoSuchMethodError value2) {
    }

    return false;
  }

  public static String getRealJobName(String string) {
    try {
      if (string.equalsIgnoreCase("TotalLevel")) {
        return CMILC.getIM("rankinfo", "requiredJobsTotalLevel");
      }

      Job value = Jobs.getJob(string);
      if (value != null) {
        return value.getName();
      }
    } catch (Exception | NoSuchMethodError value2) {
    }

    return string;
  }

  public static int getJobsLevel(Player player, String string) {
    try {
      JobsPlayer value = Jobs.getPlayerManager().getJobsPlayer(player);
      if (value == null) {
        return 0;
      }

      if (string.equalsIgnoreCase("TotalLevel")) {
        return value.getTotalLevels();
      }

      Job job = Jobs.getJob(string);
      if (job == null) {
        return 0;
      }

      JobProgression progression = value.getJobProgression(job);
      return progression == null ? 0 : progression.getLevel();
    } catch (Exception | NoSuchMethodError value4) {
      return 0;
    }
  }
}
