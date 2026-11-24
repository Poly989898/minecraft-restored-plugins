package fr.phoenixdevt.mmoprofiles.shared.request;

import com.google.gson.JsonObject;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class ProfileRequest extends Request {
   private final RequestType type;
   private final UUID playerId;
   private final UUID profileId;

   public ProfileRequest(@NotNull RequestType type, @NotNull UUID playerId, @NotNull UUID profileId) {
      this.type = type;
      this.playerId = playerId;
      this.profileId = profileId;
   }

   public ProfileRequest(JsonObject object) {
      this.type = RequestType.valueOf(object.get("RequestType").getAsString());
      this.playerId = UUID.fromString(object.get("PlayerId").getAsString());
      this.profileId = UUID.fromString(object.get("ProfileId").getAsString());
   }

   @Override
   protected void fillJsonObject(@NotNull JsonObject requestObject) {
      requestObject.addProperty("RequestType", this.type.name());
      requestObject.addProperty("PlayerId", this.playerId.toString());
      requestObject.addProperty("ProfileId", this.profileId.toString());
   }

   @NotNull
   public UUID getPlayerId() {
      return this.playerId;
   }

   @NotNull
   public UUID getProfileId() {
      return this.profileId;
   }

   @NotNull
   @Override
   public RequestType getType() {
      return this.type;
   }
}
