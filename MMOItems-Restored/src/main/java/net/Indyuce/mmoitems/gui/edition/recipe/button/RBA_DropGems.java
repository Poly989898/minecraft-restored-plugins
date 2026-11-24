package net.Indyuce.mmoitems.gui.edition.recipe.button;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.gui.edition.recipe.button.type.RBA_BooleanButton;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RBA_DropGems extends RBA_BooleanButton {
   public static final String SMITH_GEMS = "drop-gems";
   @NotNull
   final ItemStack booleanButton = ItemFactory.of(Material.EMERALD)
      .name("§aDrop Gemstones")
      .lore(SilentNumbers.chop("Usually, gemstones that dont fit the new item are lost. Enable this to make them drop (and be recovered) instead.", 65, "§7"))
      .build();

   public RBA_DropGems(@NotNull RecipeEditorGUI recipeEditorGUI) {
      super(recipeEditorGUI);
   }

   @NotNull
   @Override
   public String getBooleanConfigPath() {
      return "drop-gems";
   }

   @NotNull
   @Override
   public ItemStack getBooleanButton() {
      return this.booleanButton;
   }
}
