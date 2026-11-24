package me.TechsCode.UltraPermissions.visual;

import java.util.Optional;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateEvent;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateTime;
import me.TechsCode.UltraPermissions.base.visual.Text;
import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.migration.MigrationAssistant;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class VisualManager implements Listener {
   private final UltraPermissions plugin;
   private final VisualRegistry registry;

   public VisualManager(UltraPermissions ultraPermissions, VisualRegistry visualRegistry) {
      this.plugin = ultraPermissions;
      this.registry = visualRegistry;
      Bukkit.getPluginManager().registerEvents(this, (Plugin)ultraPermissions.getBootstrap());
   }

   @EventHandler
   public void chat(AsyncPlayerChatEvent event) {
      if (!MigrationAssistant.isMigrating()) {
         String chatFormat = this.registry.getFormat(VisualType.CHAT);
         Optional optionalUser = this.plugin.getUsers().uuid(event.getPlayer().getUniqueId());
         GroupList groups = null;
         if (optionalUser.isPresent()) {
            groups = ((User)optionalUser.get()).getActiveGroups().bestToWorst();
         }

         boolean formatApplied = false;
         boolean groupFormatApplied = false;

         for (Group group : groups) {
            if (!groupFormatApplied) {
               String groupFormat = group.getFormat();
               if (groupFormat != null && groupFormat != "none") {
                  formatApplied = true;
                  groupFormat = this.replacePlaceholders(event.getPlayer(), groupFormat).replace("%", "percent").replace("{Message}", "%2$s");
                  event.setFormat(ChatColor.translateAlternateColorCodes('&', Text.hexColor(groupFormat)));
                  groupFormatApplied = !groupFormatApplied;
               }
            }
         }

         if (!formatApplied) {
            if (chatFormat == null) {
               return;
            }

            chatFormat = this.replacePlaceholders(event.getPlayer(), chatFormat).replace("%", "percent").replace("{Message}", "%2$s");
            event.setFormat(Text.hexColor(chatFormat));
         }
      }
   }

   @EventHandler
   public void update(UpdateEvent event) {
      if (!MigrationAssistant.isMigrating()) {
         if (event.getUpdateTime() == UpdateTime.SLOWER) {
            if (this.registry.getFormat(VisualType.TABLIST) != null || this.registry.getFormat(VisualType.DISPLAY_NAME) != null) {
               Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin.getBootstrap(), () -> {
                  for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                     String tablistFormat = this.registry.getFormat(VisualType.TABLIST);
                     String displayNameFormat = this.registry.getFormat(VisualType.DISPLAY_NAME);
                     if (tablistFormat != null) {
                        onlinePlayer.setPlayerListName(this.replacePlaceholders(onlinePlayer, tablistFormat));
                     }

                     if (displayNameFormat != null) {
                        onlinePlayer.setDisplayName(this.replacePlaceholders(onlinePlayer, displayNameFormat));
                     }
                  }
               });
            }
         }
      }
   }

   private String replacePlaceholders(Player player, String format) {
      Optional optionalUser = this.plugin.getUsers().uuid(player.getUniqueId());
      if (!optionalUser.isPresent()) {
         return format;
      }

      for (UpermsPlaceholder placeholder : UltraPermissions.placeholders) {
         format = format.replace(placeholder.getNativePlaceholder(), placeholder.get((User)optionalUser.get(), this.plugin.getThisServer().orElse(null)));
      }

      format = this.plugin.replacePlaceholders(player, format);
      format = format.replace("{Player}", player.getName());
      format = format.replace("{DisplayName}", player.getDisplayName());
      return format.trim();
   }
}
