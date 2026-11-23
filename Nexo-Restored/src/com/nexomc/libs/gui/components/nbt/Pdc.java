package com.nexomc.libs.gui.components.nbt;

import com.nexomc.libs.gui.TriumphGui;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Pdc implements NbtWrapper {
   private static final Plugin PLUGIN = TriumphGui.getPlugin();

   public ItemStack setString(@NotNull ItemStack itemStack, String key, String value) {
      ItemMeta meta = itemStack.getItemMeta();
      if (meta == null) {
         return itemStack;
      } else {
         meta.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, key), PersistentDataType.STRING, value);
         itemStack.setItemMeta(meta);
         return itemStack;
      }
   }

   public ItemStack removeTag(@NotNull ItemStack itemStack, String key) {
      ItemMeta meta = itemStack.getItemMeta();
      if (meta == null) {
         return itemStack;
      } else {
         meta.getPersistentDataContainer().remove(new NamespacedKey(PLUGIN, key));
         itemStack.setItemMeta(meta);
         return itemStack;
      }
   }

   public ItemStack setBoolean(@NotNull ItemStack itemStack, String key, boolean value) {
      ItemMeta meta = itemStack.getItemMeta();
      if (meta == null) {
         return itemStack;
      } else {
         meta.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, key), PersistentDataType.BYTE, (byte) (value ? 1 : 0));
         itemStack.setItemMeta(meta);
         return itemStack;
      }
   }

   public @Nullable String getString(@NotNull ItemStack itemStack, String key) {
      ItemMeta meta = itemStack.getItemMeta();
      return meta == null ? null : (String)meta.getPersistentDataContainer().get(new NamespacedKey(PLUGIN, key), PersistentDataType.STRING);
   }
}
