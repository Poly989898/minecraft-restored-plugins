package io.lumine.mythic.api.adapters;

import io.lumine.mythic.api.skills.SkillMetadata;

public abstract class AbstractFirework {
   public abstract void launch(SkillMetadata skillMetadata, AbstractLocation abstractLocation);
}
