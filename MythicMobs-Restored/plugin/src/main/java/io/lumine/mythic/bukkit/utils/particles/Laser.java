package io.lumine.mythic.bukkit.utils.particles;

import io.lumine.mythic.bukkit.utils.particles.reflection.MappedReflectionAccessor;
import io.lumine.mythic.bukkit.utils.particles.reflection.ReflectionAccessor;
import io.lumine.mythic.bukkit.utils.particles.reflection.TransparentReflectionAccessor;
import io.lumine.mythic.bukkit.utils.particles.reflection.Version;
import io.lumine.mythic.bukkit.utils.particles.reflection.mappings.Mappings;
import io.lumine.mythic.bukkit.utils.particles.reflection.mappings.files.MappingFileReader;
import io.lumine.mythic.bukkit.utils.particles.reflection.mappings.files.ProguardMapping;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public abstract class Laser {
   protected final int distanceSquared;
   protected final int duration;
   protected boolean durationInTicks = false;
   protected Location start;
   protected Location end;
   protected Plugin plugin;
   protected BukkitRunnable main;
   protected BukkitTask startMove;
   protected BukkitTask endMove;
   protected Set<Player> show = ConcurrentHashMap.newKeySet();
   private Set<Player> seen = new HashSet<>();
   private List<Runnable> executeEnd = new ArrayList<>(1);

   protected Laser(Location start, Location end, int duration, int distance) {
      Laser.Packets.ensureInitialized();
      if (start.getWorld() != end.getWorld()) {
         throw new IllegalArgumentException("Locations do not belong to the same worlds.");
      } else {
         this.start = start.clone();
         this.end = end.clone();
         this.duration = duration;
         this.distanceSquared = distance < 0 ? -1 : distance * distance;
      }
   }

   public Laser executeEnd(Runnable runnable) {
      this.executeEnd.add(runnable);
      return this;
   }

   public Laser durationInTicks() {
      this.durationInTicks = true;
      return this;
   }

   public void start(Plugin plugin) {
      if (this.main != null) {
         throw new IllegalStateException("Task already started");
      } else {
         this.plugin = plugin;
         this.main = new BukkitRunnable() {
            int time = 0;

            public void run() {
               try {
                  if (this.time == Laser.this.duration) {
                     this.cancel();
                     return;
                  }

                  if (!Laser.this.durationInTicks || this.time % 20 == 0) {
                     for (Player p : Laser.this.start.getWorld().getPlayers()) {
                        if (Laser.this.isCloseEnough(p)) {
                           if (Laser.this.show.add(p)) {
                              Laser.this.sendStartPackets(p, !Laser.this.seen.add(p));
                           }
                        } else if (Laser.this.show.remove(p)) {
                           Laser.this.sendDestroyPackets(p);
                        }
                     }
                  }

                  this.time++;
               } catch (ReflectiveOperationException e) {
                  e.printStackTrace();
               }
            }

            public synchronized void cancel() throws IllegalStateException {
               super.cancel();
               Laser.this.main = null;

               try {
                  for (Player p : Laser.this.show) {
                     Laser.this.sendDestroyPackets(p);
                  }

                  Laser.this.show.clear();
                  Laser.this.executeEnd.forEach(Runnable::run);
               } catch (ReflectiveOperationException ex) {
                  ex.printStackTrace();
               }
            }
         };
         this.main.runTaskTimerAsynchronously(plugin, 0L, this.durationInTicks ? 1L : 20L);
      }
   }

   public void stop() {
      if (this.main == null) {
         throw new IllegalStateException("Task not started");
      } else {
         this.main.cancel();
      }
   }

   public boolean isStarted() {
      return this.main != null;
   }

   public abstract Laser.LaserType getLaserType();

   public abstract void moveStart(Location location) throws ReflectiveOperationException;

   public abstract void moveEnd(Location location) throws ReflectiveOperationException;

   public Location getStart() {
      return this.start.clone();
   }

   public Location getEnd() {
      return this.end.clone();
   }

   public void moveStart(Location location, int ticks, Runnable callback) {
      this.startMove = this.moveInternal(location, ticks, this.startMove, this.getStart(), this::moveStart, callback);
   }

   public void moveEnd(Location location, int ticks, Runnable callback) {
      this.endMove = this.moveInternal(location, ticks, this.endMove, this.getEnd(), this::moveEnd, callback);
   }

   private BukkitTask moveInternal(
      final Location location,
      final int ticks,
      BukkitTask oldTask,
      final Location from,
      final Laser.ReflectiveConsumer<Location> moveConsumer,
      final Runnable callback
   ) {
      if (ticks <= 0) {
         throw new IllegalArgumentException("Ticks must be a positive value");
      } else if (this.plugin == null) {
         throw new IllegalStateException("The laser must have been started a least once");
      } else {
         if (oldTask != null && !oldTask.isCancelled()) {
            oldTask.cancel();
         }

         return (new BukkitRunnable() {
            double xPerTick = (location.getX() - from.getX()) / ticks;
            double yPerTick = (location.getY() - from.getY()) / ticks;
            double zPerTick = (location.getZ() - from.getZ()) / ticks;
            Location loc = from.clone();
            int elapsed = 0;

            public void run() {
               try {
                  this.loc.add(this.xPerTick, this.yPerTick, this.zPerTick);
                  moveConsumer.accept(this.loc);
               } catch (ReflectiveOperationException e) {
                  e.printStackTrace();
                  this.cancel();
                  return;
               }

               if (++this.elapsed == ticks) {
                  this.cancel();
                  if (callback != null) {
                     callback.run();
                  }
               }
            }
         }).runTaskTimer(this.plugin, 0L, 1L);
      }
   }

   protected void moveFakeEntity(Location location, Object fakeEntity) throws ReflectiveOperationException {
      if (fakeEntity != null) {
         Laser.Packets.moveFakeEntity(fakeEntity, location);
      }

      if (this.main != null) {
         Object packet = Laser.Packets.createPacketMoveEntity(fakeEntity);

         for (Player p : this.show) {
            Laser.Packets.sendPackets(p, packet);
         }
      }
   }

   protected abstract void sendStartPackets(Player player, boolean flag) throws ReflectiveOperationException;

   protected abstract void sendDestroyPackets(Player player) throws ReflectiveOperationException;

   protected boolean isCloseEnough(Player player) {
      if (this.distanceSquared == -1) {
         return true;
      } else {
         Location location = player.getLocation();
         return this.getStart().distanceSquared(location) <= this.distanceSquared || this.getEnd().distanceSquared(location) <= this.distanceSquared;
      }
   }

   public static class CrystalLaser extends Laser {
      private Object createCrystalPacket;
      private Object metadataPacketCrystal;
      private Object[] destroyPackets;
      private final Object crystal;
      private final int crystalID = Laser.Packets.generateEID();
      private final Object crystalWatcher;

      public CrystalLaser(Location start, Location end, int duration, int distance) throws ReflectiveOperationException {
         super(start, new Location(end.getWorld(), end.getBlockX(), end.getBlockY(), end.getBlockZ()), duration, distance);
         this.crystal = Laser.Packets.createCrystal(start, UUID.randomUUID(), this.crystalID);
         this.crystalWatcher = Laser.Packets.getEntityData(this.crystal);
         Laser.Packets.setCrystalTarget(this.crystalWatcher, end);
         this.metadataPacketCrystal = Laser.Packets.createPacketMetadata(this.crystalID, this.crystalWatcher);
         this.destroyPackets = Laser.Packets.createPacketsRemoveEntities(this.crystalID);
      }

      private Object getCrystalSpawnPacket() throws ReflectiveOperationException {
         if (this.createCrystalPacket == null) {
            this.createCrystalPacket = Laser.Packets.createPacketEntitySpawnNormal(this.crystal);
         }

         return this.createCrystalPacket;
      }

      @Override
      public Laser.LaserType getLaserType() {
         return Laser.LaserType.ENDER_CRYSTAL;
      }

      @Override
      protected void sendStartPackets(Player p, boolean hasSeen) throws ReflectiveOperationException {
         Laser.Packets.sendPackets(p, this.getCrystalSpawnPacket());
         Laser.Packets.sendPackets(p, this.metadataPacketCrystal);
      }

      @Override
      protected void sendDestroyPackets(Player p) throws ReflectiveOperationException {
         Laser.Packets.sendPackets(p, this.destroyPackets);
      }

      @Override
      public void moveStart(Location location) throws ReflectiveOperationException {
         this.start = location.clone();
         this.createCrystalPacket = null;
         this.moveFakeEntity(this.start, this.crystal);
      }

      @Override
      public void moveEnd(Location location) throws ReflectiveOperationException {
         location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
         if (!this.end.equals(location)) {
            this.end = location;
            if (this.main != null) {
               Laser.Packets.setCrystalTarget(this.crystalWatcher, location);
               this.metadataPacketCrystal = Laser.Packets.createPacketMetadata(this.crystalID, this.crystalWatcher);

               for (Player p : this.show) {
                  Laser.Packets.sendPackets(p, this.metadataPacketCrystal);
               }
            }
         }
      }
   }

   public static class GuardianLaser extends Laser {
      private static AtomicInteger teamID = new AtomicInteger(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
      private final UUID squidUUID = UUID.randomUUID();
      private final int squidID = Laser.Packets.generateEID();
      private Object squid;
      private Object squidData;
      private Object createSquidPacket;
      private Object metadataPacketSquid;
      private final UUID guardianUUID = UUID.randomUUID();
      private final int guardianID = Laser.Packets.generateEID();
      private Object guardian;
      private Object guardianData;
      private Object createGuardianPacket;
      private Object metadataPacketGuardian;
      private int targetID;
      private UUID targetUUID;
      private Object teamCreatePacket;
      private Object[] destroyPackets;
      protected LivingEntity endEntity;
      private Location correctStart;
      private Location correctEnd;

      public GuardianLaser(Location start, Location end, int duration, int distance) throws ReflectiveOperationException {
         super(start, end, duration, distance);
         this.initSquid();
         this.initLaser();
         this.setTargetEntity(this.squidUUID, this.squidID);
      }

      public GuardianLaser(Location start, LivingEntity endEntity, int duration, int distance) throws ReflectiveOperationException {
         super(start, endEntity.getLocation(), duration, distance);
         this.initLaser();
         this.setTargetEntity(endEntity.getUniqueId(), endEntity.getEntityId());
      }

      private void initLaser() throws ReflectiveOperationException {
         this.guardian = Laser.Packets.createGuardian(this.getCorrectStart(), this.guardianUUID, this.guardianID);
         this.guardianData = Laser.Packets.getEntityData(this.guardian);
         this.teamCreatePacket = Laser.Packets.createPacketTeamCreate("noclip" + teamID.getAndIncrement(), this.squidUUID, this.guardianUUID);
         this.destroyPackets = Laser.Packets.createPacketsRemoveEntities(this.squidID, this.guardianID);
      }

      private void initSquid() throws ReflectiveOperationException {
         this.squid = Laser.Packets.createSquid(this.getCorrectEnd(), this.squidUUID, this.squidID);
         this.squidData = Laser.Packets.getEntityData(this.squid);
         this.metadataPacketSquid = Laser.Packets.createPacketMetadata(this.squidID, this.squidData);
      }

      private Object getGuardianSpawnPacket() throws ReflectiveOperationException {
         if (this.createGuardianPacket == null) {
            this.createGuardianPacket = Laser.Packets.createPacketEntitySpawnLiving(this.guardian);
         }

         return this.createGuardianPacket;
      }

      private Object getSquidSpawnPacket() throws ReflectiveOperationException {
         if (this.createSquidPacket == null) {
            this.createSquidPacket = Laser.Packets.createPacketEntitySpawnLiving(this.squid);
         }

         return this.createSquidPacket;
      }

      @Override
      public Laser.LaserType getLaserType() {
         return Laser.LaserType.GUARDIAN;
      }

      public void attachEndEntity(LivingEntity entity) throws ReflectiveOperationException {
         if (entity.getWorld() != this.start.getWorld()) {
            throw new IllegalArgumentException("Attached entity is not in the same world as the laser.");
         } else {
            this.endEntity = entity;
            this.setTargetEntity(entity.getUniqueId(), entity.getEntityId());
         }
      }

      public Entity getEndEntity() {
         return this.endEntity;
      }

      private void setTargetEntity(UUID uuid, int id) throws ReflectiveOperationException {
         this.targetUUID = uuid;
         this.targetID = id;
         Laser.Packets.setGuardianTarget(this.guardianData, this.targetID);
         this.metadataPacketGuardian = Laser.Packets.createPacketMetadata(this.guardianID, this.guardianData);

         for (Player p : this.show) {
            Laser.Packets.sendPackets(p, this.metadataPacketGuardian);
         }
      }

      @Override
      public Location getEnd() {
         return this.endEntity == null ? this.end : this.endEntity.getLocation();
      }

      protected Location getCorrectStart() {
         if (this.correctStart == null) {
            this.correctStart = this.start.clone();
            this.correctStart.subtract(0.0, 0.5, 0.0);
         }

         return this.correctStart;
      }

      protected Location getCorrectEnd() {
         if (this.correctEnd == null) {
            this.correctEnd = this.end.clone();
            this.correctEnd.subtract(0.0, 0.5, 0.0);
            Vector corrective = this.correctEnd.toVector().subtract(this.getCorrectStart().toVector()).normalize();
            if (Double.isNaN(corrective.getX())) {
               corrective.setX(0);
            }

            if (Double.isNaN(corrective.getY())) {
               corrective.setY(0);
            }

            if (Double.isNaN(corrective.getZ())) {
               corrective.setZ(0);
            }

            this.correctEnd.subtract(corrective);
         }

         return this.correctEnd;
      }

      @Override
      protected boolean isCloseEnough(Player player) {
         return player == this.endEntity || super.isCloseEnough(player);
      }

      @Override
      protected void sendStartPackets(Player p, boolean hasSeen) throws ReflectiveOperationException {
         if (this.squid == null) {
            Laser.Packets.sendPackets(p, this.getGuardianSpawnPacket(), this.metadataPacketGuardian);
         } else {
            Laser.Packets.sendPackets(p, this.getGuardianSpawnPacket(), this.getSquidSpawnPacket(), this.metadataPacketGuardian, this.metadataPacketSquid);
         }

         if (!hasSeen) {
            Laser.Packets.sendPackets(p, this.teamCreatePacket);
         }
      }

      @Override
      protected void sendDestroyPackets(Player p) throws ReflectiveOperationException {
         Laser.Packets.sendPackets(p, this.destroyPackets);
      }

      @Override
      public void moveStart(Location location) throws ReflectiveOperationException {
         this.start = location.clone();
         this.correctStart = null;
         this.createGuardianPacket = null;
         this.moveFakeEntity(this.getCorrectStart(), this.guardian);
         if (this.squid != null) {
            this.correctEnd = null;
            this.createSquidPacket = null;
            this.moveFakeEntity(this.getCorrectEnd(), this.squid);
         }
      }

      @Override
      public void moveEnd(Location location) throws ReflectiveOperationException {
         this.end = location.clone();
         this.createSquidPacket = null;
         this.correctEnd = null;
         if (this.squid == null) {
            this.initSquid();

            for (Player p : this.show) {
               Laser.Packets.sendPackets(p, this.getSquidSpawnPacket(), this.metadataPacketSquid);
            }
         } else {
            this.moveFakeEntity(this.getCorrectEnd(), this.squid);
         }

         if (this.targetUUID != this.squidUUID) {
            this.endEntity = null;
            this.setTargetEntity(this.squidUUID, this.squidID);
         }
      }

      public void callColorChange() throws ReflectiveOperationException {
         for (Player p : this.show) {
            Laser.Packets.sendPackets(p, this.metadataPacketGuardian);
         }
      }
   }

   public static enum LaserType {
      GUARDIAN,
      ENDER_CRYSTAL;

      public Laser create(Location start, Location end, int duration, int distance) throws ReflectiveOperationException {
         switch (this) {
            case ENDER_CRYSTAL:
               return new Laser.CrystalLaser(start, end, duration, distance);
            case GUARDIAN:
               return new Laser.GuardianLaser(start, end, duration, distance);
            default:
               throw new IllegalStateException();
         }
      }
   }

   protected static class Packets {
      private static AtomicInteger lastIssuedEID = new AtomicInteger(2000000000);
      private static Logger logger;
      private static String cpack;
      private static Version version;
      private static boolean isEnabled = false;
      private static boolean hasInitialized = false;
      private static Throwable initializationError = null;
      private static Object squidType;
      private static Object guardianType;
      private static Constructor<?> crystalConstructor;
      private static Constructor<?> squidConstructor;
      private static Constructor<?> guardianConstructor;
      private static Object dataAccessorFlags;
      private static Object dataAccessorGuardianMoving;
      private static Object dataAccessorGuardianTarget;
      private static Object dataAccessorCrystalTarget;
      private static Object dataAccessorCrystalBottom;
      private static ReflectionAccessor.ClassAccessor dataWatcherClass;
      private static ReflectionAccessor.ClassAccessor dataAccessorClass;
      private static Method watcherSet;
      private static Method watcherDirty;
      private static Method watcherPack;
      private static Constructor<?> blockPositionConstructor;
      private static Constructor<?> packetSpawnLiving;
      private static Constructor<?> packetSpawnNormal;
      private static Constructor<?> packetRemove;
      private static Constructor<?> packetTeleport;
      private static Method packetTeleportOf;
      private static Constructor<?> packetMetadata;
      private static ReflectionAccessor.ClassAccessor packetTeam;
      private static Method createTeamPacket;
      private static Constructor<?> createTeam;
      private static Constructor<?> createScoreboard;
      private static Method setTeamPush;
      private static Object pushNever;
      private static Method getTeamPlayers;
      private static Method getPlayerHandle;
      private static Field playerConnection;
      private static Method sendPacket;
      private static Method getData;
      private static Field entityBlockPosition;
      private static Method setLocation;
      private static Method setUUID;
      private static Method setID;
      private static Object nmsWorld;

      static int generateEID() {
         return lastIssuedEID.getAndIncrement();
      }

      protected static void ensureInitialized() {
         if (!hasInitialized) {
            initialize();
         }

         if (!isEnabled) {
            throw new IllegalStateException("The GuardianBeam API is disabled. An error has occured during first initialization.", initializationError);
         }
      }

      private static void initialize() {
         hasInitialized = true;

         try {
            logger = new Logger("GuardianBeam", null) {
               @Override
               public void log(LogRecord logRecord) {
                  logRecord.setMessage("[GuardianBeam] " + logRecord.getMessage());
                  super.log(logRecord);
               }
            };
            logger.setParent(Bukkit.getServer().getLogger());
            logger.setLevel(Level.ALL);
            String versionString = Bukkit.getBukkitVersion().split("-R")[0];
            Version serverVersion = Version.parse(versionString);
            logger.info("Found server version " + serverVersion);
            cpack = Bukkit.getServer().getClass().getPackage().getName() + ".";
            boolean remapped = Bukkit.getServer().getClass().getPackage().getName().split("\\.").length == 3;
            ReflectionAccessor reflection;
            if (remapped) {
               version = serverVersion;
               reflection = new TransparentReflectionAccessor();
               logger.info("Loaded transparent mappings.");
            } else {
               String mappingsFile = new String(Laser.class.getResourceAsStream("mappings/spigot.txt").readAllBytes());
               MappingFileReader mappingsReader = new MappingFileReader(new ProguardMapping(false), mappingsFile.lines().toList());
               Optional<Version> foundVersion = mappingsReader.keepBestMatchedVersion(serverVersion);
               if (foundVersion.isEmpty()) {
                  throw new UnsupportedOperationException("Cannot find mappings to match server version");
               }

               if (!foundVersion.get().is(serverVersion)) {
                  logger.warning("Loaded not matching version of the mappings for your server version");
               }

               version = foundVersion.get();
               mappingsReader.parseMappings();
               Mappings mappings = mappingsReader.getParsedMappings(foundVersion.get());
               logger.info("Loaded mappings for " + version);
               reflection = new MappedReflectionAccessor(mappings);
            }

            loadReflection(reflection, version);
            isEnabled = true;
         } catch (Exception e) {
            initializationError = e;
            String errorMsg = "Lasers reflection failed to initialize. The util is disabled. Please ensure your version ("
               + Bukkit.getBukkitVersion()
               + ") is supported.";
            if (logger == null) {
               e.printStackTrace();
               System.err.println(errorMsg);
            } else {
               logger.log(Level.SEVERE, errorMsg, (Throwable)e);
            }
         }
      }

      protected static void loadReflection(@NotNull ReflectionAccessor reflection, @NotNull Version version) throws ReflectiveOperationException {
         ReflectionAccessor.ClassAccessor entityTypesClass = getNMSClass(reflection, "world.entity", "EntityType");
         ReflectionAccessor.ClassAccessor entityClass = getNMSClass(reflection, "world.entity", "Entity");
         ReflectionAccessor.ClassAccessor crystalClass = getNMSClass(reflection, "world.entity.boss.enderdragon", "EndCrystal");
         ReflectionAccessor.ClassAccessor squidClass = getNMSClass(
            reflection, version.isAfter(1, 21, 11) ? "world.entity.animal.squid" : "world.entity.animal", "Squid"
         );
         ReflectionAccessor.ClassAccessor guardianClass = getNMSClass(reflection, "world.entity.monster", "Guardian");
         ReflectionAccessor.ClassAccessor blockPosClass = getNMSClass(reflection, "core", "BlockPos");
         dataAccessorFlags = entityClass.getField("DATA_SHARED_FLAGS_ID").get(null);
         dataAccessorGuardianMoving = guardianClass.getField("DATA_ID_MOVING").get(null);
         dataAccessorGuardianTarget = guardianClass.getField("DATA_ID_ATTACK_TARGET").get(null);
         dataAccessorCrystalTarget = crystalClass.getField("DATA_BEAM_TARGET").get(null);
         dataAccessorCrystalBottom = crystalClass.getField("DATA_SHOW_BOTTOM").get(null);
         squidType = entityTypesClass.getField("SQUID").get(null);
         guardianType = entityTypesClass.getField("GUARDIAN").get(null);
         dataWatcherClass = getNMSClass(reflection, "network.syncher", "SynchedEntityData");
         dataAccessorClass = getNMSClass(reflection, "network.syncher", "EntityDataAccessor");
         if (version.isAfter(1, 19, 4)) {
            watcherSet = dataWatcherClass.getMethodInstance("set", dataAccessorClass, Object.class, boolean.class);
            watcherPack = dataWatcherClass.getMethodInstance("packDirty");
         } else {
            watcherSet = dataWatcherClass.getMethodInstance("set", dataAccessorClass, Object.class);
            if (cpack != null) {
               watcherDirty = dataWatcherClass.getClassInstance().getDeclaredMethod("markDirty", dataAccessorClass.getClassInstance());
            }
         }

         packetSpawnNormal = getNMSClass(reflection, "network.protocol.game", "ClientboundAddEntityPacket")
            .getConstructorInstance(version.isBefore(1, 21, 0) ? new Type[]{entityClass} : new Type[]{entityClass, int.class, blockPosClass});
         if (version.isBefore(1, 19, 0)) {
            packetSpawnLiving = getNMSClass(reflection, "network.protocol.game", "ClientboundAddMobPacket")
               .getConstructorInstance(getNMSClass(reflection, "world.entity", "LivingEntity"));
         }

         packetRemove = version.is(1, 17, 0)
            ? getNMSClass(reflection, "network.protocol.game", "ClientboundRemoveEntityPacket").getConstructorInstance(int.class)
            : getNMSClass(reflection, "network.protocol.game", "ClientboundRemoveEntitiesPacket").getConstructorInstance(int[].class);
         packetMetadata = getNMSClass(reflection, "network.protocol.game", "ClientboundSetEntityDataPacket")
            .getConstructorInstance(version.isBefore(1, 19, 3) ? new Type[]{int.class, dataWatcherClass, boolean.class} : new Type[]{int.class, List.class});
         if (version.isBefore(1, 21, 2)) {
            packetTeleport = getNMSClass(reflection, "network.protocol.game", "ClientboundTeleportEntityPacket").getConstructorInstance(entityClass);
         } else {
            packetTeleportOf = getNMSClass(reflection, "network.protocol.game", "ClientboundEntityPositionSyncPacket").getMethodInstance("of", entityClass);
         }

         blockPositionConstructor = getNMSClass(reflection, "core", "BlockPos").getConstructorInstance(int.class, int.class, int.class);
         ReflectionAccessor.ClassAccessor levelClass = getNMSClass(reflection, "world.level", "Level");
         squidConstructor = squidClass.getConstructorInstance(entityTypesClass, levelClass);
         guardianConstructor = guardianClass.getConstructorInstance(entityTypesClass, levelClass);
         crystalConstructor = crystalClass.getConstructorInstance(levelClass, double.class, double.class, double.class);
         playerConnection = getNMSClass(reflection, "server.level", "ServerPlayer").getFieldInstance("connection");
         ReflectionAccessor.ClassAccessor packetListenerClass = getNMSClass(
            reflection, "server.network", version.isAfter(1, 20, 2) ? "ServerCommonPacketListenerImpl" : "ServerGamePacketListenerImpl"
         );
         sendPacket = packetListenerClass.getMethodInstance("send", getNMSClass(reflection, "network.protocol", "Packet"));
         getData = entityClass.getMethodInstance("getEntityData");
         setLocation = entityClass.getMethodInstance(
            version.isAfter(1, 21, 5) ? "absSnapTo" : "absMoveTo", double.class, double.class, double.class, float.class, float.class
         );
         entityBlockPosition = entityClass.getFieldInstance("blockPosition");
         setUUID = entityClass.getMethodInstance("setUUID", UUID.class);
         setID = entityClass.getMethodInstance("setId", int.class);
         ReflectionAccessor.ClassAccessor scoreboardClass = getNMSClass(reflection, "world.scores", "Scoreboard");
         ReflectionAccessor.ClassAccessor teamClass = getNMSClass(reflection, "world.scores", "PlayerTeam");
         ReflectionAccessor.ClassAccessor pushClass = getNMSClass(reflection, "world.scores", "Team$CollisionRule");
         packetTeam = getNMSClass(reflection, "network.protocol.game", "ClientboundSetPlayerTeamPacket");
         createTeamPacket = packetTeam.getMethodInstance("createAddOrModifyPacket", teamClass, boolean.class);
         createTeam = teamClass.getConstructorInstance(scoreboardClass, String.class);
         createScoreboard = scoreboardClass.getConstructorInstance();
         setTeamPush = teamClass.getMethodInstance("setCollisionRule", pushClass);
         pushNever = pushClass.getField("NEVER").get(null);
         getTeamPlayers = teamClass.getMethodInstance("getPlayers");
         if (cpack != null) {
            getPlayerHandle = Class.forName(cpack + "entity.CraftPlayer").getDeclaredMethod("getHandle");
            nmsWorld = Class.forName(cpack + "CraftWorld").getDeclaredMethod("getHandle").invoke(Bukkit.getWorlds().get(0));
         }
      }

      public static void sendPackets(Player p, Object... packets) throws ReflectiveOperationException {
         Object connection = playerConnection.get(getPlayerHandle.invoke(p));

         for (Object packet : packets) {
            if (packet != null) {
               sendPacket.invoke(connection, packet);
            }
         }
      }

      public static Object createSquid(Location location, UUID uuid, int id) throws ReflectiveOperationException {
         Object entity = squidConstructor.newInstance(squidType, nmsWorld);
         setEntityIDs(entity, uuid, id);
         moveFakeEntity(entity, location);
         Object data = getEntityData(entity);
         setEntityData(data, dataAccessorFlags, (byte)32);
         return entity;
      }

      public static Object createGuardian(Location location, UUID uuid, int id) throws ReflectiveOperationException {
         Object entity = guardianConstructor.newInstance(guardianType, nmsWorld);
         setEntityIDs(entity, uuid, id);
         moveFakeEntity(entity, location);
         return entity;
      }

      public static Object createCrystal(Location location, UUID uuid, int id) throws ReflectiveOperationException {
         Object entity = crystalConstructor.newInstance(nmsWorld, location.getX(), location.getY(), location.getZ());
         setEntityIDs(entity, uuid, id);
         return entity;
      }

      public static Object getEntityData(Object entity) throws ReflectiveOperationException {
         return getData.invoke(entity);
      }

      public static Object createPacketEntitySpawnLiving(Object entity) throws ReflectiveOperationException {
         return packetSpawnLiving == null ? createPacketEntitySpawnNormal(entity) : packetSpawnLiving.newInstance(entity);
      }

      public static Object createPacketEntitySpawnNormal(Object entity) throws ReflectiveOperationException {
         if (version.isAfter(1, 21, 0)) {
            Object entityPos = entityBlockPosition.get(entity);
            return packetSpawnNormal.newInstance(entity, 0, entityPos);
         } else {
            return packetSpawnNormal.newInstance(entity);
         }
      }

      public static void setGuardianTarget(Object watcher, int targetId) throws ReflectiveOperationException {
         setEntityData(watcher, dataAccessorGuardianTarget, targetId);
         setEntityData(watcher, dataAccessorFlags, (byte)32);
         setEntityData(watcher, dataAccessorGuardianMoving, Boolean.FALSE);
      }

      public static void setCrystalTarget(Object watcher, Location target) throws ReflectiveOperationException {
         Object blockPosition = blockPositionConstructor.newInstance(target.getBlockX(), target.getBlockY(), target.getBlockZ());
         setEntityData(watcher, dataAccessorCrystalTarget, Optional.of(blockPosition));
         setEntityData(watcher, dataAccessorCrystalBottom, Boolean.FALSE);
      }

      public static Object[] createPacketsRemoveEntities(int... entitiesId) throws ReflectiveOperationException {
         Object[] packets;
         if (version.is(1, 17, 0)) {
            packets = new Object[entitiesId.length];

            for (int i = 0; i < entitiesId.length; i++) {
               packets[i] = packetRemove.newInstance(entitiesId[i]);
            }
         } else {
            packets = new Object[]{packetRemove.newInstance(entitiesId)};
         }

         return packets;
      }

      public static void setEntityIDs(Object entity, UUID uuid, int id) throws ReflectiveOperationException {
         setUUID.invoke(entity, uuid);
         setID.invoke(entity, id);
      }

      public static void moveFakeEntity(Object entity, Location location) throws ReflectiveOperationException {
         setLocation.invoke(entity, location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
      }

      public static Object createPacketMoveEntity(Object entity) throws ReflectiveOperationException {
         return version.isBefore(1, 21, 2) ? packetTeleport.newInstance(entity) : packetTeleportOf.invoke(null, entity);
      }

      public static Object createPacketTeamCreate(String teamName, UUID... entities) throws ReflectiveOperationException {
         Object team = createTeam.newInstance(createScoreboard.newInstance(), teamName);
         setTeamPush.invoke(team, pushNever);
         Collection<String> players = (Collection<String>)getTeamPlayers.invoke(team);

         for (UUID entity : entities) {
            players.add(entity.toString());
         }

         return createTeamPacket.invoke(null, team, true);
      }

      private static Object createPacketMetadata(int entityId, Object watcher) throws ReflectiveOperationException {
         return version.isBefore(1, 19, 3)
            ? packetMetadata.newInstance(entityId, watcher, false)
            : packetMetadata.newInstance(entityId, watcherPack.invoke(watcher));
      }

      private static void setEntityData(Object watcher, Object watcherObject, Object watcherData) throws ReflectiveOperationException {
         if (version.isAfter(1, 19, 3)) {
            watcherSet.invoke(watcher, watcherObject, watcherData, true);
         } else {
            watcherSet.invoke(watcher, watcherObject, watcherData);
            watcherDirty.invoke(watcher, watcherObject);
         }
      }

      @NotNull
      private static ReflectionAccessor.ClassAccessor getNMSClass(@NotNull ReflectionAccessor reflection, @NotNull String className) throws ClassNotFoundException {
         return reflection.getClass("net.minecraft." + className);
      }

      @NotNull
      private static ReflectionAccessor.ClassAccessor getNMSClass(@NotNull ReflectionAccessor reflection, @NotNull String nmPackage, @NotNull String className) throws ClassNotFoundException {
         return reflection.getClass("net.minecraft." + nmPackage + "." + className);
      }
   }

   @FunctionalInterface
   public interface ReflectiveConsumer<T> {
      void accept(T t) throws ReflectiveOperationException;
   }
}
