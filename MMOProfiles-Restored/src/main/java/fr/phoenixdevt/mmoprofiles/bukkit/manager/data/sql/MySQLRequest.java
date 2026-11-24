package fr.phoenixdevt.mmoprofiles.bukkit.manager.data.sql;

import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

public class MySQLRequest {
   private final Map<String, String> requestMap = new HashMap<>();
   private final UUID uuid;

   public MySQLRequest(UUID uuid) {
      this.uuid = uuid;
   }

   public void addData(String key, Object value) {
      this.requestMap.put(key, "" + value);
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
         values.append(key);
         values.append("=");
         values.append("'");
         values.append(this.requestMap.get(key));
         values.append("'");
         values.append(",");
      }

      values.deleteCharAt(values.length() - 1);
      return values.toString();
   }

   public String getRequestString() {
      return "(uuid, "
         + this.formatCollection(this.requestMap.keySet(), false)
         + ") VALUES('"
         + this.uuid
         + "',"
         + this.formatCollection(this.requestMap.values(), true)
         + ") ON DUPLICATE KEY UPDATE "
         + this.formatMap()
         + ";";
   }

   public void addJSONArray(String key, Collection<String> collection) {
      JsonArray json = new JsonArray();

      for (String s : collection) {
         json.add(s);
      }

      this.addData(key, json.toString());
   }

   public void addJSONObject(String key, Set<Entry<String, String>> collection) {
      JsonObject json = new JsonObject();

      for (Entry<String, String> entry : collection) {
         json.addProperty(entry.getKey(), entry.getValue());
      }

      this.addData(key, json.toString());
   }
}
