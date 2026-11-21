package it.ytnoos.lpx.monitor;

import io.netty.channel.EventLoop;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.monitor.tick.BukkitTickHook;
import it.ytnoos.lpx.monitor.tick.PaperTickHook;
import it.ytnoos.lpx.monitor.tick.TickHook;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class PacketTickMonitor {
   private static final int TPS = 20;
   private boolean running = false;
   private final RollingAverage delays10s = new RollingAverage(200);
   private final RollingAverage delays1m = new RollingAverage(1200);
   private final RollingAverage delays5m = new RollingAverage(6000);
   private final RollingAverage p95Delays10s = new RollingAverage(200);
   private final RollingAverage p95Delays1m = new RollingAverage(1200);
   private final RollingAverage maxDelays10s = new RollingAverage(200);
   private final RollingAverage maxDelays1m = new RollingAverage(1200);
   private final Map<EventLoop, Integer> threadIndexByLoop = new ConcurrentHashMap<>();
   private final Map<EventLoop, NettyThreadAccumulator> accumulatorByLoop = new ConcurrentHashMap<>();
   private final LPXBukkit lpxBukkit;
   private final TickHook tickHook;
   private static final String PAPER_TICK_EVENT = "com.destroystokyo.paper.event.server.ServerTickStartEvent";

   public PacketTickMonitor(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
      this.tickHook = this.createTickHook();
      this.tickHook.addCallback((TickHook.Callback)value -> this.onTick());
   }

   public void start() {
      if (!this.running) {
         this.running = true;
         this.tickHook.start();
      }
   }

   public void stop() {
      if (this.running) {
         this.tickHook.close();
         this.running = false;
      }
   }

   public boolean isRunning() {
      return this.running;
   }

   private NettyThreadAccumulator getOrCreateAccumulator(Thread thread, EventLoop eventLoop) {
      this.threadIndexByLoop.computeIfAbsent(eventLoop, eventLoop2 -> ThreadUtil.extractNumber(thread.getName()));
      return this.accumulatorByLoop.computeIfAbsent(eventLoop, eventLoop3 -> new NettyThreadAccumulator(20));
   }

   public void recordDelay(long longValue, EventLoop eventLoop) {
      if (this.running) {
         this.getOrCreateAccumulator(Thread.currentThread(), eventLoop).addSample(longValue);
      }
   }

   public void addPlayer(UUID uuid, Thread thread, EventLoop eventLoop) {
      this.getOrCreateAccumulator(thread, eventLoop).addPlayer(uuid);
   }

   public void removePlayer(UUID uuid, Thread thread, EventLoop eventLoop) {
      this.getOrCreateAccumulator(thread, eventLoop).removePlayer(uuid);
   }

   public void onTick() {
      ArrayList arrayList = new ArrayList();

      for (NettyThreadAccumulator nettyThreadAccumulator : this.accumulatorByLoop.values()) {
         ArrayList arrayList2 = new ArrayList();

         BigDecimal bigDecimal;
         while ((bigDecimal = (BigDecimal)nettyThreadAccumulator.getSamples().poll()) != null) {
            arrayList2.add(bigDecimal);
            arrayList.add(bigDecimal);
         }

         if (arrayList2.isEmpty()) {
            nettyThreadAccumulator.addAggregates(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
         } else {
            BigDecimal[] bigDecimals = (BigDecimal[]) arrayList2.toArray(new BigDecimal[0]);
            Arrays.sort(bigDecimals);
            BigDecimal bigDecimal2 = this.calculateAverage(bigDecimals);
            BigDecimal bigDecimal3 = this.getPercentile(bigDecimals, 95.0);
            BigDecimal bigDecimal4 = bigDecimals[bigDecimals.length - 1];
            nettyThreadAccumulator.addAggregates(bigDecimal2, bigDecimal3, bigDecimal4);
         }
      }

      if (arrayList.isEmpty()) {
         this.record(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
      } else {
         BigDecimal[] bigDecimals2 = (BigDecimal[]) arrayList.toArray(new BigDecimal[0]);
         Arrays.sort(bigDecimals2);
         BigDecimal bigDecimal5 = this.calculateAverage(bigDecimals2);
         BigDecimal bigDecimal6 = this.getPercentile(bigDecimals2, 95.0);
         BigDecimal bigDecimal7 = bigDecimals2[bigDecimals2.length - 1];
         this.record(bigDecimal5, bigDecimal6, bigDecimal7);
      }
   }

   private void record(BigDecimal bigDecimal, BigDecimal bigDecimal2, BigDecimal bigDecimal3) {
      this.delays10s.add(bigDecimal);
      this.delays1m.add(bigDecimal);
      this.delays5m.add(bigDecimal);
      this.p95Delays10s.add(bigDecimal2);
      this.p95Delays1m.add(bigDecimal2);
      this.maxDelays10s.add(bigDecimal3);
      this.maxDelays1m.add(bigDecimal3);
   }

   private BigDecimal calculateAverage(BigDecimal[] bigDecimals) {
      BigDecimal[] bigDecimals2 = bigDecimals;
      BigDecimal bigDecimal = BigDecimal.ZERO;

      for (BigDecimal bigDecimal2 : bigDecimals2) {
         bigDecimal = bigDecimal.add(bigDecimal2);
      }

      return bigDecimal.divide(new BigDecimal(bigDecimals2.length), 30, RoundingMode.HALF_UP);
   }

   private BigDecimal getPercentile(BigDecimal[] bigDecimals, double number) {
      BigDecimal[] bigDecimals2 = bigDecimals;
      int value = (int)Math.ceil(number / 100.0 * bigDecimals2.length);
      return bigDecimals2[Math.max(0, Math.min(value - 1, bigDecimals2.length - 1))];
   }

   public BigDecimal getMin10s() {
      return this.delays10s.getMin();
   }

   public BigDecimal getMin1m() {
      return this.delays1m.getMin();
   }

   public BigDecimal getMin5m() {
      return this.delays5m.getMin();
   }

   public BigDecimal getMedian10s() {
      return this.delays10s.getPercentile(50.0);
   }

   public BigDecimal getMedian1m() {
      return this.delays1m.getPercentile(50.0);
   }

   public BigDecimal getMedian5m() {
      return this.delays5m.getPercentile(50.0);
   }

   public BigDecimal getLowPercentile10s() {
      return this.delays10s.getPercentile(10.0);
   }

   public BigDecimal getHighPercentile10s() {
      return this.delays10s.getPercentile(90.0);
   }

   public BigDecimal getP95At10s() {
      return this.p95Delays10s.getPercentile(95.0);
   }

   public BigDecimal getP95At1m() {
      return this.p95Delays1m.getPercentile(95.0);
   }

   public BigDecimal getP95At5m() {
      return this.delays5m.getPercentile(95.0);
   }

   public BigDecimal getMax10s() {
      return this.maxDelays10s.getMax();
   }

   public BigDecimal getMax1m() {
      return this.maxDelays1m.getMax();
   }

   public BigDecimal getMax5m() {
      return this.delays5m.getMax();
   }

   public int getCurrentTick() {
      return this.tickHook.getCurrentTick();
   }

   public Map getThreadPlayers() {
      HashMap hashMap = new HashMap();

      for (Entry entry : this.threadIndexByLoop.entrySet()) {
         EventLoop eventLoop = (EventLoop)entry.getKey();
         Integer integer = (Integer)entry.getValue();
         NettyThreadAccumulator nettyThreadAccumulator = this.accumulatorByLoop.get(eventLoop);
         if (nettyThreadAccumulator != null) {
            hashMap.put(integer, new PacketTickMonitor.Sample(nettyThreadAccumulator.getHighPercentile(), nettyThreadAccumulator.getPlayerCount()));
         }
      }

      return hashMap;
   }

   private TickHook createTickHook() {
      return classExists(PAPER_TICK_EVENT) ? new PaperTickHook(this.lpxBukkit.getPlugin()) : new BukkitTickHook(this.lpxBukkit.getPlugin());
   }

   private static boolean classExists(String string) {
      try {
         Class.forName(string);
         return true;
      } catch (ClassNotFoundException classNotFoundException) {
         return false;
      }
   }


   public static class Sample {
      private final BigDecimal bigDecimal;
      private final int playerCount;

      public Sample(BigDecimal bigDecimal2, int value) {
         this.bigDecimal = bigDecimal2;
         this.playerCount = value;
      }

      public BigDecimal getValue() {
         return this.bigDecimal;
      }

      public int getPlayerCount() {
         return this.playerCount;
      }
   }
}
