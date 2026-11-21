package io.lumine.mythic.core.spawning.random;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class RandomSpawnGenerator implements Runnable, Terminable, TerminableConsumer {
   @Getter
   private final RandomSpawnerManager manager;
   private final CompositeTerminable registry;

   public RandomSpawnGenerator(RandomSpawnerManager manager) {
      this.manager = manager;
      this.registry = CompositeTerminable.create();
      if (ServerVersion.isFolia()) {
         Schedulers.sync().runRepeating(this, 0L, 1L).bindWith(this);
      } else {
         Schedulers.async().runRepeating(this, 0L, 1L).bindWith(this);
      }
   }

   public MythicBukkit getPlugin() {
      return this.manager.getPlugin();
   }

   @Override
   public void run() {
      this.tick();
   }

   @Override
   public void close() {
      this.registry.terminate();
   }

   public abstract void tick();

   @NotNull
   @Override
   public <T extends AutoCloseable> T bind(@NotNull T t) {
      this.registry.bind(t);
      return t;
   }
}
