package io.lumine.mythic.core.skills.variables;

import com.google.common.base.Preconditions;
import io.lumine.mythic.bukkit.events.MythicPlayerVariableSetEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.players.PlayerData;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.Setter;

@Setter
public class PlayerVariableRegistry extends VariableRegistry {
   @Getter
   private transient PlayerData playerData;
   private final transient ReentrantLock writeLock = new ReentrantLock();

   public PlayerVariableRegistry() {
   }

   public PlayerVariableRegistry(Map<String, Variable> value) {
      super(value);
   }

   @Override
   public void put(String key, Variable value) {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(value);
      this.writeLock.lock();

      try {
         MythicPlayerVariableSetEvent event = new MythicPlayerVariableSetEvent(this.playerData, key, value);
         Events.call(event);
         if (!event.isCancelled()) {
            this.entries.put(key, value);
         }
      } finally {
         this.writeLock.unlock();
      }
   }

   @Override
   public void putAll(Map<String, ? extends Variable> value) {
      Preconditions.checkNotNull(value);
      this.writeLock.lock();

      try {
         for (Entry<String, ? extends Variable> entry : value.entrySet()) {
            MythicPlayerVariableSetEvent event = new MythicPlayerVariableSetEvent(this.playerData, entry.getKey(), entry.getValue());
            Events.call(event);
            if (!event.isCancelled()) {
               this.entries.put(entry.getKey(), entry.getValue());
            }
         }
      } finally {
         this.writeLock.unlock();
      }
   }

   @Override
   public void remove(String key) {
      Preconditions.checkNotNull(key);
      this.writeLock.lock();

      try {
         this.entries.remove(key);
      } finally {
         this.writeLock.unlock();
      }
   }

   @Override
   public void clean() {
      this.writeLock.lock();

      try {
         this.entries.values().removeIf(o -> o.shouldExpire());
      } finally {
         this.writeLock.unlock();
      }
   }
}
