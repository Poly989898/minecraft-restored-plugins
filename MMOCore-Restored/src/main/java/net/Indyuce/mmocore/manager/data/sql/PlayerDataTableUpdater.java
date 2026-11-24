package net.Indyuce.mmocore.manager.data.sql;

import io.lumine.mythic.lib.data.sql.SQLDataSource;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.manager.data.yaml.YAMLPlayerDataHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class PlayerDataTableUpdater {
   private final PlayerData playerData;
   private final SQLDataSource provider;
   private final UUID effectiveId;
   private final Map<String, String> requestMap = new HashMap<>();

   public PlayerDataTableUpdater(SQLDataSource provider, PlayerData playerData) {
      this.playerData = playerData;
      this.provider = provider;
      this.effectiveId = playerData.getEffectiveId();
   }

   public void executeRequest(boolean autosave) {
      String request = "INSERT INTO mmocore_playerdata(uuid, "
         + this.formatCollection(this.requestMap.keySet(), false)
         + ") VALUES('"
         + this.effectiveId
         + "',"
         + this.formatCollection(this.requestMap.values(), true)
         + ") ON DUPLICATE KEY UPDATE "
         + this.formatMap()
         + ";";

      try {
         Connection connection = this.provider.getConnection();

         try {
            PreparedStatement statement = connection.prepareStatement(request);

            try {
               statement.executeUpdate();
            } catch (SQLException exception) {
               MMOCore.log(Level.WARNING, "Could not save player data of " + this.effectiveId + ", saving through YAML instead");
               new YAMLPlayerDataHandler(MMOCore.plugin).saveData(this.playerData, autosave);
               exception.printStackTrace();
            } finally {
               statement.close();
            }
         } catch (SQLException exception) {
            MMOCore.log(Level.WARNING, "Could not save player data of " + this.effectiveId + ", saving through YAML instead");
            new YAMLPlayerDataHandler(MMOCore.plugin).saveData(this.playerData, autosave);
            exception.printStackTrace();
         } finally {
            connection.close();
         }
      } catch (SQLException exception) {
         MMOCore.log(Level.WARNING, "Could not save player data of " + this.effectiveId + ", saving through YAML instead");
         new YAMLPlayerDataHandler(MMOCore.plugin).saveData(this.playerData, autosave);
         exception.printStackTrace();
      }
   }

   public void addData(@NotNull String key, @Nullable Object value) {
      this.requestMap.put(key, String.valueOf(value));
   }

   public String formatCollection(Collection<String> strings, boolean withComma) {
      StringBuilder values = new StringBuilder();

      for (String key : strings) {
         if (withComma) {
            values.append("'");
         }

         values.append(key);
         if (withComma) {
            values.append("'");
         }

         values.append(",");
      }

      values.deleteCharAt(values.length() - 1);
      return values.toString();
   }

   public String formatMap() {
      StringBuilder values = new StringBuilder();

      for (String key : this.requestMap.keySet()) {
         values.append(key).append("='").append(this.requestMap.get(key)).append("',");
      }

      values.deleteCharAt(values.length() - 1);
      return values.toString();
   }

   public void addJSONArray(String key, Collection<String> collection) {
      JsonArray json = new JsonArray();

      for (String s : collection) {
         json.add(s);
      }

      this.addData(key, json.toString());
   }

   public <T, V> void addJSONObject(String key, Set<Entry<T, V>> collection) {
      JsonObject json = new JsonObject();

      for (Entry<T, V> entry : collection) {
         json.addProperty(entry.getKey().toString(), entry.getValue().toString());
      }

      this.addData(key, json.toString());
   }
}
