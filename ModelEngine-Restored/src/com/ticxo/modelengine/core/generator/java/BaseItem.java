package com.ticxo.modelengine.core.generator.java;

import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.core.generator.ModelIdCache;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class BaseItem {
   @Getter
   private final List<BaseItem.JavaOverride> overrides = new ArrayList<>();
   @Getter @Setter
   private transient String name;
   @Getter @Setter
   private transient BaseItemEnum baseItemEnum;
   private String parent;
   private Map<String, String> textures;

   public void clearOverrides() {
      this.overrides.clear();
   }

   public void addModels(String namespace, ModelIdCache cache, Map<String, String> aliases) {
      cache.sortedIterate((model, id) -> this.overrides.add(new BaseItem.JavaOverride(namespace + ":" + aliases.getOrDefault(model, model), id)));
   }

   @Getter
   static class JavaOverride {
      private final BaseItem.JavaPredicate predicate;
      private final String model;

      public JavaOverride(String model, int id) {
         this.model = model;
         this.predicate = new BaseItem.JavaPredicate(id);
      }
   }

   record JavaPredicate(int custom_model_data) {
   }
}
