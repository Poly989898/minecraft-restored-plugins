package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractParticle;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderColor;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.volatilecode.handlers.ParticleBundle;
import io.lumine.mythic.api.volatilecode.handlers.VolatileParticleHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Particle.DustTransition;
import org.bukkit.Particle.Spell;
import org.bukkit.Particle.Trail;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public final class BukkitParticle implements AbstractParticle {
   private static final List<BukkitParticle> VALUES = new ArrayList<>();
   public static final BukkitParticle POOF = register("POOF", "poof", "explosion_normal", "explode", "explosion_small");
   public static final BukkitParticle EXPLOSION = register("EXPLOSION", "explosion", "explosion_large", "largeexplode", "largeexplosion");
   public static final BukkitParticle EXPLOSION_EMITTER = register("EXPLOSION_EMITTER", "explosion_emitter", "explosion_huge", "hugeexplode", "hugeexplosion");
   public static final BukkitParticle FIREWORK = register("FIREWORK", "firework", "fireworks", "fireworks_spark", "fireworksspark");
   public static final BukkitParticle BUBBLE = register("BUBBLE", "bubble", "water_bubble");
   public static final BukkitParticle SPLASH = register("SPLASH", "splash", "water_splash");
   public static final BukkitParticle FISHING = register("FISHING", "fishing", "water_wake", "wake");
   public static final BukkitParticle UNDERWATER = register("UNDERWATER", "underwater", "suspended");
   public static final BukkitParticle SUSPENDED_DEPTH = register("SUSPENDED_DEPTH", "suspended_depth", "depthsuspend");
   public static final BukkitParticle CRIT = register("CRIT", "crit");
   public static final BukkitParticle ENCHANTED_HIT = register("ENCHANTED_HIT", "enchanted_hit", "crit_magic", "magiccrit");
   public static final BukkitParticle SMOKE = register("SMOKE", "smoke", "smoke_normal");
   public static final BukkitParticle LARGE_SMOKE = register("LARGE_SMOKE", "large_smoke", "smoke_large", "largesmoke");
   public static final BukkitParticle EFFECT = register("EFFECT", "effect", "spell");
   public static final BukkitParticle INSTANT_EFFECT = register("INSTANT_EFFECT", "instant_effect", "spell_instant", "instant_spell");
   public static final BukkitParticle ENTITY_EFFECT = register("ENTITY_EFFECT", "entity_effect", "spell_mob", "mob_spell");
   public static final BukkitParticle SPELL_MOB_AMBIENT = register("SPELL_MOB_AMBIENT", "ambient_entity_effect", "mob_spell_ambient", "spell_mob_ambient");
   public static final BukkitParticle WITCH = register("WITCH", "witch", "spell_witch", "witch_magic");
   public static final BukkitParticle DRIPPING_WATER = register("DRIPPING_WATER", "dripping_water", "drip_water", "dripwater");
   public static final BukkitParticle DRIPPING_LAVA = register("DRIPPING_LAVA", "dripping_lava", "drip_lava", "driplava");
   public static final BukkitParticle ANGRY_VILLAGER = register("ANGRY_VILLAGER", "angry_villager", "villager_angry", "angryvillager");
   public static final BukkitParticle HAPPY_VILLAGER = register("HAPPY_VILLAGER", "happy_villager", "villager_happy", "happyvillager");
   public static final BukkitParticle MYCELIUM = register("MYCELIUM", "mycelium", "town_aura", "townaura");
   public static final BukkitParticle NOTE = register("NOTE", "note");
   public static final BukkitParticle PORTAL = register("PORTAL", "portal");
   public static final BukkitParticle ENCHANT = register("ENCHANT", "enchant", "enchantment_table", "enchantmenttable", "enchantingtable");
   public static final BukkitParticle FLAME = register("FLAME", "flame");
   public static final BukkitParticle LAVA = register("LAVA", "lava");
   public static final BukkitParticle CLOUD = register("CLOUD", "cloud");
   public static final BukkitParticle DUST = register("DUST", "dust", "red_dust", "reddust", "redstone");
   public static final BukkitParticle ITEM_SNOWBALL = register("ITEM_SNOWBALL", "item_snowball", "snowball", "snowballpoof");
   public static final BukkitParticle ITEM_SLIME = register("ITEM_SLIME", "item_slime", "slime");
   public static final BukkitParticle HEART = register("HEART", "heart");
   public static final BukkitParticle BARRIER = register("BARRIER", "barrier");
   public static final BukkitParticle ITEM = register("ITEM", "item", "item_crack", "icon_crack", "item_crack", "itemcrack");
   public static final BukkitParticle BLOCK = register("BLOCK", "block_crack", "block_crack", "blockcrack");
   public static final BukkitParticle BLOCK_DUST = register("BLOCK_DUST", "block_dust", "block_dust", "blockdust");
   public static final BukkitParticle RAIN = register("RAIN", "water_drop", "rain", "droplet");
   public static final BukkitParticle ELDER_GUARDIAN = register("ELDER_GUARDIAN", "elder_guardian", "mob_appearance", "mobappearance");
   public static final BukkitParticle DRAGON_BREATH = register("DRAGON_BREATH", "dragon_breath", "dragonbreath");
   public static final BukkitParticle END_ROD = register("END_ROD", "end_rod", "endrod");
   public static final BukkitParticle DAMAGE_INDICATOR = register("DAMAGE_INDICATOR", "damage_indicator", "damageindicator");
   public static final BukkitParticle SWEEP_ATTACK = register("SWEEP_ATTACK", "sweep_attack", "sweepattack");
   public static final BukkitParticle FALLING_DUST = register("FALLING_DUST", "falling_dust", "fallingdust");
   public static final BukkitParticle TOTEM_OF_UNDYING = register("TOTEM_OF_UNDYING", "totem_of_undying", "totem");
   public static final BukkitParticle SPIT = register("SPIT", "spit");
   public static final BukkitParticle SQUID_INK = register("SQUID_INK", "squid_ink", "squidink");
   public static final BukkitParticle BUBBLE_POP = register("BUBBLE_POP", "bubble_pop", "bubblepop");
   public static final BukkitParticle CURRENT_DOWN = register("CURRENT_DOWN", "current_down", "currentdown");
   public static final BukkitParticle BUBBLE_COLUMN_UP = register("BUBBLE_COLUMN_UP", "bubble_column_up", "bubblecolumn", "bubble_column");
   public static final BukkitParticle NAUTILUS = register("NAUTILUS", "nautilus");
   public static final BukkitParticle DOLPHIN = register("DOLPHIN", "dolphin");
   public static final BukkitParticle COMPOSTER = register("COMPOSTER", "composter");
   public static final BukkitParticle FALLING_LAVA = register("FALLING_LAVA", "falling_lava", "fallinglava");
   public static final BukkitParticle FALLING_WATER = register("FALLING_WATER", "falling_water", "fallingwater");
   public static final BukkitParticle FLASH = register("FLASH", "flash");
   public static final BukkitParticle LANDING_LAVA = register("LANDING_LAVA", "landing_lava", "landinglava");
   public static final BukkitParticle SNEEZE = register("SNEEZE", "sneeze");
   public static final BukkitParticle SNOWFLAKE = register("SNOWFLAKE", "snowflake");
   public static final BukkitParticle CAMPFIRE_COSY_SMOKE = register(
      "CAMPFIRE_COSY_SMOKE", "campfire_cosy_smoke", "campfire_cosy", "campfire_cozy_smoke", "campfire_cozy", "campfire"
   );
   public static final BukkitParticle CAMPFIRE_SIGNAL_SMOKE = register("CAMPFIRE_SIGNAL_SMOKE", "campfire_signal_smoke", "campfire_signal");
   public static final BukkitParticle DRIPPING_HONEY = register("DRIPPING_HONEY", "dripping_honey", "drippinghoney");
   public static final BukkitParticle FALLING_HONEY = register("FALLING_HONEY", "falling_honey", "fallinghoney");
   public static final BukkitParticle FALLING_NECTAR = register("FALLING_NECTAR", "falling_nectar", "fallingnectar");
   public static final BukkitParticle LANDING_HONEY = register("LANDING_HONEY", "landing_honey", "landinghoney");
   public static final BukkitParticle ASH = register("ASH", "ash");
   public static final BukkitParticle WHITE_ASH = register("WHITE_ASH", "white_ash");
   public static final BukkitParticle CRIMSON_SPORE = register("CRIMSON_SPORE", "crimson_spore", "crimson_spores", "crimsonspore", "crimsonspores");
   public static final BukkitParticle SOUL_FIRE_FLAME = register("SOUL_FIRE_FLAME", "soul_fire_flame", "soulfireflame", "soulfire", "soulflame");
   public static final BukkitParticle WARPED_SPORE = register("WARPED_SPORE", "warped_spore", "warped_spores", "warpedspore", "warpedspores");
   public static final BukkitParticle DRIPPING_OBSIDIAN_TEAR = register(
      "DRIPPING_OBSIDIAN_TEAR", "dripping_obsidian_tear", "dropping_obsidian_tear", "drippingobsidiantear"
   );
   public static final BukkitParticle FALLING_OBSIDIAN_TEAR = register("FALLING_OBSIDIAN_TEAR", "falling_obsidian_tear", "fallingobsidiantear");
   public static final BukkitParticle LANDING_OBSIDIAN_TEAR = register("LANDING_OBSIDIAN_TEAR", "landing_obsidian_tear", "landingobsidiantear");
   public static final BukkitParticle SOUL = register("SOUL", "soul");
   public static final BukkitParticle DRIPPING_DRIPSTONE_LAVA = register("DRIPPING_DRIPSTONE_LAVA", "dripping_dripstone_lava", "dropping_dripstone_lava");
   public static final BukkitParticle DRIPPING_DRIPSTONE_WATER = register("DRIPPING_DRIPSTONE_WATER", "dripping_dripstone_water", "dropping_dripstone_water");
   public static final BukkitParticle DUST_COLOR_TRANSITION = register("DUST_COLOR_TRANSITION", "dust_color_transition", "dustcolortransition", "dustcolor");
   public static final BukkitParticle ELECTRIC_SPARK = register("ELECTRIC_SPARK", "electric_spark", "electricspark", "spark");
   public static final BukkitParticle FALLING_DRIPSTONE_LAVA = register("FALLING_DRIPSTONE_LAVA", "falling_dripstone_lava");
   public static final BukkitParticle FALLING_DRIPSTONE_WATER = register("FALLING_DRIPSTONE_WATER", "falling_dripstone_water");
   public static final BukkitParticle GLOW = register("GLOW", "glow");
   public static final BukkitParticle GLOW_SQUID_INK = register("GLOW_SQUID_INK", "glow_squid_ink", "glowingink", "glowink", "glowsquid");
   public static final BukkitParticle LIGHT = register("LIGHT", "light");
   public static final BukkitParticle REVERSE_PORTAL = register("REVERSE_PORTAL", "reverse_portal");
   public static final BukkitParticle SCRAPE = register("SCRAPE", "scrape");
   public static final BukkitParticle SMALL_FLAME = register("SMALL_FLAME", "small_flame");
   public static final BukkitParticle VIBRATION = register("VIBRATION", "vibration");
   public static final BukkitParticle WAX_OFF = register("WAX_OFF", "wax_off");
   public static final BukkitParticle WAX_ON = register("WAX_ON", "wax_on");
   public static final BukkitParticle FALLING_SPORE_BLOSSOM = register("FALLING_SPORE_BLOSSOM", "falling_spore_blossom");
   public static final BukkitParticle SPORE_BLOSSOM_AIR = register("SPORE_BLOSSOM_AIR", "spore_blossom_air");
   public static final BukkitParticle BLOCK_MARKER = register("BLOCK_MARKER", "block_marker");
   public static final BukkitParticle SCULK_SOUL = register("SCULK_SOUL", "sculk_soul");
   public static final BukkitParticle SHRIEK = register("SHRIEK", "shriek");
   public static final BukkitParticle SCULK_CHARGE = register("SCULK_CHARGE", "sculk_charge");
   public static final BukkitParticle SCULK_CHARGE_POP = register("SCULK_CHARGE_POP", "sculk_charge_pop");
   public static final BukkitParticle SONIC_BOOM = register("SONIC_BOOM", "sonic_boom");
   public static final BukkitParticle CHERRY_LEAVES = register("CHERRY_LEAVES", "cherry_leaves");
   public static final BukkitParticle EGG_CRACK = register("EGG_CRACK", "egg_crack");
   public static final BukkitParticle WHITE_SMOKE = register("WHITE_SMOKE", "white_smoke");
   public static final BukkitParticle DUST_PLUME = register("DUST_PLUME", "dust_plume");
   public static final BukkitParticle GUST = register("GUST", "gust");
   public static final BukkitParticle GUST_EMITTER_SMALL = register("GUST_EMITTER_SMALL", "gust_emitter_small");
   public static final BukkitParticle GUST_EMITTER_LARGE = register("GUST_EMITTER_LARGE", "gust_emitter_large", "large_gust");
   public static final BukkitParticle SMALL_GUST = register("SMALL_GUST", "small_gust", "gust_small");
   public static final BukkitParticle TRIAL_SPAWNER_DETECTION = register("TRIAL_SPAWNER_DETECTION", "trial_spawner_detection");
   public static final BukkitParticle TRIAL_SPAWNER_DETECTION_OMINOUS = register("TRIAL_SPAWNER_DETECTION_OMINOUS", "trial_spawner_detection_ominous");
   public static final BukkitParticle TRIAL_OMEN = register("TRIAL_OMEN", "trial_omen");
   public static final BukkitParticle DUST_PILLAR = register("DUST_PILLAR", "dust_pillar");
   public static final BukkitParticle VAULT_CONNECTION = register("VAULT_CONNECTION", "vault_connection");
   public static final BukkitParticle OMINOUS_SPAWNING = register("OMINOUS_SPAWNING", "ominous_spawning");
   public static final BukkitParticle RAID_OMEN = register("RAID_OMEN", "raid_omen");
   public static final BukkitParticle INFESTED = register("INFESTED", "infested");
   public static final BukkitParticle BLOCK_CRUMBLE = register("BLOCK_CRUMBLE", "block_crumble");
   public static final BukkitParticle TRAIL = register("TRAIL", "trail");
   public static final BukkitParticle PALE_OAK_LEAVES = register("PALE_OAK_LEAVES", "pale_oak_leaves");
   public static final BukkitParticle TINTED_LEAVES = register("TINTED_LEAVES", "tinted_leaves");
   public static final BukkitParticle FIREFLY = register("FIREFLY", "firefly");
   public static final BukkitParticle COPPER_FIRE_FLAME = register("COPPER_FIRE_FLAME", "copper_fire_flame");
   private static final BukkitParticle.ParticleHandler PARTICLE_ALIASES = new BukkitParticle.ParticleHandler();
   @Getter
   private final Particle bukkitParticle;
   private final String name;
   @Getter
   private final String[] aliases;

   private static BukkitParticle register(String name, String bukkitName, String... aliases) {
      BukkitParticle particle = new BukkitParticle(name, resolve(bukkitName), aliases);
      VALUES.add(particle);
      return particle;
   }

   private static void registerUnmapped() {
      HashSet<Particle> mapped = new HashSet<>();

      for (BukkitParticle particle : VALUES) {
         if (particle.bukkitParticle != null) {
            mapped.add(particle.bukkitParticle);
         }
      }

      for (Particle bukkitParticle : Particle.values()) {
         if (!mapped.contains(bukkitParticle) && !bukkitParticle.name().startsWith("LEGACY_")) {
            BukkitParticle particlex = new BukkitParticle(bukkitParticle.name(), bukkitParticle);
            VALUES.add(particlex);
            PARTICLE_ALIASES.register(particlex);
         }
      }
   }

   public static Collection<BukkitParticle> values() {
      return Collections.unmodifiableList(VALUES);
   }

   public static BukkitParticle get(String key) {
      BukkitParticle particle = PARTICLE_ALIASES.getOrDefault(key.toUpperCase(), null);
      if (particle == null) {
         MythicLogger.errorGenericConfig("Particle '" + key + "' is not supported by this version");
         return CLOUD;
      } else {
         return particle;
      }
   }

   private BukkitParticle(String name, Particle bukkitParticle, String... aliases) {
      this.name = name;
      this.bukkitParticle = bukkitParticle;
      this.aliases = aliases;
   }

   public String name() {
      return this.name;
   }

   @Override
   public String toString() {
      return this.name;
   }

   private static Particle resolve(String bukkitName) {
      try {
         String ex = bukkitName.toUpperCase();

         return switch (ex) {
            case "POOF" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("POOF") : Particle.valueOf("EXPLOSION_NORMAL");
            case "EXPLOSION" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("EXPLOSION") : Particle.valueOf("EXPLOSION_LARGE");
            case "EXPLOSION_EMITTER" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
               ? Particle.valueOf("EXPLOSION_EMITTER")
               : Particle.valueOf("EXPLOSION_HUGE");
            case "FIREWORK" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("FIREWORK") : Particle.valueOf("FIREWORKS_SPARK");
            case "BUBBLE" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("BUBBLE") : Particle.valueOf("WATER_BUBBLE");
            case "SPLASH" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("SPLASH") : Particle.valueOf("WATER_SPLASH");
            case "FISHING" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("FISHING") : Particle.valueOf("WATER_WAKE");
            case "UNDERWATER" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("UNDERWATER") : Particle.valueOf("SUSPENDED");
            case "ENCHANTED_HIT" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("ENCHANTED_HIT") : Particle.valueOf("CRIT_MAGIC");
            case "SMOKE" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("SMOKE") : Particle.valueOf("SMOKE_NORMAL");
            case "LARGE_SMOKE" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("LARGE_SMOKE") : Particle.valueOf("SMOKE_LARGE");
            case "EFFECT" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("EFFECT") : Particle.valueOf("SPELL");
            case "INSTANT_EFFECT" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
               ? Particle.valueOf("INSTANT_EFFECT")
               : Particle.valueOf("SPELL_INSTANT");
            case "ENTITY_EFFECT" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("ENTITY_EFFECT") : Particle.valueOf("SPELL_MOB");
            case "AMBIENT_ENTITY_EFFECT" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
               ? Particle.valueOf("ENTITY_EFFECT")
               : Particle.valueOf("SPELL_MOB_AMBIENT");
            case "DRIPPING_WATER" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("DRIPPING_WATER") : Particle.valueOf("DRIP_WATER");
            case "DRIPPING_LAVA" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("DRIPPING_LAVA") : Particle.valueOf("DRIP_LAVA");
            case "ANGRY_VILLAGER" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
               ? Particle.valueOf("ANGRY_VILLAGER")
               : Particle.valueOf("VILLAGER_ANGRY");
            case "HAPPY_VILLAGER" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
               ? Particle.valueOf("HAPPY_VILLAGER")
               : Particle.valueOf("VILLAGER_HAPPY");
            case "MYCELIUM" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("MYCELIUM") : Particle.valueOf("TOWN_AURA");
            case "ENCHANT" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("ENCHANT") : Particle.valueOf("ENCHANTMENT_TABLE");
            case "DUST" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("DUST") : Particle.valueOf("REDSTONE");
            case "ITEM_SNOWBALL" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("ITEM_SNOWBALL") : Particle.valueOf("SNOWBALL");
            case "ITEM_SLIME" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("ITEM_SLIME") : Particle.valueOf("SLIME");
            case "ITEM" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("ITEM") : Particle.valueOf("ITEM_CRACK");
            case "BLOCK" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("BLOCK") : Particle.valueOf("BLOCK_CRACK");
            case "RAIN" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("RAIN") : Particle.valueOf("WATER_DROP");
            case "ELDER_GUARDIAN" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
               ? Particle.valueOf("ELDER_GUARDIAN")
               : Particle.valueOf("MOB_APPEARANCE");
            case "TOTEM_OF_UNDYING" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("TOTEM_OF_UNDYING") : Particle.valueOf("TOTEM");
            case "WITCH" -> ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? Particle.valueOf("WITCH") : Particle.valueOf("SPELL_WITCH");
            default -> Particle.valueOf(bukkitName.toUpperCase());
         };
      } catch (IllegalArgumentException e) {
         return null;
      }
   }

   public Particle toBukkitParticle() {
      return this.bukkitParticle;
   }

   public boolean requiresData() {
      Particle particle = this.toBukkitParticle();
      return particle == null ? false : !particle.getDataType().equals(Void.class);
   }

   public boolean validateData(Object obj) {
      Particle particle = this.toBukkitParticle();
      if (particle.getDataType().equals(Void.class)) {
         MythicLogger.error("Could not send particle: particle data is void.");
         return false;
      } else if (particle.getDataType().equals(ItemStack.class)) {
         if (obj instanceof ItemStack) {
            return true;
         } else {
            MythicLogger.error("Could not send particle: particle {0} data must be ItemStack.", particle.getKey());
            return false;
         }
      } else if (particle.getDataType() == BlockData.class) {
         if (obj instanceof BlockData) {
            return true;
         } else {
            MythicLogger.error("Could not send particle: particle {0} data must be BlockData.", particle.getKey());
            return false;
         }
      } else if (particle.getDataType() == MaterialData.class) {
         if (obj instanceof MaterialData) {
            return true;
         } else {
            MythicLogger.error("Could not send particle: particle {0} data must be MaterialData.", particle.getKey());
            return false;
         }
      } else if (particle.getDataType() == Color.class) {
         if (obj instanceof Color) {
            return true;
         } else {
            MythicLogger.error("Could not send particle: particle {0} data must be Color.", particle.getKey());
            return false;
         }
      } else if (particle.getDataType() == DustOptions.class) {
         if (obj instanceof DustOptions) {
            return true;
         } else {
            MythicLogger.error("Could not send particle: particle {0} data must be DustOptions.", particle.getKey());
            return false;
         }
      } else if (particle.getDataType() == DustTransition.class) {
         if (obj instanceof DustTransition) {
            return true;
         } else {
            MythicLogger.error("Could not send particle: particle {0} data must be DustTransition.", particle.getKey());
            return false;
         }
      } else {
         return true;
      }
   }

   public Function<PlaceholderMeta, Object> parseDataOptions(MythicLineConfig config) {
      Particle particle = this.toBukkitParticle();
      if (particle.getDataType().equals(ItemStack.class)) {
         String strMaterial = config.getString(new String[]{"material", "m"}, "STONE");

         try {
            return data -> new ItemStack(Material.matchMaterial(strMaterial.toUpperCase()));
         } catch (Exception e) {
            return data -> new ItemStack(Material.STONE);
         }
      } else if (particle.getDataType().equals(BlockData.class)) {
         String strMaterial = config.getString(new String[]{"material", "m"}, "STONE");

         try {
            Material material = Material.matchMaterial(strMaterial.toUpperCase());
            return data -> Bukkit.getServer().createBlockData(material);
         } catch (Exception ex) {
            return data -> Bukkit.getServer().createBlockData(Material.STONE);
         }
      } else if (particle.getDataType().equals(MaterialData.class)) {
         String strMaterial = config.getString(new String[]{"material", "m"}, "STONE");

         try {
            return data -> Material.matchMaterial(strMaterial.toUpperCase()).getData();
         } catch (Exception ex2) {
            return data -> Material.STONE.getData();
         }
      } else if (particle.getDataType().equals(Color.class)) {
         PlaceholderColor color = config.getPlaceholderColor(new String[]{"color", "c"}, this == FLASH ? "#FFFFFF" : "#FF0000");
         return data -> color.get(data).toBukkitColor();
      } else if (particle.getDataType().equals(DustOptions.class)) {
         PlaceholderColor color = config.getPlaceholderColor(new String[]{"color", "c"}, "#FF0000");
         PlaceholderFloat size = config.getPlaceholderFloat(new String[]{"size"}, 1.0F);
         return data -> new DustOptions(color.get(data).toBukkitColor(), size.get(data));
      } else if (particle.getDataType().equals(DustTransition.class)) {
         PlaceholderColor color1 = config.getPlaceholderColor(new String[]{"color", "c", "color1", "c1", "fromcolor", "fc"}, "#FF0000");
         PlaceholderColor color2 = config.getPlaceholderColor(new String[]{"color2", "c2", "tocolor", "tc"}, "#0000FF");
         PlaceholderFloat size = config.getPlaceholderFloat(new String[]{"size"}, 1.0F);
         return data -> new DustTransition(color1.get(data).toBukkitColor(), color2.get(data).toBukkitColor(), size.get(data));
      } else {
         try {
            if (particle.getDataType().equals(Spell.class)) {
               PlaceholderColor color = config.getPlaceholderColor(new String[]{"color", "c"}, "#FF0000");
               PlaceholderFloat power = config.getPlaceholderFloat(new String[]{"power", "p"}, 1.0F);
               return data -> new Spell(color.get(data).toBukkitColor(), power.get(data));
            }
         } catch (Throwable ex3) {
         }

         if (particle == Particle.SCULK_CHARGE || particle == Particle.SCULK_CHARGE_POP) {
            double r = config.getDouble(new String[]{"rotation", "rot", "r"}, 0.0);
            double rotation;
            if (r > 0.0) {
               rotation = r / 180.0 * Math.PI;
            } else {
               rotation = r;
            }

            return data -> rotation;
         } else if (particle == Particle.SHRIEK) {
            return data -> 0;
         } else if (particle == Particle.DRAGON_BREATH) {
            return data -> 0.0F;
         } else {
            try {
               if (particle.getDataType().equals(Trail.class)) {
                  String location = config.getString(new String[]{"target", "t", "source", "src"}, "@target");
                  PlaceholderColor color = config.getPlaceholderColor(new String[]{"color", "c"}, "#FF0000");
                  PlaceholderInt duration = config.getPlaceholderInteger(new String[]{"duration", "d"}, 100);
                  SkillTargeter targeter = MythicBukkit.inst().getSkillManager().getTargeter(location);
                  if (targeter == null) {
                     return null;
                  }

                  return data -> {
                     if (data instanceof SkillMetadata skillMetadata) {
                        AbstractLocation loc = MythicBukkit.inst().getSkillManager().getLocationTarget(targeter, skillMetadata);
                        return new Trail(BukkitAdapter.adapt(loc), color.get(data).toBukkitColor(), duration.get(data));
                     } else {
                        return null;
                     }
                  };
               }
            } catch (Throwable ex4) {
            }

            return null;
         }
      }
   }

   private static VolatileParticleHandler handler() {
      return MythicBukkit.inst().getVolatileCodeHandler().getParticleHandler();
   }

   private static int bundleSize() {
      return MythicBukkit.inst().getConfiguration().getParticleBundleSize();
   }

   private static ParticleBundle openBundle(List<Player> targets) {
      VolatileParticleHandler h = handler();
      return MythicBukkit.inst().getConfiguration().isParticleBundleEnabled() ? h.openBundle(targets, bundleSize()) : ParticleBundle.direct(h, targets);
   }

   private static List<Player> filterByWorld(Collection<AbstractPlayer> audience, World world) {
      List<Player> out = new ArrayList<>(audience.size());

      for (AbstractPlayer e : audience) {
         Player player = BukkitAdapter.adapt(e.asPlayer());
         if (player.getWorld().equals(world)) {
            out.add(player);
         }
      }

      return out;
   }

   public void send(AbstractLocation location) {
      Particle particle = this.toBukkitParticle();
      if (particle != null) {
         Location loc = BukkitAdapter.adapt(location);
         List<Player> audience = new ArrayList<>(Bukkit.getOnlinePlayers().size());

         for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(loc.getWorld())) {
               audience.add(p);
            }
         }

         handler().sendParticle(audience, particle, null, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0F, 0.0F, 0.0F, 0.0F, true);
      }
   }

   public void send(AbstractPlayer audience, AbstractLocation location, float speed, int amount, float offsetX, float offsetY, float offsetZ) {
      Particle particle = this.toBukkitParticle();
      if (particle != null) {
         Location loc = BukkitAdapter.adapt(location);
         Player player = (Player)audience.getBukkitEntity();
         if (player.getWorld().equals(loc.getWorld())) {
            handler()
               .sendParticle(
                  Collections.singletonList(player), particle, null, loc.getX(), loc.getY(), loc.getZ(), amount, offsetX, offsetY, offsetZ, speed, true
               );
         }
      }
   }

   public void send(Collection<AbstractPlayer> audience, AbstractLocation location, float speed, int amount, float offsetX, float offsetY, float offsetZ) {
      Particle particle = this.toBukkitParticle();
      if (particle != null) {
         Location loc = BukkitAdapter.adapt(location);
         handler()
            .sendParticle(
               filterByWorld(audience, loc.getWorld()), particle, null, loc.getX(), loc.getY(), loc.getZ(), amount, offsetX, offsetY, offsetZ, speed, true
            );
      }
   }

   public void send(
      Collection<AbstractPlayer> audience, AbstractLocation location, float speed, int amount, float offsetX, float offsetY, float offsetZ, Object data
   ) {
      this.send(audience, location, speed, amount, offsetX, offsetY, offsetZ, data, false);
   }

   public void send(
      Collection<AbstractPlayer> audience,
      AbstractLocation location,
      float speed,
      int amount,
      float offsetX,
      float offsetY,
      float offsetZ,
      Object data,
      boolean exactOffsets
   ) {
      if (this.validateData(data)) {
         Particle particle = this.toBukkitParticle();
         if (particle != null) {
            Location loc = BukkitAdapter.adapt(location);
            List<Player> targets = filterByWorld(audience, loc.getWorld());
            if (!targets.isEmpty()) {
               if (exactOffsets) {
                  try (ParticleBundle bundle = openBundle(targets)) {
                     for (int i = 0; i < amount; i++) {
                        double jx = loc.getX() - offsetX + Numbers.randomDouble() * offsetX * 2.0;
                        double jy = loc.getY() + Numbers.randomDouble() * offsetY * 2.0;
                        double jz = loc.getZ() - offsetZ + Numbers.randomDouble() * offsetZ * 2.0;
                        bundle.add(particle, data, jx, jy, jz, 1, 0.0F, 0.0F, 0.0F, speed, true);
                     }
                  }
               } else {
                  handler().sendParticle(targets, particle, data, loc.getX(), loc.getY(), loc.getZ(), amount, offsetX, offsetY, offsetZ, speed, true);
               }
            }
         }
      }
   }

   public void sendDirectional(
      Collection<AbstractPlayer> audience,
      AbstractLocation location,
      float speed,
      int amount,
      float offsetX,
      float offsetY,
      float offsetZ,
      AbstractVector direction
   ) {
      Particle particle = this.toBukkitParticle();
      if (particle != null) {
         Location loc = BukkitAdapter.adapt(location);
         List<Player> targets = filterByWorld(audience, loc.getWorld());
         if (!targets.isEmpty()) {
            float dx = (float)direction.getX();
            float dy = (float)direction.getY();
            float dz = (float)direction.getZ();

            try (ParticleBundle bundle = openBundle(targets)) {
               for (int i = 0; i < amount; i++) {
                  double jx = loc.getX() - offsetX + Numbers.randomDouble() * offsetX * 2.0;
                  double jy = loc.getY() + Numbers.randomDouble() * offsetY * 2.0;
                  double jz = loc.getZ() - offsetZ + Numbers.randomDouble() * offsetZ * 2.0;
                  bundle.add(particle, null, jx, jy, jz, 0, dx, dy, dz, speed, true);
               }
            }
         }
      }
   }

   public void sendLegacyColored(
      Collection<AbstractPlayer> audience, AbstractLocation location, float speed, int amount, float offsetX, float offsetY, float offsetZ, Chroma color
   ) {
      Particle particle = this.toBukkitParticle();
      if (particle != null) {
         Location loc = BukkitAdapter.adapt(location);
         List<Player> targets = filterByWorld(audience, loc.getWorld());
         if (!targets.isEmpty()) {
            float r = color.red() / 255.0F;
            float g = color.green() / 255.0F;
            float b = color.blue() / 255.0F;
            if (r < Float.MIN_NORMAL) {
               r = Float.MIN_NORMAL;
            }

            float extra = speed > 0.0F ? speed : 1.0F;

            try (ParticleBundle bundle = openBundle(targets)) {
               for (int i = 0; i < amount; i++) {
                  double jx = loc.getX() - offsetX + Numbers.randomDouble() * offsetX * 2.0;
                  double jy = loc.getY() + Numbers.randomDouble() * offsetY * 2.0;
                  double jz = loc.getZ() - offsetZ + Numbers.randomDouble() * offsetZ * 2.0;
                  bundle.add(particle, null, jx, jy, jz, 0, r, g, b, extra, true);
               }
            }
         }
      }
   }

   static {
      for (BukkitParticle particle : values()) {
         PARTICLE_ALIASES.register(particle);
      }

      registerUnmapped();
   }

   private static class ParticleHandler extends HashMap<String, BukkitParticle> {
      public void register(BukkitParticle particle) {
         this.insertAllPermutations(particle.toString(), particle);

         for (String alias : particle.getAliases()) {
            this.insertAllPermutations(alias, particle);
         }
      }

      private void insertAllPermutations(String name, BukkitParticle particle) {
         this.insert(name.toUpperCase(), particle);
         this.insert(name.toUpperCase().replace("_", ""), particle);
      }

      private void insert(String name, BukkitParticle particle) {
         if (BukkitParticle.PARTICLE_ALIASES.containsKey(name)) {
            if (particle != BukkitParticle.PARTICLE_ALIASES.get(name)) {
               MythicLogger.error(
                  "Particle alias "
                     + name
                     + " from particle type "
                     + particle.name()
                     + " is already registered under particle "
                     + BukkitParticle.PARTICLE_ALIASES.get(name).name()
               );
            }
         } else {
            this.put(name, particle);
         }
      }
   }
}
