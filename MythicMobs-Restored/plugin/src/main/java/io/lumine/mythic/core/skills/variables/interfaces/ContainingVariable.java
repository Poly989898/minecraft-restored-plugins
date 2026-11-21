package io.lumine.mythic.core.skills.variables.interfaces;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;

public interface ContainingVariable {
   boolean checkContainsAll(PolymorphicPlaceholder polymorphicPlaceholder, PlaceholderContext placeholderContext);
}
