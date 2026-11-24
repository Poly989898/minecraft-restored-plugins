package me.TechsCode.UltraPermissions.hooks.placeholders;

import java.util.concurrent.TimeUnit;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public class RankTimerDetailedPlaceholder extends UpermsPlaceholder {
   public RankTimerDetailedPlaceholder() {
      super("rank_timer_detailed", "Detailed Rank Timer of next highest timed group");
   }

   @Override
   protected String replace(Player player, User user, Group[] groups) {
      for (Group group : groups) {
         long groupExpiry = user.getGroupExpiry(group.toStored());
         if (groupExpiry != 0L) {
            long value = groupExpiry - System.currentTimeMillis();
            return Tools.getTimeString(value, TimeUnit.MILLISECONDS);
         }
      }

      return "Permanent";
   }
}
