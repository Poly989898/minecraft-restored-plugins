package me.TechsCode.UltraPermissions.hooks.placeholders;

import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public class SecondaryPrefixPlaceholder extends UpermsPlaceholder {
   public SecondaryPrefixPlaceholder() {
      super("secondaryprefix", "Prefix of 2. Group");
   }

   @Override
   protected String replace(Player player, User user, Group[] groups) {
      return groups.length >= 2 && groups[1].getPrefix().isPresent() ? groups[1].getPrefix().get() : "";
   }
}
