package io.lumine.mythic.api.config;

import io.lumine.mythic.api.skills.placeholders.PlaceholderAngle;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderColor;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderLong;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.placeholders.PlaceholderVector;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.variables.VariableScopedName;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public interface MythicLineConfig {
   boolean getBoolean(String str);

   boolean getBoolean(String str, boolean flag);

   boolean getBoolean(String[] strs, boolean flag);

   Boolean getBooleanNullable(String str);

   Boolean getBooleanNullable(String[] strs);

   String getString(String str);

   String getString(String str, String str2);

   String getString(String[] strs);

   String getString(String[] strs, String str, String... strs2);

   PlaceholderBoolean getPlaceholderBoolean(String str);

   PlaceholderBoolean getPlaceholderBoolean(String[] strs);

   PlaceholderBoolean getPlaceholderBoolean(String str, Boolean flag);

   PlaceholderBoolean getPlaceholderBoolean(String[] strs, Boolean flag);

   Chroma getColor(String str);

   Chroma getColor(String str, Chroma chroma);

   Chroma getColor(String[] strs);

   Chroma getColor(String[] strs, Chroma chroma);

   List<String> parseStringList(String str);

   List<String> getStringList(String str, String str2);

   List<String> getStringList(String[] strs, String str);

   List<String> getStringList(String[] strs, String str, String... strs2);

   int getInteger(String str);

   int getInteger(String str, int value);

   int getInteger(String[] strs);

   int getInteger(String[] strs, int value);

   double getDouble(String str);

   double getDouble(String str, double value);

   double getDouble(String[] strs, double value);

   float getFloat(String str);

   float getFloat(String str, float value);

   float getFloat(String[] strs, float value);

   long getLong(String[] strs, long value);

   List<Locus> getLocationList(String[] strs, List<Locus> locuses);

   PlaceholderString getPlaceholderString(String str, String str2);

   PlaceholderString getPlaceholderString(String[] strs, String str, String... strs2);

   PlaceholderInt getPlaceholderInteger(String str, PlaceholderInt placeholderInt);

   PlaceholderInt getPlaceholderInteger(String[] strs, PlaceholderInt placeholderInt, String... strs2);

   PlaceholderInt getPlaceholderInteger(String str, String str2);

   PlaceholderInt getPlaceholderInteger(String[] strs, String str, String... strs2);

   PlaceholderInt getPlaceholderInteger(String str, int value);

   PlaceholderInt getPlaceholderInteger(String[] strs, int value, String... strs2);

   PlaceholderLong getPlaceholderLong(String str, PlaceholderLong placeholderLong);

   PlaceholderLong getPlaceholderLong(String[] strs, PlaceholderLong placeholderLong, String... strs2);

   PlaceholderLong getPlaceholderLong(String str, String str2);

   PlaceholderLong getPlaceholderLong(String[] strs, String str, String... strs2);

   PlaceholderLong getPlaceholderLong(String str, long value);

   PlaceholderLong getPlaceholderLong(String[] strs, long value, String... strs2);

   PlaceholderDouble getPlaceholderDouble(String str, String str2);

   PlaceholderDouble getPlaceholderDouble(String[] strs, String str, String... strs2);

   PlaceholderDouble getPlaceholderDouble(String str, double value);

   PlaceholderDouble getPlaceholderDouble(String[] strs, double value, String... strs2);

   PlaceholderDouble getPlaceholderDouble(String str, PlaceholderDouble placeholderDouble);

   PlaceholderDouble getPlaceholderDouble(String[] strs, PlaceholderDouble placeholderDouble, String... strs2);

   PlaceholderDuration getPlaceholderDuration(String str, DurationUnit durationUnit, String str2);

   PlaceholderDuration getPlaceholderDuration(String[] strs, DurationUnit durationUnit, String str, String... strs2);

   PlaceholderFloat getPlaceholderFloat(String str, String str2);

   PlaceholderFloat getPlaceholderFloat(String[] strs, String str, String... strs2);

   PlaceholderFloat getPlaceholderFloat(String str, float value);

   PlaceholderFloat getPlaceholderFloat(String[] strs, float value, String... strs2);

   PlaceholderColor getPlaceholderColor(String str, String str2);

   PlaceholderColor getPlaceholderColor(String[] strs, String str, String... strs2);

   PlaceholderVector getPlaceholderVector(String str, String str2);

   PlaceholderVector getPlaceholderVector(String[] strs, String str);

   PlaceholderVector getPlaceholderVector(String[] strs, String str, String... strs2);

   PlaceholderAngle getPlaceholderAngle(String[] strs, AngleUnit angleUnit, String str);

   PlaceholderAngle getPlaceholderAngle(String[] strs, AngleUnit angleUnit, String str, String... strs2);

   SkillAudience getAudience(String str, String str2);

   SkillAudience getAudience(String[] strs, String str, String... strs2);

   PolymorphicPlaceholder getPolymorphicPlaceholder(String str);

   PolymorphicPlaceholder getPolymorphicPlaceholder(String str, String str2);

   PolymorphicPlaceholder getPolymorphicPlaceholder(String[] strs);

   PolymorphicPlaceholder getPolymorphicPlaceholder(String[] strs, String str);

   VariableScopedName getVariableScopedName(String str);

   VariableScopedName getVariableScopedName(String str, String str2);

   VariableScopedName getVariableScopedName(String[] strs);

   VariableScopedName getVariableScopedName(String[] strs, String str);

   <T extends Enum<T>> T getEnum(String[] strs, Class<T> clazz, T t);

   <T extends Enum<T>> T getEnum(String[] strs, Class<T> clazz, T t, String str);

   String getKey();

   String getLine();

   String getFileName();

   Set<Entry<String, String>> entrySet();
}
