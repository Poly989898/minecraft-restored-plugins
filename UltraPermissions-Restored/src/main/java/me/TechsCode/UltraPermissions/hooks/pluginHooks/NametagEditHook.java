package me.TechsCode.UltraPermissions.hooks.pluginHooks;

import com.nametagedit.plugin.NametagEdit;
import java.util.Optional;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.events.group.GroupPrefixChangeEvent;
import me.TechsCode.UltraPermissions.events.group.GroupSuffixChangeEvent;
import me.TechsCode.UltraPermissions.events.user.UserGroupAddedEvent;
import me.TechsCode.UltraPermissions.events.user.UserGroupRemovedEvent;
import me.TechsCode.UltraPermissions.events.user.UserPrefixChangeEvent;
import me.TechsCode.UltraPermissions.events.user.UserSuffixChangeEvent;
import me.TechsCode.UltraPermissions.hooks.placeholders.PrefixPlaceholder;
import me.TechsCode.UltraPermissions.hooks.placeholders.SuffixPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class NametagEditHook implements Listener {
   private final UltraPermissions plugin;
   private final NServer thisServer;

   public NametagEditHook(UltraPermissions plugin) {
      this.plugin = plugin;
      this.thisServer = plugin.getThisServer().orElse(null);
      Bukkit.getPluginManager().registerEvents(this, (Plugin)plugin.getBootstrap());
      plugin.getScheduler().runTaskLater(() -> plugin.getUsers().stream().filter(user -> Bukkit.getPlayer(user.getUuid()) != null).forEach(user -> {
         if (this.isEnabled()) {
            String text = this.color(new PrefixPlaceholder().get(user, this.thisServer));
            String suffix = this.color(new SuffixPlaceholder().get(user, this.thisServer));
            NametagEdit.getApi().setNametag(user.getName(), text.equals("") ? "" : text + " ", suffix.equals("") ? "" : " " + suffix);
         }
      }), 5L);
   }

   private boolean isEnabled() {
      return this.plugin.getVisualRegistry().get().isEditingNametags();
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      if (this.isEnabled()) {
         Optional optional = this.plugin.getUserStorage().getUsers().uuid(event.getPlayer().getUniqueId());
         if (optional.isPresent()) {
            this.plugin.getScheduler().runTaskLater(() -> {
               String text = this.color(new PrefixPlaceholder().get((User)optional.get(), this.thisServer));
               String suffix = this.color(new SuffixPlaceholder().get((User)optional.get(), this.thisServer));
               NametagEdit.getApi().setNametag(((User)optional.get()).getName(), text.equals("") ? "" : text + " ", suffix.equals("") ? "" : " " + suffix);
            }, 5L);
         }
      }
   }

   @EventHandler
   public void onUserPrefixChange(UserPrefixChangeEvent event) {
      if (this.isEnabled()) {
         Optional optional = Optional.ofNullable(Bukkit.getPlayer(event.getUser().getUuid()));
         String text = this.color(new PrefixPlaceholder().get(event.getUser(), this.thisServer));
         if (optional.isPresent()) {
            NametagEdit.getApi().setPrefix(event.getUser().getName(), text.equals("") ? "" : text + " ");
         }
      }
   }

   @EventHandler
   public void onUserSuffixChange(UserSuffixChangeEvent event) {
      if (this.isEnabled()) {
         Optional optional = Optional.ofNullable(Bukkit.getPlayer(event.getUser().getUuid()));
         String text = this.color(new SuffixPlaceholder().get(event.getUser(), this.thisServer));
         if (optional.isPresent()) {
            NametagEdit.getApi().setSuffix(event.getUser().getName(), text.equals("") ? "" : " " + text);
         }
      }
   }

   @EventHandler
   public void onGroupPrefixChange(GroupPrefixChangeEvent event) {
      if (this.isEnabled()) {
         event.getGroup().getUsers().forEach(user -> Optional.ofNullable(Bukkit.getPlayer(user.getUuid())).ifPresent(player -> {
            String text = this.color(new PrefixPlaceholder().get(user, this.thisServer));
            NametagEdit.getApi().setPrefix(player, text.equals("") ? "" : text + " ");
         }));
      }
   }

   @EventHandler
   public void onGroupSuffixChange(GroupSuffixChangeEvent event) {
      if (this.isEnabled()) {
         event.getGroup().getUsers().forEach(user -> Optional.ofNullable(Bukkit.getPlayer(user.getUuid())).ifPresent(player -> {
            String text = this.color(new SuffixPlaceholder().get(user, this.thisServer));
            NametagEdit.getApi().setSuffix(player, text.equals("") ? "" : " " + text);
         }));
      }
   }

   @EventHandler
   public void onGroupAdd(UserGroupAddedEvent event) {
      if (this.isEnabled()) {
         Optional optional = Optional.ofNullable(event.getUser());
         if (optional.isPresent()) {
            this.plugin.getScheduler().runTaskLater(() -> {
               String text = this.color(new PrefixPlaceholder().get((User)optional.get(), this.thisServer));
               String suffix = this.color(new SuffixPlaceholder().get((User)optional.get(), this.thisServer));
               NametagEdit.getApi().setNametag(((User)optional.get()).getName(), text.equals("") ? "" : text + " ", suffix.equals("") ? "" : " " + suffix);
            }, 5L);
         }
      }
   }

   @EventHandler
   public void onGroupRemove(UserGroupRemovedEvent event) {
      if (this.isEnabled()) {
         Optional optional = Optional.ofNullable(event.getUser());
         if (optional.isPresent()) {
            this.plugin.getScheduler().runTaskLater(() -> {
               String text = this.color(new PrefixPlaceholder().get((User)optional.get(), this.thisServer));
               String suffix = this.color(new SuffixPlaceholder().get((User)optional.get(), this.thisServer));
               NametagEdit.getApi().setNametag(((User)optional.get()).getName(), text.equals("") ? "" : text + " ", suffix.equals("") ? "" : " " + suffix);
            }, 5L);
         }
      }
   }

   private String color(String text) {
      return ChatColor.translateAlternateColorCodes('&', text);
   }
}
