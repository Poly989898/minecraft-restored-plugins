package com.ticxo.modelengine.api.model.bone.render;

public interface BehaviorRendererParser<T extends BehaviorRenderer> {
   void sendToClients(T renderer);

   void destroy(T renderer);
}
