package com.nexomc.nexo.mechanics.furniture.culling;

import com.nexomc.nexo.mechanics.furniture.BlockLocation;
import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.FurnitureProperties;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.hitbox.BarrierHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.FurnitureHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.GhastHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.InteractionHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.ShulkerHitbox;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.LocationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.RangeUtilsKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.VersionUtil;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import io.papermc.paper.event.player.PlayerUntrackEntityEvent;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.RangesKt;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0096\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010#\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001:\u0001TB]\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b\u0012\b\b\u0002\u0010\r\u001a\u00020\t\u0012\b\b\u0002\u0010\u000e\u001a\u00020\t¢\u0006\u0004\b\u000f\u0010\u0010B\u0011\b\u0016\u0012\u0006\u0010\u0011\u001a\u00020\u0012¢\u0006\u0004\b\u000f\u0010\u0013J\u000e\u00106\u001a\u0002072\u0006\u00108\u001a\u000209J\b\u0010:\u001a\u000207H\u0002J\u0016\u0010;\u001a\u00020\u00032\u0006\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020*J\u0010\u0010?\u001a\u000207*\u00020@H\u0007b\u0002\bAJ\u0010\u0010B\u001a\u000207*\u00020CH\u0007b\u0002\bAJ\u0010\u0010D\u001a\u000207*\u00020EH\u0007b\u0002\bAJ\u0016\u0010F\u001a\u0002072\u0006\u0010<\u001a\u00020=2\u0006\u0010G\u001a\u00020+J\u0016\u0010H\u001a\u0002072\u0006\u0010<\u001a\u00020=2\u0006\u0010G\u001a\u00020+J\u001a\u0010I\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b0J2\u0006\u0010G\u001a\u00020+J&\u0010K\u001a\u00020\u00032\u0006\u0010L\u001a\u00020M2\u0006\u0010N\u001a\u00020M2\u0006\u0010O\u001a\u00020\u000b2\u0006\u0010P\u001a\u00020\u000bJ\u0018\u0010Q\u001a\u00020\u00032\u0006\u0010R\u001a\u00020M2\u0006\u0010S\u001a\u00020MH\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\u0004\u001a\u00020\u0005¢\u0006\n\n\u0002\u0010\u0018\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\f\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001dR\u0011\u0010\r\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\u000e\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b!\u0010 R\u001b\u0010\"\u001a\u00020#8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b&\u0010'\u001a\u0004\b$\u0010%R)\u0010(\u001a\u001a\u0012\u0004\u0012\u00020*\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020*\u0012\u0004\u0012\u00020+0)0)¢\u0006\b\n\u0000\u001a\u0004\b,\u0010-R \u0010.\u001a\u0014\u0012\u0004\u0012\u00020*\u0012\n\u0012\b\u0012\u0004\u0012\u00020*0/0)X\u0082\u0004¢\u0006\u0002\n\u0000R$\u00102\u001a\u0002012\u0006\u00100\u001a\u000201@@X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b3\u0010\u0017\"\u0004\b4\u00105¨\u0006U"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/culling/FurnitureCullingManager;", "Lorg/bukkit/event/Listener;", "enabled", "", "interval", "Lkotlin/time/Duration;", "forceCulling", "cullingDistanceSquared", "Lkotlin/ranges/ClosedFloatingPointRange;", "", "paddingWidth", "", "paddingHeight", "ignoreWidth", "ignoreHeight", "<init>", "(ZJZLkotlin/ranges/ClosedFloatingPointRange;DDFFLkotlin/jvm/internal/DefaultConstructorMarker;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getEnabled", "()Z", "getInterval-UwyO8pc", "()J", "J", "getForceCulling", "getCullingDistanceSquared", "()Lkotlin/ranges/ClosedFloatingPointRange;", "getPaddingWidth", "()D", "getPaddingHeight", "getIgnoreWidth", "()F", "getIgnoreHeight", "packetManager", "Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacketManager;", "getPacketManager", "()Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacketManager;", "packetManager$delegate", "Lkotlin/Lazy;", "trackedEntities", "Ljava/util/concurrent/ConcurrentHashMap;", "Ljava/util/UUID;", "Lcom/nexomc/nexo/mechanics/furniture/culling/FurnitureCullingManager$TrackedFurniture;", "getTrackedEntities", "()Ljava/util/concurrent/ConcurrentHashMap;", "hiddenFurniture", "", "value", "", "lastCullTime", "getLastCullTime", "setLastCullTime$com_nexomc_core", "(J)V", "startTask", "", "mechanicID", "", "populateTrackedEntities", "isHidden", "player", "Lorg/bukkit/entity/Player;", "furnitureUuid", "onTrack", "Lio/papermc/paper/event/player/PlayerTrackEntityEvent;", "Lorg/bukkit/event/EventHandler;", "onUntrack", "Lio/papermc/paper/event/player/PlayerUntrackEntityEvent;", "onQuit", "Lorg/bukkit/event/player/PlayerQuitEvent;", "ensureVisible", "tracked", "ensureHidden", "effectiveDimensions", "Lkotlin/Pair;", "isOccluded", "eyeLoc", "Lorg/bukkit/Location;", "targetLoc", "width", "height", "isRayOccluded", "origin", "target", "TrackedFurniture", "com.nexomc:core"}
)
public final class FurnitureCullingManager implements Listener {
   private final boolean enabled;
   private final long interval;
   private final boolean forceCulling;
   @NotNull
   private final ClosedFloatingPointRange<Float> cullingDistanceSquared;
   private final double paddingWidth;
   private final double paddingHeight;
   private final float ignoreWidth;
   private final float ignoreHeight;
   @NotNull
   private final Lazy packetManager$delegate;
   @NotNull
   private final ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, TrackedFurniture>> trackedEntities;
   @NotNull
   private final ConcurrentHashMap<UUID, Set<UUID>> hiddenFurniture;
   private long lastCullTime;

   private FurnitureCullingManager(boolean enabled, long interval, boolean forceCulling, ClosedFloatingPointRange<Float> cullingDistanceSquared, double paddingWidth, double paddingHeight, float ignoreWidth, float ignoreHeight) {
      super();
      this.enabled = enabled;
      this.interval = interval;
      this.forceCulling = forceCulling;
      this.cullingDistanceSquared = cullingDistanceSquared;
      this.paddingWidth = paddingWidth;
      this.paddingHeight = paddingHeight;
      this.ignoreWidth = ignoreWidth;
      this.ignoreHeight = ignoreHeight;
      this.packetManager$delegate = LazyKt.lazy(FurnitureCullingManager::packetManager_delegate$lambda$0);
      this.trackedEntities = new ConcurrentHashMap();
      this.hiddenFurniture = new ConcurrentHashMap();
   }

   public final boolean getEnabled() {
      return this.enabled;
   }

   public final long getInterval_UwyO8pc/* $FF was: getInterval-UwyO8pc*/() {
      return this.interval;
   }

   public final boolean getForceCulling() {
      return this.forceCulling;
   }

   @NotNull
   public final ClosedFloatingPointRange<Float> getCullingDistanceSquared() {
      return this.cullingDistanceSquared;
   }

   public final double getPaddingWidth() {
      return this.paddingWidth;
   }

   public final double getPaddingHeight() {
      return this.paddingHeight;
   }

   public final float getIgnoreWidth() {
      return this.ignoreWidth;
   }

   public final float getIgnoreHeight() {
      return this.ignoreHeight;
   }

   private final IFurniturePacketManager getPacketManager() {
      Lazy lazy = this.packetManager$delegate;
      return (IFurniturePacketManager)lazy.getValue();
   }

   @NotNull
   public final ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, TrackedFurniture>> getTrackedEntities() {
      return this.trackedEntities;
   }

   public final long getLastCullTime() {
      return this.lastCullTime;
   }

   public final void setLastCullTime$com_nexomc_core(long value) {
      this.lastCullTime = value;
   }

   public FurnitureCullingManager(@NotNull ConfigurationSection section) {
      long value;
      ClosedFloatingPointRange closedFloatingPointRange;
      boolean flag;
      boolean flag2;
      block: {
         flag = section.getBoolean("enabled", true);
         Duration duration = NexoYamlKt.durationOrNull(section, "interval", (DurationUnit)null);
         value = duration != null ? duration.unbox_impl() : DurationUtilsKt.getTicks(1);
         flag2 = section.getBoolean("force_culling", true);
         closedFloatingPointRange = NexoYamlKt.floatRangeOrNull(section, "distance");
         if (closedFloatingPointRange != null) {
            closedFloatingPointRange = RangeUtilsKt.pow(closedFloatingPointRange, 2);
            if (closedFloatingPointRange != null) {
               break block;
            }
         }

         closedFloatingPointRange = RangesKt.rangeTo(0.0F, 4096.0F);
      }

      this(flag, value, flag2, closedFloatingPointRange, section.getDouble("padding_width", (double)0.0F), section.getDouble("padding_height", (double)0.0F), (float)section.getDouble("ignore_width", (double)0.0F), (float)section.getDouble("ignore_height", (double)0.0F));
   }

   public final void startTask(@NotNull String mechanicID) {
      if (this.enabled && DurationUtilsKt.getInWholeTicks_LRDsOJo(this.interval) >= 1L) {
         this.populateTrackedEntities();
         WrappedTask wrappedTask = SchedulerUtils.INSTANCE.runTaskAsyncTimer(20L, DurationUtilsKt.getInWholeTicks_LRDsOJo(this.interval), (() -> FurnitureCullingManager.startTask$lambda$0(this)));
         if (wrappedTask != null) {
            WrappedTask task = wrappedTask;
            MechanicsManager.INSTANCE.registerTask(mechanicID, task);
         }
      }
   }

   private final void populateTrackedEntities() {
      SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
      Function1 task = (a0_ -> FurnitureCullingManager.populateTrackedEntities$lambda$0(this, (ItemDisplay)a0_));
      List worlds = Bukkit.getWorlds();
      Iterable iterable = (Iterable)worlds;

      for(Object element : iterable) {
         World world = (World)element;
         int value = 0;
         if (VersionUtil.INSTANCE.isFoliaServer()) {
            Chunk[] loadedChunks = world.getLoadedChunks();
            Object[] objs = loadedChunks;
            int value2 = 0;

            for(int i = objs.length; value2 < i; ++value2) {
               Object obj = objs[value2];
               Chunk chunk = (Chunk)obj;
               int value3 = 0;
               SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocation(new Location(world, (double)chunk.getX() * (double)16.0F, (double)100.0F, (double)chunk.getZ() * (double)16.0F), new FurnitureCullingManager$populateTrackedEntities$$inlined$runAtWorldEntities$1(chunk, task));
            }
         } else {
            Collection entitiesByClass = world.getEntitiesByClass(ItemDisplay.class);
            Iterable iterable2 = (Iterable)entitiesByClass;

            for(Object obj2 : iterable2) {
               Entity p0 = (Entity)obj2;
               int value4 = 0;
               task.invoke(p0);
            }
         }
      }

   }

   public final boolean isHidden(@NotNull Player player, @NotNull UUID furnitureUuid) {
      Set set = (Set)this.hiddenFurniture.get(player.getUniqueId());
      return set != null ? set.contains(furnitureUuid) : false;
   }

   @EventHandler
   public final void onTrack(@NotNull PlayerTrackEntityEvent playerTrackEntityEvent) {
      if (this.enabled) {
         Entity entity = playerTrackEntityEvent.getEntity();
         ItemDisplay itemDisplay2 = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
         if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) != null) {
            ItemDisplay itemDisplay = itemDisplay2;
            FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)itemDisplay);
            if (furnitureMechanic != null) {
               FurnitureMechanic mechanic = furnitureMechanic;
               Object obj = this.trackedEntities.computeIfAbsent(playerTrackEntityEvent.getPlayer().getUniqueId(), (a0_ -> FurnitureCullingManager.onTrack$lambda$0((UUID)a0_)));
               Map map = (Map)obj;
               UUID uniqueId = playerTrackEntityEvent.getEntity().getUniqueId();
               UUID uuid = playerTrackEntityEvent.getEntity().getUniqueId();
               TrackedFurniture trackedFurniture = new TrackedFurniture(uuid, itemDisplay, mechanic);
               map.put(uniqueId, trackedFurniture);
            }
         }
      }
   }

   @EventHandler
   public final void onUntrack(@NotNull PlayerUntrackEntityEvent playerUntrackEntityEvent) {
      if (this.enabled) {
         ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap)this.trackedEntities.get(playerUntrackEntityEvent.getPlayer().getUniqueId());
         if (concurrentHashMap != null) {
            TrackedFurniture trackedFurniture = (TrackedFurniture)concurrentHashMap.remove(playerUntrackEntityEvent.getEntity().getUniqueId());
         }

         Set set = (Set)this.hiddenFurniture.get(playerUntrackEntityEvent.getPlayer().getUniqueId());
         if (set != null) {
            set.remove(playerUntrackEntityEvent.getEntity().getUniqueId());
         }

      }
   }

   @EventHandler
   public final void onQuit(@NotNull PlayerQuitEvent playerQuitEvent) {
      if (this.enabled) {
         this.trackedEntities.remove(playerQuitEvent.getPlayer().getUniqueId());
         this.hiddenFurniture.remove(playerQuitEvent.getPlayer().getUniqueId());
      }
   }

   public final void ensureVisible(@NotNull Player player, @NotNull TrackedFurniture tracked) {
      Set set = (Set)this.hiddenFurniture.get(player.getUniqueId());
      if (set != null) {
         Set playerHidden = set;
         if (playerHidden.remove(tracked.getUuid())) {
            this.getPacketManager().showFurniture(tracked.getEntity(), player);
         }
      }
   }

   public final void ensureHidden(@NotNull Player player, @NotNull TrackedFurniture tracked) {
      Object obj = this.hiddenFurniture.computeIfAbsent(player.getUniqueId(), (a0_ -> FurnitureCullingManager.ensureHidden$lambda$0((UUID)a0_)));
      Set playerHidden = (Set)obj;
      if (playerHidden.add(tracked.getUuid())) {
         this.getPacketManager().hideFurniture(tracked.getEntity(), player);
      }
   }

   @NotNull
   public final Pair<Double, Double> effectiveDimensions(@NotNull TrackedFurniture tracked) {
      FurnitureProperties properties = tracked.getMechanic().getProperties();
      double width = (double)properties.getDisplayWidth();
      double height = (double)properties.getDisplayHeight();
      if (width <= (double)0.0F || height <= (double)0.0F) {
         FurnitureHitbox hitbox = tracked.getMechanic().getHitbox();
         float yaw = tracked.getEntity().getLocation().getYaw();
         double minX = Double.MAX_VALUE;
         double maxX = -Double.MAX_VALUE;
         double minY = Double.MAX_VALUE;
         double maxY = -Double.MAX_VALUE;
         double minZ = Double.MAX_VALUE;
         double maxZ = -Double.MAX_VALUE;
         ObjectIterator objectIterator = hitbox.getInteractions().iterator();

         Vector3f off;
         double hw;
         for(ObjectIterator objectIterator2 = objectIterator; objectIterator2.hasNext(); maxZ = Math.max(maxZ, (double)off.z + hw)) {
            InteractionHitbox interaction = (InteractionHitbox)objectIterator2.next();
            off = interaction.offset(yaw);
            hw = (double)interaction.getWidth() / (double)2.0F;
            double hh = (double)interaction.getHeight() / (double)2.0F;
            minX = Math.min(minX, (double)off.x - hw);
            maxX = Math.max(maxX, (double)off.x + hw);
            minY = Math.min(minY, (double)off.y - hh);
            maxY = Math.max(maxY, (double)off.y + hh);
            minZ = Math.min(minZ, (double)off.z - hw);
         }

         objectIterator = hitbox.getShulkers().iterator();

         for(ObjectIterator objectIterator3 = objectIterator; objectIterator3.hasNext(); maxZ = Math.max(maxZ, (double)off.z + hw)) {
            ShulkerHitbox shulker = (ShulkerHitbox)objectIterator3.next();
            off = shulker.offset(yaw);
            hw = shulker.getScale() / (double)2.0F;
            double hh = shulker.getScale() * shulker.getLength() / (double)2.0F;
            minX = Math.min(minX, (double)off.x - hw);
            maxX = Math.max(maxX, (double)off.x + hw);
            minY = Math.min(minY, (double)off.y - hh);
            maxY = Math.max(maxY, (double)off.y + hh);
            minZ = Math.min(minZ, (double)off.z - hw);
         }

         objectIterator = hitbox.getGhasts().iterator();

         for(ObjectIterator objectIterator4 = objectIterator; objectIterator4.hasNext(); maxZ = Math.max(maxZ, (double)off.z + hw)) {
            GhastHitbox ghast = (GhastHitbox)objectIterator4.next();
            off = ghast.offset(yaw);
            hw = ghast.getScale() / (double)2.0F;
            minX = Math.min(minX, (double)off.x - hw);
            maxX = Math.max(maxX, (double)off.x + hw);
            minY = Math.min(minY, (double)off.y - hw);
            maxY = Math.max(maxY, (double)off.y + hw);
            minZ = Math.min(minZ, (double)off.z - hw);
         }

         objectIterator = hitbox.getBarriers().iterator();

         BlockLocation rotated;
         for(ObjectIterator objectIterator5 = objectIterator; objectIterator5.hasNext(); maxZ = Math.max(maxZ, (double)rotated.getZ() + (double)1.0F)) {
            BarrierHitbox barrier = (BarrierHitbox)objectIterator5.next();
            rotated = barrier.groundRotate(yaw);
            minX = Math.min(minX, (double)rotated.getX());
            maxX = Math.max(maxX, (double)rotated.getX() + (double)1.0F);
            minY = Math.min(minY, (double)rotated.getY());
            maxY = Math.max(maxY, (double)rotated.getY() + (double)1.0F);
            minZ = Math.min(minZ, (double)rotated.getZ());
         }

         if (minX != Double.MAX_VALUE) {
            double hitboxWidth = Math.max(maxX - minX, maxZ - minZ);
            double hitboxHeight = maxY - minY;
            if (width <= (double)0.0F) {
               width = hitboxWidth;
            }

            if (height <= (double)0.0F) {
               height = hitboxHeight;
            }
         }
      }

      return TuplesKt.to(width + this.paddingWidth, height + this.paddingHeight);
   }

   public final boolean isOccluded(@NotNull Location eyeLoc, @NotNull Location targetLoc, double width, double height) {
      Location center = LocationUtilsKt.plus(targetLoc, (double)0.0F, height / (double)2.0F, (double)0.0F);
      double hw = width / (double)2.0F;
      double hh = height / (double)2.0F;
      Location[] locations = new Location[]{center, LocationUtilsKt.plus(center, -hw, (double)0.0F, -hw), LocationUtilsKt.plus(center, hw, (double)0.0F, -hw), LocationUtilsKt.plus(center, -hw, (double)0.0F, hw), LocationUtilsKt.plus(center, hw, (double)0.0F, hw), LocationUtilsKt.plus(center, (double)0.0F, hh, (double)0.0F), LocationUtilsKt.plus(center, (double)0.0F, -hh, (double)0.0F)};
      Location[] samplePoints = locations;
      int value = 0;

      for(int i = locations.length; value < i; ++value) {
         Location point = samplePoints[value];
         if (!this.isRayOccluded(eyeLoc, point)) {
            return false;
         }
      }

      return true;
   }

   private final boolean isRayOccluded(Location origin, Location target) {
      Vector toVector = target.clone().subtract(origin).toVector();
      Vector direction = toVector;
      double totalDistance = direction.length();
      if (totalDistance < 0.1) {
         return false;
      } else {
         direction.normalize();
         Object currentOrigin = null;
         Location location = origin.clone();
         Location location2 = location;
         double remainingDistance = (double)0.0F;
         remainingDistance = totalDistance;
         byte b = 5;

         for(int i = 0; i < b; ++i) {
            int value = 0;
            RayTraceResult rayTraceResult = location2.getWorld().rayTraceBlocks(location2, direction, remainingDistance, FluidCollisionMode.NEVER, true);
            if (rayTraceResult == null) {
               return false;
            }

            RayTraceResult result = rayTraceResult;
            Block block = result.getHitBlock();
            if (block == null) {
               return false;
            }

            Block hitBlock = block;
            double hitDistance = result.getHitPosition().distance(origin.toVector());
            if (hitDistance >= totalDistance - 0.1) {
               return false;
            }

            if (hitBlock.getType().isOccluding() && hitBlock.getType() != Material.BARRIER) {
               return true;
            }

            Location location3 = hitBlock.getLocation();
            Location blockLoc = location3;
            double exitX = direction.getX() > (double)0.0F ? (double)blockLoc.getBlockX() + (double)1.0F : (double)blockLoc.getBlockX();
            double exitY = direction.getY() > (double)0.0F ? (double)blockLoc.getBlockY() + (double)1.0F : (double)blockLoc.getBlockY();
            double exitZ = direction.getZ() > (double)0.0F ? (double)blockLoc.getBlockZ() + (double)1.0F : (double)blockLoc.getBlockZ();
            Vector hitPosition = result.getHitPosition();
            Vector hitPos = hitPosition;
            double tX = direction.getX() != (double)0.0F ? (exitX - hitPos.getX()) / direction.getX() : Double.MAX_VALUE;
            double tY = direction.getY() != (double)0.0F ? (exitY - hitPos.getY()) / direction.getY() : Double.MAX_VALUE;
            double tZ = direction.getZ() != (double)0.0F ? (exitZ - hitPos.getZ()) / direction.getZ() : Double.MAX_VALUE;
            double tMin = RangesKt.coerceAtLeast(Math.min(tX, Math.min(tY, tZ)), 0.01) + 0.01;
            Location location4 = hitPos.toLocation(location2.getWorld()).add(direction.clone().multiply(tMin));
            location2 = location4;
            remainingDistance = totalDistance - location2.toVector().distance(origin.toVector());
            if (remainingDistance <= 0.1) {
               return false;
            }
         }

         return false;
      }
   }

   private static final IFurniturePacketManager packetManager_delegate$lambda$0() {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      return ((FurnitureFactory)mechanicFactory).packetManager();
   }

   private static final Unit startTask$lambda$0(FurnitureCullingManager furnitureCullingManager) {
      (new CullingTask(furnitureCullingManager)).run();
      return Unit.INSTANCE;
   }

   private static final ConcurrentHashMap populateTrackedEntities$lambda$0$0$0(UUID it) {
      return new ConcurrentHashMap();
   }

   private static final ConcurrentHashMap populateTrackedEntities$lambda$0$0$1(Function1 tmp0, Object p0) {
      return (ConcurrentHashMap)tmp0.invoke(p0);
   }

   private static final Unit populateTrackedEntities$lambda$0(FurnitureCullingManager furnitureCullingManager, ItemDisplay entity) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)entity);
      if (furnitureMechanic == null) {
         return Unit.INSTANCE;
      } else {
         FurnitureMechanic mechanic = furnitureMechanic;
         UUID uniqueId = entity.getUniqueId();
         TrackedFurniture tracked = new TrackedFurniture(uniqueId, entity, mechanic);
         Set trackedBy = entity.getTrackedBy();
         Iterable iterable = (Iterable)trackedBy;

         for(Object element : iterable) {
            Player player = (Player)element;
            int value = 0;
            Object obj = furnitureCullingManager.trackedEntities.computeIfAbsent(player.getUniqueId(), (a0_ -> FurnitureCullingManager.populateTrackedEntities$lambda$0$0$0((UUID)a0_)));
            Map map = (Map)obj;
            map.put(entity.getUniqueId(), tracked);
         }

         return Unit.INSTANCE;
      }
   }

   private static final ConcurrentHashMap onTrack$lambda$0(UUID it) {
      return new ConcurrentHashMap();
   }

   private static final ConcurrentHashMap onTrack$lambda$1(Function1 tmp0, Object p0) {
      return (ConcurrentHashMap)tmp0.invoke(p0);
   }

   private static final Set ensureHidden$lambda$0(UUID it) {
      return (Set)ConcurrentHashMap.newKeySet();
   }

   private static final Set ensureHidden$lambda$1(Function1 tmp0, Object p0) {
      return (Set)tmp0.invoke(p0);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0007HÆ\u0003J'\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÆ\u0001J\u0014\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0017\u001a\u00020\u0018HÖ\u0081\u0004J\n\u0010\u0019\u001a\u00020\u001aHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/culling/FurnitureCullingManager$TrackedFurniture;", "", "uuid", "Ljava/util/UUID;", "entity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "<init>", "(Ljava/util/UUID;Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;)V", "getUuid", "()Ljava/util/UUID;", "getEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   public static final class TrackedFurniture {
      @NotNull
      private final UUID uuid;
      @NotNull
      private final ItemDisplay entity;
      @NotNull
      private final FurnitureMechanic mechanic;

      public TrackedFurniture(@NotNull UUID uuid, @NotNull ItemDisplay entity, @NotNull FurnitureMechanic mechanic) {
         super();
         this.uuid = uuid;
         this.entity = entity;
         this.mechanic = mechanic;
      }

      @NotNull
      public final UUID getUuid() {
         return this.uuid;
      }

      @NotNull
      public final ItemDisplay getEntity() {
         return this.entity;
      }

      @NotNull
      public final FurnitureMechanic getMechanic() {
         return this.mechanic;
      }

      @NotNull
      public final UUID component1() {
         return this.uuid;
      }

      @NotNull
      public final ItemDisplay component2() {
         return this.entity;
      }

      @NotNull
      public final FurnitureMechanic component3() {
         return this.mechanic;
      }

      @NotNull
      public final TrackedFurniture copy(@NotNull UUID uuid, @NotNull ItemDisplay entity, @NotNull FurnitureMechanic mechanic) {
         return new TrackedFurniture(uuid, entity, mechanic);
      }

      @NotNull
      public String toString() {
         return "TrackedFurniture(uuid=" + this.uuid + ", entity=" + this.entity + ", mechanic=" + this.mechanic + ")";
      }

      public int hashCode() {
         int result = this.uuid.hashCode();
         result = result * 31 + this.entity.hashCode();
         result = result * 31 + this.mechanic.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof TrackedFurniture)) {
            return false;
         } else {
            TrackedFurniture trackedFurniture = (TrackedFurniture)other;
            if (!Intrinsics.areEqual(this.uuid, trackedFurniture.uuid)) {
               return false;
            } else if (!Intrinsics.areEqual(this.entity, trackedFurniture.entity)) {
               return false;
            } else {
               return Intrinsics.areEqual(this.mechanic, trackedFurniture.mechanic);
            }
         }
      }
   }
}
