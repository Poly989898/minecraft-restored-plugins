package me.TechsCode.UltraPermissions;

import java.util.UUID;
import me.TechsCode.UltraPermissions.base.registry.RegistryStorable;
import com.google.gson.JsonObject;

public class PrivateRegistry extends RegistryStorable {
   private UUID serverIdentifier = UUID.randomUUID();

   public PrivateRegistry() {
      super("private");
   }

   @Override
   public void setState(JsonObject jsonObject) {
      this.serverIdentifier = UUID.fromString(jsonObject.get("serverIdentifier").getAsString());
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("serverIdentifier", this.serverIdentifier.toString());
      return jsonObject;
   }

   public UUID getServerIdentifier() {
      return this.serverIdentifier;
   }

   public void setServerIdentifier(UUID uuid) {
      this.serverIdentifier = uuid;
      this.sync();
   }
}
