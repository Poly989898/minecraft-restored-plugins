package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractSkillAdapter;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.mechanics.ShootFireballMechanic;
import io.lumine.mythic.core.skills.mechanics.ShootMechanic;
import io.lumine.mythic.core.skills.mechanics.ShootShulkerMechanic;
import io.lumine.mythic.core.utils.MythicUtil;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.SizedFireball;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class BukkitSkillAdapter implements AbstractSkillAdapter {
   public BukkitSkillAdapter(MythicBukkit plugin) {
   }

   @Override
   public boolean doDamage(DamageMetadata meta, AbstractEntity aTarget) {
      return !Events.callAndReturn(new MythicDamageEvent(meta.getDamager(), aTarget, meta)).isCancelled();
   }

   @Override
   public void strikeLightning(AbstractLocation source) {
      BukkitAdapter.adapt(source.getWorld()).strikeLightning(BukkitAdapter.adapt(source));
   }

   @Override
   public void strikeLightningEffect(AbstractLocation source) {
      BukkitAdapter.adapt(source.getWorld()).strikeLightningEffect(BukkitAdapter.adapt(source));
   }

   @Override
   public void shootFireball(
      SkillCaster am,
      ShootFireballMechanic.FireballType type,
      AbstractLocation origin,
      AbstractLocation target,
      float velocity,
      float yield,
      boolean incendiary,
      int fireTicks,
      boolean playsound,
      boolean charged,
      AbstractItemStack item
   ) {
      if (target != null) {
         Location bukkitTargetLocation = BukkitAdapter.adapt(target);
         Entity bukkitShooter = BukkitAdapter.adapt(am.getEntity());
         Location bukkitOrigin = origin == null ? bukkitShooter.getLocation().clone().add(0.0, 1.0, 0.0) : BukkitAdapter.adapt(origin);
         Fireball fireball = null;
         if (bukkitShooter instanceof Creature && ((Creature)bukkitShooter).getTarget() == target && origin != null) {
            switch (type) {
               case NORMAL:
                  fireball = ((LivingEntity)bukkitShooter).launchProjectile(Fireball.class);
                  break;
               case SMALL:
                  fireball = (Fireball)((LivingEntity)bukkitShooter).launchProjectile(SmallFireball.class);
                  break;
               case LARGE:
                  fireball = (Fireball)((LivingEntity)bukkitShooter).launchProjectile(LargeFireball.class);
                  break;
               case WITHER:
                  fireball = (Fireball)((LivingEntity)bukkitShooter).launchProjectile(WitherSkull.class);
                  break;
               case DRAGON:
                  fireball = (Fireball)((LivingEntity)bukkitShooter).launchProjectile(DragonFireball.class);
                  break;
               case ITEM:
                  fireball = (Fireball)((LivingEntity)bukkitShooter).launchProjectile(SizedFireball.class);
            }
         } else {
            Vector facing = bukkitTargetLocation.toVector().subtract(bukkitOrigin.toVector()).normalize();
            double yaw = Math.toDegrees(Math.atan2(-facing.getX(), facing.getZ()));
            double pitch = Math.toDegrees(-Math.asin(facing.getY()));
            bukkitOrigin.setYaw((float)yaw);
            bukkitOrigin.setPitch((float)pitch);
            bukkitOrigin.add(facing.multiply(2));
            switch (type) {
               case NORMAL:
                  fireball = bukkitOrigin.getWorld().spawn(bukkitOrigin, Fireball.class);
                  break;
               case SMALL:
                  fireball = (Fireball)bukkitOrigin.getWorld().spawn(bukkitOrigin, SmallFireball.class);
                  break;
               case LARGE:
                  fireball = (Fireball)bukkitOrigin.getWorld().spawn(bukkitOrigin, LargeFireball.class);
                  break;
               case WITHER:
                  fireball = (Fireball)bukkitOrigin.getWorld().spawn(bukkitOrigin, WitherSkull.class);
                  ((WitherSkull)fireball).setCharged(charged);
                  break;
               case DRAGON:
                  fireball = (Fireball)bukkitOrigin.getWorld().spawn(bukkitOrigin, DragonFireball.class);
                  break;
               case ITEM:
                  fireball = (Fireball)bukkitOrigin.getWorld().spawn(bukkitOrigin, SizedFireball.class);
                  ((SizedFireball)fireball).setDisplayItem(BukkitAdapter.adapt(item));
            }
         }

         if (playsound) {
            bukkitShooter.getWorld().playEffect(bukkitShooter.getLocation(), Effect.GHAST_SHOOT, 0);
         }

         Vector facing = bukkitTargetLocation.toVector().subtract(bukkitOrigin.toVector()).normalize().multiply(velocity);
         fireball.setVelocity(facing);
         fireball.setBounce(false);
         fireball.setIsIncendiary(incendiary);
         fireball.setFireTicks(fireTicks);
         fireball.setYield(yield);
         if (bukkitShooter instanceof LivingEntity) {
            fireball.setShooter((LivingEntity)bukkitShooter);
         }
      }
   }

   @Override
   public AbstractEntity shootProjectile(
      SkillCaster caster,
      AbstractLocation target,
      AbstractLocation origin,
      Class<? extends Projectile> projectileClass,
      AbstractVector velocity,
      ShootMechanic mechanic
   ) {
      LivingEntity l = (LivingEntity)caster.getEntity().getBukkitEntity();
      Location fTarget = BukkitAdapter.adapt(target);
      Vector vector = BukkitAdapter.adapt(velocity);
      if (Double.isNaN(vector.getX())) {
         vector.setX(0);
      }

      if (Double.isNaN(vector.getY())) {
         vector.setY(0);
      }

      if (Double.isNaN(vector.getZ())) {
         vector.setZ(0);
      }

      Projectile projectile;
      if (target == null) {
         projectile = l.launchProjectile(
            projectileClass,
            vector,
            spawnedEntity -> {
               double hLen = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
               spawnedEntity.setRotation(
                  (float)Math.toDegrees(Math.atan2(velocity.getX(), velocity.getZ())), (float)Math.toDegrees(Math.atan2(velocity.getY(), hLen))
               );
            }
         );
      } else if (mechanic.isOffsetFromCaster()) {
         Location fOrigin = BukkitAdapter.adapt(origin);
         projectile = (Projectile)fTarget.getWorld()
            .spawn(
               fOrigin,
               projectileClass,
               spawnedEntity -> {
                  double hLen = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
                  spawnedEntity.setRotation(
                     (float)Math.toDegrees(Math.atan2(velocity.getX(), velocity.getZ())), (float)Math.toDegrees(Math.atan2(velocity.getY(), hLen))
                  );
               }
            );
         projectile.setVelocity(vector);
      } else {
         projectile = l.launchProjectile(
            projectileClass,
            vector,
            spawnedEntity -> {
               double hLen = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
               spawnedEntity.setRotation(
                  (float)Math.toDegrees(Math.atan2(velocity.getX(), velocity.getZ())), (float)Math.toDegrees(Math.atan2(velocity.getY(), hLen))
               );
            }
         );
      }

      if (projectile instanceof AbstractArrow) {
         if (!mechanic.getCanPickup()) {
            ((AbstractArrow)projectile).setPickupStatus(PickupStatus.DISALLOWED);
         }

         ((AbstractArrow)projectile).setKnockbackStrength(mechanic.getKnockbackStrength());
         ((AbstractArrow)projectile).setPierceLevel(mechanic.getPierceLevel());
         if (projectile instanceof Trident) {
            String tridentItem = mechanic.getTridentItem().get(caster);
            if (!tridentItem.isEmpty()) {
               ItemStack is = BukkitAdapter.adapt(MythicBukkit.inst().getBootstrap().createItem(tridentItem));
               if (is != null) {
                  MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().setTridentItem((Trident)projectile, is);
               }
            }
         }
      } else if (projectile instanceof ThrownPotion) {
         ItemFactory potionItem;
         if (mechanic.getProjectileType() == ShootMechanic.ProjectileType.LINGERING_POTION) {
            potionItem = ItemFactory.of(Material.LINGERING_POTION);
         } else {
            potionItem = ItemFactory.of(Material.SPLASH_POTION);
         }

         PotionEffectType potionType = PotionEffectType.getByName(mechanic.getPotionEffectType().get(caster));
         int potionDuration = mechanic.getPotionDuration().get(caster).getTicks();
         int potionAmplifier = mechanic.getPotionAmplifier().get(caster);
         potionItem.potionEffect(potionType, potionDuration, potionAmplifier);
         potionItem.color(mechanic.getPotionColor());
         ((ThrownPotion)projectile).setItem(potionItem.build());
      }

      if (projectile instanceof ThrowableProjectile throwableProjectile) {
         String thrownItem = mechanic.getThrownItem().get(caster);
         if (!thrownItem.isEmpty()) {
            ItemStack is = BukkitAdapter.adapt(MythicBukkit.inst().getBootstrap().createItem(thrownItem));
            if (is != null) {
               throwableProjectile.setItem(is);
            }
         }
      }

      projectile.setBounce(mechanic.getBounce());
      projectile.setShooter(l);
      return BukkitAdapter.adapt(projectile);
   }

   @Override
   public AbstractEntity shootArcProjectile(
      SkillCaster caster, AbstractLocation target, AbstractLocation origin, Class<? extends Projectile> projectileClass, float velocity, boolean fromOrigin
   ) {
      int gravity = 20;
      LivingEntity l = (LivingEntity)caster.getEntity().getBukkitEntity();
      Location to = BukkitAdapter.adapt(target);
      Projectile projectile;
      Location from;
      if (fromOrigin) {
         Location fOrigin = BukkitAdapter.adapt(origin);
         from = fOrigin;
         projectile = (Projectile)fOrigin.getWorld().spawn(fOrigin, projectileClass);
      } else {
         from = l.getLocation();
         projectile = l.launchProjectile(projectileClass);
      }

      projectile.setBounce(false);
      projectile.setShooter(l);
      Vector test = to.clone().subtract(from).toVector();
      Double elevation = test.getY();
      Double launchAngle = MythicUtil.calculateLaunchAngle(from, to, velocity, elevation, 20.0);
      Double distance = Math.sqrt(Math.pow(test.getX(), 2.0) + Math.pow(test.getZ(), 2.0));
      if (launchAngle == null) {
         launchAngle = Math.atan((40.0 * elevation + Math.pow(velocity, 2.0)) / (40.0 * elevation + 2.0 * Math.pow(velocity, 2.0)));
      }

      Double hangtime = MythicUtil.calculateHangtime(launchAngle, velocity, elevation, 20.0);
      test.setY(Math.tan(launchAngle) * distance);
      test = MythicUtil.normalizeVector(test);
      velocity = (float)(velocity + 1.188 * Math.pow(hangtime, 2.0) + (Numbers.randomDouble() - 0.8) / 2.0);
      test = test.multiply(velocity / 20.0);
      projectile.setVelocity(test);
      return BukkitAdapter.adapt(projectile);
   }

   @Override
   public AbstractEntity rainProjectile(SkillCaster caster, AbstractLocation source, Class<? extends Projectile> projectileClass, float velocity) {
      LivingEntity l = (LivingEntity)caster.getEntity().getBukkitEntity();
      Location s = BukkitAdapter.adapt(source);
      Projectile projectile = (Projectile)l.getWorld().spawn(s, projectileClass);
      Vector v = s.clone().add(0.0, -1.0, 0.0).toVector().subtract(s.toVector()).normalize().multiply(velocity);
      projectile.setVelocity(v);
      projectile.setBounce(false);
      projectile.setShooter(l);
      return BukkitAdapter.adapt(projectile);
   }

   @Override
   public AbstractEntity shootShulkerBullet(SkillCaster caster, AbstractEntity target, AbstractLocation origin, ShootShulkerMechanic mechanic) {
      LivingEntity l = (LivingEntity)caster.getEntity().getBukkitEntity();
      Entity eTarget = BukkitAdapter.adapt(target);
      ShulkerBullet projectile;
      if (mechanic.getFromOrigin()) {
         Location fOrigin = BukkitAdapter.adapt(origin);
         projectile = eTarget.getWorld().spawn(fOrigin, ShulkerBullet.class);
      } else {
         projectile = l.launchProjectile(ShulkerBullet.class);
      }

      projectile.setTarget(eTarget);
      projectile.setBounce(mechanic.getBounce());
      projectile.setShooter(l);
      return BukkitAdapter.adapt(projectile);
   }

   @Override
   public AbstractVector throwSkill(AbstractLocation source, AbstractEntity target, float velocity, float velocityY) {
      Vector V = BukkitAdapter.adapt(target.getLocation()).toVector().subtract(BukkitAdapter.adapt(source).toVector()).normalize().multiply(velocity);
      if (velocity == 0.0F) {
         V.setY(velocityY);
      } else {
         V.setY(velocityY + V.getY());
      }

      if (V.length() > 4.0) {
         V = V.normalize().multiply(4);
      }

      if (Double.isNaN(V.getX())) {
         V.setX(0);
      }

      if (Double.isNaN(V.getY())) {
         V.setY(0);
      }

      if (Double.isNaN(V.getZ())) {
         V.setZ(0);
      }

      target.getBukkitEntity().setVelocity(V);
      return new AbstractVector(V.getX(), V.getY(), V.getZ());
   }

   @Override
   public void itemSprayEffect(
      AbstractLocation source,
      ItemStack item,
      int amount,
      int duration,
      double force,
      double yForce,
      double radius,
      double yOffset,
      boolean allowPickup,
      boolean applyGravity
   ) {
      Location l = BukkitAdapter.adapt(source);
      ItemStack is = item;
      item.setAmount(1);
      Location loc = l.clone().add(0.0, yOffset, 0.0);
      Item[] items = new Item[amount];
      if (radius <= 0.0) {
         for (int i = 0; i < amount; i++) {
            items[i] = loc.getWorld().dropItem(loc, is);
            items[i]
               .setVelocity(new Vector((Numbers.randomDouble() - 0.5) * force, (Numbers.randomDouble() - 0.5) * yForce, (Numbers.randomDouble() - 0.5) * force));
            items[i].setGravity(applyGravity);

            try {
               if (!allowPickup) {
                  items[i].setPickupDelay(32767);
                  if (ServerVersion.isPaper()) {
                     items[i].setCanMobPickup(false);
                     items[i].setCanPlayerPickup(false);
                  }
               }
            } catch (Exception e) {
            }
         }
      } else {
         for (int i = 0; i < amount; i++) {
            Location lx = loc.clone();
            lx.setX(lx.getX() - radius + Numbers.randomDouble() * (radius * 2.0));
            lx.setZ(lx.getZ() - radius + Numbers.randomDouble() * (radius * 2.0));
            items[i] = lx.getWorld().dropItem(lx, is);
            items[i]
               .setVelocity(new Vector((Numbers.randomDouble() - 0.5) * force, (Numbers.randomDouble() - 0.5) * yForce, (Numbers.randomDouble() - 0.5) * force));
            items[i].setGravity(applyGravity);

            try {
               if (!allowPickup) {
                  items[i].setPickupDelay(32767);
                  items[i].setTicksLived(5800);
                  if (ServerVersion.isPaper()) {
                     items[i].setCanMobPickup(false);
                     items[i].setCanPlayerPickup(false);
                  }
               }
            } catch (Exception ex) {
            }
         }
      }

      Schedulers.of(loc).runLater(() -> {
         for (int ix = 0; ix < items.length; ix++) {
            items[ix].remove();
         }
      }, duration);
   }

   @Override
   public void playSmokeEffect(AbstractLocation source, int direction) {
      BukkitAdapter.adapt(source).getWorld().playEffect(BukkitAdapter.adapt(source), Effect.SMOKE, direction);
   }

   @Override
   public void pushButton(SkillCaster am, AbstractLocation location) {
      Block block = BukkitAdapter.adapt(location).getWorld().getBlockAt(BukkitAdapter.adapt(location));
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Executing pushbutton mechanic @ " + location.toString());

      try {
         MythicBukkit.inst().getVolatileCodeHandler().getBlockHandler().togglePowerable(location, 20L);
      } catch (Exception e) {
         MythicLogger.error("A pushbutton skill is improperly configured: block is not a button.");
         e.printStackTrace();
      }
   }

   @Override
   public void toggleLever(SkillCaster am, AbstractLocation location, int duration) {
      Block block = BukkitAdapter.adapt(location).getWorld().getBlockAt(BukkitAdapter.adapt(location));
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Executing togglelever mechanic @ " + location.toString() + " duration=" + duration);

      try {
         MythicBukkit.inst().getVolatileCodeHandler().getBlockHandler().togglePowerable(location, duration);
      } catch (Exception e) {
         MythicLogger.error("A ToggleLever skill is improperly configured: block is not a button.");
      }
   }

   @Override
   public void executeVolley(SkillCaster am, AbstractLocation t, int amount, float velocity, float spread, int fireTicks, int removeDelay, boolean canPickup) {
      if (am.getEntity().getBukkitEntity() instanceof ProjectileSource) {
         ProjectileSource source = (ProjectileSource)am.getEntity().getBukkitEntity();
         Location target = BukkitAdapter.adapt(t);
         Location spawn = BukkitAdapter.adapt(am.getLocation()).clone();
         spawn.setY(spawn.getY() + 3.0);
         Vector v;
         if (target == null) {
            v = spawn.getDirection();
         } else {
            v = target.toVector().subtract(spawn.toVector()).normalize();
         }

         ArrayList<Arrow> arrowList = new ArrayList<>();

         for (int i = 0; i < amount; i++) {
            Arrow a = spawn.getWorld().spawnArrow(spawn, v, velocity, spread / 10.0F);
            a.setVelocity(a.getVelocity());
            if (am.getEntity() != null) {
               a.setShooter(source);
            }

            if (fireTicks > 0) {
               a.setFireTicks(fireTicks);
            }

            if (canPickup) {
               a.setPickupStatus(PickupStatus.DISALLOWED);
            }

            arrowList.add(a);
         }

         Schedulers.of(target).runLater(() -> {
            for (Arrow ax : arrowList) {
               ax.remove();
            }

            arrowList.clear();
         }, removeDelay);
      }
   }

   public void sendToastNotification(SkillCaster caster, String message) {
   }

   private static class LeverUnswitcher implements Runnable {
      private Block block;

      public LeverUnswitcher(Block block) {
         this.block = block;
      }

      @Override
      public void run() {
         try {
            if (!this.block.getChunk().isLoaded()) {
               this.block.getChunk().load();
            }

            Lever button = new Lever(Material.LEVER, this.block.getData());
            button.setPowered(false);
            BlockRedstoneEvent toggle = new BlockRedstoneEvent(this.block, 0, 1);
            Bukkit.getPluginManager().callEvent(toggle);
         } catch (Exception e) {
         }
      }
   }
}
