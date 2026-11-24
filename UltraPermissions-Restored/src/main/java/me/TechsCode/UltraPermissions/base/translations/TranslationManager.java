package me.TechsCode.UltraPermissions.base.translations;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.reflection.indexer.ClasspathIndexer;
import me.TechsCode.UltraPermissions.base.reflection.indexer.Node;
import me.TechsCode.UltraPermissions.base.registry.RegistrationChoice;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TranslationManager {
   private final TranslationRegistry translationRegistry;
   private final Map<String, Phrase> phrases;
   private List<TranslationFile> translationFiles;
   private Map<String, String> translationCache;

   public TranslationManager(TechPlugin<?> techPlugin) {
      this.translationRegistry = techPlugin.getRegistry().register(new TranslationRegistry(), RegistrationChoice.GLOBAL_IF_AVAILABLE);
      File file = new File(techPlugin.getPluginFolder().getAbsolutePath() + "/Languages");
      file.mkdirs();
      Map<Phrase, Class<?>> phrasesFromClasses = this.loadPhrasesFromClasses(techPlugin);
      validatePhrases(phrasesFromClasses.keySet());
      this.phrases = phrasesFromClasses.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getName(), Entry::getKey));
      this.phrases.values().forEach(phrase -> phrase.inject(this));
      techPlugin.getScheduler()
         .runAsync(
            () -> {
               createDefaultLanguageFile(phrasesFromClasses, file, "Base", entry -> entry.getValue().getName().startsWith("me.TechsCode." + techPlugin.getName() + ".base"));
               createDefaultLanguageFile(
                  phrasesFromClasses,
                  file,
                  techPlugin.getName(),
                  entry -> entry.getValue().getName().startsWith("me.TechsCode." + techPlugin.getName()) && !entry.getValue().getName().contains(".base")
               );
               downloadTranslationFromGithub(techPlugin, file);
               this.translationFiles = Arrays.stream(Objects.requireNonNull(file.listFiles()))
                  .map(TranslationFile::new)
                  .filter(TranslationFile::isValid)
                  .collect(Collectors.toList());
               techPlugin.log("Translations successfully loaded & applied (" + this.translationFiles.size() + " Translation files found)");
            }
         );
   }

   public String getTranslation(String text) {
      if (this.translationCache != null) {
         return this.translationCache.get(text);
      }

      this.translationCache = this.getPhrases(this.getSelectedLanguage());
      return this.translationCache == null ? null : this.translationCache.get(text);
   }

   public Map<String, String> getPhrases(String text) {
      return this.translationFiles == null
         ? null
         : this.translationFiles
            .stream()
            .filter(translationFile -> translationFile.getLanguage().equalsIgnoreCase(text))
            .flatMap(translationFile -> translationFile.getPhrases().entrySet().stream())
            .filter(entry -> this.phrases.containsKey(entry.getKey()))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
   }

   private static void downloadTranslationFromGithub(TechPlugin<?> techPlugin, File file) {
      try {
         String text = IOUtils.toString(new URL("https://api.github.com/repos/TechsCode-Team/PluginTranslations/contents/Translations"), StandardCharsets.UTF_8);
         JsonParser jsonParser = new JsonParser();

         for (JsonElement jsonElement : (JsonArray)jsonParser.parse(text)) {
            JsonObject jsonObject = (JsonObject)jsonElement;
            String asString = jsonObject.get("name").getAsString();
            if (asString.endsWith(".lang") && asString.contains("_") && (asString.startsWith("Base_") || asString.startsWith(techPlugin.getName()))) {
               String downloadUrl = jsonObject.get("download_url").getAsString();
               File target = new File(file.getAbsolutePath() + "/" + asString);
               FileUtils.copyURLToFile(new URL(downloadUrl), target);
            }
         }
      } catch (IOException exception) {
         techPlugin.log("Could not load translations from github");
      }
   }

   private static void createDefaultLanguageFile(Map<Phrase, Class<?>> map, File file, String text, Predicate<? super Entry<Phrase, Class<?>>> predicate) {
      Map defaults = map.entrySet().stream().filter(predicate).map(Entry::getKey).collect(Collectors.toMap(Phrase::getName, Phrase::getDefaultPhrase));
      TranslationFile.createFile(file, text, "English", defaults);
   }

   private static void validatePhrases(Collection<Phrase> phrases) {
      ArrayList list = new ArrayList();

      for (Phrase phrase : phrases) {
         if (list.contains(phrase.getName())) {
            throw new IllegalStateException("Duplicate Phrase '" + phrase.getName() + "'");
         }

         if (phrase.getDefaultPhrase().contains("\u00a7")) {
            throw new IllegalStateException("Phrase '" + phrase.getName() + "' contains illegal symbol");
         }

         list.add(phrase.getName());
      }
   }

   private Map<Phrase, Class<?>> loadPhrasesFromClasses(TechPlugin<?> techPlugin) {
      HashMap map = new HashMap();

      for (Node node : ClasspathIndexer.index()
         .classes()
         .stream()
         .filter(
            node -> node.getClassName().startsWith("me.TechsCode")
               && !node.getClassName().startsWith("me.TechsCode." + techPlugin.getName() + ".dependencies")
               && !node.getClassName().startsWith("me.TechsCode." + techPlugin.getName() + ".internal")
               && !node.getClassName().startsWith("me.TechsCode." + techPlugin.getName() + ".reflection")
         )
         .collect(Collectors.toList())) {
         try {
            Optional asClass = node.getAsClass();
            if (asClass.isPresent()) {
               for (Field declaredField : ((Class)asClass.get()).getDeclaredFields()) {
                  if (Modifier.isStatic(declaredField.getModifiers()) && declaredField.getType() == Phrase.class) {
                     try {
                        declaredField.setAccessible(true);
                        Phrase phrase = (Phrase)declaredField.get(null);
                        map.put(phrase, (Class)asClass.get());
                     } catch (IllegalAccessException exception) {
                     }
                  }
               }
            }
         } catch (NoClassDefFoundError | ExceptionInInitializerError exception) {
         }
      }

      return map;
   }

   public String getSelectedLanguage() {
      return this.translationRegistry.getLanguage();
   }

   public void setSelectedLanguage(String text) {
      this.translationRegistry.setLanguage(text);
      this.translationCache = null;
   }

   public List<String> getAvailableLanguages() {
      return this.translationFiles.stream().map(TranslationFile::getLanguage).distinct().collect(Collectors.toList());
   }
}
