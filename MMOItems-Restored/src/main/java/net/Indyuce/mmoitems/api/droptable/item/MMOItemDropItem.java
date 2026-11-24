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

   public MMOItemDropItem(Type var1, String var2, double var3, double var5, RandomAmount var7) {
      this(var1, var2, var3, var5, var7.getMin(), var7.getMax());
   }

   public MMOItemDropItem(Type var1, String var2, double var3, double var5, int var7, int var8) {
      super(var3, var7, var8);
      this.type = var1;
      this.id = var2;
      this.unidentification = var5;
   }

   public MMOItemDropItem(Type var1, String var2, String var3) {
      super(var3);
      this.type = var1;
      this.id = var2;
      String[] var4 = var3.split(",");
      this.unidentification = Double.parseDouble(var4[2]) / 100.0;
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
   public ItemStack getItem(PlayerData var1, int var2) {
      if (!MMOItems.plugin.getTemplates().hasTemplate(this.type, this.id)) {
         return null;
      }

      ItemStack var3 = var1 == null ? MMOItems.plugin.getItem(this.type, this.id) : MMOItems.plugin.getItem(this.type, this.id, var1);
      if (var3 != null && var3.getType() != Material.AIR) {
         if (this.rollIdentification()) {
            var3 = this.type.getUnidentifiedTemplate().newBuilder(NBTItem.get(var3)).build();
         }

         var3.setAmount(var2);
         return var3;
      } else {
         return null;
      }
   }

   @Override
   public String getKey() {
      return this.type.getId() + "." + this.id;
   }
}
