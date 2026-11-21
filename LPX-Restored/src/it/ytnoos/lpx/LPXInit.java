package it.ytnoos.lpx;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.manager.server.VersionComparison;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import it.ytnoos.lpx.api.LPX;
import it.ytnoos.lpx.api.metrics.MetricsLite;
import it.ytnoos.lpx.commands.LPXCommand;
import it.ytnoos.lpx.listeners.CommandSendListener;
import it.ytnoos.lpx.listeners.InventoryListener;
import it.ytnoos.lpx.listeners.LPXListener;
import it.ytnoos.lpx.listeners.PlayerLegacyListener;
import it.ytnoos.lpx.listeners.PlayerListener;
import it.ytnoos.lpx.listeners.PreViaListener;
import it.ytnoos.lpx.listeners.RedstoneListener;
import it.ytnoos.lpx.listeners.ServerPacketListener;
import it.ytnoos.lpx.tasks.ClearTask;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;

public class LPXInit {
   private static boolean called = false;
   private static final String ENABLED_MESSAGE = "LPX enabled!";

   public LPXInit() {
   }

   public void enable(LPXBukkit lpxBukkit) {
      if (!called) {
         called = true;
         lpxBukkit.getConfigManager().load(lpxBukkit.getPlugin());
         lpxBukkit.getAlertManager().reload();
         lpxBukkit.getCheckManager().registerChecks();
         lpxBukkit.getInventoryManager().init();
         lpxBukkit.registerListeners(new Listener[]{new PlayerListener(lpxBukkit), new InventoryListener(lpxBukkit)});
         ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
         if (serverVersion.is(VersionComparison.NEWER_THAN_OR_EQUALS, ServerVersion.V_1_13) && serverVersion.is(VersionComparison.OLDER_THAN, ServerVersion.V_1_19_4)) {
            lpxBukkit.registerListener(new RedstoneListener(lpxBukkit));
         }

         if (serverVersion.is(VersionComparison.NEWER_THAN_OR_EQUALS, ServerVersion.V_1_13)) {
            lpxBukkit.registerListener(new CommandSendListener(lpxBukkit));
         }

         if (serverVersion.is(VersionComparison.OLDER_THAN_OR_EQUALS, ServerVersion.V_1_8_8)) {
            lpxBukkit.registerListener(new PlayerLegacyListener(lpxBukkit));
         }

         lpxBukkit.setLpxCommand(new LPXCommand(lpxBukkit));
         if (lpxBukkit.getConfigManager().getOptionsConfig().isClearTaskEnabled()) {
            FoliaScheduler.getAsyncScheduler()
               .runAtFixedRate(lpxBukkit.getPlugin(), object -> new ClearTask(lpxBukkit).run(), 1L, lpxBukkit.getConfigManager().getOptionsConfig().getClearTaskDelay() * 20L);
         }

         if (lpxBukkit.getConfigManager().getOptionsConfig().isBStatsEnabled()) {
            new MetricsLite(lpxBukkit.getPlugin());
         }

         lpxBukkit.getLogger().log(Level.INFO, ENABLED_MESSAGE);
         LPXListener lpxListener = new LPXListener(lpxBukkit);
         PacketEvents.getAPI().getEventManager().registerListener(new PreViaListener(lpxListener));
         PacketEvents.getAPI().getEventManager().registerListener(new ServerPacketListener(lpxBukkit, lpxListener));
         Bukkit.getServicesManager().register(LPX.class, lpxBukkit, lpxBukkit.getPlugin(), ServicePriority.Normal);
      }
   }

}
