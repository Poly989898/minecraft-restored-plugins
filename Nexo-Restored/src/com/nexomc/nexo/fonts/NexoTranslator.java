package com.nexomc.nexo.fonts;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.pack.VanillaResourcePack;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.lang.Language;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0001\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\u001a\u0010\u0004\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\tH\u0016J\r\u0010\r\u001a\u00070\u000e¢\u0006\u0002\b\u000fH\u0016¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/fonts/NexoTranslator;", "Lnet/kyori/adventure/translation/Translator;", "<init>", "()V", "translate", "", "key", "", "locale", "Ljava/util/Locale;", "Lnet/kyori/adventure/text/Component;", "component", "Lnet/kyori/adventure/text/TranslatableComponent;", "name", "Lnet/kyori/adventure/key/Key;", "Lorg/jetbrains/annotations/NotNull;", "Companion", "com.nexomc:core"}
)
public final class NexoTranslator implements Translator {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private static final Key key;
   @NotNull
   private static final Key DEFAULT_LANG_KEY;

   @Nullable
   public java.text.MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
      return null;
   }

   @Nullable
   public Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
      String str;
      block: {
         ResourcePack resourcePack = NexoPlugin.Companion.instance().packGenerator().resourcePack();
         Language language = resourcePack.language(Companion.toKey(locale));
         if (language != null) {
            str = language.translation(component.key());
            if (str != null) {
               break block;
            }
         }

         language = resourcePack.language(DEFAULT_LANG_KEY);
         str = language != null ? language.translation(component.key()) : null;
         if (str == null) {
            return null;
         }
      }

      String translation;
      block: {
         translation = str;
         Language language2 = VanillaResourcePack.INSTANCE.getResourcePack().language(Companion.toKey(locale));
         if (language2 != null) {
            str = language2.translation(component.key());
            if (str != null) {
               break block;
            }
         }

         language2 = VanillaResourcePack.INSTANCE.getResourcePack().language(DEFAULT_LANG_KEY);
         str = language2 != null ? language2.translation(component.key()) : null;
      }

      String vanillaTranslation = str;
      if (Intrinsics.areEqual(vanillaTranslation, translation)) {
         return null;
      } else {
         return !((Collection)component.children()).isEmpty() ? AdventureUtilsKt.deserialize(translation).children(component.children()) : AdventureUtilsKt.deserialize(translation);
      }
   }

   @NotNull
   public Key name() {
      return key;
   }

   static {
      Key key2 = Key.key("nexo", "localization");
      key = key2;
      DEFAULT_LANG_KEY = Settings.DEFAULT_LANGUAGE_KEY.toKey();
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\b\u001a\u00020\u0005*\u00020\tH\u0002J\u0006\u0010\n\u001a\u00020\u000bR\u0013\u0010\u0004\u001a\u00070\u0005¢\u0006\u0002\b\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0007\u001a\u00070\u0005¢\u0006\u0002\b\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f"},
      d2 = {"Lcom/nexomc/nexo/fonts/NexoTranslator$Companion;", "", "<init>", "()V", "key", "Lnet/kyori/adventure/key/Key;", "Lorg/jetbrains/annotations/NotNull;", "DEFAULT_LANG_KEY", "toKey", "Ljava/util/Locale;", "registerTranslations", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      private final Key toKey(Locale locale) {
         String str = (locale.getLanguage() + "_" + locale.getCountry()).toLowerCase(Locale.ROOT);
         Key key2 = Key.key(str);
         return key2;
      }

      public final void registerTranslations() {
         Iterable sources = GlobalTranslator.translator().sources();
         Iterable iterable = sources;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            Translator it = (Translator)element;
            int value = 0;
            if (Intrinsics.areEqual(it.name(), NexoTranslator.key)) {
               destination.add(element);
            }
         }

         iterable = (Iterable)((List)destination);
         GlobalTranslator translator = GlobalTranslator.translator();
         GlobalTranslator globalTranslator = translator;

         for(Object obj : iterable) {
            Translator p0 = (Translator)obj;
            int value2 = 0;
            globalTranslator.removeSource(p0);
         }

         GlobalTranslator.translator().addSource(new NexoTranslator());
      }
   }
}
