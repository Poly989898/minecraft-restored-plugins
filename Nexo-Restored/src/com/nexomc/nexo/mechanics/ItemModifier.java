package com.nexomc.nexo.mechanics;

import com.nexomc.nexo.items.ItemBuilder;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bæ\u0080\u0001\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003H&¨\u0006\u0005À\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/mechanics/ItemModifier;", "", "modify", "Lcom/nexomc/nexo/items/ItemBuilder;", "builder", "com.nexomc:core"}
)
public interface ItemModifier {
   @NotNull
   ItemBuilder modify(@NotNull ItemBuilder itemBuilder);
}
