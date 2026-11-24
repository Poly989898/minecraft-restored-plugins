package fr.phoenixdevt.mmoprofiles.bukkit.profile;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.ProfileUtils;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.Rank;
import fr.phoenixdevt.mmoprofiles.shared.request.PlayerChoseProfileRequest;
import fr.phoenixdevt.profiles.ProfileList;
import fr.phoenixdevt.profiles.event.ProfileAutosavedEvent;
import fr.phoenixdevt.profiles.event.ProfileCreateEvent;
import fr.phoenixdevt.profiles.event.ProfileRemoveEvent;
import fr.phoenixdevt.profiles.event.ProfileSelectEvent;
import fr.phoenixdevt.profiles.event.ProfileUnloadEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.util.Autosaveable;
import io.lumine.mythic.lib.util.Jsonable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProfileListImpl extends SynchronizedDataHolder implements Jsonable, ProfileList<PlayerProfileImpl>, Autosaveable {
   private final List<PlayerProfileImpl> profiles = new ArrayList<>();
   @Nullable
   private UUID lastSpoofedId;
   private final Map<UUID, Double> balanceHistory = new HashMap<>();
   private boolean startedPlaying;
   private boolean saveLastLocation;
   @Nullable
   private PlayerProfileImpl currentProfile;
   @Nullable
   private Integer currentProfileIndex;
   @Nullable
   private Boolean invulnerableOnJoin;

   public ProfileListImpl(MMOPlayerData playerData) {
      super(MMOProfiles.plugin, playerData);
      this.loginScript();
   }

   public boolean hasStartedPlaying() {
      return this.startedPlaying;
   }

   public void setStartedPlaying() {
      Validate.isTrue(!this.startedPlaying, "Player already marked as starting playing");
      this.startedPlaying = true;
      this.reapplyInvulnerability();
   }

   public void setSaveLastLocation(boolean saveLastLocation) {
      this.saveLastLocation = saveLastLocation;
   }

   public boolean savesLastLocation() {
      return this.saveLastLocation;
   }

   public boolean hasProfilePending() {
      return this.getMMOPlayerData().hasProfile() && !this.hasStartedPlaying();
   }

   private void loginScript() {
      if (this.getMMOPlayerData().isOnline()) {
         this.invulnerableOnJoin = this.getPlayer().isInvulnerable();
         this.getPlayer().setInvulnerable(true);
      }
   }

   public void loadFromJson(@NotNull JsonObject jsonObject) {
      if (jsonObject.has("Profiles")) {
         JsonObject profileObject = jsonObject.getAsJsonObject("Profiles");

         for (String uuid : profileObject.keySet()) {
            this.getProfiles().add(new PlayerProfileImpl(UUID.fromString(uuid), profileObject.getAsJsonObject(uuid)));
         }
      } else if (MMOProfiles.plugin.configManager.defaultProfile && this.getPlayer().hasPlayedBefore()) {
         this.getProfiles().add(new PlayerProfileImpl(null, this.getPlayer()));
      }

      if (jsonObject.has("Shared")) {
         JsonObject shared = jsonObject.getAsJsonObject("Shared");
         if (shared.has("LastUniqueId")) {
            this.lastSpoofedId = UUID.fromString(shared.get("LastUniqueId").getAsString());
         }

         if (shared.has("BalanceHistory")) {
            JsonObject balanceHistory = shared.getAsJsonObject("BalanceHistory");

            for (String key : balanceHistory.keySet()) {
               this.balanceHistory.put(UUID.fromString(key), balanceHistory.get(key).getAsDouble());
            }
         }
      }
   }

   @NotNull
   public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      JsonObject profileObject = new JsonObject();

      for (PlayerProfileImpl playerProfile : this.getProfiles()) {
         profileObject.add(playerProfile.getUniqueId().toString(), playerProfile.toJson());
      }

      jsonObject.add("Profiles", profileObject);
      JsonObject shared = new JsonObject();
      if (this.lastSpoofedId != null) {
         shared.addProperty("LastUniqueId", this.lastSpoofedId.toString());
      }

      if (!this.balanceHistory.isEmpty()) {
         JsonObject balanceHistoryObj = new JsonObject();
         this.balanceHistory.forEach((uuid, bal) -> balanceHistoryObj.addProperty(uuid.toString(), bal));
         shared.add("BalanceHistory", balanceHistoryObj);
      }

      jsonObject.add("Shared", shared);
      return jsonObject;
   }

   public void applyBalance(double profileBalance) {
      if (this.currentProfile != null) {
         double lastBalance = this.balanceHistory
            .getOrDefault(this.currentProfile.getUniqueId(), MMOProfiles.plugin.vaultSupport.economy.getBalance(this.getPlayer()));
         MMOProfiles.plugin.vaultSupport.applyVaultTransaction(this.getPlayer(), profileBalance - lastBalance);
      }
   }

   public void applySharedBalance() {
      if (this.lastSpoofedId != null) {
         Double lastProfileBalance = this.balanceHistory.get(this.lastSpoofedId);
         Validate.notNull(lastProfileBalance, "Could not find balance of last profile");
         double lastBalance = this.balanceHistory
            .getOrDefault(this.getPlayer().getUniqueId(), MMOProfiles.plugin.vaultSupport.economy.getBalance(this.getPlayer()));
         MMOProfiles.plugin.vaultSupport.applyVaultTransaction(this.getPlayer(), lastProfileBalance - lastBalance);
      }
   }

   @Override
   public List<PlayerProfileImpl> getProfiles() {
      return this.profiles;
   }

   public int getCurrentProfileIndex() {
      Validate.notNull(this.currentProfile, "No profile chosen yet");
      return this.currentProfileIndex;
   }

   @Nullable
   public PlayerProfileImpl getProfile(UUID profileId) {
      for (PlayerProfileImpl prof : this.profiles) {
         if (prof.getUniqueId().equals(profileId)) {
            return prof;
         }
      }

      return null;
   }

   public PlayerProfileImpl getCurrent() {
      return this.currentProfile;
   }

   public int getProfileSlots() {
      int amount = MMOProfiles.plugin.configManager.defaultMaxSlots;

      for (Rank rank : MMOProfiles.plugin.configManager.ranks) {
         if (this.getPlayer().hasPermission(rank.getPermission())) {
            amount = Math.max(amount, rank.getMaxSlots());
         }
      }

      return amount;
   }

   public void applyProfile(@NotNull PlayerProfileImpl profile) {
      this.applyProfile(profile, false);
   }

   public void applyProfile(@NotNull PlayerProfileImpl profile, boolean proxy) {
      Validate.isTrue(this.currentProfile == null, "Player has already chosen a profile");
      int index = this.profiles.indexOf(profile);
      if (index < 0) {
         this.profiles.add(profile);
      }

      this.currentProfileIndex = index < 0 ? this.profiles.size() - 1 : index;
      this.currentProfile = profile;
      this.profiles.forEach(prof -> prof.getPlaceholderCache().close());
      if (!proxy && MMOProfiles.plugin.configManager.proxyBasedProfiles) {
         ProfileUtils.sendRequest(
            this.getPlayer(),
            new PlayerChoseProfileRequest(
               this.getMMOPlayerData().getOfficialId(),
               profile.getUniqueId(),
               MMOProfiles.plugin.configManager.targetServers,
               MMOProfiles.plugin.configManager.backToInitialServerDelay,
               MMOProfiles.plugin.configManager.arePermissionsSynchronized() ? MMOProfiles.plugin.configManager.getSharedPermissions(this) : null
            )
         );
      } else {
         profile.applyToPlayer(this);
         if (proxy) {
            profile.validateApplication(this);
         } else {
            Bukkit.getServer().getPluginManager().callEvent(new ProfileSelectEvent(() -> profile.validateApplication(this), MMOProfiles.plugin, this, profile));
         }
      }
   }

   @NotNull
   public CompletableFuture<PlayerProfileImpl> createProfile(@NotNull String name) {
      PlayerProfileImpl profile = new PlayerProfileImpl(MMOProfiles.plugin.configManager.newProfileSpawnPoint, name);
      CompletableFuture<PlayerProfileImpl> future = new CompletableFuture<>();
      Bukkit.getServer().getPluginManager().callEvent(new ProfileCreateEvent(() -> future.complete(profile), MMOProfiles.plugin, this, profile));
      return future;
   }

   public void removeProfile(@NotNull PlayerProfileImpl playerProfile) {
      if (this.profiles.remove(playerProfile)) {
         Bukkit.getServer().getPluginManager().callEvent(new ProfileRemoveEvent(MMOProfiles.plugin, this, playerProfile));
      }
   }

   private void reapplyInvulnerability() {
      if (this.getMMOPlayerData().isOnline() && this.invulnerableOnJoin != null) {
         this.getPlayer().setInvulnerable(this.invulnerableOnJoin);
         this.invulnerableOnJoin = null;
      }
   }

   public void prepareSaving(boolean autosave) {
      if (this.hasStartedPlaying()) {
         if (MMOProfiles.plugin.hasEconomy()) {
            this.balanceHistory.put(this.getPlayer().getUniqueId(), MMOProfiles.plugin.vaultSupport.economy.getBalance(this.getPlayer()));
         }

         this.lastSpoofedId = this.getPlayer().getUniqueId();
      }

      if (!autosave && !this.hasStartedPlaying()) {
         this.reapplyInvulnerability();
      }

      if (this.currentProfile != null && this.hasStartedPlaying()) {
         PlayerProfileImpl curr = new PlayerProfileImpl(this.currentProfile, this.getPlayer());
         this.profiles.set(Objects.requireNonNull(this.currentProfileIndex, "Current profile index not set"), curr);
         Bukkit.getPluginManager()
            .callEvent(
               autosave
                  ? new ProfileAutosavedEvent(MMOProfiles.plugin, this, this.currentProfile)
                  : new ProfileUnloadEvent(() -> this.getMMOPlayerData().setProfileId(null), MMOProfiles.plugin, this, this.currentProfile)
            );
      }
   }

   public static ProfileListImpl get(Player player) {
      return (ProfileListImpl)MMOProfiles.plugin.playerDataManager.get(player);
   }
}
