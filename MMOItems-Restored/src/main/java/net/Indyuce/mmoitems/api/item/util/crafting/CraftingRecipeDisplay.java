package net.Indyuce.mmoitems.api.item.util.crafting;

import io.lumine.mythic.lib.MythicLib;
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
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.item.util.ConfigItem;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CraftingRecipeDisplay extends ConfigItem {
   public CraftingRecipeDisplay() {
      super(
         "CRAFTING_RECIPE_DISPLAY",
         Material.BARRIER,
         "&a&lCraft&f #name#",
         "{conditions}",
         "{conditions}&8Conditions:",
         "{crafting_time}",
         "{crafting_time}&7Crafting Time: &c#crafting-time#&7s",
         "",
         "&8Ingredients:",
         "#ingredients#",
         "",
         "&eLeft-Click to craft!",
         "&eRight-Click to preview!"
      );
   }

   public CraftingRecipeDisplay.ItemBuilder newBuilder(CheckedRecipe var1) {
      return new CraftingRecipeDisplay.ItemBuilder(var1);
   }

   public class ItemBuilder {
      private final CheckedRecipe recipe;
      private final CraftingRecipe craftingRecipe;
      private final String name = CraftingRecipeDisplay.this.getName();
      private final List<String> lore = new ArrayList<>(CraftingRecipeDisplay.this.getLore());

      public ItemBuilder(CheckedRecipe nullx) {
         this.recipe = nullx;
         this.craftingRecipe = (CraftingRecipe)nullx.getRecipe();
      }

      public ItemStack build() {
         HashMap<String, String> var1 = new HashMap<>();
         int var2 = -1;
         ListIterator var3 = this.lore.listIterator();

         while (var3.hasNext()) {
            int var4 = var3.nextIndex();
            String var5 = (String)var3.next();
            if (var5.startsWith("{crafting_time}")) {
               if (this.craftingRecipe.getCraftingTime() <= 0.0) {
                  var3.remove();
                  continue;
               }

               var1.put(
                  var5,
                  var5.replace("{crafting_time}", "")
                     .replace("#crafting-time#", MythicLib.plugin.getMMOConfig().decimal.format(this.craftingRecipe.getCraftingTime()))
               );
            }

            if (var5.startsWith("{conditions}")) {
               var2 = var4 + 1;
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

         ItemStack var11 = this.craftingRecipe.getPreviewItemStack();
         int var13 = this.craftingRecipe.getOutputAmount();
         if (var13 > 64) {
            this.lore.add(0, Message.STATION_BIG_STACK.format(ChatColor.GOLD, "#size#", String.valueOf(var13)).toString());
         } else {
            var11.setAmount(var13);
         }

         ItemMeta var14 = var11.getItemMeta();
         var14.addItemFlags(ItemFlag.values());
         AdventureUtils.setDisplayName(
            var14, this.name.replace("#name#", (var13 > 1 ? ChatColor.WHITE + "" + var13 + " x " : "") + MMOUtils.getDisplayName(var11))
         );
         AdventureUtils.setLore(var14, this.lore);
         var11.setItemMeta(var14);
         return NBTItem.get(var11).addTag(new ItemTag[]{new ItemTag("recipeId", this.craftingRecipe.getId())}).toItem();
      }
   }
}
