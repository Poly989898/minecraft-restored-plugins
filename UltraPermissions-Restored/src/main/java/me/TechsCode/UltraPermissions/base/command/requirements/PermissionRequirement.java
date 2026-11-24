package me.TechsCode.UltraPermissions.base.command.requirements;

import java.util.List;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.command.Requirement;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionRequirement implements Requirement {
   public static Phrase NO_PERMISSION_MESSAGE = Phrase.create("command.noPermission", "You don't have **access** to that command", Colors.GRAY, Colors.RED);
   private final SpigotTechPlugin plugin;
   private final String permission;

   public PermissionRequirement(SpigotTechPlugin spigotTechPlugin, String text) {
      this.plugin = spigotTechPlugin;
      this.permission = text;
   }

   @Override
   public boolean isMatching(CommandSender sender, Player player, List<String> texts) {
      return sender.hasPermission(this.permission);
   }

   @Override
   public void onUnmatched(CommandSender sender, Player player) {
      player.sendMessage(this.plugin.getPrefix() + NO_PERMISSION_MESSAGE);
      this.plugin.log("Player " + player.getName() + " could not access a command because he does not have " + this.permission);
   }
}
