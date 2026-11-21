package com.ticxo.modelengine.api.utils.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Consumer;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemUtils {
   public static byte[] encodeItemStack(ItemStack item) {
      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
         BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

         byte[] bs;
         try {
            dataOutput.writeObject(item);
            bs = outputStream.toByteArray();
         } catch (Throwable e) {
            try {
               dataOutput.close();
            } catch (Throwable ex) {
               e.addSuppressed(ex);
            }

            throw e;
         }

         dataOutput.close();
         return bs;
      } catch (IOException ex2) {
         throw new RuntimeException(ex2);
      }
   }

   public static String encodeItemStackToString(ItemStack item) {
      return encode(encodeItemStack(item));
   }

   public static ItemStack decodeItemStack(byte[] buf) {
      try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf)) {
         BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

         ItemStack itemStack;
         try {
            itemStack = (ItemStack)dataInput.readObject();
         } catch (Throwable e) {
            try {
               dataInput.close();
            } catch (Throwable ex) {
               e.addSuppressed(ex);
            }

            throw e;
         }

         dataInput.close();
         return itemStack;
      } catch (IOException | ClassNotFoundException ex2) {
         throw new RuntimeException(ex2);
      }
   }

   public static ItemStack decodeItemStack(String data) {
      return decodeItemStack(decode(data));
   }

   public static String encode(byte[] buf) {
      return Base64.getEncoder().encodeToString(buf);
   }

   public static byte[] decode(String src) {
      try {
         return Base64.getDecoder().decode(src);
      } catch (IllegalArgumentException e) {
         try {
            return Base64Coder.decodeLines(src);
         } catch (Exception ex) {
            throw e;
         }
      }
   }

   public static void name(ItemStack stack, Component component) {
      meta(stack, meta -> meta.displayName(component));
   }

   public static void lore(ItemStack stack, Component... components) {
      ArrayList<Component> list = new ArrayList<>();

      for (Component component : components) {
         if (component != null) {
            list.add(component);
         }
      }

      meta(stack, meta -> meta.lore(list));
   }

   public static void lore(ItemStack stack, Collection<Component> components) {
      ArrayList<Component> list = new ArrayList<>();

      for (Component component : components) {
         if (component != null) {
            list.add(component);
         }
      }

      meta(stack, meta -> meta.lore(list));
   }

   public static <T extends ItemMeta> void meta(ItemStack stack, Consumer<T> metaConsumer) {
      try {
         ItemMeta meta = stack.getItemMeta();
         metaConsumer.accept((T)meta);
         stack.setItemMeta(meta);
      } catch (ClassCastException e) {
         e.printStackTrace();
      }
   }
}
