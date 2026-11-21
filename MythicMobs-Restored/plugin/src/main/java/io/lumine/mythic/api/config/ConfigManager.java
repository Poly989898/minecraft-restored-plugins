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
import io.lumine.mythic.api.skills.placeholders.PlaceholderTime;
import io.lumine.mythic.api.skills.placeholders.PlaceholderVector;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigManager {
   File getConfigFolder();

   int getDebugLevel();

   void setDebugLevel(int value);

   boolean isDebugMode();

   MythicConfig createConfig(File file);

   MythicConfig createConfig(FileConfiguration fileConfiguration);

   MythicConfig createConfig(String str, FileConfiguration fileConfiguration);

   MythicConfig createConfig(String str, File file);

   MythicConfig createConfig(String str, File file, FileConfiguration fileConfiguration);

   PlaceholderTime createPlaceholderTime(String str);

   PlaceholderVector createPlaceholderVector(String str);

   MythicLineConfig createLineConfig(String str);

   MythicLineConfig createLineConfig(File file, String str);

   PlaceholderAngle createPlaceholderAngle(String str, AngleUnit angleUnit);

   PlaceholderBoolean createPlaceholderBoolean(String str);

   PlaceholderColor createPlaceholderColor(String str);

   PlaceholderDouble createPlaceholderDouble(String str);

   PlaceholderDuration createPlaceholderDuration(String str, DurationUnit durationUnit);

   PlaceholderFloat createPlaceholderFloat(String str);

   PlaceholderInt createPlaceholderInt(String str);

   PlaceholderLong createPlaceholderLong(String str);

   PlaceholderString createPlaceholderString(String str);

   PolymorphicPlaceholder createPolymorphicPlaceholder(String str);

   Boolean getLoadExampleConfigs();

   int getClockIntervalMain();
}
