package io.lumine.mythic.api.adapters;

import io.lumine.mythic.api.skills.SkillMetadata;

public abstract class AbstractItemMatcher {
   public abstract boolean matches(AbstractItemStack abstractItemStack);

   public abstract boolean matches(SkillMetadata skillMetadata);

   public abstract boolean matches(AbstractEntity abstractEntity);
}
