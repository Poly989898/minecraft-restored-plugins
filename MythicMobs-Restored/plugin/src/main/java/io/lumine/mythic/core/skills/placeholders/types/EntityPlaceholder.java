package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import java.util.function.BiFunction;

@Deprecated
public interface EntityPlaceholder extends Placeholder, BiFunction<AbstractEntity, String, String> {
}
