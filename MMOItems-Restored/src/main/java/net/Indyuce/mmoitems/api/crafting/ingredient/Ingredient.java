package net.Indyuce.mmoitems.api.crafting.ingredient;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.IngredientInventory;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.PlayerIngredient;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Ingredient<C extends PlayerIngredient> {
   private final String id;
   private int amount;

   public Ingredient(String var1, MMOLineConfig var2) {
      this(var1, var2.getInt("amount", 1));
   }

   public Ingredient(String var1, int var2) {
      this.id = var1;
      this.amount = var2;
   }

   public String getId() {
      return this.id;
   }

   public void setAmount(int var1) {
      this.amount = var1;
   }

   public int getAmount() {
      return this.amount;
   }

   public ConditionalDisplay getDisplay() {
      return MMOItems.plugin.getCrafting().getIngredients().stream().filter(var1 -> var1.getId().equals(this.id)).findAny().orElseThrow().getDisplay();
   }

   @Deprecated
   public abstract String getKey();

   public abstract String formatDisplay(String var1);

   public abstract boolean matches(C var1);

   @NotNull
   public abstract ItemStack generateItemStack(@NotNull RPGPlayer var1, boolean var2);

   public CheckedIngredient evaluateIngredient(@NotNull IngredientInventory var1) {
      return var1.findMatching(this);
   }
}
