package team.unnamed.creative.server;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.server.request.ResourcePackDownloadRequest;

/** @deprecated */
@Deprecated
@FunctionalInterface
@ScheduledForRemoval(
   inVersion = "2.0.0"
)
public interface ResourcePackRequestHandler extends team.unnamed.creative.server.handler.ResourcePackRequestHandler {
   void onRequest(ResourcePackRequest var1, HttpExchange var2) throws IOException;

   default void onRequest(@Nullable ResourcePackDownloadRequest request, @NotNull HttpExchange exchange) throws IOException {
      try {
         HttpExchange var3 = exchange;

         try {
            if (request == null) {
               this.onInvalidRequest(exchange);
            } else {
               this.onRequest(new ResourcePackRequest(request.uuid(), request.username(), request.clientVersion(), request.clientVersionId(), request.packFormat()), exchange);
            }
         } catch (Throwable var7) {
            if (exchange != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (exchange != null) {
            exchange.close();
         }
      } catch (Exception e) {
         this.onException(e);
      }

   }

   default void onException(Exception e) {
      System.err.println("Exception caught when serving a resource-pack");
      e.printStackTrace();
   }

   default void onInvalidRequest(HttpExchange exchange) throws IOException {
      byte[] response = "Please use a Minecraft client\n".getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().set("Content-Type", "text/plain");
      exchange.sendResponseHeaders(400, (long)response.length);
      OutputStream responseStream = exchange.getResponseBody();

      try {
         responseStream.write(response);
      } catch (Throwable var7) {
         if (responseStream != null) {
            try {
               responseStream.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (responseStream != null) {
         responseStream.close();
      }

   }

   static ResourcePackRequestHandler of(final BuiltResourcePack pack, final boolean validOnly) {
      return new ResourcePackRequestHandler() {
         public void onRequest(@Nullable ResourcePackRequest request, HttpExchange exchange) throws IOException {
            if (request == null && validOnly) {
               ResourcePackRequestHandler.super.onInvalidRequest(exchange);
            } else {
               byte[] data = pack.data().toByteArray();
               exchange.getResponseHeaders().set("Content-Type", "application/zip");
               exchange.sendResponseHeaders(200, (long)data.length);
               OutputStream responseStream = exchange.getResponseBody();

               try {
                  responseStream.write(data);
               } catch (Throwable var8) {
                  if (responseStream != null) {
                     try {
                        responseStream.close();
                     } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                     }
                  }

                  throw var8;
               }

               if (responseStream != null) {
                  responseStream.close();
               }
            }

         }

         public void onInvalidRequest(HttpExchange exchange) throws IOException {
            this.onRequest((ResourcePackRequest)null, exchange);
         }

         public @NotNull String toString() {
            String var10000 = String.valueOf(pack);
            return "BuiltResourcePackRequestHandler{pack=" + var10000 + ", validOnly=" + validOnly + "}";
         }
      };
   }

   static ResourcePackRequestHandler of(BuiltResourcePack pack) {
      return of(pack, false);
   }
}
