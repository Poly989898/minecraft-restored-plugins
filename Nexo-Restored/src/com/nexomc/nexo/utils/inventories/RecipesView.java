package com.nexomc.nexo.utils.inventories;

import com.nexomc.libs.gui.builder.gui.ChestGuiBuilder;
import com.nexomc.libs.gui.guis.Gui;
import com.nexomc.libs.gui.guis.GuiItem;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.recipes.CustomRecipe;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¨\u0006\u000b"},
   d2 = {"Lcom/nexomc/nexo/utils/inventories/RecipesView;", "", "<init>", "()V", "create", "Lcom/nexomc/libs/gui/guis/Gui;", "page", "", "filteredRecipes", "", "Lcom/nexomc/nexo/recipes/CustomRecipe;", "com.nexomc:core"}
)
public final class RecipesView {
   @NotNull
   public final Gui create(int page, @NotNull List<CustomRecipe> filteredRecipes) {
      Gui gui2 = ((ChestGuiBuilder)((ChestGuiBuilder)Gui.gui().rows(6)).title(NexoInventory.RECIPE_SHOWCASE_TITLE.toComponent())).create();
      Gui gui = gui2;
      CustomRecipe currentRecipe = (CustomRecipe)filteredRecipes.get(page);
      boolean lastPage = filteredRecipes.size() - 1 == page;
      gui.setItem(4, 0, new GuiItem(currentRecipe.getResult()));
      int i = 0;

      for(int j = ((Collection)currentRecipe.getIngredients()).size(); i < j; ++i) {
         RecipeChoice recipeChoice = (RecipeChoice)currentRecipe.getIngredients().get(i);
         if (recipeChoice != null) {
            RecipeChoice it = recipeChoice;
            int value = 0;
            recipeChoice = !it.test(ItemStack.empty()) ? it : null;
            if (recipeChoice != null) {
               ItemStack itemStack2 = recipeChoice.getItemStack();
               if (itemStack2 != null) {
                  ItemStack itemStack = itemStack2;
                  gui.setItem(2 + i / 3, 3 + i % 3, new GuiItem(itemStack));
               }
            }
         }
      }

      ItemBuilder itemBuilder = NexoItems.itemFromId("exit_icon");
      if (itemBuilder == null) {
         itemBuilder = new ItemBuilder(Material.BARRIER);
      }

      ItemBuilder exitBuilder = itemBuilder;
      ItemStack exitItem = exitBuilder.displayName(Message.EXIT_MENU.toComponent()).build();
      gui.setItem(5, 4, new GuiItem(exitItem, RecipesView::create$lambda$1));
      if (page > 0) {
         itemBuilder = NexoItems.itemFromId("arrow_previous_icon");
         if (itemBuilder == null) {
            itemBuilder = new ItemBuilder(Material.ARROW);
         }

         ItemBuilder builder = itemBuilder;
         ItemStack guiItem = builder.setAmount(page).displayName((Component)Component.text("Open page " + page, (TextColor)NamedTextColor.YELLOW)).build();
         gui.setItem(3, 1, new GuiItem(guiItem, (a0_ -> RecipesView.create$lambda$2(this, page, filteredRecipes, (InventoryClickEvent)a0_))));
      }

      if (!lastPage) {
         itemBuilder = NexoItems.itemFromId("arrow_next_icon");
         if (itemBuilder == null) {
            itemBuilder = new ItemBuilder(Material.ARROW);
         }

         ItemBuilder builder = itemBuilder;
         ItemStack guiItem = builder.setAmount(page + 2).displayName((Component)Component.text("Open page " + (page + 2), (TextColor)NamedTextColor.YELLOW)).build();
         gui.setItem(3, 7, new GuiItem(guiItem, (a0_ -> RecipesView.create$lambda$3(this, page, filteredRecipes, (InventoryClickEvent)a0_))));
      }

      gui.setDefaultClickAction(RecipesView::create$lambda$4);
      return gui;
   }

   private static final void create$lambda$1(InventoryClickEvent it) {
      it.getWhoClicked().closeInventory();
   }

   private static final void create$lambda$2(RecipesView recipesView, int page, List filteredRecipes, InventoryClickEvent it) {
      recipesView.create(page - 1, filteredRecipes).open(it.getWhoClicked());
   }

   private static final void create$lambda$3(RecipesView recipesView, int page, List filteredRecipes, InventoryClickEvent it) {
      recipesView.create(page + 1, filteredRecipes).open(it.getWhoClicked());
   }

   private static final void create$lambda$4(InventoryClickEvent it) {
      it.setCancelled(true);
   }
}
