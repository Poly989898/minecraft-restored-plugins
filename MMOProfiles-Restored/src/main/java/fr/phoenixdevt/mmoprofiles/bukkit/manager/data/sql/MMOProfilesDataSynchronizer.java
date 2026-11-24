package fr.phoenixdevt.mmoprofiles.bukkit.manager.data.sql;

import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.data.sql.SQLDataSynchronizer;
import io.lumine.mythic.lib.gson.JsonObject;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MMOProfilesDataSynchronizer extends SQLDataSynchronizer<ProfileListImpl> {
   public MMOProfilesDataSynchronizer(SQLDataHandler handler, ProfileListImpl data) {
      super("mmoprofiles_playerdata", "uuid", handler.getDataSource(), data, true);
   }

   public void loadData(ResultSet result) throws SQLException {
      String data = result.getString("data");
      JsonObject jsonObject = data != null && !data.isEmpty() ? (JsonObject)MythicLib.plugin.getGson().fromJson(data, JsonObject.class) : new JsonObject();
      ((ProfileListImpl)this.getData()).loadFromJson(jsonObject);
   }

   public void loadEmptyData() {
      ((ProfileListImpl)this.getData()).loadFromJson(new JsonObject());
   }
}
