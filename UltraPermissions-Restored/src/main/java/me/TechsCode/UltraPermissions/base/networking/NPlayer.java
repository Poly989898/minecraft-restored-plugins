package me.TechsCode.UltraPermissions.base.networking;

import java.util.UUID;
import com.google.gson.JsonObject;

public class NPlayer {
   private UUID uuid;
   private String name;
   private NServer server;

   public NPlayer(UUID uuid, String text, NServer nServer) {
      this.uuid = uuid;
      this.name = text;
      this.server = nServer;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public String getName() {
      return this.name;
   }

   public NServer getServer() {
      return this.server;
   }

   public JsonObject toJsonObject() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("uuid", this.uuid.toString());
      jsonObject.addProperty("name", this.name);
      return jsonObject;
   }

   public static NPlayer fromJsonObject(JsonObject jsonObject, NServer nServer) {
      UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
      String asString = jsonObject.get("name").getAsString();
      return new NPlayer(uuid, asString, nServer);
   }
}
