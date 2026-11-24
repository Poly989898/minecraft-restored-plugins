package io.lumine.mythic.lib.glow;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

public interface GlowModule {
   void setGlowing(Entity entity, ChatColor chatColor);

   void disableGlowing(Entity entity);

   void enable();

   void disable();
}
