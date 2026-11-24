package net.Indyuce.mmocore.gui;

import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.builtin.GoBackItem;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.SoundEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.ClassOption;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.manager.InventoryManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SubclassSelect extends AbstractClassSelect {
   public SubclassSelect() {
      super("subclass-select");
   }

   @Nullable
   public InventoryItem<?> resolveItem(@NotNull String function, @NotNull ConfigurationSection config) {
      if (function.startsWith("sub-class")) {
         return new SubclassSelect.ClassItem(config);
      } else {
         return function.equalsIgnoreCase("back") ? new GoBackItem(config) : null;
      }
   }

   public GeneratedInventory newInventory(PlayerData data) {
      ClassSelect.ProfessSelectionInventory prev = InventoryManager.CLASS_SELECT.newInventory(data);
      return new SubclassSelect.SubclassSelectionInventory(prev, data);
   }

   public class ClassItem extends AbstractClassSelect.AbstractClassItem<SubclassSelect.SubclassSelectionInventory> {
      public ClassItem(ConfigurationSection config) {
         super(config, "sub-class-".length());
      }

      public boolean isDisplayed(@NotNull SubclassSelect.SubclassSelectionInventory inv) {
         return inv.playerData
            .getProfess()
            .getSubclasses()
            .stream()
            .anyMatch(subclass -> subclass.getLevel() <= inv.playerData.getLevel() && subclass.getProfess().getId().equals(this.playerClass.getId()));
      }

      public void onClick(@NotNull SubclassSelect.SubclassSelectionInventory inv, @NotNull InventoryClickEvent event) {
         if (inv.playerData.getClassPoints() < 1) {
            inv.getPlayer().closeInventory();
            MMOCore.plugin.soundManager.getSound(SoundEvent.CANT_SELECT_CLASS).playTo(inv.getPlayer());
            ConfigMessage.fromKey("cant-choose-new-class").send(inv.playerData);
         } else if (this.playerClass.hasOption(ClassOption.NEEDS_PERMISSION)
            && !inv.getPlayer().hasPermission("mmocore.class." + this.playerClass.getId().toLowerCase())) {
            MMOCore.plugin.soundManager.getSound(SoundEvent.CANT_SELECT_CLASS).playTo(inv.getPlayer());
            ConfigMessage.fromKey("no-permission-for-class").send(inv.playerData);
         } else {
            InventoryManager.CLASS_CONFIRM.get(MMOCoreUtils.ymlName(this.playerClass.getId())).newInventory(inv, true).open();
         }
      }
   }

   public class SubclassSelectionInventory extends AbstractClassSelect.AbstractClassGeneratedInventory {
      public SubclassSelectionInventory(ClassSelect.ProfessSelectionInventory inv, PlayerData playerData) {
         super(inv.getNavigator(), playerData, inv.profileCallback);
      }
   }
}
