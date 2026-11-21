package team.unnamed.creative.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.net.ssl.SSLContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.BuiltResourcePack;

public interface ResourcePackServer {
   static @NotNull Builder server() {
      return new ResourcePackServerImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("-> new")
   static @NotNull Builder builder() {
      return server();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @NotNull HttpServer httpServer();

   @NotNull InetSocketAddress address();

   void start();

   void stop(int var1);

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder address(@NotNull InetSocketAddress var1);

      @Contract("_, _ -> this")
      default @NotNull Builder address(@NotNull String hostname, int port) {
         Objects.requireNonNull(hostname, "hostname");
         return this.address(new InetSocketAddress(hostname, port));
      }

      @Contract("_, _ -> this")
      default @NotNull Builder address(@NotNull InetAddress address, int port) {
         Objects.requireNonNull(address, "address");
         return this.address(new InetSocketAddress(address, port));
      }

      @Contract("_ -> this")
      default @NotNull Builder address(int port) {
         return this.address(new InetSocketAddress(port));
      }

      @Contract("_ -> this")
      @NotNull Builder executor(@Nullable Executor var1);

      @Contract("_ -> this")
      @NotNull Builder secure(@NotNull HttpsConfigurator var1);

      @Contract("_, _ -> this")
      default @NotNull Builder secure(@NotNull SSLContext sslContext, final @NotNull Consumer<@NotNull HttpsParameters> configurator) {
         Objects.requireNonNull(sslContext, "sslContext");
         Objects.requireNonNull(configurator, "configurator");
         return this.secure(new HttpsConfigurator(sslContext) {
            public void configure(@NotNull HttpsParameters params) {
               configurator.accept(params);
            }

            public @NotNull String toString() {
               return "ResourcePackServer/HttpsConfigurator for " + String.valueOf(configurator);
            }
         });
      }

      @Contract("_ -> this")
      default @NotNull Builder secure(@NotNull SSLContext sslContext) {
         Objects.requireNonNull(sslContext, "sslContext");
         return this.secure(new HttpsConfigurator(sslContext));
      }

      @Contract("_ -> this")
      @NotNull Builder backlog(int var1);

      @NotNull Builder handler(team.unnamed.creative.server.handler.@NotNull ResourcePackRequestHandler var1);

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder handler(@NotNull ResourcePackRequestHandler handler) {
         return this.handler((team.unnamed.creative.server.handler.ResourcePackRequestHandler)Objects.requireNonNull(handler, "handler"));
      }

      @Contract("_, _ -> this")
      default @NotNull Builder pack(@NotNull BuiltResourcePack pack, boolean validOnly) {
         Objects.requireNonNull(pack, "pack");
         return this.handler(team.unnamed.creative.server.handler.ResourcePackRequestHandler.fixed(pack, validOnly));
      }

      @Contract("_ -> this")
      default @NotNull Builder pack(@NotNull BuiltResourcePack pack) {
         Objects.requireNonNull(pack, "pack");
         return this.handler(team.unnamed.creative.server.handler.ResourcePackRequestHandler.fixed(pack));
      }

      @Contract("_ -> this")
      @NotNull Builder path(@NotNull String var1);

      @Contract("-> new")
      @NotNull ResourcePackServer build() throws IOException;
   }
}
