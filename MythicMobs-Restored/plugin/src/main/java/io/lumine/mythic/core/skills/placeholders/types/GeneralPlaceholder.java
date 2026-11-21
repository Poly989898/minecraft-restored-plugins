package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.core.skills.placeholders.Placeholder;
import java.util.function.Function;

@Deprecated
public interface GeneralPlaceholder extends Placeholder, Function<String, String> {
}
