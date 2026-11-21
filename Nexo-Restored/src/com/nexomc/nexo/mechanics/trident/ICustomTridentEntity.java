package com.nexomc.nexo.mechanics.trident;

import kotlin.Metadata;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006À\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/mechanics/trident/ICustomTridentEntity;", "", "spawn", "Lorg/bukkit/entity/Entity;", "location", "Lorg/bukkit/Location;", "com.nexomc:core"}
)
public interface ICustomTridentEntity {
   @NotNull
   Entity spawn(@NotNull Location location);
}
