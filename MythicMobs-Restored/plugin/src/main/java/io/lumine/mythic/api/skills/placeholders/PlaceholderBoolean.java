package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;

public interface PlaceholderBoolean extends IPlaceholder {
   static PlaceholderBoolean of(String in) {
      return MythicProvider.get().getConfiguration().createPlaceholderBoolean(in);
   }

   boolean get();

   /** @deprecated */
   boolean get(PlaceholderMeta placeholderMeta);

   /** @deprecated */
   boolean get(AbstractEntity abstractEntity);

   /** @deprecated */
   boolean get(PlaceholderMeta placeholderMeta, AbstractEntity abstractEntity);

   /** @deprecated */
   boolean get(SkillCaster skillCaster);

   /** @deprecated */
   boolean get(MythicSpawner mythicSpawner);

   boolean get(PlaceholderContext placeholderContext);

   boolean isStaticallyEqualTo(Boolean flag);
}
