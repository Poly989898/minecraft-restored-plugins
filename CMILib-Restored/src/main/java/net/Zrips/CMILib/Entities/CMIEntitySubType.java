package net.Zrips.CMILib.Entities;

import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.ZombieVillager;

public enum CMIEntitySubType {
  TAMED,
  UNTAMED,
  BABY,
  ADULT,
  NOAI,
  AI,
  ANGRY,
  PASIVE,
  RED_CAT,
  SIAMESE_CAT,
  WILD_OCELOT,
  BLACK_CAT,
  WHITE,
  ORANGE,
  MAGENTA,
  LIGHT_BLUE,
  YELLOW,
  LIME,
  PINK,
  GRAY,
  LIGHT_GRAY,
  CYAN,
  PURPLE,
  BLUE,
  BROWN,
  GREEN,
  RED,
  BLACK,
  RAINBOW,
  CHESTNUT,
  CREAMY,
  DARK_BROWN,
  BLACK_AND_WHITE,
  GOLD,
  SALT_AND_PEPPER,
  THE_KILLER_BUNNY,
  SNOW,
  SCREAMING,
  LUCY,
  WILD,
  AGGRESSIVE,
  LAZY,
  PLAYFUL,
  WEAK,
  WORRIED,
  ALL_BLACK,
  BRITISH_SHORTHAIR,
  CALICO,
  JELLIE,
  PERSIAN,
  RAGDOLL,
  SIAMESE,
  TABBY,
  SIZE1,
  SIZE2,
  SIZE3,
  SIZE4,
  SIZE5,
  SIZE6,
  SIZE7,
  SIZE8,
  SIZE9,
  SIZE10,
  POWERED,
  UNPOWERED,
  NORMAL,
  FARMER,
  LIBRARIAN,
  PRIEST,
  BLACKSMITH,
  BUTCHER,
  NITWIT,
  HUSK,
  ARMORER,
  CARTOGRAPHER,
  CLERIC,
  FISHERMAN,
  FLETCHER,
  LEATHERWORKER,
  MASON,
  SHEPHERD,
  TOOLSMITH,
  WEAPONSMITH,
  DESERT,
  JUNGLE,
  PLAINS,
  SAVANNA,
  SWAMP,
  TAIGA;

  public static CMIEntitySubType getByName(String name) {
    name = name.replace("_", "");

    for (CMIEntitySubType one : values()) {
      if (one.toString().replace("_", "").equalsIgnoreCase(name)) {
        return one;
      }
    }

    return null;
  }

  public static List<CMIEntitySubType> getSubTypes(Entity ent) {
    List<CMIEntitySubType> types = new ArrayList<>();

    try {
      if (ent instanceof LivingEntity) {
        LivingEntity lentity = (LivingEntity) ent;
        if (lentity instanceof Tameable) {
          Tameable tamable = (Tameable) ent;
          types.add(tamable.isTamed() ? TAMED : UNTAMED);
        }

        try {
          if (lentity instanceof Ageable) {
            Ageable ageable = (Ageable) ent;
            types.add(ageable.isAdult() ? ADULT : BABY);
          }
        } catch (Exception var7) {
        }
      }

      if (ent instanceof Villager) {
        Villager villager = (Villager) ent;
        CMIEntitySubType c = getByName(villager.getProfession().toString());
        if (c != null) {
          types.add(c);
        }

        if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
          c = getByName(villager.getVillagerType().toString());
          if (c != null) {
            types.add(c);
          }
        }
      } else if (ent instanceof Ocelot) {
        Ocelot ocelot = (Ocelot) ent;
        CMIEntitySubType c = getByName(ocelot.getCatType().toString());
        if (c != null) {
          types.add(c);
        }
      } else if (ent instanceof Sheep) {
        Sheep sheep = (Sheep) ent;
        if (sheep.getCustomName() != null && sheep.getCustomName().equalsIgnoreCase("jeb_")) {
          types.add(RAINBOW);
        }

        CMIEntitySubType c = getByName(sheep.getColor().toString());
        if (c != null) {
          types.add(c);
        }
      } else if (ent instanceof Wolf) {
        Wolf wolf = (Wolf) ent;
        types.add(wolf.isAngry() ? ANGRY : PASIVE);
      } else if (ent instanceof Horse) {
        Horse horse = (Horse) ent;
        CMIEntitySubType c = getByName(horse.getColor().toString());
        if (c != null) {
          types.add(c);
        }
      } else if (ent instanceof Slime) {
        Slime slime = (Slime) ent;

        try {
          CMIEntitySubType c = getByName("SIZE" + slime.getSize());
          if (c != null) {
            types.add(c);
          }
        } catch (NumberFormatException var6) {
        }
      } else if (ent instanceof Creeper) {
        Creeper creeper = (Creeper) ent;
        types.add(creeper.isPowered() ? POWERED : UNPOWERED);
      } else if (ent instanceof MagmaCube) {
        MagmaCube slime = (MagmaCube) ent;

        try {
          CMIEntitySubType c = getByName("SIZE" + slime.getSize());
          if (c != null) {
            types.add(c);
          }
        } catch (NumberFormatException var5) {
        }
      } else if (ent instanceof Rabbit) {
        Rabbit rabbit = (Rabbit) ent;

        try {
          CMIEntitySubType c = getByName(rabbit.getRabbitType().toString());
          if (c != null) {
            types.add(c);
          }
        } catch (NumberFormatException var4) {
        }
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_11_R1)) {
        if (ent instanceof Llama) {
          Llama lam = (Llama) ent;
          CMIEntitySubType c = getByName(lam.getColor().toString());
          if (c != null) {
            types.add(c);
          }

          return types;
        }

        if (ent instanceof TraderLlama) {
          TraderLlama lam = (TraderLlama) ent;
          CMIEntitySubType c = getByName(lam.getColor().toString());
          if (c != null) {
            types.add(c);
          }

          return types;
        }

        if (ent instanceof ZombieVillager) {
          ZombieVillager zvil = (ZombieVillager) ent;
          CMIEntitySubType c = getByName(zvil.getVillagerProfession().toString());
          if (c != null) {
            types.add(c);
          }

          return types;
        }
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
        if (ent instanceof Shulker) {
          Shulker sl = (Shulker) ent;
          CMIEntitySubType c = getByName(sl.getColor().toString());
          if (c != null) {
            types.add(c);
          }

          return types;
        }

        if (ent instanceof Parrot) {
          Parrot par = (Parrot) ent;
          CMIEntitySubType c = getByName(par.getVariant().toString());
          if (c != null) {
            types.add(c);
          }

          return types;
        }
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
        if (ent instanceof MushroomCow) {
          MushroomCow cow = (MushroomCow) ent;
          CMIEntitySubType c = getByName(cow.getVariant().toString());
          if (c != null) {
            types.add(c);
          }

          return types;
        }

        if (ent instanceof Fox) {
          Fox fox = (Fox) ent;
          CMIEntitySubType c = getByName(fox.getFoxType().toString());
          if (c != null) {
            types.add(c);
          }

          return types;
        }

        if (ent instanceof Panda) {
          Panda panda = (Panda) ent;
          CMIEntitySubType c = getByName(panda.getMainGene().toString());
          if (c != null) {
            types.add(c);
          }

          c = getByName(panda.getHiddenGene().toString());
          if (c != null) {
            types.add(c);
          }

          return types;
        }

        if (ent instanceof Cat) {
          Cat cat = (Cat) ent;
          CMIEntitySubType c = getByName(cat.getCatType().toString());
          if (c != null) {
            types.add(c);
          }

          return types;
        }
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_15_R1) && ent instanceof Bee) {
        Bee bee = (Bee) ent;
        if (bee.getAnger() > 0) {
          types.add(ANGRY);
        } else {
          types.add(PASIVE);
        }

        return types;
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        if (ent instanceof Goat) {
          Goat goat = (Goat) ent;
          if (goat.isScreaming()) {
            types.add(SCREAMING);
          }

          return types;
        }

        if (ent instanceof Axolotl) {
          Axolotl axolotl = (Axolotl) ent;
          CMIEntitySubType c = getByName(axolotl.getVariant().toString());
          if (c != null) {
            types.add(c);
          }

          return types;
        }
      }
    } catch (Exception | Error var8) {
    }

    return types;
  }
}
