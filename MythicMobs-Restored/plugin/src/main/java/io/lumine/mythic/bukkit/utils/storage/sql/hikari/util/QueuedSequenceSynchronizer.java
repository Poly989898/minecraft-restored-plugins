package io.lumine.mythic.bukkit.utils.storage.sql.hikari.util;

import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;

public final class QueuedSequenceSynchronizer {
   private final Sequence sequence;
   private final QueuedSequenceSynchronizer.Synchronizer synchronizer = new QueuedSequenceSynchronizer.Synchronizer();

   public QueuedSequenceSynchronizer() {
      this.sequence = Sequence.Factory.create();
   }

   public void signal() {
      this.synchronizer.releaseShared(1L);
   }

   public long currentSequence() {
      return this.sequence.get();
   }

   public boolean waitUntilSequenceExceeded(long sequence, long nanosTimeout) throws InterruptedException {
      return this.synchronizer.tryAcquireSharedNanos(sequence, nanosTimeout);
   }

   public boolean hasQueuedThreads() {
      return this.synchronizer.hasQueuedThreads();
   }

   public int getQueueLength() {
      return this.synchronizer.getQueueLength();
   }

   private final class Synchronizer extends AbstractQueuedLongSynchronizer {
      private static final long serialVersionUID = 104753538004341218L;

      private Synchronizer() {
      }

      @Override
      protected long tryAcquireShared(long seq) {
         return QueuedSequenceSynchronizer.this.sequence.get() - (seq + 1L);
      }

      @Override
      protected boolean tryReleaseShared(long unused) {
         QueuedSequenceSynchronizer.this.sequence.increment();
         return true;
      }
   }
}
