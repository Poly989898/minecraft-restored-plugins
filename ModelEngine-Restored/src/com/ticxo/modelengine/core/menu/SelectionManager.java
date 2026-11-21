package com.ticxo.modelengine.core.menu;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class SelectionManager {
   private static final Set<UUID> SELECTING = Sets.newConcurrentHashSet();
   private static final Map<UUID, Entity> SELECTED = Maps.newConcurrentMap();

   public static void setSelecting(Player player, boolean flag) {
      if (flag) {
         SELECTING.add(player.getUniqueId());
      } else {
         SELECTING.remove(player.getUniqueId());
      }
   }

   public static boolean isSelecting(Player player) {
      return SELECTING.contains(player.getUniqueId());
   }

   public static void setSelected(Player player, @Nullable Entity entity) {
      if (entity == null) {
         SELECTED.remove(player.getUniqueId());
      } else {
         SELECTED.put(player.getUniqueId(), entity);
      }
   }

   public static Entity getSelected(Player player) {
      return SELECTED.get(player.getUniqueId());
   }

   public static void removePlayer(Player player) {
      SELECTING.remove(player.getUniqueId());
      SELECTED.remove(player.getUniqueId());
   }
}
