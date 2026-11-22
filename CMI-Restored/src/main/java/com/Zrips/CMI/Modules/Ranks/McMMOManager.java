package com.Zrips.CMI.Modules.Ranks;

import com.Zrips.CMI.Locale.CMILC;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.SkillAPI;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;

public class McMMOManager {
  private static Set<String> correctSkills = new HashSet<>();

  public static boolean isSkillCorrect(String string) {
    if (correctSkills.contains(string.toLowerCase())) {
      return true;
    }

    try {
      for (String skillName : ((Iterable<String>) SkillAPI.getSkills())) {
        if (skillName.equalsIgnoreCase(string)) {
          correctSkills.add(skillName.toLowerCase());
          return true;
        }
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    return false;
  }

  public static String getRealSkillName(String string) {
    try {
      if (string.equalsIgnoreCase("Power")) {
        return CMILC.getIM("rankinfo", "requiredMcmmoPower");
      }

      for (String skillName : ((Iterable<String>) SkillAPI.getSkills())) {
        if (skillName.equalsIgnoreCase(string)) {
          return skillName.substring(0, 1)
              + skillName.substring(1, skillName.length()).toLowerCase();
        }
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    return string;
  }

  public static int getSkillLevel(Player player, String string) {
    try {
      return string.equalsIgnoreCase("Power")
          ? ExperienceAPI.getPowerLevel(player)
          : ExperienceAPI.getLevel(player, string);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return 0;
    }
  }
}
