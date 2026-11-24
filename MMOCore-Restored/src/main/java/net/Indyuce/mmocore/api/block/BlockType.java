package net.Indyuce.mmocore.api.block;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockType {
   void place(@NotNull BlockInfo.RegeneratingBlock regeneratingBlock);

   void regenerate(@NotNull BlockInfo.RegeneratingBlock regeneratingBlock);

   @NotNull
   String display();

   boolean breakRestrictions(@NotNull Block block);

   @Override
   int hashCode();

   @Override
   boolean equals(@Nullable Object obj);
}
