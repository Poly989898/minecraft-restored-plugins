package me.TechsCode.UltraPermissions.hooks.placeholders;

import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PrefixColorPlaceholder extends UpermsPlaceholder {
   public PrefixColorPlaceholder() {
      super("prefix_color", "Color of Player Prefix or Prefix of 1. Group");
   }

   @Override
   protected String replace(Player player, User user, Group[] groups) {
      if (user.getPrefix().isPresent()) {
         return ChatColor.getLastColors(user.getPrefix().get());
      } else if (groups.length != 0) {
         Group group = groups[0];
         return ChatColor.getLastColors(group.getPrefix().orElse(""));
      } else {
         return ChatColor.WHITE.toString();
      }
   }
}
