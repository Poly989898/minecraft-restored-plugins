package net.Indyuce.mmocore.api.block;

import org.bukkit.block.Block;

public interface BlockType {
    void place(BlockInfo.RegeneratingBlock block);
    void regenerate(BlockInfo.RegeneratingBlock block);
    String display();
    boolean breakRestrictions(Block block);
}
