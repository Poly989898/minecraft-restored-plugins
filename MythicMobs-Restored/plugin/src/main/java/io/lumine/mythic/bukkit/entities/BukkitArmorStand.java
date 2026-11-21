package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.numbers.RandomDouble;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class BukkitArmorStand extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.ARMOR_STAND).build();
   private boolean isMarker = false;
   private boolean hasArms = true;
   private boolean hasGravity = true;
   private boolean invisible = false;
   private boolean small = false;
   private boolean hasBasePlate = true;
   private boolean canTick = true;
   private boolean canMove = true;
   private boolean interactable = true;
   private String item_head = null;
   private String item_body = null;
   private String item_legs = null;
   private String item_feet = null;
   private String item_hand = null;
   private String item_offhand = null;
   private Optional<RandomDouble> headPoseP = Optional.empty();
   private Optional<RandomDouble> headPoseY = Optional.empty();
   private Optional<RandomDouble> headPoseA = Optional.empty();
   private Optional<RandomDouble> bodyPoseP = Optional.empty();
   private Optional<RandomDouble> bodyPoseY = Optional.empty();
   private Optional<RandomDouble> bodyPoseA = Optional.empty();
   private Optional<RandomDouble> lArmPoseP = Optional.empty();
   private Optional<RandomDouble> lArmPoseY = Optional.empty();
   private Optional<RandomDouble> lArmPoseA = Optional.empty();
   private Optional<RandomDouble> rArmPoseP = Optional.empty();
   private Optional<RandomDouble> rArmPoseY = Optional.empty();
   private Optional<RandomDouble> rArmPoseA = Optional.empty();
   private Optional<RandomDouble> lLegPoseP = Optional.empty();
   private Optional<RandomDouble> lLegPoseY = Optional.empty();
   private Optional<RandomDouble> lLegPoseA = Optional.empty();
   private Optional<RandomDouble> rLegPoseP = Optional.empty();
   private Optional<RandomDouble> rLegPoseY = Optional.empty();
   private Optional<RandomDouble> rLegPoseA = Optional.empty();

   @Override
   public EntityType getBukkitType() {
      return EntityType.ARMOR_STAND;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.hasArms = mc.getBoolean("Options.HasArms", false);
      this.hasGravity = mc.getBoolean("Options.HasGravity", true);
      this.small = mc.getBoolean("Options.Small", false);
      this.hasBasePlate = mc.getBoolean("Options.HasBasePlate", true);
      this.invisible = mc.getBoolean("Options.Invisible", false);
      this.isMarker = mc.getBoolean("Options.Marker", false);
      this.canTick = mc.getBoolean("Options.CanTick", true);
      this.canMove = mc.getBoolean("Options.CanMove", true);
      this.item_head = mc.getString("Options.ItemHead", null);
      this.item_body = mc.getString("Options.ItemBody", null);
      this.item_legs = mc.getString("Options.ItemLegs", null);
      this.item_feet = mc.getString("Options.ItemFeet", null);
      this.item_hand = mc.getString("Options.ItemHand", null);
      this.item_offhand = mc.getString("Options.ItemOffhand", null);
      this.interactable = mc.getBoolean("Options.Interactable", true);
      String headPose = mc.getString("Pose.Head", null);
      if (headPose != null) {
         try {
            String[] split = headPose.split(",");
            this.headPoseP = Optional.of(new RandomDouble(split[0]));
            this.headPoseY = Optional.of(new RandomDouble(split[1]));
            this.headPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception e) {
            MythicLogger.errorEntityConfig(this, mc, "Invalid Pose, must be in format pitch,yaw,angle");
            MythicLogger.handleMinorError(e);
         }
      }

      String bodyPose = mc.getString("Pose.Body", null);
      if (bodyPose != null) {
         try {
            String[] split = bodyPose.split(",");
            this.bodyPoseP = Optional.of(new RandomDouble(split[0]));
            this.bodyPoseY = Optional.of(new RandomDouble(split[1]));
            this.bodyPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex) {
            MythicLogger.errorEntityConfig(this, mc, "Invalid Pose, must be in format pitch,yaw,angle");
            MythicLogger.handleMinorError(ex);
         }
      }

      String lArmPose = mc.getString("Pose.LeftArm", null);
      if (lArmPose != null) {
         try {
            String[] split = lArmPose.split(",");
            this.lArmPoseP = Optional.of(new RandomDouble(split[0]));
            this.lArmPoseY = Optional.of(new RandomDouble(split[1]));
            this.lArmPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex2) {
            MythicLogger.errorEntityConfig(this, mc, "Invalid Pose, must be in format pitch,yaw,angle");
            MythicLogger.handleMinorError(ex2);
         }
      }

      String rArmPose = mc.getString("Pose.RightArm", null);
      if (rArmPose != null) {
         try {
            String[] split = rArmPose.split(",");
            this.rArmPoseP = Optional.of(new RandomDouble(split[0]));
            this.rArmPoseY = Optional.of(new RandomDouble(split[1]));
            this.rArmPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex3) {
            MythicLogger.errorEntityConfig(this, mc, "Invalid Pose, must be in format pitch,yaw,angle");
            MythicLogger.handleMinorError(ex3);
         }
      }

      String lLegPose = mc.getString("Pose.LeftLeg", null);
      if (lLegPose != null) {
         try {
            String[] split = lLegPose.split(",");
            this.lLegPoseP = Optional.of(new RandomDouble(split[0]));
            this.lLegPoseY = Optional.of(new RandomDouble(split[1]));
            this.lLegPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex4) {
            MythicLogger.errorEntityConfig(this, mc, "Invalid Pose, must be in format pitch,yaw,angle");
            MythicLogger.handleMinorError(ex4);
         }
      }

      String rLegPose = mc.getString("Pose.RightLeg", null);
      if (rLegPose != null) {
         try {
            String[] split = rLegPose.split(",");
            this.rLegPoseP = Optional.of(new RandomDouble(split[0]));
            this.rLegPoseY = Optional.of(new RandomDouble(split[1]));
            this.rLegPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex5) {
            MythicLogger.errorEntityConfig(this, mc, "Invalid Pose, must be in format pitch,yaw,angle");
            MythicLogger.handleMinorError(ex5);
         }
      }
   }

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      if (this.invisible) {
         return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_13)
            ? this.spawnEntity(location, EntityType.ARMOR_STAND, reason, entity -> {
               if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(location)) {
                  Schedulers.of(location).run(() -> {
                     ((ArmorStand)entity).setInvisible(true);
                     if (prespawnFunc != null) {
                        prespawnFunc.accept(entity);
                     }
                  });
               } else {
                  ((ArmorStand)entity).setInvisible(true);
                  if (prespawnFunc != null) {
                     prespawnFunc.accept(entity);
                  }
               }
            })
            : MythicBukkit.inst().getVolatileCodeHandler().getWorldHandler().spawnInvisibleArmorStand(location);
      } else {
         return this.spawnEntity(location, EntityType.ARMOR_STAND, reason, prespawnFunc);
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      ArmorStand e = (ArmorStand)entity;
      e.setArms(this.hasArms);
      e.setGravity(this.hasGravity);
      e.setVisible(!this.invisible);
      e.setInvisible(this.invisible);
      e.setSmall(this.small);
      e.setMarker(this.isMarker);
      e.setBasePlate(this.hasBasePlate);
      if (ServerVersion.isPaper()) {
         e.setCanTick(this.canTick);
         e.setCanMove(this.canMove);
         if (!this.interactable) {
            e.setDisabledSlots(EquipmentSlot.values());
         }
      }

      if (this.item_head != null) {
         Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(this.item_head);
         if (maybeItem.isPresent()) {
            MythicItem mi = maybeItem.get();
            e.setHelmet(BukkitAdapter.adapt(mi.generateItemStack(1)));
         }
      }

      if (this.item_body != null) {
         Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(this.item_body);
         if (maybeItem.isPresent()) {
            MythicItem mi = maybeItem.get();
            e.setChestplate(BukkitAdapter.adapt(mi.generateItemStack(1)));
         }
      }

      if (this.item_legs != null) {
         Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(this.item_legs);
         if (maybeItem.isPresent()) {
            MythicItem mi = maybeItem.get();
            e.setLeggings(BukkitAdapter.adapt(mi.generateItemStack(1)));
         }
      }

      if (this.item_feet != null) {
         Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(this.item_feet);
         if (maybeItem.isPresent()) {
            MythicItem mi = maybeItem.get();
            e.setBoots(BukkitAdapter.adapt(mi.generateItemStack(1)));
         }
      }

      if (this.item_hand != null) {
         Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(this.item_hand);
         if (maybeItem.isPresent()) {
            MythicItem mi = maybeItem.get();
            e.setItemInHand(BukkitAdapter.adapt(mi.generateItemStack(1)));
         }
      }

      if (this.item_offhand != null) {
         Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(this.item_offhand);
         if (maybeItem.isPresent()) {
            MythicItem mi = maybeItem.get();
            e.getEquipment().setItemInOffHand(BukkitAdapter.adapt(mi.generateItemStack(1)));
         }
      }

      if (this.headPoseP.isPresent()) {
         double P = this.headPoseP.get().get() * Math.PI / 180.0;
         double Y = this.headPoseY.get().get() * Math.PI / 180.0;
         double A = this.headPoseA.get().get() * Math.PI / 180.0;
         EulerAngle a = new EulerAngle(P, Y, A);
         e.setHeadPose(a);
      }

      if (this.bodyPoseP.isPresent()) {
         double P = this.bodyPoseP.get().get() * Math.PI / 180.0;
         double Y = this.bodyPoseY.get().get() * Math.PI / 180.0;
         double A = this.bodyPoseA.get().get() * Math.PI / 180.0;
         EulerAngle a = new EulerAngle(P, Y, A);
         e.setBodyPose(a);
      }

      if (this.lArmPoseP.isPresent()) {
         double P = this.lArmPoseP.get().get() * Math.PI / 180.0;
         double Y = this.lArmPoseY.get().get() * Math.PI / 180.0;
         double A = this.lArmPoseA.get().get() * Math.PI / 180.0;
         EulerAngle a = new EulerAngle(P, Y, A);
         e.setLeftArmPose(a);
      }

      if (this.rArmPoseP.isPresent()) {
         double P = this.rArmPoseP.get().get() * Math.PI / 180.0;
         double Y = this.rArmPoseY.get().get() * Math.PI / 180.0;
         double A = this.rArmPoseA.get().get() * Math.PI / 180.0;
         EulerAngle a = new EulerAngle(P, Y, A);
         e.setRightArmPose(a);
      }

      if (this.lLegPoseP.isPresent()) {
         double P = this.lLegPoseP.get().get() * Math.PI / 180.0;
         double Y = this.lLegPoseY.get().get() * Math.PI / 180.0;
         double A = this.lLegPoseA.get().get() * Math.PI / 180.0;
         EulerAngle a = new EulerAngle(P, Y, A);
         e.setLeftLegPose(a);
      }

      if (this.rLegPoseP.isPresent()) {
         double P = this.rLegPoseP.get().get() * Math.PI / 180.0;
         double Y = this.rLegPoseY.get().get() * Math.PI / 180.0;
         double A = this.rLegPoseA.get().get() * Math.PI / 180.0;
         EulerAngle a = new EulerAngle(P, Y, A);
         e.setRightLegPose(a);
      }

      return e;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
