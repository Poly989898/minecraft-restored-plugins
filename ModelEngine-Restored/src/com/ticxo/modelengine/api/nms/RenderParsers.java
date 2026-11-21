package com.ticxo.modelengine.api.nms;

import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.model.render.ModelRendererParser;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RenderParsers {
   private final Map<Predicate<?>, Supplier<ModelRendererParser<?>>> modelParsers = new HashMap<>();
   private final Map<Predicate<?>, Supplier<BehaviorRendererParser<?>>> behaviorParsers = new HashMap<>();

   public void registerModelParser(Predicate<ModelRenderer> predicate, Supplier<ModelRendererParser<?>> parser) {
      this.modelParsers.put(predicate, parser);
   }

   public void registerBehaviorParser(Predicate<BehaviorRenderer> predicate, Supplier<BehaviorRendererParser<?>> parser) {
      this.behaviorParsers.put(predicate, parser);
   }

   public <T extends ModelRenderer> ModelRendererParser<T> getModelParser(T renderer) {
      for (Entry<Predicate<?>, Supplier<ModelRendererParser<?>>> entry : this.modelParsers.entrySet()) {
         if (((Predicate<T>)entry.getKey()).test(renderer)) {
            return (ModelRendererParser<T>)entry.getValue().get();
         }
      }

      return null;
   }

   public <T extends BehaviorRenderer> BehaviorRendererParser<T> getBehaviorParser(T renderer) {
      for (Entry<Predicate<?>, Supplier<BehaviorRendererParser<?>>> entry : this.behaviorParsers.entrySet()) {
         if (((Predicate<T>)entry.getKey()).test(renderer)) {
            return (BehaviorRendererParser<T>)entry.getValue().get();
         }
      }

      return null;
   }
}
