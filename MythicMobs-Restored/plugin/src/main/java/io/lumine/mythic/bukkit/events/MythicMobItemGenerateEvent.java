package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@ToString
public class MythicMobItemGenerateEvent extends Event {
   private final MythicItem item;
   private final DropMetadata metadata;
   private ItemStack itemStack;
   private final AbstractEntity dropper;
   private final AbstractEntity trigger;
   private static final HandlerList handlers = new HandlerList();

   public MythicMobItemGenerateEvent(MythicItem item, DropMetadata metadata, ItemStack itemStack) {
      this.item = item;
      this.metadata = metadata;
      this.itemStack = itemStack;
      if (metadata != null) {
         this.dropper = metadata.getDropper().isPresent() ? metadata.getDropper().get().getEntity() : null;
         this.trigger = metadata.getCause().isPresent() ? metadata.getCause().get() : null;
      } else {
         this.dropper = null;
         this.trigger = null;
      }
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @Generated
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MythicMobItemGenerateEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$item = this.getItem();
         Object other$item = other.getItem();
         if (this$item == null ? other$item == null : this$item.equals(other$item)) {
            Object this$metadata = this.getMetadata();
            Object other$metadata = other.getMetadata();
            if (this$metadata == null ? other$metadata == null : this$metadata.equals(other$metadata)) {
               Object this$itemStack = this.getItemStack();
               Object other$itemStack = other.getItemStack();
               if (this$itemStack == null ? other$itemStack == null : this$itemStack.equals(other$itemStack)) {
                  Object this$dropper = this.getDropper();
                  Object other$dropper = other.getDropper();
                  if (this$dropper == null ? other$dropper == null : this$dropper.equals(other$dropper)) {
                     Object this$trigger = this.getTrigger();
                     Object other$trigger = other.getTrigger();
                     return this$trigger == null ? other$trigger == null : this$trigger.equals(other$trigger);
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
      return other instanceof MythicMobItemGenerateEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $item = this.getItem();
      result = result * 59 + ($item == null ? 43 : $item.hashCode());
      Object $metadata = this.getMetadata();
      result = result * 59 + ($metadata == null ? 43 : $metadata.hashCode());
      Object $itemStack = this.getItemStack();
      result = result * 59 + ($itemStack == null ? 43 : $itemStack.hashCode());
      Object $dropper = this.getDropper();
      result = result * 59 + ($dropper == null ? 43 : $dropper.hashCode());
      Object $trigger = this.getTrigger();
      return result * 59 + ($trigger == null ? 43 : $trigger.hashCode());
   }
}
