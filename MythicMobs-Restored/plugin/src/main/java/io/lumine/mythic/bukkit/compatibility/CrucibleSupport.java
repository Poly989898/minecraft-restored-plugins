package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.utils.lib.lang3.StringEscapeUtils;
import io.lumine.mythiccrucible.items.CrucibleItem;
import io.lumine.mythiccrucible.items.blocks.CustomBlockItemContext;
import java.util.Optional;
import lombok.Generated;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public interface CrucibleSupport {
   Optional<CrucibleItem> getCrucibleItem(String str);

   Optional<CustomBlockItemContext> getCrucibleBlockData(String str);

   AbstractBlock getCustomBlock(String str);

   boolean isFurniture(AbstractLocation abstractLocation);

   boolean isFurniture(AbstractEntity abstractEntity);

   Optional<SkillCaster> getFurnitureCaster(AbstractEntity abstractEntity);

   default CrucibleSupport.ItemGenerationFeedback addItemToGeneration(Pack pack, String generationTarget) {
      return new CrucibleSupport.ItemGenerationFeedback();
   }

   default CrucibleSupport.ItemGenerationFeedback addItemToGeneration(Pack pack, MythicConfig config) {
      return new CrucibleSupport.ItemGenerationFeedback();
   }

   default CrucibleSupport.ItemGenerationFeedback addItemToGeneration(Pack pack, MythicLineConfig config) {
      return new CrucibleSupport.ItemGenerationFeedback();
   }

   default CrucibleSupport.ItemGenerationFeedback addItemToGeneration(Pack pack, Material material, int customModelData, String generationTarget) {
      return new CrucibleSupport.ItemGenerationFeedback();
   }

   default CrucibleSupport.ItemGenerationFeedback addItemToGeneration(Pack pack, Material material, int customModelData, MythicConfig config) {
      return new CrucibleSupport.ItemGenerationFeedback();
   }

   default CrucibleSupport.ItemGenerationFeedback addItemToGeneration(Pack pack, Material material, int customModelData, MythicLineConfig config) {
      return new CrucibleSupport.ItemGenerationFeedback();
   }

   default Optional<Character> addFontImageToGeneration(Pack pack, CrucibleSupport.FontGenerationDef fontDefinition) {
      return null;
   }

   default String getPackNamespace() {
      return "mythic";
   }

   default boolean isImplicitlyGenerating() {
      return false;
   }

   default boolean isGeneratedSound(String key) {
      return false;
   }

   default boolean isGeneratedTooltip(String key) {
      return false;
   }

   public static class FontGenerationDef {
      private String key = null;
      private String type = "bitmap";
      private String path = null;
      private String character = null;
      private Integer ascent = null;
      private Integer height = null;

      public FontGenerationDef(MythicLineConfig config) {
         this.key = config.getKey();
         this.type = config.getString(new String[]{"type", "t"}, "bitmap");
         this.path = config.getString(new String[]{"path", "p"}, null);
         this.character = config.getString(new String[]{"char", "c"}, null);
         if (this.character != null) {
            this.character = StringEscapeUtils.unescapeJava(this.character);
         }

         if (config.getString(new String[]{"ascent", "a"}, null) != null) {
            this.ascent = config.getInteger(new String[]{"ascent", "a"}, -1);
         }

         if (config.getString(new String[]{"height", "h"}, null) != null) {
            this.height = config.getInteger(new String[]{"height", "h"}, -1);
         }
      }

      @Generated
      public static CrucibleSupport.FontGenerationDef.FontGenerationDefBuilder builder() {
         return new CrucibleSupport.FontGenerationDef.FontGenerationDefBuilder();
      }

      @Generated
      public String getKey() {
         return this.key;
      }

      @Generated
      public String getType() {
         return this.type;
      }

      @Generated
      public String getPath() {
         return this.path;
      }

      @Generated
      public String getCharacter() {
         return this.character;
      }

      @Generated
      public Integer getAscent() {
         return this.ascent;
      }

      @Generated
      public Integer getHeight() {
         return this.height;
      }

      @Generated
      public void setKey(String key) {
         this.key = key;
      }

      @Generated
      public void setType(String type) {
         this.type = type;
      }

      @Generated
      public void setPath(String path) {
         this.path = path;
      }

      @Generated
      public void setCharacter(String character) {
         this.character = character;
      }

      @Generated
      public void setAscent(Integer ascent) {
         this.ascent = ascent;
      }

      @Generated
      public void setHeight(Integer height) {
         this.height = height;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof CrucibleSupport.FontGenerationDef other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$ascent = this.getAscent();
            Object other$ascent = other.getAscent();
            if (this$ascent == null ? other$ascent == null : this$ascent.equals(other$ascent)) {
               Object this$height = this.getHeight();
               Object other$height = other.getHeight();
               if (this$height == null ? other$height == null : this$height.equals(other$height)) {
                  Object this$key = this.getKey();
                  Object other$key = other.getKey();
                  if (this$key == null ? other$key == null : this$key.equals(other$key)) {
                     Object this$type = this.getType();
                     Object other$type = other.getType();
                     if (this$type == null ? other$type == null : this$type.equals(other$type)) {
                        Object this$path = this.getPath();
                        Object other$path = other.getPath();
                        if (this$path == null ? other$path == null : this$path.equals(other$path)) {
                           Object this$character = this.getCharacter();
                           Object other$character = other.getCharacter();
                           return this$character == null ? other$character == null : this$character.equals(other$character);
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof CrucibleSupport.FontGenerationDef;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $ascent = this.getAscent();
         result = result * 59 + ($ascent == null ? 43 : $ascent.hashCode());
         Object $height = this.getHeight();
         result = result * 59 + ($height == null ? 43 : $height.hashCode());
         Object $key = this.getKey();
         result = result * 59 + ($key == null ? 43 : $key.hashCode());
         Object $type = this.getType();
         result = result * 59 + ($type == null ? 43 : $type.hashCode());
         Object $path = this.getPath();
         result = result * 59 + ($path == null ? 43 : $path.hashCode());
         Object $character = this.getCharacter();
         return result * 59 + ($character == null ? 43 : $character.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "CrucibleSupport.FontGenerationDef(key="
            + this.getKey()
            + ", type="
            + this.getType()
            + ", path="
            + this.getPath()
            + ", character="
            + this.getCharacter()
            + ", ascent="
            + this.getAscent()
            + ", height="
            + this.getHeight()
            + ")";
      }

      @Generated
      public FontGenerationDef(String key, String type, String path, String character, Integer ascent, Integer height) {
         this.key = key;
         this.type = type;
         this.path = path;
         this.character = character;
         this.ascent = ascent;
         this.height = height;
      }

      @Generated
      public static class FontGenerationDefBuilder {
         @Generated
         private String key;
         @Generated
         private String type;
         @Generated
         private String path;
         @Generated
         private String character;
         @Generated
         private Integer ascent;
         @Generated
         private Integer height;

         @Generated
         FontGenerationDefBuilder() {
         }

         @Generated
         public CrucibleSupport.FontGenerationDef.FontGenerationDefBuilder key(String key) {
            this.key = key;
            return this;
         }

         @Generated
         public CrucibleSupport.FontGenerationDef.FontGenerationDefBuilder type(String type) {
            this.type = type;
            return this;
         }

         @Generated
         public CrucibleSupport.FontGenerationDef.FontGenerationDefBuilder path(String path) {
            this.path = path;
            return this;
         }

         @Generated
         public CrucibleSupport.FontGenerationDef.FontGenerationDefBuilder character(String character) {
            this.character = character;
            return this;
         }

         @Generated
         public CrucibleSupport.FontGenerationDef.FontGenerationDefBuilder ascent(Integer ascent) {
            this.ascent = ascent;
            return this;
         }

         @Generated
         public CrucibleSupport.FontGenerationDef.FontGenerationDefBuilder height(Integer height) {
            this.height = height;
            return this;
         }

         @Generated
         public CrucibleSupport.FontGenerationDef build() {
            return new CrucibleSupport.FontGenerationDef(this.key, this.type, this.path, this.character, this.ascent, this.height);
         }

         @Generated
         @Override
         public String toString() {
            return "CrucibleSupport.FontGenerationDef.FontGenerationDefBuilder(key="
               + this.key
               + ", type="
               + this.type
               + ", path="
               + this.path
               + ", character="
               + this.character
               + ", ascent="
               + this.ascent
               + ", height="
               + this.height
               + ")";
         }
      }
   }

   public static class ItemGenerationFeedback {
      private final Optional<NamespacedKey> itemModel;

      public ItemGenerationFeedback() {
         this.itemModel = Optional.empty();
      }

      public ItemGenerationFeedback(NamespacedKey itemModel) {
         this.itemModel = Optional.ofNullable(itemModel);
      }

      @Generated
      public Optional<NamespacedKey> getItemModel() {
         return this.itemModel;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof CrucibleSupport.ItemGenerationFeedback other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$itemModel = this.getItemModel();
            Object other$itemModel = other.getItemModel();
            return this$itemModel == null ? other$itemModel == null : this$itemModel.equals(other$itemModel);
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof CrucibleSupport.ItemGenerationFeedback;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $itemModel = this.getItemModel();
         return result * 59 + ($itemModel == null ? 43 : $itemModel.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "CrucibleSupport.ItemGenerationFeedback(itemModel=" + this.getItemModel() + ")";
      }
   }
}
