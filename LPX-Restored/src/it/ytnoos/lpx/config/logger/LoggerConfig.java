package it.ytnoos.lpx.config.logger;

import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.config.ConfigSection;
import it.ytnoos.lpx.config.messages.MessagesConfig;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoggerConfig extends ConfigSection {
   private static final String SECTION = "packet-logger";
   private final long heavyPacketThreshold = this.getLong("heavy-packet-threshold", 5000000L);
   private final long heavyListedPacketThreshold = this.getLong("heavy-listed-packet-threshold", 50000000L);
   private final Set<String> heavyIgnoredPackets = new HashSet<>(this.getStringList("heavy-ignored-packets"));
   private final Set<String> heavyListedPackets = new HashSet<>(this.getStringList("heavy-listed-packets"));
   private final boolean enabled = this.getBoolean("enabled");
   private final OutputType outputType = this.parseOutputType(this.getMessage("output"));
   private final WatchdogConfig watchdogConfig;
   private final FilterMode playerFilterMode;
   private final List<String> players;
   private final FilterMode packetFilterMode;
   private final List<String> packets;

   public LoggerConfig(MessagesConfig messagesConfig, LPXPlugin lpxPlugin) {
      super(lpxPlugin, "packet-logger", messagesConfig);
      this.watchdogConfig = new WatchdogConfig(lpxPlugin, "packet-logger", messagesConfig);
      this.playerFilterMode = this.parseFilterMode(this.getMessage("player-mode"));
      this.players = this.getStringList("players");
      this.packetFilterMode = this.parseFilterMode(this.getMessage("packet-mode"));
      this.packets = this.getStringList("packets");
   }

   private FilterMode parseFilterMode(String string) {
      try {
         return FilterMode.valueOf(string.toUpperCase());
      } catch (IllegalArgumentException illegalArgumentException) {
         this.plugin.getLogger().warning("Invalid logger type " + string + ", using default value (whitelist)");
         return FilterMode.WHITELIST;
      }
   }

   private OutputType parseOutputType(String string) {
      try {
         return OutputType.valueOf(string.toUpperCase());
      } catch (IllegalArgumentException illegalArgumentException) {
         this.plugin.getLogger().warning("Invalid output type " + string + ", using default value (file)");
         return OutputType.FILE;
      }
   }

   public boolean isPlayerLogged(String string) {
      return this.playerFilterMode == FilterMode.WHITELIST ? this.players.contains(string) : !this.players.contains(string);
   }

   public boolean isPacketLogged(String string) {
      return this.packetFilterMode == FilterMode.WHITELIST ? this.packets.contains(string) : !this.packets.contains(string);
   }

   public boolean isHeavy(long longValue) {
      return this.heavyPacketThreshold == -1L ? false : longValue >= this.heavyPacketThreshold;
   }

   public boolean isHeavyOverThreshold(Object object, long longValue) {
      return this.heavyListedPacketThreshold == -1L ? false : this.heavyListedPackets.contains(object) && longValue >= this.heavyListedPacketThreshold;
   }

   public boolean isHeavyListed(Object object) {
      return this.heavyListedPackets.contains(object);
   }

   public boolean isHeavyIgnored(Object object) {
      return this.heavyIgnoredPackets.contains(object);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public WatchdogConfig getWatchdogConfig() {
      return this.watchdogConfig;
   }

   public OutputType getOutputType() {
      return this.outputType;
   }
}
