package com.ticxo.modelengine.api.model.render;

public interface ModelRendererParser<T extends ModelRenderer> {
   void sendToClients(T renderer);

   void destroy(T renderer);
}
