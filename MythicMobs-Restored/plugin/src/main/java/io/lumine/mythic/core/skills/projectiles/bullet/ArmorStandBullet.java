package io.lumine.mythic.core.skills.projectiles.bullet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class ArmorStandBullet extends ProjectileBullet {
   @MythicField(
      name = "bulletMaterial",
      aliases = {"material", "mat"},
      description = "The material to use for TRACKING, BLOCK, ITEM, or MYTHICITEM bullet types",
      defValue = "STONE"
   )
   protected Material bulletMaterial = null;
   @MythicField(
      name = "bulletModel",
      aliases = {"model"},
      description = "The custom model data of the bullet type",
      defValue = "0"
   )
   protected int bulletModelId = 0;
   @MythicField(
      name = "bulletColor",
      description = "The color of the bullet type if applicable i.e. potion color, leather armor."
   )
   protected String bulletColor = null;
   @MythicField(
      name = "bulletSmall",
      description = "Whether the bullet should use the small version or not"
   )
   protected boolean bulletSmall = false;
   @MythicField(
      name = "bulletEnchanted",
      aliases = {"enchanted"},
      description = "Whether the item should be enchanted or not",
      defValue = "false"
   )
   protected boolean bulletEnchanted;
   @MythicField(
      name = "bulletKillable",
      description = "Whether the bullet can be killed to cancel the projectile"
   )
   protected boolean bulletKillable = false;
   private AbstractItemStack bulletItemStack;

   public ArmorStandBullet(ProjectileBulletType type, SkillMechanic projectile, MythicLineConfig config) {
      super(type, projectile, config);
      String strBulletMaterial = config.getString(new String[]{"bulletmaterial", "material", "mat"}, "STONE");
      this.bulletModelId = config.getInteger(new String[]{"bulletmodel", "model"}, 0);
      this.bulletColor = config.getString(new String[]{"bulletcolor"}, null);
      this.bulletEnchanted = config.getBoolean(new String[]{"bulletEnchanted", "enchanted"}, false);
      this.getPlugin().getSkillManager().queueAfterLoad(() -> {
         try {
            Optional<MythicItem> maybeItem = this.getPlugin().getItemManager().getItem(strBulletMaterial);
            if (maybeItem.isPresent()) {
               MythicItem mythicItem = maybeItem.get();
               this.bulletMaterial = mythicItem.getMaterial();
               this.bulletModelId = mythicItem.getCustomModelData();
               return;
            }

            Optional<AbstractModelEngineSupport> support = this.getPlugin().getCompatibility().getModelEngine();
            if (strBulletMaterial.contains(":") && support.isPresent()) {
               support.get().queuePostModelRegistration(() -> {
                  try {
                     String[] mat = strBulletMaterial.split(":");
                     AbstractModelEngineSupport.ModelConfig modelConfig = support.get().getBoneModel(mat[0], mat[1]);
                     int id = modelConfig.getId();
                     if (id == -1) {
                        throw new Exception();
                     }

                     this.bulletModelId = id;
                     this.bulletMaterial = modelConfig.getMaterial();
                  } catch (Exception var6x) {
                     var6x.printStackTrace();
                  }
               });
            } else {
               this.bulletMaterial = Material.valueOf(strBulletMaterial.toUpperCase());
               this.bulletModelId = config.getInteger(new String[]{"bulletmodel", "model"}, 0);
            }

            this.bulletColor = config.getString(new String[]{"bulletcolor"}, null);
         } catch (Exception e) {
            e.printStackTrace();
            MythicLogger.errorMechanicConfig(projectile, config, "Specified bullet material does not exist");
            this.bulletMaterial = Material.STONE;
         }

         this.bulletItemStack = BukkitAdapter.adapt(this.bulletMaterial).modelData(this.bulletModelId).enchantmentGlow(this.bulletEnchanted);
         if (this.bulletColor != null) {
            this.bulletItemStack.color(this.bulletColor);
         }
      });
   }

   @Override
   public ProjectileBullet.BulletTracker create(ProjectileBulletableTracker projectile, AbstractEntity target) {
      return new ArmorStandBullet.ArmorStandBulletTracker(projectile, target);
   }

   @Override
   public boolean isVirtual() {
      return false;
   }

   public class ArmorStandBulletTracker extends ProjectileBullet.BulletTracker {
      protected AbstractEntity bullet;

      public ArmorStandBulletTracker(ProjectileBulletableTracker projectile, AbstractEntity target) {
         super(projectile, target);
         Events.subscribe(EntityDamageByEntityEvent.class)
            .filter(event -> event.getEntity() != null)
            .filter(event -> event.getEntity().getUniqueId().equals(this.bullet.getUniqueId()))
            .handler(event -> projectile.terminate())
            .bindWith(projectile);
      }

      @Override
      public void spawn(AbstractLocation start) {
         AbstractLocation l = this.getProjectile().getCurrentLocation().clone().subtract(0.0, 1.4375, 0.0);
         Location location = BukkitAdapter.adapt(l);
         ArmorStand armorStand;
         if (ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_13)) {
            armorStand = (ArmorStand)location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND, SpawnReason.CUSTOM, entity -> {
               this.bullet = BukkitAdapter.adapt(entity);
               ArmorStand as = (ArmorStand)entity;
               as.setInvisible(true);
               as.setAI(true);
               as.setTicksLived(Integer.MAX_VALUE);
               as.setMarker(true);
               as.setGravity(false);
               as.setRemoveWhenFarAway(true);
               if (!ArmorStandBullet.this.bulletKillable) {
                  as.setInvulnerable(true);
               }
            });
         } else {
            armorStand = (ArmorStand)ArmorStandBullet.this.getPlugin().getVolatileCodeHandler().getWorldHandler().spawnInvisibleArmorStand(location);
            this.bullet = BukkitAdapter.adapt(armorStand);
            armorStand.setInvisible(true);
            armorStand.setAI(true);
            armorStand.setTicksLived(Integer.MAX_VALUE);
            armorStand.setGravity(false);
            armorStand.setRemoveWhenFarAway(true);
            if (!ArmorStandBullet.this.bulletKillable) {
               armorStand.setInvulnerable(true);
            }
         }

         ItemFactory bulletItem = ItemFactory.of(ArmorStandBullet.this.bulletMaterial).model(ArmorStandBullet.this.bulletModelId);
         if (ArmorStandBullet.this.bulletColor != null) {
            bulletItem.color(ArmorStandBullet.this.bulletColor);
         }

         armorStand.getEquipment().setHelmet(bulletItem.build());
         Projectile.BULLET_ENTITIES.add(this.bullet);
         ArmorStandBullet.this.getPlugin().getVolatileCodeHandler().getEntityHandler().setHitBox(this.bullet, 0.0, 0.0);
         if (this.getProjectile().hasTerminated()) {
            armorStand.remove();
         }
      }

      @Override
      public void tick(AbstractLocation origin) {
         if (this.bullet != null) {
            if (this.bullet.getBukkitEntity() instanceof ArmorStand) {
               ArmorStand stand = (ArmorStand)this.bullet.getBukkitEntity();
               double forwardOffset;
               if (ArmorStandBullet.this.getProjectileMechanic() instanceof Projectile projectileMechanic) {
                  forwardOffset = projectileMechanic.getTickInterpolation() == 0
                     ? ArmorStandBullet.this.bulletForwardOffset
                     : ArmorStandBullet.this.bulletForwardOffset * projectileMechanic.getTickInterpolation();
               } else {
                  forwardOffset = ArmorStandBullet.this.bulletForwardOffset;
               }

               AbstractLocation currentLocation = this.getProjectile().getCurrentLocation().clone();
               AbstractVector currentVelocity = this.getProjectile().getCurrentVelocity().clone().multiply(forwardOffset);
               Location to = BukkitAdapter.adapt(currentLocation).clone();
               Vector delta = BukkitAdapter.adapt(currentVelocity).normalize().multiply(this.getProjectile().getVelocityMagnitude() / 7.6);
               double yaw = Math.atan2(delta.getX(), delta.getZ());
               double len = delta.getZ() / Math.cos(yaw);
               double pitch = Math.atan2(delta.getY(), len);
               stand.setHeadPose(new EulerAngle(-pitch, 0.0, 0.0));
               Location loc = to.clone().add(delta).subtract(0.0, 1.4375, 0.0);
               loc.setYaw((float)Math.toDegrees(-yaw));
               if (ServerVersion.isFolia()) {
                  stand.teleportAsync(loc);
               } else {
                  stand.teleport(loc);
               }
            }
         }
      }

      @Override
      public void despawn() {
         this.bullet.remove();
         Projectile.BULLET_ENTITIES.remove(this.bullet);
      }
   }
}
