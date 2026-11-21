package com.ticxo.modelengine.api.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;

public interface BehaviorRenderer extends DataIO {
   ModelRenderer getModelRenderer();

   void setModelRenderer(ModelRenderer modelRenderer);

   ActiveModel getActiveModel();

   void initialize();

   void readBoneData();

   void sendToClient(RenderParsers parsers);

   void destroy(RenderParsers parsers);

   @Override
   default void load(SavedData data) {
   }

   @Override
   default void save(SavedData data) {
   }
}
