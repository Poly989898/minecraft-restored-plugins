package io.lumine.mythic.core.menus.mobs;

import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;

@Getter
@Setter
@ToString
public class MobMenuContext {
   private MobBrowseMode mode = MobBrowseMode.PACKS;
   private String filter = "";
   private Pack packFilter = null;

   public boolean hasFilter() {
      return this.filter != null && !this.filter.isEmpty();
   }

   public void clearFilter() {
      this.filter = "";
   }

   public boolean hasPackFilter() {
      return this.packFilter != null;
   }

   public void clearPackFilter() {
      this.packFilter = null;
   }

   public void openMenu(Player player) {
      MythicBukkit.inst().getMenuManager().getMobBrowseMenu().open(player, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MobMenuContext other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$mode = this.getMode();
         Object other$mode = other.getMode();
         if (this$mode == null ? other$mode == null : this$mode.equals(other$mode)) {
            Object this$filter = this.getFilter();
            Object other$filter = other.getFilter();
            if (this$filter == null ? other$filter == null : this$filter.equals(other$filter)) {
               Object this$packFilter = this.getPackFilter();
               Object other$packFilter = other.getPackFilter();
               return this$packFilter == null ? other$packFilter == null : this$packFilter.equals(other$packFilter);
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
      return other instanceof MobMenuContext;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $mode = this.getMode();
      result = result * 59 + ($mode == null ? 43 : $mode.hashCode());
      Object $filter = this.getFilter();
      result = result * 59 + ($filter == null ? 43 : $filter.hashCode());
      Object $packFilter = this.getPackFilter();
      return result * 59 + ($packFilter == null ? 43 : $packFilter.hashCode());
   }
}
