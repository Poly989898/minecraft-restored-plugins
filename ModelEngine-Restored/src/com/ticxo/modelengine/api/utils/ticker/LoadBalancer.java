package com.ticxo.modelengine.api.utils.ticker;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface LoadBalancer<KEY, SER extends LoadBalancer.Server> {
   SER getOrRegister(KEY key);

   SER get(KEY key);

   void unregister(KEY key);

   void execute(KEY key, BiConsumer<KEY, SER> balConsumer);

   SER supply();

   default <VAL> VAL map(KEY key, Function<SER, VAL> mapper) {
      return mapper.apply(this.getOrRegister(key));
   }

   interface Server {
      int getLoad();
   }
}
