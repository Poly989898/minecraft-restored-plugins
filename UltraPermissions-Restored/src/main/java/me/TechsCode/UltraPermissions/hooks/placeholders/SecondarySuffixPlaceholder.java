package me.TechsCode.UltraPermissions.hooks.placeholders;

import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public class SecondarySuffixPlaceholder extends UpermsPlaceholder {
   public SecondarySuffixPlaceholder() {
      super("secondarysuffix", "Suffix of 2. Group");
   }

   @Override
   protected String replace(Player player, User user, Group[] groups) {
      return groups.length >= 2 && groups[1].getSuffix().isPresent() ? groups[1].getSuffix().get() : "";
   }
}
