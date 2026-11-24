package net.Indyuce.mmoitems.command.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class UpdateItemCompletion implements TabCompleter {
   public List<String> onTabComplete(CommandSender sender, Command command, String text, String[] texts) {
      if (!sender.hasPermission("mmoitems.update")) {
         return null;
      }

      ArrayList<String> suggestions = new ArrayList<>();
      if (texts.length == 1) {
         for (Type type : MMOItems.plugin.getTypes().getAll()) {
            suggestions.add(type.getId());
         }
      }

      if (texts.length == 2 && Type.isValid(texts[0])) {
         Type.get(texts[0]).getConfigFile().getConfig().getKeys(false).forEach(var1x -> suggestions.add(var1x.toUpperCase()));
      }

      return texts[texts.length - 1].isEmpty()
         ? suggestions
         : suggestions.stream().filter(var1x -> var1x.toLowerCase().startsWith(texts[texts.length - 1].toLowerCase())).collect(Collectors.toList());
   }
}
