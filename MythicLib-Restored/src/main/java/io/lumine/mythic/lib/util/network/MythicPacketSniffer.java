package io.lumine.mythic.lib.util.network;

import io.netty.channel.Channel;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicPacketSniffer extends LightInjector {
   private static final double MAX_RANGE = 5.0;
   private static final Map<String, Map<String, Field>> FIELDS = new ConcurrentHashMap<>();

   public MythicPacketSniffer(@NotNull Plugin plugin) {
      super(plugin);
   }

   @Nullable
   @Override
   protected Object onPacketReceiveAsync(@Nullable Player sender, @NotNull Channel channel, @NotNull Object packet) {
      if (sender == null) {
         return packet;
      }

      String packetName = packet.getClass().getSimpleName();
      if (packetName.equals("PacketPlayInArmAnimation")) {
         Object hand = this.getField(packet, "a");
         if (hand == null || !hand.toString().equals("MAIN_HAND")) {
            return packet;
         }

         Bukkit.getScheduler().runTask(this.getPlugin(), () -> {
            MythicPacketSniffer.SeenEntity entity = this.getLineOfSight(sender);
            if (entity != null) {
               this.triggerEvent(sender);
            }
         });
      }

      return packet;
   }

   @Nullable
   @Override
   protected Object onPacketSendAsync(@Nullable Player receiver, @NotNull Channel channel, @NotNull Object packet) {
      return packet;
   }

   @Nullable
   private MythicPacketSniffer.SeenEntity getLineOfSight(Player player) {
      RayTraceResult result = player.getWorld()
         .rayTrace(
            player.getEyeLocation(),
            player.getEyeLocation().getDirection(),
            5.0,
            FluidCollisionMode.NEVER,
            true,
            0.0,
            entityx -> entityx instanceof LivingEntity && !entityx.equals(player)
         );
      if (result == null) {
         return null;
      } else {
         Entity entity = result.getHitEntity();
         if (entity != null && (!(entity instanceof Player) || ((Player)entity).getGameMode().ordinal() != 3)) {
            double distance = result.getHitPosition().distance(player.getEyeLocation().toVector());
            return new MythicPacketSniffer.SeenEntity(entity, distance);
         } else {
            return null;
         }
      }
   }

   private void triggerEvent(@NotNull Player player) {
      Bukkit.getPluginManager()
         .callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, player.getInventory().getItemInMainHand(), null, BlockFace.EAST, EquipmentSlot.HAND));
   }

   @Nullable
   private Object getField(Object object, String field) {
      return this.getField(object, object.getClass(), field);
   }

   @Nullable
   private Object getField(Object object, Class<?> c, String field) {
      if (FIELDS.containsKey(c.getCanonicalName())) {
         Map<String, Field> fs = FIELDS.get(c.getCanonicalName());
         if (fs.containsKey(field)) {
            try {
               return fs.get(field).get(object);
            } catch (ReflectiveOperationException e) {
               return null;
            }
         }
      }

      Class<?> current = c;

      Field f;
      while (true) {
         try {
            f = current.getDeclaredField(field);
            break;
         } catch (ReflectiveOperationException e1) {
            current = current.getSuperclass();
            if (current == null) {
               return null;
            }
         }
      }

      f.setAccessible(true);
      Map<String, Field> map;
      if (FIELDS.containsKey(c.getCanonicalName())) {
         map = FIELDS.get(c.getCanonicalName());
      } else {
         map = new ConcurrentHashMap<>();
         FIELDS.put(c.getCanonicalName(), map);
      }

      map.put(f.getName(), f);

      try {
         return f.get(object);
      } catch (ReflectiveOperationException e) {
         return null;
      }
   }

   private class SeenEntity {
      @NotNull
      final Entity entity;
      final double distance;

      private SeenEntity(@NotNull Entity entity, double distance) {
         this.entity = entity;
         this.distance = distance;
      }
   }
}
