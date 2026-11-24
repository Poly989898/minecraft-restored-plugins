package net.Indyuce.mmoitems.api.item.util.identify;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.util.AdventureUtils;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.item.util.ConfigItem;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class UnidentifiedItem extends ConfigItem {
   public UnidentifiedItem(Type type) {
      super("unidentified", type.getItem().getType());
      this.setName("#prefix#Unidentified " + type.getName());
      this.setLore(
         Arrays.asList(
            "&7This item is unidentified. I must",
            "&7find a way to identify it!",
            "{tier}",
            "{tier}&8Item Info:",
            "{range}&8- &7Lvl Range: &e#range#",
            "{tier}&8- &7Item Tier: #prefix##tier#"
         )
      );
   }

   public UnidentifiedItem.ItemBuilder newBuilder(NBTItem nbtItem) {
      return new UnidentifiedItem.ItemBuilder(nbtItem);
   }

   public class ItemBuilder {
      private final int amount;
      private final NBTItem item;
      private String name = UnidentifiedItem.this.getName();
      private final List<String> lore = new ArrayList<>(UnidentifiedItem.this.getLore());

      public ItemBuilder(NBTItem nullx) {
         this.amount = nullx.getItem().getAmount();
         this.item = nullx;
      }

      public ItemStack build() {
         VolatileMMOItem volatileMMOItem = new VolatileMMOItem(this.item);
         ItemTier itemTier = ItemTier.ofItem(this.item);
         int count = volatileMMOItem.hasData(ItemStats.REQUIRED_LEVEL) ? (int)((DoubleData)volatileMMOItem.getData(ItemStats.REQUIRED_LEVEL)).getValue() : -1;
         HashMap<String, String> map = new HashMap<>();
         if (itemTier != null) {
            map.put("prefix", itemTier.getUnidentificationInfo().getPrefix());
            map.put("tier", itemTier.getUnidentificationInfo().getDisplayName());
            if (count > -1) {
               int[] range = itemTier.getUnidentificationInfo().calculateRange(count);
               map.put("range", range[0] + "-" + range[1]);
            }
         } else {
            this.name = this.name.replace("#prefix#", "");
         }

         this.lore.removeIf(var2x -> var2x.startsWith("{tier}") && itemTier == null || var2x.startsWith("{range}") && (itemTier == null || count < 0));

         for (String key : map.keySet()) {
            this.name = this.name.replace("#" + key + "#", (CharSequence)map.get(key));
         }

         for (int i = 0; i < this.lore.size(); i++) {
            String text = this.lore.get(i);

            for (String key : map.keySet()) {
               text = text.replace("#" + key + "#", (CharSequence)map.get(key));
            }

            this.lore.set(i, MythicLib.plugin.parseColors(text.replace("{range}", "").replace("{tier}", "")));
         }

         this.item.getItem().setAmount(1);
         ItemStack item = NBTItem.get(new ItemStack(UnidentifiedItem.this.material != null ? UnidentifiedItem.this.material : this.item.getItem().getType()))
            .addTag(new ItemTag[]{new ItemTag("MMOITEMS_UNIDENTIFIED_ITEM", this.serialize(this.item.getItem()))})
            .toItem();
         ItemMeta itemMeta = item.getItemMeta();
         if (UnidentifiedItem.this.customModelData != null) {
            itemMeta.setCustomModelData(UnidentifiedItem.this.customModelData);
         } else if (this.item.getItem().hasItemMeta() && this.item.getItem().getItemMeta().hasCustomModelData()) {
            itemMeta.setCustomModelData(this.item.getItem().getItemMeta().getCustomModelData());
         }

         item.setAmount(this.amount);
         itemMeta.addItemFlags(ItemFlag.values());
         itemMeta.setUnbreakable(true);
         AdventureUtils.setDisplayName(itemMeta, this.name);
         AdventureUtils.setLore(itemMeta, this.lore);
         item.setItemMeta(itemMeta);
         return item;
      }

      @NotNull
      private String serialize(ItemStack item) {
         try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);
            bukkitObjectOutputStream.writeObject(item);
            bukkitObjectOutputStream.close();
            return Base64Coder.encodeLines(byteArrayOutputStream.toByteArray());
         } catch (Exception exception) {
            throw new RuntimeException(exception);
         }
      }
   }
}
