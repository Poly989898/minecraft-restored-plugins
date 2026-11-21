package team.unnamed.creative.server.request;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface ResourcePackDownloadRequest {
   static @NotNull ResourcePackDownloadRequest request(@NotNull UUID uuid, @NotNull String username, @NotNull String clientVersion, @NotNull String clientVersionId, int packFormat) {
      return new ResourcePackDownloadRequestImpl(uuid, username, clientVersion, clientVersionId, packFormat);
   }

   @NotNull UUID uuid();

   @NotNull String username();

   @NotNull String clientVersion();

   @NotNull String clientVersionId();

   int packFormat();
}
