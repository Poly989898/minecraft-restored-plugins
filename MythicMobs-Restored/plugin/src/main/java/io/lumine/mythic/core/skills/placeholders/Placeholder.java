package io.lumine.mythic.core.skills.placeholders;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.bukkit.utils.interfaces.TriFunction;
import io.lumine.mythic.core.logging.ClassInfo;
import io.lumine.mythic.core.skills.placeholders.all.FunctionalEntityPlaceholder;
import io.lumine.mythic.core.skills.placeholders.all.FunctionalMetaPlaceholder;
import io.lumine.mythic.core.skills.placeholders.all.FunctionalPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.EntityPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GeneralPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.LocationPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.ParentPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.SpawnerPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.TargetPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.WorldPlaceholder;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Placeholder {
   String deprecationMessage = "You should create a separate class for each placeholder, and register them via the CustomComponentRegistry https://git.lumine.io/mythiccraft/MythicMobs/-/wikis/API/Placeholders#registering-the-placeholders";

   @Deprecated(
      forRemoval = true
   )
   static GeneralPlaceholder general(Function<String, String> transformer) {
      ClassInfo.ofCaller()
         .setUsedMethod(Placeholder.class, Function.class)
         .warnClassIsUsingDeprecatedAPI(
            "You should create a separate class for each placeholder, and register them via the CustomComponentRegistry https://git.lumine.io/mythiccraft/MythicMobs/-/wikis/API/Placeholders#registering-the-placeholders"
         );
      return new FunctionalPlaceholder(transformer);
   }

   @Deprecated(
      forRemoval = true
   )
   static MetaPlaceholder meta(BiFunction<PlaceholderMeta, String, String> transformer) {
      ClassInfo.ofCaller()
         .setUsedMethod(Placeholder.class, BiFunction.class)
         .warnClassIsUsingDeprecatedAPI(
            "You should create a separate class for each placeholder, and register them via the CustomComponentRegistry https://git.lumine.io/mythiccraft/MythicMobs/-/wikis/API/Placeholders#registering-the-placeholders"
         );
      return new FunctionalMetaPlaceholder(transformer);
   }

   @Deprecated(
      forRemoval = true
   )
   static EntityPlaceholder entity(BiFunction<AbstractEntity, String, String> transformer) {
      ClassInfo.ofCaller()
         .setUsedMethod(Placeholder.class, BiFunction.class)
         .warnClassIsUsingDeprecatedAPI(
            "You should create a separate class for each placeholder, and register them via the CustomComponentRegistry https://git.lumine.io/mythiccraft/MythicMobs/-/wikis/API/Placeholders#registering-the-placeholders"
         );
      return new FunctionalEntityPlaceholder(transformer);
   }

   @Deprecated(
      forRemoval = true
   )
   static LocationPlaceholder location(BiFunction<AbstractLocation, String, String> transformer) {
      ClassInfo.ofCaller()
         .setUsedMethod(Placeholder.class, BiFunction.class)
         .warnClassIsUsingDeprecatedAPI(
            "You should create a separate class for each placeholder, and register them via the CustomComponentRegistry https://git.lumine.io/mythiccraft/MythicMobs/-/wikis/API/Placeholders#registering-the-placeholders"
         );
      return new LocationPlaceholder(transformer);
   }

   @Deprecated(
      forRemoval = true
   )
   static ParentPlaceholder parent(BiFunction<AbstractEntity, String, String> transformer) {
      ClassInfo.ofCaller()
         .setUsedMethod(Placeholder.class, BiFunction.class)
         .warnClassIsUsingDeprecatedAPI(
            "You should create a separate class for each placeholder, and register them via the CustomComponentRegistry https://git.lumine.io/mythiccraft/MythicMobs/-/wikis/API/Placeholders#registering-the-placeholders"
         );
      return new ParentPlaceholder(transformer);
   }

   @Deprecated(
      forRemoval = true
   )
   static SpawnerPlaceholder spawner(BiFunction<MythicSpawner, String, String> transformer) {
      ClassInfo.ofCaller()
         .setUsedMethod(Placeholder.class, BiFunction.class)
         .warnClassIsUsingDeprecatedAPI(
            "You should create a separate class for each placeholder, and register them via the CustomComponentRegistry https://git.lumine.io/mythiccraft/MythicMobs/-/wikis/API/Placeholders#registering-the-placeholders"
         );
      return new SpawnerPlaceholder(transformer);
   }

   @Deprecated(
      forRemoval = true
   )
   static TargetPlaceholder target(TriFunction<PlaceholderMeta, AbstractEntity, String, String> transformer) {
      ClassInfo.ofCaller()
         .setUsedMethod(Placeholder.class, TriFunction.class)
         .warnClassIsUsingDeprecatedAPI(
            "You should create a separate class for each placeholder, and register them via the CustomComponentRegistry https://git.lumine.io/mythiccraft/MythicMobs/-/wikis/API/Placeholders#registering-the-placeholders"
         );
      return new TargetPlaceholder(transformer);
   }

   @Deprecated(
      forRemoval = true
   )
   static WorldPlaceholder world(BiFunction<AbstractWorld, String, String> transformer) {
      ClassInfo.ofCaller()
         .setUsedMethod(Placeholder.class, BiFunction.class)
         .warnClassIsUsingDeprecatedAPI(
            "You should create a separate class for each placeholder, and register them via the CustomComponentRegistry https://git.lumine.io/mythiccraft/MythicMobs/-/wikis/API/Placeholders#registering-the-placeholders"
         );
      return new WorldPlaceholder(transformer);
   }
}
