package fr.phoenixdevt.mmoprofiles.bukkit.profile.data;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.ProfileUtils;
import fr.phoenixdevt.profiles.data.LocationMap;
import fr.phoenixdevt.profiles.util.WrappedLocation;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.util.Jsonable;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocationMapImpl implements LocationMap, Jsonable {
   private final Map<String, WrappedLocation> map = new HashMap<>();

   public LocationMapImpl() {
   }

   public LocationMapImpl(@NotNull JsonObject object) {
      Validate.notNull(object, "JSON cannot be null");

      for (String key : object.keySet()) {
         WrappedLocation loc = (WrappedLocation)MythicLib.plugin.getGson().fromJson(object.getAsJsonObject(key), WrappedLocation.class);
         if (loc != null && loc.getWorldName() != null) {
            this.map.put(key, loc);
         }
      }
   }

   @Override
   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   @Nullable
   @Override
   public Location getCurrent() {
      WrappedLocation wrapped = this.getCurrentWrapped();
      return wrapped == null ? null : ProfileUtils.toBukkit(wrapped);
   }

   @Nullable
   @Override
   public WrappedLocation getCurrentWrapped() {
      return this.map.get(this.getIdentifier());
   }

   @Override
   public boolean hasCurrent() {
      return this.map.containsKey(this.getIdentifier());
   }

   @Override
   public void setCurrent(@NotNull Location location) {
      Validate.notNull(location, "Location cannot be null");
      Validate.notNull(location.getWorld(), "Location world cannot be null");
      this.map.put(this.getIdentifier(), ProfileUtils.wrap(location));
   }

   @NotNull
   @Override
   public String getIdentifier() {
      return MMOProfiles.plugin.configManager.serverIdentifier;
   }

   public static LocationMapImpl from(@Nullable LocationMapImpl oldMap, @Nullable Location location) {
      LocationMapImpl newMap = oldMap == null ? new LocationMapImpl() : oldMap;
      if (location == null) {
         newMap.map.remove(MMOProfiles.plugin.configManager.serverIdentifier);
      } else {
         newMap.map.put(MMOProfiles.plugin.configManager.serverIdentifier, ProfileUtils.wrap(location));
      }

      return newMap;
   }

   @NotNull
   public JsonObject toJson() {
      JsonObject object = new JsonObject();
      this.map.forEach((key, value) -> object.add(key, MythicLib.plugin.getGson().toJsonTree(value)));
      return object;
   }

   @Override
   public String toString() {
      return "LocationMapImpl{map=" + this.map + '}';
   }
}
