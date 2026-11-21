package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import lombok.Generated;
import lombok.ToString;

@ToString
public class AbstractItemTrialSpawnerComponent implements AbstractItemComponent {
   public AbstractItemTrialSpawnerComponent(MythicItem item, MythicConfig config) {
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyTrialSpawnerComponent(this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof AbstractItemTrialSpawnerComponent other) ? false : other.canEqual(this);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemTrialSpawnerComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int result = 1;
      return 1;
   }
}
