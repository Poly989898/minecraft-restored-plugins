package net.Indyuce.mmoitems.api.droptable.item;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.RandomAmount;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MMOItemDropItem extends DropItem {
   private final Type type;
   private final String id;
   private final double unidentification;

   public MMOItemDropItem(Type type, String text, double value, double value2, RandomAmount randomAmount) {
      this(type, text, value, value2, randomAmount.getMin(), randomAmount.getMax());
   }

   public MMOItemDropItem(Type type, String text, double value, double value2, int count, int count2) {
      super(value, count, count2);
      this.type = type;
      this.id = text;
      this.unidentification = value2;
   }

   public MMOItemDropItem(Type type, String text, String text2) {
      super(text2);
      this.type = type;
      this.id = text;
      String[] texts = text2.split(",");
      this.unidentification = Double.parseDouble(texts[2]) / 100.0;
   }

   public Type getType() {
      return this.type;
   }

   public String getId() {
      return this.id;
   }

   public boolean rollIdentification() {
      return random.nextDouble() < this.unidentification;
   }

   @Override
   public ItemStack getItem(PlayerData playerData, int count) {
      if (!MMOItems.plugin.getTemplates().hasTemplate(this.type, this.id)) {
         return null;
      }

      ItemStack item = playerData == null ? MMOItems.plugin.getItem(this.type, this.id) : MMOItems.plugin.getItem(this.type, this.id, playerData);
      if (item != null && item.getType() != Material.AIR) {
         if (this.rollIdentification()) {
            item = this.type.getUnidentifiedTemplate().newBuilder(NBTItem.get(item)).build();
         }

         item.setAmount(count);
         return item;
      } else {
         return null;
      }
   }

   @Override
   public String getKey() {
      return this.type.getId() + "." + this.id;
   }
}
