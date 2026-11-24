package me.TechsCode.UltraPermissions.base.networking;

import com.google.gson.JsonObject;

public class NetworkData {
   private String proxyPluginVersion;
   private ServerList serverList;

   public NetworkData(String text, ServerList list) {
      this.proxyPluginVersion = text;
      this.serverList = list;
   }

   public String getProxyPluginVersion() {
      return this.proxyPluginVersion;
   }

   public ServerList getServerList() {
      return this.serverList;
   }

   public JsonObject toJsonObject() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("proxyVersion", this.proxyPluginVersion);
      jsonObject.add("serverList", this.serverList.toJsonArray());
      return jsonObject;
   }

   public static NetworkData fromJsonObject(JsonObject jsonObject) {
      String asString = jsonObject.get("proxyVersion").getAsString();
      ServerList list = new ServerList(jsonObject.getAsJsonArray("serverList"));
      return new NetworkData(asString, list);
   }
}
