package net.Indyuce.mmocore.api.block;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockType {
   void place(@NotNull BlockInfo.RegeneratingBlock var1);

   void regenerate(@NotNull BlockInfo.RegeneratingBlock var1);

   @NotNull
   String display();

   boolean breakRestrictions(@NotNull Block var1);

   @Override
   int hashCode();

   @Override
   boolean equals(@Nullable Object var1);
}
