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

   public RecipeListGUI(@NotNull Player player, @NotNull MMOItemTemplate mmoItemTemplate, @NotNull RecipeRegistry recipeRegistry) {
      super(player, mmoItemTemplate);
      this.recipeType = recipeRegistry;
      this.listedItem = this.getRecipeRegistry().getDisplayListItem();
      ConfigurationSection section = RecipeEditorGUI.getSection(this.getEditedSection(), "crafting");
      ConfigurationSection recipesSection = RecipeEditorGUI.getSection(section, recipeRegistry.getRecipeConfigPath());
      this.recipeNames.addAll(recipesSection.getKeys(false));
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

      for (int i = 21 * this.page; i < 21 * (this.page + 1); i++) {
         int count = page(i);
         if (i == this.recipeNames.size()) {
            this.inventory
               .setItem(
                  count,
                  RecipeEditorGUI.rename(
                     new ItemStack(Material.NETHER_STAR),
                     FFPMMOItems.get().getBodyFormat() + "Create new " + SilentNumbers.getItemName(this.getListedItem(), false)
                  )
               );
            this.createSlot = count;
         } else if (i > this.recipeNames.size()) {
            this.inventory.setItem(count, this.noRecipe);
         } else {
            this.inventory
               .setItem(
                  count,
                  RecipeEditorGUI.rename(
                     this.getListedItem().clone(),
                     FFPMMOItems.get().getBodyFormat() + "Edit " + FFPMMOItems.get().getInputFormat() + this.recipeNames.get(i)
                  )
               );
            this.recipeMap.put(count, this.recipeNames.get(i));
         }
      }
   }

   public static int page(int count) {
      int pageIndex = SilentNumbers.floor(count / 21.0);
      count -= pageIndex * 21;
      int rowIndex = SilentNumbers.floor(count / 7.0);
      int columnIndex = count - 7 * rowIndex;
      int baseSlot = 18 + rowIndex * 9;
      int columnOffset = columnIndex + 1;
      return baseSlot + columnOffset;
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      if (VersionUtils.getView(event).getTopInventory() == event.getClickedInventory()) {
         event.setCancelled(true);
         if (!this.invalidRecipe) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               if (event.getSlot() == 27) {
                  this.page--;
                  this.refreshInventory();
               } else if (event.getSlot() == 36) {
                  this.page++;
                  this.refreshInventory();
               } else if (event.getSlot() == this.createSlot) {
                  String text = String.valueOf(this.recipeMap.size() + 1);
                  if (this.recipeMap.containsValue(text)) {
                     text = text + "_" + UUID.randomUUID();
                  }

                  this.getRecipeRegistry().openForPlayer(this, text);
               } else if (event.getSlot() > 18) {
                  String text = this.recipeMap.get(event.getSlot());
                  if (text != null) {
                     this.getRecipeRegistry().openForPlayer(this, text);
                  }
               }
            } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
               String text = this.recipeMap.get(event.getSlot());
               if (text != null) {
                  ConfigurationSection section = RecipeEditorGUI.getSection(this.getEditedSection(), "crafting");
                  ConfigurationSection recipesSection = RecipeEditorGUI.getSection(section, this.getRecipeRegistry().getRecipeConfigPath());
                  this.recipeNames.remove(text);
                  recipesSection.set(text, null);
                  this.registerTemplateEdition();
               }
            }
         }
      }
   }
}
