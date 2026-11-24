package fr.phoenixdevt.mmoprofiles.shared.player;

import com.google.gson.JsonObject;
import fr.phoenixdevt.mmoprofiles.shared.request.PlayerChoseProfileRequest;
import fr.phoenixdevt.mmoprofiles.shared.request.Request;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProxiedPlayerData {
   private final UUID officialId;
   @Nullable
   private UUID profileId;
   @Nullable
   private Request currentRequest;

   public ProxiedPlayerData(@NotNull UUID officialId) {
      this.officialId = Objects.requireNonNull(officialId);
   }

   @NotNull
   public UUID getOfficialId() {
      return this.officialId;
   }

   public boolean hasChosenProfile() {
      return this.profileId != null;
   }

   @Nullable
   public UUID getProfileId() {
      return this.profileId;
   }

   @NotNull
   public Request getCurrentRequest() {
      return Objects.requireNonNull(this.currentRequest, "Player has no current request");
   }

   public boolean isOnTemporaryServer() {
      return this.hasCurrentRequest(PlayerChoseProfileRequest.class) && ((PlayerChoseProfileRequest)this.currentRequest).usesTemporaryServer();
   }

   public boolean hasCurrentRequest(@NotNull Class<? extends Request> requestClass) {
      Validate.notNull(requestClass, "Request class cannot be null");
      return this.currentRequest != null && requestClass.isInstance(this.currentRequest);
   }

   public void setCurrentRequest(@Nullable Request currentRequest) {
      this.currentRequest = currentRequest;
   }

   public void setProfileId(@Nullable UUID profileId) {
      this.profileId = profileId;
   }

   @NotNull
   public JsonObject toJson() {
      JsonObject object = new JsonObject();
      object.addProperty("OfficialId", this.officialId.toString());
      if (this.profileId != null) {
         object.addProperty("ProfileId", this.profileId.toString());
      }

      if (this.currentRequest != null) {
         object.add("CurrentRequest", this.currentRequest.toJson());
      }

      return object;
   }

   @NotNull
   public static ProxiedPlayerData fromJson(@NotNull JsonObject object) {
      ProxiedPlayerData data = new ProxiedPlayerData(UUID.fromString(object.get("OfficialId").getAsString()));
      if (object.has("ProfileId")) {
         data.profileId = UUID.fromString(object.get("ProfileId").getAsString());
      }

      if (object.has("CurrentRequest")) {
         data.currentRequest = Request.fromJson(object.getAsJsonObject("CurrentRequest"));
      }

      return data;
   }
}
