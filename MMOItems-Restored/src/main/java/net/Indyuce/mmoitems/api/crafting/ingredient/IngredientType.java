package net.Indyuce.mmoitems.api.crafting.ingredient;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.function.Function;
import java.util.function.Predicate;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.Indyuce.mmoitems.api.crafting.LoadedCraftingObject;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.PlayerIngredient;

public class IngredientType extends LoadedCraftingObject<Ingredient> {
   private final Predicate<NBTItem> check;
   private final Function<NBTItem, PlayerIngredient> readIngredient;

   public IngredientType(
      String var1, Function<MMOLineConfig, Ingredient> var2, ConditionalDisplay var3, Predicate<NBTItem> var4, Function<NBTItem, PlayerIngredient> var5
   ) {
      super(var1, var2, var3);
      this.check = var4;
      this.readIngredient = var5;
   }

   public boolean check(NBTItem var1) {
      return this.check.test(var1);
   }

   public PlayerIngredient readPlayerIngredient(NBTItem var1) {
      return this.readIngredient.apply(var1);
   }
}
