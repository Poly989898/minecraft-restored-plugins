package fr.phoenixdevt.profiles.data;

import fr.phoenixdevt.profiles.util.WrappedLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LocationMap {
   boolean isEmpty();

   @Nullable
   Location getCurrent();

   @Nullable
   WrappedLocation getCurrentWrapped();

   boolean hasCurrent();

   void setCurrent(@NotNull Location var1);

   @NotNull
   String getIdentifier();
}
