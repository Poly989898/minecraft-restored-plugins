package litebans;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public final class Template {
   public static final TemplateCompanion TEMPLATE_COMPANION = new TemplateCompanion(null);
   private final byte id;
   private final String name;
   private final PunishmentType punishmentType;
   private final TemplateInfo templateInfo;
   private final boolean flag2;
   private final List escalation;
   private final long escalationWindow;
   public static final byte VALUE_MINUS_1 = -1;
   public static final int VALUE_MINUS_1_2 = -1;
   private static final TemplateInfo EMPTY_INFO = new TemplateInfo(null, null, null, null, CollectionsKt.emptyList(), CollectionsKt.emptyList());
   private static final Template NONE = new Template((byte)-1, "", PunishmentType.BAN, EMPTY_INFO, false, CollectionsKt.emptyList(), 0L, null);

   private Template(byte b, String text, PunishmentType punishmentType, TemplateInfo templateInfo, boolean flag, List list, long value) {
      this.id = b;
      this.name = text;
      this.punishmentType = punishmentType;
      this.templateInfo = templateInfo;
      this.flag2 = flag;
      this.escalation = list;
      this.escalationWindow = value;
   }

   public final byte getId() {
      return this.id;
   }

   public final String getName() {
      return this.name;
   }

   public final PunishmentType getType() {
      return this.punishmentType;
   }

   public final TemplateInfo getDefaultInfo() {
      return this.templateInfo;
   }

   public final boolean isFlag2() {
      return this.flag2;
   }

   public final List getEscalation() {
      return this.escalation;
   }

   public final long getEscalationWindow() {
      return this.escalationWindow;
   }

   public final TemplateInfo getInfoForCount(int value) {
      return this.escalation.isEmpty() ? this.templateInfo : (value >= this.escalation.size() ? (TemplateInfo)CollectionsKt.last(this.escalation) : (value < 0 ? (TemplateInfo)CollectionsKt.first(this.escalation) : (TemplateInfo)this.escalation.get(value)));
   }

   @Override
   public String toString() {
      return "Template(id="
         + UByte.toStringImpl(this.id)
         + ", name="
         + this.name
         + ", type="
         + this.punishmentType
         + ", defaults="
         + this.templateInfo
         + ", ipTemplate="
         + this.flag2
         + ", ladder="
         + this.escalation
         + ", ladderExpiry="
         + this.escalationWindow
         + ')';
   }

   @Override
   public int hashCode() {
      int hashCode2 = UByte.hashCodeImpl(this.id);
      int value = hashCode2 * 31 + this.name.hashCode();
      int value2 = value * 31 + this.punishmentType.hashCode();
      int value3 = value2 * 31 + this.templateInfo.hashCode();
      int value4 = value3 * 31 + Boolean.hashCode(this.flag2);
      int value5 = value4 * 31 + this.escalation.hashCode();
      return value5 * 31 + Long.hashCode(this.escalationWindow);
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof Template)) {
         return false;
      } else {
         Template template = (Template)obj;
         if (this.id != template.id) {
            return false;
         } else if (!Intrinsics.areEqual(this.name, template.name)) {
            return false;
         } else if (this.punishmentType != template.punishmentType) {
            return false;
         } else if (!Intrinsics.areEqual(this.templateInfo, template.templateInfo)) {
            return false;
         } else if (this.flag2 != template.flag2) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.escalation, template.escalation) ? false : this.escalationWindow == template.escalationWindow;
         }
      }
   }

   public Template(byte b, String text, PunishmentType punishmentType, TemplateInfo templateInfo, boolean flag, List list, long value, DefaultConstructorMarker defaultConstructorMarker) {
      this(b, text, punishmentType, templateInfo, flag, list, value);
   }

   public static final TemplateInfo getEmptyInfo() {
      return EMPTY_INFO;
   }

   public static final Template getNone() {
      return NONE;
   }
}
