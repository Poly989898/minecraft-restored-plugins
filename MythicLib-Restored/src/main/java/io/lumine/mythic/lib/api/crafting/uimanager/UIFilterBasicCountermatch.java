package io.lumine.mythic.lib.api.crafting.uimanager;

import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UIFilterBasicCountermatch implements UIFilterCountermatch {
   private boolean loading = true;
   private boolean evaluated = false;

   public void load() {
      this.loading = false;
   }

   public boolean wasEvaluated() {
      return this.evaluated;
   }

   @Override
   public boolean preventsMatching(@NotNull ItemStack item, @Nullable FriendlyFeedbackProvider ffp) {
      this.evaluated = true;
      return this.loading;
   }
}
