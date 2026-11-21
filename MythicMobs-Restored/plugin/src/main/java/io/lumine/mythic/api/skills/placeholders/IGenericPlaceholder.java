package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;

public interface IGenericPlaceholder<T> extends IPlaceholder {
   default T get() {
      return this.get(PlaceholderContext.of());
   }

   @Deprecated
   default T get(PlaceholderMeta meta) {
      return this.get(PlaceholderContext.builder().meta(meta).build());
   }

   @Deprecated
   default T get(AbstractEntity entity) {
      return this.get(PlaceholderContext.builder().entity(entity).build());
   }

   @Deprecated
   default T get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.get(PlaceholderContext.builder().meta(meta).entity(entity).build());
   }

   @Deprecated
   default T get(PlaceholderMeta meta, AbstractLocation location) {
      return this.get(PlaceholderContext.builder().meta(meta).location(location).build());
   }

   @Deprecated
   default T get(PlaceholderMeta meta, AbstractItemStack itemStack) {
      return this.get(PlaceholderContext.builder().meta(meta).item(itemStack).build());
   }

   @Deprecated
   default T get(SkillCaster caster) {
      return this.get(PlaceholderContext.builder().caster(caster).build());
   }

   @Deprecated
   default T get(MythicSpawner spawner) {
      return this.get(PlaceholderContext.builder().spawner(spawner).build());
   }

   T get(PlaceholderContext placeholderContext);

   boolean isStaticallyEqualTo(T t);
}
