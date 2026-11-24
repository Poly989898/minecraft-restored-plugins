package fr.phoenixdevt.mmoprofiles.bukkit.profile.data;

import fr.phoenixdevt.profiles.data.ProfileAttributable;
import fr.phoenixdevt.profiles.util.Pair;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.Attributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProfileAttributableImpl implements ProfileAttributable {
   private final Map<Attribute, ProfileAttributableImpl.AttributeInstanceWrapper> attributes = new HashMap<>();

   public ProfileAttributableImpl() {
   }

   public ProfileAttributableImpl(@NotNull Player player) {
      for (Attribute attribute : Attributes.getAll()) {
         AttributeInstance ins = player.getAttribute(attribute);
         if (ins != null) {
            this.attributes.put(attribute, new ProfileAttributableImpl.AttributeInstanceWrapper(ins));
         }
      }
   }

   public ProfileAttributableImpl(@NotNull JsonObject object) {
      for (String key : object.keySet()) {
         this.attributes
            .put(Attributes.fromName(new String[]{this.adaptName(key)}), new ProfileAttributableImpl.AttributeInstanceWrapper(object.getAsJsonObject(key)));
      }
   }

   private String adaptName(String name) {
      return MythicLib.plugin.getVersion().isAbove(new int[]{1, 21, 2}) ? name.replace("GENERIC_", "").replace("PLAYER_", "") : name;
   }

   public void apply(@NotNull Player player) {
      for (Attribute attribute : Attributes.getAll()) {
         AttributeInstance ins = player.getAttribute(attribute);
         if (ins != null) {
            ins.getModifiers().forEach(ins::removeModifier);
            ProfileAttributableImpl.AttributeInstanceWrapper wrapper = this.attributes.get(attribute);
            double defaultBase = UtilityMethods.getPlayerDefaultBaseValue(attribute, ins);
            ins.setBaseValue(wrapper != null && (ins.getBaseValue() != 0.0 || defaultBase == 0.0) ? wrapper.base : defaultBase);
            if (wrapper != null) {
               wrapper.modifiers.forEach(mod -> ins.addModifier(mod));
            }
         }
      }
   }

   @Override
   public double getValue(@NotNull Attribute attribute) {
      ProfileAttributableImpl.AttributeInstanceWrapper wrapper = this.attributes.get(attribute);
      if (wrapper == null) {
         return UtilityMethods.getPlayerDefaultBaseValue(attribute, null);
      }

      double d = wrapper.base;

      for (AttributeModifier mod : wrapper.modifiers) {
         if (mod.getOperation() == Operation.ADD_NUMBER) {
            d += mod.getAmount();
         }
      }

      double e = d;

      for (AttributeModifier mod : wrapper.modifiers) {
         if (mod.getOperation() == Operation.ADD_SCALAR) {
            e += d * mod.getAmount();
         }
      }

      for (AttributeModifier mod : wrapper.modifiers) {
         if (mod.getOperation() == Operation.MULTIPLY_SCALAR_1) {
            e *= 1.0 + mod.getAmount();
         }
      }

      return e;
   }

   @Deprecated
   @NotNull
   public Map<Attribute, Pair<Double, AttributeModifier[]>> map() {
      Map<Attribute, Pair<Double, AttributeModifier[]>> map = new HashMap<>();
      this.attributes.forEach((attribute, wrapper) -> {
         AttributeModifier[] array = wrapper.modifiers.toArray(new AttributeModifier[0]);
         Pair<Double, AttributeModifier[]> pair = Pair.of(wrapper.base, array);
         map.put(attribute, pair);
      });
      return map;
   }

   @NotNull
   public JsonElement toJson() {
      JsonObject object = new JsonObject();
      this.attributes.forEach((key, value) -> object.add(Attributes.name(key), value.toJson()));
      return object;
   }

   public class AttributeInstanceWrapper {
      private final double base;
      private final List<AttributeModifier> modifiers = new ArrayList<>();

      public AttributeInstanceWrapper(@NotNull AttributeInstance ins) {
         this.base = ins.getBaseValue();
         this.modifiers.addAll(ins.getModifiers());
      }

      public AttributeInstanceWrapper(@NotNull JsonObject object) {
         this.base = object.get("Base").getAsDouble();

         for (JsonElement element : object.get("Modifiers").getAsJsonArray()) {
            this.modifiers.add((AttributeModifier)MythicLib.plugin.getGson().fromJson(element, AttributeModifier.class));
         }
      }

      @NotNull
      public JsonObject toJson() {
         JsonObject object = new JsonObject();
         object.addProperty("Base", this.base);
         JsonArray array = new JsonArray();
         this.modifiers.forEach(mod -> array.add(MythicLib.plugin.getGson().toJsonTree(mod)));
         object.add("Modifiers", array);
         return object;
      }
   }
}
