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
   public UnidentifiedItem(Type var1) {
      super("unidentified", var1.getItem().getType());
      this.setName("#prefix#Unidentified " + var1.getName());
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

   public UnidentifiedItem.ItemBuilder newBuilder(NBTItem var1) {
      return new UnidentifiedItem.ItemBuilder(var1);
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
         VolatileMMOItem var1 = new VolatileMMOItem(this.item);
         ItemTier var2 = ItemTier.ofItem(this.item);
         int var3 = var1.hasData(ItemStats.REQUIRED_LEVEL) ? (int)((DoubleData)var1.getData(ItemStats.REQUIRED_LEVEL)).getValue() : -1;
         HashMap<String, String> var4 = new HashMap<>();
         if (var2 != null) {
            var4.put("prefix", var2.getUnidentificationInfo().getPrefix());
            var4.put("tier", var2.getUnidentificationInfo().getDisplayName());
            if (var3 > -1) {
               int[] var5 = var2.getUnidentificationInfo().calculateRange(var3);
               var4.put("range", var5[0] + "-" + var5[1]);
            }
         } else {
            this.name = this.name.replace("#prefix#", "");
         }

         this.lore.removeIf(var2x -> var2x.startsWith("{tier}") && var2 == null || var2x.startsWith("{range}") && (var2 == null || var3 < 0));

         for (String var6 : var4.keySet()) {
            this.name = this.name.replace("#" + var6 + "#", (CharSequence)var4.get(var6));
         }

         for (int var10 = 0; var10 < this.lore.size(); var10++) {
            String var12 = this.lore.get(var10);

            for (String var8 : var4.keySet()) {
               var12 = var12.replace("#" + var8 + "#", (CharSequence)var4.get(var8));
            }

            this.lore.set(var10, MythicLib.plugin.parseColors(var12.replace("{range}", "").replace("{tier}", "")));
         }

         this.item.getItem().setAmount(1);
         ItemStack var11 = NBTItem.get(new ItemStack(UnidentifiedItem.this.material != null ? UnidentifiedItem.this.material : this.item.getItem().getType()))
            .addTag(new ItemTag[]{new ItemTag("MMOITEMS_UNIDENTIFIED_ITEM", this.serialize(this.item.getItem()))})
            .toItem();
         ItemMeta var13 = var11.getItemMeta();
         if (UnidentifiedItem.this.customModelData != null) {
            var13.setCustomModelData(UnidentifiedItem.this.customModelData);
         } else if (this.item.getItem().hasItemMeta() && this.item.getItem().getItemMeta().hasCustomModelData()) {
            var13.setCustomModelData(this.item.getItem().getItemMeta().getCustomModelData());
         }

         var11.setAmount(this.amount);
         var13.addItemFlags(ItemFlag.values());
         var13.setUnbreakable(true);
         AdventureUtils.setDisplayName(var13, this.name);
         AdventureUtils.setLore(var13, this.lore);
         var11.setItemMeta(var13);
         return var11;
      }

      @NotNull
      private String serialize(ItemStack var1) {
         try {
            ByteArrayOutputStream var2 = new ByteArrayOutputStream();
            BukkitObjectOutputStream var3 = new BukkitObjectOutputStream(var2);
            var3.writeObject(var1);
            var3.close();
            return Base64Coder.encodeLines(var2.toByteArray());
         } catch (Exception var4) {
            throw new RuntimeException(var4);
         }
      }
   }
}
