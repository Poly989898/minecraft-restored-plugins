package fr.phoenixdevt.mmoprofiles.bukkit.listener;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.manager.InventoryManager;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.PlayerProfileImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.data.PlayerDataType;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.ProfileUtils;
import io.lumine.mythic.lib.api.event.SynchronizedDataLoadEvent;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.jetbrains.annotations.NotNull;
import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent;

public class PlayerListener implements Listener {
   @EventHandler
   public void onPlayerSpawnEvent(AsyncPlayerSpawnLocationEvent event) {
      if (!MMOProfiles.plugin.configManager.uniqueProfile && MMOProfiles.plugin.configManager.syncedDataMap.contains(PlayerDataType.LOCATION)) {
         event.setSpawnLocation(MMOProfiles.plugin.configManager.profileSelectionLocation);
      }
   }

   @EventHandler
   public void onDataLoad(SynchronizedDataLoadEvent event) {
      if (event.getManager().getOwningPlugin().equals(MMOProfiles.plugin)) {
         ProfileListImpl playerData = (ProfileListImpl)event.getHolder();
         if (MMOProfiles.plugin.configManager.waitForResourcePack) {
            return;
         }

         if (!playerData.hasProfilePending() && MMOProfiles.plugin.configManager.noGuiOnLogin) {
            return;
         }

         openProfileSelection(playerData);
      }
   }

   @Deprecated
   public static void processLogin(@NotNull ProfileListImpl playerData) {
      openProfileSelection(playerData);
   }

   public static void openProfileSelection(@NotNull ProfileListImpl playerData) {
      Validate.isTrue(!playerData.hasStartedPlaying(), "Player has already chosen a profile");
      if (!playerData.getMMOPlayerData().hasExternalData("UsingProfileSelectionTempServer")) {
         if (playerData.hasProfilePending()) {
            PlayerProfileImpl profile = playerData.getProfile(playerData.getProfileId());
            Validate.notNull(profile, "Could not find profile on login with ID '" + playerData.getProfileId() + "'");
            playerData.applyProfile(profile, true);
         } else {
            new PlayerProfileImpl(playerData.getPlayer().getLocation(), "Temporary Profile").applyToPlayer(playerData);
            ProfileUtils.castScript(playerData.getMMOPlayerData(), MMOProfiles.plugin.configManager.loginScript);
            if (MMOProfiles.plugin.configManager.uniqueProfile) {
               if (!playerData.getProfiles().isEmpty()) {
                  playerData.applyProfile(playerData.getProfiles().get(0));
               } else {
                  playerData.createProfile("Main Profile").thenAccept(prof -> playerData.applyProfile(prof));
               }
            } else {
               InventoryManager.PROFILE_LIST.generate(playerData).open();
            }
         }
      }
   }

   @EventHandler
   public void onResourcePackLoad(PlayerResourcePackStatusEvent event) {
      if (MMOProfiles.plugin.configManager.waitForResourcePack && event.getStatus() == Status.SUCCESSFULLY_LOADED) {
         Bukkit.getScheduler()
            .runTaskLater(
               MMOProfiles.plugin,
               () -> openProfileSelection(MMOProfiles.plugin.getPlayerData(event.getPlayer().getUniqueId())),
               MMOProfiles.plugin.configManager.resourcePackLoadDelay
            );
      }
   }
}
