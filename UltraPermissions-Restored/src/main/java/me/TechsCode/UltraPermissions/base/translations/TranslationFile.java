package me.TechsCode.UltraPermissions.base.translations;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

public class TranslationFile {
   private static final String[] disclaimer = new String[]{
      "#",
      "# Official Language File",
      "# To make your own language file, copy this file and edit the copy",
      "#",
      "# Changes made to this file will be reset!",
      "# Do not edit it!",
      "#",
      ""
   };
   private String name;
   private String language;
   private Map<String, String> phrases;

   public static TranslationFile createFile(File file, String text, String language, Map<String, String> map) {
      File translationFile = new File(file.getAbsolutePath() + "/" + text + "_" + language + ".lang");
      if (translationFile.exists() && !translationFile.delete()) {
         throw new IllegalStateException("No write access to directory");
      }

      try {
         if (translationFile.createNewFile()) {
            ArrayList list = new ArrayList<>(Arrays.asList(disclaimer));
            map.entrySet()
               .stream()
               .sorted(Entry.comparingByKey())
               .forEach(entry -> list.add((String)entry.getKey() + ": \"" + (String)entry.getValue() + "\""));
            FileUtils.writeLines(translationFile, list);
            return new TranslationFile(translationFile);
         } else {
            throw new IllegalStateException("No write access to file");
         }
      } catch (IOException exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public TranslationFile(File file) {
      String name = file.getName();
      if (name.endsWith(".lang") && name.contains("_")) {
         this.name = name.split("_")[0];
         this.language = name.split("_")[1].replace(".lang", "");

         try {
            this.phrases = FileUtils.readLines(file, StandardCharsets.UTF_8)
               .stream()
               .filter(text -> !text.startsWith("#"))
               .map(text -> text.split(": \""))
               .filter(texts -> texts.length == 2)
               .filter(texts -> !texts[0].contains("_"))
               .collect(Collectors.toMap(texts -> (String)texts[0], texts -> texts[1].replace("\"", "")));
         } catch (IOException exception) {
            exception.printStackTrace();
         }
      }
   }

   public String getLanguage() {
      return this.language;
   }

   public String getName() {
      return this.name;
   }

   public Map<String, String> getPhrases() {
      return this.phrases;
   }

   public boolean isValid() {
      return this.name != null && this.language != null && this.phrases != null;
   }
}
