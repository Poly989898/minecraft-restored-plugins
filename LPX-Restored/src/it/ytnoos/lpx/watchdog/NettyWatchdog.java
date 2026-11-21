package it.ytnoos.lpx.watchdog;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.config.ConfigManager;
import it.ytnoos.lpx.logger.PacketLogger;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NettyWatchdog implements Listener {
   private final LPXBukkit lpxBukkit;
   private final ConfigManager configManager;
   private final Map<Long, NettyWatchdog.NettyThread> timings = new HashMap<>();
   private final AtomicBoolean running = new AtomicBoolean(false);
   private ScheduledExecutorService watchdog;

   public NettyWatchdog(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
      this.configManager = lpxBukkit2.getConfigManager();
   }

   public synchronized void toggle() {
      if (this.running.get()) {
         this.disable();
      } else {
         this.enable();
      }
   }

   private void enable() {
      this.watchdog = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("lpx-netty-watchdog-%d").setDaemon(true).build());
      this.watchdog
         .scheduleAtFixedRate(
            () -> {
               if (this.running.get()) {
                  long longValue = System.currentTimeMillis();

                  for (Entry entry : this.timings.entrySet()) {
                     NettyWatchdog.NettyThread nettyThread = (NettyWatchdog.NettyThread)entry.getValue();
                     long longValue2 = longValue - nettyThread.lastHeartbeat;
                     long longValue3 = longValue - nettyThread.lastReport;
                     if (longValue2 > this.configManager.getLoggerConfig().getWatchdogConfig().getMaxTime()
                        && longValue3 > this.configManager.getLoggerConfig().getWatchdogConfig().getReportCooldown()) {
                        PacketLogger packetLogger = this.lpxBukkit.getPacketLogger();
                        ThreadInfo threadInfo = ManagementFactory.getThreadMXBean().getThreadInfo(new long[]{(Long)entry.getKey()}, true, true)[0];
                        StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();

                        for (StackTraceElement stackTraceElement : stackTraceElements) {
                           if (stackTraceElement.toString().contains("loadClass")) {
                              return;
                           }
                        }

                        packetLogger.logMessage(() -> {
                           long longValue4 = this.configManager.getLoggerConfig().getWatchdogConfig().getMaxTime();
                           return "Netty thread has stopped responding for more than " + longValue4 + "ms! (Delay: " + longValue2 + "ms)";
                        });
                        packetLogger.logMessage(() -> {
                           String string = threadInfo.getThreadName();
                           long longValue5 = threadInfo.getThreadId();
                           boolean flag = threadInfo.isSuspended();
                           String string2 = String.valueOf(threadInfo.getThreadState());
                           boolean flag2 = threadInfo.isInNative();
                           boolean flag3 = flag;
                           long longValue6 = longValue5;
                           String string3 = string;
                           return "\tName: " + string3 + " | PID: " + longValue6 + " | Suspended: " + flag3 + " | Native: " + flag2 + " | State: " + string2;
                        });
                        packetLogger.logMessage(() -> "------------------------------");
                        packetLogger.logMessage(() -> "Thread Information");
                        this.dumpThread(threadInfo, stackTraceElements, packetLogger);
                        packetLogger.logMessage(() -> "------------------------------");
                        nettyThread.lastReport = longValue;
                     }
                  }
               }
            },
            this.configManager.getLoggerConfig().getWatchdogConfig().getCheckFrequency(),
            this.configManager.getLoggerConfig().getWatchdogConfig().getCheckFrequency(),
            TimeUnit.MILLISECONDS
         );
      this.lpxBukkit.getPlugin().getServer().getPluginManager().registerEvents(this, this.lpxBukkit.getPlugin());
      this.running.set(true);
   }

   public synchronized void disable() {
      if (this.running.get()) {
         HandlerList.unregisterAll(this);
         this.watchdog.shutdown();

         try {
            if (!this.watchdog.awaitTermination(10L, TimeUnit.SECONDS)) {
               this.watchdog.shutdownNow();
            }
         } catch (InterruptedException interruptedException) {
            this.watchdog.shutdownNow();
            Thread.currentThread().interrupt();
         } finally {
            this.running.set(false);
         }
      }
   }

   public boolean isRunning() {
      return this.running.get();
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent playerJoinEvent) {
      this.watchPlayer(playerJoinEvent.getPlayer());
   }

   private void watchPlayer(Player player) {
      User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
      if (user != null) {
         Object object = user.getChannel();
         if (object != null) {
            if (!player.hasMetadata("fake")) {
               this.watchChannel((Channel)object);
            }
         }
      }
   }

   public void watchChannel(Channel channel) {
      channel.eventLoop().submit(() -> {
         String string = Thread.currentThread().getName();
         long longValue = Thread.currentThread().getId();
         this.watchdog.execute(() -> this.timings.computeIfAbsent(longValue, long2 -> {
            NettyWatchdog.NettyThread nettyThread = new NettyWatchdog.NettyThread(string);
            nettyThread.lastHeartbeat = System.currentTimeMillis();
            channel.eventLoop().scheduleAtFixedRate(() -> nettyThread.lastHeartbeat = System.currentTimeMillis(), 1L, 1L, TimeUnit.MILLISECONDS);
            return nettyThread;
         }));
      });
   }

   private void dumpThread(ThreadInfo threadInfo, StackTraceElement[] stackTraceElements, PacketLogger packetLogger) {
      StackTraceElement[] stackTraceElements2 = stackTraceElements;
      if (threadInfo.getLockedMonitors().length != 0) {
         packetLogger.logMessage(() -> "\tThread is waiting on monitor(s):");

         for (MonitorInfo monitorInfo : threadInfo.getLockedMonitors()) {
            packetLogger.logMessage(() -> "\t\tLocked on: " + monitorInfo.getLockedStackFrame());
         }
      }

      if (threadInfo.getLockedSynchronizers().length != 0) {
         packetLogger.logMessage(() -> "\tThread is waiting on lock(s):");

         for (LockInfo lockInfo : threadInfo.getLockedSynchronizers()) {
            packetLogger.logMessage(() -> "\t\tLocked on: " + lockInfo.toString());
         }
      }

      packetLogger.logMessage(() -> "\tStack:");

      for (StackTraceElement stackTraceElement : stackTraceElements2) {
         packetLogger.logMessage(() -> "\t\t" + stackTraceElement);
      }
   }

   public static class NettyThread {
      private final String name;
      private volatile long lastHeartbeat;
      private volatile long lastReport;

      public NettyThread(String string) {
         this.name = string;
      }

      public long getLastHeartbeat() {
         return this.lastHeartbeat;
      }

      public void setLastHeartbeat(long longValue) {
         this.lastHeartbeat = longValue;
      }

      public long getLastReport() {
         return this.lastReport;
      }

      public void setLastReport(long longValue) {
         this.lastReport = longValue;
      }

      public String getName() {
         return this.name;
      }
   }
}
