package net.Indyuce.mmoitems.api.recipe.workbench.ingredients;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;

@Deprecated
public class MMOItemIngredient extends WorkbenchIngredient {
   private final Type type;
   private final String id;

   public MMOItemIngredient(Type var1, String var2, int var3) {
      super(var3);
      this.type = var1;
      this.id = var2;
   }

   @Override
   public boolean corresponds(ItemStack var1) {
      NBTItem var2 = NBTItem.get(var1);
      return this.type.equals(Type.get(var2.getType())) && var2.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(this.id);
   }

   @Override
   public ItemStack generateItem() {
      return MMOItems.plugin.getItem(this.type, this.id);
   }

   @Override
   public RecipeChoice toBukkit() {
      return new ExactChoice(this.generateItem());
   }
}
