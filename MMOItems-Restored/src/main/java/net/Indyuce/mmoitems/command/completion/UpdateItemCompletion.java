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
   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var1.hasPermission("mmoitems.update")) {
         return null;
      }

      ArrayList<String> var5 = new ArrayList<>();
      if (var4.length == 1) {
         for (Type var7 : MMOItems.plugin.getTypes().getAll()) {
            var5.add(var7.getId());
         }
      }

      if (var4.length == 2 && Type.isValid(var4[0])) {
         Type.get(var4[0]).getConfigFile().getConfig().getKeys(false).forEach(var1x -> var5.add(var1x.toUpperCase()));
      }

      return var4[var4.length - 1].isEmpty()
         ? var5
         : var5.stream().filter(var1x -> var1x.toLowerCase().startsWith(var4[var4.length - 1].toLowerCase())).collect(Collectors.toList());
   }
}
