package io.lumine.mythic.bukkit.utils;

import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

public final class Services {
   @Nonnull
   public static <T> T load(@Nonnull Class<T> clazz) {
      Objects.requireNonNull(clazz, "clazz");
      return get(clazz).orElseThrow(() -> new IllegalStateException("No registration present for service '" + clazz.getName() + "'"));
   }

   @Nonnull
   public static <T> Optional<T> get(@Nonnull Class<T> clazz) {
      Objects.requireNonNull(clazz, "clazz");
      RegisteredServiceProvider<T> registration = Bukkit.getServicesManager().getRegistration(clazz);
      return registration == null ? Optional.empty() : Optional.ofNullable((T)registration.getProvider());
   }

   @Nonnull
   public static <T> T provide(@Nonnull Class<T> clazz, @Nonnull T instance, @Nonnull Plugin plugin, @Nonnull ServicePriority priority) {
      Objects.requireNonNull(clazz, "clazz");
      Objects.requireNonNull(instance, "instance");
      Objects.requireNonNull(plugin, "plugin");
      Objects.requireNonNull(priority, "priority");
      Bukkit.getServicesManager().register(clazz, instance, plugin, priority);
      return instance;
   }

   @Nonnull
   public static <T> T provide(@Nonnull Class<T> clazz, @Nonnull T instance, @Nonnull ServicePriority priority) {
      return provide(clazz, instance, LoaderUtils.getPlugin(), priority);
   }

   @Nonnull
   public static <T> T provide(@Nonnull Class<T> clazz, @Nonnull T instance) {
      return provide(clazz, instance, ServicePriority.Normal);
   }

   private Services() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
