package io.lumine.mythic.core.skills.projectiles.bullet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand;
import io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer;
import io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.OrbitalMechanic;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.OptionalInt;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DisplayPacketBullet extends ProjectileBullet {
   @MythicField(
      name = "audience",
      description = "The audience of the effect"
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
      description = "The model of the bullet type",
      defValue = "null"
   )
   protected NamespacedKey bulletModel = null;
   @MythicField(
      name = "bulletCustomModelData",
      aliases = {"cmd"},
      description = "The custom model data of the bullet type",
      defValue = "0"
   )
   protected int bulletModelId = 0;
   @MythicField(
      name = "bulletGeneration",
      description = "A model to generate for the bullet (requires Crucible)"
   )
   protected String bulletGeneration = null;
   @MythicField(
      name = "bulletColor",
      description = "The color of the bullet type if applicable i.e. potion color, leather armor."
   )
   protected String bulletColor = null;
   @MythicField(
      name = "bulletBillboarding",
      aliases = {"bulletBillboard"},
      description = "The billboarding for the display bullet."
   )
   protected String bulletBillboarding = null;
   @MythicField(
      name = "bulletCullingDistance",
      aliases = {"bulletViewDistance", "bulletViewRange"},
      description = "The bullet's brightness"
   )
   protected int bulletCullingDistance = 50;
   @MythicField(
      name = "bulletCullingHeight",
      aliases = {"cullHeight"},
      description = "The bullet's display height"
   )
   protected float bulletCullingHeight = 0.0F;
   @MythicField(
      name = "bulletCullingWidth",
      aliases = {"cullWidth"},
      description = "The bullet's display width"
   )
   protected float bulletCullingWidth = 0.0F;
   @MythicField(
      name = "bulletBrightness",
      aliases = {"bulletBrightnessBlock"},
      description = "The bullet's brightness"
   )
   protected int bulletBrightnessBlock = -1;
   @MythicField(
      name = "bulletBrightnessSky",
      description = "The bullet's sky light brightness"
   )
   protected int bulletBrightnessSky = -1;
   protected int bulletBrightness = -1;
   @MythicField(
      name = "bulletEnchanted",
      aliases = {"enchanted"},
      description = "Whether the item should be enchanted or not",
      defValue = "false"
   )
   protected boolean bulletEnchanted;
   @MythicField(
      name = "pitch",
      description = "The pitch rotation",
      defValue = "0"
   )
   final PlaceholderDouble rotationPitch;
   @MythicField(
      name = "yaw",
      description = "The yaw rotation",
      defValue = "0"
   )
   final PlaceholderDouble rotationYaw;
   @MythicField(
      name = "roll",
      description = "The roll rotation",
      defValue = "0"
   )
   final PlaceholderDouble rotationRoll;
   @MythicField(
      name = "rotation",
      aliases = {"rot"},
      description = "The rotation of the projectile, in the x,y,z format",
      defValue = "0,0,0"
   )
   final String rotationString;
   @MythicField(
      name = "tx",
      description = "The X position translation",
      defValue = "0"
   )
   final PlaceholderDouble translationX;
   @MythicField(
      name = "ty",
      description = "The Y position translation",
      defValue = "0"
   )
   final PlaceholderDouble translationY;
   @MythicField(
      name = "tz",
      description = "The Z position translation",
      defValue = "0"
   )
   final PlaceholderDouble translationZ;
   @MythicField(
      name = "translation",
      aliases = {"pos", "offset"},
      description = "The translation of the projectile, in the x,y,z format",
      defValue = "0,0,0"
   )
   final String translation;
   @MythicField(
      name = "pitchSpeed",
      description = "The pitch rotation speed",
      defValue = "0"
   )
   final PlaceholderDouble pitchSpeed;
   @MythicField(
      name = "yaw",
      description = "The yaw rotation speed",
      defValue = "0"
   )
   final PlaceholderDouble yawSpeed;
   @MythicField(
      name = "roll",
      description = "The roll rotation speed",
      defValue = "0"
   )
   final PlaceholderDouble rollSpeed;
   @MythicField(
      name = "rotation",
      aliases = {"rot"},
      description = "The rotation speed of the projectile, in the x,y,z format",
      defValue = "0,0,0"
   )
   final String rotationStringSpeed;
   AbstractVector rotationSpeed;
   private final PlaceholderDouble[] rotationComponents;
   private final PlaceholderDouble[] translationComponents;
   private final PlaceholderDouble[] rotationSpeedComponents;
   @MythicField(
      name = "hideFirstTick",
      aliases = {"hft"},
      description = "Hides the item for the first tick",
      defValue = "0,0,0"
   )
   final boolean hideFirstTick;
   ItemStack bulletItem;
   NamespacedKey bulletItemModel;
   String bulletSkinTexture;
   @MythicField(
      name = "bulletScale",
      description = "The bullet's size scale"
   )
   protected PlaceholderString bulletScale;
   private PacketItemDisplay.PacketItemDisplayBuilder options;
   private AbstractItemStack bulletItemStack;

   public DisplayPacketBullet(ProjectileBulletType type, SkillMechanic projectile, MythicLineConfig config) {
      super(type, projectile, config);
      this.audience = config.getAudience("audience", "world");
      String strBulletMaterial = config.getString(new String[]{"bulletmaterial", "bulletitem", "material", "mat"}, "LEATHER_BOOTS");
      String bulletModel = config.getString(new String[]{"bulletmodel", "model"}, null);
      this.bulletModelId = config.getInteger(new String[]{"bulletcustommodeldata", "custommodeldata", "cmd"}, 0);
      this.bulletGeneration = config.getString(new String[]{"bulletgeneration", "bulletgen", "generation"}, null);
      if (bulletModel != null) {
         OptionalInt maybeNumber = Numbers.parseInteger(bulletModel);
         if (maybeNumber.isPresent()) {
            this.bulletModelId = maybeNumber.getAsInt();
            this.bulletModel = null;
         } else if (!bulletModel.contains(":")) {
            this.bulletModel = NamespacedKey.fromString("minecraft:" + bulletModel);
         } else {
            this.bulletModel = NamespacedKey.fromString(bulletModel);
         }
      }

      this.bulletColor = config.getString(new String[]{"bulletcolor"}, null);
      this.bulletEnchanted = config.getBoolean(new String[]{"bulletEnchanted", "enchanted"}, false);
      this.bulletCullingDistance = config.getInteger(new String[]{"bulletCullingDistance", "bulletViewDistance", "bulletViewRange"}, 50);
      this.bulletCullingHeight = config.getFloat(new String[]{"bulletCullingHeight", "cullHeight"}, 0.0F);
      this.bulletCullingWidth = config.getFloat(new String[]{"bulletCullingWidth", "cullWidth"}, 0.0F);
      this.bulletBillboarding = config.getString(new String[]{"bulletBillboarding", "bulletBillboard"}, Billboard.FIXED.toString());
      this.bulletBrightnessBlock = config.getInteger(new String[]{"bulletbrightness", "bulletbrightnessblock"}, -1);
      this.bulletBrightnessSky = config.getInteger(new String[]{"bulletbrightnesssky"}, this.bulletBrightnessBlock);
      if (this.bulletBrightnessBlock <= -1 && this.bulletBrightnessSky <= -1) {
         this.bulletBrightness = -1;
      } else {
         this.bulletBrightness = this.bulletBrightnessBlock << 4 | this.bulletBrightnessSky << 20;
      }

      this.rotationPitch = config.getPlaceholderDouble(new String[]{"pitch"}, 0.0);
      this.rotationYaw = config.getPlaceholderDouble(new String[]{"yaw"}, 0.0);
      this.rotationRoll = config.getPlaceholderDouble(new String[]{"roll"}, 0.0);
      this.rotationString = config.getString(new String[]{"rotation", "rot"}, "0,0,0");
      this.translationX = config.getPlaceholderDouble(new String[]{"tx"}, 0.0);
      this.translationY = config.getPlaceholderDouble(new String[]{"ty"}, 0.0);
      this.translationZ = config.getPlaceholderDouble(new String[]{"tz"}, 0.0);
      this.translation = config.getString(new String[]{"translation", "pos", "offset"}, "0,0,0");
      this.pitchSpeed = config.getPlaceholderDouble(new String[]{"pitchSpeed", "ps"}, 0.0);
      this.yawSpeed = config.getPlaceholderDouble(new String[]{"yawSpeed", "ys"}, 0.0);
      this.rollSpeed = config.getPlaceholderDouble(new String[]{"rollSpeed", "rs"}, 0.0);
      this.rotationStringSpeed = config.getString(new String[]{"rotationSpeed", "rotSpeed", "rots"}, "0,0,0");
      this.rotationComponents = this.precompileRotation(this.rotationString);
      this.translationComponents = this.precompileRotation(this.translation);
      this.rotationSpeedComponents = this.precompileRotation(this.rotationStringSpeed);
      this.hideFirstTick = config.getBoolean(new String[]{"hideFirstTick", "hft"}, false);
      this.bulletScale = config.getPlaceholderString(new String[]{"bulletscale", "scale"}, "0.5,0.5,0.5");
      this.bulletColor = config.getString(new String[]{"bulletcolor"}, null);
      this.bulletYOffset = config.getDouble(new String[]{"bulletyoffset", "byoffset", "byo"}, 0.2);
      this.getPlugin()
         .getSkillManager()
         .queueAfterLoad(
            () -> {
               try {
                  Optional<MythicItem> maybeItem = this.getPlugin().getItemManager().getItem(strBulletMaterial);
                  if (maybeItem.isPresent()) {
                     MythicItem mythicItem = maybeItem.get();
                     this.bulletMaterial = mythicItem.getMaterial();
                     this.bulletModel = mythicItem.getItemModel();
                     this.bulletModelId = mythicItem.getCustomModelData();
                     this.bulletItemModel = mythicItem.getItemModel();
                     this.bulletSkinTexture = mythicItem.getSkinURL();
                     this.build();
                     return;
                  }

                  Optional<AbstractModelEngineSupport> support = this.getPlugin().getCompatibility().getModelEngine();
                  if (strBulletMaterial.contains(":") && support.isPresent()) {
                     support.get()
                        .queuePostModelRegistration(
                           () -> {
                              try {
                                 String[] mat = strBulletMaterial.split(":");
                                 AbstractModelEngineSupport.ModelConfig modelConfig = support.get().getBoneModel(mat[0], mat[1]);
                                 int id = modelConfig.getId();
                                 if (id == -1) {
                                    throw new Exception();
                                 }

                                 this.bulletModelId = id;
                                 this.bulletMaterial = modelConfig.getMaterial();
                              } catch (Exception var7x) {
                                 var7x.printStackTrace();
                                 this.bulletMaterial = Material.STONE;
                              }

                              this.build();
                              if (this.bulletGeneration != null
                                 && this.getPlugin().getCompatibility().getCrucible().isPresent()
                                 && this.getProjectileMechanic().getPack() != null) {
                                 this.getPlugin()
                                    .getCompatibility()
                                    .getCrucible()
                                    .get()
                                    .addItemToGeneration(this.getProjectileMechanic().getPack(), this.bulletMaterial, this.bulletModelId, config);
                              }
                           }
                        );
                     return;
                  }

                  this.bulletItemStack = this.getPlugin().getBootstrap().createItem(strBulletMaterial);
                  this.bulletModelId = config.getInteger(new String[]{"bulletmodel", "model"}, -1);
               } catch (Exception var6x) {
                  var6x.printStackTrace();
                  MythicLogger.errorMechanicConfig(projectile, config, "Specified bullet material does not exist");
                  this.bulletMaterial = Material.STONE;
               }

               this.build();
               if (this.bulletGeneration != null
                  && this.getPlugin().getCompatibility().getCrucible().isPresent()
                  && this.getProjectileMechanic().getPack() != null) {
                  this.getPlugin()
                     .getCompatibility()
                     .getCrucible()
                     .get()
                     .addItemToGeneration(this.getProjectileMechanic().getPack(), this.bulletMaterial, this.bulletModelId, config);
               }
            }
         );
   }

   private void build() {
      if (this.bulletMaterial != null) {
         this.bulletItemStack = BukkitAdapter.adapt(this.bulletMaterial);
      }

      if (this.bulletModel != null) {
         this.bulletItemStack.itemModel(this.bulletModel);
      }

      if (this.bulletModelId > -1) {
         this.bulletItemStack.modelData(this.bulletModelId);
      }

      if (this.bulletItemModel != null) {
         this.bulletItemStack.itemModel(this.bulletItemModel);
      }

      if (this.bulletSkinTexture != null) {
         this.bulletItemStack.skullTexture(URI.create(this.bulletSkinTexture));
      }

      if (this.bulletColor != null) {
         this.bulletItemStack.color(this.bulletColor);
      }

      this.bulletItemStack.enchantmentGlow(this.bulletEnchanted);
      if (this.hideFirstTick) {
         this.bulletItem = BukkitAdapter.adapt(this.bulletItemStack);
         this.options = PacketItemDisplay.create()
            .height(this.bulletCullingHeight)
            .width(this.bulletCullingWidth)
            .billboard(this.bulletBillboarding)
            .brightness(this.bulletBrightness);
      } else {
         this.options = PacketItemDisplay.create()
            .item(BukkitAdapter.adapt(this.bulletItemStack))
            .height(this.bulletCullingHeight)
            .width(this.bulletCullingWidth)
            .billboard(this.bulletBillboarding)
            .brightness(this.bulletBrightness);
      }

      AbstractVector bulletScaleStatic = null;
      if (this.bulletScale.isStatic()) {
         try {
            bulletScaleStatic = new AbstractVector(this.bulletScale.get());
         } catch (Throwable e) {
            bulletScaleStatic = new AbstractVector(0.5, 0.5, 0.5);
            MythicLogger.errorMechanic(this.getProjectileMechanic(), "Invalid bullet scale set: " + this.bulletScale.get());
         }
      }

      if (bulletScaleStatic != null) {
         this.options.scale(bulletScaleStatic);
      }
   }

   @Override
   public ProjectileBullet.BulletTracker create(ProjectileBulletableTracker projectile, AbstractEntity target) {
      return new DisplayPacketBullet.DisplayPacketBulletTracker(projectile, target);
   }

   @Override
   public boolean isVirtual() {
      return true;
   }

   private PlaceholderDouble[] precompileRotation(String rotationString) {
      String[] split = rotationString.split(",");
      if (split.length != 3) {
         MythicLogger.error("Invalid format for " + rotationString + " in " + this.getConfig().getLine());
         return null;
      } else {
         return new PlaceholderDouble[]{PlaceholderDouble.of(split[0]), PlaceholderDouble.of(split[1]), PlaceholderDouble.of(split[2])};
      }
   }

   AbstractVector parseRotation(PlaceholderDouble[] components, PlaceholderDouble pPitch, PlaceholderDouble pYaw, PlaceholderDouble pRoll, SkillMetadata data) {
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      if (components != null) {
         try {
            x = components[0].get(data);
            y = components[1].get(data);
            z = components[2].get(data);
         } catch (NumberFormatException e) {
            MythicLogger.error("Invalid rotation value in " + this.getConfig().getLine());
         }
      }

      double pitch = pPitch.get(data);
      if (pitch != 0.0) {
         x = pitch;
      }

      double yaw = pYaw.get(data);
      if (yaw != 0.0) {
         y = yaw;
      }

      double roll = pRoll.get(data);
      if (roll != 0.0) {
         z = roll;
      }

      return new AbstractVector(x, y, z);
   }

   public class DisplayPacketBulletTracker extends ProjectileBullet.BulletTracker implements IItemBulletTracker {
      protected PacketArmorStand mount;
      protected PacketItemDisplay bullet;
      AbstractVector lastRotation = new AbstractVector(0, 0, 0);
      private boolean rotationApplied;
      private long lastDirX;
      private long lastDirY;
      private long lastDirZ;
      private float lastYaw;
      private float lastPitch;

      public DisplayPacketBulletTracker(ProjectileBulletableTracker projectile, AbstractEntity target) {
         super(projectile, target);
      }

      @Override
      public void spawn(AbstractLocation start) {
         ItemStack i = BukkitAdapter.adapt(DisplayPacketBullet.this.bulletItemStack);
         AbstractLocation l = start.clone();
         AbstractLocation al = l.clone();
         al.setYaw(0.0F);
         al.setPitch(0.0F);
         this.mount = new PacketArmorStand(al);
         this.bullet = DisplayPacketBullet.this.options.build(l);
         AbstractLocation currentLocation = this.getProjectile().getPreviousLocation();
         AbstractVector currentVelocity = this.getProjectile().getCurrentVelocity();
         AbstractVector dir = currentVelocity.lengthSquared() > 0.0 ? currentVelocity.clone().normalize() : new AbstractVector(0, 0, 1);
         double forwardOffset = DisplayPacketBullet.this.getProjectileMechanic() instanceof OrbitalMechanic
            ? 0.0
            : DisplayPacketBullet.this.bulletForwardOffset;
         AbstractLocation loc = start.clone().add(dir.clone().multiply(forwardOffset)).add(0.0, DisplayPacketBullet.this.bulletYOffset, 0.0);
         double yaw = Math.atan2(dir.getX(), dir.getZ());
         double len = Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ());
         float pitch = (float)Math.atan2(dir.getY(), len);
         float newYaw = (float)Math.toDegrees(-yaw) + 180.0F;
         float newPitch = (float)Math.toDegrees(-pitch);
         loc.setYaw(newYaw);
         loc.setPitch(newPitch);
         this.lastRotation = DisplayPacketBullet.this.parseRotation(
            DisplayPacketBullet.this.rotationComponents,
            DisplayPacketBullet.this.rotationPitch,
            DisplayPacketBullet.this.rotationYaw,
            DisplayPacketBullet.this.rotationRoll,
            this.getProjectile().getData()
         );
         this.bullet.setRotationTransformation((float)this.lastRotation.getX(), (float)this.lastRotation.getY(), (float)this.lastRotation.getZ());
         AbstractVector p = DisplayPacketBullet.this.parseRotation(
            DisplayPacketBullet.this.translationComponents,
            DisplayPacketBullet.this.translationX,
            DisplayPacketBullet.this.translationY,
            DisplayPacketBullet.this.translationZ,
            this.getProjectile().getData()
         );
         this.bullet.setTranslationTransformation((float)p.getX(), (float)p.getY(), (float)p.getZ());
         Collection<AbstractPlayer> seen = SkillAudience.getEffective(DisplayPacketBullet.this.audience, this.getProjectile().getData(), this.getTarget());
         if (!DisplayPacketBullet.this.bulletScale.isStatic()) {
            AbstractVector scaleVector = new AbstractVector(DisplayPacketBullet.this.bulletScale.get(this.getProjectile().getData()));
            this.bullet.setScale(scaleVector);
         }

         this.mount.getRenderer().setCullingDistance(DisplayPacketBullet.this.bulletCullingDistance);
         this.bullet.getRenderer().setCullingDistance(DisplayPacketBullet.this.bulletCullingDistance);
         this.bullet.getRenderer().spawnWithMount(seen, (PacketEntityRenderer)this.mount.getRenderer());
         Schedulers.async().runLater(() -> this.mount.getRenderer().addPassenger((PacketEntityRenderer)this.bullet.getRenderer()), 1L);
      }

      @Override
      public void tick(AbstractLocation origin) {
         if (this.mount != null && this.bullet != null) {
            if (DisplayPacketBullet.this.hideFirstTick && this.getProjectile().getTimesRan() == 1) {
               this.bullet.setItem(DisplayPacketBullet.this.bulletItem);
            }

            if (this.getProjectile().getTimesRan() % 5 == 0) {
               this.mount.getRenderer().updateRenderedPlayers();
               this.bullet.getRenderer().updateRenderedPlayers();
            }

            AbstractVector currentVelocity = this.getProjectile().getCurrentVelocity();
            AbstractVector dir = currentVelocity.lengthSquared() > 0.0 ? currentVelocity.clone().normalize() : new AbstractVector(0, 0, 1);
            double forwardOffset = DisplayPacketBullet.this.getProjectileMechanic() instanceof OrbitalMechanic
               ? 0.0
               : DisplayPacketBullet.this.bulletForwardOffset;
            AbstractLocation loc = origin.clone().add(dir.clone().multiply(forwardOffset)).add(0.0, DisplayPacketBullet.this.bulletYOffset, 0.0);
            long dirX = Double.doubleToRawLongBits(dir.getX());
            long dirY = Double.doubleToRawLongBits(dir.getY());
            long dirZ = Double.doubleToRawLongBits(dir.getZ());
            boolean sameDirection = this.rotationApplied && dirX == this.lastDirX && dirY == this.lastDirY && dirZ == this.lastDirZ;
            float newYaw;
            float newPitch;
            if (sameDirection) {
               newYaw = this.lastYaw;
               newPitch = this.lastPitch;
            } else {
               double yaw = Math.atan2(dir.getX(), dir.getZ());
               double len = Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ());
               float pitch = (float)Math.atan2(dir.getY(), len);
               newYaw = (float)Math.toDegrees(-yaw) + 180.0F;
               newPitch = (float)Math.toDegrees(-pitch);
            }

            loc.setYaw(newYaw);
            loc.setPitch(newPitch);
            DisplayPacketBullet.this.rotationSpeed = DisplayPacketBullet.this.parseRotation(
               DisplayPacketBullet.this.rotationSpeedComponents,
               DisplayPacketBullet.this.pitchSpeed,
               DisplayPacketBullet.this.yawSpeed,
               DisplayPacketBullet.this.rollSpeed,
               this.getProjectile().getData()
            );
            if (!sameDirection) {
               this.bullet.setRotation(newYaw, newPitch);
               this.lastDirX = dirX;
               this.lastDirY = dirY;
               this.lastDirZ = dirZ;
               this.lastYaw = newYaw;
               this.lastPitch = newPitch;
               this.rotationApplied = true;
            }

            if (DisplayPacketBullet.this.rotationSpeed.length() > 0.0) {
               this.lastRotation.add(DisplayPacketBullet.this.rotationSpeed);
               this.bullet.setRotationTransformation((float)this.lastRotation.getX(), (float)this.lastRotation.getY(), (float)this.lastRotation.getZ());
            }

            this.bullet.setPosition(loc);
            this.bullet.update();
            this.mount.teleport(loc);
         }
      }

      @Override
      public void despawn() {
         this.mount.destroy();
         this.bullet.destroy();
      }

      @Override
      public void setBulletItemModel(int model) {
         ItemStack item = BukkitAdapter.adapt(DisplayPacketBullet.this.bulletItemStack).clone();
         ItemMeta meta = item.getItemMeta();
         meta.setCustomModelData(model);
         item.setItemMeta(meta);
         this.bullet.setItem(item);
      }
   }
}
