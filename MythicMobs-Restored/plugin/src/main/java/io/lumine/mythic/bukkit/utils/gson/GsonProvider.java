package io.lumine.mythic.bukkit.utils.gson;

import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lumine.mythic.bukkit.utils.gson.adapters.BukkitSerializableAdapterFactory;
import io.lumine.mythic.bukkit.utils.gson.adapters.FuckyTypeAdapterFactory;
import io.lumine.mythic.bukkit.utils.gson.adapters.GsonSerializableAdapterFactory;
import io.lumine.mythic.bukkit.utils.gson.adapters.HashBasedTableAdapter;
import java.io.Reader;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public final class GsonProvider {
   private static final GsonBuilder STANDARD_GSON_BUILDER = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
      .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
      .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
      .registerTypeAdapterFactory(FuckyTypeAdapterFactory.INSTANCE)
      .registerTypeAdapter(Table.class, HashBasedTableAdapter.INSTANCE)
      .serializeNulls()
      .disableHtmlEscaping();
   private static final GsonBuilder PRETTY_PRINT_GSON_BUILDER = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
      .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
      .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
      .registerTypeAdapterFactory(FuckyTypeAdapterFactory.INSTANCE)
      .registerTypeAdapter(Table.class, HashBasedTableAdapter.INSTANCE)
      .serializeNulls()
      .disableHtmlEscaping()
      .setPrettyPrinting();
   private static Gson STANDARD_GSON = STANDARD_GSON_BUILDER.create();
   private static Gson PRETTY_PRINT_GSON = PRETTY_PRINT_GSON_BUILDER.create();
   private static final JsonParser PARSER = new JsonParser();

   @Nonnull
   public static Gson standard() {
      return STANDARD_GSON;
   }

   @Nonnull
   public static Gson prettyPrinting() {
      return PRETTY_PRINT_GSON;
   }

   public static void update(Consumer<GsonBuilder> func) {
      func.accept(STANDARD_GSON_BUILDER);
      func.accept(PRETTY_PRINT_GSON_BUILDER);
      STANDARD_GSON = STANDARD_GSON_BUILDER.create();
      PRETTY_PRINT_GSON = PRETTY_PRINT_GSON_BUILDER.create();
   }

   @Nonnull
   public static JsonParser parser() {
      return PARSER;
   }

   @Nonnull
   public static JsonObject readObject(@Nonnull Reader reader) {
      return PARSER.parse(reader).getAsJsonObject();
   }

   @Nonnull
   public static JsonObject readObject(@Nonnull String s) {
      return PARSER.parse(s).getAsJsonObject();
   }

   public static void writeObject(@Nonnull Appendable writer, @Nonnull JsonObject object) {
      standard().toJson(object, writer);
   }

   public static void writeObjectPretty(@Nonnull Appendable writer, @Nonnull JsonObject object) {
      prettyPrinting().toJson(object, writer);
   }

   public static void writeElement(@Nonnull Appendable writer, @Nonnull JsonElement element) {
      standard().toJson(element, writer);
   }

   public static void writeElementPretty(@Nonnull Appendable writer, @Nonnull JsonElement element) {
      prettyPrinting().toJson(element, writer);
   }

   @Nonnull
   public static String toString(@Nonnull JsonElement element) {
      return Objects.requireNonNull(standard().toJson(element));
   }

   @Nonnull
   public static String toStringPretty(@Nonnull JsonElement element) {
      return Objects.requireNonNull(prettyPrinting().toJson(element));
   }

   private GsonProvider() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }

   @Nonnull
   @Deprecated
   public static Gson get() {
      return standard();
   }

   @Nonnull
   @Deprecated
   public static Gson getPrettyPrinting() {
      return prettyPrinting();
   }
}
