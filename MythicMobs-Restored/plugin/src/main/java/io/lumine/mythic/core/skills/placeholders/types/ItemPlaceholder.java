package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.core.skills.placeholders.Placeholder;

@Deprecated
public interface ItemPlaceholder extends Placeholder {
   String apply(AbstractItemStack abstractItemStack, String str);
}
