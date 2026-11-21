package io.lumine.mythic.core.skills.commands;

import java.util.List;
import org.bukkit.entity.Player;

public interface CustomCommandTabCompleteFunction {
   List<String> tabComplete(Player player, String[] strs);
}
