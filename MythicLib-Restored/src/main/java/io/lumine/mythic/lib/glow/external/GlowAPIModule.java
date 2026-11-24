package io.lumine.mythic.lib.glow.external;

import io.lumine.mythic.lib.glow.GlowModule;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.inventivetalent.glow.GlowAPI;
import org.inventivetalent.glow.GlowAPI.Color;

@Deprecated
public class GlowAPIModule implements GlowModule {
   private final Map<ChatColor, Color> glowColors = new HashMap<>();

   @Override
   public void enable() {
      this.glowColors.put(ChatColor.BLACK, Color.BLACK);
      this.glowColors.put(ChatColor.DARK_BLUE, Color.DARK_BLUE);
      this.glowColors.put(ChatColor.DARK_GREEN, Color.DARK_GREEN);
      this.glowColors.put(ChatColor.DARK_AQUA, Color.DARK_AQUA);
      this.glowColors.put(ChatColor.DARK_RED, Color.DARK_RED);
      this.glowColors.put(ChatColor.DARK_PURPLE, Color.DARK_PURPLE);
      this.glowColors.put(ChatColor.GOLD, Color.GOLD);
      this.glowColors.put(ChatColor.GRAY, Color.GRAY);
      this.glowColors.put(ChatColor.DARK_GRAY, Color.DARK_GRAY);
      this.glowColors.put(ChatColor.BLUE, Color.BLUE);
      this.glowColors.put(ChatColor.GREEN, Color.GREEN);
      this.glowColors.put(ChatColor.AQUA, Color.AQUA);
      this.glowColors.put(ChatColor.RED, Color.RED);
      this.glowColors.put(ChatColor.LIGHT_PURPLE, Color.PURPLE);
      this.glowColors.put(ChatColor.YELLOW, Color.YELLOW);
      this.glowColors.put(ChatColor.WHITE, Color.WHITE);
   }

   @Override
   public void disable() {
   }

   @Override
   public void setGlowing(Entity entity, ChatColor color) {
      GlowAPI.setGlowing(entity, Objects.requireNonNull(this.glowColors.get(color), "Color not found"), Bukkit.getOnlinePlayers());
   }

   @Override
   public void disableGlowing(Entity entity) {
      GlowAPI.setGlowing(entity, Color.NONE, Bukkit.getOnlinePlayers());
   }
}
