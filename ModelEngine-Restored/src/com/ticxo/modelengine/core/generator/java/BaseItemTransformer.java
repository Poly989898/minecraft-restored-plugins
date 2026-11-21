package com.ticxo.modelengine.core.generator.java;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ticxo.modelengine.api.generator.assets.ItemModel;
import com.ticxo.modelengine.api.generator.assets.TintSource;

public class BaseItemTransformer {
   public static ItemModel to1214Format(BaseItem item) {
      ItemModel.RangeDispatch model = new ItemModel.RangeDispatch();

      TintSource tint = switch (item.getBaseItemEnum()) {
         case FILLED_MAP -> new TintSource.MapColor(-1);
         case LEATHER_BOOTS, LEATHER_CHESTPLATE, LEATHER_HORSE_ARMOR, LEATHER_LEGGINGS -> new TintSource.Dye(-1);
         case LINGERING_POTION, POTION, SPLASH_POTION, TIPPED_ARROW -> new TintSource.Potion(-1);
      };

      for (BaseItem.JavaOverride overrides : item.getOverrides()) {
         int id = overrides.getPredicate().custom_model_data();
         ItemModel.Model idModel = new ItemModel.Model(overrides.getModel());
         idModel.getTints().add(tint);
         model.getEntries().add(new ItemModel.RangeDispatch.Entry(idModel, id));
      }

      ItemModel.Model fallback = new ItemModel.Model("minecraft:item/" + item.getName());
      switch (item.getBaseItemEnum()) {
         case FILLED_MAP:
            fallback.getTints().add(new TintSource.Constant(-1));
            fallback.getTints().add(new TintSource.MapColor());
            break;
         case LEATHER_BOOTS:
         case LEATHER_CHESTPLATE:
         case LEATHER_HORSE_ARMOR:
         case LEATHER_LEGGINGS:
            fallback.getTints().add(new TintSource.Dye());
            break;
         case LINGERING_POTION:
         case POTION:
         case SPLASH_POTION:
         case TIPPED_ARROW:
            fallback.getTints().add(new TintSource.Potion());
      }

      model.setFallback(fallback);
      return model;
   }

   public static JsonElement wrapModel(Gson gson, ItemModel model, boolean oversized) {
      JsonObject obj = new JsonObject();
      obj.add("model", gson.toJsonTree(model));
      if (oversized) {
         obj.add("oversized_in_gui", new JsonPrimitive(true));
      }

      return obj;
   }
}
