package io.lumine.mythic.lib.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BukkitFuture<T> {
   private final Plugin plugin;
   private final CompletableFuture<T> wrapped;

   public BukkitFuture(@NotNull Plugin plugin) {
      this(plugin, new CompletableFuture<>());
   }

   public BukkitFuture(@NotNull Plugin plugin, @NotNull CompletableFuture<T> wrapped) {
      this.plugin = plugin;
      this.wrapped = wrapped;
   }

   @NotNull
   public BukkitFuture<Void> thenAccept(Consumer<T> consumer) {
      return new BukkitFuture<>(this.plugin, this.wrapped.thenAccept(Tasks.sync(this.plugin, consumer)));
   }

   @NotNull
   public BukkitFuture<Void> thenAcceptAsync(Consumer<T> consumer) {
      return new BukkitFuture<>(this.plugin, this.wrapped.thenAccept(t -> Tasks.runAsync(this.plugin, () -> consumer.accept((T)t))));
   }

   public boolean complete(T t) {
      return this.wrapped.complete(t);
   }
}
