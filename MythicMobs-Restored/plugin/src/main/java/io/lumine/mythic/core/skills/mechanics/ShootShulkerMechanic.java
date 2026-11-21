package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import lombok.Getter;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@MythicMechanic(
   author = "Ashijin",
   name = "shootshulkerbullet",
   aliases = {"shootshulker"},
   description = "Shoots a shulker bullet at the target entity"
)
public class ShootShulkerMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderDouble damage;
   protected PlaceholderFloat startYOffset;
   protected PlaceholderFloat startForwardOffset;
   protected float startSideOffset;
   protected Optional<Skill> onTickSkill = Optional.empty();
   protected Optional<Skill> onHitSkill = Optional.empty();
   protected Optional<Skill> onEndSkill = Optional.empty();
   protected PlaceholderDuration tickInterval;
   protected String onTickSkillName;
   protected String onHitSkillName;
   protected String onEndSkillName;
   protected String onStartSkillName;
   @Getter
   protected Boolean bounce;
   @Getter
   protected Boolean fromOrigin;
   protected Optional<String> disguise = Optional.empty();

   public ShootShulkerMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String strProjectile = mlc.getString(new String[]{"type", "t"}, "arrow");
      this.damage = mlc.getPlaceholderDouble(new String[]{"damage", "d"}, 5.0);
      this.bounce = mlc.getBoolean(new String[]{"bounce"}, false);
      this.tickInterval = mlc.getPlaceholderDuration(new String[]{"interval", "int", "i"}, DurationUnit.TICKS, "4");
      this.startYOffset = mlc.getPlaceholderFloat(new String[]{"startyoffset", "syo"}, 0.0F);
      this.startForwardOffset = mlc.getPlaceholderFloat(new String[]{"forwardoffset", "startfoffset", "sfo"}, 0.0F);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.disguise = Optional.ofNullable(mlc.getString(new String[]{"disguise", "d"}, null));
      this.onTickSkillName = mlc.getString(new String[]{"ontickskill", "ontick", "ot", "skill", "s", "meta", "m"});
      this.onHitSkillName = mlc.getString(new String[]{"onhitskill", "onhit", "oh"});
      this.onEndSkillName = mlc.getString(new String[]{"onendskill", "onend", "oe"});
      this.getPlugin().getSkillManager().queueSecondPass(() -> {
         if (this.onTickSkillName != null) {
            this.onTickSkill = this.getPlugin().getSkillManager().getSkill(file, this, this.onTickSkillName);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "ShootShulker Loaded onTick pointing at " + this.onTickSkillName);
         if (this.onHitSkillName != null) {
            this.onHitSkill = this.getPlugin().getSkillManager().getSkill(file, this, this.onHitSkillName);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loaded onHit pointing at " + this.onHitSkillName);
         if (this.onEndSkillName != null) {
            this.onEndSkill = this.getPlugin().getSkillManager().getSkill(file, this, this.onEndSkillName);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loaded onEnd pointing at " + this.onEndSkillName);
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.shoot(data, target);
   }

   public SkillResult shoot(SkillMetadata data, AbstractEntity target) {
      SkillCaster caster = data.getCaster();
      AbstractLocation origin = data.getOrigin().clone();
      float power = data.getPower();
      if (!this.onTickSkill.isPresent() && this.onTickSkillName != null) {
         this.onTickSkill = this.getPlugin().getSkillManager().getSkill(this.file, this, this.onTickSkillName);
      }

      if (!this.onHitSkill.isPresent() && this.onHitSkillName != null) {
         this.onHitSkill = this.getPlugin().getSkillManager().getSkill(this.file, this, this.onHitSkillName);
      }

      if (!this.onEndSkill.isPresent() && this.onEndSkillName != null) {
         this.onEndSkill = this.getPlugin().getSkillManager().getSkill(this.file, this, this.onEndSkillName);
      }

      float startYOffset = this.startYOffset.get(data);
      float startForwardOffset = this.startForwardOffset.get(data);
      if (startYOffset > 0.0F) {
         origin = origin.add(0.0, startYOffset, 0.0);
      }

      if (startForwardOffset != 0.0F) {
         origin = MythicUtil.move(this.faulty, origin, startForwardOffset, 0.0, 0.0);
      }

      if (this.startSideOffset != 0.0F) {
         origin = MythicUtil.move(this.faulty, origin, 0.0, 0.0, this.startSideOffset);
      }

      AbstractEntity projectile = SkillAdapter.get().shootShulkerBullet(caster, target, origin, this);
      if (this.disguise.isPresent()) {
         this.getPlugin().getCompatibility();
         if (CompatibilityManager.LibsDisguises.enabled) {
            Entity entity = BukkitAdapter.adapt(projectile);
            this.getPlugin().getCompatibility();
            DisguiseAPI.disguiseEntity(entity, CompatibilityManager.LibsDisguises.getParsedDisguise(this.disguise.get()));
         }
      }

      new ShootShulkerMechanic.ProjectileTracker(data, projectile, power);
      return SkillResult.SUCCESS;
   }

   protected class ProjectileTracker implements Runnable, Terminable {
      private final TerminableRegistry registry = TerminableRegistry.create();
      private SkillMetadata data;
      private SkillCaster caster;
      private AbstractEntity projectile;
      private boolean hasEnded = false;
      private final int tickInterval;
      private float power;

      public ProjectileTracker(SkillMetadata caster, AbstractEntity projectile, float power) {
         this.caster = caster.getCaster();
         this.data = caster.deepClone();
         this.data.setIsAsync(true);
         this.projectile = projectile;
         this.tickInterval = Math.max(1, ShootShulkerMechanic.this.tickInterval.get(caster).getTicks());
         this.power = power;
         this.registry
            .accept(
               Events.subscribe(EntityDamageByEntityEvent.class)
                  .filter(event -> event.getDamager() instanceof ShulkerBullet)
                  .filter(event -> event.getEntity() instanceof LivingEntity)
                  .filter(event -> event.getDamager().getUniqueId().equals(projectile.getUniqueId()))
                  .handler(this::doHitSkill)
            );
         this.registry.accept(Schedulers.async().runRepeating(this, 0L, (long)this.tickInterval));
      }

      @Override
      public void run() {
         if (!this.projectile.isValid()
            || this.projectile.getBukkitEntity() instanceof Projectile && ((Projectile)this.projectile.getBukkitEntity()).isOnGround()) {
            this.doEndSkill();
            this.close();
         }

         if (ShootShulkerMechanic.this.onTickSkill.isPresent() && ShootShulkerMechanic.this.onTickSkill.get().isUsable(this.data)) {
            SkillMetadata sData = this.data.deepCloneNoTargets();
            HashSet<AbstractLocation> targets = new HashSet<>();
            targets.add(this.projectile.getLocation().clone());
            sData.setLocationTargets(targets);
            sData.setOrigin(this.projectile.getLocation().clone());
            sData.setPower(this.power);
            ShootShulkerMechanic.this.onTickSkill.get().execute(sData);
         }
      }

      public void doHitSkill(EntityDamageByEntityEvent event) {
         this.close();
         Projectile projectile = (Projectile)event.getDamager();
         AbstractEntity proj = BukkitAdapter.adapt(projectile);
         AbstractEntity target = BukkitAdapter.adapt(event.getEntity());
         HashSet<AbstractEntity> t = Sets.newHashSet();
         t.add(target);
         SkillMetadata meta = new SkillMetadataImpl(SkillTriggers.ATTACK, this.caster, target, proj.getLocation(), t, null, this.power);
         if (ShootShulkerMechanic.this.damage.get(this.data, target) == 0.0) {
            event.setCancelled(true);
         } else {
            event.setDamage(ShootShulkerMechanic.this.damage.get(this.data, target));
         }

         if (ShootShulkerMechanic.this.onHitSkill.isPresent() && ShootShulkerMechanic.this.onHitSkill.get().isUsable(meta)) {
            ShootShulkerMechanic.this.onHitSkill.get().execute(meta);
         }
      }

      private void doEndSkill() {
         if (!this.hasEnded && ShootShulkerMechanic.this.onEndSkill.isPresent()) {
            if (ShootShulkerMechanic.this.onEndSkill.get().isUsable(this.data)) {
               SkillMetadata sData = this.data.deepClone();
               HashSet<AbstractLocation> targets = new HashSet<>();
               targets.add(this.projectile.getLocation().clone());
               sData.setLocationTargets(targets);
               sData.setOrigin(this.projectile.getLocation().clone());
               sData.setPower(this.power);
               ShootShulkerMechanic.this.onEndSkill.get().execute(sData);
            }

            this.hasEnded = true;
         }
      }

      @Override
      public void close() {
         this.registry.terminate();
      }
   }
}
