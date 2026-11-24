package net.Indyuce.mmoitems.gui.edition.recipe.button;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.gui.edition.recipe.button.type.RBA_DoubleButton;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RBA_CookingTime extends RBA_DoubleButton {
   public static final String FURNACE_TIME = "time";
   public static final double DEFAULT = 200.0;
   @NotNull
   final ItemStack doubleButton = ItemFactory.of(Material.CLOCK)
      .name("§cDuration")
      .lore(SilentNumbers.chop("How long it takes this recipe to finish cooking", 65, "§7"))
      .build();

   public RBA_CookingTime(@NotNull RecipeEditorGUI var1) {
      super(var1);
   }

   @NotNull
   @Override
   public String getDoubleConfigPath() {
      return "time";
   }

   @Nullable
   @Override
   public QuickNumberRange getRange() {
      return new QuickNumberRange(0.0, null);
   }

   @Override
   public boolean requireInteger() {
      return true;
   }

   @Override
   public double getDefaultValue() {
      return 200.0;
   }

   @NotNull
   @Override
   public ItemStack getDoubleButton() {
      return this.doubleButton;
   }
}
