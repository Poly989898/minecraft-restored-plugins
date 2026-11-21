package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.function.BiFunction;

@Deprecated
public interface MetaPlaceholder extends Placeholder, BiFunction<PlaceholderMeta, String, String> {
}
