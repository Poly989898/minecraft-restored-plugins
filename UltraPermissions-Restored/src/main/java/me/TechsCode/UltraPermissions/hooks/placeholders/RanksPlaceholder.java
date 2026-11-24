package me.TechsCode.UltraPermissions.hooks.placeholders;

import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public class RanksPlaceholder extends UpermsPlaceholder {
   public RanksPlaceholder() {
      super("ranks", "Group Name of all Groups");
   }

   @Override
   protected String replace(Player player, User user, Group[] groups) {
      if (groups.length == 0) {
         return "";
      }

      String name = groups[0].getName();

      for (int i = 1; i < groups.length; i++) {
         name = name + ", " + groups[i].getName();
      }

      return name;
   }
}
