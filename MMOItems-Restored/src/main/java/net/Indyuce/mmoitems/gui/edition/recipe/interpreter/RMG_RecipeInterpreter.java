package net.Indyuce.mmoitems.gui.edition.recipe.interpreter;

import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RMG_RecipeInterpreter {
   void editInput(@NotNull ProvidedUIFilter var1, int var2);

   void editOutput(@NotNull ProvidedUIFilter var1, int var2);

   void deleteInput(int var1);

   void deleteOutput(int var1);

   @Nullable
   ProvidedUIFilter getInput(int var1);

   @Nullable
   ProvidedUIFilter getOutput(int var1);
}
