package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import lombok.Generated;
import lombok.ToString;

@ToString
public class AbstractItemGliderComponent implements AbstractItemComponent {
   public static final String KEY_GLIDER = "Glider";
   public static final String KEY_GLIDER_OPTIONS = "Options.Glider";

   public AbstractItemGliderComponent(MythicItem mythicItem) {
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyGliderComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof AbstractItemGliderComponent other) ? false : other.canEqual(this);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemGliderComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int result = 1;
      return 1;
   }
}
