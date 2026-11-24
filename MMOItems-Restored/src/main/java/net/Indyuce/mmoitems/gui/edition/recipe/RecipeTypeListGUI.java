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

   public RecipeTypeListGUI(@NotNull Player var1, @NotNull MMOItemTemplate var2) {
      super(var1, var2);
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

      HashMap var1 = new HashMap();
      int var2 = 0;

      for (RecipeRegistry var4 : registeredRecipes.values()) {
         var1.put(var2, var4);
         var2++;
      }

      for (int var6 = 21 * this.page; var6 < 21 * (this.page + 1); var6++) {
         int var7 = page(var6);
         if (var6 >= registeredRecipes.size()) {
            this.inventory.setItem(var7, NO_RECIPE);
         } else {
            RecipeRegistry var5 = (RecipeRegistry)var1.get(var6);
            this.inventory.setItem(var7, var5.getDisplayListItem());
            this.recipeTypeMap.put(var7, var5);
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
         if (var1.getAction() == InventoryAction.PICKUP_ALL) {
            if (var1.getSlot() == 27) {
               this.page--;
               this.refreshInventory();
            } else if (var1.getSlot() == 36) {
               this.page++;
               this.refreshInventory();
            } else if (var1.getSlot() > 18) {
               RecipeRegistry var2 = this.recipeTypeMap.get(var1.getSlot());
               if (var2 != null) {
                  new RecipeListGUI(this.player, this.template, var2).open(this);
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

   public static void registerRecipe(@NotNull RecipeRegistry var0) {
      registeredRecipes.put(var0.getRecipeConfigPath(), var0);
   }

   @NotNull
   public static Set<String> getRegisteredRecipes() {
      return registeredRecipes.keySet();
   }

   @NotNull
   public static RecipeRegistry getRegisteredRecipe(@NotNull String var0) {
      return registeredRecipes.get(var0);
   }
}
