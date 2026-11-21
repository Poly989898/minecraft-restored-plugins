package io.lumine.mythic.core.skills.audience;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.utils.annotations.MythicAudience;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@MythicAudience(
   name = "nearby",
   aliases = {"nearbyplayers"},
   version = "5.4",
   description = "All players nearby"
)
public class NearbyAudience extends SkillAudience {
   public NearbyAudience(MythicLineConfig mlc) {
      super(mlc);
   }

   @Override
   public Collection<AbstractPlayer> get(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> targets = Lists.newArrayList();
      AbstractWorld world = data.getCaster().getEntity().getWorld();
      if (world == null) {
         return targets;
      } else {
         World bukkitWorld = BukkitAdapter.adapt(world);

         for (Player player : bukkitWorld.getPlayers()) {
            AbstractLocation origin = data.getOrigin();
            AbstractWorld playerWorld = player.getWorld() == bukkitWorld ? world : BukkitAdapter.adapt(player.getWorld());
            if (playerWorld.equals(origin.getWorld())) {
               Location location = player.getLocation();
               double dx = origin.getX() - location.getX();
               double dy = origin.getY() - location.getY();
               double dz = origin.getZ() - location.getZ();
               if (dx * dx + dy * dy + dz * dz < 65536.0) {
                  targets.add(BukkitAdapter.adapt(player));
               }
            }
         }

         return targets;
      }
   }
}
