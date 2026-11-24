package net.Indyuce.mmoitems.api.crafting;

import io.lumine.mythic.lib.api.crafting.uifilters.VanillaUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterCountermatch;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanillaMMOItemCountermatch implements UIFilterCountermatch {
   public static void enable() {
      VanillaUIFilter.get().addMatchOverride(new VanillaMMOItemCountermatch());
   }

   public boolean preventsMatching(@NotNull ItemStack item, @Nullable FriendlyFeedbackProvider friendlyFeedbackProvider) {
      return NBTItem.get(item).hasType();
   }
}
