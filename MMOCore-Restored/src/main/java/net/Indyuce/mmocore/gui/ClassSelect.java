package net.Indyuce.mmocore.gui;

import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.SoundEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.ClassOption;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.manager.InventoryManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClassSelect extends AbstractClassSelect {
   public ClassSelect() {
      super("class-select");
   }

   @Nullable
   public InventoryItem<?> resolveItem(@NotNull String function, @NotNull ConfigurationSection config) {
      return function.startsWith("class") ? new ClassSelect.ClassItem(config) : null;
   }

   public ClassSelect.ProfessSelectionInventory newInventory(PlayerData data) {
      return this.newInventory(data, null);
   }

   public ClassSelect.ProfessSelectionInventory newInventory(PlayerData data, @Nullable Runnable profileRunnable) {
      return new ClassSelect.ProfessSelectionInventory(data, profileRunnable);
   }

   private PlayerClass findDeepestSubclass(PlayerData player, PlayerClass root) {
      for (String checkedName : player.getSavedClasses()) {
         PlayerClass checked = MMOCore.plugin.classManager.getOrThrow(checkedName);
         if (root.hasSubclass(checked)) {
            return checked;
         }
      }

      return root;
   }

   public class ClassItem extends AbstractClassSelect.AbstractClassItem<ClassSelect.ProfessSelectionInventory> {
      public ClassItem(ConfigurationSection config) {
         super(config, "class-".length());
      }

      public void onClick(@NotNull ClassSelect.ProfessSelectionInventory inv, @NotNull InventoryClickEvent event) {
         if (inv.profileCallback == null && inv.playerData.getClassPoints() < 1) {
            MMOCore.plugin.soundManager.getSound(SoundEvent.CANT_SELECT_CLASS).playTo(inv.getPlayer());
            ConfigMessage.fromKey("cant-choose-new-class").send(inv.playerData);
         } else if (this.playerClass.hasOption(ClassOption.NEEDS_PERMISSION)
            && !inv.getPlayer().hasPermission("mmocore.class." + this.playerClass.getId().toLowerCase())) {
            MMOCore.plugin.soundManager.getSound(SoundEvent.CANT_SELECT_CLASS).playTo(inv.getPlayer());
            ConfigMessage.fromKey("no-permission-for-class").send(inv.playerData);
         } else if (this.playerClass.equals(inv.playerData.getProfess())) {
            MMOCore.plugin.soundManager.getSound(SoundEvent.CANT_SELECT_CLASS).playTo(inv.getPlayer());
            ConfigMessage.fromKey("already-on-class", "class", this.playerClass.getName()).send(inv.getPlayer());
         } else {
            inv.getNavigator().unblockClosing();
            PlayerClass playerClass = ClassSelect.this.findDeepestSubclass(inv.playerData, this.playerClass);
            InventoryManager.CLASS_CONFIRM.get(MMOCoreUtils.ymlName(playerClass.getId())).newInventory(inv, inv.profileCallback != null).open();
         }
      }
   }

   public class ProfessSelectionInventory extends AbstractClassSelect.AbstractClassGeneratedInventory {
      public ProfessSelectionInventory(PlayerData playerData, @Nullable Runnable profileRunnable) {
         super(new Navigator(playerData.getMMOPlayerData()), playerData, profileRunnable);
      }
   }
}
