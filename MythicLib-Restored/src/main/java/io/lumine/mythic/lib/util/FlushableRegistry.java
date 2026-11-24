package io.lumine.mythic.lib.util;

import io.lumine.mythic.lib.MythicLib;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class FlushableRegistry<K, V> implements Closeable, Listener {
   private final Map<K, V> registry = new HashMap<>();
   private final BiPredicate<K, V> condition;
   private final long period;
   private final BukkitRunnable flushRunnable = new BukkitRunnable() {
      public void run() {
         Iterator<Entry<K, V>> iterator = FlushableRegistry.this.registry.entrySet().iterator();

         while (iterator.hasNext()) {
            Entry<K, V> next = iterator.next();
            if (FlushableRegistry.this.condition.test(next.getKey(), next.getValue())) {
               iterator.remove();
            }
         }
      }
   };
   private boolean open = true;

   public FlushableRegistry(BiPredicate<K, V> condition, long period) {
      this.condition = condition;
      this.period = period;
      this.flushRunnable.runTaskTimer(MythicLib.plugin, period, period);
      Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
   }

   @NotNull
   public Map<K, V> getRegistry() {
      return this.registry;
   }

   public boolean isOpen() {
      return this.open;
   }

   public long getPeriod() {
      return this.period;
   }

   @Override
   public void close() {
      Validate.isTrue(this.open, "Registry already closed");
      this.open = false;
      this.flushRunnable.cancel();
      this.registry.clear();
      HandlerList.unregisterAll(this);
   }
}
