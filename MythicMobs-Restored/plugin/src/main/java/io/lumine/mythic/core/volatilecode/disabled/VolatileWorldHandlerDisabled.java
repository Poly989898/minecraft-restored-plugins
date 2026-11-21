package io.lumine.mythic.core.volatilecode.disabled;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.volatilecode.handlers.VolatileWorldHandler;
import io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand;
import io.lumine.mythic.api.volatilecode.virtual.PacketFallingBlock;
import io.lumine.mythic.api.volatilecode.virtual.PacketItem;
import io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class VolatileWorldHandlerDisabled implements VolatileWorldHandler {
   public void registerWorldAccess(World world) {
   }

   public void unregisterWorldAccess(World world) {
   }

   public AbstractEntity getEntity(UUID uuid) {
      Entity entity = Bukkit.getEntity(uuid);
      return entity != null ? BukkitAdapter.adapt(entity) : null;
   }

   @Override
   public void playSoundAtLocation(AbstractLocation location, String sound, float volume, float pitch, double radius) {
      Location l = BukkitAdapter.adapt(location);

      for (Player player : l.getWorld().getPlayers()) {
         player.playSound(l, sound, volume, pitch);
      }
   }

   @Override
   public Collection<AbstractEntity> getEntities(AbstractWorld world) {
      return List.of();
   }

   @Override
   public Collection<AbstractEntity> getLivingEntities(AbstractWorld world) {
      return List.of();
   }

   @Override
   public Collection<AbstractPlayer> getPlayers(AbstractWorld world) {
      return List.of();
   }

   @Override
   public float getDifficultyScale(AbstractLocation location) {
      return 1.0F;
   }

   @Override
   public Entity spawnInvisibleArmorStand(Location location) {
      return location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
   }

   @Override
   public PacketArmorStand.PacketArmorStandEntityRenderer createVirtualArmorStandRenderer(PacketArmorStand wrapper) {
      return null;
   }

   @Override
   public PacketFallingBlock.PacketFallingBlockEntityRenderer createVirtualBlockRenderer(PacketFallingBlock wrapper) {
      return null;
   }

   @Override
   public PacketItemDisplay.PacketItemDisplayEntityRenderer createVirtualDisplayItemEntityRenderer(PacketItemDisplay wrapper) {
      return null;
   }

   @Override
   public PacketTextDisplay.PacketTextDisplayEntityRenderer createVirtualTextDisplayEntityRenderer(PacketTextDisplay wrapper) {
      return null;
   }

   @Override
   public PacketItem.PacketItemEntityRenderer createVirtualItemRenderer(PacketItem wrapper) {
      return null;
   }
}
