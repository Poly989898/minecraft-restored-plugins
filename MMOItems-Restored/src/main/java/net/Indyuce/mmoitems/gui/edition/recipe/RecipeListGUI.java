package net.Indyuce.mmoitems.gui.edition.recipe;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RecipeRegistry;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RecipeListGUI extends EditionInventory {
   @NotNull
   final ItemStack nextPage = ItemFactory.of(Material.ARROW).name("§7Next Page").build();
   @NotNull
   final ItemStack prevPage = ItemFactory.of(Material.ARROW).name("§7Previous Page").build();
   @NotNull
   final ItemStack noRecipe = ItemFactory.of(Material.BLACK_STAINED_GLASS_PANE).name("§7No Recipe").build();
   @NotNull
   final RecipeRegistry recipeType;
   @NotNull
   final ItemStack listedItem;
   @NotNull
   final ArrayList<String> recipeNames = new ArrayList<>();
   boolean invalidRecipe;
   int createSlot = -1;
   @NotNull
   final HashMap<Integer, String> recipeMap = new HashMap<>();

   @NotNull
   public RecipeRegistry getRecipeRegistry() {
      return this.recipeType;
   }

   @NotNull
   public ItemStack getListedItem() {
      return this.listedItem;
   }

   @NotNull
   public ArrayList<String> getRecipeNames() {
      return this.recipeNames;
   }

   public RecipeListGUI(@NotNull Player var1, @NotNull MMOItemTemplate var2, @NotNull RecipeRegistry var3) {
      super(var1, var2);
      this.recipeType = var3;
      this.listedItem = this.getRecipeRegistry().getDisplayListItem();
      ConfigurationSection var4 = RecipeEditorGUI.getSection(this.getEditedSection(), "crafting");
      ConfigurationSection var5 = RecipeEditorGUI.getSection(var4, var3.getRecipeConfigPath());
      this.recipeNames.addAll(var5.getKeys(false));
      this.page = 0;
   }

   @Override
   public String getName() {
      return "Choose " + this.getRecipeRegistry().getRecipeTypeName() + " Recipe";
   }

   @Override
   public void arrangeInventory() {
      this.recipeMap.clear();
      this.createSlot = -1;
      if (this.page > 0) {
         this.inventory.setItem(27, this.prevPage);
      }

      if (this.recipeNames.size() >= (this.page + 1) * 21) {
         this.inventory.setItem(36, this.nextPage);
      }

      for (int var1 = 21 * this.page; var1 < 21 * (this.page + 1); var1++) {
         int var2 = page(var1);
         if (var1 == this.recipeNames.size()) {
            this.inventory
               .setItem(
                  var2,
                  RecipeEditorGUI.rename(
                     new ItemStack(Material.NETHER_STAR),
                     FFPMMOItems.get().getBodyFormat() + "Create new " + SilentNumbers.getItemName(this.getListedItem(), false)
                  )
               );
            this.createSlot = var2;
         } else if (var1 > this.recipeNames.size()) {
            this.inventory.setItem(var2, this.noRecipe);
         } else {
            this.inventory
               .setItem(
                  var2,
                  RecipeEditorGUI.rename(
                     this.getListedItem().clone(),
                     FFPMMOItems.get().getBodyFormat() + "Edit " + FFPMMOItems.get().getInputFormat() + this.recipeNames.get(var1)
                  )
               );
            this.recipeMap.put(var2, this.recipeNames.get(var1));
         }
      }
   }

   public static int page(int var0) {
      int var1 = SilentNumbers.floor(var0 / 21.0);
      var0 -= var1 * 21;
      int var2 = SilentNumbers.floor(var0 / 7.0);
      int var3 = var0 - 7 * var2;
      int var4 = 18 + var2 * 9;
      int var5 = var3 + 1;
      return var4 + var5;
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      if (VersionUtils.getView(var1).getTopInventory() == var1.getClickedInventory()) {
         var1.setCancelled(true);
         if (!this.invalidRecipe) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               if (var1.getSlot() == 27) {
                  this.page--;
                  this.refreshInventory();
               } else if (var1.getSlot() == 36) {
                  this.page++;
                  this.refreshInventory();
               } else if (var1.getSlot() == this.createSlot) {
                  String var2 = String.valueOf(this.recipeMap.size() + 1);
                  if (this.recipeMap.containsValue(var2)) {
                     var2 = var2 + "_" + UUID.randomUUID();
                  }

                  this.getRecipeRegistry().openForPlayer(this, var2);
               } else if (var1.getSlot() > 18) {
                  String var5 = this.recipeMap.get(var1.getSlot());
                  if (var5 != null) {
                     this.getRecipeRegistry().openForPlayer(this, var5);
                  }
               }
            } else if (var1.getAction() == InventoryAction.PICKUP_HALF) {
               String var6 = this.recipeMap.get(var1.getSlot());
               if (var6 != null) {
                  ConfigurationSection var3 = RecipeEditorGUI.getSection(this.getEditedSection(), "crafting");
                  ConfigurationSection var4 = RecipeEditorGUI.getSection(var3, this.getRecipeRegistry().getRecipeConfigPath());
                  this.recipeNames.remove(var6);
                  var4.set(var6, null);
                  this.registerTemplateEdition();
               }
            }
         }
      }
   }
}
