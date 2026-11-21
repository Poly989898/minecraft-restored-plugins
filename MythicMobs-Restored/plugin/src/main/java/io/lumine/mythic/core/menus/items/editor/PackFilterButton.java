package io.lumine.mythic.core.menus.items.editor;

import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.core.menus.items.ItemMenuContext;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;

@Getter
@ToString
@AllArgsConstructor
public class PackFilterButton implements MenuData<ItemMenuContext> {
   private final Pack pack;

   @Override
   public Icon<ItemMenuContext> getIcon() {
      return IconBuilder.<ItemMenuContext>create()
         .material(Material.ENDER_CHEST)
         .name("<gold>Pack: <yellow>" + this.pack.getName())
         .click((context, player) -> {
            context.setFilter("pack:" + this.pack.getName());
            context.openMenu(player);
         })
         .build();
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PackFilterButton other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$pack = this.getPack();
         Object other$pack = other.getPack();
         return this$pack == null ? other$pack == null : this$pack.equals(other$pack);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof PackFilterButton;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $pack = this.getPack();
      return result * 59 + ($pack == null ? 43 : $pack.hashCode());
   }
}
