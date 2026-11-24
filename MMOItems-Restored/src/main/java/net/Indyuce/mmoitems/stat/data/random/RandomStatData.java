package net.Indyuce.mmoitems.stat.data.random;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.type.StatData;

public interface RandomStatData<S extends StatData> {
   S randomize(MMOItemBuilder mmoItemBuilder);
}
