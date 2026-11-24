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

public class RBA_SmithingEnchantments extends RBA_ChooseableButton {
   @NotNull
   final ItemStack chooseableButton = ItemFactory.of(Material.ENCHANTING_TABLE)
      .name("§aEnchantment Transfer")
      .lore(
         SilentNumbers.chop("What will happen to the enchantments of the ingredients? Will enchanted ingredients produce an enchanted output item?", 65, "§7")
      )
      .build();
   public static final String SMITH_ENCHANTS = "enchantments";

   public RBA_SmithingEnchantments(@NotNull RecipeEditorGUI var1) {
      super(var1);
   }

   @NotNull
   @Override
   public ItemStack getChooseableButton() {
      return this.chooseableButton;
   }

   @NotNull
   @Override
   public String getChooseableConfigPath() {
      return "enchantments";
   }

   @NotNull
   @Override
   public ArrayList<String> getChooseableList() {
      return RBA_SmithingUpgrades.getSmithingList();
   }

   @NotNull
   @Override
   public String getDefaultValue() {
      return SmithingCombinationType.MAXIMUM.toString();
   }

   @NotNull
   @Override
   public String getChooseableDefinition(@NotNull String var1) {
      SmithingCombinationType var2 = SmithingCombinationType.MAXIMUM;

      try {
         var2 = SmithingCombinationType.valueOf(this.getCurrentChooseableValue());
      } catch (IllegalArgumentException var4) {
      }

      switch (var2) {
         case EVEN:
            return "For each enchantment, will take the average of that enchantment's level across the ingredients.";
         case NONE:
            return "Will ignore the enchantments of any ingredients.";
         case MAXIMUM:
            return "Output will have the best enchantment from each ingredient";
         case MINIMUM:
            return "Output will have worst enchantment from each ingredient with that enchantment.";
         case ADDITIVE:
            return "The enchantments of all ingredients will add together.";
         default:
            return "Unknown behaviour. Add description in net.Indyuce.mmoitems.gui.edition.recipe.rba.RBA_SmithingEnchantments";
      }
   }
}
