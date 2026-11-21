package it.ytnoos.lpx.plugin;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.LPX;
import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.config.CustomConfig;
import it.ytnoos.lpx.logger.PacketEventsExceptionHandler;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LPXBukkitPlugin extends JavaPlugin implements LPXPlugin {
   private LPX lpx;
   private FileConfiguration config;
   private File file;

   public LPXBukkitPlugin() {
   }

   public void onLoad() {
      PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
      PacketEvents.getAPI().getSettings().reEncodeByDefault(false).checkForUpdates(false).fullStackTrace(false).kickOnPacketException(true);
      PacketEvents.getAPI().load();
   }

   public void onEnable() {
      FileConfiguration v8 = super.getConfig();
      if (v8.getBoolean("options.external-config", false)) {
         CustomConfig v9 = new CustomConfig(this, "config.yml", true);
         this.config = v9.getConfiguration();
         this.file = v9.getFile();
      } else {
         this.config = v8;
         this.file = null;
      }

      this.lpx = new LPXBukkit(this);
      PacketEvents.getAPI().getSettings().packetExceptionHandler(new PacketEventsExceptionHandler((LPXBukkit)this.lpx));
   }

   public void onDisable() {
      PacketEvents.getAPI().getSettings().packetExceptionHandler(null);
      if (this.lpx != null) {
         this.lpx.disable(true);
      }
   }

   @Override
   public File getConfigFile() {
      return this.file != null ? this.file : new File(this.getDataFolder(), "config.yml");
   }

   @Override
   public FileConfiguration getConfig() {
      return this.config;
   }

   @Override
   public void saveConfig() {
      if (this.file != null) {
         try {
            this.config.save(this.file);
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      } else {
         super.saveConfig();
      }
   }

   @Override
   public void reload() {
      if (super.getConfig().getBoolean("options.external-config", false)) {
         this.config = new CustomConfig(this, "config.yml", true).getConfiguration();
      } else {
         super.reloadConfig();
         this.config = super.getConfig();
      }
   }
}
