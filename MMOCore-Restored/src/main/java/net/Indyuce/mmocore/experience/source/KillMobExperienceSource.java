package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.util.FlushableRegistry;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class KillMobExperienceSource extends SpecificExperienceSource<Entity> {
   private final EntityType type;
   @Nullable
   private final String displayName;

   public KillMobExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser, config);
      config.validateKeys(new String[]{"type"});
      this.displayName = config.contains("name") ? MythicLib.plugin.parseColors(config.getString("name")) : null;
      this.type = EntityType.valueOf(config.getString("type").toUpperCase().replace("-", "_").replace(" ", "_"));
   }

   @Override
   public ExperienceSourceManager<KillMobExperienceSource> newManager() {
      return new KillMobExperienceSource.Manager();
   }

   public boolean matchesParameter(PlayerData player, Entity obj) {
      return obj.getType() == this.type && (this.displayName == null || this.displayName.equals(obj.getCustomName()));
   }

   private static class Manager extends ExperienceSourceManager<KillMobExperienceSource> {
      private final FlushableRegistry<UUID, UUID> registry = new FlushableRegistry<>((entity, attacker) -> Bukkit.getEntity(entity) == null, 1200L);

      @Override
      public void whenClosed() {
         this.registry.close();
      }

      @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
      public void registerLastAttacker(PlayerAttackEvent event) {
         this.registry.getRegistry().put(event.getEntity().getUniqueId(), event.getAttacker().getPlayer().getUniqueId());
      }

      @EventHandler(priority = EventPriority.MONITOR)
      public void giveExp(EntityDeathEvent event) {
         UUID lastAttacker = (UUID)this.registry.getRegistry().remove(event.getEntity().getUniqueId());
         if (lastAttacker != null) {
            if (!event.getEntity().getPersistentDataContainer().has(new NamespacedKey(MMOCore.plugin, "spawner_spawned"), PersistentDataType.STRING)) {
               PlayerData data = PlayerData.get(lastAttacker);

               for (KillMobExperienceSource source : this.getSources()) {
                  if (source.matches(data, event.getEntity())) {
                     source.giveExperience(data, 1.0, MMOCoreUtils.getCenterLocation(event.getEntity()));
                  }
               }
            }
         }
      }
   }
}
