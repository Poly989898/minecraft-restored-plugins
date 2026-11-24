package net.Indyuce.mmoitems.gui;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.api.crafting.ingredient.CheckedIngredient;
import net.Indyuce.mmoitems.api.crafting.recipe.CheckedRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.UpgradingRecipe;
import net.Indyuce.mmoitems.api.item.util.ConfigItems;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class CraftingStationPreview extends PluginInventory {
   private final CraftingStationView previous;
   private final CheckedRecipe recipe;
   private final List<ItemStack> ingredients = new ArrayList<>();
   private static final int[] slots = new int[]{12, 13, 14, 21, 22, 23, 30, 31, 32};
   private static final int[] fill = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17, 18, 19, 25, 26, 27, 29, 33, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

   public CraftingStationPreview(CraftingStationView var1, CheckedRecipe var2) {
      super(var1.getPlayer());
      this.previous = var1;
      this.recipe = var2;
   }

   @NotNull
   @Override
   public Inventory getInventory() {
      Inventory var1 = Bukkit.createInventory(
         this, 45, MythicLib.plugin.getPlaceholderParser().parse(this.getPlayer(), Message.RECIPE_PREVIEW.format(ChatColor.RESET).toString())
      );
      this.ingredients.clear();

      for (CheckedIngredient var3 : this.recipe.getIngredients()) {
         if (var3.getIngredient().getAmount() > 64) {
            ItemStack var4 = var3.getIngredient().generateItemStack(this.playerData.getRPG(), true);
            var4.setAmount(64);
            int var5 = var3.getIngredient().getAmount();
            int var6 = SilentNumbers.floor(var5 / 64.0);

            while (var5 > 0) {
               if (var5 > 64) {
                  this.ingredients.add(var4.clone());
                  var5 -= 64;
               } else {
                  var4.setAmount(var5);
                  this.ingredients.add(var4.clone());
                  var5 -= var5;
               }
            }
         } else {
            this.ingredients.add(var3.getIngredient().generateItemStack(this.playerData.getRPG(), true));
         }
      }

      int var8 = (this.page - 1) * slots.length;
      int var9 = this.page * slots.length;

      for (int var10 = var8; var10 < var9 && var10 < this.ingredients.size(); var10++) {
         var1.setItem(slots[var10 - var8], this.ingredients.get(var10));
      }

      for (int var7 : fill) {
         var1.setItem(var7, ConfigItems.FILL.getItem());
      }

      if (this.recipe.getRecipe() instanceof CraftingRecipe) {
         ItemStack var12 = ((CraftingRecipe)this.recipe.getRecipe()).getPreviewItemStack();
         var12.setAmount(((CraftingRecipe)this.recipe.getRecipe()).getOutputAmount());
         var1.setItem(16, var12);
      }

      if (this.recipe.getRecipe() instanceof UpgradingRecipe) {
         ItemStack var13 = ((UpgradingRecipe)this.recipe.getRecipe()).getItem().getPreview();
         ItemMeta var16 = var13.getItemMeta();
         var16.setDisplayName(var13.getItemMeta().getDisplayName() + ChatColor.GREEN + "+1!");
         var13.setItemMeta(var16);
         var1.setItem(16, var13);
      }

      var1.setItem(10, ConfigItems.BACK.getItem());
      var1.setItem(34, ConfigItems.CONFIRM.getItem());
      ItemStack var14 = this.recipe.display();
      var14.setType(Material.KNOWLEDGE_BOOK);
      var14.setAmount(1);
      ItemMeta var17 = var14.getItemMeta();

      for (Enchantment var20 : var17.getEnchants().keySet()) {
         var17.removeEnchant(var20);
      }

      var17.setLore(var17.getLore().subList(0, var17.getLore().size() - 3));
      var14.setItemMeta(var17);
      var1.setItem(28, var14);
      var1.setItem(20, this.page > 1 ? ConfigItems.PREVIOUS_PAGE.getItem() : ConfigItems.FILL.getItem());
      var1.setItem(24, var9 < this.ingredients.size() ? ConfigItems.NEXT_PAGE.getItem() : ConfigItems.FILL.getItem());
      return var1;
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      var1.setCancelled(true);
      if (MMOUtils.isMetaItem(var1.getCurrentItem(), false)) {
         NBTItem var2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getCurrentItem());
         switch (var2.getString("ItemId")) {
            case "CONFIRM":
               this.previous.processRecipe(this.recipe);
               this.previous.open();
               return;
            case "PREVIOUS_PAGE":
               this.page--;
               this.open();
               return;
            case "NEXT_PAGE":
               this.page++;
               this.open();
               return;
            case "BACK":
               this.previous.open();
               return;
         }
      }
   }
}
