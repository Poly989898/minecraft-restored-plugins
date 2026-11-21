package io.lumine.mythic.core.skills.variables;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

public class DeferredVariableRegistry extends VariableRegistry {
   private final VariableRegistry live;
   private final DeferredVariableRegistry.WriteLog log;
   private final long dispatchedAt;
   private final Map<String, Variable> snapshot;
   private volatile boolean released = false;

   private DeferredVariableRegistry(VariableRegistry live) {
      this.live = live;
      this.entries = live.entries;
      this.log = live.deferredWrites();
      this.dispatchedAt = this.log.current();
      this.snapshot = live.asMap();
   }

   public static DeferredVariableRegistry of(VariableRegistry live) {
      return new DeferredVariableRegistry(live);
   }

   public void release() {
      this.released = true;
   }

   @Override
   DeferredVariableRegistry.WriteLog deferredWrites() {
      return this.log;
   }

   @Override
   public void put(String key, Variable value) {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(value);
      this.live.put(key, value);
      if (!this.released) {
         this.log.record(key, value);
      }
   }

   @Override
   public void remove(String key) {
      Preconditions.checkNotNull(key);
      this.live.remove(key);
      if (!this.released) {
         this.log.record(key, null);
      }
   }

   @Override
   public boolean has(String key) {
      return this.get(key) != null;
   }

   @Override
   public Variable get(String key) {
      Preconditions.checkNotNull(key);
      if (this.released) {
         return this.live.get(key);
      } else {
         DeferredVariableRegistry.WriteLog.Write written = this.log.since(key, this.dispatchedAt);
         Variable var = written != null ? written.value() : this.snapshot.get(key);
         return var != null && !var.shouldExpire() ? var : null;
      }
   }

   @Override
   public Variable getOrDefault(String key, Variable def) {
      Variable var = this.get(key);
      return var == null ? def : var;
   }

   @Override
   public ImmutableMap<String, Variable> asMap() {
      if (this.released) {
         return this.live.asMap();
      } else {
         HashMap<String, Variable> merged = new HashMap<>(this.snapshot);
         this.log.forEachSince(this.dispatchedAt, (key, value) -> {
            if (value == null) {
               merged.remove(key);
            } else {
               merged.put(key, value);
            }
         });
         return ImmutableMap.copyOf(merged);
      }
   }

   @Override
   public VariableRegistry clone() throws CloneNotSupportedException {
      VariableRegistry clone = new VariableRegistry();
      UnmodifiableIterator unmodifiableIterator = this.asMap().entrySet().iterator();

      while (unmodifiableIterator.hasNext()) {
         Entry<String, Variable> entry = (Entry<String, Variable>)unmodifiableIterator.next();
         clone.entries.put(entry.getKey(), entry.getValue().clone());
      }

      return clone;
   }

   static final class WriteLog {
      private final AtomicLong sequence = new AtomicLong();
      private final ConcurrentHashMap<String, DeferredVariableRegistry.WriteLog.Write> latest = new ConcurrentHashMap<>();

      long current() {
         return this.sequence.get();
      }

      void record(String key, Variable value) {
         DeferredVariableRegistry.WriteLog.Write write = new DeferredVariableRegistry.WriteLog.Write(this.sequence.incrementAndGet(), value);
         this.latest.merge(key, write, (old, now) -> (now.sequence() > old.sequence() ? now : old));
      }

      DeferredVariableRegistry.WriteLog.Write since(String key, long dispatchedAt) {
         DeferredVariableRegistry.WriteLog.Write write = this.latest.get(key);
         return write != null && write.sequence() > dispatchedAt ? write : null;
      }

      void forEachSince(long dispatchedAt, BiConsumer<String, Variable> action) {
         this.latest.forEach((key, write) -> {
            if (write.sequence() > dispatchedAt) {
               action.accept(key, write.value());
            }
         });
      }

      record Write(long sequence, Variable value) {
      }
   }
}
