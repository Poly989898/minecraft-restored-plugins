package me.TechsCode.UltraPermissions.base.item;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemSerializer {
   public ItemSerializer() {
   }

   public static String itemTo64(ItemStack item) {
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);
         bukkitObjectOutputStream.writeObject(item);
         bukkitObjectOutputStream.close();
         return Base64Coder.encodeLines(byteArrayOutputStream.toByteArray());
      } catch (Exception exception) {
         throw new IllegalStateException("Unable to save item stack.", exception);
      }
   }

   public static ItemStack itemFrom64(String text) throws IOException {
      try {
         ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(text));
         BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

         ItemStack item;
         try {
            item = (ItemStack)bukkitObjectInputStream.readObject();
         } catch (Throwable exception) {
            try {
               bukkitObjectInputStream.close();
            } catch (Throwable closeException) {
               exception.addSuppressed(closeException);
            }

            throw exception;
         }

         bukkitObjectInputStream.close();
         return item;
      } catch (ClassNotFoundException exception) {
         throw new IOException("Unable to decode class type.", exception);
      }
   }
}
