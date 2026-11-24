package fr.phoenixdevt.profiles;

import fr.phoenixdevt.profiles.data.LocationMap;
import fr.phoenixdevt.profiles.data.ProfileAttributable;
import fr.phoenixdevt.profiles.placeholder.PlaceholderCache;
import java.util.List;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlayerProfile<D extends ProfileList> {
   @NotNull
   UUID getUniqueId();

   double getHealth();

   int getFoodLevel();

   float getSaturation();

   float getExperience();

   int getLevel();

   int getAirLevel();

   @NotNull
   LocationMap getLocationMap();

   @Nullable
   Location getLocation();

   @NotNull
   LocationMap getTeleportLocationMap();

   @NotNull
   Location findLastLocation();

   @NotNull
   LocationMap getRespawnLocationMap();

   @Nullable
   Location getRespawnLocation();

   @NotNull
   LocationMap getCompassTargetMap();

   @Nullable
   Location getCompassTarget();

   @NotNull
   ItemStack[] getInventory();

   @NotNull
   ItemStack[] getEnderChest();

   @NotNull
   List<PotionEffect> getPotionEffects();

   double getBalance();

   long getLastTimePlayed();

   long getTimePlayed();

   @NotNull
   GameMode getGameMode();

   @NotNull
   ProfileAttributable getAttributes();

   @NotNull
   PlaceholderCache getPlaceholderCache();

   void applyToPlayer(D var1);

   void validateApplication(D var1);
}
