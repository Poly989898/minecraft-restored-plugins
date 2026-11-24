package net.Indyuce.mmocore.comp.mythicmobs.load;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.Random;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.util.item.CurrencyItemBuilder;
import net.Indyuce.mmocore.util.item.SimpleItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GoldPouchDrop extends Drop implements IItemDrop {
   private final int min;
   private final int max;
   private static final Random random = new Random();

   public GoldPouchDrop(MythicLineConfig config) {
      super(config.getLine(), config);
      this.min = config.getInteger("min", 1);
      this.max = config.getInteger("max", 10);
   }

   public AbstractItemStack getDrop(DropMetadata dropMetadata, double v) {
      NBTItem nbt = NBTItem.get(new SimpleItemBuilder("MOB_GOLD_POUCH").build());
      ItemStack[] content = new ItemStack[18];
      int money = random.nextInt(this.max - this.min + 1) + this.min;

      for (int j = 0; j < 7 && money > 0; j++) {
         int a = j == 6 ? money : Math.max(1, (int)((0.12 + random.nextDouble() * 0.4) * money));
         money -= a;
         if (a < 30 && random.nextDouble() < 0.3) {
            content[this.getAvailableSlot(content)] = this.setAmount(new CurrencyItemBuilder("GOLD_COIN", 1).build(), a);
         } else {
            content[this.getAvailableSlot(content)] = new CurrencyItemBuilder("NOTE", a).build();
         }
      }

      nbt.addTag(
         new ItemTag[]{new ItemTag("RpgPouchSize", 18), new ItemTag("RpgPouchMob", true), new ItemTag("RpgPouchInventory", MMOCoreUtils.toBase64(content))}
      );
      return BukkitAdapter.adapt(nbt.toItem());
   }

   private ItemStack setAmount(ItemStack item, int amount) {
      item.setAmount(amount);
      return item;
   }

   private int getAvailableSlot(ItemStack[] content) {
      int slot;
      while (content[slot = random.nextInt(content.length)] != null && content[slot].getType() != Material.AIR) {
      }

      return slot;
   }
}
