package net.Indyuce.mmoitems.gui.edition.recipe.interpreter;

import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RMG_RecipeInterpreter {
   void editInput(@NotNull ProvidedUIFilter providedUIFilter, int count);

   void editOutput(@NotNull ProvidedUIFilter providedUIFilter, int count);

   void deleteInput(int count);

   void deleteOutput(int count);

   @Nullable
   ProvidedUIFilter getInput(int count);

   @Nullable
   ProvidedUIFilter getOutput(int count);
}
