package io.lumine.mythic.core.skills;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.utils.ReflectionUtils;
import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Deprecated
public class ParticleMaker {
   public static final class ParticlePacket {
      private static int version;
      private Class<?> enumParticle;
      private Constructor<?> packetConstructor;
      private Method getHandle;
      private Field playerConnection;
      private Method sendPacket;
      private boolean initialized;
      private ParticleMaker.ParticleType particle;
      private int[] particleData;
      private float offsetX;
      private float offsetY;
      private float offsetZ;
      private float speed;
      private int amount;
      private final boolean longDistance;
      private Object packet;
      public static List<Player> playerCache;

      @Deprecated
      public ParticlePacket(String name, Color color, float speed, int amount, boolean longDistance) throws IllegalArgumentException {
         this(name, color.getRed() / 255, color.getGreen() / 255, color.getBlue() / 255, 1.0F, 0, longDistance);
         this.offsetX = color.getRed() / 255.0F;
         this.offsetY = color.getGreen() / 255.0F;
         this.offsetZ = color.getBlue() / 255.0F;
         int value = 0;
         speed = 1.0F;
         if (this.offsetX < Float.MIN_NORMAL) {
            this.offsetX = Float.MIN_NORMAL;
         }

         switch (this.particle) {
            default:
               value = 1;
            case SPELL_MOB:
            case SPELL_MOB_AMBIENT:
            case NOTE:
            case REDSTONE:
               try {
                  this.packet = this.packetConstructor.newInstance();
                  ReflectionUtils.setValue(this.packet, true, "a", this.enumParticle.getEnumConstants()[this.particle.getId()]);
                  ReflectionUtils.setValue(this.packet, true, "j", longDistance);
                  if (this.particleData != null) {
                     int[] packetData = this.particleData;
                     ReflectionUtils.setValue(this.packet, true, "k", packetData);
                  }

                  ReflectionUtils.setValue(this.packet, true, "e", this.offsetX);
                  ReflectionUtils.setValue(this.packet, true, "f", this.offsetY);
                  ReflectionUtils.setValue(this.packet, true, "g", this.offsetZ);
                  ReflectionUtils.setValue(this.packet, true, "h", speed);
                  ReflectionUtils.setValue(this.packet, true, "i", Integer.valueOf(value));
               } catch (Exception e) {
                  throw new ParticleMaker.ParticlePacket.PacketInstantiationException("Packet instantiation failed", e);
               }
         }
      }

      @Deprecated
      public ParticlePacket(String name, AbstractVector vector, float speed, int amount, boolean longDistance) throws IllegalArgumentException {
         this(name, (float)vector.getX(), (float)vector.getY(), (float)vector.getZ(), speed, 0, longDistance);
         int value = 0;
         switch (this.particle) {
            case SPELL_MOB:
            case SPELL_MOB_AMBIENT:
            case REDSTONE:
            default:
               try {
                  this.packet = this.packetConstructor.newInstance();
                  ReflectionUtils.setValue(this.packet, true, "a", this.enumParticle.getEnumConstants()[this.particle.getId()]);
                  ReflectionUtils.setValue(this.packet, true, "j", longDistance);
                  if (this.particleData != null) {
                     int[] packetData = this.particleData;
                     ReflectionUtils.setValue(this.packet, true, "k", packetData);
                  }

                  ReflectionUtils.setValue(this.packet, true, "e", this.offsetX);
                  ReflectionUtils.setValue(this.packet, true, "f", this.offsetY);
                  ReflectionUtils.setValue(this.packet, true, "g", this.offsetZ);
                  ReflectionUtils.setValue(this.packet, true, "h", speed);
                  ReflectionUtils.setValue(this.packet, true, "i", Integer.valueOf(value));
               } catch (Exception e) {
                  throw new ParticleMaker.ParticlePacket.PacketInstantiationException("Packet instantiation failed", e);
               }
         }
      }

      @Deprecated
      public ParticlePacket(String name, float offsetX, float offsetY, float offsetZ, float speed, int amount, boolean longDistance) throws IllegalArgumentException {
         this.initialize();
         this.particle = ParticleMaker.ParticleType.get(name);
         if (this.particle == null) {
         }

         if (name.contains("_")) {
            String[] split = name.split("_");
            name = split[0] + "_";
            if (split.length > 1) {
               String[] split2 = split[1].split(":");
               int[] data = new int[split2.length];

               for (int i = 0; i < data.length; i++) {
                  data[i] = Integer.parseInt(split2[i]);
               }

               this.particleData = data;
            }
         }

         this.offsetX = offsetX;
         this.offsetY = offsetY;
         this.offsetZ = offsetZ;
         this.speed = speed;
         this.amount = amount;
         this.longDistance = longDistance;

         try {
            this.packet = this.packetConstructor.newInstance();
            ReflectionUtils.setValue(this.packet, true, "a", this.enumParticle.getEnumConstants()[this.particle.getId()]);
            ReflectionUtils.setValue(this.packet, true, "j", longDistance);
            if (this.particleData != null) {
               int[] packetData = this.particleData;
               ReflectionUtils.setValue(this.packet, true, "k", packetData);
            }

            ReflectionUtils.setValue(this.packet, true, "e", offsetX);
            ReflectionUtils.setValue(this.packet, true, "f", offsetY);
            ReflectionUtils.setValue(this.packet, true, "g", offsetZ);
            ReflectionUtils.setValue(this.packet, true, "h", speed);
            ReflectionUtils.setValue(this.packet, true, "i", amount);
         } catch (Exception e) {
         }
      }

      @Deprecated
      public void initialize() throws ParticleMaker.ParticlePacket.VersionIncompatibleException {
         if (!this.initialized) {
            try {
               String[] split = ReflectionUtils.PackageType.getServerVersion().split("_");
               version = Integer.parseInt(split[1]);
               if (version > 7 && version < 13) {
                  this.enumParticle = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("EnumParticle");
               }

               Class<?> packetClass = ReflectionUtils.PackageType.MINECRAFT_SERVER
                  .getClass(version < 7 ? "Packet63WorldParticles" : "PacketPlayOutWorldParticles");
               this.packetConstructor = ReflectionUtils.getConstructor(packetClass);
               this.getHandle = ReflectionUtils.getMethod("CraftPlayer", ReflectionUtils.PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
               this.playerConnection = ReflectionUtils.getField("EntityPlayer", ReflectionUtils.PackageType.MINECRAFT_SERVER, false, "playerConnection");
               this.sendPacket = ReflectionUtils.getMethod(
                  this.playerConnection.getType(), "sendPacket", ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("Packet")
               );
            } catch (Exception e) {
               throw new ParticleMaker.ParticlePacket.VersionIncompatibleException(
                  "Your current bukkit version seems to be incompatible with this library", e
               );
            }

            this.initialized = true;
         }
      }

      @Deprecated
      public boolean isInitialized() {
         return this.initialized;
      }

      @Deprecated
      public void send(AbstractLocation center, Player player) throws ParticleMaker.ParticlePacket.PacketInstantiationException, ParticleMaker.ParticlePacket.PacketSendingException {
         try {
            ReflectionUtils.setValue(this.packet, true, "b", (float)center.getX());
            ReflectionUtils.setValue(this.packet, true, "c", (float)center.getY());
            ReflectionUtils.setValue(this.packet, true, "d", (float)center.getZ());
         } catch (Exception e) {
            throw new ParticleMaker.ParticlePacket.PacketInstantiationException("Setting particle location failed", e);
         }

         try {
            this.sendPacket.invoke(this.playerConnection.get(this.getHandle.invoke(player)), this.packet);
         } catch (Exception ex) {
            throw new ParticleMaker.ParticlePacket.PacketSendingException("Failed to send the packet to player '" + player.getName() + "'", ex);
         }
      }

      @Deprecated
      public void send(AbstractLocation center, List<Player> players) throws IllegalArgumentException {
         if (players.isEmpty()) {
            throw new IllegalArgumentException("The player list is empty");
         } else {
            for (Player player : players) {
               this.send(center, player);
            }
         }
      }

      @Deprecated
      public void send(AbstractLocation center, double range) throws IllegalArgumentException {
         if (range < 1.0) {
            throw new IllegalArgumentException("The range is lower than 1");
         } else {
            Location c = BukkitAdapter.adapt(center);
            String worldName = center.getWorld().getName();
            double squared = range * range;

            for (Player player : Bukkit.getOnlinePlayers()) {
               if (player.getWorld().getName().equals(worldName) && !(player.getLocation().distanceSquared(c) > squared)) {
                  this.send(center, player);
               }
            }
         }
      }

      @Deprecated
      public void sendAsync(final AbstractLocation center, final int viewDistance) {
         final Location c = BukkitAdapter.adapt(center);
         (new BukkitRunnable() {
               public void run() {
                  try {
                     ReflectionUtils.setValue(ParticlePacket.this.packet, true, "b", (float)center.getX());
                     ReflectionUtils.setValue(ParticlePacket.this.packet, true, "c", (float)center.getY());
                     ReflectionUtils.setValue(ParticlePacket.this.packet, true, "d", (float)center.getZ());
                  } catch (Exception e) {
                     throw new ParticleMaker.ParticlePacket.PacketInstantiationException("Setting particle location failed", e);
                  }

                  for (AbstractPlayer player : center.getWorld().getPlayers()) {
                     Player p = BukkitAdapter.adapt(player);
                     if (p.getWorld().equals(center.getWorld()) && p.getLocation().distanceSquared(c) <= viewDistance) {
                        try {
                           ParticlePacket.this.sendPacket
                              .invoke(ParticlePacket.this.playerConnection.get(ParticlePacket.this.getHandle.invoke(p)), ParticlePacket.this.packet);
                        } catch (Exception ex) {
                           ex.printStackTrace();
                        }
                     }
                  }
               }
            })
            .runTaskAsynchronously(MythicBukkit.inst());
      }

      @Deprecated
      public void sendAsync(final List<AbstractLocation> ll, final int viewDistance) {
         (new BukkitRunnable() {
               public void run() {
                  for (AbstractLocation center : ll) {
                     try {
                        ReflectionUtils.setValue(ParticlePacket.this.packet, true, "b", (float)center.getX());
                        ReflectionUtils.setValue(ParticlePacket.this.packet, true, "c", (float)center.getY());
                        ReflectionUtils.setValue(ParticlePacket.this.packet, true, "d", (float)center.getZ());
                     } catch (Exception e) {
                        throw new ParticleMaker.ParticlePacket.PacketInstantiationException("Setting particle location failed", e);
                     }

                     Location c = BukkitAdapter.adapt(center);

                     for (AbstractPlayer player : center.getWorld().getPlayers()) {
                        Player p = BukkitAdapter.adapt(player);
                        if (p.getWorld().equals(center.getWorld()) && p.getLocation().distanceSquared(c) <= viewDistance) {
                           try {
                              ParticlePacket.this.sendPacket
                                 .invoke(ParticlePacket.this.playerConnection.get(ParticlePacket.this.getHandle.invoke(p)), ParticlePacket.this.packet);
                           } catch (Exception ex) {
                              ex.printStackTrace();
                           }
                        }
                     }
                  }
               }
            })
            .runTaskAsynchronously(MythicBukkit.inst());
      }

      private static final class PacketInstantiationException extends RuntimeException {
         private static final long serialVersionUID = 3203085387160737484L;

         public PacketInstantiationException(String message, Throwable cause) {
            super(message, cause);
         }
      }

      private static final class PacketSendingException extends RuntimeException {
         private static final long serialVersionUID = 3203085387160737484L;

         public PacketSendingException(String message, Throwable cause) {
            super(message, cause);
         }
      }

      private static final class VersionIncompatibleException extends RuntimeException {
         private static final long serialVersionUID = 3203085387160737484L;

         public VersionIncompatibleException(String message, Throwable cause) {
            super(message, cause);
         }
      }
   }

   public static enum ParticleType {
      EXPLODE("explode", 0, -1, "EXPLODE", "EXPLOSION", "EXPLOSION_SMALL"),
      EXPLOSION_LARGE("largeexplode", 1, -1, "largeexplosion", "EXPLOSION_LARGE"),
      EXPLOSION_HUGE("hugeexplosion", 2, -1, "hugeexplode", "EXPLOSION_HUGE"),
      FIREWORKS_SPARK("fireworksSpark", 3, -1, "FIREWORKS_SPARK"),
      WATER_BUBBLE("bubble", 4, -1, "WATER_BUBBLE"),
      WATER_SPLASH("splash", 5, -1, "WATER_SPLASH"),
      WATER_WAKE("wake", 6, 7, "WATER_WAKE"),
      SUSPENDED("suspended", 7, -1),
      SUSPENDED_DEPTH("depthSuspend", 8, -1, "SUSPENDED_DEPTH"),
      CRIT("crit", 9, -1),
      CRIT_MAGIC("magicCrit", 10, -1, "CRIT_MAGIC"),
      SMOKE_NORMAL("smoke", 11, -1, "SMOKE_NORMAL"),
      SMOKE_LARGE("largesmoke", 12, -1, "SMOKE_LARGE"),
      SPELL("spell", 13, -1),
      SPELL_INSTANT("instantSpell", 14, -1, "SPELL_INSTANT"),
      SPELL_MOB("mobSpell", 15, -1, "SPELL_MOB"),
      SPELL_MOB_AMBIENT("mobSpellAmbient", 16, -1, "SPELL_MOB_AMBIENT"),
      WITCH("witchMagic", 17, -1, "SPELL_WITCH", "WITCH"),
      DRIP_WATER("dripWater", 18, -1),
      DRIP_LAVA("dripLava", 19, -1),
      VILLAGER_ANGRY("angryVillager", 20, -1, "VILLAGER_ANGRY"),
      VILLAGER_HAPPY("happyVillager", 21, -1, "VILLAGER_HAPPY"),
      TOWN_AURA("townaura", 22, -1, "TOWN_AURA"),
      NOTE("note", 23, -1),
      PORTAL("portal", 24, -1),
      ENCHANTMENT_TABLE("enchantmenttable", 25, -1, "ENCHANTMENT_TABLE", "enchanttable"),
      FLAME("flame", 26, -1),
      LAVA("lava", 27, -1),
      FOOTSTEP("footstep", 28, -1),
      CLOUD("cloud", 29, -1),
      REDSTONE("reddust", 30, -1),
      SNOWBALL("snowballpoof", 31, -1),
      SNOW_SHOVEL("snowshovel", 32, -1, "SNOW_SHOVEL"),
      SLIME("slime", 33, -1),
      HEART("heart", 34, -1),
      BARRIER("barrier", 35, 8),
      ITEM_CRACK("iconcrack", 36, -1, "ITEM_CRACK"),
      BLOCK_CRACK("blockcrack", 37, -1),
      BLOCK_DUST("blockdust", 38, 7),
      WATER_DROP("droplet", 39, 8),
      ITEM_TAKE("take", 40, 8),
      MOB_APPEARANCE("mobappearance", 41, 8),
      END_ROD("endRod", 43, 9),
      DRAGON_BREATH("dragonbreath", 42, 9),
      DAMAGE_INDICATOR("damageIndicator", 44, 9),
      SWEEP_ATTACK("sweepAttack", 45, 9),
      FALLING_DUST("fallingdust", 46, 10),
      TOTEM("totem", 47, 11),
      SPIT("spit", 48, 11);

      private static final Map<String, ParticleMaker.ParticleType> LOOKUP_MAP = new HashMap<>();
      private final String name;
      private final int id;
      private final int requiredVersion;
      private final List<String> aliases;

      private ParticleType(String name, int id, int requiredVersion, String... aliases) {
         if (name.contains("_")) {
            String[] split = name.split("_");
            name = split[0];
         }

         this.name = name;
         this.id = id;
         this.requiredVersion = requiredVersion;
         this.aliases = Arrays.asList(aliases);
      }

      public static ParticleMaker.ParticleType get(String s) {
         if (s.contains("_")) {
            String[] split = s.split("_");
            s = split[0];
         }

         s = s.toUpperCase();
         return LOOKUP_MAP.containsKey(s) ? LOOKUP_MAP.get(s) : null;
      }

      public String getName() {
         return this.name;
      }

      public int getId() {
         return this.id;
      }

      static {
         for (ParticleMaker.ParticleType t : values()) {
            LOOKUP_MAP.put(t.name.toUpperCase(), t);

            for (String a : t.aliases) {
               LOOKUP_MAP.put(a.toUpperCase(), t);
            }
         }
      }
   }
}
