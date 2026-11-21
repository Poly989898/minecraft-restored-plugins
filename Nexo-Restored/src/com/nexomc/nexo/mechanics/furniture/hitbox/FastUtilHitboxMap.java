package com.nexomc.nexo.mechanics.furniture.hitbox;

import com.nexomc.nexo.mechanics.furniture.FurnitureHitboxEntity;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0000\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001B\u0007¢\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0003H\u0096\u0080\bJ\u0014\u0010\t\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0007\u001a\u00020\u0002H\u0096\u0080\bJ\u001a\u0010\t\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0003H\u0096\u0080\u0004J\n\u0010\u000b\u001a\u00020\fH\u0096\u0080\u0004¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/hitbox/FastUtilHitboxMap;", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "Ljava/util/UUID;", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureHitboxEntity;", "<init>", "()V", "put", "key", "value", "remove", "", "clear", "", "com.nexomc:core"}
)
public final class FastUtilHitboxMap extends Object2ObjectOpenHashMap<UUID, FurnitureHitboxEntity> {
   @Nullable
   public FurnitureHitboxEntity put(@NotNull UUID key, @NotNull FurnitureHitboxEntity value) {
      FurnitureHitboxEntity previous = (FurnitureHitboxEntity)super.put(key, value);
      if (previous != null) {
         IFurniturePacketManager.Companion.removeHitboxFromReverseMap$com_nexomc_core(key, previous);
      }

      IFurniturePacketManager.Companion.addHitboxToReverseMap$com_nexomc_core(key, value);
      return previous;
   }

   @Nullable
   public FurnitureHitboxEntity remove(@NotNull UUID key) {
      FurnitureHitboxEntity previous = (FurnitureHitboxEntity)super.remove(key);
      if (previous != null) {
         IFurniturePacketManager.Companion.removeHitboxFromReverseMap$com_nexomc_core(key, previous);
      }

      return previous;
   }

   public boolean remove(@NotNull UUID key, @NotNull FurnitureHitboxEntity value) {
      boolean removed = super.remove(key, value);
      if (removed) {
         IFurniturePacketManager.Companion.removeHitboxFromReverseMap$com_nexomc_core(key, value);
      }

      return removed;
   }

   public void clear() {
      Map map = (Map)this;

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value2 = 0;
         UUID key = (UUID)element.getKey();
         FurnitureHitboxEntity value = (FurnitureHitboxEntity)element.getValue();
         IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
         companion.removeHitboxFromReverseMap$com_nexomc_core(key, value);
      }

      super.clear();
   }
}
