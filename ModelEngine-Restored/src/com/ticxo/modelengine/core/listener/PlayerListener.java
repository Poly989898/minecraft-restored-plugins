package com.ticxo.modelengine.core.listener;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BukkitPlayer;
import com.ticxo.modelengine.api.entity.cull.BasicCulling;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.network.ModPlayerTracker;
import com.ticxo.modelengine.core.menu.SelectionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.util.Vector;

public class PlayerListener implements Listener {
   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      ServerInfo.setOnline(player.getUniqueId(), true);
      ModelEngineAPI.getNetworkHandler().injectChannel(player);
      AnimationLODHandler.registerPlayer(player.getUniqueId());
      if (ModelEngineAPI.getConfigCache().isEagerGenerate()) {
         ModelEngineAPI.getUserLimbRegistry().generate(player);
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      ServerInfo.setOnline(player.getUniqueId(), false);
      ModelEngineAPI.getNetworkHandler().ejectChannel(player);
      ModelEngineAPI.getMountPairManager().tryDismount(player);
      ModelEngineAPI.getInteractionTracker().removeDynamicHitbox(player.getUniqueId());
      ModelEngineAPI.getEntityHandler().setForcedInvisible(player, false);
      SelectionManager.removePlayer(event.getPlayer());
      AnimationLODHandler.setPlayerActive(event.getPlayer().getUniqueId(), false);
      BasicCulling.setGlobalCullExempt(player.getUniqueId(), false);
      ModelEngineAPI.getPivotOverrideRegistry().remove(player.getUniqueId());
      ModPlayerTracker.removePlayer(player.getUniqueId());
   }

   @EventHandler
   public void onChannelRegister(PlayerRegisterChannelEvent event) {
      if ("modelengine:bulk_data".equals(event.getChannel())) {
         ModPlayerTracker.addPlayer(event.getPlayer().getUniqueId());
      }
   }

   @EventHandler
   public void onChannelUnregister(PlayerUnregisterChannelEvent event) {
      if ("modelengine:bulk_data".equals(event.getChannel())) {
         ModPlayerTracker.removePlayer(event.getPlayer().getUniqueId());
      }
   }

   @EventHandler
   public void onPortal(PlayerPortalEvent event) {
      Player entity = event.getPlayer();
      if (ModelEngineAPI.getMountPairManager().get(entity.getUniqueId()) != null) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onTeleport(PlayerTeleportEvent event) {
      if (!(event instanceof PlayerPortalEvent)) {
         ModelEngineAPI.getMountPairManager().tryDismount(event.getPlayer());
      }
   }

   @EventHandler
   public void onChangedWorld(PlayerChangedWorldEvent event) {
      Player player = event.getPlayer();
      ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(player.getUniqueId());
      if (modeledEntity != null) {
         if (modeledEntity.getBase().getData() instanceof BukkitEntityData data) {
            data.resendTracking(player.getUniqueId());
         }

         ModelEngineAPI.getPivotOverrideRegistry().get(player.getUniqueId()).ifPresent(override -> override.getPassengers().markDirty());
      }
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent event) {
      ModelEngineAPI.getMountPairManager().tryDismount(event.getPlayer());
   }

   @EventHandler
   public void onMove(PlayerMoveEvent event) {
      Vector dir = event.getTo().toVector().subtract(event.getFrom().toVector());
      if (!dir.isZero()) {
         Player player = event.getPlayer();
         ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(player.getUniqueId());
         if (modeledEntity != null) {
            if (modeledEntity.getBase().getData() instanceof BukkitPlayer.BukkitPlayerData playerData) {
               if (dir.getX() != 0.0 || dir.getZ() != 0.0) {
                  playerData.setWalkTick(3);
               }

               if (dir.getY() > 0.0) {
                  playerData.setJumpTick(3);
               }
            }
         }
      }
   }
}
