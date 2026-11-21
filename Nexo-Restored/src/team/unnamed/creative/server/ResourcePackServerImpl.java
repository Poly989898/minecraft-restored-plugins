package team.unnamed.creative.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.Executor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.server.request.ResourcePackDownloadRequest;
import team.unnamed.creative.server.util.ResourcePackDownloadRequestParser;

final class ResourcePackServerImpl implements ResourcePackServer {
   private final HttpServer server;
   private final team.unnamed.creative.server.handler.ResourcePackRequestHandler handler;

   ResourcePackServerImpl(@NotNull HttpServer server, @NotNull String path, team.unnamed.creative.server.handler.@NotNull ResourcePackRequestHandler handler) {
      this.server = (HttpServer)Objects.requireNonNull(server, "server");
      this.handler = (team.unnamed.creative.server.handler.ResourcePackRequestHandler)Objects.requireNonNull(handler, "handler");
      this.server.createContext((String)Objects.requireNonNull(path, "path"), this::handleRequest);
   }

   /** @deprecated */
   @Deprecated
   public @NotNull HttpServer httpServer() {
      return this.server;
   }

   public @NotNull InetSocketAddress address() {
      return this.server.getAddress();
   }

   public void start() {
      this.server.start();
   }

   public void stop(int delay) {
      this.server.stop(delay);
   }

   private void handleRequest(@NotNull HttpExchange exchange) throws IOException {
      if (!"GET".equals(exchange.getRequestMethod())) {
         exchange.close();
      } else {
         Headers headers = exchange.getRequestHeaders();
         ResourcePackDownloadRequest request = ResourcePackDownloadRequestParser.parse(headers);
         HttpExchange var4 = exchange;

         try {
            this.handler.onRequest(request, exchange);
         } catch (Throwable var8) {
            if (exchange != null) {
               try {
                  var4.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (exchange != null) {
            exchange.close();
         }

      }
   }

   static final class BuilderImpl implements ResourcePackServer.Builder {
      private InetSocketAddress address;
      private Executor executor;
      private int backlog;
      private team.unnamed.creative.server.handler.ResourcePackRequestHandler handler;
      private String path = "/";
      private HttpServerFactory serverFactory = HttpServer::create;

      public ResourcePackServer.@NotNull Builder address(@NotNull InetSocketAddress address) {
         this.address = (InetSocketAddress)Objects.requireNonNull(address, "address");
         return this;
      }

      public ResourcePackServer.@NotNull Builder executor(@Nullable Executor executor) {
         this.executor = executor;
         return this;
      }

      public ResourcePackServer.@NotNull Builder secure(@NotNull HttpsConfigurator httpsConfigurator) {
         Objects.requireNonNull(httpsConfigurator, "httpsConfigurator");
         this.serverFactory = (address, backlog) -> {
            HttpsServer server = HttpsServer.create(address, backlog);
            server.setHttpsConfigurator(httpsConfigurator);
            return server;
         };
         return this;
      }

      public ResourcePackServer.@NotNull Builder backlog(int backlog) {
         this.backlog = backlog;
         return this;
      }

      public ResourcePackServer.@NotNull Builder handler(team.unnamed.creative.server.handler.@NotNull ResourcePackRequestHandler handler) {
         this.handler = (team.unnamed.creative.server.handler.ResourcePackRequestHandler)Objects.requireNonNull(handler, "handler");
         return this;
      }

      public ResourcePackServer.@NotNull Builder path(@NotNull String path) {
         this.path = (String)Objects.requireNonNull(path, "path");
         return this;
      }

      public @NotNull ResourcePackServer build() throws IOException {
         HttpServer server = this.serverFactory.create(this.address, this.backlog);
         server.setExecutor(this.executor);
         return new ResourcePackServerImpl(server, this.path, this.handler);
      }
   }

   interface HttpServerFactory {
      HttpServer create(InetSocketAddress var1, int var2) throws IOException;
   }
}
