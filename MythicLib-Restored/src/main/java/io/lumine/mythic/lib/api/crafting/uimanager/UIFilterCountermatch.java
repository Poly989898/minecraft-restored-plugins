package io.lumine.mythic.lib.api.crafting.uimanager;

import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UIFilterCountermatch {
   boolean preventsMatching(@NotNull ItemStack item, @Nullable FriendlyFeedbackProvider friendlyFeedbackProvider);
}
