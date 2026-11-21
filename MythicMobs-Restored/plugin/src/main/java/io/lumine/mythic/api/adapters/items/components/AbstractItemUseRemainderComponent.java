package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@ToString
public class AbstractItemUseRemainderComponent implements AbstractItemComponent {
   private PlaceholderString remainderItemName;
   private PlaceholderInt amount;

   public AbstractItemUseRemainderComponent(MythicItem mythicItem, MythicConfig config) {
      String section = config.isConfigurationSection("UseRemainder") ? "UseRemainder" : "Options.UseRemainder";
      if (config.isConfigurationSection(section)) {
         this.remainderItemName = config.getPlaceholderString(section + ".Item", null);
         this.amount = config.getPlaceholderInt(section + ".Amount", "1");
      } else {
         this.remainderItemName = config.getPlaceholderString(new String[]{"UseRemainder", "Options.UseRemainder"}, null);
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyUseRemainderComponent(dropMetadata, this);
   }

   public ItemStack resolveRemainderItem(DropMetadata dropMetadata) {
      if (this.remainderItemName == null) {
         return null;
      } else {
         String remainderName = this.remainderItemName.get(dropMetadata);
         if (remainderName != null && !remainderName.isEmpty()) {
            ItemStack remainderItem = MythicBukkit.inst().getItemManager().getItemStack(remainderName);
            if (remainderItem == null) {
               MythicLogger.error("UseRemainder component item not found: {0}", remainderName);
            } else if (this.amount != null) {
               remainderItem.setAmount(this.amount.get(dropMetadata));
            }

            return remainderItem;
         } else {
            return null;
         }
      }
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemUseRemainderComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$remainderItemName = this.getRemainderItemName();
         Object other$remainderItemName = other.getRemainderItemName();
         if (this$remainderItemName == null ? other$remainderItemName == null : this$remainderItemName.equals(other$remainderItemName)) {
            Object this$amount = this.getAmount();
            Object other$amount = other.getAmount();
            return this$amount == null ? other$amount == null : this$amount.equals(other$amount);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemUseRemainderComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $remainderItemName = this.getRemainderItemName();
      result = result * 59 + ($remainderItemName == null ? 43 : $remainderItemName.hashCode());
      Object $amount = this.getAmount();
      return result * 59 + ($amount == null ? 43 : $amount.hashCode());
   }
}
