package com.ticxo.modelengine.api.model.bone.render.renderer;

import java.util.Map;

public interface RenderQueues<T> {
   Map<String, T> getSpawnQueue();

   Map<String, T> getRendered();

   Map<String, T> getDestroyQueue();

   default T getQueued(String id) {
      Map<String, T> sq = this.getSpawnQueue();
      if (!sq.isEmpty()) {
         T queued = sq.get(id);
         if (queued != null) {
            return queued;
         }
      }

      return this.getRendered().get(id);
   }
}
