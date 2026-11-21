package com.ticxo.modelengine.api.generator.assets;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;

@AllArgsConstructor
public abstract class ItemModel {
   protected final String type;

   public static ItemModel.Composite composite(ItemModel... models) {
      ItemModel.Composite composite = new ItemModel.Composite();
      composite.models.addAll(List.of(models));
      return composite;
   }

   @Getter
   public static class Composite extends ItemModel {
      protected final List<ItemModel> models = new ArrayList<>();

      public Composite() {
         super("minecraft:composite");
      }
   }

   @Getter
   public static class Model extends ItemModel {
      protected final String model;
      protected final List<TintSource> tints = new ArrayList<>();

      public Model(NamespacedKey model) {
         super("minecraft:model");
         this.model = model.asString();
         this.tints.add(new TintSource.CustomModelData(0));
      }

      public Model(String model) {
         super("minecraft:model");
         this.model = model;
      }
   }

   @Getter
   @Setter
   public static class RangeDispatch extends ItemModel {
      protected final List<ItemModel.RangeDispatch.Entry> entries = new ArrayList<>();
      protected ItemModel fallback;
      protected final String property = "minecraft:custom_model_data";

      public RangeDispatch() {
         super("minecraft:range_dispatch");
      }

      public record Entry(ItemModel model, int threshold) {
      }
   }

   @Getter
   @Setter
   public static class Select extends ItemModel {
      protected final List<ItemModel.Select.Case> cases = new ArrayList<>();
      protected ItemModel fallback;
      protected final String property = "minecraft:custom_model_data";

      public Select() {
         super("minecraft:select");
      }

      public record Case(ItemModel model, String when) {
      }
   }
}
