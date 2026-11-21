package io.lumine.mythic.api.drops;

import io.lumine.mythic.api.adapters.AbstractItemStack;

public interface IItemDrop extends IDrop {
   AbstractItemStack getDrop(DropMetadata dropMetadata, double value);
}
