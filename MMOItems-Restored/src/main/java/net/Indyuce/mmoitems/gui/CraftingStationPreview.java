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

   public CraftingStationPreview(CraftingStationView craftingStationView, CheckedRecipe checkedRecipe) {
      super(craftingStationView.getPlayer());
      this.previous = craftingStationView;
      this.recipe = checkedRecipe;
   }

   @NotNull
   @Override
   public Inventory getInventory() {
      Inventory inventory = Bukkit.createInventory(
         this, 45, MythicLib.plugin.getPlaceholderParser().parse(this.getPlayer(), Message.RECIPE_PREVIEW.format(ChatColor.RESET).toString())
      );
      this.ingredients.clear();

      for (CheckedIngredient ingredient : this.recipe.getIngredients()) {
         if (ingredient.getIngredient().getAmount() > 64) {
            ItemStack item = ingredient.getIngredient().generateItemStack(this.playerData.getRPG(), true);
            item.setAmount(64);
            int amount = ingredient.getIngredient().getAmount();
            int count = SilentNumbers.floor(amount / 64.0);

            while (amount > 0) {
               if (amount > 64) {
                  this.ingredients.add(item.clone());
                  amount -= 64;
               } else {
                  item.setAmount(amount);
                  this.ingredients.add(item.clone());
                  amount -= amount;
               }
            }
         } else {
            this.ingredients.add(ingredient.getIngredient().generateItemStack(this.playerData.getRPG(), true));
         }
      }

      int count = (this.page - 1) * slots.length;
      int firstRecipeIndex = this.page * slots.length;

      for (int i = count; i < firstRecipeIndex && i < this.ingredients.size(); i++) {
         inventory.setItem(slots[i - count], this.ingredients.get(i));
      }

      for (int slot : fill) {
         inventory.setItem(slot, ConfigItems.FILL.getItem());
      }

      if (this.recipe.getRecipe() instanceof CraftingRecipe) {
         ItemStack previewItemStack = ((CraftingRecipe)this.recipe.getRecipe()).getPreviewItemStack();
         previewItemStack.setAmount(((CraftingRecipe)this.recipe.getRecipe()).getOutputAmount());
         inventory.setItem(16, previewItemStack);
      }

      if (this.recipe.getRecipe() instanceof UpgradingRecipe) {
         ItemStack preview = ((UpgradingRecipe)this.recipe.getRecipe()).getItem().getPreview();
         ItemMeta itemMeta = preview.getItemMeta();
         itemMeta.setDisplayName(preview.getItemMeta().getDisplayName() + ChatColor.GREEN + "+1!");
         preview.setItemMeta(itemMeta);
         inventory.setItem(16, preview);
      }

      inventory.setItem(10, ConfigItems.BACK.getItem());
      inventory.setItem(34, ConfigItems.CONFIRM.getItem());
      ItemStack item = this.recipe.display();
      item.setType(Material.KNOWLEDGE_BOOK);
      item.setAmount(1);
      ItemMeta itemMeta = item.getItemMeta();

      for (Enchantment key : itemMeta.getEnchants().keySet()) {
         itemMeta.removeEnchant(key);
      }

      itemMeta.setLore(itemMeta.getLore().subList(0, itemMeta.getLore().size() - 3));
      item.setItemMeta(itemMeta);
      inventory.setItem(28, item);
      inventory.setItem(20, this.page > 1 ? ConfigItems.PREVIOUS_PAGE.getItem() : ConfigItems.FILL.getItem());
      inventory.setItem(24, firstRecipeIndex < this.ingredients.size() ? ConfigItems.NEXT_PAGE.getItem() : ConfigItems.FILL.getItem());
      return inventory;
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      event.setCancelled(true);
      if (MMOUtils.isMetaItem(event.getCurrentItem(), false)) {
         NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(event.getCurrentItem());
         switch (nBTItem.getString("ItemId")) {
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
