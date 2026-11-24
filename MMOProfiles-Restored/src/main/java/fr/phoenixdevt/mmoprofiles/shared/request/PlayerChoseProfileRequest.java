package fr.phoenixdevt.mmoprofiles.shared.request;

import com.google.gson.JsonObject;
import fr.phoenixdevt.mmoprofiles.shared.utils.SharedUtils;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerChoseProfileRequest extends ProfileRequest {
   private final List<String> targetServers;
   @Nullable
   private final Long tempServerDelay;
   @Nullable
   private final List<String> sharedPermissions;
   @Nullable
   private String originalServer;

   public PlayerChoseProfileRequest(
      @NotNull UUID playerId,
      @NotNull UUID profileId,
      @NotNull List<String> targetServers,
      @Nullable Long tempServerDelay,
      @Nullable List<String> sharedPermissions
   ) {
      super(RequestType.PLAYER_CHOSE_PROFILE, playerId, profileId);
      this.targetServers = targetServers;
      this.tempServerDelay = tempServerDelay;
      this.sharedPermissions = sharedPermissions;
   }

   public PlayerChoseProfileRequest(@NotNull JsonObject object) {
      super(object);
      this.targetServers = SharedUtils.toStringList(object.get("TargetServers").getAsJsonArray());
      this.tempServerDelay = object.has("TempServerDelay") ? object.get("TempServerDelay").getAsLong() : null;
      this.sharedPermissions = object.has("SharedPermissions") ? SharedUtils.toStringList(object.get("SharedPermissions").getAsJsonArray()) : null;
      if (object.has("OriginalServer")) {
         this.originalServer = object.get("OriginalServer").getAsString();
      }
   }

   @Override
   protected void fillJsonObject(@NotNull JsonObject requestObject) {
      super.fillJsonObject(requestObject);
      requestObject.add("TargetServers", SharedUtils.toArray(this.targetServers));
      if (this.tempServerDelay != null) {
         requestObject.addProperty("TempServerDelay", this.tempServerDelay);
      }

      if (this.sharedPermissions != null) {
         requestObject.add("SharedPermissions", SharedUtils.toArray(this.sharedPermissions));
      }

      if (this.originalServer != null) {
         requestObject.addProperty("OriginalServer", this.originalServer);
      }
   }

   @NotNull
   public List<String> getTargetServers() {
      return this.targetServers;
   }

   public boolean usesTemporaryServer() {
      return this.tempServerDelay != null;
   }

   @Nullable
   public List<String> getSharedPermissions() {
      return this.sharedPermissions;
   }

   @NotNull
   public String getOriginalServer() {
      return Objects.requireNonNull(this.originalServer, "Proxy has not provided original server name yet");
   }

   @Nullable
   public Long getTemporaryServerDelay() {
      return Objects.requireNonNull(this.tempServerDelay);
   }

   public void setOriginalServer(@Nullable String originalServer) {
      this.originalServer = originalServer;
   }
}
