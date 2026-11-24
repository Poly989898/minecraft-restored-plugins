package net.Indyuce.mmoitems.stat.type;

import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;

public interface Previewable<R extends RandomStatData<S>, S extends StatData> {
   void whenPreviewed(@NotNull ItemStackBuilder var1, @NotNull S var2, @NotNull R var3) throws IllegalArgumentException;
}
