package com.nexomc.nexo.mechanics.furniture.bed;

import kotlin.Metadata;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&J\b\u0010\u0005\u001a\u00020\u0003H&¨\u0006\u0006À\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/bed/IFurnitureBedPacket;", "", "spawn", "", "refresh", "remove", "com.nexomc:core"}
)
public interface IFurnitureBedPacket {
   void spawn();

   void refresh();

   void remove();
}
