package me.TechsCode.UltraPermissions.commands.requirements;

import java.util.List;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.Requirement;
import me.TechsCode.UltraPermissions.base.command.requirements.PermissionRequirement;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantGroupsRequirement implements Requirement {
   private final UltraPermissions plugin;

   public GrantGroupsRequirement(UltraPermissions ultraPermissions) {
      this.plugin = ultraPermissions;
   }

   @Override
   public boolean isMatching(CommandSender sender, Player player, List<String> texts) {
      return player == null
         || this.plugin
            .getUsers()
            .uuid(player.getUniqueId())
            .get()
            .getGroups()
            .stream()
            .anyMatch(stored -> stored.isPresent() && !stored.get().get().getGrantGroups().isEmpty())
         || this.plugin.getUsers().uuid(player.getUniqueId()).map(User::isSuperadmin).orElse(false);
   }

   @Override
   public void onUnmatched(CommandSender sender, Player player) {
      sender.sendMessage(this.plugin.getPrefix() + PermissionRequirement.NO_PERMISSION_MESSAGE);
   }
}
