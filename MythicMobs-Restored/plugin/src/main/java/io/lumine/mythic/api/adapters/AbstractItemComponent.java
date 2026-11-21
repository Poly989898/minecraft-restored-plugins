package io.lumine.mythic.api.adapters;

import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;

public interface AbstractItemComponent {
   void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack abstractItemStack);
}
