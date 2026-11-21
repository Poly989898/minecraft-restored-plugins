package io.lumine.mythic.core.skills.cinematic;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.volatilecode.handlers.VolatilePacketHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicCinematicEndEvent;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillTriggers;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class CinematicState implements Runnable, Terminable, TerminableConsumer {
   private static final int BASE_INTERPOLATION = 10;
   private final TerminableRegistry terminables = TerminableRegistry.create();
   private final CinematicManager manager;
   private final UUID playerUuid;
   private final String pathId;
   private final CameraPath path;
   private final int totalFrames;
   private final int maxDuration;
   private final boolean freezePlayer;
   private final CinematicMode mode;
   private final Optional<Skill> onEndSkill;
   private final SkillMetadata startMeta;
   private Location origLoc;
   private boolean origInvulnerable;
   private float origWalkSpeed;
   private float origFlySpeed;
   private boolean origFlying;
   private boolean origAllowFlight;
   private Vector origVelocity;
   private boolean origInvisible;
   private int fakeCameraEntityId = -1;
   private boolean cameraBound;
   private int frame;
   private Task task;
   private boolean restored;
   private MythicCinematicEndEvent.Reason endReason = MythicCinematicEndEvent.Reason.FINISHED;

   public void setEndReason(MythicCinematicEndEvent.Reason reason) {
      this.endReason = reason;
   }

   public CinematicState(
      CinematicManager manager,
      Player player,
      String pathId,
      CameraPath path,
      int maxDuration,
      boolean freezePlayer,
      CinematicMode mode,
      Optional<Skill> onStartSkill,
      Optional<Skill> onEndSkill,
      SkillMetadata meta
   ) {
      this.manager = manager;
      this.playerUuid = player.getUniqueId();
      this.pathId = pathId == null ? "inline" : pathId;
      AbstractLocation anchor = resolveAnchor(meta, player);
      this.path = path.resolveRelative(anchor);
      this.totalFrames = this.path.getTotalDuration();
      this.maxDuration = Math.max(this.totalFrames, maxDuration);
      this.freezePlayer = freezePlayer;
      this.mode = mode == null ? CinematicMode.DISPLAY : mode;
      this.onEndSkill = onEndSkill;
      this.startMeta = meta;
      Location firstBukkit = toBukkit(this.path.sample(0), player.getLocation());
      this.snapshot(player);

      try {
         this.applyLock(player);
         if (this.mode == CinematicMode.DISPLAY) {
            teleport(player, firstBukkit);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 0, false, false));
            this.fakeCameraEntityId = this.packetHandler()
               .spawnFakeCamera(player, firstBukkit.getX(), firstBukkit.getY(), firstBukkit.getZ(), firstBukkit.getYaw(), firstBukkit.getPitch(), 10);
            this.cameraBound = this.fakeCameraEntityId >= 0;
            if (!this.cameraBound) {
               MythicLogger.error("cinematicCamera DISPLAY mode is not supported on this server version; falling back to TELEPORT.");
            }
         } else {
            teleport(player, firstBukkit);
         }

         this.fireTrigger(player, SkillTriggers.CINEMATIC_START);
         if (onStartSkill.isPresent() && onStartSkill.get().isUsable(meta)) {
            SkillMetadata clone = meta.deepClone();
            onStartSkill.get().execute(clone);
         }

         this.task = Schedulers.of(player).runRepeating(this, 1L, 1L);
         this.task.bindWith(this);
      } catch (Throwable e) {
         this.closeWith(player);
         throw e;
      }
   }

   public UUID getPlayerUuid() {
      return this.playerUuid;
   }

   public int getFrame() {
      return this.frame;
   }

   public int getTotalFrames() {
      return this.totalFrames;
   }

   public double getProgress() {
      return this.totalFrames <= 0 ? 0.0 : Math.min(1.0, (double)this.frame / this.totalFrames);
   }

   public Location getOriginalLocation() {
      return this.origLoc == null ? null : this.origLoc.clone();
   }

   @Override
   public void run() {
      try {
         this.tick();
      } catch (Throwable e) {
         MythicLogger.error("cinematicCamera tick failed, ending cinematic: " + e);
         this.manager.stop(this.playerUuid);
      }
   }

   private void tick() {
      Player player = Bukkit.getPlayer(this.playerUuid);
      if (player == null || !player.isOnline()) {
         this.endReason = MythicCinematicEndEvent.Reason.QUIT;
         this.manager.stop(this.playerUuid);
      } else if (player.isDead()) {
         this.endReason = MythicCinematicEndEvent.Reason.DEATH;
         this.manager.stop(this.playerUuid);
      } else if (this.frame >= this.maxDuration) {
         this.manager.stop(this.playerUuid);
      } else {
         boolean usingDisplay = this.mode == CinematicMode.DISPLAY && this.cameraBound;
         int effectiveTotal = usingDisplay ? Math.max(1, this.totalFrames - 10) : this.totalFrames;
         int sampleFrame = this.frame;
         if (this.frame >= effectiveTotal) {
            if (this.path.isLoop()) {
               sampleFrame = this.frame % effectiveTotal;
            } else {
               if (this.frame >= this.totalFrames) {
                  this.endReason = MythicCinematicEndEvent.Reason.FINISHED;
                  this.manager.stop(this.playerUuid);
                  return;
               }

               sampleFrame = effectiveTotal - 1;
            }
         }

         AbstractLocation sampled = this.path.sample(sampleFrame);
         Location target = toBukkit(sampled, this.origLoc);
         if (usingDisplay) {
            this.packetHandler()
               .moveFakeCamera(player, this.fakeCameraEntityId, target.getX(), target.getY(), target.getZ(), target.getYaw(), target.getPitch());
            this.packetHandler().setPlayerPositionSilent(player, target.getX(), target.getY(), target.getZ());
         } else {
            player.setVelocity(new Vector(0, 0, 0));
            teleport(player, target);
         }

         this.frame++;
      }
   }

   @Override
   public void close() {
      this.closeWith(Bukkit.getPlayer(this.playerUuid));
   }

   public void closeWith(Player player) {
      try {
         if (!this.restored) {
            this.restored = true;
            if (player != null && player.isOnline()) {
               if (this.mode == CinematicMode.DISPLAY && this.fakeCameraEntityId >= 0) {
                  this.packetHandler().removeFakeCamera(player, this.fakeCameraEntityId);
               }

               this.restore(player);
               Bukkit.getPluginManager().callEvent(new MythicCinematicEndEvent(player, this.path, this.getOriginalLocation(), this.frame, this.endReason));
               this.fireTrigger(player, SkillTriggers.CINEMATIC_END);
               if (this.onEndSkill.isPresent() && this.onEndSkill.get().isUsable(this.startMeta)) {
                  SkillMetadata clone = this.startMeta.deepClone();
                  this.onEndSkill.get().execute(clone);
               }
            }

            this.fakeCameraEntityId = -1;
         }
      } finally {
         this.terminables.closeAndReportException();
      }
   }

   @Override
   public <T extends AutoCloseable> T bind(T terminable) {
      this.terminables.accept((Terminable)terminable);
      return terminable;
   }

   private VolatilePacketHandler packetHandler() {
      return MythicBukkit.inst().getVolatileCodeHandler().getPacketHandler();
   }

   private static AbstractLocation resolveAnchor(SkillMetadata meta, Player player) {
      if (meta != null && meta.getOrigin() != null) {
         return meta.getOrigin();
      } else {
         return meta != null && meta.getCaster() != null && meta.getCaster().getLocation() != null
            ? meta.getCaster().getLocation()
            : BukkitAdapter.adapt(player.getLocation());
      }
   }

   private void snapshot(Player player) {
      this.origLoc = player.getLocation().clone();
      this.origInvulnerable = player.isInvulnerable();
      this.origWalkSpeed = player.getWalkSpeed();
      this.origFlySpeed = player.getFlySpeed();
      this.origFlying = player.isFlying();
      this.origAllowFlight = player.getAllowFlight();
      this.origVelocity = player.getVelocity().clone();
      this.origInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);
   }

   private void applyLock(Player player) {
      if (this.freezePlayer) {
         player.setInvulnerable(true);
         player.setWalkSpeed(0.0F);
         player.setFlySpeed(0.0F);
      }
   }

   private void restore(Player player) {
      if (this.mode == CinematicMode.DISPLAY && !this.origInvisible) {
         player.removePotionEffect(PotionEffectType.INVISIBILITY);
      }

      if (this.freezePlayer) {
         player.setInvulnerable(this.origInvulnerable);
         player.setWalkSpeed(this.origWalkSpeed);
         player.setFlySpeed(this.origFlySpeed);
      }

      if (this.origLoc != null) {
         teleport(player, this.origLoc);
      }

      player.setAllowFlight(this.origAllowFlight);
      player.setFlying(this.origAllowFlight && this.origFlying);
      if (this.origVelocity != null) {
         player.setVelocity(this.origVelocity);
      }
   }

   private void fireTrigger(Player player, SkillTrigger trigger) {
      if (trigger != null) {
         AbstractEntity entity = BukkitAdapter.adapt(player);
         SkillCaster caster = MythicBukkit.inst().getSkillManager().getCaster(entity);
         if (caster != null) {
            MythicBukkit.inst().getSkillManager().getEventBus().processTrigger(trigger, caster, entity, true);
         }
      }
   }

   private static Location toBukkit(AbstractLocation src, Location originalForWorld) {
      World world = src.getWorld() == null ? null : BukkitAdapter.adapt(src.getWorld());
      if (world == null && originalForWorld != null) {
         world = originalForWorld.getWorld();
      }

      return new Location(world, src.getX(), src.getY(), src.getZ(), src.getYaw(), src.getPitch());
   }

   private static void teleport(Player player, Location location) {
      if (ServerVersion.isFolia()) {
         player.teleportAsync(location, TeleportCause.PLUGIN);
      } else {
         player.teleport(location, TeleportCause.PLUGIN);
      }
   }
}
