package it.ytnoos.lpx.config;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.api.check.RegisteredCheck;
import it.ytnoos.lpx.api.check.abstraction.Check;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.api.protocol.CheckContainer;
import it.ytnoos.lpx.config.logger.LoggerConfig;
import it.ytnoos.lpx.config.messages.MessagesConfig;
import it.ytnoos.lpx.config.options.OptionsConfig;
import java.util.List;

public class ConfigManager {
   private MessagesConfig messagesConfig;
   private OptionsConfig optionsConfig;
   private DiscordConfig discordConfig;
   private LoggerConfig loggerConfig;

   public ConfigManager(LPXPlugin lpxPlugin) {
      lpxPlugin.saveDefaultConfig();
      ConfigUpdater.update(lpxPlugin);
   }

   public void load(LPXPlugin lpxPlugin) {
      this.messagesConfig = new MessagesConfig(lpxPlugin);
      this.optionsConfig = new OptionsConfig(this.messagesConfig, lpxPlugin);
      this.loggerConfig = new LoggerConfig(this.messagesConfig, lpxPlugin);
      this.discordConfig = new DiscordConfig(this.messagesConfig, lpxPlugin);
   }

   public void reload(LPXBukkit lpxBukkit) {
      lpxBukkit.getPlugin().reload();
      this.load(lpxBukkit.getPlugin());

      for (List list : lpxBukkit.getCheckManager().getChecks().values()) {
         for (Object var13_o : list) { RegisteredCheck registeredCheck = (RegisteredCheck) var13_o;
            registeredCheck.getOptions().reload();
         }
      }

      for (ProtocolPlayer protocolPlayer : lpxBukkit.getRegistry().getPlayers().values()) {
         for (CheckContainer checkContainer : protocolPlayer.getChecks().values()) {
            for (Object var15_o : checkContainer) { Check check = (Check) var15_o;
               check.reload();
            }
         }
      }

      lpxBukkit.getAlertManager().reloadDiscord();
   }

   public OptionsConfig getOptionsConfig() {
      return this.optionsConfig;
   }

   public MessagesConfig getMessagesConfig() {
      return this.messagesConfig;
   }

   public LoggerConfig getLoggerConfig() {
      return this.loggerConfig;
   }

   public DiscordConfig getDiscordConfig() {
      return this.discordConfig;
   }


   // State hooks of the obfuscator: the bundled Adventure bytecode calls these by name (see README, "Hooks").
   // Values found by running the bundled serializers: TG() == 0 and Tc() != 0 select their normal control flow (same pattern as the removed SecurityUtils.Ew()/En() == 0/84).
   private static int hookCount;

   public static int TG() {
      return 0;
   }

   public static int Tc() {
      return 84;
   }

   public static void MK(int count) {
      hookCount = count;
   }
}
