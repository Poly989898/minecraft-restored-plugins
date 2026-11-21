package io.lumine.mythic.core.items;

import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.core.menus.items.ItemMenuContext;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ItemGroup implements MenuData<ItemMenuContext> {
   private final String id;
   private final String name;
   private final ItemFactory menuItem;

   @Override
   public Icon<ItemMenuContext> getIcon() {
      return IconBuilder.<ItemMenuContext>create().item(this.menuItem).name(this.name).click((context, player) -> {}).build();
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ItemGroup other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$id = this.getId();
         Object other$id = other.getId();
         if (this$id == null ? other$id == null : this$id.equals(other$id)) {
            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null ? other$name == null : this$name.equals(other$name)) {
               Object this$menuItem = this.getMenuItem();
               Object other$menuItem = other.getMenuItem();
               return this$menuItem == null ? other$menuItem == null : this$menuItem.equals(other$menuItem);
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
      return other instanceof ItemGroup;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $menuItem = this.getMenuItem();
      return result * 59 + ($menuItem == null ? 43 : $menuItem.hashCode());
   }
}
