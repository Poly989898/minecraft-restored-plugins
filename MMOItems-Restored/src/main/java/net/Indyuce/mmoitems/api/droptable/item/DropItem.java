package net.Indyuce.mmoitems.api.droptable.item;

import java.util.Random;
import javax.annotation.Nullable;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.inventory.ItemStack;

public abstract class DropItem {
   private final double drop;
   private final int min;
   private final int max;
   protected static final Random random = new Random();

   public DropItem(double var1, int var3, int var4) {
      this.drop = var1;
      this.min = var3;
      this.max = var4;
   }

   public DropItem(String var1) {
      String[] var2 = var1.split(",");
      this.drop = Double.parseDouble(var2[0]) / 100.0;
      String[] var3 = var2[1].split("-");
      this.min = Integer.parseInt(var3[0]);
      this.max = var3.length > 1 ? Integer.parseInt(var3[1]) : this.min;
   }

   public boolean rollDrop() {
      return random.nextDouble() < this.drop;
   }

   public int rollAmount() {
      return this.max > this.min ? this.min + random.nextInt(this.max - this.min + 1) : this.min;
   }

   public ItemStack getItem(@Nullable PlayerData var1) {
      return this.getItem(var1, this.rollAmount());
   }

   public abstract ItemStack getItem(@Nullable PlayerData var1, int var2);

   public abstract String getKey();
}
