package it.ytnoos.lpx.config;

import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.config.messages.MessagesConfig;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigSection {
   protected final LPXPlugin plugin;
   protected final String key;
   @Nullable
   private MessagesConfig messagesConfig;

   public ConfigSection(LPXPlugin lpxPlugin, String string, @Nullable MessagesConfig messagesConfig2) {
      this.plugin = lpxPlugin;
      this.key = string;
      this.messagesConfig = messagesConfig2;
   }

   public void setMessagesConfig(@Nullable MessagesConfig messagesConfig2) {
      this.messagesConfig = messagesConfig2;
   }

   public FileConfiguration getConfig() {
      return this.plugin.getConfig();
   }

   protected String getMessage(String string) {
      return this.resolveMessage(this.getConfig().getString(this.key + "." + string));
   }

   protected boolean getBoolean(String string) {
      return this.getConfig().getBoolean(this.key + "." + string);
   }

   protected boolean getBoolean(String string, boolean flag) {
      return this.getConfig().getBoolean(this.key + "." + string, flag);
   }

   protected long getLong(String string, long longValue) {
      return this.getConfig().getLong(this.key + "." + string, longValue);
   }

   protected int getInt(String string) {
      return this.getConfig().getInt(this.key + "." + string);
   }

   protected List getStringList(String string) {
      List list = this.getConfig().getStringList(this.key + "." + string);

      for (int value = 0; value < list.size(); value++) {
         list.set(value, this.resolveMessage((String)list.get(value)));
      }

      return list;
   }

   private String resolveMessage(String string) {
      String string2 = string;
      if (string2 == null) {
         return this.key + "." + this.key;
      }

      if (this.messagesConfig != null) {
         string2 = string2.replace("%main-color%", this.messagesConfig.getMainColor());
         string2 = string2.replace("%second-color%", this.messagesConfig.getSecondColor());
         if (this.messagesConfig.getPrefix() != null) {
            string2 = string2.replace("%prefix%", this.messagesConfig.getPrefix());
         }
      }

      return this.colorize(string2);
   }

   private String colorize(String string) {
      return ChatColor.translateAlternateColorCodes('&', string);
   }

}
