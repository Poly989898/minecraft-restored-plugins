package io.lumine.mythic.lib.util.configobject;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.targeter.EntityTargeter;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.util.DoubleFormula;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigObject {
   String getString(String var1);

   String getString(String var1, String var2);

   double getDouble(String var1);

   double getDouble(String var1, double var2);

   int getInt(String var1);

   int getInt(String var1, int var2);

   boolean getBoolean(String var1);

   boolean getBoolean(String var1, boolean var2);

   default DoubleFormula getDoubleFormula(String key) {
      return new DoubleFormula(this.getString(key));
   }

   default DoubleFormula getDoubleFormula(String key, DoubleFormula defaultValue) {
      return this.contains(key) ? this.getDoubleFormula(key) : Objects.requireNonNull(defaultValue, "Default value cannot be null");
   }

   @Nullable
   default Script getScriptOrNull(String key) {
      return this.contains(key) ? MythicLib.plugin.getSkills().getScriptOrThrow(this.getString(key)) : null;
   }

   @NotNull
   default Script getScript(String key) {
      return MythicLib.plugin.getSkills().getScriptOrThrow(this.getString(key));
   }

   @NotNull
   default EntityTargeter getEntityTargeter(String key) {
      return MythicLib.plugin.getSkills().loadEntityTargeter(this.adaptObject(key));
   }

   @NotNull
   default LocationTargeter getLocationTargeter(String key) {
      return MythicLib.plugin.getSkills().loadLocationTargeter(this.adaptObject(key));
   }

   @NotNull
   ConfigObject getObject(String var1);

   @NotNull
   ConfigObject adaptObject(String var1);

   boolean contains(String var1);

   @NotNull
   Set<String> getKeys();

   @Nullable
   String getKey();

   default boolean hasKey() {
      return this.getKey() != null;
   }

   default void validateKeys(String... keys) {
      for (String key : keys) {
         Validate.isTrue(this.contains(key), "Could not find key '" + key + "' in config");
      }
   }

   default void validateArgs(int count) {
      Validate.isTrue(this.getKeys().size() >= count, "Config must have at least " + count + " parameters");
   }
}
