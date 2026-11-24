package net.Indyuce.mmocore.loot.droptable.dropitem;

import java.util.Random;
import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.loot.LootBuilder;

public abstract class DropItem {
    protected static final Random random = new Random();
    public DropItem(MMOLineConfig config) { }
    public int rollAmount() { return 0; }
    public abstract void collect(LootBuilder builder);
}
