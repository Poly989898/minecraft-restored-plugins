package io.lumine.mythic.bukkit.utils.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public final class InventorySerialization {
   public static byte[] encodeItemStack(ItemStack item) {
      try {
         byte[] bs;
         try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

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
         }

         return bs;
      } catch (IOException ex2) {
         throw new RuntimeException(ex2);
      }
   }

   public static String encodeItemStackToString(ItemStack item) {
      return Base64Util.encode(encodeItemStack(item));
   }

   public static ItemStack decodeItemStack(byte[] buf) {
      try {
         ItemStack itemStack;
         try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf)) {
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

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
         }

         return itemStack;
      } catch (IOException | ClassNotFoundException ex2) {
         throw new RuntimeException(ex2);
      }
   }

   public static ItemStack decodeItemStack(String data) {
      return decodeItemStack(Base64Util.decode(data));
   }

   public static byte[] encodeItemStacks(ItemStack[] items) {
      try {
         byte[] bs;
         try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            try {
               dataOutput.writeInt(items.length);

               for (ItemStack item : items) {
                  dataOutput.writeObject(item);
               }

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
         }

         return bs;
      } catch (IOException ex2) {
         throw new RuntimeException(ex2);
      }
   }

   public static String encodeItemStacksToString(ItemStack[] items) {
      return Base64Util.encode(encodeItemStacks(items));
   }

   public static ItemStack[] decodeItemStacks(byte[] buf) {
      try {
         ItemStack[] itemStacks;
         try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf)) {
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            try {
               ItemStack[] items = new ItemStack[dataInput.readInt()];

               for (int i = 0; i < items.length; i++) {
                  items[i] = (ItemStack)dataInput.readObject();
               }

               itemStacks = items;
            } catch (Throwable e) {
               try {
                  dataInput.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }

               throw e;
            }

            dataInput.close();
         }

         return itemStacks;
      } catch (IOException | ClassNotFoundException ex2) {
         throw new RuntimeException(ex2);
      }
   }

   public static ItemStack[] decodeItemStacks(String data) {
      return decodeItemStacks(Base64Util.decode(data));
   }

   public static byte[] encodeInventory(Inventory inventory) {
      try {
         byte[] bs;
         try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            try {
               dataOutput.writeInt(inventory.getSize());

               for (int i = 0; i < inventory.getSize(); i++) {
                  dataOutput.writeObject(inventory.getItem(i));
               }

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
         }

         return bs;
      } catch (IOException ex2) {
         throw new RuntimeException(ex2);
      }
   }

   public static String encodeInventoryToString(Inventory inventory) {
      return Base64Util.encode(encodeInventory(inventory));
   }

   public static Inventory decodeInventory(byte[] buf, String title) {
      try {
         Inventory inventory2;
         try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf)) {
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            try {
               Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt(), title);

               for (int i = 0; i < inventory.getSize(); i++) {
                  inventory.setItem(i, (ItemStack)dataInput.readObject());
               }

               inventory2 = inventory;
            } catch (Throwable e) {
               try {
                  dataInput.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }

               throw e;
            }

            dataInput.close();
         }

         return inventory2;
      } catch (IOException | ClassNotFoundException ex2) {
         throw new RuntimeException(ex2);
      }
   }

   public static Inventory decodeInventory(String data, String title) {
      return decodeInventory(Base64Util.decode(data), title);
   }

   private InventorySerialization() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
