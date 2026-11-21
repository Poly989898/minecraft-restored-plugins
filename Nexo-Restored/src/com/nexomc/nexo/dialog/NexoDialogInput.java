package com.nexomc.nexo.dialog;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.NexoYamlKt;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput.MultilineOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\f\u001a\u0004\u0018\u00010\rJ\n\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002J\u0012\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0012\u001a\u00020\u0005H\u0002J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0019\u001a\u00020\u001aHÖ\u0081\u0004J\n\u0010\u001b\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/dialog/NexoDialogInput;", "", "key", "", "inputSection", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Ljava/lang/String;Lorg/bukkit/configuration/ConfigurationSection;)V", "getKey", "()Ljava/lang/String;", "getInputSection", "()Lorg/bukkit/configuration/ConfigurationSection;", "createDialogInput", "Lio/papermc/paper/registry/data/dialog/input/DialogInput;", "multilineOptions", "Lio/papermc/paper/registry/data/dialog/input/TextDialogInput$MultilineOptions;", "singleOptionEntry", "Lio/papermc/paper/registry/data/dialog/input/SingleOptionDialogInput$OptionEntry;", "option", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "com.nexomc:core"}
)
public final class NexoDialogInput {
   @NotNull
   private final String key;
   @NotNull
   private final ConfigurationSection inputSection;

   public NexoDialogInput(@NotNull String key, @NotNull ConfigurationSection inputSection) {
      super();
      this.key = key;
      this.inputSection = inputSection;
   }

   @NotNull
   public final String getKey() {
      return this.key;
   }

   @NotNull
   public final ConfigurationSection getInputSection() {
      return this.inputSection;
   }

   @Nullable
   public final DialogInput createDialogInput() {
      java.util.List list;
      NexoDialogInput nexoDialogInput = this;

      Object obj2;
      try {
         NexoDialogInput nexoDialogInput2 = nexoDialogInput;
         int value = 0;
         String string = nexoDialogInput2.inputSection.getString("type");
         obj2 = ResultShim.ok(DialogInputTypes.valueOf(string));
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj2;
      DialogInputTypes dialogInputTypes = DialogInputTypes.TEXT;
      DialogInputTypes type = (DialogInputTypes)(ResultShim.isFailure(obj) ? dialogInputTypes : obj);
      DialogInput dialogInput;
      switch (NexoDialogInput.WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
         case 1:
            int width = RangesKt.coerceIn(this.inputSection.getInt("width", 200), 1, 1024);
            Component richMessage = this.inputSection.getRichMessage("label");
            if (richMessage == null) {
               TextComponent textComponent = Component.empty();
               richMessage = (Component)textComponent;
            }

            Component label = richMessage;
            boolean labelVisible = this.inputSection.getBoolean("labelVisible", true);
            int maxLength = RangesKt.coerceAtLeast(this.inputSection.getInt("maxLength", 32), 0);
            String initial = StringsKt.take(NexoYamlKt.stringOrEmpty(this.inputSection, "initial"), maxLength);
            TextDialogInput.MultilineOptions multilineOptions = this.multilineOptions();
            dialogInput = (DialogInput)DialogInput.text(this.key, width, label, labelVisible, initial, maxLength, multilineOptions);
            break;
         case 2:
            Component component = this.inputSection.getRichMessage("label");
            if (component == null) {
               TextComponent textComponent2 = Component.empty();
               component = (Component)textComponent2;
            }

            Component label_10 = component;
            boolean initial_9 = this.inputSection.getBoolean("initial");
            String str = this.inputSection.getString("onTrue");
            if (str == null) {
               str = "";
            }

            String onTrue = str;
            str = this.inputSection.getString("onFalse");
            if (str == null) {
               str = "";
            }

            String onFalse = str;
            dialogInput = (DialogInput)DialogInput.bool(this.key, label_10, initial_9, onTrue, onFalse);
            break;
         case 3:
            int width_8 = RangesKt.coerceIn(this.inputSection.getInt("width", 200), 1, 1024);
            Component component2 = this.inputSection.getRichMessage("label");
            if (component2 == null) {
               TextComponent textComponent3 = Component.empty();
               component2 = (Component)textComponent3;
            }

            Component label_7 = component2;
            String str2 = this.inputSection.getString("labelFormat");
            if (str2 == null) {
               str2 = "options.generic_value";
            }

            String labelFormat = str2;
            float start = (float)this.inputSection.getDouble("start", (double)0.0F);
            float end = (float)this.inputSection.getDouble("end", (double)1.0F);
            Float initial_6 = this.inputSection.contains("initial") ? (float)RangesKt.coerceAtLeast(this.inputSection.getDouble("initial", (double)end / (double)2.0F), (double)0.0F) : null;
            Double number = this.inputSection.getDouble("step");
            double it = ((Number)number).doubleValue();
            Float step = it > (double)0.0F ? Float.valueOf((float)it) : null;
            dialogInput = (DialogInput)DialogInput.numberRange(this.key, width_8, label_7, labelFormat, start, end, initial_6, step);
            break;
         case 4:
            int width_5 = RangesKt.coerceIn(this.inputSection.getInt("width", 200), 1, 1024);
            Component component3 = this.inputSection.getRichMessage("label");
            if (component3 == null) {
               TextComponent textComponent4 = Component.empty();
               component3 = (Component)textComponent4;
            }

            Component label_4;
            boolean labelVisible_3;
            block: {
               label_4 = component3;
               labelVisible_3 = this.inputSection.getBoolean("labelVisible", true);
               ConfigurationSection configurationSection = NexoYamlKt.section(this.inputSection, "options");
               if (configurationSection != null) {
                  Map map = NexoYamlKt.childSections(configurationSection);
                  if (map != null) {
                     Collection collection = map.values();
                     if (collection != null) {
                        Iterable iterable = (Iterable)collection;
                        Collection destination = (Collection)(new ArrayList());

                        for(Object element : iterable) {
                           int value2 = 0;
                           ConfigurationSection p0 = (ConfigurationSection)element;
                           int value3 = 0;
                           SingleOptionDialogInput.OptionEntry optionEntry = this.singleOptionEntry(p0);
                           if (optionEntry != null) {
                              Object obj3 = optionEntry;
                              int value4 = 0;
                              destination.add(obj3);
                           }
                        }

                        list = (List)destination;
                        break block;
                     }
                  }
               }

               list = null;
            }

            List optionEntries = list;
            Collection initial_1 = (Collection)optionEntries;
            if (initial_1 == null || initial_1.isEmpty()) {
               NexoDialogs.INSTANCE.getContext().getLogger().warn("Skipped " + this.inputSection.getCurrentPath() + " as there was no valid options specified.");
               return null;
            }

            dialogInput = (DialogInput)DialogInput.singleOption(this.key, width_5, optionEntries, label_4, labelVisible_3);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return dialogInput;
   }

   private final TextDialogInput.MultilineOptions multilineOptions() {
      ConfigurationSection configurationSection = NexoYamlKt.section(this.inputSection, "multilineOptions");
      if (configurationSection == null) {
         return null;
      } else {
         ConfigurationSection multilineSection = configurationSection;
         Integer maxLines = multilineSection.contains("maxLines") ? RangesKt.coerceAtLeast(multilineSection.getInt("maxLines"), 0) : null;
         Integer height = (Integer)(multilineSection.contains("height") ? RangesKt.coerceIn(Math.min(multilineSection.getInt("height"), 512), 1, 512) : null);
         return MultilineOptions.create(maxLines, height);
      }
   }

   private final SingleOptionDialogInput.OptionEntry singleOptionEntry(ConfigurationSection option) {
      Component display = option.getRichMessage("display", (Component)Component.text(option.getName()));
      boolean initial = option.getBoolean("initial");
      return OptionEntry.create(option.getName(), display, initial);
   }

   @NotNull
   public final String component1() {
      return this.key;
   }

   @NotNull
   public final ConfigurationSection component2() {
      return this.inputSection;
   }

   @NotNull
   public final NexoDialogInput copy(@NotNull String key, @NotNull ConfigurationSection inputSection) {
      return new NexoDialogInput(key, inputSection);
   }

   @NotNull
   public String toString() {
      return "NexoDialogInput(key=" + this.key + ", inputSection=" + this.inputSection + ")";
   }

   public int hashCode() {
      int result = this.key.hashCode();
      result = result * 31 + this.inputSection.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof NexoDialogInput)) {
         return false;
      } else {
         NexoDialogInput nexoDialogInput = (NexoDialogInput)other;
         if (!Intrinsics.areEqual(this.key, nexoDialogInput.key)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.inputSection, nexoDialogInput.inputSection);
         }
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.nexomc.nexo.dialog.DialogInputTypes.values().length];
         try { a_0[com.nexomc.nexo.dialog.DialogInputTypes.TEXT.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogInputTypes.BOOL.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogInputTypes.NUMBER.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogInputTypes.SINGLE.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
