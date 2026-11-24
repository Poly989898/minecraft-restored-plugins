package fr.phoenixdevt.mmoprofiles.shared.request;

import com.google.gson.JsonObject;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class PlayerUnselectProfileRequest extends Request {
   private final UUID playerId;

   public PlayerUnselectProfileRequest(@NotNull UUID playerId) {
      this.playerId = playerId;
   }

   @NotNull
   @Override
   public RequestType getType() {
      return RequestType.PLAYER_UNSELECT_PROFILE;
   }

   public PlayerUnselectProfileRequest(JsonObject object) {
      this.playerId = UUID.fromString(object.get("PlayerId").getAsString());
   }

   @Override
   protected void fillJsonObject(@NotNull JsonObject requestObject) {
      requestObject.addProperty("PlayerId", this.playerId.toString());
   }

   @NotNull
   public UUID getPlayerId() {
      return this.playerId;
   }
}
