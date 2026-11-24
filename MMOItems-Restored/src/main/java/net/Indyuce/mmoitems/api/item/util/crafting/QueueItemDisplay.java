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

   public QueueItemDisplay.ItemBuilder newBuilder(CraftingStatus.CraftingQueue.QueueItem queueItem, int count) {
      return new QueueItemDisplay.ItemBuilder(queueItem, count);
   }

   private String formatDelay(long value) {
      StringBuilder builder = new StringBuilder();
      int count = 0;

      for (int i = ms.length - 1; i >= 0 && count < 2; i--) {
         if (value >= ms[i]) {
            builder.append(value / ms[i]).append(chars[i]).append(" ");
            value %= ms[i];
            count++;
         }
      }

      return builder.length() == 0 ? "1s" : builder.toString();
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
         HashMap<String, String> map = new HashMap<>();
         Iterator iterator = this.lore.iterator();

         while (iterator.hasNext()) {
            String text = (String)iterator.next();
            if (text.startsWith("{queue}")) {
               if (this.crafting.isReady()) {
                  iterator.remove();
                  continue;
               }

               map.put(text, text.replace("{queue}", ""));
            }

            if (text.startsWith("{ready}")) {
               if (!this.crafting.isReady()) {
                  iterator.remove();
               } else {
                  map.put(text, text.replace("{ready}", ""));
               }
            }
         }

         for (String key : map.keySet()) {
            this.lore.set(this.lore.indexOf(key), ((String)map.get(key)).replace("#left#", QueueItemDisplay.this.formatDelay(this.crafting.getLeft())));
         }

         ItemStack previewItemStack = this.crafting.getRecipe().getPreviewItemStack();
         previewItemStack.setAmount(this.position);
         ItemMeta itemMeta = previewItemStack.getItemMeta();
         AdventureUtils.setDisplayName(itemMeta, this.name.replace("#name#", itemMeta.getDisplayName()));
         AdventureUtils.setLore(itemMeta, this.lore);
         itemMeta.addItemFlags(ItemFlag.values());
         previewItemStack.setItemMeta(itemMeta);
         return NBTItem.get(previewItemStack).addTag(new ItemTag[]{new ItemTag("queueId", this.crafting.getUniqueId().toString())}).toItem();
      }
   }
}
