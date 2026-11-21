package io.lumine.mythic.core.constants;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.pdc.DataType;
import io.lumine.mythic.bukkit.utils.pdc.datatypes.collections.MapDataType;
import java.util.HashMap;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class MobKeys {
   public static final MapDataType<HashMap<String, Double>, String, Double> STAT_FIELD_TYPE = DataType.asHashMap(
      PersistentDataType.STRING, PersistentDataType.DOUBLE
   );
   public static final MapDataType<HashMap<String, HashMap<String, Double>>, String, HashMap<String, Double>> STAT_FIELD_ITEMS = DataType.asHashMap(
      PersistentDataType.STRING, DataType.asHashMap(PersistentDataType.STRING, PersistentDataType.DOUBLE)
   );
   public static final NamespacedKey TYPE = new NamespacedKey(MythicBukkit.inst(), "type");
   public static final NamespacedKey LEVEL = new NamespacedKey(MythicBukkit.inst(), "level");
   public static final NamespacedKey FACTION = new NamespacedKey(MythicBukkit.inst(), "faction");
   public static final NamespacedKey SPAWNER = new NamespacedKey(MythicBukkit.inst(), "spawner");
   public static final NamespacedKey STATS = new NamespacedKey(MythicBukkit.inst(), "stats");
   public static final NamespacedKey STAT_RATIOS = new NamespacedKey(MythicBukkit.inst(), "stat_ratios");
   public static final NamespacedKey VARIABLES = new NamespacedKey(MythicBukkit.inst(), "variables");
   public static final NamespacedKey EQUIPPED = new NamespacedKey(MythicBukkit.inst(), "equipped");
   public static final NamespacedKey SPAWN_TIME = new NamespacedKey(MythicBukkit.inst(), "spawn_time");
   public static final NamespacedKey SPAWN_LOCATION = new NamespacedKey(MythicBukkit.inst(), "spawn_location");
   public static final NamespacedKey STANCE = new NamespacedKey(MythicBukkit.inst(), "stance");
   public static final NamespacedKey LAST_SIGNAL = new NamespacedKey(MythicBukkit.inst(), "last_signal");
   public static final NamespacedKey OWNER = new NamespacedKey(MythicBukkit.inst(), "owner");
   public static final NamespacedKey PARENT = new NamespacedKey(MythicBukkit.inst(), "parent");
   public static final NamespacedKey CHILDREN = new NamespacedKey(MythicBukkit.inst(), "children");
   public static final NamespacedKey DISPLAY_NAME = new NamespacedKey(MythicBukkit.inst(), "display_name");
   public static final NamespacedKey DESPAWN_MODE = new NamespacedKey(MythicBukkit.inst(), "despawn_mode");
   public static final NamespacedKey HIDDEN_FROM = new NamespacedKey(MythicBukkit.inst(), "hidden_from");
   public static final NamespacedKey EXECUTION_AUDIENCE = new NamespacedKey(MythicBukkit.inst(), "execution_audience");
   public static final NamespacedKey EXECUTION_AUDIENCE_MUTATED = new NamespacedKey(MythicBukkit.inst(), "execution_audience_mutated");
   public static final NamespacedKey PIN_WAND = new NamespacedKey(MythicBukkit.inst(), "pin_wand");
   public static final NamespacedKey PIN_PACK = new NamespacedKey(MythicBukkit.inst(), "pin_pack");
   public static final NamespacedKey FANCY_DROP = new NamespacedKey(MythicBukkit.inst(), "fancydrop");
   public static final NamespacedKey IS_PROJECTILE = new NamespacedKey(MythicBukkit.inst(), "is-projectile");
   public static final NamespacedKey REBOUND_COUNT = new NamespacedKey(MythicBukkit.inst(), "rebound_count");
   public static final NamespacedKey REBOUND_LAST_AT = new NamespacedKey(MythicBukkit.inst(), "rebound_last_at");
}
