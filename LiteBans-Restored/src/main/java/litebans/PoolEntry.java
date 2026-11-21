package litebans;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

final class PoolEntry implements IConcurrentBagEntry {
   private static final PoolLogger POOL_LOGGER = PoolLogger.computePoolLogger(PoolEntry.class);
   private static final AtomicIntegerFieldUpdater STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(PoolEntry.class, "state");
   Connection connection2;
   long longValue;
   private volatile int state = 0;
   private volatile boolean markedEvicted;
   private volatile ScheduledFuture futureEol;
   private volatile ScheduledFuture endOfLifeFuture;
   private final FastList fastList;
   private final HikariPool hikariPool;
   private final boolean flag3;
   private final boolean flag4;

   PoolEntry(Connection connection, PoolBase poolBase, boolean flag, boolean flag2) {
      this.connection2 = connection;
      this.hikariPool = (HikariPool)poolBase;
      this.flag3 = flag;
      this.flag4 = flag2;
      this.longValue = ClockSource.currentTime();
      this.fastList = new FastList(Statement.class, 16);
   }

   void recycle(long value) {
      if (this.connection2 != null) {
         this.longValue = value;
         this.hikariPool.recycle(this);
      }
   }

   void setFutureEol(ScheduledFuture scheduledFuture) {
      this.futureEol = scheduledFuture;
   }

   public void setScheduledFuture3(ScheduledFuture scheduledFuture) {
      this.endOfLifeFuture = scheduledFuture;
   }

   Connection createProxyConnection(LeakTask leakTask, long value) {
      return ProxyFactory.computeProxyConnection(this, this.connection2, this.fastList, leakTask, value, this.flag3, this.flag4);
   }

   void resetConnectionState(ProxyConnection proxyConnection, int value) {
      this.hikariPool.resetConnectionState(this.connection2, proxyConnection, value);
   }

   String getPoolName() {
      return this.hikariPool.toString();
   }

   boolean isMarkedEvicted() {
      return this.markedEvicted;
   }

   void markEvicted() {
      this.markedEvicted = true;
   }

   void evict(String text) {
      this.hikariPool.closeConnection(this, text);
   }

   PoolBase getPoolBase() {
      return this.hikariPool;
   }

   @Override
   public String toString() {
      long currentTime2 = ClockSource.currentTime();
      return this.connection2 + ", accessed " + ClockSource.elapsedDisplayString(this.longValue, currentTime2) + " ago, " + this.stateToString();
   }

   @Override
   public int getState() {
      return STATE_UPDATER.get(this);
   }

   @Override
   public boolean compareAndSet(int value, int value2) {
      return STATE_UPDATER.compareAndSet(this, value, value2);
   }

   @Override
   public void setState(int value) {
      STATE_UPDATER.set(this, value);
   }

   Connection close() {
      ScheduledFuture scheduledFuture = this.futureEol;
      if (scheduledFuture != null && !scheduledFuture.isDone() && !scheduledFuture.cancel(false)) {
         POOL_LOGGER.handleText4("%s - maxLifeTime expiration task cancellation unexpectedly returned false for connection %s", new Object[]{this.getPoolName(), this.connection2.toString()});
      }

      ScheduledFuture scheduledFuture2 = this.endOfLifeFuture;
      if (scheduledFuture2 != null && !scheduledFuture2.isDone() && !scheduledFuture2.cancel(false)) {
         POOL_LOGGER.handleText4("%s - keepalive task cancellation unexpectedly returned false for connection %s", new Object[]{this.getPoolName(), this.connection2.toString()});
      }

      Connection connection = this.connection2;
      this.connection2 = null;
      this.futureEol = null;
      this.endOfLifeFuture = null;
      return connection;
   }

   private final String stateToString() {
      switch (this.state) {
         case -2:
            return "RESERVED";
         case -1:
            return "REMOVED";
         case 0:
            return "NOT_IN_USE";
         case 1:
            return "IN_USE";
         default:
            return "Invalid";
      }
   }
}
