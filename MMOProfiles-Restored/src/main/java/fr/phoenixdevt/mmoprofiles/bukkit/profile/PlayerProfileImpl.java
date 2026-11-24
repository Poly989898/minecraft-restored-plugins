package fr.phoenixdevt.mmoprofiles.bukkit.profile;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.data.LocationMapImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.data.PlayerDataType;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.data.ProfileAttributableImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.data.inventory.EnderChestWrapper;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.data.inventory.InventoryWrapper;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.placeholder.PlaceholderCacheImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.proxy.ProxyBasedProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.ProfileUtils;
import fr.phoenixdevt.profiles.PlayerProfile;
import fr.phoenixdevt.profiles.event.ProfileSelectedEvent;
import fr.phoenixdevt.profiles.exception.InvalidLocationException;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.util.Jsonable;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.Attributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerProfileImpl implements Jsonable, PlayerProfile<ProfileListImpl> {
   private final UUID uuid;
   private final String name;
   private final double health;
   private final double balance;
   private final double healthScale;
   private final int foodLevel;
   private final int level;
   private final int airLevel;
   private final float exp;
   private final float saturation;
   private final float walkSpeed;
   private final float flySpeed;
   private final boolean allowFlight;
   private final boolean flying;
   private final boolean invulnerable;
   private final boolean healthScaled;
   private final LocationMapImpl location;
   private final LocationMapImpl teleportLocation;
   private final LocationMapImpl respawnLocation;
   private final LocationMapImpl compassTarget;
   private final InventoryWrapper inventory;
   private final EnderChestWrapper enderChest;
   private final long lastTimePlayed;
   private final long timePlayed;
   private final GameMode gameMode;
   private final ProfileAttributableImpl attributes;
   private final List<PotionEffect> potionEffects;
   private final PlaceholderCacheImpl placeholderCache = new PlaceholderCacheImpl();

   public PlayerProfileImpl(
      UUID uuid,
      String name,
      double health,
      double healthScale,
      int foodLevel,
      float saturation,
      float walkSpeed,
      float flySpeed,
      boolean allowFlight,
      boolean flying,
      boolean invulnerable,
      boolean healthScaled,
      float exp,
      int level,
      int airLevel,
      LocationMapImpl location,
      LocationMapImpl teleportLocation,
      LocationMapImpl respawnLocation,
      LocationMapImpl compassTarget,
      InventoryWrapper inventory,
      EnderChestWrapper enderChest,
      List<PotionEffect> potionEffects,
      double balance,
      long lastTimePlayed,
      long timePlayed,
      GameMode gameMode,
      ProfileAttributableImpl attributes
   ) {
      this.uuid = uuid;
      this.name = name;
      this.health = health;
      this.healthScale = healthScale;
      this.foodLevel = foodLevel;
      this.saturation = saturation;
      this.walkSpeed = walkSpeed;
      this.flySpeed = flySpeed;
      this.allowFlight = allowFlight;
      this.flying = flying;
      this.invulnerable = invulnerable;
      this.healthScaled = healthScaled;
      this.exp = exp;
      this.level = level;
      this.airLevel = airLevel;
      this.location = location;
      this.teleportLocation = teleportLocation;
      this.respawnLocation = respawnLocation;
      this.compassTarget = compassTarget;
      this.inventory = inventory;
      this.enderChest = enderChest;
      this.potionEffects = potionEffects;
      this.balance = balance;
      this.lastTimePlayed = lastTimePlayed;
      this.timePlayed = timePlayed;
      this.gameMode = gameMode;
      this.attributes = attributes;
   }

   public PlayerProfileImpl(@Nullable PlayerProfileImpl currentProfile, @NotNull Player player) {
      this(
         currentProfile == null ? player.getUniqueId() : currentProfile.getUniqueId(),
         currentProfile == null ? MMOProfiles.plugin.configManager.defaultMainProfileName : currentProfile.getName(),
         player.getHealth(),
         player.getHealthScale(),
         player.getFoodLevel(),
         player.getSaturation(),
         player.getWalkSpeed(),
         player.getFlySpeed(),
         player.getAllowFlight(),
         player.isFlying(),
         player.isInvulnerable(),
         player.isHealthScaled(),
         player.getExp(),
         player.getLevel(),
         player.getRemainingAir(),
         LocationMapImpl.from(currentProfile == null ? null : currentProfile.location, player.getLocation()),
         currentProfile == null ? new LocationMapImpl() : currentProfile.teleportLocation,
         LocationMapImpl.from(currentProfile == null ? null : currentProfile.respawnLocation, ProfileUtils.fixSpawnLocation(player.getBedSpawnLocation())),
         LocationMapImpl.from(currentProfile == null ? null : currentProfile.compassTarget, player.getCompassTarget()),
         new InventoryWrapper(player),
         new EnderChestWrapper(player),
         new ArrayList<>(player.getActivePotionEffects()),
         MMOProfiles.plugin.hasEconomy() ? MMOProfiles.plugin.vaultSupport.economy.getBalance(player) : 0.0,
         System.currentTimeMillis(),
         currentProfile == null ? 0L : currentProfile.getTimePlayed() + (System.currentTimeMillis() - currentProfile.getLastTimePlayed()),
         player.getGameMode(),
         new ProfileAttributableImpl(player)
      );
   }

   public PlayerProfileImpl(@NotNull Location location, @NotNull String name) {
      this(
         UUID.randomUUID(),
         name,
         20.0,
         20.0,
         20,
         20.0F,
         0.2F,
         0.1F,
         false,
         false,
         false,
         false,
         0.0F,
         0,
         300,
         LocationMapImpl.from(null, location),
         new LocationMapImpl(),
         new LocationMapImpl(),
         new LocationMapImpl(),
         new InventoryWrapper(),
         new EnderChestWrapper(),
         new ArrayList<>(),
         0.0,
         System.currentTimeMillis(),
         0L,
         Bukkit.getServer().getDefaultGameMode(),
         new ProfileAttributableImpl()
      );
   }

   public PlayerProfileImpl(UUID uuid, JsonObject object) {
      this.attributes = object.has("Attributes") ? new ProfileAttributableImpl(object.get("Attributes").getAsJsonObject()) : new ProfileAttributableImpl();
      this.uuid = uuid;
      this.name = object.get("Name").getAsString();
      this.health = object.get("Health").getAsDouble();
      this.healthScale = object.get("HealthScale").getAsDouble();
      this.foodLevel = object.get("FoodLevel").getAsInt();
      this.saturation = object.get("Saturation").getAsFloat();
      this.walkSpeed = object.get("WalkSpeed").getAsFloat();
      this.flySpeed = object.get("FlySpeed").getAsFloat();
      this.allowFlight = object.get("AllowFlight").getAsBoolean();
      this.invulnerable = object.get("Invulnerable").getAsBoolean();
      this.healthScaled = object.get("HealthScaled").getAsBoolean();
      this.flying = object.get("Flying").getAsBoolean();
      this.balance = object.get("Balance").getAsDouble();
      this.exp = object.get("Exp").getAsFloat();
      this.level = object.get("Level").getAsInt();
      this.airLevel = object.get("AirLevel").getAsInt();
      this.gameMode = GameMode.valueOf(object.get("GameMode").getAsString());
      this.lastTimePlayed = object.get("LastTimePlayed").getAsLong();
      this.timePlayed = object.get("TimePlayed").getAsLong();
      this.location = object.has("Location") ? new LocationMapImpl(object.getAsJsonObject("Location")) : new LocationMapImpl();
      this.teleportLocation = object.has("LastTeleport") ? new LocationMapImpl(object.getAsJsonObject("LastTeleport")) : new LocationMapImpl();
      this.respawnLocation = object.has("RespawnLocation") ? new LocationMapImpl(object.getAsJsonObject("RespawnLocation")) : new LocationMapImpl();
      this.compassTarget = object.has("CompassTarget") ? new LocationMapImpl(object.getAsJsonObject("CompassTarget")) : new LocationMapImpl();
      this.potionEffects = new ArrayList<>();
      if (object.has("PotionEffects")) {
         for (JsonElement element : object.get("PotionEffects").getAsJsonArray()) {
            this.potionEffects.add((PotionEffect)MythicLib.plugin.getGson().fromJson(element, PotionEffect.class));
         }
      }

      this.inventory = new InventoryWrapper(object.get("Inventory").getAsString());
      this.enderChest = new EnderChestWrapper(object.get("EnderChest").getAsString());
   }

   @Override
   public UUID getUniqueId() {
      return this.uuid;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public int getFoodLevel() {
      return this.foodLevel;
   }

   @Override
   public double getHealth() {
      return this.health;
   }

   @Override
   public float getSaturation() {
      return this.saturation;
   }

   @Override
   public float getExperience() {
      return this.exp;
   }

   @Override
   public int getLevel() {
      return this.level;
   }

   @Override
   public int getAirLevel() {
      return this.airLevel;
   }

   public LocationMapImpl getLocationMap() {
      return this.location;
   }

   @Nullable
   @Override
   public Location getLocation() {
      return this.findLastLocation();
   }

   @NotNull
   public LocationMapImpl getTeleportLocationMap() {
      return this.teleportLocation;
   }

   @NotNull
   @Override
   public Location findLastLocation() {
      try {
         Location found = this.location.getCurrent();
         if (found != null) {
            return found;
         }
      } catch (InvalidLocationException var4) {
      }

      try {
         Location found = this.teleportLocation.getCurrent();
         if (found != null) {
            return found;
         }
      } catch (InvalidLocationException exception) {
         MMOProfiles.plugin.getLogger().log(Level.SEVERE, "Profile " + this.uuid + " has tried logging into a non existing world: " + exception.getMessage());
      }

      return MMOProfiles.plugin.configManager.newProfileSpawnPoint;
   }

   public void setLastTeleportLocation(@NotNull Location location) {
      this.teleportLocation.setCurrent(location);
   }

   @NotNull
   public LocationMapImpl getRespawnLocationMap() {
      return this.respawnLocation;
   }

   @Nullable
   @Override
   public Location getRespawnLocation() {
      return this.respawnLocation.getCurrent();
   }

   @NotNull
   public LocationMapImpl getCompassTargetMap() {
      return this.compassTarget;
   }

   @Nullable
   @Override
   public Location getCompassTarget() {
      return this.compassTarget.getCurrent();
   }

   @Override
   public ItemStack[] getInventory() {
      return this.inventory.asArray();
   }

   @NotNull
   @Override
   public ItemStack[] getEnderChest() {
      return this.enderChest.asArray();
   }

   @Override
   public List<PotionEffect> getPotionEffects() {
      return this.potionEffects;
   }

   @NotNull
   public ProfileAttributableImpl getAttributes() {
      return this.attributes;
   }

   @Override
   public double getBalance() {
      return this.balance;
   }

   @Override
   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   @Override
   public long getTimePlayed() {
      return this.timePlayed;
   }

   @NotNull
   @Override
   public GameMode getGameMode() {
      return this.gameMode;
   }

   public InventoryWrapper getInventoryWrapper() {
      return this.inventory;
   }

   public PlaceholderCacheImpl getPlaceholderCache() {
      return this.placeholderCache;
   }

   public void applyToPlayer(ProfileListImpl playerData) {
      Player player = playerData.getPlayer();
      Set<PlayerDataType> synced = MMOProfiles.plugin.configManager.syncedDataMap;
      if (synced.contains(PlayerDataType.ATTRIBUTES)) {
         this.attributes.apply(player);
      }

      if (synced.contains(PlayerDataType.HEALTH)) {
         player.setHealth(Math.max(0.001, Math.min(player.getAttribute(Attributes.MAX_HEALTH).getValue(), this.health)));
      }

      if (synced.contains(PlayerDataType.HEALTH_SCALE)) {
         player.setHealthScaled(this.healthScaled);
         player.setHealthScale(this.healthScale);
      }

      if (synced.contains(PlayerDataType.FOOD_LEVEL)) {
         player.setFoodLevel(this.foodLevel);
      }

      if (synced.contains(PlayerDataType.SATURATION)) {
         player.setSaturation(this.saturation);
      }

      if (synced.contains(PlayerDataType.WALK_SPEED)) {
         player.setWalkSpeed(this.walkSpeed);
      }

      if (synced.contains(PlayerDataType.FLY_SPEED)) {
         player.setFlySpeed(this.flySpeed);
      }

      if (synced.contains(PlayerDataType.ALLOW_FLIGHT)) {
         player.setAllowFlight(this.allowFlight);
      }

      if (synced.contains(PlayerDataType.EXP)) {
         player.setExp(this.exp);
      }

      if (synced.contains(PlayerDataType.LEVEL)) {
         player.setLevel(this.level);
      }

      if (synced.contains(PlayerDataType.AIR_LEVEL)) {
         player.setRemainingAir(Math.min(player.getMaximumAir(), Math.max(0, this.airLevel)));
      }

      if (synced.contains(PlayerDataType.GAME_MODE)) {
         player.setGameMode(this.gameMode);
      }

      if (synced.contains(PlayerDataType.RESPAWN_LOCATION)) {
         try {
            player.setBedSpawnLocation(this.respawnLocation.getCurrent());
         } catch (InvalidLocationException exception) {
            player.setBedSpawnLocation(null);
         }
      }

      if (synced.contains(PlayerDataType.COMPASS_TARGET)) {
         try {
            player.setCompassTarget(Objects.requireNonNullElse(this.compassTarget.getCurrent(), player.getWorld().getSpawnLocation()));
         } catch (InvalidLocationException exception) {
            ProfileUtils.resetCompassTarget(player);
         }
      }

      if (synced.contains(PlayerDataType.POTION_EFFECTS)) {
         player.getActivePotionEffects().clear();

         for (PotionEffect potionEffect : this.potionEffects) {
            player.addPotionEffect(potionEffect);
         }
      }

      if (synced.contains(PlayerDataType.INVENTORY)) {
         this.inventory.apply(player);
      }

      if (synced.contains(PlayerDataType.ENDER_CHEST)) {
         this.enderChest.apply(player);
      }

      if (MMOProfiles.plugin.configManager.forcedGameMode != null) {
         player.setGameMode(MMOProfiles.plugin.configManager.forcedGameMode);
      }

      if (MMOProfiles.plugin.configManager.arePermissionsSynchronized() && playerData.hasProfilePending()) {
         ProxyBasedProfiles.getInstance().synchronizePermissions(playerData);
      }

      if (MMOProfiles.plugin.hasEconomy()) {
         if (synced.contains(PlayerDataType.BALANCE)) {
            playerData.applyBalance(this.balance);
         } else if (MMOProfiles.plugin.configManager.proxyBasedProfiles) {
            playerData.applySharedBalance();
         }
      }
   }

   public void validateApplication(ProfileListImpl playerData) {
      playerData.setStartedPlaying();
      Bukkit.getPluginManager().callEvent(new ProfileSelectedEvent(playerData, this));
      Player player = playerData.getPlayer();
      Set<PlayerDataType> synced = MMOProfiles.plugin.configManager.syncedDataMap;
      if (synced.contains(PlayerDataType.LOCATION)) {
         playerData.setSaveLastLocation(false);
         player.teleport(this.findLastLocation());
         playerData.setSaveLastLocation(true);
      }

      if (synced.contains(PlayerDataType.FLYING) && (!this.flying || player.getAllowFlight())) {
         player.setFlying(this.flying);
      }

      if (synced.contains(PlayerDataType.INVULNERABLE)) {
         player.setInvulnerable(this.invulnerable);
      }

      if (MMOProfiles.plugin.configManager.forcedInvulnerability != null) {
         player.setInvulnerable(MMOProfiles.plugin.configManager.forcedInvulnerability);
      }

      if (this.timePlayed == 0L) {
         ProfileUtils.castScript(playerData.getMMOPlayerData(), MMOProfiles.plugin.configManager.profileCreateScript);
      }

      ProfileUtils.castScript(playerData.getMMOPlayerData(), MMOProfiles.plugin.configManager.profileSelectScript);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.uuid);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PlayerProfileImpl that = (PlayerProfileImpl)o;
         return Objects.equals(this.uuid, that.uuid);
      } else {
         return false;
      }
   }

   public JsonObject toJson() {
      JsonObject mainObject = new JsonObject();
      mainObject.add("Attributes", this.attributes.toJson());
      mainObject.addProperty("Name", this.name);
      mainObject.addProperty("Health", this.health);
      mainObject.addProperty("HealthScale", this.healthScale);
      mainObject.addProperty("FoodLevel", this.foodLevel);
      mainObject.addProperty("Saturation", this.saturation);
      mainObject.addProperty("WalkSpeed", this.walkSpeed);
      mainObject.addProperty("FlySpeed", this.flySpeed);
      mainObject.addProperty("AllowFlight", this.allowFlight);
      mainObject.addProperty("Flying", this.flying);
      mainObject.addProperty("Invulnerable", this.invulnerable);
      mainObject.addProperty("HealthScaled", this.healthScaled);
      mainObject.addProperty("Exp", this.exp);
      mainObject.addProperty("Level", this.level);
      mainObject.addProperty("GameMode", this.gameMode.toString());
      mainObject.addProperty("Balance", this.balance);
      mainObject.addProperty("AirLevel", this.airLevel);
      mainObject.addProperty("LastTimePlayed", System.currentTimeMillis());
      mainObject.addProperty("TimePlayed", this.timePlayed);
      mainObject.add("Location", this.location.toJson());
      if (!this.teleportLocation.isEmpty()) {
         mainObject.add("LastTeleport", this.teleportLocation.toJson());
      }

      if (!this.respawnLocation.isEmpty()) {
         mainObject.add("RespawnLocation", this.respawnLocation.toJson());
      }

      if (!this.compassTarget.isEmpty()) {
         mainObject.add("CompassTarget", this.compassTarget.toJson());
      }

      JsonArray potionEffectsArray = new JsonArray();

      for (PotionEffect eff : this.potionEffects) {
         potionEffectsArray.add(MythicLib.plugin.getGson().toJsonTree(eff));
      }

      mainObject.add("PotionEffects", potionEffectsArray);
      mainObject.addProperty("Inventory", this.inventory.serializeBase64());
      mainObject.addProperty("EnderChest", this.enderChest.serializeBase64());
      return mainObject;
   }
}
