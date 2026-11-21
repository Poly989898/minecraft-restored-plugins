package team.unnamed.creative.server.handler;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.server.request.ResourcePackDownloadRequest;

@FunctionalInterface
public interface ResourcePackRequestHandler {
   @Contract("_, _ -> new")
   static @NotNull ResourcePackRequestHandler fixed(@NotNull BuiltResourcePack pack, boolean validOnly) {
      return new FixedResourcePackRequestHandler(pack, validOnly);
   }

   @Contract("_ -> new")
   static @NotNull ResourcePackRequestHandler fixed(@NotNull BuiltResourcePack pack) {
      return fixed(pack, false);
   }

   void onRequest(@Nullable ResourcePackDownloadRequest var1, @NotNull HttpExchange var2) throws IOException;
}
