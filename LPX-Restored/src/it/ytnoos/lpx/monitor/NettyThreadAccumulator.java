package it.ytnoos.lpx.monitor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NettyThreadAccumulator {
   private final ConcurrentLinkedQueue<BigDecimal> samples = new ConcurrentLinkedQueue<>();
   private final Set<UUID> players = ConcurrentHashMap.newKeySet();
   private final RollingAverage averages;
   private final RollingAverage percentiles;
   private final RollingAverage maximums;

   public NettyThreadAccumulator(int value) {
      this.averages = new RollingAverage(value * 10);
      this.percentiles = new RollingAverage(value * 10);
      this.maximums = new RollingAverage(value * 10);
   }

   public void addSample(long longValue) {
      this.samples.add(new BigDecimal(longValue));
   }

   public void addPlayer(Object object) {
      this.players.add((UUID)object);
   }

   public void removePlayer(Object object) {
      this.players.remove(object);
   }

   public int getPlayerCount() {
      return this.players.size();
   }

   public ConcurrentLinkedQueue getSamples() {
      return this.samples;
   }

   public void addAggregates(BigDecimal bigDecimal, Object object, BigDecimal bigDecimal2) {
      this.averages.add(bigDecimal);
      this.percentiles.add(bigDecimal);
      this.maximums.add(bigDecimal2);
   }

   public BigDecimal getLowPercentile() {
      return this.averages.getPercentile(10.0);
   }

   public BigDecimal getHighPercentile() {
      return this.averages.getPercentile(90.0);
   }

   public RollingAverage getAverages() {
      return this.averages;
   }

}
