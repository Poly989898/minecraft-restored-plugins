package com.nexomc.nexo.utils.breaker;

import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.utils.Reloadable;
import kotlin.Metadata;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH&J\u0010\u0010\n\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u000bÀ\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/utils/breaker/BreakerManager;", "Lcom/nexomc/nexo/utils/Reloadable;", "startBlockBreak", "", "player", "Lorg/bukkit/entity/Player;", "block", "Lorg/bukkit/block/Block;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "stopBlockBreak", "com.nexomc:core"}
)
public interface BreakerManager extends Reloadable {
   void startBlockBreak(@NotNull Player player, @NotNull Block block, @NotNull CustomBlockMechanic customBlockMechanic);

   void stopBlockBreak(@NotNull Player player);
}
