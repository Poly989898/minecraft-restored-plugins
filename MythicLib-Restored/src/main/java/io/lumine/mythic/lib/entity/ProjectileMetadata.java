package io.lumine.mythic.lib.entity;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.damage.ProjectileAttackMetadata;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.player.skill.PassiveSkill;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectileMetadata extends TemporaryListener {
   private final int entityId;
   private final ProjectileType projectileType;
   private final List<PassiveSkill> cachedSkills;
   private final PlayerMetadata shooter;
   @Nullable
   private NBTItem sourceItem;
   private boolean customDamage;
   private double damageMultiplier = 1.0;
   public static final String METADATA_KEY = "MythicLibProjectileMetadata";
   private static final HandlerList[] HANDLER_LISTS = inferHandlerLists(ProjectileMetadata.class);

   private ProjectileMetadata(@NotNull final PlayerMetadata shooter, @NotNull final ProjectileType projectileType, @NotNull final Entity projectile) {
      super(HANDLER_LISTS);
      this.entityId = projectile.getEntityId();
      this.projectileType = projectileType;
      this.shooter = shooter;
      this.cachedSkills = shooter.getData().getPassiveSkillMap().isolateModifiers(shooter.getActionHand());
      this.registerRunnable(new BukkitRunnable() {
         final TriggerMetadata tickTriggerMetadata = new TriggerMetadata(shooter, projectileType.getTickTrigger(), projectile, null);

         public void run() {
            shooter.getData().triggerSkills(this.tickTriggerMetadata, ProjectileMetadata.this.cachedSkills);
         }
      }, runnable -> runnable.runTaskTimer(MythicLib.plugin, 0L, 1L));
      projectile.setMetadata("MythicLibProjectileMetadata", new FixedMetadataValue(MythicLib.plugin, this));
   }

   @NotNull
   public PlayerMetadata getShooter() {
      return this.shooter;
   }

   @Nullable
   public NBTItem getSourceItem() {
      return this.sourceItem;
   }

   public void setSourceItem(@Nullable NBTItem sourceItem) {
      this.sourceItem = sourceItem;
   }

   @NotNull
   public List<PassiveSkill> getEffectiveSkills() {
      return this.cachedSkills;
   }

   public boolean isCustomDamage() {
      return this.customDamage;
   }

   public void setCustomDamage(boolean customDamage) {
      this.customDamage = customDamage;
   }

   public double getDamageMultiplier() {
      return this.damageMultiplier;
   }

   public void setDamageMultiplier(double damageMultiplier) {
      Validate.isTrue(damageMultiplier >= 0.0, "Damage multiplier must be positive");
      this.damageMultiplier = damageMultiplier;
   }

   public double getDamage() {
      return this.shooter.getStat("ATTACK_DAMAGE") * this.damageMultiplier;
   }

   @EventHandler
   public void unregisterOnHit(ProjectileHitEvent event) {
      if (event.getEntity().getEntityId() == this.entityId) {
         Bukkit.getScheduler().runTask(MythicLib.plugin, () -> this.close());
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void triggerHit(PlayerAttackEvent event) {
      if (event.getAttack() instanceof ProjectileAttackMetadata && ((ProjectileAttackMetadata)event.getAttack()).getProjectile().getEntityId() == this.entityId
         )
       {
         this.shooter
            .getData()
            .triggerSkills(new TriggerMetadata(this.shooter, this.projectileType.getHitTrigger(), event.getEntity(), null), this.cachedSkills);
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void triggerLand(ProjectileHitEvent event) {
      if (event.getHitBlock() != null && event.getEntity().getEntityId() == this.entityId) {
         TriggerMetadata meta = new TriggerMetadata(this.shooter, this.projectileType.getLandTrigger(), event.getEntity(), null);
         this.shooter.getData().triggerSkills(meta, this.cachedSkills);
      }
   }

   @EventHandler
   public void unregisterOnDeath(EntityDeathEvent event) {
      if (event.getEntity().getEntityId() == this.entityId) {
         this.close();
      }
   }

   @EventHandler
   public void unregisterOnLogout(PlayerQuitEvent event) {
      if (event.getPlayer().getUniqueId().equals(this.shooter.getData().getUniqueId())) {
         this.close();
      }
   }

   @Deprecated
   public static ProjectileMetadata getCustomData(Entity proj) {
      return get(proj);
   }

   @Nullable
   public static ProjectileMetadata get(@NotNull Entity projectile) {
      for (MetadataValue mv : projectile.getMetadata("MythicLibProjectileMetadata")) {
         if (mv.getOwningPlugin().equals(MythicLib.plugin)) {
            return (ProjectileMetadata)mv.value();
         }
      }

      return null;
   }

   @NotNull
   public static ProjectileMetadata create(@NotNull PlayerMetadata shooter, @NotNull ProjectileType projectileType, @NotNull Entity projectile) {
      ProjectileMetadata found = get(projectile);
      return found != null ? found : new ProjectileMetadata(shooter, projectileType, projectile);
   }

   @NotNull
   public static ProjectileMetadata create(
      @NotNull MMOPlayerData data, @NotNull EquipmentSlot actionHand, @NotNull ProjectileType projectileType, @NotNull Entity projectile
   ) {
      ProjectileMetadata found = get(projectile);
      return found != null ? found : new ProjectileMetadata(data.getStatMap().cache(actionHand), projectileType, projectile);
   }
}
