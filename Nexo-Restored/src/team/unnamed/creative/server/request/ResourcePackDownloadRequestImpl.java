package team.unnamed.creative.server.request;

import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record ResourcePackDownloadRequestImpl(UUID uuid, String username, String clientVersion, String clientVersionId, int packFormat) implements ResourcePackDownloadRequest {
   ResourcePackDownloadRequestImpl(final @NotNull UUID uuid, final @NotNull String username, final @NotNull String clientVersion, final @NotNull String clientVersionId, final int packFormat) {
      this.uuid = (UUID)Objects.requireNonNull(uuid, "uuid");
      this.username = (String)Objects.requireNonNull(username, "username");
      this.clientVersion = (String)Objects.requireNonNull(clientVersion, "clientVersion");
      this.clientVersionId = (String)Objects.requireNonNull(clientVersionId, "clientVersionId");
      this.packFormat = packFormat;
   }

   public @NotNull UUID uuid() {
      return this.uuid;
   }

   public @NotNull String username() {
      return this.username;
   }

   public @NotNull String clientVersion() {
      return this.clientVersion;
   }

   public @NotNull String clientVersionId() {
      return this.clientVersionId;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{uuid=" + String.valueOf(this.uuid) + ", username=" + this.username + ", clientVersion=" + this.clientVersion + ", clientVersionId=" + this.clientVersionId + ", packFormat=" + this.packFormat + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ResourcePackDownloadRequestImpl that = (ResourcePackDownloadRequestImpl)o;
         return this.packFormat == that.packFormat && this.uuid.equals(that.uuid) && this.username.equals(that.username) && this.clientVersion.equals(that.clientVersion) && this.clientVersionId.equals(that.clientVersionId);
      } else {
         return false;
      }
   }
}
