package io.lumine.mythic.lib.hologram;

import java.util.List;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface HologramFactory {
   @NotNull
   Hologram newHologram(@NotNull Location var1, @NotNull List<String> var2);
}
