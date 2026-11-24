package me.TechsCode.UltraPermissions.hooks.placeholders;

import java.util.ArrayList;
import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public class PrefixesPlaceholder extends UpermsPlaceholder {
   public PrefixesPlaceholder() {
      super("prefixes", "Replaced with player prefix & group prefixes");
   }

   @Override
   protected String replace(Player player, User user, Group[] groups) {
      ArrayList list = new ArrayList();
      if (user.getPrefix().isPresent()) {
         list.add(user.getPrefix().get());
      }

      for (Group group : groups) {
         if (group.getPrefix().isPresent()) {
            list.add(group.getPrefix().get());
         }
      }

      return String.join(" ", list);
   }
}
