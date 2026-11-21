package io.lumine.mythic.bukkit.commands;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicPostReloadedEvent;
import io.lumine.mythic.bukkit.events.MythicPreReloadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadCompleteEvent;
import io.lumine.mythic.bukkit.events.MythicReloadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.MythicLang;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.variables.types.MetaSkillVariable;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends Command<MythicBukkit> {
   boolean reloading = false;
   long reloadStartTime = System.currentTimeMillis();

   public ReloadCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (this.reloading) {
         CommandHelper.sendError(sender, "Plugin is already reloading...");
         return true;
      } else {
         boolean optProfile = false;
         boolean optSync = true;
         if (args != null && args.length > 0 && args[0].startsWith("-")) {
            if (args[0].startsWith("-a")) {
               optSync = false;
            }

            if (args[0].startsWith("-p")) {
               optProfile = true;
            }

            args = Arrays.copyOfRange(args, 1, args.length);
         }

         if (this.plugin.getConfiguration().getAnnounceOpReload()) {
            if (!sender.isOp()) {
               CommandHelper.sendSuccess(sender, MythicLang.get("command.reload.started", "Reloading plugin..."));
            }

            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
               if (player.isOp()) {
                  CommandHelper.sendSuccess(player, MythicLang.get("command.reload.started", "Reloading plugin..."));
               }
            }
         } else {
            CommandHelper.sendSuccess(sender, MythicLang.get("command.reload.started", "Reloading plugin..."));
         }

         this.reloading = true;
         this.reloadStartTime = System.currentTimeMillis();
         boolean profiling = optProfile;
         if (profiling) {
            CommandHelper.send(sender, "[Reload] Started Reload: " + this.timeSinceStart());
         }

         try {
            MythicPreReloadEvent event = new MythicPreReloadEvent(this.plugin);
            this.plugin.getServer().getPluginManager().callEvent(event);
         } catch (Error | Exception e) {
            CommandHelper.sendError(sender, "An error occurred while reloading!");
            e.printStackTrace();
         }

         if (profiling) {
            CommandHelper.send(sender, "[Reload] MythicPreReloadEvent Completed: " + this.timeSinceStart());
         }

         this.getPlugin().getClock().unload();
         if (optSync) {
            this.getPlugin().getPlaceholderManager().setAcceptingRegisteredParsers(true);
            this.doReloadPart1(sender, profiling);
            this.doReloadPart2(sender, profiling);
            this.getPlugin().getPlaceholderManager().setAcceptingRegisteredParsers(false);
         } else {
            Schedulers.async().run(() -> {
               this.getPlugin().getPlaceholderManager().setAcceptingRegisteredParsers(true);
               this.doReloadPart1(sender, profiling);
               Schedulers.sync().run(() -> {
                  this.doReloadPart2(sender, profiling);
                  this.getPlugin().getPlaceholderManager().setAcceptingRegisteredParsers(false);
               });
            });
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.reload";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "reload";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"r", "re"};
   }

   private void doReloadPart1(CommandSender sender, boolean profiling) {
      try {
         this.plugin.getConfiguration().reload();
         if (profiling) {
            CommandHelper.send(sender, "[Reload] Config Reloaded: " + this.timeSinceStart());
         }

         MythicReloadEvent event = new MythicReloadEvent(this.plugin);
         this.plugin.getServer().getPluginManager().callEvent(event);
         if (profiling) {
            CommandHelper.send(sender, "[Reload] MythicReloadEvent Completed: " + this.timeSinceStart());
         }
      } catch (Error | Exception e) {
         CommandHelper.sendError(sender, "An error occurred while reloading!");
         e.printStackTrace();
      }
   }

   private void doReloadPart2(CommandSender sender, boolean profiling) {
      try {
         for (ActiveMob am : this.getPlugin().getMobManager().getActiveMobs()) {
            if (ServerVersion.isFolia() && am.getEntity() != null && !FoliaHelper.isOwnedByCurrentRegion(am.getEntity().getBukkitEntity())) {
               Schedulers.of(am.getEntity().getBukkitEntity()).run(() -> {
                  try {
                     am.remountSpawner();
                     am.remountType();
                     am.getStatRegistry().refresh();
                  } catch (Error | Exception var2x) {
                  }
               });
            } else {
               am.remountSpawner();
               am.remountType();
               am.getStatRegistry().refresh();
            }
         }
      } catch (Error | Exception e) {
         CommandHelper.sendError(sender, "An error occurred while reloading active mobs!");
         e.printStackTrace();
      }

      if (profiling) {
         CommandHelper.send(sender, "[Reload] Active Mobs reloaded: " + this.timeSinceStart());
      }

      try {
         for (PlayerData profile : this.getPlugin().getPlayerManager().getKnownProfiles()) {
            profile.getStatRegistry().refresh();
         }
      } catch (Error | Exception ex) {
         CommandHelper.sendError(sender, "An error occurred while reloading player data!");
         ex.printStackTrace();
      }

      if (profiling) {
         CommandHelper.send(sender, "[Reload] Stats Resynced: " + this.timeSinceStart());
      }

      this.getPlugin().getClock().load(this.getPlugin());
      if (profiling) {
         CommandHelper.send(sender, "[Reload] Clocks Reloaded: " + this.timeSinceStart());
      }

      this.reloading = false;

      try {
         MythicReloadedEvent event = new MythicReloadedEvent(this.plugin);
         this.plugin.getServer().getPluginManager().callEvent(event);
      } catch (Error | Exception ex2) {
         CommandHelper.sendError(sender, "An error occurred while reloading!");
         ex2.printStackTrace();
      }

      if (profiling) {
         CommandHelper.send(sender, "[Reload] MythicReloadedEvent Completed: " + this.timeSinceStart());
      }

      try {
         MythicPostReloadedEvent event = new MythicPostReloadedEvent(this.plugin);
         this.plugin.getServer().getPluginManager().callEvent(event);
      } catch (Error | Exception ex3) {
         CommandHelper.sendError(sender, "An error occurred while reloading!");
         ex3.printStackTrace();
      }

      if (profiling) {
         CommandHelper.send(sender, "[Reload] MythicPostReloadedEvent Completed: " + this.timeSinceStart());
      }

      this.getPlugin().getClock().runPostReload();
      if (profiling) {
         CommandHelper.send(sender, "[Reload] runPostReload Completed: " + this.timeSinceStart());
      }

      MetaSkillVariable.MetaSkillVariableManager.reloadAll();
      if (profiling) {
         CommandHelper.send(sender, "[Reload] MetaSkillVariableManager Reloaded: " + this.timeSinceStart());
      }

      if (this.plugin.getConfiguration().getAnnounceOpReload()) {
         if (!sender.isOp()) {
            CommandHelper.sendSuccess(sender, MythicLang.get("command.reload.completed", "Mythic has finished reloading!"));
         }

         for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (player.isOp()) {
               CommandHelper.sendSuccess(player, MythicLang.get("command.reload.completed", "Mythic has finished reloading!"));
            }
         }
      } else {
         CommandHelper.sendSuccess(sender, MythicLang.get("command.reload.completed", "Mythic has finished reloading!"));
      }

      MythicLogger.log(MythicLang.get("command.reload.completed", "Mythic has finished reloading!"));
      MythicReloadCompleteEvent event = new MythicReloadCompleteEvent(this.plugin);
      this.plugin.getServer().getPluginManager().callEvent(event);
   }

   private long timeSinceStart() {
      return System.currentTimeMillis() - this.reloadStartTime;
   }
}
