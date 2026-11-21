package it.ytnoos.lpx.config;

import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.config.messages.MessagesConfig;
import java.util.List;

public class AlertsConfig extends ConfigSection {
   private static final String SECTION = "alerts";
   private final String permission = this.getMessage("permission");
   private final String format = this.getMessage("format");
   private final List<String> hover = this.getStringList("hover");
   private final String enabled = this.getMessage("enabled");
   private final String disabled = this.getMessage("disabled");

   public AlertsConfig(LPXPlugin lpxPlugin, MessagesConfig messagesConfig, String string) {
      super(lpxPlugin, string + ".alerts", messagesConfig);
   }

   public String getPermission() {
      return this.permission;
   }

   public String getFormat() {
      return this.format;
   }

   public List getHover() {
      return this.hover;
   }

   public String getEnabled() {
      return this.enabled;
   }

   public String getDisabled() {
      return this.disabled;
   }

}
