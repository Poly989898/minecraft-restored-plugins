package net.Indyuce.mmoitems.stat.data.random;

import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface UpdatableRandomStatData<S extends StatData> {
   @NotNull
   S reroll(@NotNull ItemStat var1, @NotNull S var2, int var3);
}
