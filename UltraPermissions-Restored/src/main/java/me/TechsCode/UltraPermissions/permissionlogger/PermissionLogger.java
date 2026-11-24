package me.TechsCode.UltraPermissions.permissionlogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import me.TechsCode.UltraPermissions.UltraPermissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PermissionLogger implements Listener {
   private ArrayList<LoggedPermission> list = new ArrayList<>();

   public PermissionLogger(UltraPermissions plugin) {
      Bukkit.getPluginManager().registerEvents(this, (Plugin)plugin.getBootstrap());
   }

   public void log(Player player, String text, boolean flag, String source) {
      LoggedPermission loggedPermission = new LoggedPermission(player, text, flag, System.currentTimeMillis(), source);

      try {
         new ArrayList<>(this.list).stream().filter(entry -> entry != null).forEach(existing -> {
            if (existing.getPermission().equals(loggedPermission.getPermission()) && existing.getPlayer().equals(loggedPermission.getPlayer())) {
               this.list.remove(existing);
            }
         });
         if (this.list.size() > 500) {
            this.list.remove(0);
         }

         this.list.add(loggedPermission);
      } catch (Exception ex) {
      }
   }

   public LoggedPermission[] getLoggedPermissions() {
      return Arrays.stream(this.list.toArray(new LoggedPermission[this.list.size()]))
         .filter(Objects::nonNull)
         .sorted(Comparator.comparing(LoggedPermission::getTime, Comparator.reverseOrder()).thenComparing(LoggedPermission::getPermission))
         .toArray(LoggedPermission[]::new);
   }

   @EventHandler
   public void clear(PlayerQuitEvent event) {
      try {
         for (LoggedPermission loggedPermission : new ArrayList<>(this.list)) {
            if (loggedPermission != null && loggedPermission.getPlayer() != null && loggedPermission.getPlayer().equals(event.getPlayer())) {
               this.list.remove(loggedPermission);
            }
         }
      } catch (Exception ex) {
      }
   }

   public void clearManually(Player player, String text) {
      try {
         for (LoggedPermission loggedPermission : new ArrayList<>(this.list)) {
            if (loggedPermission != null && loggedPermission.getPlayer() != null && loggedPermission.getPermission().contains(text) && loggedPermission.getPlayer().equals(player.getPlayer())) {
               this.list.remove(loggedPermission);
            }
         }
      } catch (Exception ex) {
      }
   }
}
