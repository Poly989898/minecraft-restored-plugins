package io.lumine.mythic.lib.api.crafting.recipes;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public interface VanillaBookableRecipe {
   @NotNull
   Recipe getBukkitRecipe(@NotNull NamespacedKey namespacedKey, @NotNull ItemStack item) throws IllegalArgumentException;
}
