package fr.phoenixdevt.mmoprofiles.shared.request;

import com.google.gson.JsonObject;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class ProfileSelectionFallbackRequest extends Request {
   private final UUID playerId;
   private final String fallbackServer;

   public ProfileSelectionFallbackRequest(@NotNull UUID playerId, @NotNull String fallbackServer) {
      this.playerId = playerId;
      this.fallbackServer = fallbackServer;
   }

   @NotNull
   @Override
   public RequestType getType() {
      return RequestType.PROFILE_SELECTION_FALLBACK;
   }

   public ProfileSelectionFallbackRequest(JsonObject object) {
      this.playerId = UUID.fromString(object.get("PlayerId").getAsString());
      this.fallbackServer = object.get("TargetServer").getAsString();
   }

   @Override
   protected void fillJsonObject(@NotNull JsonObject requestObject) {
      requestObject.addProperty("PlayerId", this.playerId.toString());
      requestObject.addProperty("TargetServer", this.fallbackServer);
   }

   @NotNull
   public UUID getPlayerId() {
      return this.playerId;
   }

   @NotNull
   public String getFallbackServer() {
      return this.fallbackServer;
   }
}
