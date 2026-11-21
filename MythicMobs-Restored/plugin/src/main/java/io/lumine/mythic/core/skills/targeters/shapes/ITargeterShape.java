package io.lumine.mythic.core.skills.targeters.shapes;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillMetadata;
import java.util.Collection;
import java.util.function.Predicate;

public interface ITargeterShape {
   Collection<AbstractEntity> getEntities(SkillMetadata skillMetadata, AbstractLocation abstractLocation);

   Collection<AbstractEntity> getEntities(SkillMetadata skillMetadata, AbstractLocation abstractLocation, Predicate<AbstractEntity> predicate);
}
