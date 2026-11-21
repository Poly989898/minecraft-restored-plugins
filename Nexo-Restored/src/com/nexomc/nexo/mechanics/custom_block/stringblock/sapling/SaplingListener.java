package com.nexomc.nexo.mechanics.custom_block.stringblock.sapling;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.compatibilities.worldedit.WrappedWorldEdit;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.wrappers.ParticleWrapper;
import java.io.File;
import kotlin.Metadata;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002¨\u0006\f"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/sapling/SaplingListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onBoneMeal", "", "Lorg/bukkit/event/player/PlayerInteractEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGH", "ignoreCancelled", "com.nexomc:core"}
)
public final class SaplingListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public final void onBoneMeal(@NotNull PlayerInteractEvent playerInteractEvent) {
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock != null) {
         Block it = clickedBlock;
         Block it_5 = it;
         clickedBlock = it_5.getType() == Material.TRIPWIRE ? it_5 : null;
         if (clickedBlock != null) {
            Block block = clickedBlock;
            Location location2 = block.getLocation();
            Location location = location2;
            ItemStack itemStack = playerInteractEvent.getItem();
            if (itemStack != null) {
               ItemStack selectedSchematic = itemStack;
               int it_4 = 0;
               itemStack = selectedSchematic.getType() == Material.BONE_MEAL ? selectedSchematic : null;
               if (itemStack != null) {
                  ItemStack item = itemStack;
                  StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(block);
                  if (stringBlockMechanic != null) {
                     StringBlockMechanic it_3 = stringBlockMechanic;
                     int value = 0;
                     stringBlockMechanic = it_3.isSapling() ? it_3 : null;
                     if (stringBlockMechanic != null) {
                        StringBlockMechanic mechanic = stringBlockMechanic;
                        SaplingMechanic saplingMechanic = mechanic.sapling();
                        if (saplingMechanic != null) {
                           SaplingMechanic it_2 = saplingMechanic;
                           int value2 = 0;
                           saplingMechanic = it_2.hasSchematic() ? it_2 : null;
                           if (saplingMechanic != null) {
                              SaplingMechanic sapling = saplingMechanic;
                              if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && playerInteractEvent.getHand() == EquipmentSlot.HAND) {
                                 if (sapling.requiresLight() && sapling.getMinLightLevel() > block.getLightLevel()) {
                                    return;
                                 }

                                 if (sapling.getRequiresWaterSource() && sapling.isUnderWater(block)) {
                                    return;
                                 }

                                 if (sapling.getCanGrowFromBoneMeal() && WrappedWorldEdit.INSTANCE.getLoaded()) {
                                    File selectSchematic = sapling.selectSchematic();
                                    if (selectSchematic == null) {
                                       return;
                                    }

                                    File selectedSchematic_1 = selectSchematic;
                                    if (!sapling.canPlaceSchematic(location, selectedSchematic_1)) {
                                       return;
                                    }

                                    if (playerInteractEvent.getPlayer().getGameMode() != GameMode.CREATIVE) {
                                       item.setAmount(item.getAmount() - 1);
                                    }

                                    block.getWorld().playEffect(location, Effect.BONE_MEAL_USE, 3);
                                    PersistentDataContainer pdc = BlockHelpers.INSTANCE.getPersistentDataContainer(block);
                                    Integer number = (Integer)pdc.get(SaplingMechanic.Companion.getSAPLING_KEY(), PersistentDataType.INTEGER);
                                    int growthTimeRemains = (number != null ? number : sapling.getNaturalGrowthTime()) - sapling.getBoneMealGrowthSpeedup();
                                    block.getWorld().spawnParticle(ParticleWrapper.INSTANCE.getHAPPY_VILLAGER(), location, 10, (double)0.5F, (double)0.5F, (double)0.5F);
                                    playerInteractEvent.getPlayer().swingMainHand();
                                    if (growthTimeRemains <= 0) {
                                       sapling.placeSchematic(location, selectedSchematic_1);
                                    } else {
                                       pdc.set(SaplingMechanic.Companion.getSAPLING_KEY(), PersistentDataType.INTEGER, growthTimeRemains);
                                    }

                                    return;
                                 }

                                 return;
                              }

                              return;
                           }
                        }

                        return;
                     }
                  }

                  return;
               }
            }

            return;
         }
      }

   }
}
