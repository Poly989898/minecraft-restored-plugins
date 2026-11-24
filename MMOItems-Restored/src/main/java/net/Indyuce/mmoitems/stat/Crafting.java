package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.recipe.RecipeTypeListGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RecipeButtonAction;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMG_RecipeInterpreter;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Crafting extends ItemStat<RandomStatData<StatData>, StatData> {
   public Crafting() {
      super(
         "CRAFTING",
         Material.CRAFTING_TABLE,
         "Crafting",
         new String[]{"The crafting recipes of your item.", "Changing a recipe requires &o/mi reload recipes&7."},
         new String[0]
      );
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new RecipeTypeListGUI(editionInventory.getPlayer(), editionInventory.getEdited()).open(editionInventory);
      } else if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("crafting")) {
         editionInventory.getEditedSection().set("crafting", null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + "Crafting recipes successfully removed. Make sure you reload active recipes using "
                  + ChatColor.RED
                  + "/mi reload recipes"
                  + ChatColor.GRAY
                  + "."
            );
      }
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<RandomStatData<StatData>> optional) {
      texts.add(ChatColor.YELLOW + "►" + " Click to access the crafting edition menu.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove all crafting recipes.");
   }

   @NotNull
   @Override
   public StatData getClearStatData() {
      return new StringData(null);
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      int count = (Integer)objs[0];
      switch (count) {
         case 0:
         case 1:
            int spaceIndex = text.indexOf(32);
            QuickNumberRange quickNumberRange = null;
            if (spaceIndex > 0) {
               String recipeId = text.substring(spaceIndex + 1);
               if (SilentNumbers.DoubleTryParse(recipeId)) {
                  recipeId = recipeId + "..";
               }

               quickNumberRange = QuickNumberRange.getFromString(recipeId);
            }

            if (spaceIndex <= 0 || quickNumberRange != null) {
               if (quickNumberRange == null) {
                  quickNumberRange = new QuickNumberRange(1.0, null);
               } else {
                  text = text.substring(0, spaceIndex);
               }

               if (text.contains(".")) {
                  String[] texts = text.split("\\.");
                  text = "m " + texts[0] + " " + texts[1] + " " + quickNumberRange;
               } else {
                  text = "v " + text + " - " + quickNumberRange;
               }
            }

            RMG_RecipeInterpreter rmG_RecipeInterpreter = (RMG_RecipeInterpreter)objs[1];
            int craftingAmount = (Integer)objs[2];
            ProvidedUIFilter uIFilter = UIFilterManager.getUIFilter(text, editionInventory.getFFP());
            if (uIFilter == null) {
               throw new IllegalArgumentException("");
            }

            if (!uIFilter.isValid(editionInventory.getFFP())) {
               throw new IllegalArgumentException("");
            }

            ConfigurationSection section = RecipeEditorGUI.getSection(editionInventory.getEditedSection(), "crafting");
            section = RecipeEditorGUI.getSection(section, ((RecipeEditorGUI)editionInventory).getRecipeRegistry().getRecipeConfigPath());
            section = RecipeEditorGUI.getSection(section, ((RecipeEditorGUI)editionInventory).getRecipeName());
            if (count == 0) {
               rmG_RecipeInterpreter.editInput(uIFilter, craftingAmount);
            } else {
               rmG_RecipeInterpreter.editOutput(uIFilter, craftingAmount);
            }

            editionInventory.registerTemplateEdition();
            break;
         case 2:
         case 3:
            if (objs.length < 2) {
               return;
            }

            if (!(objs[1] instanceof RecipeButtonAction)) {
               return;
            }

            if (count == 2) {
               ((RecipeButtonAction)objs[1]).primaryProcessInput(text, objs);
            } else {
               ((RecipeButtonAction)objs[1]).secondaryProcessInput(text, objs);
            }

            editionInventory.registerTemplateEdition();
            break;
         default:
            editionInventory.registerTemplateEdition();
      }
   }

   @Nullable
   @Override
   public RandomStatData whenInitialized(Object obj) {
      return null;
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StatData statData) {
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StatData statData) {
      return new ArrayList<>();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
   }

   @Nullable
   @Override
   public StatData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      return null;
   }
}
