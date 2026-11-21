package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.utils.interfaces.TriFunction;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

@Deprecated
public interface MetaTargetPlaceholder extends Placeholder, TriFunction<PlaceholderMeta, AbstractEntity, String, String> {
}
