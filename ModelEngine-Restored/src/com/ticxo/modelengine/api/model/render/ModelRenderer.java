package com.ticxo.modelengine.api.model.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.nms.RenderParsers;

public interface ModelRenderer {
   ActiveModel getActiveModel();

   void updatePivotOverride(PivotOverride override);

   boolean isInitialized();

   void initialize();

   void readModelData();

   void sendToClient(RenderParsers parsers);

   void destroy(RenderParsers parsers);

   void createRealEntities();

   boolean pollFirstSpawn();
}
