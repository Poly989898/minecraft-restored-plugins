package io.lumine.mythic.lib.gui.editable.item.builtin;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.PhysicalItem;
import io.lumine.mythic.lib.gui.editable.placeholder.EmptyPlaceholders;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import io.lumine.mythic.lib.version.Sounds;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

/** Common base of the navigation buttons, which may play a "click_sound". */
abstract class ClickSoundItem<T extends GeneratedInventory> extends PhysicalItem<T> {
   protected final Sound clickSound;

   ClickSoundItem(ConfigurationSection config) {
      super(config);
      this.clickSound = config.contains("click_sound") ? Sounds.fromName(UtilityMethods.enumName(config.getString("click_sound"))) : null;
   }

   protected void playClickSound(T inv) {
      if (this.clickSound != null) {
         inv.getPlayer().playSound(inv.getPlayer().getLocation(), this.clickSound, 1.0F, 1.0F);
      }
   }

   @Override
   public Placeholders getPlaceholders(T inv, int n) {
      return new EmptyPlaceholders();
   }
}
