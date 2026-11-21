package team.unnamed.creative.server.util;

import com.sun.net.httpserver.Headers;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.server.request.ResourcePackDownloadRequest;

@Internal
public final class ResourcePackDownloadRequestParser {
   private ResourcePackDownloadRequestParser() {
      throw new UnsupportedOperationException("Can't instantiate utility class");
   }

   public static @Nullable ResourcePackDownloadRequest parse(@NotNull Headers headers) {
      String username = headers.getFirst("X-Minecraft-Username");
      String rawUuid = headers.getFirst("X-Minecraft-UUID");
      String clientVersion = headers.getFirst("X-Minecraft-Version");
      String clientVersionId = headers.getFirst("X-Minecraft-Version-ID");
      String rawPackFormat = headers.getFirst("X-Minecraft-Pack-Format");
      if (username != null && rawUuid != null && clientVersion != null && clientVersionId != null && rawPackFormat != null) {
         UUID uuid;
         int packFormat;
         try {
            uuid = UndashedUUID.fromUndashedString(rawUuid);
            packFormat = Integer.parseInt(rawPackFormat);
         } catch (IllegalArgumentException var9) {
            return null;
         }

         return ResourcePackDownloadRequest.request(uuid, username, clientVersion, clientVersionId, packFormat);
      } else {
         return null;
      }
   }
}
