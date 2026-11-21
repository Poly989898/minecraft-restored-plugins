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
public class AbstractItemBucketEntityDataComponent implements AbstractItemComponent {
   private String snbt;

   public AbstractItemBucketEntityDataComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("BucketEntityData")) {
         this.snbt = config.getString("BucketEntityData");
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyBucketEntityDataComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemBucketEntityDataComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$snbt = this.getSnbt();
         Object other$snbt = other.getSnbt();
         return this$snbt == null ? other$snbt == null : this$snbt.equals(other$snbt);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemBucketEntityDataComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $snbt = this.getSnbt();
      return result * 59 + ($snbt == null ? 43 : $snbt.hashCode());
   }
}
