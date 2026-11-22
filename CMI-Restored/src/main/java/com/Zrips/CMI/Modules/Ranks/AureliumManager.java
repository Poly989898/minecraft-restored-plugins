package com.Zrips.CMI.Modules.Ranks;

import com.Zrips.CMI.Locale.CMILC;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.skills.Skill;
import net.Zrips.CMILib.Container.CMIText;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AureliumManager {
  public static boolean isSkillCorrect(String string) {
    return string.equalsIgnoreCase("Power")
        ? true
        : AureliumAPI.getPlugin().getSkillRegistry().getSkill(string) != null;
  }

  public static String getRealSkillName(CommandSender commandSender, String string) {
    if (string.equalsIgnoreCase("Power")) {
      return CMILC.getIM("rankinfo", "requiredMcmmoPower");
    }

    if (!isSkillCorrect(string)) {
      return CMIText.firstToUpperCase(string);
    }

    Skill value = AureliumAPI.getPlugin().getSkillRegistry().getSkill(string);
    return CMIText.firstToUpperCase(
        value.getDisplayName(AureliumAPI.getPlugin().getLang().getLocale(commandSender)));
  }

  public static int getSkillLevel(Player player, String string) {
    try {
      if (string.equalsIgnoreCase("Power")) {
        return AureliumAPI.getPlugin().getPlayerManager().getPlayerData(player).getPowerLevel();
      }
    } catch (Exception | NoSuchMethodError value) {
    }

    Skill skill = AureliumAPI.getPlugin().getSkillRegistry().getSkill(string);
    return skill == null ? 0 : AureliumAPI.getSkillLevel(player, skill);
  }
}
