package fr.phoenixdevt.mmoprofiles.bukkit.proxy;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.Converters;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.ProfileUtils;
import fr.phoenixdevt.mmoprofiles.shared.player.ProxiedPlayerData;
import fr.phoenixdevt.mmoprofiles.shared.request.PlayerChoseProfileRequest;
import fr.phoenixdevt.mmoprofiles.shared.request.PlayerUnselectProfileRequest;
import fr.phoenixdevt.profiles.event.PlayerIdDispatchEvent;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.jetbrains.annotations.NotNull;

public class ProxyBasedProfiles implements Listener {
   private static ProxyBasedProfiles INSTANCE;

   public static ProxyBasedProfiles getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ProxyBasedProfiles();
      }

      return INSTANCE;
   }

   public void initialize() {
      Bukkit.getPluginManager().registerEvents(this, MMOProfiles.plugin);
      Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(MMOProfiles.plugin, "mmoprofiles:main");
      ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(MMOProfiles.plugin, Client.CHAT_SESSION_UPDATE) {
         public void onPacketReceiving(PacketEvent event) {
            event.setCancelled(true);
         }
      });
      ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(MMOProfiles.plugin, Server.PLAYER_INFO) {
         public void onPacketSending(PacketEvent event) {
            Set<PlayerInfoAction> playerInfoActions = (Set<PlayerInfoAction>)event.getPacket().getPlayerInfoActions().read(0);
            if (playerInfoActions.contains(PlayerInfoAction.ADD_PLAYER)) {
               for (PlayerInfoData playerInfo : (List<PlayerInfoData>)event.getPacket().getPlayerInfoDataLists().read(1)) {
                  if (event.getPlayer().getUniqueId().equals(playerInfo.getProfileId())) {
                     ProxyBasedProfiles.this.scheduleClientSidePatch(event.getPlayer(), playerInfo, playerInfoActions);
                     return;
                  }
               }
            }
         }
      });
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onPlayerLogin(PlayerLoginEvent event) {
      WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(event.getPlayer());
      Optional<WrappedSignedProperty> propertyOpt = gameProfile.getProperties().get("MMOProfilesData").stream().findAny();
      Validate.isTrue(propertyOpt.isPresent(), "Could not find MMOProfiles game profile property. Perhaps MMOProfiles is not installed/enabled on the proxy?");
      JsonObject readObject = new GsonBuilder().create().fromJson(propertyOpt.get().getValue(), JsonObject.class);
      ProxiedPlayerData playerData = ProxiedPlayerData.fromJson(readObject);
      if (playerData.isOnTemporaryServer()) {
         MMOPlayerData.setup(event.getPlayer()).setExternalData("UsingProfileSelectionTempServer", null);
      } else if (MMOProfiles.plugin.configManager.kickIfNoProfile && !playerData.hasChosenProfile()) {
         event.setKickMessage(MMOProfiles.plugin.configManager.kickIfNoProfileMessage);
         event.setResult(Result.KICK_OTHER);
      } else {
         if (MMOProfiles.plugin.configManager.unselectProfileOnLogin && playerData.hasChosenProfile()) {
            playerData.setCurrentRequest(null);
            playerData.setProfileId(null);
            ProfileUtils.sendRequest(event.getPlayer(), new PlayerUnselectProfileRequest(event.getPlayer().getUniqueId()));
         }

         if (playerData.hasChosenProfile()) {
            MythicLib.plugin.getVersion().getWrapper().setUUID(event.getPlayer(), playerData.getProfileId());
         }

         MMOPlayerData mmoPlayerData = MMOPlayerData.setup(event.getPlayer());
         mmoPlayerData.setOfficialId(playerData.getOfficialId());
         mmoPlayerData.setProfileId(playerData.getProfileId());
         mmoPlayerData.setExternalData("MMOProfilesProxiedData", playerData);
         Bukkit.getPluginManager().callEvent(new PlayerIdDispatchEvent(event.getPlayer(), playerData.getOfficialId(), playerData.getProfileId()));
      }
   }

   private void scheduleClientSidePatch(@NotNull Player player, @NotNull PlayerInfoData playerInfoData, @NotNull Set<PlayerInfoAction> playerInfoActions) {
      Bukkit.getScheduler().runTask(MMOProfiles.plugin, () -> {
         MMOPlayerData playerData = MMOPlayerData.get(player);
         if (playerData.hasProfile()) {
            Validate.isTrue(playerData.getProfileId().equals(player.getUniqueId()), "Player selected a profile but UUID was not applied");
            if (!playerData.getProfileId().equals(playerData.getOfficialId())) {
               PacketContainer removePacket = ProtocolLibrary.getProtocolManager().createPacket(Server.PLAYER_INFO_REMOVE);
               removePacket.getLists(Converters.passthrough(UUID.class)).write(0, Arrays.asList(playerData.getProfileId(), playerData.getOfficialId()));
               ProtocolLibrary.getProtocolManager().sendServerPacket(player, removePacket);
               PacketContainer addPacket = ProtocolLibrary.getProtocolManager().createPacket(Server.PLAYER_INFO);
               addPacket.getPlayerInfoActions().write(0, playerInfoActions);
               addPacket.getPlayerInfoDataLists().write(1, Arrays.asList(this.generatePlayerInfoData(player, playerInfoData)));
               ProtocolLibrary.getProtocolManager().sendServerPacket(player, addPacket);
            }
         }
      });
   }

   @NotNull
   private PlayerInfoData generatePlayerInfoData(@NotNull Player player, @NotNull PlayerInfoData playerInfoData) {
      MMOPlayerData playerData = MMOPlayerData.get(player);
      Validate.isTrue(playerData.hasProfile(), "Player has no profile selected");
      WrappedGameProfile gameProfile = playerInfoData.getProfile();
      WrappedGameProfile newProfile = gameProfile.withId(playerData.getOfficialId().toString());
      gameProfile.getProperties().forEach((str, val) -> newProfile.getProperties().put(str, val));
      return new PlayerInfoData(
         newProfile.getUUID(),
         playerInfoData.getLatency(),
         playerInfoData.isListed(),
         playerInfoData.getGameMode(),
         newProfile,
         playerInfoData.getDisplayName(),
         playerInfoData.getRemoteChatSessionData()
      );
   }

   public void synchronizePermissions(@NotNull ProfileListImpl playerData) {
      String playerName = playerData.getPlayer().getName();
      ProxiedPlayerData proxiedPlayerData = (ProxiedPlayerData)playerData.getMMOPlayerData().getExternalData("MMOProfilesProxiedData", ProxiedPlayerData.class);
      Validate.notNull(proxiedPlayerData, "Could not fetch proxied player data of " + playerName + " for permission synchronization");
      Validate.isTrue(
         proxiedPlayerData.hasCurrentRequest(PlayerChoseProfileRequest.class),
         "Could not fetch profile chose request of " + playerName + " for permission synchronization"
      );
      PlayerChoseProfileRequest request = (PlayerChoseProfileRequest)proxiedPlayerData.getCurrentRequest();
      List<String> expected = request.getSharedPermissions();
      Validate.notNull(expected, "Could not synchronize permissions of " + playerName + " as permission list is null");
      MMOProfiles.plugin.permissionModule.loadPermissible(playerData).thenAccept(permissible -> {
         List<String> current = MMOProfiles.plugin.configManager.getSharedPermissions(permissible);
         List<String> added = new ArrayList<>(expected);
         added.removeAll(current);
         List<String> removed = new ArrayList<>(current);
         removed.removeAll(expected);
         permissible.removePermissions(removed);
         permissible.givePermissions(added);
         permissible.save();
      });
   }
}
