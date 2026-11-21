package io.lumine.mythic.core.menus.items;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import lombok.Generated;
import org.bukkit.entity.Player;

public class ItemEditorMenuContext {
   private final MythicItem item;
   private final ItemMenuContext previousMenu;

   public void openMenu(Player player) {
      MythicBukkit.inst().getMenuManager().getItemEditorMenu().open(player, this);
   }

   public void openPreviousMenu(Player player) {
      MythicBukkit.inst().getMenuManager().getItemBrowseMenu().open(player, this.previousMenu);
   }

   @Generated
   public ItemEditorMenuContext(MythicItem item, ItemMenuContext previousMenu) {
      this.item = item;
      this.previousMenu = previousMenu;
   }

   @Generated
   public MythicItem getItem() {
      return this.item;
   }

   @Generated
   public ItemMenuContext getPreviousMenu() {
      return this.previousMenu;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ItemEditorMenuContext other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$item = this.getItem();
         Object other$item = other.getItem();
         if (this$item == null ? other$item == null : this$item.equals(other$item)) {
            Object this$previousMenu = this.getPreviousMenu();
            Object other$previousMenu = other.getPreviousMenu();
            return this$previousMenu == null ? other$previousMenu == null : this$previousMenu.equals(other$previousMenu);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof ItemEditorMenuContext;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $item = this.getItem();
      result = result * 59 + ($item == null ? 43 : $item.hashCode());
      Object $previousMenu = this.getPreviousMenu();
      return result * 59 + ($previousMenu == null ? 43 : $previousMenu.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "ItemEditorMenuContext(item=" + this.getItem() + ", previousMenu=" + this.getPreviousMenu() + ")";
   }
}
