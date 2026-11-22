package net.Zrips.CMILib.Entities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMIAttribute;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class CMIEntity {
  private Entity ent;
  public static HashMap<EntityType, String> translatedNames = new HashMap<>();
  static String prefix = "{";
  static String suffix = "}";
  static Pattern pname = Pattern.compile("^(?i)(name|n)\\" + prefix);

  public static void initialize() {
    Arrays.stream(EntityType.values())
        .filter(type -> type != null)
        .map(Enum::name)
        .sorted()
        .filter(name -> name != null)
        .forEach(
            name -> {
              EntityType ent = EntityType.valueOf(name);
              translatedNames.put(
                  ent,
                  CMILib.getInstance()
                      .getConfigManager()
                      .getLocaleConfig()
                      .get(
                          "info.EntityType." + name.toLowerCase(),
                          CMIText.firstToUpperCase(name.toUpperCase())));
            });
  }

  public CMIEntity(Entity ent) {
    this.ent = ent;
  }

  public Entity getEnt() {
    return this.ent;
  }

  public void setEnt(Entity ent) {
    this.ent = ent;
  }

  public String getName() {
    String name = translatedNames.get(this.getEnt().getType());
    if (name == null) {
      name = this.getEnt().getCustomName();
    }

    if (name == null) {
      name = this.getEnt().getName();
    }

    return name == null ? this.getEnt().getType().name() : name;
  }

  public String getCustomName() {
    String name = null;
    if (this.getEnt() instanceof Player) {
      Player player = (Player) this.getEnt();
      if (player.getDisplayName() != null) {
        name = player.getDisplayName();
      } else {
        name = player.getName();
      }
    } else {
      name = translatedNames.get(this.getEnt().getType());
      if (this.getEnt().getCustomName() != null) {
        name = this.getEnt().getCustomName();
      }

      if (name == null) {
        name = this.getEnt().getName();
      }
    }

    return name == null ? this.getEnt().getType().name() : name;
  }

  public Inventory getInventory() {
    if (this.ent instanceof InventoryHolder) {
      InventoryHolder holder = (InventoryHolder) this.ent;
      return holder.getInventory();
    } else {
      return null;
    }
  }

  public static boolean isItemFrame(Entity entity) {
    return entity == null
        ? false
        : entity.getType() == EntityType.ITEM_FRAME
            || Version.isCurrentEqualOrHigher(Version.v1_17_R1)
                && entity.getType().toString().equals("GLOW_ITEM_FRAME");
  }

  public static ItemStack setEntityType(ItemStack is, EntityType type) {
    try {
      BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
      CreatureSpawner bs = (CreatureSpawner) bsm.getBlockState();
      bs.setSpawnedType(type);
      if (!Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        bs.setCreatureTypeByName(type.name());
      }

      bsm.setBlockState(bs);
      String s =
          CMILib.getInstance()
              .getLM()
              .getMessage("info.EntityType." + type.toString().toLowerCase());
      String cap =
          type.name().toLowerCase().replace("_", " ").substring(0, 1).toUpperCase()
              + type.name().toLowerCase().replace("_", " ").substring(1);
      s =
          CMILib.getInstance().getLM().isString("info.EntityType." + type.toString().toLowerCase())
              ? s
              : cap;
      if (!bsm.hasDisplayName()) {
        bsm.setDisplayName(LC.info_Spawner.getLocale("[type]", s));
      }

      is.setItemMeta(bsm);
    } catch (Throwable e) {
      if (!Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        is.setDurability(type.getTypeId());
      }
    }

    return is;
  }

  public static String serialize(Entity ent) {
    if (ent == null) {
      return null;
    }

    StringBuilder str = new StringBuilder();
    str.append(CMIEntityType.getByType(ent.getType()).toString());
    if (ent.getCustomName() != null && !ent.getCustomName().isEmpty()) {
      str.append(";n" + prefix);
      str.append(ent.getCustomName().replace(" ", "_"));
      str.append(suffix);
    }

    return str.toString();
  }

  public static double getMaxHealth(Entity entity) {
    if (!(entity instanceof LivingEntity)) {
      return 0.0;
    } else {
      LivingEntity lentity = (LivingEntity) entity;
      if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
        AttributeInstance attr = CMIAttribute.MAX_HEALTH.get(lentity);
        return attr == null ? 0.0 : attr.getBaseValue();
      } else {
        return lentity.getMaxHealth();
      }
    }
  }
}
