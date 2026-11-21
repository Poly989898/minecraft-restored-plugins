package io.lumine.mythic.api.drops;

import io.lumine.mythic.core.drops.LootBag;

public interface IMultiDrop {
   LootBag get(DropMetadata dropMetadata);
}
