package net.Indyuce.mmoitems.stat.data.random;

import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface UpdatableRandomStatData<S extends StatData> {
   @NotNull
   S reroll(@NotNull ItemStat itemStat, @NotNull S value, int count);
}
