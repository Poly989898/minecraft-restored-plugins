package io.lumine.mythic.bukkit.utils.holograms;

import io.lumine.mythic.bukkit.utils.serialize.Position;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;

public interface BaseHologram extends Terminable {
   void spawn();

   void despawn();

   boolean isSpawned();

   void updatePosition(@Nonnull Position position);

   void setClickCallback(@Nullable Consumer<Player> consumer);
}
