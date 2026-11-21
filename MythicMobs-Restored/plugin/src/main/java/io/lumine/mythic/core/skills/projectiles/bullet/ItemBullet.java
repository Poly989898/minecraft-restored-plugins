package io.lumine.mythic.core.skills.projectiles.bullet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.volatilecode.virtual.PacketItem;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBullet extends ProjectileBullet {
   @MythicField(
      name = "audience",
      description = "The audience of the message"
   )
   protected SkillAudience audience;
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
      name = "bulletMatchDirection",
      aliases = {"bmd"},
      description = "Make the bullet face where the projectile is facing",
      defValue = "false"
   )
   protected boolean bulletMatchDirection;
   @MythicField(
      name = "bulletEnchanted",
      aliases = {"enchanted"},
      description = "Whether the item should be enchanted or not",
      defValue = "false"
   )
   protected boolean bulletEnchanted;
   private AbstractItemStack bulletItemStack;

   public ItemBullet(ProjectileBulletType type, SkillMechanic projectile, MythicLineConfig config) {
      super(type, projectile, config);
      this.audience = config.getAudience("audience", "world");
      String strBulletMaterial = config.getString(new String[]{"bulletmaterial", "material", "mat"}, "STONE");
      this.bulletModelId = config.getInteger(new String[]{"bulletmodel", "model"}, -1);
      this.bulletColor = config.getString(new String[]{"bulletcolor"}, null);
      this.bulletMatchDirection = config.getBoolean(new String[]{"bulletsmall", "bulletmatchdirection", "bmd"}, false);
      this.bulletEnchanted = config.getBoolean(new String[]{"bulletEnchanted", "enchanted"}, false);
      this.getPlugin().getSkillManager().queueAfterLoad(() -> {
         try {
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
                     this.bulletItemStack = BukkitAdapter.adapt(this.bulletMaterial).modelData(this.bulletModelId).enchantmentGlow(this.bulletEnchanted);
                  } catch (Exception var6x) {
                     var6x.printStackTrace();
                  }
               });
            } else {
               Optional<MythicItem> maybeMI = this.getPlugin().getItemManager().getItem(strBulletMaterial);
               if (maybeMI.isPresent()) {
                  this.bulletItemStack = maybeMI.get().generateItemStack(1);
               } else {
                  this.bulletMaterial = Material.valueOf(strBulletMaterial.toUpperCase());
                  this.bulletModelId = config.getInteger(new String[]{"bulletmodel", "model"}, 0);
                  this.bulletItemStack = BukkitAdapter.adapt(this.bulletMaterial).modelData(this.bulletModelId).enchantmentGlow(this.bulletEnchanted);
               }
            }

            this.bulletColor = config.getString(new String[]{"bulletcolor"}, null);
         } catch (Exception e) {
            e.printStackTrace();
            MythicLogger.errorMechanicConfig(projectile, config, "Specified bullet material does not exist");
            this.bulletMaterial = Material.STONE;
            this.bulletItemStack = BukkitAdapter.adapt(this.bulletMaterial);
         }
      });
   }

   @Override
   public ProjectileBullet.BulletTracker create(ProjectileBulletableTracker projectile, AbstractEntity target) {
      return new ItemBullet.ItemBulletTracker(projectile, target);
   }

   @Override
   public boolean isVirtual() {
      return true;
   }

   public class ItemBulletTracker extends ProjectileBullet.BulletTracker {
      private PacketItem virtualItem;

      public ItemBulletTracker(ProjectileBulletableTracker projectile, AbstractEntity target) {
         super(projectile, target);
      }

      @Override
      public void spawn(AbstractLocation start) {
         ItemStack i = BukkitAdapter.adapt(ItemBullet.this.bulletItemStack);
         AbstractVector dir = this.getProjectile().getCurrentVelocity().clone().normalize();
         AbstractLocation l = start.clone().add(dir.multiply(ItemBullet.this.bulletForwardOffset)).add(0.0, ItemBullet.this.bulletYOffset, 0.0);
         if (ItemBullet.this.bulletMatchDirection) {
            l.setDirection(dir);
         }

         this.virtualItem = new PacketItem(l);
         this.virtualItem.item.set(i);
         this.virtualItem.setHasGravity(false);
         this.virtualItem.getRenderer().spawn(SkillAudience.getEffective(ItemBullet.this.audience, this.getProjectile().getData(), this.getTarget()));
      }

      @Override
      public void tick(AbstractLocation origin) {
         AbstractVector dir = this.getProjectile().getCurrentVelocity().clone().normalize();
         AbstractLocation l = origin.clone().add(dir.multiply(ItemBullet.this.bulletForwardOffset)).add(0.0, ItemBullet.this.bulletYOffset, 0.0);
         if (ItemBullet.this.bulletMatchDirection) {
            l.setDirection(dir);
         }

         this.virtualItem.teleport(l);
      }

      @Override
      public void despawn() {
         this.virtualItem.destroy();
      }
   }
}
