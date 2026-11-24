package io.lumine.mythic.lib.api.util.ui;

import java.util.ArrayList;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompactCodedValue {
   @NotNull
   String code;
   @NotNull
   String value;

   @NotNull
   public String getID() {
      return this.code;
   }

   @NotNull
   public String getValue() {
      return this.value;
   }

   public CompactCodedValue(@NotNull String code, @NotNull String value) {
      this.code = code;
      this.value = value;
   }

   @NotNull
   public static ArrayList<CompactCodedValue> getListFromString(@Nullable String source, @Nullable FriendlyFeedbackProvider ffp) {
      ArrayList<CompactCodedValue> ret = new ArrayList<>();
      if (source == null) {
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "No string provided to parse list of CompactCodedValues. ");
         return ret;
      }

      if (!source.contains("=")) {
         FriendlyFeedbackProvider.log(
            ffp, FriendlyFeedbackCategory.ERROR, "String provided '$u{0}$b' has not a single $e=$b (syntax of CompactCodedValues). ", source
         );
         return ret;
      }

      ArrayList<String> encoded = new ArrayList<>();
      if (source.contains(";")) {
         encoded.addAll(Arrays.asList(source.split(";")));
      } else {
         encoded.add(source);
      }

      for (String encode : encoded) {
         CompactCodedValue ccv = getFromString(encode, ffp);
         if (ccv != null) {
            ret.add(ccv);
         }
      }

      return ret;
   }

   @Nullable
   public static CompactCodedValue getFromString(@Nullable String source, @Nullable FriendlyFeedbackProvider ffp) {
      if (source == null) {
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "No string provided to parse list of CompactCodedValues. ");
         return null;
      } else {
         int locationOfEquals = source.indexOf("=");
         if (source.length() >= 3 && locationOfEquals >= 1) {
            String id = source.substring(0, locationOfEquals);
            String val = source.substring(locationOfEquals + 1);
            id = id.replace("<&sc>", ";").replace("<$sc>", ";");
            val = val.replace("<&sc>", ";").replace("<$sc>", ";");
            return new CompactCodedValue(id, val);
         } else {
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "Incorrect syntax in '$u{0}$b': Expected a Code=Value pair like $efruit=pear$b. ");
            return null;
         }
      }
   }
}
