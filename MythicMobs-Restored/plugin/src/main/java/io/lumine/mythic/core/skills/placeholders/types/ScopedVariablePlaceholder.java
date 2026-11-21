package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.segments.types.GenericPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.segments.types.MetaKeywordsSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableIdentifier;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ScopedVariablePlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   protected PlaceholderString variable;
   protected ScopedVariablePlaceholder.VariableGetter variableGetter;

   public ScopedVariablePlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) throws IllegalArgumentException {
      super(metaContext);
   }

   @Override
   protected void initializeMetaKeywords() {
      this.metaKeywordParser = (context, parsedPlaceholder) -> parsedPlaceholder;
      List<GenericPlaceholderSegment> extraArgs = this.getPlaceholderSegmentsManager().getExtraArgs();
      if (extraArgs.isEmpty()) {
         throw new IllegalArgumentException("At least one argument is required to fetch the variable's name!");
      } else if (extraArgs.size() == 1) {
         this.variable = PlaceholderString.of(extraArgs.get(0).key());
         this.metaKeywords = null;
         this.variableGetter = this::getPlainly;
      } else {
         VariableIdentifier name = VariableIdentifier.of(extraArgs.get(0).key());
         if (name.defaultValue() != null && extraArgs.get(1).key().matches("^\\d+$") && name.defaultValue().matches("^\\d+$")) {
            this.variable = PlaceholderString.of(extraArgs.get(0).key() + "." + extraArgs.get(1).key());
            if (extraArgs.size() == 2) {
               this.metaKeywords = null;
               this.variableGetter = this::getPlainly;
            } else {
               this.metaKeywords = MetaKeywordsSegment.getMetaKeywordSegments(extraArgs, 2);
               this.variableGetter = this::getWithMetaKeywords;
            }
         } else {
            this.variable = PlaceholderString.of(extraArgs.get(0).key());
            this.metaKeywords = MetaKeywordsSegment.getMetaKeywordSegments(extraArgs, 1);
            this.variableGetter = this::getWithMetaKeywords;
            this.metaKeywordsHaveBeenInitialized = true;
         }
      }
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      VariableRegistry registry = this.getRegistry(placeholderContext);
      return registry == null ? null : this.variableGetter.get(registry, placeholderContext);
   }

   @Nullable
   protected abstract VariableRegistry getRegistry(PlaceholderContext placeholderContext);

   @Nullable
   private String getWithMetaKeywords(@NotNull VariableRegistry registry, @NotNull PlaceholderContext context) {
      if (this.metaKeywords == null) {
         return this.getPlainly(registry, context);
      } else {
         VariableIdentifier variableIdentifier = VariableIdentifier.of(this.variable.get(context));
         Variable var = registry.get(variableIdentifier.name());
         return var == null
            ? variableIdentifier.defaultValue()
            : MetaKeywords.resolveMetaKeywords(new MetaKeywords.ExtractionContext(context, this.metaKeywords), var);
      }
   }

   @Nullable
   private String getPlainly(@NotNull VariableRegistry registry, @NotNull PlaceholderContext context) {
      return registry.getString(VariableIdentifier.of(this.variable.get(context)));
   }

   @FunctionalInterface
   public interface VariableGetter {
      @Nullable
      String get(@NotNull VariableRegistry variableRegistry, @NotNull PlaceholderContext placeholderContext);
   }
}
