package me.TechsCode.UltraPermissions.base.item;

import com.google.common.base.Enums;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class XEnchantment extends XModule<XEnchantment, Enchantment> {
   private static final boolean ISFLAT;
   private static final boolean IS_SUPER_FLAT;
   private static final boolean USES_WRAPPER;
   public static final XRegistry<XEnchantment, Enchantment> REGISTRY;
   public static final XEnchantment AQUA_AFFINITY;
   public static final XEnchantment BANE_OF_ARTHROPODS;
   public static final XEnchantment BINDING_CURSE;
   public static final XEnchantment BLAST_PROTECTION;
   public static final XEnchantment BREACH;
   public static final XEnchantment CHANNELING;
   public static final XEnchantment DENSITY;
   public static final XEnchantment DEPTH_STRIDER;
   public static final XEnchantment EFFICIENCY;
   public static final XEnchantment FEATHER_FALLING;
   public static final XEnchantment FIRE_ASPECT;
   public static final XEnchantment FIRE_PROTECTION;
   public static final XEnchantment FLAME;
   public static final XEnchantment FORTUNE;
   public static final XEnchantment FROST_WALKER;
   public static final XEnchantment IMPALING;
   public static final XEnchantment INFINITY;
   public static final XEnchantment KNOCKBACK;
   public static final XEnchantment LOOTING;
   public static final XEnchantment LOYALTY;
   public static final XEnchantment LUCK_OF_THE_SEA;
   public static final XEnchantment LURE;
   public static final XEnchantment MENDING;
   public static final XEnchantment MULTISHOT;
   public static final XEnchantment PIERCING;
   public static final XEnchantment POWER;
   public static final XEnchantment PROJECTILE_PROTECTION;
   public static final XEnchantment PROTECTION;
   public static final XEnchantment PUNCH;
   public static final XEnchantment QUICK_CHARGE;
   public static final XEnchantment RESPIRATION;
   public static final XEnchantment RIPTIDE;
   public static final XEnchantment SHARPNESS;
   public static final XEnchantment SILK_TOUCH;
   public static final XEnchantment SMITE;
   public static final XEnchantment SOUL_SPEED;
   public static final XEnchantment SWIFT_SNEAK;
   public static final XEnchantment THORNS;
   public static final XEnchantment UNBREAKING;
   public static final XEnchantment VANISHING_CURSE;
   public static final XEnchantment WIND_BURST;
   public static final XEnchantment SWEEPING_EDGE;
   public static final XEnchantment LUNGE;
   @Deprecated
   public static final XEnchantment[] VALUES;
   @Deprecated
   public static final Set<EntityType> EFFECTIVE_SMITE_ENTITIES;
   @Deprecated
   public static final Set<EntityType> EFFECTIVE_BANE_OF_ARTHROPODS_ENTITIES;

   private XEnchantment(Enchantment enchantment, String[] texts) {
      super(enchantment, texts);
   }

   @NotNull
   public static XEnchantment of(@NotNull Enchantment enchantment) {
      return REGISTRY.getByBukkitForm(enchantment);
   }

   public static Optional<XEnchantment> of(@NotNull String text) {
      return REGISTRY.getByName(text);
   }

   @Deprecated
   @NotNull
   public static XEnchantment[] values() {
      return REGISTRY.values();
   }

   @NotNull
   private static XEnchantment std(@NotNull String... texts) {
      XEnchantment xEnchantment = REGISTRY.std(texts);
      if (USES_WRAPPER && xEnchantment.isSupported()) {
         Enchantment enchantment = xEnchantment.get();
         if (enchantment instanceof EnchantmentWrapper) {
            Enchantment wrapped = ((EnchantmentWrapper)enchantment).getEnchantment();
            REGISTRY.bukkitMapping().put(wrapped, xEnchantment);
         }
      }

      return xEnchantment;
   }

   @Deprecated
   private static Enchantment getBukkitEnchant(String text) {
      if (IS_SUPER_FLAT) {
         return (Enchantment)Registry.ENCHANTMENT.get(NamespacedKey.minecraft(text.toLowerCase(Locale.ENGLISH)));
      } else {
         return ISFLAT ? Enchantment.getByKey(NamespacedKey.minecraft(text.toLowerCase(Locale.ENGLISH))) : Enchantment.getByName(text);
      }
   }

   @Deprecated
   public static boolean isSmiteEffectiveAgainst(@Nullable EntityType entityType) {
      return entityType != null && EFFECTIVE_SMITE_ENTITIES.contains(entityType);
   }

   @Deprecated
   public static boolean isArthropodsEffectiveAgainst(@Nullable EntityType entityType) {
      return entityType != null && EFFECTIVE_BANE_OF_ARTHROPODS_ENTITIES.contains(entityType);
   }

   @Deprecated
   @NotNull
   public static Optional<XEnchantment> matchXEnchantment(@NotNull String text) {
      if (text != null && !text.isEmpty()) {
         return of(text);
      } else {
         throw new IllegalArgumentException("Enchantment name cannot be null or empty");
      }
   }

   @Deprecated
   @NotNull
   public static XEnchantment matchXEnchantment(@NotNull Enchantment enchantment) {
      Objects.requireNonNull(enchantment, "Cannot parse XEnchantment of a null enchantment");
      return of(enchantment);
   }

   @NotNull
   public ItemStack getBook(int count) {
      ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
      EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta)item.getItemMeta();
      itemMeta.addStoredEnchant(this.get(), count, true);
      item.setItemMeta(itemMeta);
      return item;
   }

   @Deprecated
   @Nullable
   public Enchantment getEnchant() {
      return this.get();
   }

   static {
      boolean flag = false;

      boolean isFlat;
      try {
         Class type = Class.forName("org.bukkit.NamespacedKey");
         Class enchantmentClass = Class.forName("org.bukkit.enchantments.Enchantment");
         enchantmentClass.getDeclaredMethod("getByKey", type);
         isFlat = true;
      } catch (ClassNotFoundException | NoSuchMethodException exception) {
         isFlat = false;
      }

      boolean isSuperFlat;
      try {
         Class.forName("org.bukkit.Registry");
         isSuperFlat = true;
      } catch (ClassNotFoundException exception) {
         isSuperFlat = false;
      }

      for (Field declaredField : Enchantment.class.getDeclaredFields()) {
         int modifiers = declaredField.getModifiers();
         if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && declaredField.getType() == Enchantment.class) {
            try {
               Object obj = declaredField.get(null);
               if (obj instanceof EnchantmentWrapper) {
                  flag = true;
               }
            } catch (IllegalAccessException exception) {
               throw new IllegalStateException("Cannot get enchantment field for " + declaredField, exception);
            }
         }
      }

      ISFLAT = isFlat;
      IS_SUPER_FLAT = isSuperFlat;
      USES_WRAPPER = flag;
      REGISTRY = new XRegistry<>(Enchantment.class, XEnchantment.class, () -> Registry.ENCHANTMENT, XEnchantment::new, value -> new XEnchantment[value]);
      AQUA_AFFINITY = std("WATER_WORKER", "WATER_WORKER", "AQUA_AFFINITY", "WATER_MINE");
      BANE_OF_ARTHROPODS = std("BANE_OF_ARTHROPODS", "DAMAGE_ARTHROPODS", "BANE_OF_ARTHROPOD", "ARTHROPOD");
      BINDING_CURSE = std("BINDING_CURSE", "BIND_CURSE", "BINDING", "BIND");
      BLAST_PROTECTION = std("PROTECTION_EXPLOSIONS", "BLAST_PROTECT", "EXPLOSIONS_PROTECTION", "EXPLOSION_PROTECTION", "BLAST_PROTECTION");
      BREACH = std("BREACH");
      CHANNELING = std("CHANNELING", "CHANNELLING", "CHANELLING", "CHANELING", "CHANNEL");
      DENSITY = std("DENSITY");
      DEPTH_STRIDER = std("DEPTH_STRIDER", "DEPTH", "STRIDER");
      EFFICIENCY = std("EFFICIENCY", "DIG_SPEED", "MINE_SPEED", "CUT_SPEED");
      FEATHER_FALLING = std("PROTECTION_FALL", "FEATHER_FALL", "FALL_PROTECTION", "FEATHER_FALLING");
      FIRE_ASPECT = std("FIRE_ASPECT", "FIRE", "MELEE_FIRE", "MELEE_FLAME");
      FIRE_PROTECTION = std("PROTECTION_FIRE", "FIRE_PROT", "FIRE_PROTECT", "FIRE_PROTECTION", "FLAME_PROTECTION", "FLAME_PROTECT");
      FLAME = std("FLAME", "ARROW_FIRE", "FLAME_ARROW", "FIRE_ARROW");
      FORTUNE = std("FORTUNE", "LOOT_BONUS_BLOCKS", "BLOCKS_LOOT_BONUS");
      FROST_WALKER = std("FROST_WALKER", "FROST", "WALKER");
      IMPALING = std("IMPALING", "IMPALE", "OCEAN_DAMAGE");
      INFINITY = std("INFINITY", "ARROW_INFINITE", "INFINITE_ARROWS", "INFINITE", "UNLIMITED_ARROWS");
      KNOCKBACK = std("KNOCKBACK");
      LOOTING = std("LOOTING", "LOOT_BONUS_MOBS", "MOB_LOOT", "MOBS_LOOT_BONUS");
      LOYALTY = std("LOYALTY", "LOYAL", "RETURN");
      LUCK_OF_THE_SEA = std("LUCK_OF_THE_SEA", "LUCK", "LUCK_OF_SEA", "LUCK_OF_SEAS", "ROD_LUCK");
      LURE = std("LURE", "ROD_LURE");
      MENDING = std("MENDING");
      MULTISHOT = std("MULTISHOT", "TRIPLE_SHOT");
      PIERCING = std("PIERCING");
      POWER = std("POWER", "ARROW_DAMAGE", "ARROW_POWER");
      PROJECTILE_PROTECTION = std("PROTECTION_PROJECTILE", "PROJECTILE_PROTECTION");
      PROTECTION = std("PROTECTION", "PROTECTION_ENVIRONMENTAL", "PROTECT");
      PUNCH = std("PUNCH", "ARROW_KNOCKBACK", "ARROW_PUNCH");
      QUICK_CHARGE = std("QUICK_CHARGE", "QUICKCHARGE", "QUICK_DRAW", "FAST_CHARGE", "FAST_DRAW");
      RESPIRATION = std("RESPIRATION", "OXYGEN", "BREATH", "BREATHING");
      RIPTIDE = std("RIPTIDE", "RIP", "TIDE", "LAUNCH");
      SHARPNESS = std("SHARPNESS", "DAMAGE_ALL", "ALL_DAMAGE", "ALL_DMG", "SHARP");
      SILK_TOUCH = std("SILK_TOUCH", "SOFT_TOUCH");
      SMITE = std("SMITE", "DAMAGE_UNDEAD", "UNDEAD_DAMAGE");
      SOUL_SPEED = std("SOUL_SPEED", "SPEED_SOUL", "SOUL_RUNNER");
      SWIFT_SNEAK = std("SWIFT_SNEAK", "SNEAK_SWIFT");
      THORNS = std("THORNS", "HIGHCRIT", "THORN", "HIGHERCRIT");
      UNBREAKING = std("UNBREAKING", "DURABILITY", "DURA");
      VANISHING_CURSE = std("VANISHING_CURSE", "VANISH_CURSE", "VANISHING", "VANISH");
      WIND_BURST = std("WIND_BURST");
      SWEEPING_EDGE = std("SWEEPING", "SWEEPING_EDGE", "SWEEP_EDGE");
      LUNGE = std("LUNGE");
      VALUES = values();
      EntityType entityType = (EntityType)Enums.getIfPresent(EntityType.class, "BEE").orNull();
      EntityType phantom = (EntityType)Enums.getIfPresent(EntityType.class, "PHANTOM").orNull();
      EntityType drowned = (EntityType)Enums.getIfPresent(EntityType.class, "DROWNED").orNull();
      EntityType witherSkeleton = (EntityType)Enums.getIfPresent(EntityType.class, "WITHER_SKELETON").orNull();
      EntityType skeletonHorse = (EntityType)Enums.getIfPresent(EntityType.class, "SKELETON_HORSE").orNull();
      EntityType stray = (EntityType)Enums.getIfPresent(EntityType.class, "STRAY").orNull();
      EntityType husk = (EntityType)Enums.getIfPresent(EntityType.class, "HUSK").orNull();
      EnumSet set = EnumSet.of(EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.SILVERFISH, EntityType.ENDERMITE);
      if (entityType != null) {
         set.add(entityType);
      }

      EFFECTIVE_BANE_OF_ARTHROPODS_ENTITIES = Collections.unmodifiableSet(set);
      EnumSet smiteEntities = EnumSet.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.WITHER);
      if (phantom != null) {
         smiteEntities.add(phantom);
      }

      if (drowned != null) {
         smiteEntities.add(drowned);
      }

      if (witherSkeleton != null) {
         smiteEntities.add(witherSkeleton);
      }

      if (skeletonHorse != null) {
         smiteEntities.add(skeletonHorse);
      }

      if (stray != null) {
         smiteEntities.add(stray);
      }

      if (husk != null) {
         smiteEntities.add(husk);
      }

      EFFECTIVE_SMITE_ENTITIES = Collections.unmodifiableSet(smiteEntities);
      if (USES_WRAPPER) {
         for (Field declaredField : Enchantment.class.getDeclaredFields()) {
            int modifiers = declaredField.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && declaredField.getType() == Enchantment.class) {
               try {
                  if (declaredField.get(null) instanceof EnchantmentWrapper enchantmentWrapper) {
                     XEnchantment xEnchantment = REGISTRY.bukkitMapping().get(enchantmentWrapper.getEnchantment());
                     Objects.requireNonNull(xEnchantment, () -> "No main mapping found for Enchantment." + declaredField.getName() + " (" + enchantmentWrapper + ")");
                     REGISTRY.bukkitMapping().put(enchantmentWrapper, xEnchantment);
                  }
               } catch (IllegalAccessException exception) {
                  throw new IllegalStateException("Cannot get direct enchantment field for " + declaredField, exception);
               }
            }
         }
      }

      REGISTRY.discardMetadata();
   }
}
