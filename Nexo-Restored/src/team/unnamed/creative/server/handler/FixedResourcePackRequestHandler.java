package team.unnamed.creative.server.handler;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.server.request.ResourcePackDownloadRequest;

final class FixedResourcePackRequestHandler implements ResourcePackRequestHandler {
   private final BuiltResourcePack pack;
   private final boolean validOnly;

   FixedResourcePackRequestHandler(@NotNull BuiltResourcePack pack, boolean validOnly) {
      this.pack = (BuiltResourcePack)Objects.requireNonNull(pack, "pack");
      this.validOnly = validOnly;
   }

   public void onRequest(@Nullable ResourcePackDownloadRequest request, @NotNull HttpExchange exchange) throws IOException {
      if (request == null && this.validOnly) {
         byte[] data = "Please use a Minecraft client\n".getBytes(StandardCharsets.UTF_8);
         exchange.getResponseHeaders().set("Content-Type", "text/plain");
         exchange.sendResponseHeaders(400, (long)data.length);
         OutputStream responseStream = exchange.getResponseBody();

         try {
            responseStream.write(data);
         } catch (Throwable var10) {
            if (responseStream != null) {
               try {
                  responseStream.close();
               } catch (Throwable var8) {
                  var10.addSuppressed(var8);
               }
            }

            throw var10;
         }

         if (responseStream != null) {
            responseStream.close();
         }

      } else {
         byte[] data = this.pack.data().toByteArray();
         exchange.getResponseHeaders().set("Content-Type", "application/zip");
         exchange.sendResponseHeaders(200, (long)data.length);
         OutputStream responseStream = exchange.getResponseBody();

         try {
            responseStream.write(data);
         } catch (Throwable var9) {
            if (responseStream != null) {
               try {
                  responseStream.close();
               } catch (Throwable var7) {
                  var9.addSuppressed(var7);
               }
            }

            throw var9;
         }

         if (responseStream != null) {
            responseStream.close();
         }

      }
   }

   public @NotNull String toString() {
      return "FixedResourcePackRequestHandler{pack=" + String.valueOf(this.pack) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         FixedResourcePackRequestHandler that = (FixedResourcePackRequestHandler)o;
         return this.pack.equals(that.pack);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.pack.hashCode();
   }
}
