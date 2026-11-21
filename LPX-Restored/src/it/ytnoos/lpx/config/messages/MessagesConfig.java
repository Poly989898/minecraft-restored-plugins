package it.ytnoos.lpx.config.messages;

import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.config.AlertsConfig;
import it.ytnoos.lpx.config.ConfigSection;

public class MessagesConfig extends ConfigSection {
   private static final String SECTION = "messages";
   private final String mainColor = this.getMessage("main-color");
   private final String secondColor = this.getMessage("second-color");
   private final String prefix;
   private final String kick;
   private final String kickAlert;
   private final String noPermission;
   private final String invalidArguments;
   private final String injectionFailure;
   private final String updateFound;
   private final String nullAddress;
   private final String unknownCommand;
   private final AlertsConfig alertsConfig;

   public MessagesConfig(LPXPlugin lpxPlugin) {
      super(lpxPlugin, "messages", null);
      this.setMessagesConfig(this);
      this.prefix = this.getMessage("prefix");
      this.kick = this.getMessage("kick");
      this.kickAlert = this.getMessage("kick-alert");
      this.noPermission = this.getMessage("no-permission");
      this.invalidArguments = this.getMessage("invalid-arguments");
      this.injectionFailure = this.getMessage("injection-failure");
      this.updateFound = this.getMessage("update-found");
      this.nullAddress = this.getMessage("null-address");
      this.unknownCommand = this.getMessage("unknown-command");
      this.alertsConfig = new AlertsConfig(lpxPlugin, this, "messages");
   }

   public String getMainColor() {
      return this.mainColor;
   }

   public String getSecondColor() {
      return this.secondColor;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public String getKick() {
      return this.kick;
   }

   public String getKickAlert() {
      return this.kickAlert;
   }

   public String getNoPermission() {
      return this.noPermission;
   }

   public String getInvalidArguments() {
      return this.invalidArguments;
   }

   public String getInjectionFailure() {
      return this.injectionFailure;
   }

   public String getUpdateFound() {
      return this.updateFound;
   }

   public String getNullAddress() {
      return this.nullAddress;
   }

   public String getUnknownCommand() {
      return this.unknownCommand;
   }

   public AlertsConfig getAlertsConfig() {
      return this.alertsConfig;
   }

}
