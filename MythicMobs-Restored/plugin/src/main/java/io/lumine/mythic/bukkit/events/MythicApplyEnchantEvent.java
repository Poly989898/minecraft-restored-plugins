package io.lumine.mythic.bukkit.events;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString
public class MythicApplyEnchantEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final ItemStack item;
   private final Enchantment enchantment;
   private final int level;
   private Boolean wasHandled = false;

   public MythicApplyEnchantEvent(ItemStack item, Enchantment enchantment, int level) {
      super(!Bukkit.getServer().isPrimaryThread());
      this.item = item;
      this.enchantment = enchantment;
      this.level = level;
   }

   public void completed() {
      this.wasHandled = true;
   }

   @NotNull
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
      } else if (!(o instanceof MythicApplyEnchantEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.getLevel() != other.getLevel()) {
         return false;
      } else {
         Object this$wasHandled = this.getWasHandled();
         Object other$wasHandled = other.getWasHandled();
         if (this$wasHandled == null ? other$wasHandled == null : this$wasHandled.equals(other$wasHandled)) {
            Object this$item = this.getItem();
            Object other$item = other.getItem();
            if (this$item == null ? other$item == null : this$item.equals(other$item)) {
               Object this$enchantment = this.getEnchantment();
               Object other$enchantment = other.getEnchantment();
               return this$enchantment == null ? other$enchantment == null : this$enchantment.equals(other$enchantment);
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
      return other instanceof MythicApplyEnchantEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getLevel();
      Object $wasHandled = this.getWasHandled();
      result = result * 59 + ($wasHandled == null ? 43 : $wasHandled.hashCode());
      Object $item = this.getItem();
      result = result * 59 + ($item == null ? 43 : $item.hashCode());
      Object $enchantment = this.getEnchantment();
      return result * 59 + ($enchantment == null ? 43 : $enchantment.hashCode());
   }
}
