package net.Indyuce.mmoitems.api.item.util.identify;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class IdentifiedItem {
   private final NBTItem item;

   public IdentifiedItem(NBTItem nbtItem) {
      this.item = nbtItem;
   }

   @NotNull
   public ItemStack identify() {
      return this.deserialize(this.item.getString("MMOITEMS_UNIDENTIFIED_ITEM"));
   }

   @NotNull
   private ItemStack deserialize(String text) {
      try {
         ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(text));
         BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
         ItemStack item = (ItemStack)bukkitObjectInputStream.readObject();
         bukkitObjectInputStream.close();
         NBTItem nbtItem = NBTItem.get(item);
         if (nbtItem.hasType()) {
            LiveMMOItem liveMMOItem = new LiveMMOItem(item);
            return liveMMOItem.newBuilder().build();
         } else {
            return item;
         }
      } catch (ClassNotFoundException | IOException exception) {
         throw new RuntimeException(exception);
      }
   }
}
