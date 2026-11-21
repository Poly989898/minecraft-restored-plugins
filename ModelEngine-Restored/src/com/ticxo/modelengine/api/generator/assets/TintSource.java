package com.ticxo.modelengine.api.generator.assets;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class TintSource {
   protected final String type;

   public static class Constant extends TintSource {
      protected final int value;

      public Constant(int value) {
         super("minecraft:constant");
         this.value = value;
      }
   }

   public static class CustomModelData extends TintSource {
      protected final int index;
      @SerializedName("default")
      protected final int def = 16777215;

      public CustomModelData(int index) {
         super("minecraft:custom_model_data");
         this.index = index;
      }
   }

   public static class Dye extends TintSource {
      @SerializedName("default")
      protected final int def;

      public Dye() {
         this(-6265536);
      }

      public Dye(int def) {
         super("minecraft:dye");
         this.def = def;
      }
   }

   public static class MapColor extends TintSource {
      @SerializedName("default")
      protected final int def;

      public MapColor() {
         this(4603950);
      }

      public MapColor(int def) {
         super("minecraft:map_color");
         this.def = def;
      }
   }

   public static class Potion extends TintSource {
      @SerializedName("default")
      protected final int def;

      public Potion() {
         this(-13083194);
      }

      public Potion(int def) {
         super("minecraft:potion");
         this.def = def;
      }
   }
}
