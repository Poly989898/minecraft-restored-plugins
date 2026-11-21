package it.ytnoos.lpx.config.logger;

import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.config.ConfigSection;
import it.ytnoos.lpx.config.messages.MessagesConfig;

public class WatchdogConfig extends ConfigSection {
   private static final String SECTION = "printer";
   private final long maxTime = this.getLong("max-time", 50L);
   private final long checkFrequency = this.getLong("check-frequency", 25L);
   private final long reportCooldown = this.getLong("report-cooldown", 5000L);

   public WatchdogConfig(LPXPlugin lpxPlugin, String string, MessagesConfig messagesConfig) {
      super(lpxPlugin, string + ".printer", messagesConfig);
   }

   public long getMaxTime() {
      return this.maxTime;
   }

   public long getCheckFrequency() {
      return this.checkFrequency;
   }

   public long getReportCooldown() {
      return this.reportCooldown;
   }

}
