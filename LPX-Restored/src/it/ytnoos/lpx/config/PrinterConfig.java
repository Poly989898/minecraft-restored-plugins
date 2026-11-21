package it.ytnoos.lpx.config;

import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.config.messages.MessagesConfig;

public class PrinterConfig extends ConfigSection {
   private static final String SECTION = "printer";
   private final boolean automatic = this.getBoolean("automatic");
   private final int placeThreshold = this.getInt("place-threshold");
   private final int disableDelay = this.getInt("disable-delay");
   private final boolean alerts = this.getBoolean("alerts");
   private final String permission = this.getMessage("permission");
   private final String join = this.getMessage("join");
   private final String leave = this.getMessage("leave");

   public PrinterConfig(LPXPlugin lpxPlugin, String string, MessagesConfig messagesConfig) {
      super(lpxPlugin, string + ".printer", messagesConfig);
   }

   public boolean isAutomatic() {
      return this.automatic;
   }

   public int getPlaceThreshold() {
      return this.placeThreshold;
   }

   public int getDisableDelay() {
      return this.disableDelay;
   }

   public boolean isAlerts() {
      return this.alerts;
   }

   public String getPermission() {
      return this.permission;
   }

   public String getJoin() {
      return this.join;
   }

   public String getLeave() {
      return this.leave;
   }

}
