package net.Indyuce.mmoitems.api.item.util.crafting;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.util.AdventureUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.Indyuce.mmoitems.api.crafting.CraftingStatus;
import net.Indyuce.mmoitems.api.item.util.ConfigItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class QueueItemDisplay extends ConfigItem {
   private static final long[] ms = new long[]{1000L, 60000L, 3600000L, 86400000L};
   private static final String[] chars = new String[]{"s", "m", "h", "d"};

   public QueueItemDisplay() {
      super(
         "QUEUE_ITEM_DISPLAY",
         Material.BARRIER,
         "&6&lQueue&f #name#",
         "{ready}&7&oThis item was successfully crafted.",
         "{queue}&7&oThis item is in the crafting queue.",
         "{queue}",
         "{queue}&7Time Left: &c#left#",
         "",
         "{ready}&eClick to claim!",
         "{queue}&eClick to cancel."
      );
   }

   public QueueItemDisplay.ItemBuilder newBuilder(CraftingStatus.CraftingQueue.QueueItem var1, int var2) {
      return new QueueItemDisplay.ItemBuilder(var1, var2);
   }

   private String formatDelay(long var1) {
      StringBuilder var3 = new StringBuilder();
      int var4 = 0;

      for (int var5 = ms.length - 1; var5 >= 0 && var4 < 2; var5--) {
         if (var1 >= ms[var5]) {
            var3.append(var1 / ms[var5]).append(chars[var5]).append(" ");
            var1 %= ms[var5];
            var4++;
         }
      }

      return var3.length() == 0 ? "1s" : var3.toString();
   }

   public class ItemBuilder {
      private final CraftingStatus.CraftingQueue.QueueItem crafting;
      private final int position;
      private final String name = QueueItemDisplay.this.getName();
      private final List<String> lore = new ArrayList<>(QueueItemDisplay.this.getLore());

      public ItemBuilder(CraftingStatus.CraftingQueue.QueueItem nullx, int nullxx) {
         this.crafting = nullx;
         this.position = nullxx;
      }

      public ItemStack build() {
         HashMap<String, String> var1 = new HashMap<>();
         Iterator var2 = this.lore.iterator();

         while (var2.hasNext()) {
            String var3 = (String)var2.next();
            if (var3.startsWith("{queue}")) {
               if (this.crafting.isReady()) {
                  var2.remove();
                  continue;
               }

               var1.put(var3, var3.replace("{queue}", ""));
            }

            if (var3.startsWith("{ready}")) {
               if (!this.crafting.isReady()) {
                  var2.remove();
               } else {
                  var1.put(var3, var3.replace("{ready}", ""));
               }
            }
         }

         for (String var6 : var1.keySet()) {
            this.lore.set(this.lore.indexOf(var6), ((String)var1.get(var6)).replace("#left#", QueueItemDisplay.this.formatDelay(this.crafting.getLeft())));
         }

         ItemStack var5 = this.crafting.getRecipe().getPreviewItemStack();
         var5.setAmount(this.position);
         ItemMeta var7 = var5.getItemMeta();
         AdventureUtils.setDisplayName(var7, this.name.replace("#name#", var7.getDisplayName()));
         AdventureUtils.setLore(var7, this.lore);
         var7.addItemFlags(ItemFlag.values());
         var5.setItemMeta(var7);
         return NBTItem.get(var5).addTag(new ItemTag[]{new ItemTag("queueId", this.crafting.getUniqueId().toString())}).toItem();
      }
   }
}
