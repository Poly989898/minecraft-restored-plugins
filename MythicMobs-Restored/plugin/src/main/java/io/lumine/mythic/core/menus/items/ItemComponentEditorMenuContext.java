package io.lumine.mythic.core.menus.items;

import io.lumine.mythic.core.items.MythicItem;
import java.util.function.Consumer;
import lombok.Generated;
import org.bukkit.entity.Player;

public class ItemComponentEditorMenuContext {
   private final MythicItem item;
   private final ItemEditorMenuContext previousMenu;
   private Consumer<Player> reopenMenu;

   public void openMenu(Player player) {
      if (this.reopenMenu != null) {
         this.reopenMenu.accept(player);
      } else {
         this.previousMenu.openMenu(player);
      }
   }

   public void openPreviousMenu(Player player) {
      this.previousMenu.openMenu(player);
   }

   @Generated
   public ItemComponentEditorMenuContext(MythicItem item, ItemEditorMenuContext previousMenu) {
      this.item = item;
      this.previousMenu = previousMenu;
   }

   @Generated
   public MythicItem getItem() {
      return this.item;
   }

   @Generated
   public ItemEditorMenuContext getPreviousMenu() {
      return this.previousMenu;
   }

   @Generated
   public Consumer<Player> getReopenMenu() {
      return this.reopenMenu;
   }

   @Generated
   public void setReopenMenu(Consumer<Player> reopenMenu) {
      this.reopenMenu = reopenMenu;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ItemComponentEditorMenuContext other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$item = this.getItem();
         Object other$item = other.getItem();
         if (this$item == null ? other$item == null : this$item.equals(other$item)) {
            Object this$previousMenu = this.getPreviousMenu();
            Object other$previousMenu = other.getPreviousMenu();
            if (this$previousMenu == null ? other$previousMenu == null : this$previousMenu.equals(other$previousMenu)) {
               Object this$reopenMenu = this.getReopenMenu();
               Object other$reopenMenu = other.getReopenMenu();
               return this$reopenMenu == null ? other$reopenMenu == null : this$reopenMenu.equals(other$reopenMenu);
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
      return other instanceof ItemComponentEditorMenuContext;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $item = this.getItem();
      result = result * 59 + ($item == null ? 43 : $item.hashCode());
      Object $previousMenu = this.getPreviousMenu();
      result = result * 59 + ($previousMenu == null ? 43 : $previousMenu.hashCode());
      Object $reopenMenu = this.getReopenMenu();
      return result * 59 + ($reopenMenu == null ? 43 : $reopenMenu.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "ItemComponentEditorMenuContext(item="
         + this.getItem()
         + ", previousMenu="
         + this.getPreviousMenu()
         + ", reopenMenu="
         + this.getReopenMenu()
         + ")";
   }
}
