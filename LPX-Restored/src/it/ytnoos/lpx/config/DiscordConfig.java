package it.ytnoos.lpx.config;

import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.config.messages.MessagesConfig;
import java.util.List;

public class DiscordConfig extends ConfigSection {
   private static final String SECTION = "discord";
   private final boolean enabled = this.getBoolean("enabled");
   private final String url = this.getMessage("url");
   private final String color = this.getConfig().getString(this.key + ".color");
   private final List<String> content = this.getStringList("content");

   public DiscordConfig(MessagesConfig messagesConfig, LPXPlugin lpxPlugin) {
      super(lpxPlugin, "discord", messagesConfig);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public String getUrl() {
      return this.url;
   }

   public String getColor() {
      return this.color;
   }

   public List getContent() {
      return this.content;
   }
}
