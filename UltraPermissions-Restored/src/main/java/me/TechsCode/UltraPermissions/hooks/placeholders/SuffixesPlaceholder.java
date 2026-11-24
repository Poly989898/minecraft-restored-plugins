package me.TechsCode.UltraPermissions.hooks.placeholders;

import java.util.ArrayList;
import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public class SuffixesPlaceholder extends UpermsPlaceholder {
   public SuffixesPlaceholder() {
      super("suffixes", "Replaced with all group suffixes");
   }

   @Override
   protected String replace(Player player, User user, Group[] groups) {
      ArrayList list = new ArrayList();
      if (user.getSuffix().isPresent()) {
         list.add(user.getSuffix().get());
      }

      for (Group group : groups) {
         if (group.getSuffix().isPresent()) {
            list.add(group.getSuffix().get());
         }
      }

      return String.join(" ", list);
   }
}
