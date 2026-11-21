package io.lumine.mythic.core.menus.spawners;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import lombok.Generated;
import org.bukkit.entity.Player;

public class SpawnerEditorMenuContext {
   private final MythicSpawner spawner;
   private final SpawnerMenuContext previousMenu;
   private String filter = "";

   public boolean hasFilter() {
      return this.filter != null && !this.filter.isEmpty();
   }

   public void clearFilter() {
      this.filter = "";
   }

   public void openMenu(Player player) {
      MythicBukkit.inst().getMenuManager().getSpawnerEditorMenu().open(player, this);
   }

   public void openPreviousMenu(Player player) {
      MythicBukkit.inst().getMenuManager().getSpawnerBrowseMenu().open(player, this.previousMenu);
   }

   @Generated
   public SpawnerEditorMenuContext(MythicSpawner spawner, SpawnerMenuContext previousMenu) {
      this.spawner = spawner;
      this.previousMenu = previousMenu;
   }

   @Generated
   public MythicSpawner getSpawner() {
      return this.spawner;
   }

   @Generated
   public SpawnerMenuContext getPreviousMenu() {
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
      } else if (!(o instanceof SpawnerEditorMenuContext other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$spawner = this.getSpawner();
         Object other$spawner = other.getSpawner();
         if (this$spawner == null ? other$spawner == null : this$spawner.equals(other$spawner)) {
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
      return other instanceof SpawnerEditorMenuContext;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $spawner = this.getSpawner();
      result = result * 59 + ($spawner == null ? 43 : $spawner.hashCode());
      Object $previousMenu = this.getPreviousMenu();
      result = result * 59 + ($previousMenu == null ? 43 : $previousMenu.hashCode());
      Object $filter = this.getFilter();
      return result * 59 + ($filter == null ? 43 : $filter.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "SpawnerEditorMenuContext(spawner=" + this.getSpawner() + ", previousMenu=" + this.getPreviousMenu() + ", filter=" + this.getFilter() + ")";
   }
}
