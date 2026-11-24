package fr.phoenixdevt.mmoprofiles.bukkit.manager.data.sql;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.data.DefaultOfflineDataHolder;
import io.lumine.mythic.lib.data.sql.SQLDataSource;
import io.lumine.mythic.lib.data.sql.SQLSynchronizedDataHandler;
import java.util.UUID;

public class SQLDataHandler extends SQLSynchronizedDataHandler<ProfileListImpl, DefaultOfflineDataHolder> {
   public SQLDataHandler(SQLDataSource dataSource) {
      super(dataSource);
   }

   public void setup() {
      this.getDataSource()
         .executeUpdateAsync("CREATE TABLE IF NOT EXISTS mmoprofiles_playerdata(uuid VARCHAR(36),data LONGTEXT,is_saved TINYINT,PRIMARY KEY (uuid));");
   }

   public void saveData(ProfileListImpl data, boolean autosave) {
      MySQLTableEditor sql = new MySQLTableEditor(MySQLTableEditor.Table.PLAYERDATA, data.getEffectiveId(), this.getDataSource());
      UtilityMethods.debug(MMOProfiles.plugin, "SQL", "Saving data for '" + data.getEffectiveId() + "'...");
      MySQLRequest request = new MySQLRequest(data.getEffectiveId());
      request.addData("data", data.toJson().toString());
      request.addData("is_saved", autosave ? 0 : 1);
      sql.updateData(request);
      UtilityMethods.debug(MMOProfiles.plugin, "SQL", "Saved data for '" + data.getEffectiveId() + "'");
   }

   public DefaultOfflineDataHolder getOffline(UUID uuid) {
      return new DefaultOfflineDataHolder(uuid);
   }

   public MMOProfilesDataSynchronizer newDataSynchronizer(ProfileListImpl data) {
      return new MMOProfilesDataSynchronizer(this, data);
   }
}
