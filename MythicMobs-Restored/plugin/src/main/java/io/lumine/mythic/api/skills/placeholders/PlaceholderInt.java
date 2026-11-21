package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;

public interface PlaceholderInt extends IPlaceholder {
   static PlaceholderInt of(String in) {
      return MythicProvider.get().getConfiguration().createPlaceholderInt(in);
   }

   int get();

   /** @deprecated */
   int get(PlaceholderMeta placeholderMeta);

   /** @deprecated */
   int get(AbstractEntity abstractEntity);

   /** @deprecated */
   int get(PlaceholderMeta placeholderMeta, AbstractEntity abstractEntity);

   /** @deprecated */
   int get(SkillCaster skillCaster);

   /** @deprecated */
   int get(MythicSpawner mythicSpawner);

   int get(PlaceholderContext placeholderContext);

   boolean isStaticallyEqualTo(int value);
}
