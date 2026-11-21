package io.lumine.mythic.core.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class SkillHelper {
   public static List<Player> getPlayersInRadius(LivingEntity l, int i) {
      List<Player> list = new ArrayList<>();
      double radiusSq = (double)i * i;

      for (Player p : l.getWorld().getPlayers()) {
         if (p.getWorld().equals(l.getWorld()) && p.getGameMode() != GameMode.CREATIVE && l.getLocation().distanceSquared(p.getLocation()) < radiusSq) {
            list.add(p);
         }
      }

      return list;
   }

   public static List<LivingEntity> getLivingEntitiesInRadius(LivingEntity l, int i) {
      List<LivingEntity> list = new ArrayList<>();
      double radiusSq = (double)i * i;

      for (LivingEntity p : l.getWorld().getLivingEntities()) {
         if (p.getWorld().equals(l.getWorld())
            && (!(p instanceof Player) || ((Player)p).getGameMode() != GameMode.CREATIVE)
            && !p.getUniqueId().equals(l.getUniqueId())
            && l.getLocation().distanceSquared(p.getLocation()) < radiusSq) {
            list.add(p);
         }
      }

      return list;
   }

   public static boolean hasUsedSkill(String full, AbstractEntity abstractEntity) {
      for (MetadataValue mv : BukkitAdapter.adapt(abstractEntity).getMetadata(full)) {
         if (mv.asString().equals(full)) {
            return true;
         }
      }

      return false;
   }
}
