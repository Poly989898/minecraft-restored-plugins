package me.TechsCode.UltraPermissions.migration;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class MigrationAssistant implements Listener {
   private static boolean migrating;
   private static final Phrase KICK_MESSAGE = Phrase.create(
      "migrationAssistant.kickMessage", "Please remove **%plugin%** after the server has stopped", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase LOGIN_MESSAGE = Phrase.create("migrationAssistant.loginMessage", "The Server will be back in a few minutes", Colors.GRAY);

   public MigrationAssistant() {
   }

   public static boolean isMigrating() {
      return migrating;
   }

   public abstract String getPluginName();

   protected abstract void migrate(UltraPermissions ultraPermissions, Runnable task);

   public void doMigration(UltraPermissions ultraPermissions) {
      Bukkit.getPluginManager().registerEvents(this, (Plugin)ultraPermissions.getBootstrap());
      Bukkit.getOnlinePlayers().forEach(value -> value.kickPlayer(KICK_MESSAGE.get().replace("%plugin%", this.getPluginName())));
      Bukkit.getScheduler().runTaskAsynchronously((Plugin)ultraPermissions.getBootstrap(), () -> {
         ultraPermissions.getUsers().forEach(User::remove);
         ultraPermissions.getPermissions().forEach(Permission::remove);
         ultraPermissions.getGroups().forEach(Group::remove);
         Runnable task = () -> (new BukkitRunnable() {
            public void run() {
               MigrationAssistant.migrating = false;
               Bukkit.shutdown();
            }
         }).runTaskLater((Plugin)ultraPermissions.getBootstrap(), 60L);
         migrating = true;
         this.migrate(ultraPermissions, task);
      });
   }

   @EventHandler
   public void login(PlayerLoginEvent event) {
      event.disallow(Result.KICK_WHITELIST, LOGIN_MESSAGE.get());
   }
}
