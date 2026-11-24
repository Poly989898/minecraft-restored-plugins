package fr.phoenixdevt.mmoprofiles.bukkit.manager.data;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.manager.data.json.JSONDataHandler;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.data.DefaultOfflineDataHolder;
import io.lumine.mythic.lib.data.SynchronizedDataManager;
import org.jetbrains.annotations.NotNull;

public class PlayerDataManager extends SynchronizedDataManager<ProfileListImpl, DefaultOfflineDataHolder> {
   public PlayerDataManager() {
      super(MMOProfiles.plugin, new JSONDataHandler(), true);
   }

   public ProfileListImpl newPlayerData(@NotNull MMOPlayerData mmoPlayerData) {
      return new ProfileListImpl(mmoPlayerData);
   }

   public Object newProfileDataModule() {
      throw new RuntimeException("Not supported");
   }
}
