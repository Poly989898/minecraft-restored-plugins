package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.nms.NMSHandler;
import lombok.Generated;

public abstract class AbstractBehaviorRenderer implements BehaviorRenderer {
   protected final ActiveModel activeModel;
   protected final NMSHandler nmsHandler = ModelEngineAPI.getNMSHandler();
   protected ModelRenderer modelRenderer;

   public AbstractBehaviorRenderer(ActiveModel activeModel) {
      this.activeModel = activeModel;
   }

   @Generated
   @Override
   public ActiveModel getActiveModel() {
      return this.activeModel;
   }

   @Generated
   @Override
   public ModelRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   @Generated
   @Override
   public void setModelRenderer(ModelRenderer modelRenderer) {
      this.modelRenderer = modelRenderer;
   }
}
