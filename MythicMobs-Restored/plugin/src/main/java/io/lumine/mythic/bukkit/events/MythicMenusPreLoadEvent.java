package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.menus.MenuManager;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@ToString
public class MythicMenusPreLoadEvent extends Event {
   private final MythicBukkit plugin;
   private final MenuManager manager;
   private static final HandlerList handlers = new HandlerList();

   public MythicMenusPreLoadEvent(MythicBukkit plugin, MenuManager manager) {
      this.plugin = plugin;
      this.manager = manager;
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
      } else if (!(o instanceof MythicMenusPreLoadEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$plugin = this.getPlugin();
         Object other$plugin = other.getPlugin();
         if (this$plugin == null ? other$plugin == null : this$plugin.equals(other$plugin)) {
            Object this$manager = this.getManager();
            Object other$manager = other.getManager();
            return this$manager == null ? other$manager == null : this$manager.equals(other$manager);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof MythicMenusPreLoadEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $plugin = this.getPlugin();
      result = result * 59 + ($plugin == null ? 43 : $plugin.hashCode());
      Object $manager = this.getManager();
      return result * 59 + ($manager == null ? 43 : $manager.hashCode());
   }
}
