package fr.phoenixdevt.mmoprofiles.bukkit.manager.data.json;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import io.lumine.mythic.lib.data.DefaultOfflineDataHolder;
import io.lumine.mythic.lib.data.json.JSONSynchronizedDataHandler;
import io.lumine.mythic.lib.gson.JsonObject;
import java.util.UUID;

public class JSONDataHandler extends JSONSynchronizedDataHandler<ProfileListImpl, DefaultOfflineDataHolder> {
   public JSONDataHandler() {
      super(MMOProfiles.plugin, true);
   }

   public void setup() {
   }

   public void close() {
   }

   public void loadFromObject(ProfileListImpl profileList, JsonObject jsonObject) {
      profileList.loadFromJson(jsonObject);
   }

   public DefaultOfflineDataHolder getOffline(UUID uuid) {
      return new DefaultOfflineDataHolder(uuid);
   }
}
