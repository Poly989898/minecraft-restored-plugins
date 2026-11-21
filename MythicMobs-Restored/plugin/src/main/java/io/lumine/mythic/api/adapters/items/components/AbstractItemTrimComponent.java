package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.items.MythicItem;
import lombok.Generated;
import lombok.Getter;

@Getter
public class AbstractItemTrimComponent implements AbstractItemComponent {
   public static final String SECTION_KEY = "Trim";
   public static final String SECTION_OPTIONS_KEY = "Options.Trim";
   public static final String KEY_MATERIAL = "Trim.Material";
   public static final String KEY_PATTERN = "Trim.Pattern";
   public static final String KEY_MATERIAL_OPTIONS = "Options.Trim.Material";
   public static final String KEY_PATTERN_OPTIONS = "Options.Trim.Pattern";
   public static final String KEY_TRIM = "Trim";
   public static final String KEY_TRIM_OPTIONS = "Options.Trim";
   private PlaceholderString trimMaterial;
   private PlaceholderString trimPattern;

   public AbstractItemTrimComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("Trim.Material")) {
         this.trimMaterial = config.getPlaceholderString("Trim.Material", "IRON");
      } else if (config.isSet("Options.Trim.Material")) {
         this.trimMaterial = config.getPlaceholderString("Options.Trim.Material", "IRON");
      } else {
         this.trimMaterial = PlaceholderString.of("IRON");
      }

      if (config.isSet("Trim.Pattern")) {
         this.trimPattern = config.getPlaceholderString("Trim.Pattern", "WILD");
      } else if (config.isSet("Options.Trim.Pattern")) {
         this.trimPattern = config.getPlaceholderString("Options.Trim.Pattern", "WILD");
      } else {
         this.trimPattern = PlaceholderString.of("WILD");
      }
   }

   public AbstractItemTrimComponent(MythicItem mythicItem, String trimMaterial, String trimPattern) {
      this.trimMaterial = PlaceholderString.of(trimMaterial);
      this.trimPattern = PlaceholderString.of(trimPattern);
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyTrimComponent(dropMetadata, this);
   }

   @Generated
   public void setTrimMaterial(PlaceholderString trimMaterial) {
      this.trimMaterial = trimMaterial;
   }

   @Generated
   public void setTrimPattern(PlaceholderString trimPattern) {
      this.trimPattern = trimPattern;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemTrimComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$trimMaterial = this.getTrimMaterial();
         Object other$trimMaterial = other.getTrimMaterial();
         if (this$trimMaterial == null ? other$trimMaterial == null : this$trimMaterial.equals(other$trimMaterial)) {
            Object this$trimPattern = this.getTrimPattern();
            Object other$trimPattern = other.getTrimPattern();
            return this$trimPattern == null ? other$trimPattern == null : this$trimPattern.equals(other$trimPattern);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemTrimComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $trimMaterial = this.getTrimMaterial();
      result = result * 59 + ($trimMaterial == null ? 43 : $trimMaterial.hashCode());
      Object $trimPattern = this.getTrimPattern();
      return result * 59 + ($trimPattern == null ? 43 : $trimPattern.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "AbstractItemTrimComponent(trimMaterial=" + this.getTrimMaterial() + ", trimPattern=" + this.getTrimPattern() + ")";
   }
}
