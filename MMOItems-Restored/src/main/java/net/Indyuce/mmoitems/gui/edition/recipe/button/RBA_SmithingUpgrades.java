package net.Indyuce.mmoitems.gui.edition.recipe.button;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import net.Indyuce.mmoitems.api.crafting.recipe.SmithingCombinationType;
import net.Indyuce.mmoitems.gui.edition.recipe.button.type.RBA_ChooseableButton;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RBA_SmithingUpgrades extends RBA_ChooseableButton {
   @NotNull
   final ItemStack chooseableButton = ItemFactory.of(Material.ANVIL)
      .name("§aUpgrades Transfer")
      .lore(SilentNumbers.chop("What will happen to the upgrades of the ingredients? Will upgraded ingredients produce an upgraded output item?", 65, "§7"))
      .build();
   public static final String SMITH_UPGRADES = "upgrades";
   static ArrayList<String> smithingList;

   public RBA_SmithingUpgrades(@NotNull RecipeEditorGUI recipeEditorGUI) {
      super(recipeEditorGUI);
   }

   @NotNull
   @Override
   public ItemStack getChooseableButton() {
      return this.chooseableButton;
   }

   @NotNull
   @Override
   public String getChooseableConfigPath() {
      return "upgrades";
   }

   @NotNull
   @Override
   public ArrayList<String> getChooseableList() {
      return getSmithingList();
   }

   @NotNull
   @Override
   public String getDefaultValue() {
      return SmithingCombinationType.MAXIMUM.toString();
   }

   @NotNull
   @Override
   public String getChooseableDefinition(@NotNull String text) {
      SmithingCombinationType smithingCombinationType = SmithingCombinationType.MAXIMUM;

      try {
         smithingCombinationType = SmithingCombinationType.valueOf(this.getCurrentChooseableValue());
      } catch (IllegalArgumentException exception) {
      }

      switch (smithingCombinationType) {
         case EVEN:
            return "Will take the average of the upgrade levels of the combined items.";
         case NONE:
            return "Will ignore the upgrade levels of any ingredients.";
         case MAXIMUM:
            return "Output will have the upgrade level of the most upgraded ingredient.";
         case MINIMUM:
            return "Output will have the upgrade level of the least-upgraded upgradeable ingredient.";
         case ADDITIVE:
            return "The upgrade levels of the ingredients will be added, and the result will be the crafted item's level.";
         default:
            return "Unknown behaviour. Add description in net.Indyuce.mmoitems.gui.edition.recipe.rba.RBA_SmithingUpgrades";
      }
   }

   @NotNull
   static ArrayList<String> getSmithingList() {
      if (smithingList != null) {
         return smithingList;
      }

      smithingList = new ArrayList<>();

      for (SmithingCombinationType smithingCombinationType : SmithingCombinationType.values()) {
         smithingList.add(smithingCombinationType.toString());
      }

      return smithingList;
   }
}
