package me.TechsCode.UltraPermissions.hooks.placeholders;

import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public class RankPlaceholder extends UpermsPlaceholder {
   public RankPlaceholder() {
      super("rank", "Group Name of 1. Group");
   }

   @Override
   protected String replace(Player player, User user, Group[] groups) {
      return groups.length == 0 ? "" : groups[0].getName();
   }
}
