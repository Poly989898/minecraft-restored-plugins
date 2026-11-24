package me.TechsCode.UltraPermissions.hooks;

import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.base.visual.Text;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class UpermsPlaceholder {
   private final String name;
   private final String description;

   public UpermsPlaceholder(String text, String description) {
      this.name = text;
      this.description = description;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public String getNativePlaceholder() {
      return "{" + Text.firstUpperCase(this.name) + "}";
   }

   public String get(User user, NServer nServer) {
      if (user == null) {
         return "";
      }

      Player player = Bukkit.getPlayer(user.getUuid());
      if (player != null && !player.isOnline()) {
         player = null;
      }

      Group[] groups = user.getActiveGroups()
         .bestToWorst()
         .servers(true, nServer != null ? nServer.getName() : null)
         .worlds(true, player != null ? player.getWorld().getName() : "*")
         .toArray(new Group[0]);
      return this.replace(player, user, groups);
   }

   protected abstract String replace(Player player, User user, Group[] groups);
}
