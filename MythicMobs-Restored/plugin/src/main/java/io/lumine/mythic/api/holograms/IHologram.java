package io.lumine.mythic.api.holograms;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;

public interface IHologram extends Terminable {
   void teleport(AbstractLocation abstractLocation);

   void setText(String str);

   void setText(String[] strs, String str);
}
