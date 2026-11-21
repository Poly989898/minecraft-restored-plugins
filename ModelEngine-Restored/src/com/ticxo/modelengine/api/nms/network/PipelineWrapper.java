package com.ticxo.modelengine.api.nms.network;

import com.ticxo.modelengine.api.utils.config.DebugToggle;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import io.netty.channel.Channel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@Getter
public class PipelineWrapper {
   private static final ScheduledExecutorService SCHEDULED_SERVICE = Executors.newScheduledThreadPool(10);
   private final Player player;
   private final Channel channel;
   private final boolean fakeChannel;
   private final Consumer<Object> flushAndWrite;
   private final ClientDesyncMonitor desyncMonitor;
   private long delay;

   public PipelineWrapper(Player player, Channel channel) {
      this.player = player;
      this.channel = channel;
      this.fakeChannel = isFakeChannel(channel);
      if (this.fakeChannel) {
         TLogger.warn("Model packets will not be sent to " + player.getName() + ": " + channel.getClass().getName() + " is not a standard player connection.");
         this.flushAndWrite = msg -> {};
      } else {
         this.flushAndWrite = msg -> {
            if (channel.isActive()) {
               channel.writeAndFlush(msg);
            }
         };
      }

      this.desyncMonitor = new ClientDesyncMonitor(this);
   }

   private static boolean isFakeChannel(Channel channel) {
      try {
         return channel.eventLoop().inEventLoop();
      } catch (Throwable ignored) {
         return true;
      }
   }

   public void setDelay(long delay) {
      this.delay = delay;
      if (DebugToggle.isDebugging(DebugToggle.NOTIFY_DESYNC)) {
         this.player.sendMessage(Component.text("Client delay: " + delay));
      }
   }

   public void writeAndFlush(Object object) {
      if (this.delay <= 0L) {
         this.flushAndWrite.accept(object);
      } else {
         SCHEDULED_SERVICE.schedule(() -> this.flushAndWrite.accept(object), this.delay, TimeUnit.MILLISECONDS);
      }
   }

   public void writeAndFlushNow(Object object) {
      this.flushAndWrite.accept(object);
   }
}
