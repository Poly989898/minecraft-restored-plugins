package me.TechsCode.UltraPermissions.commands.requirements;

import java.util.List;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.Requirement;
import me.TechsCode.UltraPermissions.base.command.requirements.PermissionRequirement;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuperadminRequirement implements Requirement {
   private final UltraPermissions plugin;

   public SuperadminRequirement(UltraPermissions plugin) {
      this.plugin = plugin;
   }

   @Override
   public boolean isMatching(CommandSender commandSender, Player player, List<String> list) {
      return player == null || this.plugin.getUsers().uuid(player.getUniqueId()).map(User::isSuperadmin).orElse(false);
   }

   @Override
   public void onUnmatched(CommandSender commandSender, Player player) {
      commandSender.sendMessage(this.plugin.getPrefix() + PermissionRequirement.NO_PERMISSION_MESSAGE);
   }
}
