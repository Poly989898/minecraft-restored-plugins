package com.ticxo.modelengine.api.vfx.render;

public interface VFXRendererParser<T extends VFXRenderer> {
   void sendToClients(T renderer);

   void destroy(T renderer);
}
