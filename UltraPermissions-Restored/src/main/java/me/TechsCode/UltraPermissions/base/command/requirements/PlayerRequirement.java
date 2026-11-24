package me.TechsCode.UltraPermissions.base.command.requirements;

import java.util.List;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.command.Requirement;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerRequirement implements Requirement {
   private static final Phrase NO_CONSOLE = Phrase.create("command.noConsole", "This command cannot be run through the console", Colors.GRAY);
   private final SpigotTechPlugin plugin;

   public PlayerRequirement(SpigotTechPlugin spigotTechPlugin) {
      this.plugin = spigotTechPlugin;
   }

   @Override
   public boolean isMatching(CommandSender sender, Player player, List<String> texts) {
      return player != null;
   }

   @Override
   public void onUnmatched(CommandSender sender, Player player) {
      sender.sendMessage(this.plugin.getPrefix() + NO_CONSOLE);
   }
}
