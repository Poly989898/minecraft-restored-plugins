package me.TechsCode.UltraPermissions.base.networking;

import java.util.ArrayList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ServerList extends ArrayList<NServer> {
   public ServerList() {
   }

   public ServerList(JsonArray jsonArray) {
      jsonArray.forEach(jsonElement -> this.add(NServer.fromJsonObject((JsonObject)jsonElement)));
   }

   public JsonArray toJsonArray() {
      JsonArray jsonArray = new JsonArray();

      for (NServer thi : this) {
         jsonArray.add(thi.toJsonObject());
      }

      return jsonArray;
   }
}
