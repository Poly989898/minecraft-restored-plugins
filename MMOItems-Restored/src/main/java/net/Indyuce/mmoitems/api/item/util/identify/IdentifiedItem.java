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

   public IdentifiedItem(NBTItem var1) {
      this.item = var1;
   }

   @NotNull
   public ItemStack identify() {
      return this.deserialize(this.item.getString("MMOITEMS_UNIDENTIFIED_ITEM"));
   }

   @NotNull
   private ItemStack deserialize(String var1) {
      try {
         ByteArrayInputStream var2 = new ByteArrayInputStream(Base64Coder.decodeLines(var1));
         BukkitObjectInputStream var3 = new BukkitObjectInputStream(var2);
         ItemStack var4 = (ItemStack)var3.readObject();
         var3.close();
         NBTItem var5 = NBTItem.get(var4);
         if (var5.hasType()) {
            LiveMMOItem var6 = new LiveMMOItem(var4);
            return var6.newBuilder().build();
         } else {
            return var4;
         }
      } catch (ClassNotFoundException | IOException var7) {
         throw new RuntimeException(var7);
      }
   }
}
