package net.Indyuce.mmoitems.gui.edition.recipe;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.HashMap;
import java.util.Set;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RMGRR_LegacyBurning;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RMGRR_MegaShaped;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RMGRR_Shaped;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RMGRR_Shapeless;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RMGRR_Smithing;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RMGRR_SuperShaped;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RecipeRegistry;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.burninglegacy.CraftingType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RecipeTypeListGUI extends EditionInventory {
   @NotNull
   private static final ItemStack NEXT_PAGE = ItemFactory.of(Material.ARROW).name(FFPMMOItems.get().getExampleFormat() + "Next Page").build();
   @NotNull
   private static final ItemStack PREVIOUS_PAGE = ItemFactory.of(Material.ARROW).name(FFPMMOItems.get().getExampleFormat() + "Previous Page").build();
   @NotNull
   private static final ItemStack NO_RECIPE = ItemFactory.of(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name("").build();
   @NotNull
   final HashMap<Integer, RecipeRegistry> recipeTypeMap = new HashMap<>();
   @NotNull
   static final HashMap<String, RecipeRegistry> registeredRecipes = new HashMap<>();

   public RecipeTypeListGUI(@NotNull Player player, @NotNull MMOItemTemplate mmoItemTemplate) {
      super(player, mmoItemTemplate);
      this.page = 0;
   }

   @Override
   public String getName() {
      return "Choose Recipe Type";
   }

   @Override
   public void arrangeInventory() {
      this.recipeTypeMap.clear();
      if (this.page > 0) {
         this.inventory.setItem(27, PREVIOUS_PAGE);
      }

      if (registeredRecipes.size() >= (this.page + 1) * 21) {
         this.inventory.setItem(36, NEXT_PAGE);
      }

      HashMap map = new HashMap();
      int count = 0;

      for (RecipeRegistry recipeRegistry : registeredRecipes.values()) {
         map.put(count, recipeRegistry);
         count++;
      }

      for (int i = 21 * this.page; i < 21 * (this.page + 1); i++) {
         int count2 = page(i);
         if (i >= registeredRecipes.size()) {
            this.inventory.setItem(count2, NO_RECIPE);
         } else {
            RecipeRegistry recipeRegistry = (RecipeRegistry)map.get(i);
            this.inventory.setItem(count2, recipeRegistry.getDisplayListItem());
            this.recipeTypeMap.put(count2, recipeRegistry);
         }
      }
   }

   public static int page(int count) {
      int count2 = SilentNumbers.floor(count / 21.0);
      count -= count2 * 21;
      int count3 = SilentNumbers.floor(count / 7.0);
      int count4 = count - 7 * count3;
      int count5 = 18 + count3 * 9;
      int count6 = count4 + 1;
      return count5 + count6;
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      if (VersionUtils.getView(event).getTopInventory() == event.getClickedInventory()) {
         event.setCancelled(true);
         if (event.getAction() == InventoryAction.PICKUP_ALL) {
            if (event.getSlot() == 27) {
               this.page--;
               this.refreshInventory();
            } else if (event.getSlot() == 36) {
               this.page++;
               this.refreshInventory();
            } else if (event.getSlot() > 18) {
               RecipeRegistry recipeRegistry = this.recipeTypeMap.get(event.getSlot());
               if (recipeRegistry != null) {
                  new RecipeListGUI(this.player, this.template, recipeRegistry).open(this);
               }
            }
         }
      }
   }

   public static void registerNativeRecipes() {
      registerRecipe(new RMGRR_Smithing());
      registerRecipe(new RMGRR_Shapeless());
      registerRecipe(new RMGRR_Shaped());
      registerRecipe(new RMGRR_SuperShaped());
      registerRecipe(new RMGRR_MegaShaped());
      registerRecipe(new RMGRR_LegacyBurning(CraftingType.FURNACE));
      registerRecipe(new RMGRR_LegacyBurning(CraftingType.BLAST));
      registerRecipe(new RMGRR_LegacyBurning(CraftingType.SMOKER));
      registerRecipe(new RMGRR_LegacyBurning(CraftingType.CAMPFIRE));
   }

   public static void registerRecipe(@NotNull RecipeRegistry recipeRegistry) {
      registeredRecipes.put(recipeRegistry.getRecipeConfigPath(), recipeRegistry);
   }

   @NotNull
   public static Set<String> getRegisteredRecipes() {
      return registeredRecipes.keySet();
   }

   @NotNull
   public static RecipeRegistry getRegisteredRecipe(@NotNull String text) {
      return registeredRecipes.get(text);
   }
}
