package io.lumine.mythic.core.mobs.totems;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.api.mobs.NPCManager;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitBlock;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.mobs.MobType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;

public class TotemExecutor extends ReloadableModule<MythicBukkit> implements NPCManager {
   private final Map<Material, Collection<TotemConfig>> mobTotems = Maps.newConcurrentMap();

   public TotemExecutor(MythicBukkit plugin, MobManager mobManager) {
      super(plugin);
   }

   public void load(MythicBukkit mythicBukkit) {
      Events.subscribe(BlockPlaceEvent.class)
         .handler(event -> Schedulers.of(event.getBlock().getLocation()).runLater(() -> this.onBlockPlace(event), 5L))
         .bindWith(this);
   }

   @Override
   public void unload() {
      this.mobTotems.clear();
   }

   public void registerTotem(MobType mobType) {
      TotemConfig totemConfig = mobType.getTotemConfig();
      if (totemConfig != null) {
         Material material = totemConfig.getTriggerBlock().getBlockData().getMaterial();
         this.mobTotems.putIfAbsent(material, new ArrayList<>());
         this.mobTotems.get(material).add(totemConfig);
      }
   }

   public void onBlockPlace(BlockPlaceEvent event) {
      Material material = event.getBlock().getType();
      Collection<TotemConfig> totems = this.mobTotems.get(material);
      if (totems != null && !totems.isEmpty()) {
         Block placedBlock = event.getBlockPlaced();
         Schedulers.async()
            .run(
               () -> {
                  for (TotemConfig totem : totems) {
                     if (!(
                           totem.getTriggerBlock() instanceof BukkitBlock bukkitBlock
                              ? !bukkitBlock.matches(placedBlock)
                              : !totem.getTriggerBlock().matches(BukkitAdapter.adapt(placedBlock.getLocation()))
                        )
                        && totem.isValidTotemStructure(placedBlock)) {
                        Schedulers.of(placedBlock.getLocation()).run(() -> {
                           totem.summonMob(placedBlock);
                           totem.replacePattern(placedBlock);
                        });
                        break;
                     }
                  }
               }
            );
      }
   }
}
