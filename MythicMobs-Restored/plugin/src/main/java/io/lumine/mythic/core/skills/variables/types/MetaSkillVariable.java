package io.lumine.mythic.core.skills.variables.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.core.skills.MetaSkill;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.referencemanagers.VariableListReferenceManager;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class MetaSkillVariable extends Variable {
   MetaSkill value;
   final String originalString;
   public static final VariableInfo<MetaSkill> variableInfo = VariableInfo.<MetaSkill>builder()
      .clazz(MetaSkillVariable.class)
      .type(VariableType.METASKILL)
      .typeName("MetaSkill")
      .valueClass(MetaSkill.class)
      .objectConstructor(MetaSkillVariable::ofObject)
      .polymorphicConstructor(MetaSkillVariable::of)
      .build();
   public static VariableListReferenceManager<MetaSkillVariable> MetaSkillVariableManager = new VariableListReferenceManager<MetaSkillVariable>() {
      @Override
      public void reloadAll() {
         for (MetaSkillVariable var : this.getAllAlive()) {
            var.reload();
         }
      }
   };

   @Override
   public VariableInfo<MetaSkill> getVariableInfo() {
      return variableInfo;
   }

   public static MetaSkillVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new MetaSkillVariable(data.getString(context), expirationTime);
   }

   public static String toString(MetaSkill data) {
      return data.getBaseSkill().getMechanics().stream().map(Object::toString).collect(Collectors.joining("\n"));
   }

   public static MetaSkill fromString(String data) {
      AtomicReference<MetaSkill> ret = new AtomicReference<>();
      VolatileCodeHandler.getPlugin().getSkillManager().getSkill(null, data).ifPresent(skill -> ret.set((MetaSkill)skill));
      return ret.get();
   }

   public static MetaSkillVariable ofObject(Object o, Long expirationTime) {
      return new MetaSkillVariable(String.valueOf(o), expirationTime);
   }

   public MetaSkillVariable(String value, long expirationTime) {
      super(expirationTime);
      this.originalString = value;
      this.value = fromString(value);
      MetaSkillVariableManager.register(this);
   }

   public MetaSkill get() {
      return this.value;
   }

   public MetaSkill getValueClone() {
      return this.value;
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      return false;
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      return false;
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      return false;
   }

   @Override
   public String toString() {
      return this.originalString;
   }

   public void reload() {
      MetaSkill newMeta = fromString(this.originalString);
      this.updateAfterSecondPass(newMeta);
   }

   private void updateAfterSecondPass(MetaSkill newMeta) {
      VolatileCodeHandler.getPlugin().getSkillManager().queueSecondPass(() -> this.value = newMeta);
   }

   public static class Serializer implements JsonSerializer<MetaSkillVariable>, JsonDeserializer<MetaSkillVariable> {
      public JsonElement serialize(MetaSkillVariable src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject obj = new JsonObject();
         obj.addProperty("value", src.originalString);
         obj.addProperty("expirationTime", src.getExpirationTime());
         obj.addProperty("type", src.getTypeName());
         return obj;
      }

      public MetaSkillVariable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject obj = json.getAsJsonObject();
         String originalString = obj.get("value").getAsString();
         long expirationTime = obj.has("expirationTime") ? obj.get("expirationTime").getAsLong() : 0L;
         return new MetaSkillVariable(originalString, expirationTime);
      }
   }
}
