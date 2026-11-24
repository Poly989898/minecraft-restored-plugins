package me.TechsCode.UltraPermissions.base.reflection.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketSender {
   private static final Class<?> CraftPlayerClass;
   private static final Class<?> EntityPlayerClass;
   Method sendPacket;

   public PacketSender() throws ClassNotFoundException, NoSuchMethodException {
      Class clazz = ReflectionUtil.getClass(false, "network.protocol.Packet", "Packet");
      Class connectionClass = ReflectionUtil.getClass(false, "server.network.ServerGamePacketListenerImpl", "server.network.PlayerConnection", "PlayerConnection");
      if (ReflectionUtil.MINOR_VERSION >= 7) {
         this.sendPacket = ReflectionUtil.getMethods(connectionClass, void.class, clazz).get(0);
      } else {
         this.sendPacket = ReflectionUtil.getMethod(connectionClass, new String[]{"sendPacket"}, clazz);
      }
   }

   public void sendPacket(@NotNull Player player, @NotNull Object obj) {
      try {
         this.sendPacket.invoke(this.getPlayerConnection(player), obj);
      } catch (IllegalAccessException | InvocationTargetException exception) {
         throw new RuntimeException(exception);
      }
   }

   private Object getPlayerConnection(Player player) {
      try {
         Object obj = CraftPlayerClass.getMethod("getHandle").invoke(CraftPlayerClass.cast(player));
         return ReflectionUtil.getField(EntityPlayerClass, "connection", "playerConnection").get(obj);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
         throw new RuntimeException(exception);
      }
   }

   static {
      try {
         EntityPlayerClass = ReflectionUtil.getClass(false, "server.level.ServerPlayer", "server.level.EntityPlayer", "EntityPlayer");
         CraftPlayerClass = ReflectionUtil.getClass(true, "entity.CraftPlayer");
      } catch (Exception exception) {
         throw new RuntimeException(exception);
      }
   }
}
