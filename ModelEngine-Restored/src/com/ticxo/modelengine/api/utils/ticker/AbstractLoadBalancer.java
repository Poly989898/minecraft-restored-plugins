package com.ticxo.modelengine.api.utils.ticker;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import lombok.Getter;

public abstract class AbstractLoadBalancer<KEY, SER extends LoadBalancer.Server> implements LoadBalancer<KEY, SER> {
   @Getter
   protected final Set<SER> available = ConcurrentHashMap.newKeySet();
   protected final Map<KEY, SER> reference = new ConcurrentHashMap<>();

   public AbstractLoadBalancer(int counts) {
      if (counts <= 0) {
         throw new RuntimeException("Cannot create load balancer with less than 1 server: " + counts);
      }

      for (int i = 0; i < counts; i++) {
         this.available.add(this.supply());
      }
   }

   @Override
   public SER getOrRegister(KEY key) {
      SER current = this.reference.get(key);
      if (current != null) {
         return current;
      }

      int load = Integer.MAX_VALUE;
      SER leastLoad = null;

      for (SER server : this.available) {
         if (server.getLoad() < load) {
            leastLoad = server;
            load = server.getLoad();
         }
      }

      if (leastLoad == null) {
         throw new RuntimeException("No server found.");
      }

      this.reference.put(key, leastLoad);
      return leastLoad;
   }

   @Override
   public SER get(KEY key) {
      return this.reference.get(key);
   }

   @Override
   public void unregister(KEY key) {
      this.reference.remove(key);
   }

   @Override
   public void execute(KEY key, BiConsumer<KEY, SER> balConsumer) {
      balConsumer.accept(key, this.getOrRegister(key));
   }
}
