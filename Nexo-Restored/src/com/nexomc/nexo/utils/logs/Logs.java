package com.nexomc.nexo.utils.logs;

import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.AdventureUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007b\u0002\b\bJ\u001c\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u0007b\u0002\b\bJ\"\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\nH\u0007b\u0002\b\bb\u0002\b\fJ\"\u0010\r\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\nH\u0007b\u0002\b\bb\u0002\b\fJ\"\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\nH\u0007b\u0002\b\bb\u0002\b\fJ\f\u0010\t\u001a\u00020\u0005H\u0007b\u0002\b\bJ\u000e\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u0007J\u000e\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0001J\u0016\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u0007J#\u0010\u000f\u001a\u00020\u00052\u0016\u0010\u0016\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00010\u0017\"\u0004\u0018\u00010\u0001¢\u0006\u0002\u0010\u0018J-\u0010\u000f\u001a\u00020\u00052\b\u0010\u0012\u001a\u0004\u0018\u00010\u00072\u0016\u0010\u0016\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00010\u0017\"\u0004\u0018\u00010\u0001¢\u0006\u0002\u0010\u0019J\u0015\u0010\u001a\u001a\u0002H\u001b\"\u0004\b\u0000\u0010\u001b*\u0002H\u001b¢\u0006\u0002\u0010\u001cJ\u001d\u0010\u001a\u001a\u0002H\u001b\"\u0004\b\u0000\u0010\u001b*\u0002H\u001b2\u0006\u0010\u0012\u001a\u00020\u0007¢\u0006\u0002\u0010\u001dJ\u000e\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u001e\u001a\u00020\u001fR\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006 "},
   d2 = {"Lcom/nexomc/nexo/utils/logs/Logs;", "", "<init>", "()V", "logInfo", "", "message", "", "Lkotlin/jvm/JvmStatic;", "newline", "", "logSuccess", "Lkotlin/jvm/JvmOverloads;", "logError", "logWarn", "debug", "string", "object", "prefix", "debugColors", "", "Lorg/bukkit/ChatColor;", "objects", "", "([Ljava/lang/Object;)V", "(Ljava/lang/String;[Ljava/lang/Object;)V", "debugVal", "T", "(Ljava/lang/Object;)Ljava/lang/Object;", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", "component", "Lnet/kyori/adventure/text/Component;", "com.nexomc:core"}
)
public final class Logs {
   @NotNull
   public static final Logs INSTANCE = new Logs();
   @NotNull
   private static final List<ChatColor> debugColors;

   private Logs() {
   }

   @JvmStatic
   public static final void logInfo(@NotNull String message) {
      if (((CharSequence)message).length() > 0) {
         logInfo(message, false);
      }

   }

   @JvmStatic
   public static final void logInfo(@NotNull String message, boolean newline) {
      Component component = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize("<prefix><#529ced>" + message + "</#529ced>");
      Component info = component;
      Bukkit.getConsoleSender().sendMessage(newline ? info.append((Component)Component.newline()) : info);
   }

   @JvmStatic
   @JvmOverloads
   public static final void logSuccess(@NotNull String message, boolean newline) {
      Component component = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize("<prefix><#55ffa4>" + message + "</#55ffa4>");
      Component success = component;
      Bukkit.getConsoleSender().sendMessage(newline ? success.append((Component)Component.newline()) : success);
   }

   @JvmStatic
   @JvmOverloads
   public static final void logError(@NotNull String message, boolean newline) {
      Component component = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize("<prefix><#e73f34>" + message + "</#e73f34>");
      Component error = component;
      Bukkit.getConsoleSender().sendMessage(newline ? error.append((Component)Component.newline()) : error);
   }

   @JvmStatic
   @JvmOverloads
   public static final void logWarn(@NotNull String message, boolean newline) {
      Component component = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize("<prefix><#f9f178>" + message + "</#f9f178>");
      Component warning = component;
      Bukkit.getConsoleSender().sendMessage(newline ? warning.append((Component)Component.newline()) : warning);
   }

   @JvmStatic
   public static final void newline() {
      Bukkit.getConsoleSender().sendMessage((Component)Component.empty());
   }

   public final void debug(@NotNull String string) {
      if (Settings.DEBUG.toBool()) {
         Bukkit.broadcastMessage(string);
      }

   }

   public final void debug(@NotNull Object object) {
      if (Settings.DEBUG.toBool()) {
         Bukkit.broadcastMessage(object.toString());
      }

   }

   public final void debug(@NotNull Object object, @NotNull String prefix) {
      if (Settings.DEBUG.toBool()) {
         Bukkit.broadcastMessage(prefix + object);
      }

   }

   public final void debug(@NotNull Object... objects) {
      if (Settings.DEBUG.toBool()) {
         List objectList = CollectionsKt.listOf(Arrays.copyOf(objects, objects.length));
         StringBuilder stringBuilder = new StringBuilder();
         StringBuilder stringBuilder2 = stringBuilder;
         int value = 0;
         int i = 0;

         for(int j = ((Collection)objectList).size(); i < j; ++i) {
            stringBuilder2.append(((ChatColor)debugColors.get(i % debugColors.size())).toString()).append(objectList.get(i));
            if (i + 1 != objectList.size()) {
               stringBuilder2.append(ChatColor.WHITE.toString() + " | ");
            }
         }

         String p0 = stringBuilder.toString();
         value = 0;
         Bukkit.broadcastMessage(p0);
      }
   }

   public final void debug(@Nullable String prefix, @NotNull Object... objects) {
      if (Settings.DEBUG.toBool()) {
         List objectList = CollectionsKt.listOf(Arrays.copyOf(objects, objects.length));
         StringBuilder stringBuilder = new StringBuilder();
         StringBuilder stringBuilder2 = stringBuilder;
         int value = 0;
         stringBuilder.append(prefix);
         Iterable indices = (Iterable)CollectionsKt.getIndices((Collection)objectList);
         Iterator iterator = indices.iterator();

         while(iterator.hasNext()) {
            int element = ((IntIterator)iterator).nextInt();
            int value2 = 0;
            stringBuilder2.append(((ChatColor)debugColors.get(element % debugColors.size())).toString()).append(objectList.get(element));
            if (element + 1 != objectList.size()) {
               stringBuilder2.append(ChatColor.WHITE.toString() + " | ");
            }
         }

         stringBuilder2.append(prefix);
         String p0 = stringBuilder.toString();
         value = 0;
         Bukkit.broadcastMessage(p0);
      }
   }

   public final <T> T debugVal(T t) {
      int value = 0;
      if (Settings.DEBUG.toBool()) {
         Bukkit.broadcastMessage(String.valueOf(t));
      }

      return (T)t;
   }

   public final <T> T debugVal(T t, @NotNull String prefix) {
      int value = 0;
      if (Settings.DEBUG.toBool()) {
         Bukkit.broadcastMessage(prefix + t);
      }

      return (T)t;
   }

   public final void debug(@NotNull Component component) {
      if (Settings.DEBUG.toBool()) {
         Bukkit.getConsoleSender().sendMessage(component);
      }

   }

   @JvmStatic
   @JvmOverloads
   public static final void logSuccess(@NotNull String message) {
      logSuccess(message, false);
   }

   @JvmStatic
   @JvmOverloads
   public static final void logError(@NotNull String message) {
      logError(message, false);
   }

   @JvmStatic
   @JvmOverloads
   public static final void logWarn(@NotNull String message) {
      logWarn(message, false);
   }

   static {
      ChatColor[] chatColors = new ChatColor[]{ChatColor.GREEN, ChatColor.BLUE, ChatColor.RED, ChatColor.GOLD, ChatColor.LIGHT_PURPLE};
      debugColors = CollectionsKt.listOf(chatColors);
   }

   /** Kotlin default-argument bridge, kept for binary compatibility with classes that were not recompiled. */
   public static void logWarn$default(String message, boolean verbose, int mask, Object marker) { if ((mask & 2) != 0) verbose = false; logWarn(message, verbose); }
   public static void logError$default(String message, boolean verbose, int mask, Object marker) { if ((mask & 2) != 0) verbose = false; logError(message, verbose); }
   public static void logSuccess$default(String message, boolean verbose, int mask, Object marker) { if ((mask & 2) != 0) verbose = false; logSuccess(message, verbose); }
}
