package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractItemFoodComponent implements AbstractItemComponent {
   public static final String SECTION_KEY = "Food";
   public static final String KEY_NUTRITION = "Food.Nutrition";
   public static final String KEY_SATURATION = "Food.Saturation";
   public static final String KEY_EAT_SECONDS = "Food.EatSeconds";
   public static final String KEY_CAN_ALWAYS_EAT = "Food.CanAlwaysEat";
   private Boolean canAlwaysEat = null;
   private Integer nutrition = null;
   private Float saturation = null;
   private Float eatSeconds = null;

   public AbstractItemFoodComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("Food.Nutrition")) {
         this.nutrition = config.getInt("Food.Nutrition", 0);
      }

      if (config.isSet("Food.Saturation")) {
         this.saturation = config.getFloat("Food.Saturation", 0.0F);
      }

      if (config.isSet("Food.EatSeconds")) {
         this.eatSeconds = config.getFloat("Food.EatSeconds", 1.6F);
      }

      if (config.isSet("Food.CanAlwaysEat")) {
         this.canAlwaysEat = config.getBoolean("Food.CanAlwaysEat", false);
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyFoodComponent(this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemFoodComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$canAlwaysEat = this.getCanAlwaysEat();
         Object other$canAlwaysEat = other.getCanAlwaysEat();
         if (this$canAlwaysEat == null ? other$canAlwaysEat == null : this$canAlwaysEat.equals(other$canAlwaysEat)) {
            Object this$nutrition = this.getNutrition();
            Object other$nutrition = other.getNutrition();
            if (this$nutrition == null ? other$nutrition == null : this$nutrition.equals(other$nutrition)) {
               Object this$saturation = this.getSaturation();
               Object other$saturation = other.getSaturation();
               if (this$saturation == null ? other$saturation == null : this$saturation.equals(other$saturation)) {
                  Object this$eatSeconds = this.getEatSeconds();
                  Object other$eatSeconds = other.getEatSeconds();
                  return this$eatSeconds == null ? other$eatSeconds == null : this$eatSeconds.equals(other$eatSeconds);
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
      return other instanceof AbstractItemFoodComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $canAlwaysEat = this.getCanAlwaysEat();
      result = result * 59 + ($canAlwaysEat == null ? 43 : $canAlwaysEat.hashCode());
      Object $nutrition = this.getNutrition();
      result = result * 59 + ($nutrition == null ? 43 : $nutrition.hashCode());
      Object $saturation = this.getSaturation();
      result = result * 59 + ($saturation == null ? 43 : $saturation.hashCode());
      Object $eatSeconds = this.getEatSeconds();
      return result * 59 + ($eatSeconds == null ? 43 : $eatSeconds.hashCode());
   }
}
