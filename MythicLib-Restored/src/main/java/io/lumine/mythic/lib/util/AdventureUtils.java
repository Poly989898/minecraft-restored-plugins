package io.lumine.mythic.lib.util;

import io.lumine.mythic.lib.MythicLib;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class AdventureUtils {
   @NotNull
   public static Optional<ChatColor> getByName(@NotNull String name) {
      return Arrays.stream(ChatColor.values()).filter(chatColor -> chatColor.name().equalsIgnoreCase(name)).filter(ChatColor::isColor).findFirst();
   }

   @NotNull
   public static Optional<net.md_5.bungee.api.ChatColor> getByHex(@NotNull String hex) {
      if (hex.length() == 7 && hex.startsWith("#")) {
         hex = hex.substring(1);
      }

      if (hex.length() != 6) {
         return Optional.empty();
      }

      try {
         return Optional.of(net.md_5.bungee.api.ChatColor.of('#' + hex));
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   public static void runAsync(final Runnable runnable) {
      if (!Bukkit.isPrimaryThread()) {
         runnable.run();
      } else {
         (new BukkitRunnable() {
            public void run() {
               runnable.run();
            }
         }).runTaskAsynchronously(MythicLib.plugin);
      }
   }

   public static <U> CompletableFuture<U> supplyAsync(final Supplier<U> supplier) {
      if (!Bukkit.isPrimaryThread()) {
         return CompletableFuture.completedFuture(supplier.get());
      }

      final CompletableFuture<U> future = new CompletableFuture<>();
      (new BukkitRunnable() {
         public void run() {
            try {
               future.complete(supplier.get());
            } catch (Exception e) {
               future.completeExceptionally(e);
            }
         }
      }).runTaskAsynchronously(MythicLib.plugin);
      return future;
   }

   public static Color color(String raw) {
      try {
         net.md_5.bungee.api.ChatColor chatColor = net.md_5.bungee.api.ChatColor.of(raw);
         return chatColor.getColor();
      } catch (Exception e) {
         return Color.WHITE;
      }
   }

   public static ItemMeta setDisplayName(ItemMeta meta, String name) {
      meta.setDisplayName(MythicLib.plugin.parseColors(name));
      return meta;
   }

   public static ItemMeta setLore(ItemMeta meta, List<String> lore) {
      meta.setLore(MythicLib.plugin.parseColors(lore));
      return meta;
   }
}
