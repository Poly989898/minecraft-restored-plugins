package com.ticxo.modelengine.api.generator.assets;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.BaseItemEnum;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class ItemModelData {
   private final ItemModelData.MultiModels multiModels = new ItemModelData.MultiModels();
   private ItemModelData.SingleComposite singleComposite;
   private String variant;
   private boolean isTranslucent;

   public static ItemModelData.Context.ContextBuilder context() {
      return ItemModelData.Context.builder();
   }

   public Collection<ItemStack> createItemStack() {
      return this.createItemStack(ItemModelData.Context.builder().color(Color.WHITE).build());
   }

   public Set<ItemStack> createItemStack(@Nullable ItemModelData.Context context) {
      return ModelEngineAPI.getNMSHandler().createStack(this, context);
   }

   public record Context(Color color) {
      @Generated
      public static ItemModelData.Context.ContextBuilder builder() {
         return new ItemModelData.Context.ContextBuilder();
      }

      @ToString
      @Generated
      public static class ContextBuilder {
         @Generated
         private Color color;

         @Generated
         ContextBuilder() {
         }

         @Generated
         public ItemModelData.Context.ContextBuilder color(Color color) {
            this.color = color;
            return this;
         }

         @Generated
         public ItemModelData.Context build() {
            return new ItemModelData.Context(this.color);
         }
      }
   }

   public static class MultiModels {
      private final Map<String, ItemModelData.SubModel> map = new Object2ObjectOpenHashMap();

      public void addSubModel(ItemModelData.SubModel model) {
         this.map.put(model.id, model);
      }

      public ItemModelData.SubModel getSubModel(String id) {
         return this.map.get(id);
      }

      public Collection<ItemModelData.SubModel> getSubModels() {
         return this.map.values();
      }

      public Set<String> getKeys() {
         return this.map.keySet();
      }
   }

   public record SingleComposite(NamespacedKey model) {
   }

   @Getter
   @Setter
   public static class SubModel {
      private final String id;
      private BaseItemEnum item;
      private int data;

      @Generated
      public SubModel(String id) {
         this.id = id;
      }
   }
}
