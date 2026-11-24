package me.TechsCode.UltraPermissions.commands.requirements;

import java.util.List;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.Requirement;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MySQLEnabledRequirement implements Requirement {
   private final UltraPermissions plugin;
   private static final Phrase MESSAGE = Phrase.create("mysqlEnabledRequirement.message", "Command not available if MySQL is not configured", Colors.GRAY);

   public MySQLEnabledRequirement(UltraPermissions ultraPermissions) {
      this.plugin = ultraPermissions;
   }

   @Override
   public boolean isMatching(CommandSender sender, Player player, List<String> texts) {
      return this.plugin.getMySQLManager().isEnabled();
   }

   @Override
   public void onUnmatched(CommandSender sender, Player player) {
      sender.sendMessage(this.plugin.getPrefix() + MESSAGE.get());
   }
}
