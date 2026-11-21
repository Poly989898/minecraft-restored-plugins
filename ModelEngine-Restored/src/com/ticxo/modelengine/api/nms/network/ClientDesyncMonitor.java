package com.ticxo.modelengine.api.nms.network;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDesyncMonitor {
   private static long CLIENT_SYNC_INTERVAL = 10000L;
   private final PipelineWrapper pipeline;
   private final LinkedList<Long> clientSyncTime = new LinkedList<>();
   private final AtomicInteger pingCounter = new AtomicInteger();
   private boolean testing;
   private boolean hasPinged;
   private volatile boolean pingSent;
   private long lastTestTime;
   private long clientSyncOrigin;
   private long pongReceiveTime;

   public ClientDesyncMonitor(PipelineWrapper pipeline) {
      this.pipeline = pipeline;
      this.startTest();
   }

   public void startTest() {
      this.testing = true;
      this.hasPinged = false;
      this.pingSent = false;
      ModelEngineAPI.getAPI().getModelUpdaters().startDesyncMonitor(this.pipeline.getPlayer().getUniqueId());
   }

   public boolean shouldPing() {
      return this.testing && !this.pingSent;
   }

   public void markPingSent() {
      this.pingSent = true;
   }

   public void stopTest() {
      this.testing = false;
      this.lastTestTime = System.currentTimeMillis();
      ModelEngineAPI.getAPI().getModelUpdaters().stopDesyncMonitor(this.pipeline.getPlayer().getUniqueId());
      if (!this.clientSyncTime.isEmpty()) {
         this.clientSyncOrigin = this.clientSyncTime.getLast();
         long delta = (this.clientSyncOrigin - this.pongReceiveTime) % 50L;
         long deviation = Math.abs(delta - 25L);
         if (deviation <= 15L) {
            this.pipeline.setDelay(0L);
         } else {
            if (delta > 25L) {
               this.pipeline.setDelay(delta - 25L);
            } else {
               this.pipeline.setDelay(delta + 25L);
            }
         }
      }
   }

   public void recordClientSyncTime(long time) {
      this.clientSyncTime.add(time);
      if (this.clientSyncTime.size() > 10) {
         this.clientSyncTime.poll();
      }

      if (time > this.pongReceiveTime && this.testing && this.hasPinged) {
         this.stopTest();
      }
   }

   public void recordPongTime(long time) {
      if (this.testing) {
         this.pongReceiveTime = time;
         if (!this.clientSyncTime.isEmpty()) {
            if (time > this.clientSyncTime.getLast()) {
               this.hasPinged = true;
            }
         }
      }
   }

   public boolean clientTickShifted() {
      if (this.clientSyncTime.isEmpty()) {
         return true;
      }

      long last = this.clientSyncTime.getLast();
      return (last - this.clientSyncOrigin) % 50L < 40L;
   }

   public boolean shouldRetest() {
      return System.currentTimeMillis() - this.lastTestTime > CLIENT_SYNC_INTERVAL;
   }

   static {
      ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> CLIENT_SYNC_INTERVAL = ConfigProperty.CLIENT_SYNC_INTERVAL.getInt());
   }
}
