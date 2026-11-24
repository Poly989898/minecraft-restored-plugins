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
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new RecipeTypeListGUI(var1.getPlayer(), var1.getEdited()).open(var1);
      } else if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("crafting")) {
         var1.getEditedSection().set("crafting", null);
         var1.registerTemplateEdition();
         var1.getPlayer()
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
   public void whenDisplayed(List<String> var1, Optional<RandomStatData<StatData>> var2) {
      var1.add(ChatColor.YELLOW + "►" + " Click to access the crafting edition menu.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove all crafting recipes.");
   }

   @NotNull
   @Override
   public StatData getClearStatData() {
      return new StringData(null);
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      int var4 = (Integer)var3[0];
      switch (var4) {
         case 0:
         case 1:
            int var5 = var2.indexOf(32);
            QuickNumberRange var6 = null;
            if (var5 > 0) {
               String var7 = var2.substring(var5 + 1);
               if (SilentNumbers.DoubleTryParse(var7)) {
                  var7 = var7 + "..";
               }

               var6 = QuickNumberRange.getFromString(var7);
            }

            if (var5 <= 0 || var6 != null) {
               if (var6 == null) {
                  var6 = new QuickNumberRange(1.0, null);
               } else {
                  var2 = var2.substring(0, var5);
               }

               if (var2.contains(".")) {
                  String[] var11 = var2.split("\\.");
                  var2 = "m " + var11[0] + " " + var11[1] + " " + var6;
               } else {
                  var2 = "v " + var2 + " - " + var6;
               }
            }

            RMG_RecipeInterpreter var12 = (RMG_RecipeInterpreter)var3[1];
            int var8 = (Integer)var3[2];
            ProvidedUIFilter var9 = UIFilterManager.getUIFilter(var2, var1.getFFP());
            if (var9 == null) {
               throw new IllegalArgumentException("");
            }

            if (!var9.isValid(var1.getFFP())) {
               throw new IllegalArgumentException("");
            }

            ConfigurationSection var10 = RecipeEditorGUI.getSection(var1.getEditedSection(), "crafting");
            var10 = RecipeEditorGUI.getSection(var10, ((RecipeEditorGUI)var1).getRecipeRegistry().getRecipeConfigPath());
            var10 = RecipeEditorGUI.getSection(var10, ((RecipeEditorGUI)var1).getRecipeName());
            if (var4 == 0) {
               var12.editInput(var9, var8);
            } else {
               var12.editOutput(var9, var8);
            }

            var1.registerTemplateEdition();
            break;
         case 2:
         case 3:
            if (var3.length < 2) {
               return;
            }

            if (!(var3[1] instanceof RecipeButtonAction)) {
               return;
            }

            if (var4 == 2) {
               ((RecipeButtonAction)var3[1]).primaryProcessInput(var2, var3);
            } else {
               ((RecipeButtonAction)var3[1]).secondaryProcessInput(var2, var3);
            }

            var1.registerTemplateEdition();
            break;
         default:
            var1.registerTemplateEdition();
      }
   }

   @Nullable
   @Override
   public RandomStatData whenInitialized(Object var1) {
      return null;
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StatData var2) {
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StatData var1) {
      return new ArrayList<>();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
   }

   @Nullable
   @Override
   public StatData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      return null;
   }
}
