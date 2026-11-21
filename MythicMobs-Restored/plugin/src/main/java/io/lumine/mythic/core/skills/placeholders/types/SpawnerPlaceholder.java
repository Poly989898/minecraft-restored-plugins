package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.function.BiFunction;

@Deprecated
public class SpawnerPlaceholder implements Placeholder, BiFunction<MythicSpawner, String, String> {
   private final BiFunction<MythicSpawner, String, String> transformer;

   public SpawnerPlaceholder(BiFunction<MythicSpawner, String, String> transformer) {
      this.transformer = transformer;
   }

   public String apply(MythicSpawner spawner, String conditional) {
      return this.transformer.apply(spawner, conditional);
   }
}
