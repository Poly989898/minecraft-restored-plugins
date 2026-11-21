package io.lumine.mythic.core.skills.commands;

import io.lumine.mythic.api.skills.SkillMetadata;
import org.bukkit.entity.Player;

public interface CustomCommandFunction {
   void execute(Player player, String[] strs, SkillMetadata skillMetadata);
}
