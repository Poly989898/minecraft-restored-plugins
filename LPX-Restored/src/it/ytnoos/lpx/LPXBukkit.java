package it.ytnoos.lpx;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import io.netty.channel.Channel;
import it.ytnoos.lpx.alert.AlertManager;
import it.ytnoos.lpx.api.LPX;
import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.api.adapter.InventoryViewAdapter;
import it.ytnoos.lpx.api.check.abstraction.CheckManager;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.api.protocol.PlayerRegistry;
import it.ytnoos.lpx.check.BukkitCheckManager;
import it.ytnoos.lpx.check.PendingPunishment;
import it.ytnoos.lpx.commands.LPXCommand;
import it.ytnoos.lpx.common.utils.NettyUtils;
import it.ytnoos.lpx.config.ConfigManager;
import it.ytnoos.lpx.inventories.InventoryContainer;
import it.ytnoos.lpx.inventories.smartinvs.InventoryManager;
import it.ytnoos.lpx.logger.PacketLogger;
import it.ytnoos.lpx.monitor.PacketTickMonitor;
import it.ytnoos.lpx.player.PlayerRegistryImpl;
import it.ytnoos.lpx.plugin.LPXBukkitPlugin;
import it.ytnoos.lpx.utils.string.MessageFormatter;
import it.ytnoos.lpx.watchdog.NettyWatchdog;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

public class LPXBukkit implements LPX {
   private final LPXBukkitPlugin lpxBukkitPlugin;
   private final PlayerRegistry registry;
   private final CheckManager checkManager;
   private final ConfigManager configManager;
   private final InventoryManager inventoryManager;
   private final InventoryContainer inventoryContainer;
   private final PacketLogger packetLogger;
   private final AlertManager alertManager;
   private final ArrayDeque<PendingPunishment> globalViolations;
   private final PacketTickMonitor packetTickMonitor;
   private final NettyWatchdog nettyWatchdog;
   private boolean updated = true;
   private boolean disabled = false;
   private long lastTick;
   @Nullable
   private LPXCommand lpxCommand;

   public LPXBukkit(LPXBukkitPlugin lpxBukkitPlugin2) {
      this.lpxBukkitPlugin = lpxBukkitPlugin2;
      this.globalViolations = new ArrayDeque<>();
      this.configManager = new ConfigManager(lpxBukkitPlugin2);
      this.alertManager = new AlertManager(this);
      this.registry = new PlayerRegistryImpl(this);
      this.inventoryManager = new InventoryManager(this);
      this.inventoryContainer = new InventoryContainer(this);
      this.packetLogger = new PacketLogger(this);
      this.checkManager = new BukkitCheckManager(this);
      this.packetTickMonitor = new PacketTickMonitor(this);
      this.nettyWatchdog = new NettyWatchdog(this);
      PacketEvents.getAPI().init();

      try {
         new LPXInit().enable(this);
      } catch (Exception exception) {
         this.getLogger()
            .log(
               Level.SEVERE,
               exception,
               () -> "Unable to enable LPX"
            );
         this.disable(false);
      }
   }

   @Override
   public void disable(boolean flag) {
      if (!this.disabled) {
         this.disabled = true;
         if (!flag) {
            this.lpxBukkitPlugin.getServer().getPluginManager().disablePlugin(this.lpxBukkitPlugin);
         }

         if (this.registry != null) {
            this.registry.uninjectAll();
         }

         if (this.packetLogger != null) {
            this.packetLogger.shutdown();
         }

         if (this.nettyWatchdog != null) {
            this.nettyWatchdog.disable();
         }

         this.packetTickMonitor.stop();
         PacketEvents.getAPI().terminate();
         this.alertManager.shutdown();
      }
   }

   @Override
   public void reloadConfig() {
      this.configManager.reload(this);
      if (this.lpxCommand != null) {
         this.lpxCommand.updateHiddenCommand();
         if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
            for (Player player : this.lpxBukkitPlugin.getServer().getOnlinePlayers()) {
               FoliaScheduler.getEntityScheduler().run(player, this.lpxBukkitPlugin, object -> player.updateCommands(), null);
            }
         }
      }
   }

   public LPXCommand getLpxCommand() {
      return this.lpxCommand;
   }

   public void setLpxCommand(LPXCommand lpxCommand2) {
      this.lpxCommand = lpxCommand2;
   }

   @Override
   public InventoryViewAdapter getInventoryViewAdapter(Player player) {
      return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21) ? new ModernInventoryViewAdapter(player) : new LegacyInventoryViewAdapter(player);
   }

   public void registerListener(Listener listener) {
      this.lpxBukkitPlugin.getServer().getPluginManager().registerEvents(listener, this.lpxBukkitPlugin);
   }

   public void registerListeners(Listener[] listeners) {
      Listener[] listeners2 = listeners;

      for (Listener listener : listeners2) {
         this.registerListener(listener);
      }
   }

   @Override
   public String getServer() {
      return this.configManager.getOptionsConfig().getServer();
   }

   @Override
   public LPXPlugin getPlugin() {
      return this.lpxBukkitPlugin;
   }

   @Override
   public PlayerRegistry getRegistry() {
      return this.registry;
   }

   @Override
   public CheckManager getCheckManager() {
      return this.checkManager;
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   public AlertManager getAlertManager() {
      return this.alertManager;
   }

   public PacketLogger getPacketLogger() {
      return this.packetLogger;
   }

   @Override
   public boolean isUpdated() {
      return this.updated;
   }

   @Override
   public void setUpdated(boolean flag) {
      this.updated = flag;
   }

   public InventoryManager getInventoryManager() {
      return this.inventoryManager;
   }

   public InventoryContainer getInventoryContainer() {
      return this.inventoryContainer;
   }

   public Deque getRecentViolations() {
      return this.globalViolations;
   }

   public PacketTickMonitor getPacketTickMonitor() {
      return this.packetTickMonitor;
   }

   public NettyWatchdog getNettyWatchdog() {
      return this.nettyWatchdog;
   }

   @Override
   public long getFirstJoinTime() {
      return this.lastTick;
   }

   @Override
   public void setFirstJoinTime(long longValue) {
      this.lastTick = longValue;
   }

   @Override
   public boolean canPacketLogHeavy() {
      return this.lastTick != 0L && System.currentTimeMillis() - this.lastTick > 5000L;
   }

   public void kick(Player player, String string) {
      this.registry.getPlayer(player).ifPresent(protocolPlayer -> protocolPlayer.kick(string));
   }

   public void forceKick(ProtocolPlayer protocolPlayer, String string) {
      User user = protocolPlayer.getUser();
      protocolPlayer.getPlayer()
         .ifPresent(player -> PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDisconnect(MessageFormatter.deserialize(string))));

      try {
         if (user != null) {
            NettyUtils.closeChannel((Channel)user.getChannel());
         }
      } catch (Exception exception) {
         this.getLogger().log(Level.SEVERE, exception, () -> "Unable to force kick player " + user.getName());
      }
   }

   public void debug(Supplier supplier) {
      if (this.configManager.getOptionsConfig().isDebug()) {
         this.getLogger().info(supplier);
      }
   }
}
