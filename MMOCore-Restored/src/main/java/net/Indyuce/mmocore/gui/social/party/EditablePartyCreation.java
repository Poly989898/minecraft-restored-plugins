package net.Indyuce.mmocore.gui.social.party;

import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.EditableInventory;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.gui.editable.item.builtin.CloseInventoryItem;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.manager.InventoryManager;
import net.Indyuce.mmocore.party.provided.MMOCorePartyModule;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditablePartyCreation extends EditableInventory {
   public EditablePartyCreation() {
      super("party-creation");
   }

   @Nullable
   public InventoryItem<?> resolveItem(@NotNull String function, @NotNull ConfigurationSection config) {
      if (function.equalsIgnoreCase("create")) {
         return new EditablePartyCreation.CreateItem(config);
      } else {
         return function.equalsIgnoreCase("back") ? new CloseInventoryItem(config) : null;
      }
   }

   public EditablePartyCreation.ClassConfirmationInventory newInventory(PlayerData data) {
      return new EditablePartyCreation.ClassConfirmationInventory(data);
   }

   public class ClassConfirmationInventory extends GeneratedInventory {
      private final PlayerData playerData;

      public ClassConfirmationInventory(PlayerData playerData) {
         super(new Navigator(playerData.getMMOPlayerData()), EditablePartyCreation.this);
         this.playerData = playerData;
      }
   }

   public class CreateItem extends SimpleItem<EditablePartyCreation.ClassConfirmationInventory> {
      public CreateItem(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull EditablePartyCreation.ClassConfirmationInventory inv, @NotNull InventoryClickEvent event) {
         ((MMOCorePartyModule)MMOCore.plugin.partyModule).newRegisteredParty(inv.playerData);
         InventoryManager.PARTY_VIEW.newInventory(inv.playerData).open();
         inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
      }
   }
}
