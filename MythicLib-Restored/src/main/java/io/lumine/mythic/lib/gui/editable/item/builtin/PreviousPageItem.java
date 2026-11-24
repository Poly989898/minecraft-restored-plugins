package io.lumine.mythic.lib.gui.editable.item.builtin;

import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.ItemOptions;
import io.lumine.mythic.lib.gui.editable.item.PhysicalItem;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.util.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/** Goes to the previous page of a paginated GUI. Shows the optional "no_page" item (or nothing with "hide_if_no_page") when there is none. */
public class PreviousPageItem<T extends GeneratedInventory> extends ClickSoundItem<T> {
   private final boolean hideIfNoPage;
   private final PhysicalItem<T> noPage;

   public PreviousPageItem(ConfigurationSection config) {
      super(config);
      this.hideIfNoPage = config.getBoolean("hide_if_no_page", false);
      this.noPage = config.contains("no_page") ? new SimpleItem<>(this, config.getConfigurationSection("no_page")) : null;
   }

   @Override
   public boolean isDisplayed(T inv) {
      Validate.isTrue(inv.hasPagination(), "Pagination disabled");
      return !this.hideIfNoPage || this.isWithinBounds(inv);
   }

   private boolean isWithinBounds(T inv) {
      return inv.page > 1;
   }

   @Override
   public ItemStack getDisplayedItem(T inv, ItemOptions options) {
      return this.noPage != null && !this.isWithinBounds(inv) ? this.noPage.getDisplayedItem(inv, options) : super.getDisplayedItem(inv, options);
   }

   @Override
   public void onClick(T inv, InventoryClickEvent event) {
      if (!this.isWithinBounds(inv)) {
         return;
      }

      inv.page--;
      this.playClickSound(inv);
      inv.open();
   }
}
