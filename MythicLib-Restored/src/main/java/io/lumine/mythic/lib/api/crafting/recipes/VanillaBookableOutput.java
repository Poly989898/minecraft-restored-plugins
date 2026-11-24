package io.lumine.mythic.lib.api.crafting.recipes;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface VanillaBookableOutput {
   @NotNull
   ItemStack getBukkitRecipeResult() throws IllegalArgumentException;
}
