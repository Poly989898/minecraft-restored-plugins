package io.lumine.mythic.lib.glow;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

public interface GlowModule {
   void setGlowing(Entity var1, ChatColor var2);

   void disableGlowing(Entity var1);

   void enable();

   void disable();
}
