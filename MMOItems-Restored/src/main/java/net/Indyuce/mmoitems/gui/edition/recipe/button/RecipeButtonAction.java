package net.Indyuce.mmoitems.gui.edition.recipe.button;

import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class RecipeButtonAction {
   @NotNull
   final RecipeEditorGUI inv;

   @NotNull
   public RecipeEditorGUI getInv() {
      return this.inv;
   }

   public RecipeButtonAction(@NotNull RecipeEditorGUI var1) {
      this.inv = var1;
   }

   public abstract boolean runPrimary();

   public abstract void primaryProcessInput(@NotNull String var1, Object... var2);

   public abstract boolean runSecondary();

   public abstract void secondaryProcessInput(@NotNull String var1, Object... var2);

   @NotNull
   public abstract ItemStack getButton();

   public void clickSFX() {
      this.getInv().getPlayer().playSound(this.getInv().getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
   }
}
