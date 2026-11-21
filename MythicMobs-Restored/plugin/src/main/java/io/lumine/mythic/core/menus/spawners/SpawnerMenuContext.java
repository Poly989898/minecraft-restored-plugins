package io.lumine.mythic.core.menus.spawners;

import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;

@Getter
@Setter
@ToString
public class SpawnerMenuContext {
   private SpawnerBrowseMode mode = SpawnerBrowseMode.DISTANCE;
   private String filter = "";

   public boolean hasFilter() {
      return this.filter != null && !this.filter.isEmpty();
   }

   public void clearFilter() {
      this.filter = "";
   }

   public void openMenu(Player player) {
      MythicBukkit.inst().getMenuManager().getSpawnerBrowseMenu().open(player, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SpawnerMenuContext other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$mode = this.getMode();
         Object other$mode = other.getMode();
         if (this$mode == null ? other$mode == null : this$mode.equals(other$mode)) {
            Object this$filter = this.getFilter();
            Object other$filter = other.getFilter();
            return this$filter == null ? other$filter == null : this$filter.equals(other$filter);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof SpawnerMenuContext;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $mode = this.getMode();
      result = result * 59 + ($mode == null ? 43 : $mode.hashCode());
      Object $filter = this.getFilter();
      return result * 59 + ($filter == null ? 43 : $filter.hashCode());
   }
}
