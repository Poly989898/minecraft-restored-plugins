package io.lumine.mythic.bukkit.commands;

// MODIFIED VERSION (GNU AGPL v3, section 5a): this file was changed from the upstream MythicMobs 5.14.0
// source. The checks that restricted "premium" features were removed; see MODIFICATIONS.md.

import io.lumine.mythic.api.volatilecode.VolatileCodeDisabled;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import java.util.List;
import org.bukkit.command.CommandSender;

public class VersionCommand extends Command<MythicBukkit> {
   public VersionCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      String devBuilds = MythicBukkit.inst().getDescription().getVersion().contains("SNAPSHOT") ? "&aYes" : "&aNo";
      String preBuilds = "&eUnlocked (modified build)";
      String supported = MythicBukkit.inst().getVolatileCodeHandler() instanceof VolatileCodeDisabled ? "&cNo" : "&aYes";
      String extraInfo = ServerVersion.getNMS().toString();
      if (ServerVersion.isPaper()) {
         extraInfo = extraInfo + "&7(Paper)";
      }

      String[] messages = new String[]{
         ColorString.get("&6Server Version&f: " + ServerVersion.get().getVersion() + " " + extraInfo),
         ColorString.get("&6Plugin Version&f: " + MythicBukkit.inst().getVersion()),
         ColorString.get("&6Premium-gated features&f: " + preBuilds),
         ColorString.get("&6Is Dev Build&f: " + devBuilds),
         ColorString.get("&6Supported Version&f: " + supported)
      };
      CommandHelper.sendCommandMessage(sender, messages);
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.version";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "version";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"ver", "v"};
   }
}
