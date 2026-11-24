package fr.phoenixdevt.mmoprofiles.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.GameProfile.Property;
import fr.phoenixdevt.mmoprofiles.shared.player.ProxiedPlayerData;
import fr.phoenixdevt.mmoprofiles.velocity.MMOProfiles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class GameProfileInjector {
   private final MMOProfiles plugin;

   public GameProfileInjector(MMOProfiles plugin) {
      this.plugin = plugin;
   }

   @Subscribe
   public void onPreConnect(ServerPreConnectEvent event) {
      Player player = event.getPlayer();
      ProxiedPlayerData playerData = this.plugin.getPlayerDataMap().fromInitialId(event.getPlayer().getUniqueId());
      this.injectGameProfile(player, this.modifyGameProfile(player.getGameProfile(), playerData));
   }

   @NotNull
   private GameProfile modifyGameProfile(@NotNull GameProfile gameProfile, @NotNull ProxiedPlayerData playerData) {
      List<Property> properties = new ArrayList<>(gameProfile.getProperties());
      properties.removeIf(property -> property.getName().equals("MMOProfilesData"));
      properties.add(this.toProperty(playerData));
      return gameProfile.withProperties(properties);
   }

   @NotNull
   private Property toProperty(@NotNull ProxiedPlayerData playerData) {
      return new Property("MMOProfilesData", playerData.toJson().toString(), "ApprovedByTaner");
   }

   private void injectGameProfile(@NotNull Player player, @NotNull GameProfile gameProfile) {
      try {
         Field _profile = player.getClass().getDeclaredField("profile");
         _profile.setAccessible(true);
         _profile.set(player, gameProfile);
         _profile.setAccessible(false);
      } catch (Exception exception) {
         throw new RuntimeException("Could not fetch game profile", exception);
      }
   }
}
