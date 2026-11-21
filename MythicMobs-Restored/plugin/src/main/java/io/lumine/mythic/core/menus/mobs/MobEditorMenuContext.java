package io.lumine.mythic.core.menus.mobs;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.Generated;
import org.bukkit.entity.Player;

public class MobEditorMenuContext {
   private final MythicMob mob;
   private final MobMenuContext previousMenu;
   private String filter = "";

   public boolean hasFilter() {
      return this.filter != null && !this.filter.isEmpty();
   }

   public void clearFilter() {
      this.filter = "";
   }

   public void openMenu(Player player) {
      MythicBukkit.inst().getMenuManager().getMobEditorMenu().open(player, this);
   }

   public void openPreviousMenu(Player player) {
      MythicBukkit.inst().getMenuManager().getMobBrowseMenu().open(player, this.previousMenu);
   }

   @Generated
   public MobEditorMenuContext(MythicMob mob, MobMenuContext previousMenu) {
      this.mob = mob;
      this.previousMenu = previousMenu;
   }

   @Generated
   public MythicMob getMob() {
      return this.mob;
   }

   @Generated
   public MobMenuContext getPreviousMenu() {
      return this.previousMenu;
   }

   @Generated
   public String getFilter() {
      return this.filter;
   }

   @Generated
   public void setFilter(String filter) {
      this.filter = filter;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MobEditorMenuContext other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$mob = this.getMob();
         Object other$mob = other.getMob();
         if (this$mob == null ? other$mob == null : this$mob.equals(other$mob)) {
            Object this$previousMenu = this.getPreviousMenu();
            Object other$previousMenu = other.getPreviousMenu();
            if (this$previousMenu == null ? other$previousMenu == null : this$previousMenu.equals(other$previousMenu)) {
               Object this$filter = this.getFilter();
               Object other$filter = other.getFilter();
               return this$filter == null ? other$filter == null : this$filter.equals(other$filter);
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
      return other instanceof MobEditorMenuContext;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $mob = this.getMob();
      result = result * 59 + ($mob == null ? 43 : $mob.hashCode());
      Object $previousMenu = this.getPreviousMenu();
      result = result * 59 + ($previousMenu == null ? 43 : $previousMenu.hashCode());
      Object $filter = this.getFilter();
      return result * 59 + ($filter == null ? 43 : $filter.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "MobEditorMenuContext(mob=" + this.getMob() + ", previousMenu=" + this.getPreviousMenu() + ", filter=" + this.getFilter() + ")";
   }
}
