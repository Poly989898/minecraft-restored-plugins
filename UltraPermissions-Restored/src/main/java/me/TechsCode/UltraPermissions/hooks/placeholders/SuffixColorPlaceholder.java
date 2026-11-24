package me.TechsCode.UltraPermissions.hooks.placeholders;

import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SuffixColorPlaceholder extends UpermsPlaceholder {
   public SuffixColorPlaceholder() {
      super("suffix_color", "Color of Player Suffix or Suffix of 1. Group");
   }

   @Override
   protected String replace(Player player, User user, Group[] groups) {
      if (user.getSuffix().isPresent()) {
         return ChatColor.getLastColors(user.getSuffix().get());
      } else if (groups.length != 0) {
         Group group = groups[0];
         return ChatColor.getLastColors(group.getSuffix().orElse(""));
      } else {
         return ChatColor.WHITE.toString();
      }
   }
}
