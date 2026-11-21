package it.ytnoos.lpx.alert;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.manager.server.VersionComparison;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.abstraction.Check;
import it.ytnoos.lpx.api.event.LPXAlertEvent;
import it.ytnoos.lpx.api.event.LPXPunishEvent;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.check.PendingPunishment;
import it.ytnoos.lpx.commands.sub.KickCommand;
import it.ytnoos.lpx.common.utils.TimeUtils;
import it.ytnoos.lpx.config.AlertsConfig;
import it.ytnoos.lpx.logger.LogWriter;
import it.ytnoos.lpx.utils.string.MessageFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AlertManager {
   private final LPXBukkit lpxBukkit;
   private final LogWriter logWriter;
   private final DiscordAlerts discordAlerts;
   private final Set<UUID> alertsDisabled = new HashSet<>();
   private long lastAlert = 0L;

   public AlertManager(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
      this.logWriter = new LogWriter(lpxBukkit2);
      this.discordAlerts = new DiscordAlerts(lpxBukkit2);
   }

   public void reload() {
      this.logWriter.start();
      this.discordAlerts.reload();
   }

   public void shutdown() {
      this.logWriter.shutdown();
   }

   public void flag(ProtocolPlayer protocolPlayer, Check check, FlagInfo[] flagInfos) {
      FlagInfo[] flagInfos2 = flagInfos;
      if (!protocolPlayer.isKicked()) {
         this.logWriter.writeFlag(protocolPlayer, check, flagInfos2);
         if (this.lpxBukkit.getRecentViolations().size() >= 360) {
            this.lpxBukkit.getRecentViolations().removeLast();
         }

         this.lpxBukkit.getRecentViolations().addFirst(new PendingPunishment(protocolPlayer.getUsername(), check.getOptions(), System.currentTimeMillis(), flagInfos2));
         this.callEvent(() -> Bukkit.getPluginManager().callEvent(new LPXAlertEvent(protocolPlayer, check, flagInfos2)));
         int value = check.getOptions().getCheckOptions().getMinVl();
         if (value != -1 && check.getOptions().getVl() >= value) {
            AlertsConfig alertsConfig = this.lpxBukkit.getConfigManager().getMessagesConfig().getAlertsConfig();
            String string = this.replace(alertsConfig.getFormat(), protocolPlayer, check, flagInfos2);
            this.lpxBukkit.getLogger().log(Level.INFO, MessageFormatter.toPlain(string));
            if (TimeUtils.difference(protocolPlayer.getLastPunish()) >= this.lpxBukkit.getConfigManager().getOptionsConfig().getPunishDelay()) {
               StringBuilder stringBuilder = new StringBuilder();

               for (int value2 = 0; value2 < alertsConfig.getHover().size(); value2++) {
                  stringBuilder.append(this.replace((String)alertsConfig.getHover().get(value2), protocolPlayer, check, flagInfos2));
                  if (value2 != alertsConfig.getHover().size() - 1) {
                     stringBuilder.append('\n');
                  }
               }

               HoverEvent hoverEvent = new HoverEvent(Action.SHOW_TEXT, MessageFormatter.toBungee(stringBuilder.toString()));
               BaseComponent[] baseComponents = MessageFormatter.toBungee(string);

               for (BaseComponent baseComponent : baseComponents) {
                  baseComponent.setHoverEvent(hoverEvent);
               }

               Bukkit.getOnlinePlayers().forEach(value3 -> {
                  if (value3.hasPermission(alertsConfig.getPermission()) && !this.alertsDisabled.contains(value3.getUniqueId())) {
                     value3.getPlayer().spigot().sendMessage(baseComponents);
                  }
               });
               FoliaScheduler.getAsyncScheduler().runNow(this.lpxBukkit.getPlugin(), object -> this.discordAlerts.sendAlert(protocolPlayer, check, flagInfos2));
            }
         }
      }
   }

   public void punish(ProtocolPlayer protocolPlayer, Check check) {
      if (TimeUtils.difference(protocolPlayer.getLastPunish()) >= this.lpxBukkit.getConfigManager().getOptionsConfig().getPunishDelay()) {
         this.callEvent(() -> Bukkit.getPluginManager().callEvent(new LPXPunishEvent(protocolPlayer, check)));
         protocolPlayer.updateLastPunish();

         for (String string : check.getOptions().getCheckOptions().getPunishCommands()) {
            string = this.replacePlayer(string, protocolPlayer);
            if (string.startsWith("lpx kick")) {
               KickCommand.runCommand(this.lpxBukkit, Bukkit.getConsoleSender(), string.substring(4).split(" "));
            } else {
               String string2 = string;
               FoliaScheduler.getGlobalRegionScheduler().execute(this.lpxBukkit.getPlugin(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string2));
            }
         }

         Bukkit.getOnlinePlayers()
            .forEach(
               value -> {
                  if (value.hasPermission(this.lpxBukkit.getConfigManager().getMessagesConfig().getAlertsConfig().getPermission())
                     && !this.alertsDisabled.contains(value.getUniqueId())) {
                     MessageFormatter.send(
                        value.getPlayer(), this.replace(this.lpxBukkit.getConfigManager().getMessagesConfig().getKickAlert(), protocolPlayer, check, new FlagInfo[0])
                     );
                  }
               }
            );
      }
   }

   public String replace(String string, ProtocolPlayer protocolPlayer, Check check, FlagInfo[] flagInfos) {
      FlagInfo[] flagInfos2 = flagInfos;
      return this.replaceFlagInfo(this.replacePlayer(this.replaceServer(string), protocolPlayer), check, flagInfos2);
   }

   public String replacePlayer(String string, ProtocolPlayer protocolPlayer) {
      return string.replace("%player%", protocolPlayer.getUsername()).replace("%uuid%", protocolPlayer.getUniqueId().toString());
   }

   private String replaceFlagInfo(String string, Check check, FlagInfo[] flagInfos) {
      FlagInfo[] flagInfos2 = flagInfos;
      StringBuilder stringBuilder = new StringBuilder(flagInfos2.length == 0 ? ":(" : "\n");

      for (FlagInfo flagInfo : flagInfos2) {
         stringBuilder.append(this.lpxBukkit.getConfigManager().getMessagesConfig().getSecondColor())
            .append(flagInfo.getName())
            .append(": ")
            .append(ChatColor.GRAY)
            .append(MessageFormatter.escapeTags(String.valueOf(flagInfo.getValue())))
            .append('\n');
      }

      return string.replace("%check%", check.getType().getCategory().getFormattedName())
         .replace("%type%", check.getType().getId().name())
         .replace("%vl%", check.getOptions().getVl() + "")
         .replace("%infos%", stringBuilder.toString())
         .replace("%description%", check.getType().getDescription())
         .replace("%max-vl%", check.getOptions().getCheckOptions().getMaxVL() + "");
   }

   private void callEvent(Runnable runnable) {
      if (PacketEvents.getAPI().getServerManager().getVersion().is(VersionComparison.NEWER_THAN_OR_EQUALS, ServerVersion.V_1_13)) {
         FoliaScheduler.getGlobalRegionScheduler().execute(this.lpxBukkit.getPlugin(), runnable);
      } else {
         runnable.run();
      }
   }

   private String replaceServer(String string) {
      return string.replace("%server%", this.lpxBukkit.getServer());
   }

   public void sendPermissionAlert(String string, String string2) {
      Bukkit.getOnlinePlayers().forEach(value -> {
         if (value.hasPermission(string2) && !this.alertsDisabled.contains(value.getUniqueId())) {
            MessageFormatter.send(value, string);
         }
      });
      this.lpxBukkit.getLogger().log(Level.INFO, MessageFormatter.toPlain(string));
   }

   public boolean toggleAlerts(Player player) {
      UUID uuid = player.getUniqueId();
      if (this.alertsDisabled.contains(uuid)) {
         this.alertsDisabled.remove(uuid);
         return true;
      } else {
         this.alertsDisabled.add(uuid);
         return false;
      }
   }

   public void enableAlerts(Object object) {
      this.alertsDisabled.remove(object);
   }

   public void reloadDiscord() {
      this.discordAlerts.reload();
   }

   public LogWriter getLogWriter() {
      return this.logWriter;
   }

   public Set getAlertsDisabled() {
      return this.alertsDisabled;
   }

}
