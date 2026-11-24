package me.TechsCode.UltraPermissions.base.item;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.item.annotations.XChange;
import me.TechsCode.UltraPermissions.base.item.annotations.XInfo;
import me.TechsCode.UltraPermissions.base.item.annotations.XMerge;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class XRegistry<XForm extends XBase<XForm, BukkitForm>, BukkitForm> implements Iterable<XForm> {
   @Internal
   private static boolean PERFORM_AUTO_ADD = true;
   @Internal
   private static boolean DISCARD_METADATA = true;
   private static final boolean KEYED_EXISTS;
   private static final Map<Class<? extends XBase<?, ?>>, XRegistry<?, ?>> REGISTRIES;
   private static boolean ensureLoaded;
   private final Map<String, XForm> nameMappings = new HashMap<>(20);
   private final Map<BukkitForm, XForm> bukkitToX = new IdentityHashMap<>(20);
   private Map<XForm, XModuleMetadata> metadata;
   private Map<XForm, Field> backingFields;
   private final Class<BukkitForm> bukkitFormClass;
   private final Class<XForm> xFormClass;
   private final Supplier<Object> registrySupplier;
   private final BiFunction<BukkitForm, String[], XForm> creator;
   private final Function<Integer, XForm[]> createArray;
   private final String registryName;
   private final boolean supportsRegistry;
   private final XRegistry.ClassType bukkitClassType;
   private boolean pulled = false;
   private boolean alreadyDiscardedMetadata = false;

   private static void ensureLoadedRegistries() {
      if (!ensureLoaded) {
         XSound.REGISTRY.getClass();
         ensureLoaded = true;
      }
   }

   @Nullable
   @Experimental
   public static XRegistry<?, ?> rawRegistryOf(Class<?> type) {
      ensureLoadedRegistries();
      return REGISTRIES.get(type);
   }

   @Nullable
   @Experimental
   public static <XForm extends XBase<XForm, BukkitForm>, BukkitForm> XRegistry<XForm, BukkitForm> registryOf(Class<? extends XForm> type) {
      ensureLoadedRegistries();
      return (XRegistry<XForm, BukkitForm>)REGISTRIES.get(type);
   }

   protected static <XForm extends XBase<XForm, BukkitForm>, BukkitForm> void registerModule(XRegistry<XForm, BukkitForm> xRegistry, Class<? extends XForm> type) {
      REGISTRIES.put(type, xRegistry);
   }

   @Internal
   public XRegistry(
      Class<BukkitForm> type, Class<XForm> xFormType, Supplier<Object> supplier, BiFunction<BukkitForm, String[], XForm> biFunction, Function<Integer, XForm[]> function
   ) {
      boolean flag;
      try {
         supplier.get();
         flag = true;
      } catch (Throwable exception) {
         flag = false;
      }

      this.bukkitFormClass = Objects.requireNonNull(type);
      this.xFormClass = Objects.requireNonNull(xFormType);
      this.registryName = this.bukkitFormClass.getSimpleName();
      this.registrySupplier = supplier;
      this.createArray = Objects.requireNonNull(function);
      this.creator = biFunction;
      this.supportsRegistry = flag;
      if (type.isEnum()) {
         this.bukkitClassType = XRegistry.ClassType.ENUM;
      } else if (Modifier.isAbstract(type.getModifiers())) {
         this.bukkitClassType = XRegistry.ClassType.ABSTRACTION;
      } else {
         this.bukkitClassType = null;
      }

      if (!this.supportsRegistry && this.bukkitClassType == null) {
         throw new IllegalStateException("Bukkit form is not an enum, abstraction or a registry " + type);
      }

      registerModule(this, xFormType);
   }

   @Internal
   public XRegistry(Class<BukkitForm> type, Class<XForm> xFormType, Function<Integer, XForm[]> function) {
      this(type, xFormType, null, null, function);
   }

   @Internal
   @NotNull
   public Map<String, XForm> nameMapping() {
      return this.nameMappings;
   }

   @Internal
   @NotNull
   public Map<BukkitForm, XForm> bukkitMapping() {
      return this.bukkitToX;
   }

   public Class<BukkitForm> getBukkitFormClass() {
      return this.bukkitFormClass;
   }

   public Class<XForm> getXFormClass() {
      return this.xFormClass;
   }

   public String getName() {
      return this.registryName;
   }

   private void pullValues() {
      if (!this.pulled) {
         this.pulled = true;
         if (this.creator == null) {
            return;
         }

         this.pullFieldNames();
         if (PERFORM_AUTO_ADD) {
            this.pullSystemValues();
         }
      }
   }

   private static <T> void processEnumLikeFields(Class<T> type, BiConsumer<Field, T> biConsumer) {
      for (Field declaredField : type.getDeclaredFields()) {
         int modifiers = declaredField.getModifiers();
         if (declaredField.getType() == type && Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
            try {
               biConsumer.accept(declaredField, (T)declaredField.get(null));
            } catch (IllegalAccessException exception) {
               throw new IllegalStateException("Cannot process enum-like fields of: " + type, exception);
            }
         }
      }
   }

   @Internal
   public void registerName(String text, XForm value) {
      this.nameMappings.put(normalizeName(text), (XForm)value);
   }

   private void pullFieldNames() {
      processEnumLikeFields(this.xFormClass, (field, value) -> this.registerMerged(value, field));
   }

   private void pullSystemValues() {
      if (this.bukkitClassType == XRegistry.ClassType.ENUM) {
         for (Object enumConstant : this.bukkitFormClass.getEnumConstants()) {
            this.std(((Enum)enumConstant).name(), (BukkitForm)enumConstant);
         }
      } else {
         processEnumLikeFields(this.bukkitFormClass, (field, value) -> {
            if (value != null) {
               this.std(field.getName(), value);
            }
         });
      }

      if (this.supportsRegistry) {
         for (Keyed keyed : this.bukkitRegistry()) {
            this.std((BukkitForm)keyed);
         }
      }
   }

   private BukkitForm valueOf(String text) {
      text = text.toUpperCase(Locale.ENGLISH).replace('.', '_');
      Class bukkitFormClass = this.bukkitFormClass;

      try {
         return (BukkitForm)Enum.valueOf((Class)bukkitFormClass, text);
      } catch (IllegalArgumentException exception) {
         return null;
      }
   }

   private BukkitForm fieldOf(String text) {
      try {
         return (BukkitForm)this.bukkitFormClass.getDeclaredField(text).get(null);
      } catch (IllegalAccessException | NoSuchFieldException exception) {
         return null;
      }
   }

   @NotNull
   private Registry<?> bukkitRegistry() {
      return (Registry<?>)this.registrySupplier.get();
   }

   @Nullable
   protected BukkitForm getBukkit(String[] texts) {
      for (String text : texts) {
         Object obj;
         if (this.supportsRegistry) {
            text = text.toLowerCase(Locale.ENGLISH);
            NamespacedKey namespacedKey;
            if (text.contains(":")) {
               namespacedKey = NamespacedKey.fromString(text);
            } else {
               namespacedKey = NamespacedKey.minecraft(text);
            }

            Keyed keyed = this.bukkitRegistry().get(namespacedKey);
            if (keyed != null) {
               obj = keyed;
            } else {
               obj = null;
            }
         } else if (this.bukkitClassType == XRegistry.ClassType.ENUM) {
            obj = this.valueOf(text);
         } else {
            if (this.bukkitClassType != XRegistry.ClassType.ABSTRACTION) {
               throw new AssertionError("None of the class strategies worked for " + this);
            }

            obj = this.fieldOf(text);
         }

         if (obj != null) {
            return (BukkitForm)obj;
         }
      }

      return null;
   }

   @Internal
   public void discardMetadata() {
      if (DISCARD_METADATA) {
         this.backingFields = null;
         this.metadata = null;
      }
   }

   @NotNull
   public @Unmodifiable Collection<XForm> getValues() {
      this.pullValues();
      return Collections.unmodifiableCollection(this.bukkitToX.values());
   }

   @Deprecated
   public XForm[] values() {
      this.pullValues();
      Collection collection = this.bukkitToX.values();
      return (XForm[])collection.toArray((XBase[])this.createArray.apply(collection.size()));
   }

   @NotNull
   @Override
   public Iterator<XForm> iterator() {
      return this.getValues().iterator();
   }

   @NotNull
   public XForm getByBukkitForm(BukkitForm value) {
      Objects.requireNonNull(value, () -> "Cannot match null " + this.registryName);
      XBase xBase = this.bukkitToX.get(value);
      if (xBase == null) {
         if (!PERFORM_AUTO_ADD) {
            throw new UnsupportedOperationException("Unknown standard bukkit form (no auto-add) for " + this.registryName + ": " + value);
         }

         if (this.creator == null) {
            throw new UnsupportedOperationException("Unsupported value for " + this.registryName + ": " + value);
         }

         XBase standardized = this.std((BukkitForm)value);
         if (standardized == null) {
            throw new IllegalStateException("Unknown " + this.registryName + ": " + value);
         }
      }

      return (XForm)xBase;
   }

   public Optional<XForm> getByName(@NotNull String text) {
      Objects.requireNonNull(text, () -> "Cannot match null " + this.registryName);
      if (text.isEmpty()) {
         return Optional.empty();
      }

      this.pullValues();
      return Optional.ofNullable(this.nameMappings.get(normalizeName(text)));
   }

   @Internal
   @NotNull
   public static String getBukkitName(@NotNull Object obj) {
      Objects.requireNonNull(obj, "Cannot get name of a null bukkit form");
      if (obj instanceof Enum) {
         return ((Enum)obj).name();
      } else if (KEYED_EXISTS && obj instanceof Keyed) {
         return ((Keyed)obj).getKey().toString();
      } else if (obj instanceof PotionEffectType) {
         return ((PotionEffectType)obj).getName();
      } else if (obj instanceof Enchantment) {
         return ((Enchantment)obj).getName();
      } else {
         throw new AssertionError("Unknown xform type: " + obj + " (" + obj.getClass() + ")");
      }
   }

   @NotNull
   private static String format(@NotNull String text) {
      int length = text.length();
      char[] chars = new char[length];
      int index = 0;
      boolean flag = false;

      for (int i = 0; i < length; i++) {
         char ch = text.charAt(i);
         if (!flag && index != 0 && (ch == '-' || ch == ' ' || ch == '_') && chars[index] != '_') {
            flag = true;
         } else {
            boolean isDigit = false;
            if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || (isDigit = ch >= '0' && ch <= '9')) {
               if (flag) {
                  chars[index++] = '_';
                  flag = false;
               }

               if (isDigit) {
                  chars[index++] = ch;
               } else {
                  chars[index++] = (char)(ch & 95);
               }
            }
         }
      }

      return new String(chars, 0, index);
   }

   private static String normalizeName(String text) {
      text = text.toLowerCase(Locale.ENGLISH);
      if (text.startsWith("minecraft:")) {
         text = text.substring("minecraft:".length());
      }

      return text.replace('.', '_');
   }

   private XForm std(BukkitForm value) {
      return this.std(null, (BukkitForm)value);
   }

   private XForm std(@Nullable String text, BukkitForm value) {
      XBase xBase = this.bukkitToX.get(value);
      if (xBase != null) {
         return (XForm)xBase;
      }

      String bukkitName = getBukkitName(value);
      if (this.getBukkit(new String[]{bukkitName}) == null && text == null) {
         throw new IllegalArgumentException(
            "Unknown standard bukkit form for " + this.registryName + ": " + value + (value.toString().equals(bukkitName) ? "" : " (" + bukkitName + ")")
         );
      }

      xBase = this.creator.apply((BukkitForm)value, text == null ? new String[]{bukkitName} : new String[]{text, bukkitName});
      if (!PERFORM_AUTO_ADD) {
         return (XForm)xBase;
      }

      this.registerName(bukkitName, (XForm)xBase);
      if (text != null) {
         this.registerName(text, (XForm)xBase);
      }

      this.bukkitToX.put((BukkitForm)value, (XForm)xBase);
      return (XForm)xBase;
   }

   @Internal
   public XForm std(String[] texts) {
      Object bukkit = this.getBukkit(texts);
      XBase xBase = this.creator.apply((BukkitForm)bukkit, texts);
      return this.std((XForm)xBase);
   }

   @Internal
   public BukkitForm stdEnum(XForm value, String[] texts) {
      String name = value.name();
      boolean flag = false;
      Object bukkit = this.getBukkit(new String[]{name});
      if (bukkit == null) {
         bukkit = this.getBukkit(texts);
      }

      if (bukkit == null) {
         bukkit = this.registerMerged((XForm)value);
         flag = true;
      }

      return this.stdEnum0((XForm)value, texts, (BukkitForm)bukkit, flag);
   }

   public BukkitForm stdEnum(XForm value, String[] texts, BukkitForm bukkitValue) {
      return this.stdEnum0((XForm)value, texts, (BukkitForm)bukkitValue, false);
   }

   @Internal
   private BukkitForm stdEnum0(XForm value, String[] texts, BukkitForm bukkitValue, boolean flag) {
      String name = value.name();
      if (!flag) {
         this.registerMerged((XForm)value);
      }

      this.registerName(name, (XForm)value);

      for (String text : texts) {
         this.registerName(text, (XForm)value);
      }

      if (bukkitValue != null) {
         this.bukkitToX.put((BukkitForm)bukkitValue, (XForm)value);
      }

      return (BukkitForm)bukkitValue;
   }

   private BukkitForm registerMerged(XForm value) {
      return this.registerMerged((XForm)value, this.getBackingField((XForm)value));
   }

   @NotNull
   @Internal
   public Field getBackingField(XForm value) {
      try {
         return value.getClass().getDeclaredField(value.name());
      } catch (NoSuchFieldException exception) {
         try {
            if (this.backingFields == null) {
               this.cacheBackingFields();
            }

            Field field = this.backingFields.get(value);
            if (field != null) {
               return field;
            }
         } catch (Throwable fieldException) {
            IllegalStateException wrapped = new IllegalStateException("Cannot find field for XForm: " + value + " - " + value.getClass(), fieldException);
            wrapped.addSuppressed(exception);
            throw wrapped;
         }

         throw new IllegalStateException("Cannot find field for XForm: " + value + " - " + value.getClass(), exception);
      }
   }

   private void cacheBackingFields() {
      if (this.backingFields != null) {
         throw new IllegalStateException("Backing fields are already cached");
      }

      if (this.alreadyDiscardedMetadata) {
         throw new IllegalStateException("Metadata have already been used and discarded");
      }

      this.backingFields = new IdentityHashMap<>();
      this.alreadyDiscardedMetadata = true;

      for (Field declaredField : this.xFormClass.getDeclaredFields()) {
         int modifiers = declaredField.getModifiers();
         if (Modifier.isPublic(modifiers)
            && Modifier.isStatic(modifiers)
            && Modifier.isFinal(modifiers)
            && declaredField.getType() == this.xFormClass
            && !declaredField.isAnnotationPresent(XRegistry.Ignore.class)) {
            try {
               Object obj = Objects.requireNonNull(declaredField.get(null), () -> "XForm backing field returned null: " + declaredField + " for registry of " + this.xFormClass);
               XBase xBase = (XBase)obj;
               this.backingFields.put((XForm)xBase, declaredField);
            } catch (IllegalAccessException exception) {
               throw new RuntimeException(exception);
            }
         }
      }
   }

   @Internal
   public XModuleMetadata getOrRegisterMetadata(XForm value, Field field, boolean flag) {
      XModuleMetadata xModuleMetadata = this.metadata == null ? null : this.metadata.get(value);
      if (xModuleMetadata != null) {
         return xModuleMetadata;
      }

      xModuleMetadata = new XModuleMetadata(
         field.isAnnotationPresent(Deprecated.class),
         field.getAnnotationsByType(XChange.class),
         field.getAnnotationsByType(XMerge.class),
         field.getAnnotation(XInfo.class)
      );
      if (!flag) {
         if (this.metadata == null) {
            this.metadata = new IdentityHashMap<>(10);
         }

         this.metadata.put((XForm)value, xModuleMetadata);
      }

      return xModuleMetadata;
   }

   private BukkitForm registerMerged(XForm value, Field field) {
      XMerge[] merges = this.getOrRegisterMetadata((XForm)value, field, true).getMerges();
      Object obj = null;

      for (XMerge xMerge : merges) {
         obj = this.getBukkit(new String[]{xMerge.name()});
         this.registerName(xMerge.name(), (XForm)value);
         if (obj != null) {
            this.bukkitToX.put((BukkitForm)obj, (XForm)value);
         }
      }

      return (BukkitForm)obj;
   }

   @Internal
   public XForm std(Function<BukkitForm, XForm> function, String[] texts) {
      BukkitForm bukkit = this.getBukkit(texts);
      return this.std((XForm)function.apply(bukkit));
   }

   @Internal
   public XForm std(Function<BukkitForm, XForm> function, XForm value, String[] texts) {
      BukkitForm bukkit = this.getBukkit(texts);
      if (bukkit == null) {
         bukkit = value.get();
      }

      return this.std((XForm)function.apply(bukkit));
   }

   @Internal
   public XForm std(XForm value) {
      for (String name : value.getNames()) {
         this.registerName(name, (XForm)value);
      }

      if (value.isSupported()) {
         this.bukkitToX.put((BukkitForm)value.get(), (XForm)value);
      }

      return (XForm)value;
   }

   @Override
   public String toString() {
      return "XRegistry<"
         + this.registryName
         + ">(nameMappings="
         + this.nameMappings.size()
         + ", bukkitToX="
         + this.bukkitToX.size()
         + ", bukkitFormClass="
         + this.bukkitFormClass.getName()
         + ", xFormClass="
         + this.xFormClass.getName()
         + ", supportsRegistry="
         + this.supportsRegistry
         + ", bukkitFormClassType="
         + this.bukkitClassType
         + ", pulled="
         + this.pulled
         + ", values=["
         + this.bukkitToX.values().stream().limit(10L).map(XBase::name).collect(Collectors.joining(", "))
         + "])";
   }

   static {
      boolean flag = false;

      try {
         Class.forName("org.bukkit.Keyed");
         flag = true;
      } catch (ClassNotFoundException exception) {
      }

      KEYED_EXISTS = flag;
      REGISTRIES = new IdentityHashMap<>();
      ensureLoaded = false;
   }

   private enum ClassType {
      ENUM,
      ABSTRACTION;

      ClassType() {
      }
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.FIELD)
   @Documented
   @Internal
   public @interface Ignore {
   }
}
