package net.Indyuce.mmocore.api.player.attribute;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.util.Closeable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class PlayerAttributes {
   private final PlayerData data;
   private final Map<String, PlayerAttributes.AttributeInstance> instances = new HashMap<>();

   public PlayerAttributes(PlayerData data) {
      this.data = data;
   }

   public void load(ConfigurationSection config) {
      for (String key : config.getKeys(false)) {
         try {
            String id = key.toLowerCase().replace("_", "-").replace(" ", "-");
            Validate.isTrue(MMOCore.plugin.attributeManager.has(id), "Could not find attribute called '" + id + "'");
            PlayerAttributes.AttributeInstance ins = new PlayerAttributes.AttributeInstance(id);
            ins.setBase(config.getInt(key));
            this.instances.put(id, ins);
         } catch (IllegalArgumentException exception) {
            this.data.log(Level.WARNING, exception.getMessage());
         }
      }
   }

   public void save(ConfigurationSection config) {
      this.instances.values().forEach(ins -> config.set(ins.id, ins.getBase()));
   }

   public String toJsonString() {
      JsonObject json = new JsonObject();

      for (PlayerAttributes.AttributeInstance ins : this.instances.values()) {
         json.addProperty(ins.getId(), ins.getBase());
      }

      return json.toString();
   }

   public void load(String json) {
      JsonObject jo = (JsonObject)MythicLib.plugin.getGson().fromJson(json, JsonObject.class);

      for (Entry<String, JsonElement> entry : jo.entrySet()) {
         try {
            String id = entry.getKey().toLowerCase().replace("_", "-").replace(" ", "-");
            Validate.isTrue(MMOCore.plugin.attributeManager.has(id), "Could not find attribute called '" + id + "'");
            PlayerAttributes.AttributeInstance ins = new PlayerAttributes.AttributeInstance(id);
            ins.setBase(entry.getValue().getAsInt());
            this.instances.put(id, ins);
         } catch (IllegalArgumentException exception) {
            this.data.log(Level.WARNING, exception.getMessage());
         }
      }
   }

   public PlayerData getData() {
      return this.data;
   }

   public int getAttribute(PlayerAttribute attribute) {
      return this.getInstance(attribute).getTotal();
   }

   public Collection<PlayerAttributes.AttributeInstance> getInstances() {
      return this.instances.values();
   }

   public Map<String, Integer> mapPoints() {
      Map<String, Integer> map = new HashMap<>();
      this.instances.values().forEach(ins -> map.put(ins.id, ins.spent));
      return map;
   }

   @NotNull
   public PlayerAttributes.AttributeInstance getInstance(String attribute) {
      return this.instances.computeIfAbsent(attribute, x$0 -> new PlayerAttributes.AttributeInstance(x$0));
   }

   @NotNull
   public PlayerAttributes.AttributeInstance getInstance(PlayerAttribute attribute) {
      return this.getInstance(attribute.getId());
   }

   @Deprecated
   public int countSkillPoints() {
      return this.countPoints();
   }

   public int countPoints() {
      int n = 0;

      for (PlayerAttributes.AttributeInstance ins : this.instances.values()) {
         n += ins.getBase();
      }

      return n;
   }

   @Deprecated
   public void setBaseAttribute(String id, int value) {
      PlayerAttributes.AttributeInstance ins = this.instances.get(id);
      if (ins != null) {
         ins.setBase(value);
      }
   }

   public class AttributeInstance {
      private int spent;
      private final String id;
      private final String enumName;
      private final Map<String, AttributeModifier> map = new HashMap<>();

      public AttributeInstance(@NotNull String id) {
         this.id = id;
         this.enumName = UtilityMethods.enumName(this.id);
      }

      public int getBase() {
         return this.spent;
      }

      @Deprecated
      public int getSpent() {
         return this.getBase();
      }

      public void setBase(int value) {
         this.spent = Math.max(0, value);
         if (PlayerAttributes.this.data.isOnline()) {
            this.updateStats();
         }
      }

      public void addBase(int value) {
         this.setBase(this.getBase() + value);
      }

      public int getTotal() {
         double d = this.spent;

         for (AttributeModifier attr : this.map.values()) {
            if (attr.getType() == ModifierType.FLAT) {
               d += attr.getValue();
            }
         }

         d += PlayerAttributes.this.data.getMMOPlayerData().getStatMap().getStat("ADDITIONAL_" + this.enumName);

         for (AttributeModifier attr : this.map.values()) {
            if (attr.getType() == ModifierType.RELATIVE) {
               d *= attr.getValue();
            }
         }

         d *= 1.0 + PlayerAttributes.this.data.getMMOPlayerData().getStatMap().getStat("ADDITIONAL_" + this.enumName + "_PERCENT") / 100.0;
         return (int)d;
      }

      public AttributeModifier getModifier(String key) {
         return this.map.get(key);
      }

      public AttributeModifier addModifier(String key, double value) {
         return this.addModifier(new AttributeModifier(key, this.id, value, ModifierType.FLAT, EquipmentSlot.OTHER, ModifierSource.OTHER));
      }

      public AttributeModifier addModifier(AttributeModifier modifier) {
         AttributeModifier current = this.map.put(modifier.getKey(), modifier);
         if (current instanceof Closeable) {
            ((Closeable)current).close();
         }

         this.updateStats();
         return current;
      }

      public Set<String> getKeys() {
         return this.map.keySet();
      }

      public boolean contains(String key) {
         return this.map.containsKey(key);
      }

      public AttributeModifier removeModifier(String key) {
         AttributeModifier mod = this.map.remove(key);
         if (mod != null) {
            if (mod instanceof Closeable) {
               ((Closeable)mod).close();
            }

            this.updateStats();
         }

         return mod;
      }

      public void updateStats() {
         PlayerAttribute attr = MMOCore.plugin.attributeManager.get(this.id);
         int total = this.getTotal();

         for (StatInstance ins : PlayerAttributes.this.data.getMMOPlayerData().getStatMap().getInstances()) {
            ins.removeIf(str -> str.equals("attribute." + this.id));
         }

         attr.getBuffs().forEach(buff -> buff.multiply(total).register(PlayerAttributes.this.data.getMMOPlayerData()));
      }

      public String getId() {
         return this.id;
      }
   }
}
