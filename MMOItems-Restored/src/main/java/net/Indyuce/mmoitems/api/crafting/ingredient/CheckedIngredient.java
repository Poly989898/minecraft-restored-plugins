package net.Indyuce.mmoitems.api.crafting.ingredient;

import java.util.Iterator;
import java.util.List;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.PlayerIngredient;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CheckedIngredient {
   @NotNull
   private final Ingredient ingredient;
   @Nullable
   private final List<PlayerIngredient> found;
   private final boolean isHad;

   public CheckedIngredient(@NotNull Ingredient var1, @NotNull List<PlayerIngredient> var2) {
      this.ingredient = var1;
      this.found = var2;
      this.isHad = this.getTotalAmount() >= var1.getAmount();
   }

   public boolean isHad() {
      return this.isHad;
   }

   public int getTotalAmount() {
      int var1 = 0;

      for (PlayerIngredient var3 : this.found) {
         var1 += var3.getAmount();
      }

      return var1;
   }

   public void takeAway() {
      this.reduceItem(this.ingredient.getAmount());
   }

   public void reduceItem(int var1) {
      Iterator var2 = this.found.iterator();

      while (var2.hasNext() && var1 > 0) {
         ItemStack var3 = ((PlayerIngredient)var2.next()).getItem();
         if (var3.getAmount() <= 0) {
            var2.remove();
         } else {
            int var4 = Math.min(var3.getAmount(), var1);
            var1 -= var4;
            var3.setAmount(var3.getAmount() - var4);
         }
      }
   }

   @NotNull
   public Ingredient getIngredient() {
      return this.ingredient;
   }

   @Nullable
   public List<PlayerIngredient> getFound() {
      return this.found;
   }

   @NotNull
   public String format() {
      return this.ingredient.formatDisplay(this.ingredient.getDisplay().format(this.isHad));
   }
}
