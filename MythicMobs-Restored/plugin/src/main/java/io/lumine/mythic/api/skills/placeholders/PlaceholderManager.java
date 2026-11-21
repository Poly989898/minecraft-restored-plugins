package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor;
import io.lumine.mythic.core.skills.placeholders.PlaceholderParser;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

public interface PlaceholderManager {
   void register(String[] strs, Placeholder placeholder);

   void register(String str, Placeholder placeholder);

   void register(Class<?> clazz);

   void register(Class<?> clazz, MythicPlaceholder mythicPlaceholder);

   PlaceholderExecutor.PlaceholderEntry getPlaceholder(String str);

   void registerParser(PlaceholderParser placeholderParser);

   void recheckForPlaceholders();
}
