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
   String getString(String text);

   String getString(String path, String defaultValue);

   double getDouble(String text);

   double getDouble(String text, double value);

   int getInt(String text);

   int getInt(String text, int count);

   boolean getBoolean(String text);

   boolean getBoolean(String text, boolean flag);

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
   ConfigObject getObject(String text);

   @NotNull
   ConfigObject adaptObject(String text);

   boolean contains(String text);

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
