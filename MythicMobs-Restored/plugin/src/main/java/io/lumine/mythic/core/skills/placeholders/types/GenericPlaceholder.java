package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.segments.PlaceholderSegmentResolver;
import io.lumine.mythic.core.skills.placeholders.segments.PlaceholderSegmentsManager;
import io.lumine.mythic.core.skills.placeholders.segments.SegmentSource;
import io.lumine.mythic.core.skills.placeholders.segments.types.GenericPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.segments.types.MetaKeywordsSegment;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.GenericPlaceholderTyping;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GenericPlaceholder<T> implements Placeholder, GenericPlaceholderTyping<T> {
   @Getter
   protected final GenericPlaceholder.GenericPlaceholderArguments instanceData;
   @NotNull
   protected final PlaceholderExecutor manager;
   @Getter
   @NotNull
   protected final PlaceholderSegmentsManager placeholderSegmentsManager;
   @Deprecated(
      forRemoval = true
   )
   protected final String[] args;
   @Getter
   protected final AtomicInteger usedPlaceholderArguments;
   @Nullable
   protected List<MetaKeywordsSegment> metaKeywords;
   @NotNull
   private final Class<T> parsedValueClass = this.getParsedValueClass();
   @Getter
   protected boolean metaKeywordsHaveBeenInitialized = false;
   @Nullable
   protected final PlaceholderString defaultValue;
   @NotNull
   protected GenericPlaceholder.MetaKeywordParser<T> metaKeywordParser = this::applyWithTypeConversion;

   public GenericPlaceholder(@NotNull GenericPlaceholder.GenericPlaceholderArguments arguments) {
      this.instanceData = arguments;
      this.placeholderSegmentsManager = this.instanceData.buildPlaceholderSegmentManager();
      this.manager = arguments.manager;
      this.args = arguments.args;
      this.usedPlaceholderArguments = new AtomicInteger(arguments.usedPlaceholderArguments);
      if (arguments.config != null) {
         this.defaultValue = arguments.config.getPlaceholderString(new String[]{"default", "def", "fallback", "|"}, "UNDEFINED");
      } else {
         this.defaultValue = null;
      }

      if (this.usedPlaceholderArguments.get() >= 0) {
         this.initializeMetaKeywords();
      } else {
         this.usedPlaceholderArguments.set(0);
      }
   }

   protected void initializeMetaKeywords() {
      List<GenericPlaceholderSegment> extraArgs = this.placeholderSegmentsManager.getExtraArgs();
      if (extraArgs.size() > this.usedPlaceholderArguments.get()) {
         this.metaKeywords = MetaKeywordsSegment.getMetaKeywordSegments(extraArgs, this.usedPlaceholderArguments.get());
         this.metaKeywordParser = this::applyMetaKeywords;
         this.metaKeywordsHaveBeenInitialized = true;
      } else {
         this.metaKeywords = null;
         if (this.getParsedValueClass() == String.class) {
            this.metaKeywordParser = this::applyDirectly;
         } else {
            this.metaKeywordParser = this::applyWithTypeConversion;
         }

         this.metaKeywordsHaveBeenInitialized = true;
      }
   }

   protected String applyMetaKeywords(PlaceholderContext context, T parsedPlaceholder) {
      return MetaKeywords.resolveMetaKeywords(new MetaKeywords.ExtractionContext(context, this.metaKeywords), parsedPlaceholder, this.parsedValueClass);
   }

   protected String applyWithTypeConversion(PlaceholderContext context, T parsedPlaceholder) {
      return this.convertType(parsedPlaceholder);
   }

   @Contract(
      value = "_, _ -> param2",
      pure = true
   )
   protected final String applyDirectly(PlaceholderContext context, T parsedPlaceholder) {
      return (String)parsedPlaceholder;
   }

   public String apply(PlaceholderContext placeholderContext) {
      try {
         T parsed = this.applyWithMetaKeywords(placeholderContext);
         if (parsed == null) {
            return this.defaultValue != null ? this.defaultValue.get(placeholderContext) : "UNDEFINED";
         } else {
            return this.metaKeywordParser.apply(placeholderContext, parsed);
         }
      } catch (Exception e) {
         return this.instanceData.unparsedPlaceholder != null ? this.instanceData.unparsedPlaceholder : "UNDEFINED";
      }
   }

   @Nullable
   public abstract T applyWithMetaKeywords(PlaceholderContext placeholderContext);

   @NotNull
   @Contract("_, _ -> new")
   public final ResolvedPlaceholderSegment<PlaceholderString> getPlaceholderString(Integer index, String... aliases) {
      return this.getPlaceholderString(index, SegmentSource.EXTRA_ARGS, aliases);
   }

   @NotNull
   @Contract("_, _, _ -> new")
   public final ResolvedPlaceholderSegment<PlaceholderString> getPlaceholderString(Integer index, SegmentSource source, String... aliases) {
      return this.getPlaceholder(index, source, mythicLineConfig -> mythicLineConfig.getPlaceholderString(aliases, null), PlaceholderString::of);
   }

   public final <U> PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<U> getResolver() {
      return PlaceholderSegmentResolver.<U>builder().placeholder(this);
   }

   @Contract("_, _, _, _ -> new")
   @NotNull
   public final <U> ResolvedPlaceholderSegment<U> getPlaceholder(
      Integer index, SegmentSource source, Function<MythicLineConfig, U> mlcGetter, Function<String, U> segValueParser
   ) {
      return this.<U>getResolver().mlcGetter(mlcGetter).segIndex(index).segValueParser(segValueParser).listSource(source).build().get();
   }

   public static class GenericPlaceholderArguments {
      @NotNull
      public final PlaceholderExecutor manager;
      @Nullable
      public final MythicLineConfig config;
      @Nullable
      public final String unparsedPlaceholder;
      public final short usedPlaceholderArguments;
      @NotNull
      private final List<GenericPlaceholderSegment> allArgs;
      public final int extraArgsStartIndex;
      @NotNull
      private final List<Integer> wildcardArgs;
      @Deprecated(
         forRemoval = true
      )
      @Nullable
      public final String[] args;

      @Contract(" -> new")
      public PlaceholderSegmentsManager buildPlaceholderSegmentManager() {
         return PlaceholderSegmentsManager.builder()
            .allArgs(this.allArgs)
            .extraArgsStartIndex(this.extraArgsStartIndex)
            .wildcardArgsIndexes(this.wildcardArgs)
            .build();
      }

      @Generated
      protected GenericPlaceholderArguments(GenericPlaceholder.GenericPlaceholderArguments.GenericPlaceholderArgumentsBuilder<?, ?> b) {
         this.manager = b.manager;
         if (this.manager == null) {
            throw new NullPointerException("manager is marked non-null but is null");
         } else {
            this.config = b.config;
            this.unparsedPlaceholder = b.unparsedPlaceholder;
            this.usedPlaceholderArguments = b.usedPlaceholderArguments;

            List allArgs = switch (b.allArgs == null ? 0 : b.allArgs.size()) {
               case 0 -> Collections.emptyList();
               case 1 -> Collections.singletonList((b.allArgs.get(0)));
               default -> Collections.unmodifiableList(new ArrayList<>(b.allArgs));
            };
            this.allArgs = allArgs;
            if (allArgs == null) {
               throw new NullPointerException("allArgs is marked non-null but is null");
            } else {
               this.extraArgsStartIndex = b.extraArgsStartIndex;

               List wildcardArgs = switch (b.wildcardArgs == null ? 0 : b.wildcardArgs.size()) {
                  case 0 -> Collections.emptyList();
                  case 1 -> Collections.singletonList((b.wildcardArgs.get(0)));
                  default -> Collections.unmodifiableList(new ArrayList<>(b.wildcardArgs));
               };
               this.wildcardArgs = wildcardArgs;
               if (wildcardArgs == null) {
                  throw new NullPointerException("wildcardArgs is marked non-null but is null");
               } else {
                  this.args = b.args;
               }
            }
         }
      }

      @Generated
      public static GenericPlaceholder.GenericPlaceholderArguments.GenericPlaceholderArgumentsBuilder<?, ?> builder() {
         return new GenericPlaceholder.GenericPlaceholderArguments.GenericPlaceholderArgumentsBuilderImpl();
      }

      @Generated
      public GenericPlaceholder.GenericPlaceholderArguments.GenericPlaceholderArgumentsBuilder<?, ?> toBuilder() {
         return new GenericPlaceholder.GenericPlaceholderArguments.GenericPlaceholderArgumentsBuilderImpl().$fillValuesFrom(this);
      }

      @ToString
      @Generated
      public abstract static class GenericPlaceholderArgumentsBuilder<C extends GenericPlaceholder.GenericPlaceholderArguments, B extends GenericPlaceholder.GenericPlaceholderArguments.GenericPlaceholderArgumentsBuilder<C, B>> {
         @Generated
         private PlaceholderExecutor manager;
         @Generated
         private MythicLineConfig config;
         @Generated
         private String unparsedPlaceholder;
         @Generated
         private short usedPlaceholderArguments;
         @Generated
         private ArrayList<GenericPlaceholderSegment> allArgs;
         @Generated
         private int extraArgsStartIndex;
         @Generated
         private ArrayList<Integer> wildcardArgs;
         @Generated
         private String[] args;

         @Generated
         protected B $fillValuesFrom(C instance) {
            $fillValuesFromInstanceIntoBuilder(instance, this);
            return this.self();
         }

         @Generated
         private static void $fillValuesFromInstanceIntoBuilder(
            GenericPlaceholder.GenericPlaceholderArguments instance, GenericPlaceholder.GenericPlaceholderArguments.GenericPlaceholderArgumentsBuilder<?, ?> b
         ) {
            b.manager(instance.manager);
            b.config(instance.config);
            b.unparsedPlaceholder(instance.unparsedPlaceholder);
            b.usedPlaceholderArguments(instance.usedPlaceholderArguments);
            b.allArgs(instance.allArgs == null ? Collections.emptyList() : instance.allArgs);
            b.extraArgsStartIndex(instance.extraArgsStartIndex);
            b.wildcardArgs(instance.wildcardArgs == null ? Collections.emptyList() : instance.wildcardArgs);
            b.args(instance.args);
         }

         @Generated
         public B manager(@NotNull PlaceholderExecutor manager) {
            if (manager == null) {
               throw new NullPointerException("manager is marked non-null but is null");
            } else {
               this.manager = manager;
               return this.self();
            }
         }

         @Generated
         public B config(@Nullable MythicLineConfig config) {
            this.config = config;
            return this.self();
         }

         @Generated
         public B unparsedPlaceholder(@Nullable String unparsedPlaceholder) {
            this.unparsedPlaceholder = unparsedPlaceholder;
            return this.self();
         }

         @Generated
         public B usedPlaceholderArguments(short usedPlaceholderArguments) {
            this.usedPlaceholderArguments = usedPlaceholderArguments;
            return this.self();
         }

         @Generated
         public B allArg(GenericPlaceholderSegment allArg) {
            if (this.allArgs == null) {
               this.allArgs = new ArrayList<>();
            }

            this.allArgs.add(allArg);
            return this.self();
         }

         @Generated
         public B allArgs(Collection<? extends GenericPlaceholderSegment> allArgs) {
            if (allArgs == null) {
               throw new NullPointerException("allArgs cannot be null");
            } else {
               if (this.allArgs == null) {
                  this.allArgs = new ArrayList<>();
               }

               this.allArgs.addAll(allArgs);
               return this.self();
            }
         }

         @Generated
         public B clearAllArgs() {
            if (this.allArgs != null) {
               this.allArgs.clear();
            }

            return this.self();
         }

         @Generated
         public B extraArgsStartIndex(int extraArgsStartIndex) {
            this.extraArgsStartIndex = extraArgsStartIndex;
            return this.self();
         }

         @Generated
         public B wildcardArg(Integer wildcardArg) {
            if (this.wildcardArgs == null) {
               this.wildcardArgs = new ArrayList<>();
            }

            this.wildcardArgs.add(wildcardArg);
            return this.self();
         }

         @Generated
         public B wildcardArgs(Collection<? extends Integer> wildcardArgs) {
            if (wildcardArgs == null) {
               throw new NullPointerException("wildcardArgs cannot be null");
            } else {
               if (this.wildcardArgs == null) {
                  this.wildcardArgs = new ArrayList<>();
               }

               this.wildcardArgs.addAll(wildcardArgs);
               return this.self();
            }
         }

         @Generated
         public B clearWildcardArgs() {
            if (this.wildcardArgs != null) {
               this.wildcardArgs.clear();
            }

            return this.self();
         }

         @Deprecated
         @Generated
         public B args(@Nullable String[] args) {
            this.args = args;
            return this.self();
         }

         @Generated
         protected abstract B self();

         @Generated
         public abstract C build();
      }

      @Generated
      private static final class GenericPlaceholderArgumentsBuilderImpl
         extends GenericPlaceholder.GenericPlaceholderArguments.GenericPlaceholderArgumentsBuilder<GenericPlaceholder.GenericPlaceholderArguments, GenericPlaceholder.GenericPlaceholderArguments.GenericPlaceholderArgumentsBuilderImpl> {
         @Generated
         protected GenericPlaceholder.GenericPlaceholderArguments.GenericPlaceholderArgumentsBuilderImpl self() {
            return this;
         }

         @Generated
         @Override
         public GenericPlaceholder.GenericPlaceholderArguments build() {
            return new GenericPlaceholder.GenericPlaceholderArguments(this);
         }
      }
   }

   @FunctionalInterface
   public interface MetaKeywordParser<T> {
      String apply(PlaceholderContext placeholderContext, @NotNull T t);
   }
}
