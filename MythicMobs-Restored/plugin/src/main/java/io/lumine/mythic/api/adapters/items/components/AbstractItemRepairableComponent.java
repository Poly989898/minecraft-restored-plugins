package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractItemRepairableComponent implements AbstractItemComponent {
   private List<String> itemTypes = Collections.emptyList();

   public AbstractItemRepairableComponent(MythicItem mythicItem, MythicConfig config) {
      List<String> items = null;
      if (config.isList("Repairable")) {
         items = config.getStringList("Repairable");
      } else if (config.isSet("Repairable")) {
         items = List.of(config.getString("Repairable"));
      }

      if (items != null && !items.isEmpty()) {
         this.itemTypes = items.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).toList();
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyRepairableComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemRepairableComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$itemTypes = this.getItemTypes();
         Object other$itemTypes = other.getItemTypes();
         return this$itemTypes == null ? other$itemTypes == null : this$itemTypes.equals(other$itemTypes);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemRepairableComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $itemTypes = this.getItemTypes();
      return result * 59 + ($itemTypes == null ? 43 : $itemTypes.hashCode());
   }
}
