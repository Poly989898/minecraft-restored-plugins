package it.ytnoos.lpx.logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.protocol.ReceivedPacket;
import it.ytnoos.lpx.config.logger.OutputType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.function.Supplier;

public class PacketLogger {
   private static final int MAX_QUEUE = 1000;
   private final LPXBukkit lpxBukkit;
   private final ExecutorService executor;

   public PacketLogger(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
      ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("lpx-packet-logger-%d").setDaemon(true).build();
      this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1000), threadFactory, new DiscardPolicy());
   }

   public void logPacket(ReceivedPacket receivedPacket) {
      this.logMessage(() -> {
         String string = receivedPacket.getPlayerName();
         String string2 = receivedPacket.getClientVersionName();
         String string3 = receivedPacket.getPacketType().getName();
         String string4 = receivedPacket.getFormattedElapsedTime();
         String string5 = String.valueOf(receivedPacket);
         long longValue = receivedPacket.getSize();
         String string6 = string4;
         String string7 = string3;
         String string8 = string2;
         return string + " [" + string8 + "] sent " + string7 + " (" + string6 + "ms) (" + longValue + " bytes) " + string5;
      });
   }

   public void logMessage(Supplier supplier) {
      if (!this.executor.isShutdown()) {
         try {
            OutputType outputType = this.lpxBukkit.getConfigManager().getLoggerConfig().getOutputType();
            if (outputType == OutputType.CONSOLE || outputType == OutputType.BOTH) {
               this.executor.execute(() -> this.lpxBukkit.getPlugin().getLogger().info(supplier));
            }

            if (outputType == OutputType.FILE || outputType == OutputType.BOTH) {
               this.lpxBukkit.getAlertManager().getLogWriter().writeString(supplier);
            }
         } catch (RejectedExecutionException rejectedExecutionException) {
         }
      }
   }

   public void shutdown() {
      this.executor.shutdown();

      try {
         if (!this.executor.awaitTermination(5L, TimeUnit.SECONDS)) {
            this.executor.shutdownNow();
         }
      } catch (InterruptedException interruptedException) {
         this.executor.shutdownNow();
         Thread.currentThread().interrupt();
      }
   }
}
