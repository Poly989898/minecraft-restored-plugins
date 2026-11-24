package net.Indyuce.mmoitems.api.item.util.crafting;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.util.AdventureUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.Indyuce.mmoitems.api.crafting.condition.CheckedCondition;
import net.Indyuce.mmoitems.api.crafting.recipe.CheckedRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.UpgradingRecipe;
import net.Indyuce.mmoitems.api.item.util.ConfigItem;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UpgradingRecipeDisplay extends ConfigItem {
   public UpgradingRecipeDisplay() {
      super(
         "UPGRADING_RECIPE_DISPLAY",
         Material.BARRIER,
         "&e&lUpgrade&f #name#",
         "{conditions}",
         "{conditions}&8Conditions:",
         "",
         "&8Ingredients:",
         "#ingredients#",
         "",
         "&eLeft-Click to craft!",
         "&eRight-Click to preview!"
      );
   }

   public UpgradingRecipeDisplay.ItemBuilder newBuilder(CheckedRecipe var1) {
      return new UpgradingRecipeDisplay.ItemBuilder(var1);
   }

   public class ItemBuilder {
      private final CheckedRecipe recipe;
      private final UpgradingRecipe upgradingRecipe;
      private final String name = UpgradingRecipeDisplay.this.getName();
      private final List<String> lore = new ArrayList<>(UpgradingRecipeDisplay.this.getLore());

      public ItemBuilder(CheckedRecipe nullx) {
         this.recipe = nullx;
         this.upgradingRecipe = (UpgradingRecipe)nullx.getRecipe();
      }

      public ItemStack build() {
         HashMap<String, String> var1 = new HashMap<>();
         int var2 = -1;
         ListIterator var3 = this.lore.listIterator();

         while (var3.hasNext()) {
            int var4 = var3.nextIndex();
            String var5 = (String)var3.next();
            if (var5.startsWith("{conditions}")) {
               var2 = var4;
               if (this.recipe.getConditions().size() == 0) {
                  var3.remove();
               } else {
                  var1.put(var5, var5.replace("{conditions}", ""));
               }
            }
         }

         for (String var9 : var1.keySet()) {
            this.lore.set(this.lore.indexOf(var9), (String)var1.get(var9));
         }

         int var8 = this.lore.indexOf("#ingredients#");
         this.lore.remove(var8);
         this.recipe.getIngredients().forEach(var2x -> this.lore.add(var8, var2x.format()));
         if (var2 >= 0) {
            for (CheckedCondition var12 : this.recipe.getConditions()) {
               ConditionalDisplay var6 = var12.getCondition().getDisplay();
               if (var6 != null) {
                  this.lore.add(var2++, var12.format());
               }
            }
         }

         ItemStack var11 = this.upgradingRecipe.getItem().getPreview();
         ItemMeta var13 = var11.getItemMeta();
         AdventureUtils.setDisplayName(var13, this.name.replace("#name#", MMOUtils.getDisplayName(var11)));
         AdventureUtils.setLore(var13, this.lore);
         var13.addItemFlags(ItemFlag.values());
         var11.setItemMeta(var13);
         return NBTItem.get(var11).addTag(new ItemTag[]{new ItemTag("recipeId", this.recipe.getRecipe().getId())}).toItem();
      }
   }
}
