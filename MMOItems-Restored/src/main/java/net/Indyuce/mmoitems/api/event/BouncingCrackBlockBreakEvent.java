package net.Indyuce.mmoitems.api.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BouncingCrackBlockBreakEvent extends BlockBreakEvent {
   public BouncingCrackBlockBreakEvent(@NotNull Block var1, @NotNull Player var2) {
      super(var1, var2);
   }
}
