package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AbstractItemWorkstationComponent implements AbstractItemComponent {
   private final AbstractItemWorkstationComponent.Kind kind;
   private final String value;
   private final String speedMultiplier;

   public AbstractItemWorkstationComponent(MythicConfig config, AbstractItemWorkstationComponent.Kind kind) {
      this.kind = kind;
      this.value = config.getString(kind.getValueKey(), null);
      this.speedMultiplier = config.getString("SpeedMultiplier", null);
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyWorkstationComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemWorkstationComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$kind = this.getKind();
         Object other$kind = other.getKind();
         if (this$kind == null ? other$kind == null : this$kind.equals(other$kind)) {
            Object this$value = this.getValue();
            Object other$value = other.getValue();
            if (this$value == null ? other$value == null : this$value.equals(other$value)) {
               Object this$speedMultiplier = this.getSpeedMultiplier();
               Object other$speedMultiplier = other.getSpeedMultiplier();
               return this$speedMultiplier == null ? other$speedMultiplier == null : this$speedMultiplier.equals(other$speedMultiplier);
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
      return other instanceof AbstractItemWorkstationComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $kind = this.getKind();
      result = result * 59 + ($kind == null ? 43 : $kind.hashCode());
      Object $value = this.getValue();
      result = result * 59 + ($value == null ? 43 : $value.hashCode());
      Object $speedMultiplier = this.getSpeedMultiplier();
      return result * 59 + ($speedMultiplier == null ? 43 : $speedMultiplier.hashCode());
   }

   @Getter
   @AllArgsConstructor(access = AccessLevel.PRIVATE)
   public static enum Kind {
      COMPOSTABLE("Compostable", "Layers"),
      COOKING_FUEL("CookingFuel", "BurnTime"),
      BREWING_FUEL("BrewingFuel", "Uses");

      private final String section;
      private final String valueKey;
   }
}
