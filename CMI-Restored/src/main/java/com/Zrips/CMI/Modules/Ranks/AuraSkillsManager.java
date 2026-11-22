package com.Zrips.CMI.Modules.Ranks;

import com.Zrips.CMI.Locale.CMILC;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import net.Zrips.CMILib.Container.CMIText;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuraSkillsManager {
  static AuraSkillsApi auraSkills = AuraSkillsApi.get();

  public static boolean isSkillCorrect(String string) {
    return string.equalsIgnoreCase("power")
        ? true
        : auraSkills.getGlobalRegistry().getSkill(NamespacedId.fromString(string)) != null;
  }

  public static String getRealSkillName(CommandSender commandSender, String string) {
    if (string.equalsIgnoreCase("power")) {
      return CMILC.getIM("rankinfo", "requiredMcmmoPower");
    }

    if (!isSkillCorrect(string)) {
      return CMIText.firstToUpperCase(string);
    }

    Skill value = auraSkills.getGlobalRegistry().getSkill(NamespacedId.fromString(string));
    return CMIText.firstToUpperCase(
        value.getDisplayName(
            auraSkills.getUser(((Player) commandSender).getUniqueId()).getLocale()));
  }

  public static int getSkillLevel(Player player, String string) {
    try {
      if (string.equalsIgnoreCase("power")) {
        return auraSkills.getUser(player.getUniqueId()).getPowerLevel();
      }
    } catch (Exception | NoSuchMethodError value) {
    }

    Skill auraSkill = auraSkills.getGlobalRegistry().getSkill(NamespacedId.fromString(string));
    return auraSkill == null
        ? 0
        : auraSkills.getUser(player.getUniqueId()).getSkillLevel(auraSkill);
  }
}
