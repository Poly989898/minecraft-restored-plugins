package fr.phoenixdevt.mmoprofiles.bukkit.gui;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.PlayerProfileImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.ProfileUtils;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.Navigator;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.EditableInventory;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.GeneratedInventory;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.InventoryItem;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.SimpleItem;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.GoBackItem;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class ConfirmDeletionViewer extends EditableInventory {
   private final Map<String, Function<ConfigurationSection, InventoryItem<?>>> ITEM_REGISTRY = Map.of(
      "yes", x$0 -> new ConfirmDeletionViewer.DeleteItem(x$0), "back", GoBackItem::new
   );

   public ConfirmDeletionViewer() {
      super("confirm-deletion");
   }

   public InventoryItem<?> resolveItem(String function, ConfigurationSection config) {
      Function<ConfigurationSection, InventoryItem<?>> type = this.ITEM_REGISTRY.get(function);
      return type != null ? type.apply(config) : null;
   }

   @NotNull
   public ConfirmDeletionViewer.Generated generate(
      @NotNull GeneratedInventory generatedInventory, @NotNull ProfileListImpl playerData, @NotNull PlayerProfileImpl profile
   ) {
      return new ConfirmDeletionViewer.Generated(generatedInventory.getNavigator(), playerData, profile);
   }

   public class DeleteItem extends SimpleItem<ConfirmDeletionViewer.Generated> {
      public DeleteItem(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull ConfirmDeletionViewer.Generated inv, @NotNull InventoryClickEvent event) {
         inv.playerData.removeProfile(inv.profile);
         ProfileUtils.castScript(inv.playerData.getMMOPlayerData(), MMOProfiles.plugin.configManager.profileDeleteScript);
         inv.getNavigator().popOpen();
      }
   }

   public class Generated extends GeneratedInventory {
      private final ProfileListImpl playerData;
      private final PlayerProfileImpl profile;

      public Generated(Navigator navigator, ProfileListImpl playerData, PlayerProfileImpl profile) {
         super(navigator, ConfirmDeletionViewer.this);
         this.playerData = playerData;
         this.profile = profile;
      }

      public long getCloseTimeOut() {
         return MMOProfiles.plugin.configManager.guiCloseTimeout;
      }
   }
}
