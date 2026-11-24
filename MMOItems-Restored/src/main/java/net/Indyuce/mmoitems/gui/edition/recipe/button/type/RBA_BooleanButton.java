package net.Indyuce.mmoitems.gui.edition.recipe.button.type;

import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RecipeButtonAction;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class RBA_BooleanButton extends RecipeButtonAction {
   public RBA_BooleanButton(@NotNull RecipeEditorGUI recipeEditorGUI) {
      super(recipeEditorGUI);
   }

   public boolean isEnabled() {
      return this.getInv().getNameSection().getBoolean(this.getBooleanConfigPath(), false);
   }

   @NotNull
   public abstract String getBooleanConfigPath();

   @Override
   public boolean runPrimary() {
      this.getInv().getNameSection().set(this.getBooleanConfigPath(), !this.isEnabled());
      this.clickSFX();
      this.getInv().registerTemplateEdition();
      return true;
   }

   @Override
   public void primaryProcessInput(@NotNull String text, Object... objs) {
   }

   @Override
   public boolean runSecondary() {
      this.getInv().getNameSection().set(this.getBooleanConfigPath(), null);
      this.clickSFX();
      this.getInv().registerTemplateEdition();
      return true;
   }

   @Override
   public void secondaryProcessInput(@NotNull String text, Object... objs) {
   }

   @NotNull
   public abstract ItemStack getBooleanButton();

   @NotNull
   @Override
   public ItemStack getButton() {
      String text = this.isEnabled() ? "§aTRUE" : "§cFALSE";
      return RecipeEditorGUI.addLore(
         this.getBooleanButton().clone(),
         SilentNumbers.toArrayList(
            new String[]{
               "",
               "§7Current Value: " + text,
               "",
               ChatColor.YELLOW + "►" + " Right click to reset §8(to§4 FALSE§8)§e.",
               ChatColor.YELLOW + "►" + " Left click to toggle this option."
            }
         )
      );
   }
}
