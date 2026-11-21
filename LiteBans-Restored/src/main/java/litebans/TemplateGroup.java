package litebans;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TemplateGroup {
   private final String name;
   private final PunishmentType punishmentType;
   private final Map map3;
   private final Map map4;
   private final List levels;
   private final long escalationWindow;

   public TemplateGroup(@NotNull String text, @NotNull PunishmentType punishmentType2, @NotNull Map map, @NotNull Map map2, @NotNull List list, long value) {
      this.name = text;
      this.punishmentType = punishmentType2;
      this.map3 = map;
      this.map4 = map2;
      this.levels = list;
      this.escalationWindow = value;
   }

   public final String getName() {
      return this.name;
   }

   public final PunishmentType getPunishmentType() {
      return this.punishmentType;
   }

   public final Map getMap3() {
      return this.map3;
   }

   public final Map getMap4() {
      return this.map4;
   }

   public final List getLevels() {
      return this.levels;
   }

   public final long getEscalationWindow() {
      return this.escalationWindow;
   }

   public final TemplateInfo computeTemplateInfo(int value) {
      return value >= this.levels.size() ? (TemplateInfo)CollectionsKt.last(this.levels) : (value < 0 ? (TemplateInfo)CollectionsKt.first(this.levels) : (TemplateInfo)this.levels.get(value));
   }

   @Override
   public String toString() {
      return "TemplateGroup(name="
         + this.name
         + ", type="
         + this.punishmentType
         + ", weights="
         + this.map3
         + ", templates="
         + this.map4
         + ", ladder="
         + this.levels
         + ", ladderExpiry="
         + this.escalationWindow
         + ')';
   }

   @Override
   public int hashCode() {
      int value = this.name.hashCode();
      int value2 = value * 31 + this.punishmentType.hashCode();
      int value3 = value2 * 31 + this.map3.hashCode();
      int value4 = value3 * 31 + this.map4.hashCode();
      int value5 = value4 * 31 + this.levels.hashCode();
      return value5 * 31 + Long.hashCode(this.escalationWindow);
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof TemplateGroup)) {
         return false;
      } else {
         TemplateGroup templateGroup = (TemplateGroup)obj;
         if (!Intrinsics.areEqual(this.name, templateGroup.name)) {
            return false;
         } else if (this.punishmentType != templateGroup.punishmentType) {
            return false;
         } else if (!Intrinsics.areEqual(this.map3, templateGroup.map3)) {
            return false;
         } else if (!Intrinsics.areEqual(this.map4, templateGroup.map4)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.levels, templateGroup.levels) ? false : this.escalationWindow == templateGroup.escalationWindow;
         }
      }
   }
}
