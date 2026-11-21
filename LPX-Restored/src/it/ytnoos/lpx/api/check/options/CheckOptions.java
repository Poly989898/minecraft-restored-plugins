package it.ytnoos.lpx.api.check.options;

import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.api.check.enums.CheckType;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class CheckOptions {
   private final LPXPlugin plugin;
   private final String path;
   private final CheckType type;
   private boolean enabled;
   private boolean punish;
   private int maxVL;
   private int minVl;
   private List<String> punishCommands;
   private int maxBuffer;
   private int bufferMultiplier;
   private int bufferDecay;

   public CheckOptions(LPXPlugin lpxPlugin, CheckType checkType) {
      this.plugin = lpxPlugin;
      this.type = checkType;
      this.path = "checks." + checkType.getCategory().name().toLowerCase() + "." + checkType.getId().name().toLowerCase() + ".";
      this.reload();
   }

   public void reload() {
      this.enabled = this.getConfig().getBoolean(this.path + "enabled", true);
      this.punish = this.getConfig().getBoolean(this.path + "punish");
      this.maxVL = this.getConfig().getInt(this.path + "max-vl");
      this.minVl = this.getConfig().getInt(this.path + "min-vl");
      this.punishCommands = this.getConfig().getStringList(this.path + "punish-commands");

      for (int value = 0; value < this.punishCommands.size(); value++) {
         this.punishCommands.set(value, ChatColor.translateAlternateColorCodes('&', this.punishCommands.get(value)));
      }

      this.maxBuffer = this.getConfig().getInt(this.path + "buffer.max", 1);
      this.bufferMultiplier = this.getConfig().getInt(this.path + "buffer.multiply", 0);
      this.bufferDecay = this.getConfig().getInt(this.path + "buffer.decay", 1);
   }

   public LPXPlugin getPlugin() {
      return this.plugin;
   }

   public FileConfiguration getConfig() {
      return this.plugin.getConfig();
   }

   public String getPath() {
      return this.path;
   }

   public CheckType getType() {
      return this.type;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isPunish() {
      return this.punish;
   }

   public int getMaxVL() {
      return this.maxVL;
   }

   public int getMinVl() {
      return this.minVl;
   }

   public List<String> getPunishCommands() {
      return this.punishCommands;
   }

   public int getMaxBuffer() {
      return this.maxBuffer;
   }

   public int getBufferMultiplier() {
      return this.bufferMultiplier;
   }

   public int getBufferDecay() {
      return this.bufferDecay;
   }

}
