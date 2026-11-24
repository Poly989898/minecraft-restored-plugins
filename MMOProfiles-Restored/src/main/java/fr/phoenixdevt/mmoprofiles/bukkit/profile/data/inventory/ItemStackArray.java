package fr.phoenixdevt.mmoprofiles.bukkit.profile.data.inventory;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import io.lumine.mythic.lib.UtilityMethods;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ItemStackArray {
   protected final ItemStack[] content;

   public ItemStackArray(int length) {
      Validate.isTrue(length >= 0, "Length must be positive");
      this.content = new ItemStack[length];
   }

   @Deprecated
   public ItemStackArray(int length, Map<String, Object> map) {
      this(length);

      for (Entry<String, Object> entry : map.entrySet()) {
         if (!entry.getKey().equals("==")) {
            this.content[Integer.parseInt(entry.getKey())] = (ItemStack)entry.getValue();
         }
      }
   }

   public ItemStackArray(int length, @NotNull String serializedInventory) {
      this(length);

      try {
         byte[] serializedBytes = Base64.getDecoder().decode(serializedInventory);
         ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedBytes);
         BukkitObjectInputStream bukkitAdapter = new BukkitObjectInputStream(inputStream);
         int streamSize = bukkitAdapter.readInt();

         for (int i = 0; i < streamSize; i++) {
            int slot = bukkitAdapter.readInt();
            ItemStack stack = (ItemStack)bukkitAdapter.readObject();
            this.content[slot] = stack;
         }

         bukkitAdapter.close();
      } catch (IOException | ClassNotFoundException exception) {
         throw new IllegalArgumentException("Unable to deserialize inventory", exception);
      } catch (IllegalArgumentException exception) {
         MMOProfiles.plugin
            .getLogger()
            .log(Level.SEVERE, "Newer item format detected - server downgrades are not supported!! You are going to lose inventory data");
      }
   }

   @NotNull
   public ItemStack[] asArray() {
      return this.content;
   }

   @Nullable
   public ItemStack at(int index) {
      return this.content[index];
   }

   public int getLength() {
      return this.content.length;
   }

   public int countNonNullItems() {
      int c = 0;

      for (ItemStack item : this.content) {
         if (!UtilityMethods.isAir(item)) {
            c++;
         }
      }

      return c;
   }

   @NotNull
   public String serializeBase64() {
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         BukkitObjectOutputStream bukkitAdapter = new BukkitObjectOutputStream(outputStream);
         bukkitAdapter.writeInt(this.countNonNullItems());

         for (int i = 0; i < this.content.length; i++) {
            ItemStack curr;
            if (!UtilityMethods.isAir(curr = this.content[i])) {
               bukkitAdapter.writeInt(i);
               bukkitAdapter.writeObject(curr);
            }
         }

         bukkitAdapter.close();
         return new String(Base64.getEncoder().encode(outputStream.toByteArray()));
      } catch (IOException exception) {
         throw new IllegalArgumentException("Unable to serialize inventory", exception);
      }
   }
}
