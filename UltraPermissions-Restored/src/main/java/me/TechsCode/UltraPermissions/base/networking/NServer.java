package me.TechsCode.UltraPermissions.base.networking;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class NServer {
   private String name;
   private String ip;
   private int port;
   private List<NPlayer> players;

   public NServer(String text, String ip, int count, List<NPlayer> nPlayers) {
      this.name = text;
      this.ip = ip;
      this.port = count;
      this.players = nPlayers;
   }

   public String getName() {
      return this.name;
   }

   public String getIp() {
      return this.ip;
   }

   public int getPort() {
      return this.port;
   }

   public List<NPlayer> getPlayers() {
      return this.players;
   }

   public JsonObject toJsonObject() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("name", this.name);
      jsonObject.addProperty("ip", this.ip);
      jsonObject.addProperty("port", this.port);
      JsonArray jsonArray = new JsonArray();
      this.players.forEach(nPlayer -> jsonArray.add(nPlayer.toJsonObject()));
      jsonObject.add("players", jsonArray);
      return jsonObject;
   }

   public static NServer fromJsonObject(JsonObject jsonObject) {
      String asString = jsonObject.get("name").getAsString();
      String ip = jsonObject.get("ip").getAsString();
      int asInt = jsonObject.get("port").getAsInt();
      NServer nServer = new NServer(asString, ip, asInt, new ArrayList<>());
      jsonObject.get("players").getAsJsonArray().forEach(jsonElement -> nServer.players.add(NPlayer.fromJsonObject((JsonObject)jsonElement, nServer)));
      return nServer;
   }
}
