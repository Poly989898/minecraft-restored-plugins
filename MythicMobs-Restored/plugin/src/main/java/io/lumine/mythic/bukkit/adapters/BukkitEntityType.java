package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.MythicEntity;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.entities.BukkitAllay;
import io.lumine.mythic.bukkit.entities.BukkitArmadillo;
import io.lumine.mythic.bukkit.entities.BukkitArmorStand;
import io.lumine.mythic.bukkit.entities.BukkitAxolotl;
import io.lumine.mythic.bukkit.entities.BukkitBabyDrowned;
import io.lumine.mythic.bukkit.entities.BukkitBabyHusk;
import io.lumine.mythic.bukkit.entities.BukkitBabyPigZombie;
import io.lumine.mythic.bukkit.entities.BukkitBabyPiglin;
import io.lumine.mythic.bukkit.entities.BukkitBabyPiglinBrute;
import io.lumine.mythic.bukkit.entities.BukkitBabyZoglin;
import io.lumine.mythic.bukkit.entities.BukkitBabyZombie;
import io.lumine.mythic.bukkit.entities.BukkitBabyZombieVillager;
import io.lumine.mythic.bukkit.entities.BukkitBat;
import io.lumine.mythic.bukkit.entities.BukkitBee;
import io.lumine.mythic.bukkit.entities.BukkitBlaze;
import io.lumine.mythic.bukkit.entities.BukkitBlockDisplay;
import io.lumine.mythic.bukkit.entities.BukkitBoat;
import io.lumine.mythic.bukkit.entities.BukkitBoatChest;
import io.lumine.mythic.bukkit.entities.BukkitBogged;
import io.lumine.mythic.bukkit.entities.BukkitBreeze;
import io.lumine.mythic.bukkit.entities.BukkitCamel;
import io.lumine.mythic.bukkit.entities.BukkitCamelHusk;
import io.lumine.mythic.bukkit.entities.BukkitCat;
import io.lumine.mythic.bukkit.entities.BukkitCaveSpider;
import io.lumine.mythic.bukkit.entities.BukkitChicken;
import io.lumine.mythic.bukkit.entities.BukkitCod;
import io.lumine.mythic.bukkit.entities.BukkitCopperGolem;
import io.lumine.mythic.bukkit.entities.BukkitCow;
import io.lumine.mythic.bukkit.entities.BukkitCreaking;
import io.lumine.mythic.bukkit.entities.BukkitCreeper;
import io.lumine.mythic.bukkit.entities.BukkitCustom;
import io.lumine.mythic.bukkit.entities.BukkitDolphin;
import io.lumine.mythic.bukkit.entities.BukkitDonkey;
import io.lumine.mythic.bukkit.entities.BukkitDrowned;
import io.lumine.mythic.bukkit.entities.BukkitElderGuardian;
import io.lumine.mythic.bukkit.entities.BukkitEnderDragon;
import io.lumine.mythic.bukkit.entities.BukkitEnderman;
import io.lumine.mythic.bukkit.entities.BukkitEndermite;
import io.lumine.mythic.bukkit.entities.BukkitEvoker;
import io.lumine.mythic.bukkit.entities.BukkitExperienceBottle;
import io.lumine.mythic.bukkit.entities.BukkitExperienceOrb;
import io.lumine.mythic.bukkit.entities.BukkitFallingBlock;
import io.lumine.mythic.bukkit.entities.BukkitFox;
import io.lumine.mythic.bukkit.entities.BukkitFrog;
import io.lumine.mythic.bukkit.entities.BukkitGhast;
import io.lumine.mythic.bukkit.entities.BukkitGiant;
import io.lumine.mythic.bukkit.entities.BukkitGlowSquid;
import io.lumine.mythic.bukkit.entities.BukkitGoat;
import io.lumine.mythic.bukkit.entities.BukkitGuardian;
import io.lumine.mythic.bukkit.entities.BukkitHappyGhast;
import io.lumine.mythic.bukkit.entities.BukkitHoglin;
import io.lumine.mythic.bukkit.entities.BukkitHorse;
import io.lumine.mythic.bukkit.entities.BukkitHusk;
import io.lumine.mythic.bukkit.entities.BukkitIllusioner;
import io.lumine.mythic.bukkit.entities.BukkitInteraction;
import io.lumine.mythic.bukkit.entities.BukkitIronGolem;
import io.lumine.mythic.bukkit.entities.BukkitItem;
import io.lumine.mythic.bukkit.entities.BukkitItemDisplay;
import io.lumine.mythic.bukkit.entities.BukkitLlama;
import io.lumine.mythic.bukkit.entities.BukkitMagmaCube;
import io.lumine.mythic.bukkit.entities.BukkitMannequin;
import io.lumine.mythic.bukkit.entities.BukkitMarker;
import io.lumine.mythic.bukkit.entities.BukkitMinecart;
import io.lumine.mythic.bukkit.entities.BukkitMinecartChest;
import io.lumine.mythic.bukkit.entities.BukkitMule;
import io.lumine.mythic.bukkit.entities.BukkitMushroomCow;
import io.lumine.mythic.bukkit.entities.BukkitNautilus;
import io.lumine.mythic.bukkit.entities.BukkitOcelot;
import io.lumine.mythic.bukkit.entities.BukkitPanda;
import io.lumine.mythic.bukkit.entities.BukkitParched;
import io.lumine.mythic.bukkit.entities.BukkitParrot;
import io.lumine.mythic.bukkit.entities.BukkitPhantom;
import io.lumine.mythic.bukkit.entities.BukkitPig;
import io.lumine.mythic.bukkit.entities.BukkitPigZombie;
import io.lumine.mythic.bukkit.entities.BukkitPiglin;
import io.lumine.mythic.bukkit.entities.BukkitPiglinBrute;
import io.lumine.mythic.bukkit.entities.BukkitPillager;
import io.lumine.mythic.bukkit.entities.BukkitPolarBear;
import io.lumine.mythic.bukkit.entities.BukkitPufferFish;
import io.lumine.mythic.bukkit.entities.BukkitRabbit;
import io.lumine.mythic.bukkit.entities.BukkitRavager;
import io.lumine.mythic.bukkit.entities.BukkitSalmon;
import io.lumine.mythic.bukkit.entities.BukkitSheep;
import io.lumine.mythic.bukkit.entities.BukkitShulker;
import io.lumine.mythic.bukkit.entities.BukkitSilverfish;
import io.lumine.mythic.bukkit.entities.BukkitSkeleton;
import io.lumine.mythic.bukkit.entities.BukkitSkeletonHorse;
import io.lumine.mythic.bukkit.entities.BukkitSlime;
import io.lumine.mythic.bukkit.entities.BukkitSniffer;
import io.lumine.mythic.bukkit.entities.BukkitSnowman;
import io.lumine.mythic.bukkit.entities.BukkitSpider;
import io.lumine.mythic.bukkit.entities.BukkitSquid;
import io.lumine.mythic.bukkit.entities.BukkitStray;
import io.lumine.mythic.bukkit.entities.BukkitStrider;
import io.lumine.mythic.bukkit.entities.BukkitSulfurCube;
import io.lumine.mythic.bukkit.entities.BukkitTNT;
import io.lumine.mythic.bukkit.entities.BukkitTadpole;
import io.lumine.mythic.bukkit.entities.BukkitTextDisplay;
import io.lumine.mythic.bukkit.entities.BukkitTraderLlama;
import io.lumine.mythic.bukkit.entities.BukkitTropicalFish;
import io.lumine.mythic.bukkit.entities.BukkitTurtle;
import io.lumine.mythic.bukkit.entities.BukkitVex;
import io.lumine.mythic.bukkit.entities.BukkitVillager;
import io.lumine.mythic.bukkit.entities.BukkitVindicator;
import io.lumine.mythic.bukkit.entities.BukkitWanderingTrader;
import io.lumine.mythic.bukkit.entities.BukkitWarden;
import io.lumine.mythic.bukkit.entities.BukkitWitch;
import io.lumine.mythic.bukkit.entities.BukkitWither;
import io.lumine.mythic.bukkit.entities.BukkitWitherSkeleton;
import io.lumine.mythic.bukkit.entities.BukkitWolf;
import io.lumine.mythic.bukkit.entities.BukkitZoglin;
import io.lumine.mythic.bukkit.entities.BukkitZombie;
import io.lumine.mythic.bukkit.entities.BukkitZombieHorse;
import io.lumine.mythic.bukkit.entities.BukkitZombieNautilus;
import io.lumine.mythic.bukkit.entities.BukkitZombieVillager;
import io.lumine.mythic.bukkit.entities.properties.EntityPropertySet;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public abstract class BukkitEntityType extends MythicEntity {
   private static final HashMap<MythicEntityType, Class<? extends BukkitEntityType>> ENTITIES = new HashMap<>();
   @Getter
   private static Boolean isSpawning = false;

   /** Paper overload World#spawn(Location, Class, SpawnReason, boolean, Consumer); absent from the newest API, looked up once. */
   private static final class PaperSpawn {
      static final java.lang.reflect.Method METHOD = find();

      private static java.lang.reflect.Method find() {
         try {
            return org.bukkit.World.class.getMethod("spawn", Location.class, Class.class, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.class, boolean.class, Consumer.class);
         } catch (ReflectiveOperationException ex) {
            return null;
         }
      }
   }
   private List<EntityPropertySet> propertySets = new ArrayList<>();
   private final ItemStack defaultHead = ItemFactory.of(Material.PIG_SPAWN_EGG).build();

   protected MythicBukkit getPlugin() {
      return MythicBukkit.inst();
   }

   @Override
   public AbstractEntity spawn(AbstractLocation location, SpawnReason reason) {
      return this.spawn(location, reason, null);
   }

   @Override
   public AbstractEntity spawn(AbstractLocation location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      Entity entity = this.spawn(BukkitAdapter.adapt(location), reason, prespawnFunc);
      entity = this.applySpawnOptions(entity);
      return BukkitAdapter.adapt(entity);
   }

   @Override
   public AbstractEntity spawnBaseEntity(AbstractLocation location, SpawnReason reason) {
      Entity entity = this.spawn(BukkitAdapter.adapt(location), reason, null);
      return BukkitAdapter.adapt(entity);
   }

   public Entity spawn(Location location, SpawnReason reason) {
      return this.spawn(location, reason, null);
   }

   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      return this.spawnEntity(location, this.getBukkitType(), reason, prespawnFunc);
   }

   protected Entity spawnEntity(Location location, EntityType type, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      isSpawning = true;

      LivingEntity livingEntity;
      try {
         if (!ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_2)) {
            Entity entity = location.getWorld().spawnEntity(location, type, reason.getBukkitReason());
            if (prespawnFunc != null) {
               prespawnFunc.accept(entity);
            }

            return entity;
         }

         boolean randomizeProperties = reason.isRandomizeSpawnProperties();
         Class<?> entityClass = type.getEntityClass();
         if (!LivingEntity.class.isAssignableFrom(entityClass)) {
            return location.getWorld().spawnEntity(location, type, reason.getBukkitReason(), entityx -> {
               if (prespawnFunc != null) {
                  prespawnFunc.accept(entityx);
               }
            });
         }

         Consumer<Entity> spawnFunc = entityx -> {
            if (prespawnFunc != null) {
               prespawnFunc.accept(entityx);
            }
         };
         Entity spawned = null;
         java.lang.reflect.Method m = PaperSpawn.METHOD;
         if (m != null) {
            try {
               spawned = (Entity)m.invoke(location.getWorld(), location, entityClass, reason.getBukkitReason(), randomizeProperties, spawnFunc);
            } catch (ReflectiveOperationException ex) {
               spawned = null;
            }
         }
         if (spawned == null) {
            spawned = location.getWorld().spawn(location, (Class<Entity>)entityClass, spawnFunc, reason.getBukkitReason());
         }

         livingEntity = (LivingEntity)spawned;
      } finally {
         isSpawning = false;
      }

      return livingEntity;
   }

   public Entity applySpawnOptions(Entity e) {
      if (this.propertySets != null) {
         for (EntityPropertySet property : this.propertySets) {
            try {
               property.applyProperties(e);
            } catch (Throwable ex) {
               ex.printStackTrace();
            }
         }
      }

      return this.applyOptions(e);
   }

   protected Entity applyOptions(Entity entity) {
      return entity;
   }

   protected void registerProperty(EntityPropertySet property) {
      if (this.propertySets == null) {
         this.propertySets = new ArrayList<>();
      }

      this.propertySets.add(property);
   }

   public boolean compare(Entity entity) {
      return entity.getType() == this.getBukkitType();
   }

   public static BukkitEntityType getMythicEntity(MythicMob mm) {
      return getMythicEntity(mm.getEntityTypeString());
   }

   public static BukkitEntityType getMythicEntity(String s) {
      MythicEntityType met = MythicEntityType.get(s);
      return met == null ? null : getMythicEntity(met);
   }

   public static BukkitEntityType getMythicEntity(MythicEntityType entityType) {
      if (entityType == null) {
         return null;
      } else {
         Class<? extends BukkitEntityType> clazz = ENTITIES.get(entityType);
         if (clazz == null) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! MythicEntityType not found");

            try {
               clazz = BukkitCustom.class;
               return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
               MythicLogger.handleMinorError(e);
               return null;
            }
         } else {
            try {
               return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
               MythicLogger.handleMinorError(ex);
               return null;
            }
         }
      }
   }

   public abstract EntityType getBukkitType();

   @Override
   public String getHeadString() {
      return "";
   }

   public ItemStack getHead() {
      return this.defaultHead;
   }

   static {
      ENTITIES.put(MythicEntityType.ALLAY, BukkitAllay.class);
      ENTITIES.put(MythicEntityType.ARMADILLO, BukkitArmadillo.class);
      ENTITIES.put(MythicEntityType.ARMOR_STAND, BukkitArmorStand.class);
      ENTITIES.put(MythicEntityType.AXOLOTL, BukkitAxolotl.class);
      ENTITIES.put(MythicEntityType.BABY_DROWNED, BukkitBabyDrowned.class);
      ENTITIES.put(MythicEntityType.BABY_HUSK, BukkitBabyHusk.class);
      ENTITIES.put(MythicEntityType.BABY_PIGLIN, BukkitBabyPiglin.class);
      ENTITIES.put(MythicEntityType.BABY_PIGLIN_BRUTE, BukkitBabyPiglinBrute.class);
      ENTITIES.put(MythicEntityType.BABY_PIG_ZOMBIE, BukkitBabyPigZombie.class);
      ENTITIES.put(MythicEntityType.BABY_ZOGLIN, BukkitBabyZoglin.class);
      ENTITIES.put(MythicEntityType.BABY_ZOMBIE, BukkitBabyZombie.class);
      ENTITIES.put(MythicEntityType.BABY_ZOMBIE_VILLAGER, BukkitBabyZombieVillager.class);
      ENTITIES.put(MythicEntityType.BAT, BukkitBat.class);
      ENTITIES.put(MythicEntityType.BEE, BukkitBee.class);
      ENTITIES.put(MythicEntityType.BLAZE, BukkitBlaze.class);
      ENTITIES.put(MythicEntityType.BLOCK_DISPLAY, BukkitBlockDisplay.class);
      ENTITIES.put(MythicEntityType.BOAT, BukkitBoat.class);
      ENTITIES.put(MythicEntityType.BOGGED, BukkitBogged.class);
      ENTITIES.put(MythicEntityType.BREEZE, BukkitBreeze.class);
      ENTITIES.put(MythicEntityType.CAT, BukkitCat.class);
      ENTITIES.put(MythicEntityType.CAMEL, BukkitCamel.class);
      ENTITIES.put(MythicEntityType.CAMEL_HUSK, BukkitCamelHusk.class);
      ENTITIES.put(MythicEntityType.CAVE_SPIDER, BukkitCaveSpider.class);
      ENTITIES.put(MythicEntityType.CHEST_BOAT, BukkitBoatChest.class);
      ENTITIES.put(MythicEntityType.CHICKEN, BukkitChicken.class);
      ENTITIES.put(MythicEntityType.COD, BukkitCod.class);
      ENTITIES.put(MythicEntityType.COPPER_GOLEM, BukkitCopperGolem.class);
      ENTITIES.put(MythicEntityType.COW, BukkitCow.class);
      ENTITIES.put(MythicEntityType.CREAKING, BukkitCreaking.class);
      ENTITIES.put(MythicEntityType.CREEPER, BukkitCreeper.class);
      ENTITIES.put(MythicEntityType.CUSTOM, BukkitCustom.class);
      ENTITIES.put(MythicEntityType.DOLPHIN, BukkitDolphin.class);
      ENTITIES.put(MythicEntityType.DONKEY, BukkitDonkey.class);
      ENTITIES.put(MythicEntityType.DROWNED, BukkitDrowned.class);
      ENTITIES.put(MythicEntityType.ELDER_GUARDIAN, BukkitElderGuardian.class);
      ENTITIES.put(MythicEntityType.ENDERMAN, BukkitEnderman.class);
      ENTITIES.put(MythicEntityType.ENDERMITE, BukkitEndermite.class);
      ENTITIES.put(MythicEntityType.ENDER_DRAGON, BukkitEnderDragon.class);
      ENTITIES.put(MythicEntityType.EVOKER, BukkitEvoker.class);
      ENTITIES.put(MythicEntityType.EXPERIENCE_BOTTLE, BukkitExperienceBottle.class);
      ENTITIES.put(MythicEntityType.EXPERIENCE_ORB, BukkitExperienceOrb.class);
      ENTITIES.put(MythicEntityType.FALLING_BLOCK, BukkitFallingBlock.class);
      ENTITIES.put(MythicEntityType.FOX, BukkitFox.class);
      ENTITIES.put(MythicEntityType.FROG, BukkitFrog.class);
      ENTITIES.put(MythicEntityType.GHAST, BukkitGhast.class);
      ENTITIES.put(MythicEntityType.GIANT, BukkitGiant.class);
      ENTITIES.put(MythicEntityType.GOAT, BukkitGoat.class);
      ENTITIES.put(MythicEntityType.GLOW_SQUID, BukkitGlowSquid.class);
      ENTITIES.put(MythicEntityType.GUARDIAN, BukkitGuardian.class);
      ENTITIES.put(MythicEntityType.HAPPY_GHAST, BukkitHappyGhast.class);
      ENTITIES.put(MythicEntityType.HOGLIN, BukkitHoglin.class);
      ENTITIES.put(MythicEntityType.HORSE, BukkitHorse.class);
      ENTITIES.put(MythicEntityType.HUSK, BukkitHusk.class);
      ENTITIES.put(MythicEntityType.ILLUSIONER, BukkitIllusioner.class);
      ENTITIES.put(MythicEntityType.INTERACTION, BukkitInteraction.class);
      ENTITIES.put(MythicEntityType.IRON_GOLEM, BukkitIronGolem.class);
      ENTITIES.put(MythicEntityType.ITEM, BukkitItem.class);
      ENTITIES.put(MythicEntityType.ITEM_DISPLAY, BukkitItemDisplay.class);
      ENTITIES.put(MythicEntityType.LLAMA, BukkitLlama.class);
      ENTITIES.put(MythicEntityType.MAGMA_CUBE, BukkitMagmaCube.class);
      ENTITIES.put(MythicEntityType.MANNEQUIN, BukkitMannequin.class);
      ENTITIES.put(MythicEntityType.MARKER, BukkitMarker.class);
      ENTITIES.put(MythicEntityType.MINECART, BukkitMinecart.class);
      ENTITIES.put(MythicEntityType.MINECART_CHEST, BukkitMinecartChest.class);
      ENTITIES.put(MythicEntityType.MULE, BukkitMule.class);
      ENTITIES.put(MythicEntityType.MUSHROOM_COW, BukkitMushroomCow.class);
      ENTITIES.put(MythicEntityType.NAUTILUS, BukkitNautilus.class);
      ENTITIES.put(MythicEntityType.OCELOT, BukkitOcelot.class);
      ENTITIES.put(MythicEntityType.PANDA, BukkitPanda.class);
      ENTITIES.put(MythicEntityType.PARCHED, BukkitParched.class);
      ENTITIES.put(MythicEntityType.PARROT, BukkitParrot.class);
      ENTITIES.put(MythicEntityType.PHANTOM, BukkitPhantom.class);
      ENTITIES.put(MythicEntityType.PIG, BukkitPig.class);
      ENTITIES.put(MythicEntityType.PIGLIN, BukkitPiglin.class);
      ENTITIES.put(MythicEntityType.PIGLIN_BRUTE, BukkitPiglinBrute.class);
      ENTITIES.put(MythicEntityType.ZOMBIFIED_PIGLIN, BukkitPigZombie.class);
      ENTITIES.put(MythicEntityType.PILLAGER, BukkitPillager.class);
      ENTITIES.put(MythicEntityType.POLAR_BEAR, BukkitPolarBear.class);
      ENTITIES.put(MythicEntityType.PRIMED_TNT, BukkitTNT.class);
      ENTITIES.put(MythicEntityType.PUFFERFISH, BukkitPufferFish.class);
      ENTITIES.put(MythicEntityType.RABBIT, BukkitRabbit.class);
      ENTITIES.put(MythicEntityType.RAVAGER, BukkitRavager.class);
      ENTITIES.put(MythicEntityType.SALMON, BukkitSalmon.class);
      ENTITIES.put(MythicEntityType.SHEEP, BukkitSheep.class);
      ENTITIES.put(MythicEntityType.SHULKER, BukkitShulker.class);
      ENTITIES.put(MythicEntityType.SILVERFISH, BukkitSilverfish.class);
      ENTITIES.put(MythicEntityType.SKELETON, BukkitSkeleton.class);
      ENTITIES.put(MythicEntityType.SKELETON_HORSE, BukkitSkeletonHorse.class);
      ENTITIES.put(MythicEntityType.SLIME, BukkitSlime.class);
      ENTITIES.put(MythicEntityType.SNIFFER, BukkitSniffer.class);
      ENTITIES.put(MythicEntityType.SNOWMAN, BukkitSnowman.class);
      ENTITIES.put(MythicEntityType.SPIDER, BukkitSpider.class);
      ENTITIES.put(MythicEntityType.STRAY, BukkitStray.class);
      ENTITIES.put(MythicEntityType.STRIDER, BukkitStrider.class);
      ENTITIES.put(MythicEntityType.SQUID, BukkitSquid.class);
      ENTITIES.put(MythicEntityType.SULFUR_CUBE, BukkitSulfurCube.class);
      ENTITIES.put(MythicEntityType.TADPOLE, BukkitTadpole.class);
      ENTITIES.put(MythicEntityType.TEXT_DISPLAY, BukkitTextDisplay.class);
      ENTITIES.put(MythicEntityType.TRADER_LLAMA, BukkitTraderLlama.class);
      ENTITIES.put(MythicEntityType.TROPICAL_FISH, BukkitTropicalFish.class);
      ENTITIES.put(MythicEntityType.TURTLE, BukkitTurtle.class);
      ENTITIES.put(MythicEntityType.VEX, BukkitVex.class);
      ENTITIES.put(MythicEntityType.VILLAGER, BukkitVillager.class);
      ENTITIES.put(MythicEntityType.VINDICATOR, BukkitVindicator.class);
      ENTITIES.put(MythicEntityType.WANDERING_TRADER, BukkitWanderingTrader.class);
      ENTITIES.put(MythicEntityType.WARDEN, BukkitWarden.class);
      ENTITIES.put(MythicEntityType.WITCH, BukkitWitch.class);
      ENTITIES.put(MythicEntityType.WITHER, BukkitWither.class);
      ENTITIES.put(MythicEntityType.WITHER_SKELETON, BukkitWitherSkeleton.class);
      ENTITIES.put(MythicEntityType.WOLF, BukkitWolf.class);
      ENTITIES.put(MythicEntityType.ZOGLIN, BukkitZoglin.class);
      ENTITIES.put(MythicEntityType.ZOMBIE, BukkitZombie.class);
      ENTITIES.put(MythicEntityType.ZOMBIE_HORSE, BukkitZombieHorse.class);
      ENTITIES.put(MythicEntityType.ZOMBIE_NAUTILUS, BukkitZombieNautilus.class);
      ENTITIES.put(MythicEntityType.ZOMBIE_VILLAGER, BukkitZombieVillager.class);
   }
}
