package net.Zrips.CMILib.Effects;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Reflections;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Particle.DustTransition;
import org.bukkit.Particle.Spell;
import org.bukkit.Particle.Trail;
import org.bukkit.Vibration;
import org.bukkit.entity.Player;

public class CMIEffectManager {
  private CMILib plugin;
  private static Class<?> PacketPlayOutWorldParticles;
  private static Class<?> EnumParticle;
  private static Class<?> CraftParticle;
  private static Class<?> ParticleParam;
  private static Constructor<?> effectConstructor = null;
  private static Method CraftParticleMethod = null;
  private static Constructor<?> vibrationConstructor = null;
  private static Constructor<?> destinationConstructor = null;

  public CMIEffectManager(CMILib plugin) {
    this.plugin = plugin;
  }

  public static Object getParticleParameter(@Nullable Location location, @Nonnull CMIEffect ef) {
    Object dd = null;

    try {
      switch (ef.getParticle().getDataType()) {
        case Void:
        case MaterialData:
        case EntityData:
        default:
          break;
        case DustOptions:
          CMIParticleDustOptions dustOptions = (CMIParticleDustOptions) ef.getOptions();
          dd = new DustOptions(dustOptions.getColor(), dustOptions.getSize());
          break;
        case ItemStack:
          CMIParticleItemStack itemData = (CMIParticleItemStack) ef.getOptions();
          dd = itemData.getMaterial().newItemStack();
          break;
        case BlockData:
          CMIParticleBlockData blockData = (CMIParticleBlockData) ef.getOptions();
          dd = Bukkit.createBlockData(blockData.getMaterial().getMaterial());
          break;
        case Color:
          CMIParticleColor color = (CMIParticleColor) ef.getOptions();
          dd = color.getColor();
          break;
        case DustTransition:
          CMIParticleDustTransition dustTransitionOptions =
              (CMIParticleDustTransition) ef.getOptions();
          dd =
              new DustTransition(
                  dustTransitionOptions.getColor(),
                  dustTransitionOptions.getColorTo(),
                  dustTransitionOptions.getSize());
          break;
        case Vibration:
          if (destinationConstructor == null) {
            destinationConstructor =
                Class.forName("org.bukkit.Vibration$Destination$BlockDestination")
                    .getConstructor(Location.class);
          }

          if (vibrationConstructor == null) {
            if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
              vibrationConstructor =
                  Vibration.class.getConstructor(
                      Location.class, Class.forName("org.bukkit.Vibration$Destination"), int.class);
            } else {
              vibrationConstructor =
                  Vibration.class.getConstructor(
                      Class.forName("org.bukkit.Vibration$Destination$BlockDestination"),
                      int.class);
            }
          }

          CMIParticleVibration vibration = (CMIParticleVibration) ef.getOptions();
          if (location != null) {
            Location d = location.clone();
            d.add(vibration.getDestination().toVector());
            if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
              Location f = location.clone();
              f.add(vibration.getOffset().toVector());
              dd =
                  vibrationConstructor.newInstance(
                      f, destinationConstructor.newInstance(d), vibration.getArrivalTime());
            } else {
              dd =
                  vibrationConstructor.newInstance(
                      destinationConstructor.newInstance(d), vibration.getArrivalTime());
            }
          }
          break;
        case Float:
          CMIParticleFloat floatData = (CMIParticleFloat) ef.getOptions();
          dd = floatData.getValue();
          break;
        case Int:
          CMIParticleInteger intData = (CMIParticleInteger) ef.getOptions();
          dd = intData.getValue();
          break;
        case Trail:
          CMIParticleTrail trail = (CMIParticleTrail) ef.getOptions();
          if (trail.getOffset() != null && location != null) {
            Location l = location.clone();
            l.add(trail.getOffset().toVector());
            dd = new Trail(l, trail.getColor(), trail.getDuration());
          }
          break;
        case Spell:
          CMIParticleSpell spell = (CMIParticleSpell) ef.getOptions();
          dd = new Spell(spell.getColor(), spell.getPower());
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return dd;
  }

  @Nullable
  public static Object getParticleOptions(@Nullable Location location, @Nonnull CMIEffect ef) {
    Particle particle = ef.getParticle().getParticle();
    if (particle == null) {
      return null;
    }

    try {
      Object dd = getParticleParameter(location, ef);
      if (CraftParticleMethod == null) {
        try {
          CraftParticleMethod = CraftParticle.getMethod("toNMS", Particle.class, Object.class);
        } catch (Throwable e) {
          CraftParticleMethod =
              CraftParticle.getMethod("createParticleParam", Particle.class, Object.class);
        }
      }

      return CraftParticleMethod.invoke(null, particle, dd);
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  private static void playFor1_17Up(Object playerConnection, Location location, CMIEffect ef) {
    Object particleParam = ef.getParticleOptions(location);
    if (particleParam != null) {
      try {
        Object packet = null;
        if (Version.isMojangMappings()) {
          if (effectConstructor == null) {
            effectConstructor =
                PacketPlayOutWorldParticles.getConstructor(
                    ParticleParam,
                    boolean.class,
                    boolean.class,
                    double.class,
                    double.class,
                    double.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    int.class);
          }

          packet =
              effectConstructor.newInstance(
                  particleParam,
                  true,
                  true,
                  location.getX(),
                  location.getY(),
                  location.getZ(),
                  (float) ef.getOffset().getX(),
                  (float) ef.getOffset().getY(),
                  (float) ef.getOffset().getZ(),
                  ef.getSpeed(),
                  ef.getAmount());
        } else if (Version.isCurrentEqualOrLower(Version.v1_21_R2)) {
          if (effectConstructor == null) {
            Class<?> packetClass =
                Class.forName("net.minecraft.network.protocol.game.PacketPlayOutWorldParticles");
            Class<?> particleParamClass =
                Class.forName("net.minecraft.core.particles.ParticleParam");
            effectConstructor =
                packetClass.getConstructor(
                    particleParamClass,
                    boolean.class,
                    double.class,
                    double.class,
                    double.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    int.class);
          }

          packet =
              effectConstructor.newInstance(
                  particleParam,
                  true,
                  location.getX(),
                  location.getY(),
                  location.getZ(),
                  (float) ef.getOffset().getX(),
                  (float) ef.getOffset().getY(),
                  (float) ef.getOffset().getZ(),
                  ef.getSpeed(),
                  ef.getAmount());
        } else {
          if (effectConstructor == null) {
            Class<?> packetClass =
                Class.forName("net.minecraft.network.protocol.game.PacketPlayOutWorldParticles");
            Class<?> particleParamClass =
                Class.forName("net.minecraft.core.particles.ParticleParam");
            effectConstructor =
                packetClass.getConstructor(
                    particleParamClass,
                    boolean.class,
                    boolean.class,
                    double.class,
                    double.class,
                    double.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    int.class);
          }

          packet =
              effectConstructor.newInstance(
                  particleParam,
                  true,
                  false,
                  location.getX(),
                  location.getY(),
                  location.getZ(),
                  (float) ef.getOffset().getX(),
                  (float) ef.getOffset().getY(),
                  (float) ef.getOffset().getZ(),
                  ef.getSpeed(),
                  ef.getAmount());
        }

        CMILib.getInstance().getReflectionManager().sendPacket(playerConnection, packet);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  public static void playEffect(Player player, Location location, CMIEffect ef) {
    if (location != null && location.getWorld() != null && player != null && player.isOnline()) {
      if (location.getWorld().equals(player.getWorld())) {
        playEffect(
            CMILib.getInstance().getReflectionManager().getPlayerConnection(player), location, ef);
      }
    }
  }

  public static void playEffect(Object playerConnection, Location location, CMIEffect ef) {
    if (location != null && location.getWorld() != null && playerConnection != null) {
      if (ef != null && ef.getParticle() != null) {
        if (ef.getParticle().isParticle()) {
          try {
            if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
              playFor1_17Up(playerConnection, location, ef);
            } else if (Version.isCurrentEqualOrHigher(Version.v1_14_R2)) {
              playFor1_14Up(playerConnection, location, ef);
            } else if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
              playFor1_13Up(playerConnection, location, ef);
            } else if (Version.isCurrentEqualOrHigher(Version.v1_8_R1)) {
              playFor1_8Up(playerConnection, location, ef);
            } else {
              playForAncient(playerConnection, location, ef);
            }
          } catch (Throwable e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  private static void playForAncient(Object playerConnection, Location location, CMIEffect ef) {
    Effect effect = ef.getParticle().getEffect();
    if (effect != null) {
      try {
        if (effectConstructor == null) {
          effectConstructor =
              PacketPlayOutWorldParticles.getConstructor(
                  String.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  int.class);
        }

        Object newPack =
            effectConstructor.newInstance(
                effect.name(),
                (float) location.getX(),
                (float) location.getY(),
                (float) location.getZ(),
                (float) ef.getOffset().getX(),
                (float) ef.getOffset().getY(),
                (float) ef.getOffset().getZ(),
                ef.getSpeed(),
                ef.getAmount());
        CMILib.getInstance().getReflectionManager().sendPacket(playerConnection, newPack);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  private static void playFor1_8Up(Object playerConnection, Location location, CMIEffect ef) {
    Effect effect = ef.getParticle().getEffect();
    if (effect != null) {
      try {
        Object particle =
            ef.getParticle().getEnumParticle() == null
                ? null
                : EnumParticle.cast(ef.getParticle().getEnumParticle());
        int[] extra = ef.getParticle().getExtra();
        if (particle == null) {
          for (Object p : EnumParticle.getEnumConstants()) {
            String name = p.toString().replace("_", "");
            if (ef.getParticle().is(name)) {
              particle = p;
              if (ef.getParticle().getEffect().getData() != null) {
                extra = new int[] {0};
              }
              break;
            }
          }

          if (extra == null) {
            extra = new int[0];
          }
        }

        if (particle == null) {
          return;
        }

        if (ef.getParticle().getEnumParticle() == null) {
          ef.getParticle().setEnumParticle(particle);
          ef.getParticle().setExtra(extra);
        }

        if (effectConstructor == null) {
          effectConstructor =
              PacketPlayOutWorldParticles.getConstructor(
                  EnumParticle,
                  boolean.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  int.class,
                  int[].class);
        }

        Object newPack = null;
        if (ef.getParticle().isColored()) {
          newPack =
              effectConstructor.newInstance(
                  particle,
                  true,
                  (float) location.getX(),
                  (float) location.getY(),
                  (float) location.getZ(),
                  ef.getColorFrom().getRed() / 255.0F,
                  ef.getColorFrom().getGreen() / 255.0F,
                  ef.getColorFrom().getBlue() / 255.0F,
                  1,
                  0,
                  extra);
        } else {
          newPack =
              effectConstructor.newInstance(
                  particle,
                  true,
                  (float) location.getX(),
                  (float) location.getY(),
                  (float) location.getZ(),
                  (float) ef.getOffset().getX(),
                  (float) ef.getOffset().getY(),
                  (float) ef.getOffset().getZ(),
                  ef.getSpeed(),
                  ef.getAmount(),
                  extra);
        }

        CMILib.getInstance().getReflectionManager().sendPacket(playerConnection, newPack);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  private static void playFor1_13Up(Object playerConnection, Location location, CMIEffect ef) {
    Particle particle = ef.getParticle().getParticle();
    if (particle != null) {
      try {
        DustOptions dd = null;
        if (particle.toString().equals("REDSTONE")) {
          dd = new DustOptions(ef.getColor(), ef.getSize());
        }

        if (CraftParticleMethod == null) {
          CraftParticleMethod = CraftParticle.getMethod("toNMS", Particle.class, Object.class);
        }

        if (effectConstructor == null) {
          effectConstructor =
              PacketPlayOutWorldParticles.getConstructor(
                  ParticleParam,
                  boolean.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  int.class);
        }

        Object param = CraftParticleMethod.invoke(null, particle, dd);
        Object packet =
            effectConstructor.newInstance(
                param,
                true,
                (float) location.getX(),
                (float) location.getY(),
                (float) location.getZ(),
                (float) ef.getOffset().getX(),
                (float) ef.getOffset().getY(),
                (float) ef.getOffset().getZ(),
                ef.getSpeed(),
                ef.getAmount());
        CMILib.getInstance().getReflectionManager().sendPacket(playerConnection, packet);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  private static void playFor1_14Up(Object playerConnection, Location location, CMIEffect ef) {
    Particle particle = ef.getParticle().getParticle();
    if (particle != null) {
      try {
        DustOptions dd = null;
        if (particle.toString().equals("REDSTONE")) {
          dd = new DustOptions(ef.getColorFrom(), ef.getSize());
        }

        if (CraftParticleMethod == null) {
          CraftParticleMethod = CraftParticle.getMethod("toNMS", Particle.class, Object.class);
        }

        if (effectConstructor == null) {
          effectConstructor =
              PacketPlayOutWorldParticles.getConstructor(
                  ParticleParam,
                  boolean.class,
                  double.class,
                  double.class,
                  double.class,
                  float.class,
                  float.class,
                  float.class,
                  float.class,
                  int.class);
        }

        Object packet =
            effectConstructor.newInstance(
                CraftParticleMethod.invoke(null, particle, dd),
                true,
                location.getX(),
                location.getY(),
                location.getZ(),
                (float) ef.getOffset().getX(),
                (float) ef.getOffset().getY(),
                (float) ef.getOffset().getZ(),
                ef.getSpeed(),
                ef.getAmount());
        CMILib.getInstance().getReflectionManager().sendPacket(playerConnection, packet);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  static {
    if (Version.isMojangMappings()) {
      try {
        PacketPlayOutWorldParticles =
            Class.forName("net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket");
        ParticleParam = Class.forName("net.minecraft.core.particles.ParticleOptions");
        CraftParticle = Class.forName("org.bukkit.craftbukkit.CraftParticle");
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
      try {
        PacketPlayOutWorldParticles =
            Class.forName("net.minecraft.network.protocol.game.PacketPlayOutWorldParticles");
        ParticleParam = Class.forName("net.minecraft.core.particles.ParticleParam");
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }

      CraftParticle = Reflections.getBukkitClass("CraftParticle");
    } else {
      try {
        PacketPlayOutWorldParticles =
            CMILib.getInstance()
                .getReflectionManager()
                .getMinecraftClass("PacketPlayOutWorldParticles");
      } catch (Throwable e) {
        e.printStackTrace();
      }

      try {
        if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
          EnumParticle =
              CMILib.getInstance().getReflectionManager().getMinecraftClass("EnumParticle");
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }

      if (Version.isCurrentHigher(Version.v1_12_R1)) {
        try {
          CraftParticle = Reflections.getBukkitClass("CraftParticle");
        } catch (Throwable var2) {
        }

        try {
          ParticleParam =
              CMILib.getInstance().getReflectionManager().getMinecraftClass("ParticleParam");
        } catch (Throwable var1) {
        }
      }
    }
  }

  public enum CMIParticle {
    CLICK2(CMIEffectManager.CMIParticleType.SOUND),
    CLICK1(CMIEffectManager.CMIParticleType.SOUND),
    BOW_FIRE(CMIEffectManager.CMIParticleType.SOUND),
    DOOR_TOGGLE(CMIEffectManager.CMIParticleType.SOUND),
    IRON_DOOR_TOGGLE(CMIEffectManager.CMIParticleType.SOUND),
    TRAPDOOR_TOGGLE(CMIEffectManager.CMIParticleType.SOUND),
    IRON_TRAPDOOR_TOGGLE(CMIEffectManager.CMIParticleType.SOUND),
    FENCE_GATE_TOGGLE(CMIEffectManager.CMIParticleType.SOUND),
    DOOR_CLOSE(CMIEffectManager.CMIParticleType.SOUND),
    IRON_DOOR_CLOSE(CMIEffectManager.CMIParticleType.SOUND),
    TRAPDOOR_CLOSE(CMIEffectManager.CMIParticleType.SOUND),
    IRON_TRAPDOOR_CLOSE(CMIEffectManager.CMIParticleType.SOUND),
    FENCE_GATE_CLOSE(CMIEffectManager.CMIParticleType.SOUND),
    EXTINGUISH(CMIEffectManager.CMIParticleType.SOUND),
    RECORD_PLAY(CMIEffectManager.CMIParticleType.SOUND),
    GHAST_SHRIEK(CMIEffectManager.CMIParticleType.SOUND),
    GHAST_SHOOT(CMIEffectManager.CMIParticleType.SOUND),
    BLAZE_SHOOT(CMIEffectManager.CMIParticleType.SOUND),
    ZOMBIE_CHEW_WOODEN_DOOR(CMIEffectManager.CMIParticleType.SOUND),
    ZOMBIE_CHEW_IRON_DOOR(CMIEffectManager.CMIParticleType.SOUND),
    ZOMBIE_DESTROY_DOOR(CMIEffectManager.CMIParticleType.SOUND),
    STEP_SOUND(CMIEffectManager.CMIParticleType.SOUND),
    BREWING_STAND_BREW(CMIEffectManager.CMIParticleType.SOUND),
    CHORUS_FLOWER_GROW(CMIEffectManager.CMIParticleType.SOUND),
    CHORUS_FLOWER_DEATH(CMIEffectManager.CMIParticleType.SOUND),
    PORTAL_TRAVEL(CMIEffectManager.CMIParticleType.SOUND),
    ENDEREYE_LAUNCH(CMIEffectManager.CMIParticleType.SOUND),
    FIREWORK_SHOOT(CMIEffectManager.CMIParticleType.SOUND),
    ANVIL_BREAK(CMIEffectManager.CMIParticleType.SOUND),
    ANVIL_USE(CMIEffectManager.CMIParticleType.SOUND),
    ANVIL_LAND(CMIEffectManager.CMIParticleType.SOUND),
    ENDERDRAGON_SHOOT(CMIEffectManager.CMIParticleType.SOUND),
    WITHER_BREAK_BLOCK(CMIEffectManager.CMIParticleType.SOUND),
    WITHER_SHOOT(CMIEffectManager.CMIParticleType.SOUND),
    ZOMBIE_INFECT(CMIEffectManager.CMIParticleType.SOUND),
    ZOMBIE_CONVERTED_VILLAGER(CMIEffectManager.CMIParticleType.SOUND),
    BAT_TAKEOFF(CMIEffectManager.CMIParticleType.SOUND),
    ENDERDRAGON_GROWL(CMIEffectManager.CMIParticleType.SOUND),
    FIREWORKS_SPARK(CMIMaterial.FIRE_CHARGE),
    VILLAGER_PLANT_GROW(CMIEffectManager.CMIParticleType.VISUAL),
    POTION_BREAK(CMIEffectManager.CMIParticleType.VISUAL),
    ENDER_SIGNAL(CMIEffectManager.CMIParticleType.VISUAL),
    MOBSPAWNER_FLAMES(CMIEffectManager.CMIParticleType.VISUAL),
    END_GATEWAY_SPAWN(CMIEffectManager.CMIParticleType.VISUAL),
    SMOKE(CMIMaterial.COBBLESTONE),
    DRAGON_BREATH(CMIEffectManager.CMIParticleDataType.Float, CMIMaterial.ACACIA_PRESSURE_PLATE),
    CRIT(CMIMaterial.IRON_SWORD),
    MAGIC_CRIT("CRIT_MAGIC", CMIMaterial.POTION),
    POTION_SWIRL("MOB_SPELL", "SPELL_MOB", CMIMaterial.BLAZE_ROD),
    POTION_SWIRL_TRANSPARENT("MOB_SPELL_AMBIENT", "SPELL_MOB_AMBIENT", CMIMaterial.BLAZE_POWDER),
    SPELL(CMIMaterial.MILK_BUCKET),
    INSTANT_SPELL("SPELL_INSTANT", CMIMaterial.GLASS_BOTTLE),
    WITCH_MAGIC("SPELL_WITCH", CMIMaterial.SPIDER_EYE),
    NOTE(CMIMaterial.NOTE_BLOCK),
    PORTAL(CMIMaterial.OBSIDIAN),
    @Deprecated
    FLYING_GLYPH("ENCHANTMENT_TABLE", "ENCHANT", CMIMaterial.ENCHANTING_TABLE),
    FLAME(CMIMaterial.FIRE_CHARGE),
    LAVA_POP("LAVA", CMIMaterial.FLINT_AND_STEEL),
    FOOTSTEP(CMIMaterial.IRON_BOOTS),
    SPLASH("WATER_SPLASH", CMIMaterial.STICK),
    PARTICLE_SMOKE("SMOKE", "SMOKE_NORMAL", CMIMaterial.ANVIL),
    EXPLOSION_HUGE("HUGE_EXPLOSION", CMIMaterial.FURNACE),
    EXPLOSION_LARGE("LARGE_EXPLODE", CMIMaterial.FURNACE),
    EXPLOSION("EXPLODE", "EXPLOSION_NORMAL", CMIMaterial.TNT),
    VOID_FOG("DEPTH_SUSPEND", "SUSPENDED_DEPTH", CMIMaterial.SALMON),
    SMALL_SMOKE("TOWN_AURA", CMIMaterial.MYCELIUM),
    CLOUD(CMIMaterial.COBWEB),
    @Deprecated
    COLOURED_DUST(
        Arrays.asList("RED_DUST", "REDSTONE"),
        CMIMaterial.REDSTONE,
        CMIEffectManager.CMIParticleDataType.DustOptions),
    DUST(
        Arrays.asList("RED_DUST", "REDSTONE", "COLOURED_DUST"),
        CMIMaterial.REDSTONE,
        CMIEffectManager.CMIParticleDataType.DustOptions),
    SNOWBALL_BREAK("SNOW_BALL_POOF", "SNOW_BALL", CMIMaterial.SNOWBALL),
    WATERDRIP("DRIP_WATER", "WATER_DROP", CMIMaterial.WATER_BUCKET),
    LAVADRIP("DRIP_LAVA", CMIMaterial.LAVA_BUCKET),
    SNOW_SHOVEL(CMIMaterial.DIAMOND_SHOVEL),
    SLIME(CMIMaterial.SLIME_BALL),
    HEART(CMIMaterial.ROSE_BUSH),
    VILLAGER_THUNDERCLOUD(
        "ANGRY_VILLAGER",
        "VILLAGER_ANGRY",
        CMIEffectManager.CMIParticleType.PARTICLE,
        CMIMaterial.EMERALD),
    HAPPY_VILLAGER("VILLAGER_HAPPY", CMIEffectManager.CMIParticleType.PARTICLE, CMIMaterial.BOOK),
    LARGE_SMOKE("SMOKE_LARGE", CMIEffectManager.CMIParticleType.PARTICLE, CMIMaterial.FURNACE),
    ITEM_BREAK("ICON_CRACK", CMIEffectManager.CMIParticleType.NONE, CMIMaterial.DIAMOND_BOOTS),
    WATER_BUBBLE(CMIMaterial.PRISMARINE_CRYSTALS),
    WATER_WAKE(CMIMaterial.SEAGRASS),
    SUSPENDED(CMIMaterial.KELP),
    BARRIER(CMIMaterial.BARRIER),
    MOB_APPEARANCE(CMIMaterial.ZOMBIE_HEAD),
    END_ROD(CMIMaterial.END_ROD),
    DAMAGE_INDICATOR(CMIMaterial.RED_DYE),
    SWEEP_ATTACK(CMIMaterial.IRON_SWORD),
    TOTEM(CMIMaterial.TOTEM_OF_UNDYING),
    SPIT(CMIMaterial.LLAMA_SPAWN_EGG),
    SQUID_INK(CMIMaterial.INK_SAC),
    BUBBLE_POP(CMIMaterial.PUFFERFISH),
    CURRENT_DOWN(CMIMaterial.HEART_OF_THE_SEA),
    BUBBLE_COLUMN_UP(CMIMaterial.CONDUIT),
    NAUTILUS(CMIMaterial.NAUTILUS_SHELL),
    DOLPHIN(CMIMaterial.DOLPHIN_SPAWN_EGG),
    WATER_SPLASH(CMIMaterial.SPLASH_POTION),
    CAMPFIRE_SIGNAL_SMOKE(CMIMaterial.SOUL_CAMPFIRE),
    CAMPFIRE_COSY_SMOKE(CMIMaterial.CAMPFIRE),
    SNEEZE(CMIMaterial.SLIME_BALL),
    COMPOSTER(CMIMaterial.BONE_MEAL),
    FLASH(CMIEffectManager.CMIParticleDataType.Color, CMIMaterial.GLOWSTONE),
    FALLING_LAVA(CMIMaterial.MAGMA_BLOCK),
    LANDING_LAVA(CMIMaterial.BLAZE_POWDER),
    FALLING_WATER(CMIMaterial.WATER_BUCKET),
    DRIPPING_HONEY(CMIMaterial.HONEYCOMB),
    FALLING_HONEY(CMIMaterial.HONEY_BLOCK),
    LANDING_HONEY(CMIMaterial.HONEY_BOTTLE),
    FALLING_NECTAR(CMIMaterial.BEEHIVE),
    SOUL_FIRE_FLAME(CMIMaterial.SOUL_TORCH),
    ASH(CMIMaterial.BLACK_DYE),
    CRIMSON_SPORE(CMIMaterial.CRIMSON_FUNGUS),
    WARPED_SPORE(CMIMaterial.WARPED_FUNGUS),
    SOUL(CMIMaterial.SOUL_SOIL),
    DRIPPING_OBSIDIAN_TEAR(CMIMaterial.CRYING_OBSIDIAN),
    FALLING_OBSIDIAN_TEAR(CMIMaterial.ANCIENT_DEBRIS),
    LANDING_OBSIDIAN_TEAR(CMIMaterial.RESPAWN_ANCHOR),
    REVERSE_PORTAL(CMIMaterial.OBSIDIAN),
    WHITE_ASH(CMIMaterial.QUARTZ),
    LIGHT(CMIMaterial.LIGHT),
    FALLING_SPORE_BLOSSOM(CMIMaterial.SPORE_BLOSSOM),
    SPORE_BLOSSOM_AIR(CMIMaterial.FLOWERING_AZALEA_LEAVES),
    SMALL_FLAME(CMIMaterial.FLINT_AND_STEEL),
    SNOWFLAKE(CMIMaterial.SNOWBALL),
    DRIPPING_DRIPSTONE_LAVA(CMIMaterial.DRIPSTONE_BLOCK),
    FALLING_DRIPSTONE_LAVA(CMIMaterial.LAVA_BUCKET),
    DRIPPING_DRIPSTONE_WATER(CMIMaterial.BONE_BLOCK),
    FALLING_DRIPSTONE_WATER(CMIMaterial.POINTED_DRIPSTONE),
    GLOW_SQUID_INK(CMIMaterial.GLOW_INK_SAC),
    GLOW(CMIMaterial.GLOW_BERRIES),
    WAX_ON(CMIMaterial.HONEYCOMB_BLOCK),
    WAX_OFF(CMIMaterial.COPPER_BLOCK),
    ELECTRIC_SPARK(CMIMaterial.AMETHYST_SHARD),
    SCRAPE(CMIMaterial.COPPER_INGOT),
    BLOCK_MARKER(CMIEffectManager.CMIParticleDataType.BlockData, CMIMaterial.REINFORCED_DEEPSLATE),
    SONIC_BOOM(CMIMaterial.SCULK_SHRIEKER),
    SCULK_SOUL(CMIMaterial.SCULK_SENSOR),
    SCULK_CHARGE_POP(CMIMaterial.SCULK_CATALYST),
    CHERRY_LEAVES(CMIMaterial.CHERRY_LEAVES),
    SMALL_GUST(CMIMaterial.FEATHER),
    TRIAL_SPAWNER_DETECTION_OMINOUS(CMIMaterial.OAK_TRAPDOOR),
    VAULT_CONNECTION(CMIMaterial.ENDER_CHEST),
    INFESTED(CMIMaterial.STONE_AXE),
    ITEM_COBWEB(CMIMaterial.COBWEB),
    OMINOUS_SPAWNING(CMIMaterial.WITHER_SKELETON_SKULL),
    RAID_OMEN(CMIMaterial.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE),
    TRIAL_OMEN(CMIMaterial.BREEZE_SPAWN_EGG),
    POOF(CMIMaterial.FIREWORK_STAR),
    EXPLOSION_EMITTER(CMIMaterial.TNT),
    FIREWORK(CMIMaterial.FIREWORK_ROCKET),
    BUBBLE(CMIMaterial.BUBBLE_CORAL),
    FISHING(CMIMaterial.FISHING_ROD),
    UNDERWATER(CMIMaterial.TURTLE_EGG),
    ENCHANTED_HIT(CMIMaterial.ENCHANTED_BOOK),
    EFFECT(CMIEffectManager.CMIParticleDataType.Spell, CMIMaterial.POTION),
    INSTANT_EFFECT(CMIEffectManager.CMIParticleDataType.Spell, CMIMaterial.GOLDEN_APPLE),
    ENTITY_EFFECT(CMIEffectManager.CMIParticleDataType.Color, CMIMaterial.GLOWSTONE_DUST),
    WITCH(CMIMaterial.POTION),
    DRIPPING_WATER(CMIMaterial.ICE),
    DRIPPING_LAVA(CMIMaterial.BASALT),
    MYCELIUM(CMIMaterial.MYCELIUM),
    ENCHANT("FLYING_GLYPH", "ENCHANTMENT_TABLE", CMIMaterial.ENCHANTED_BOOK),
    ITEM_SNOWBALL(CMIMaterial.SNOWBALL),
    ITEM_SLIME(CMIMaterial.SLIME_BALL),
    ITEM(CMIEffectManager.CMIParticleDataType.ItemStack, CMIMaterial.IRON_CHAIN),
    BLOCK(CMIEffectManager.CMIParticleDataType.BlockData, CMIMaterial.TNT),
    RAIN(CMIMaterial.BLUE_STAINED_GLASS),
    ELDER_GUARDIAN(CMIMaterial.PRISMARINE_SHARD),
    FALLING_DUST(CMIEffectManager.CMIParticleDataType.BlockData, CMIMaterial.BLUE_WOOL),
    TOTEM_OF_UNDYING(CMIMaterial.TOTEM_OF_UNDYING),
    DUST_COLOR_TRANSITION(
        CMIEffectManager.CMIParticleDataType.DustTransition, CMIMaterial.GREEN_BANNER),
    VIBRATION(CMIEffectManager.CMIParticleDataType.Vibration, CMIMaterial.YELLOW_BED),
    SCULK_CHARGE(CMIEffectManager.CMIParticleDataType.Float, CMIMaterial.WIND_CHARGE),
    SHRIEK(CMIEffectManager.CMIParticleDataType.Int, CMIMaterial.SCULK_SHRIEKER),
    EGG_CRACK(CMIMaterial.TURTLE_EGG),
    DUST_PLUME(CMIMaterial.BREEZE_ROD),
    WHITE_SMOKE(CMIMaterial.HEAVY_CORE),
    GUST(CMIMaterial.OMINOUS_BOTTLE),
    GUST_EMITTER_LARGE(CMIMaterial.POLISHED_TUFF_STAIRS),
    GUST_EMITTER_SMALL(CMIMaterial.POLISHED_TUFF_WALL),
    TRIAL_SPAWNER_DETECTION(CMIMaterial.TRIAL_SPAWNER),
    DUST_PILLAR(CMIEffectManager.CMIParticleDataType.BlockData, CMIMaterial.RESIN_CLUMP),
    PALE_OAK_LEAVES(CMIMaterial.PALE_OAK_LEAVES),
    TINTED_LEAVES(CMIEffectManager.CMIParticleDataType.Color, CMIMaterial.LEAF_LITTER),
    BLOCK_CRUMBLE(CMIEffectManager.CMIParticleDataType.BlockData, CMIMaterial.BROWN_EGG),
    TRAIL(CMIEffectManager.CMIParticleDataType.Trail, CMIMaterial.BUSH),
    FIREFLY(CMIMaterial.FIREFLY_BUSH);

    static HashMap<String, CMIEffectManager.CMIParticle> byName = new HashMap<>();
    static HashMap<Object, CMIEffectManager.CMIParticle> byType = new HashMap<>();
    private String name;
    private List<String> secondaryNames = new ArrayList<>();
    private CMIEffectManager.CMIParticleType type;
    private CMIMaterial icon;
    private Object particle;
    private Effect effect;
    @Deprecated private Object EnumParticle;
    @Deprecated private int[] extra;
    private CMIEffectManager.CMIParticleDataType dataType =
        CMIEffectManager.CMIParticleDataType.Void;
    static List<CMIEffectManager.CMIParticle> ls;

    CMIParticle() {
      this(new ArrayList<>(), null, null, null);
    }

    CMIParticle(CMIMaterial material) {
      this(
          new ArrayList<>(),
          null,
          material.getMaterial() == null ? CMIMaterial.STONE : material,
          null);
    }

    CMIParticle(CMIEffectManager.CMIParticleType type) {
      this(new ArrayList<>(), type, null, null);
    }

    CMIParticle(CMIEffectManager.CMIParticleType type, CMIMaterial icon) {
      this(new ArrayList<>(), type, icon, CMIEffectManager.CMIParticleDataType.Void);
    }

    CMIParticle(CMIEffectManager.CMIParticleDataType dataType, CMIMaterial icon) {
      this(new ArrayList<>(), null, icon, dataType);
    }

    CMIParticle(CMIEffectManager.CMIParticleDataType dataType) {
      this(new ArrayList<>(), null, null, dataType);
    }

    CMIParticle(
        CMIEffectManager.CMIParticleType type,
        CMIMaterial icon,
        CMIEffectManager.CMIParticleDataType dataType) {
      this(new ArrayList<>(), type, icon, dataType);
    }

    CMIParticle(String secondaryName, String name) {
      this(Arrays.asList(secondaryName, name), null, null, null);
    }

    CMIParticle(String secondaryName) {
      this(Arrays.asList(secondaryName), null, null, null);
    }

    CMIParticle(String secondaryName, CMIMaterial icon) {
      this(Arrays.asList(secondaryName), null, icon, null);
    }

    CMIParticle(String secondaryName, CMIEffectManager.CMIParticleType type, CMIMaterial icon) {
      this(Arrays.asList(secondaryName), type, icon, null);
    }

    CMIParticle(
        String name,
        String secondaryName,
        CMIEffectManager.CMIParticleType type,
        CMIMaterial icon) {
      this(
          Arrays.asList(name, secondaryName),
          type,
          icon,
          CMIEffectManager.CMIParticleDataType.Void);
    }

    CMIParticle(String name, String secondaryName, CMIMaterial icon) {
      this(
          Arrays.asList(name, secondaryName),
          null,
          icon,
          CMIEffectManager.CMIParticleDataType.Void);
    }

    CMIParticle(
        List<String> secondaryNames,
        CMIMaterial icon,
        CMIEffectManager.CMIParticleDataType dataType) {
      this(secondaryNames, null, icon, dataType);
    }

    CMIParticle(
        List<String> secondaryNames,
        CMIEffectManager.CMIParticleType type,
        CMIMaterial icon,
        CMIEffectManager.CMIParticleDataType dataType) {
      this.name = this.name == null ? CMIText.everyFirstToUpperCase(this.toString()) : this.name;
      this.secondaryNames = secondaryNames;
      this.type = type == null ? CMIEffectManager.CMIParticleType.PARTICLE : type;
      this.icon = icon == null ? CMIMaterial.STONE : icon;
      this.dataType = dataType == null ? CMIEffectManager.CMIParticleDataType.Void : dataType;
    }

    public String getName() {
      return this.name;
    }

    @Deprecated
    public int getId() {
      return 0;
    }

    public CMIEffectManager.CMIParticleType getType() {
      return this.type;
    }

    public boolean isParticle() {
      return this.type == CMIEffectManager.CMIParticleType.PARTICLE;
    }

    public boolean isColored() {
      return this.dataType.contains(CMIEffectDataValueTypes.ColorFrom);
    }

    public static boolean isParticle(Effect effect) {
      if (effect == null) {
        return false;
      }

      CMIEffectManager.CMIParticle cmiEffect = get(effect.toString());
      return cmiEffect == null ? false : cmiEffect.isParticle();
    }

    public static Material getSafeIcon(Effect effect) {
      CMIEffectManager.CMIParticle cmiEffect = get(effect.toString());
      if (cmiEffect == null) {
        return Material.STONE;
      } else {
        return cmiEffect.getIcon() == null ? Material.STONE : cmiEffect.getIcon();
      }
    }

    public Material getSafeIcon() {
      return this.getIcon() == null ? Material.STONE : this.getIcon();
    }

    @Deprecated
    public static CMIEffectManager.CMIParticle getCMIParticle(String name) {
      return get(name);
    }

    public static CMIEffectManager.CMIParticle get(String name) {
      if (name == null) {
        return null;
      }

      name = name.replace("_", "").replace(" ", "").toLowerCase();
      return byName.get(name);
    }

    public static CMIEffect getCMIEffect(String name) {
      return CMIEffect.get(name);
    }

    public Effect getEffect() {
      if (this.effect != null) {
        return this.effect;
      }

      if (!this.isParticle()) {
        return null;
      }

      String toStringKey = this.toString().replace("_", "").toLowerCase();
      String nameKey = this.name().replace("_", "").toLowerCase();
      String getNameKey = this.getName().replace("_", "").toLowerCase();

      for (Effect one : Effect.values()) {
        String candidateToString = one.toString().toLowerCase().replace("_", "");
        String candidateName = one.name().toLowerCase().replace("_", "");
        if (candidateToString.equalsIgnoreCase(toStringKey)
            || candidateToString.equalsIgnoreCase(nameKey)
            || candidateToString.equalsIgnoreCase(getNameKey)
            || candidateName.equalsIgnoreCase(toStringKey)
            || candidateName.equalsIgnoreCase(nameKey)
            || candidateName.equalsIgnoreCase(getNameKey)) {
          this.effect = one;
          break;
        }
      }

      if (this.effect != null) {
        return this.effect;
      }

      for (String oneS : this.getSecondaryNames()) {
        String n = oneS.replace("_", "").toLowerCase();
        if (!n.isEmpty()) {
          for (Effect one : Effect.values()) {
            if (one.toString().toLowerCase().replace("_", "").equalsIgnoreCase(n)) {
              this.effect = one;
              return this.effect;
            }
          }
        }
      }

      return this.effect;
    }

    public Material getIcon() {
      return this.icon == null ? Material.STONE : this.icon.getMaterial();
    }

    public static List<CMIEffectManager.CMIParticle> getParticleList() {
      if (!ls.isEmpty()) {
        return ls;
      }

      TreeMap<String, CMIEffectManager.CMIParticle> sorted = new TreeMap<>(byName);

      for (CMIEffectManager.CMIParticle one : sorted.values()) {
        if (one.isParticle()
            && (!Version.isCurrentEqualOrHigher(Version.v1_9_R1) || one.getParticle() != null)
            && (!Version.isCurrentLower(Version.v1_13_R1) || one.getEffect() != null)
            && !ls.contains(one)) {
          ls.add(one);
        }
      }

      return ls;
    }

    @Deprecated
    public CMIEffectManager.CMIParticle getNextPartcileEffect() {
      return this.getNextParticleEffect();
    }

    public CMIEffectManager.CMIParticle getNextParticleEffect() {
      List<CMIEffectManager.CMIParticle> list = getParticleList();
      CMIEffectManager.CMIParticle firstValid = null;
      boolean returnNext = false;

      for (CMIEffectManager.CMIParticle p : list) {
        if (p != null
            && p.isParticle()
            && (!Version.isCurrentEqualOrHigher(Version.v1_9_R1) || p.getParticle() != null)) {
          if (firstValid == null) {
            firstValid = p;
          }

          if (returnNext) {
            return p;
          }

          if (p.equals(this)) {
            returnNext = true;
          }
        }
      }

      return firstValid != null ? firstValid : this;
    }

    public CMIEffectManager.CMIParticle getPrevParticleEffect() {
      List<CMIEffectManager.CMIParticle> list = getParticleList();
      CMIEffectManager.CMIParticle lastValid = null;

      for (int i = list.size() - 1; i >= 0; i--) {
        CMIEffectManager.CMIParticle p = list.get(i);
        if (p != null
            && p.isParticle()
            && (!Version.isCurrentEqualOrHigher(Version.v1_9_R1) || p.getParticle() != null)) {
          if (lastValid == null) {
            lastValid = p;
          }

          if (p.equals(this)) {
            for (int j = i - 1; j >= 0; j--) {
              CMIEffectManager.CMIParticle prev = list.get(j);
              if (prev != null
                  && prev.isParticle()
                  && (!Version.isCurrentEqualOrHigher(Version.v1_9_R1)
                      || prev.getParticle() != null)) {
                return prev;
              }
            }

            return lastValid;
          }
        }
      }

      return lastValid != null ? lastValid : this;
    }

    @Deprecated
    public String getSecondaryName() {
      return this.secondaryNames.isEmpty() ? "" : this.secondaryNames.get(0);
    }

    @Deprecated
    public void setSecondaryName(String secondaryName) {
      this.secondaryNames.clear();
      this.secondaryNames.add(secondaryName);
    }

    public boolean is(String name) {
      name = name.replace("_", "");
      if (this.toString().replace("_", "").equalsIgnoreCase(name)) {
        return true;
      }

      if (this.getName().replace("_", "").equalsIgnoreCase(name)) {
        return true;
      }

      for (String one : this.getSecondaryNames()) {
        if (one.replace("_", "").equalsIgnoreCase(name)) {
          return true;
        }
      }

      return false;
    }

    public List<String> getSecondaryNames() {
      return this.secondaryNames;
    }

    public Particle getParticle() {
      if (Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
        return null;
      }

      if (this.particle != null) {
        return (Particle) this.particle;
      }

      String toStringKey = this.toString().replace("_", "").toLowerCase();
      String nameKey = this.name().replace("_", "").toLowerCase();
      String getNameKey = this.getName().replace("_", "").toLowerCase();

      for (Particle one : Particle.values()) {
        String candidateToString = one.toString().toLowerCase().replace("_", "");
        String candidateName = one.name().toLowerCase().replace("_", "");
        if (candidateToString.equalsIgnoreCase(toStringKey)
            || candidateToString.equalsIgnoreCase(nameKey)
            || candidateToString.equalsIgnoreCase(getNameKey)
            || candidateName.equalsIgnoreCase(toStringKey)
            || candidateName.equalsIgnoreCase(nameKey)
            || candidateName.equalsIgnoreCase(getNameKey)) {
          this.particle = one;
          return one;
        }
      }

      for (String oneS : this.getSecondaryNames()) {
        String n = oneS.replace("_", "").toLowerCase();
        if (!n.isEmpty()) {
          for (Particle one : Particle.values()) {
            if (one.toString().toLowerCase().replace("_", "").equalsIgnoreCase(n)) {
              this.particle = one;
              return one;
            }
          }
        }
      }

      return this.particle == null ? null : (Particle) this.particle;
    }

    @Deprecated
    public Object getEnumParticle() {
      return this.EnumParticle;
    }

    @Deprecated
    public void setEnumParticle(Object enumParticle) {
      this.EnumParticle = enumParticle;
    }

    @Deprecated
    public int[] getExtra() {
      return this.extra;
    }

    @Deprecated
    public void setExtra(int[] extra) {
      this.extra = extra;
    }

    public CMIEffectManager.CMIParticleDataType getDataType() {
      return this.dataType;
    }

    static {
      for (CMIEffectManager.CMIParticle one : values()) {
        if ((!Version.isCurrentEqualOrHigher(Version.v1_20_R4)
                || !one.equals(COLOURED_DUST) && !one.equals(FLYING_GLYPH))
            && (!Version.isCurrentEqualOrLower(Version.v1_20_R3)
                || !one.equals(DUST) && !one.equals(FLYING_GLYPH))) {
          byName.put(one.toString().replace("_", "").toLowerCase(), one);
          byName.put(one.getName().replace("_", "").replace(" ", "").toLowerCase(), one);
          if (!one.getSecondaryNames().isEmpty()) {
            for (String name : one.getSecondaryNames()) {
              byName.put(name.replace("_", "").toLowerCase(), one);
            }
          }

          if (one.getParticle() != null) {
            byType.put(one.getParticle(), one);
          }
        }
      }

      ls = new ArrayList<>();
    }
  }

  public enum CMIParticleDataType {
    Void(),
    DustOptions(CMIEffectDataValueTypes.ColorFrom, CMIEffectDataValueTypes.Size),
    ItemStack(CMIEffectDataValueTypes.Material),
    BlockData(CMIEffectDataValueTypes.Material),
    MaterialData(CMIEffectDataValueTypes.Material),
    EntityData(),
    Color(CMIEffectDataValueTypes.ColorFrom),
    DustTransition(
        CMIEffectDataValueTypes.ColorFrom,
        CMIEffectDataValueTypes.ColorTo,
        CMIEffectDataValueTypes.Size),
    Vibration(CMIEffectDataValueTypes.Duration, CMIEffectDataValueTypes.Location),
    Float(CMIEffectDataValueTypes.Speed),
    Int(CMIEffectDataValueTypes.Speed),
    Trail(
        CMIEffectDataValueTypes.ColorFrom,
        CMIEffectDataValueTypes.Duration,
        CMIEffectDataValueTypes.Location),
    Spell(CMIEffectDataValueTypes.ColorFrom, CMIEffectDataValueTypes.Speed);

    private Set<CMIEffectDataValueTypes> dataTypes = new HashSet<>();

    CMIParticleDataType(CMIEffectDataValueTypes... cmiEffectDataValueTypes) {
      this.dataTypes.addAll(Arrays.asList(cmiEffectDataValueTypes));
    }

    public Set<CMIEffectDataValueTypes> getDataTypes() {
      return this.dataTypes;
    }

    public boolean contains(CMIEffectDataValueTypes type) {
      return this.dataTypes.contains(type);
    }
  }

  public enum CMIParticleType {
    SOUND,
    VISUAL,
    PARTICLE,
    NONE;
  }
}
