package litebans;

import java.lang.constant.Constable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Punishment implements Comparable {
   public static final PunishmentCompanion PUNISHMENT_COMPANION = new PunishmentCompanion(null);
   private final PunishmentType type;
   private final String uuid;
   private final String ip;
   private final CharSequence reason;
   private final String bannedByUuid;
   private final String bannedByName;
   private final String serverScope;
   private final String serverOrigin;
   private final long time;
   private final long until;
   private final byte template;
   private final boolean silent;
   private final boolean ipban;
   private final boolean active;
   private long id;
   private long removedByDate;
   private String removedByUuid;
   private String removedByName;
   private CharSequence removedByReason;
   private boolean warned;

   private Punishment(
      PunishmentType punishmentType,
      String text,
      String text2,
      CharSequence message,
      String text3,
      String text4,
      String text5,
      String text6,
      long value,
      long value2,
      byte b,
      boolean flag,
      boolean flag2,
      boolean flag3,
      long value3
   ) {
      this.type = punishmentType;
      this.uuid = text;
      this.ip = text2;
      this.reason = message;
      this.bannedByUuid = text3;
      this.bannedByName = text4;
      this.serverScope = text5;
      this.serverOrigin = text6;
      this.time = value;
      this.until = value2;
      this.template = b;
      this.silent = flag;
      this.ipban = flag2;
      this.active = flag3;
      this.id = value3;
   }

   public Punishment(
      PunishmentType punishmentType,
      String text,
      String text2,
      CharSequence message,
      String text3,
      String text4,
      String text5,
      String text6,
      long value,
      long value2,
      byte b,
      boolean flag,
      boolean flag2,
      boolean flag3,
      long value3,
      int value4,
      DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value4 & 1024) != 0) {
         b = -1;
      }

      if ((value4 & 2048) != 0) {
         flag = false;
      }

      if ((value4 & 4096) != 0) {
         flag2 = false;
      }

      if ((value4 & 8192) != 0) {
         flag3 = true;
      }

      if ((value4 & 16384) != 0) {
         value3 = -1L;
      }

      this(punishmentType, text, text2, message, text3, text4, text5, text6, value, value2, b, flag, flag2, flag3, value3, null);
   }

   public final PunishmentType getType() {
      return this.type;
   }

   public final String getUuid() {
      return this.uuid;
   }

   public final String getIp() {
      return this.ip;
   }

   public final CharSequence getReason() {
      return this.reason;
   }

   public final String getBannedByUuid() {
      return this.bannedByUuid;
   }

   public final String getBannedByName() {
      return this.bannedByName;
   }

   public final String getServerScope() {
      return this.serverScope;
   }

   public final String getServerOrigin() {
      return this.serverOrigin;
   }

   public final long getTime() {
      return this.time;
   }

   public final long getUntil() {
      return this.until;
   }

   public final byte getTemplate() {
      return this.template;
   }

   public final boolean isSilent() {
      return this.silent;
   }

   public final boolean isIpBan() {
      return this.ipban;
   }

   public final boolean isActive() {
      return this.active;
   }

   public final long getId() {
      return this.id;
   }

   public final void setId(long value) {
      this.id = value;
   }

   public Punishment(@NotNull PunishmentRequest punishmentRequest, @NotNull KotlinHelper13 kotlinHelper, @NotNull CharSequence message) {
      PunishmentType type2 = punishmentRequest.getType();
      String text = kotlinHelper.getText8();
      String text2 = kotlinHelper.getText9();
      String executorUuid = punishmentRequest.getExecutorUuid();
      String text3 = punishmentRequest.getExecutorName();
      String serverScope2 = punishmentRequest.getServerScope();
      String serverOrigin2 = punishmentRequest.getServerOrigin();
      long value = punishmentRequest.getCreationTime();
      long until2 = punishmentRequest.getUntil();
      Template template2 = punishmentRequest.getTemplate();
      this(
         type2,
         text,
         text2,
         message,
         executorUuid,
         text3,
         serverScope2,
         serverOrigin2,
         value,
         until2,
         template2 != null ? template2.getId() : -1,
         punishmentRequest.isSilent(),
         punishmentRequest.isIpBan(),
         !punishmentRequest.isSkip(),
         0L,
         16384,
         null
      );
   }

   public final long getRemovedByDate() {
      return this.removedByDate;
   }

   public final void setRemovedByDate(long value) {
      this.removedByDate = value;
   }

   public final String getRemovedByUuid() {
      return this.removedByUuid;
   }

   public final void setRemovedByUuid(@Nullable String text) {
      this.removedByUuid = text;
   }

   public final String getRemovedByName() {
      return this.removedByName;
   }

   public final void setRemovedByName(@Nullable String text) {
      this.removedByName = text;
   }

   public final CharSequence getRemovedByReason() {
      return this.removedByReason;
   }

   public final void setRemovedByReason(@Nullable CharSequence message) {
      this.removedByReason = message;
   }

   public final boolean isWarned() {
      return this.warned;
   }

   public final void setWarned(boolean flag) {
      this.warned = flag;
   }

   public int computeIntValue(@NotNull Punishment punishment) {
      return Intrinsics.compare(punishment.time, this.time);
   }

   public final String getWarnedSqlSuffix() {
      return this.type == PunishmentType.WARN ? ",warned" : "";
   }

   public final long getDuration() {
      return this.isPermanent() ? -1L : this.until - this.time;
   }

   public final boolean obtainFlag() {
      return this.ipban && PUNISHMENT_COMPANION.checkText(this.ip);
   }

   public final boolean isExpiredAt(long value) {
      return !this.isPermanent() && value >= this.until;
   }

   public final boolean isPermanent() {
      return this.until <= 0L;
   }

   public final String computeText(@NotNull PunishmentRequest punishmentRequest) {
      PunishmentRequest punishmentRequest2 = punishmentRequest;
      StringBuilder builder = new StringBuilder().append(punishmentRequest2.isSilent() ? "silent_" : "");
      Constable constable;
      switch (PunishmentSwitchMap.value[punishmentRequest2.getType().ordinal()]) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            constable = punishmentRequest2.getType();
            break;
         default:
            StringBuilder stringBuilder = new StringBuilder();
            constable = stringBuilder.append(!this.isPermanent() ? "temp_" : "").append(punishmentRequest2.isIpBan() ? "ip" : "").append(punishmentRequest2.getType()).toString();
      }

      return builder.append(constable).toString();
   }

   public final boolean checkText(@Nullable String text, @Nullable String text2, long value) {
      if (!PUNISHMENT_COMPANION.checkText(this.getUuid(), text)) {
         if (!PUNISHMENT_COMPANION.checkFlag(this.isIpBan(), this.getIp(), text2, this.obtainFlag())) {
            return false;
         }
      }

      return this.active && !this.isExpiredAt(value);
   }

   public final void handleText(@Nullable String text, @Nullable String text2, @NotNull CharSequence message) {
      this.removedByName = text;
      this.removedByUuid = text2;
      this.removedByReason = message.toString();
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Entry(type=")
         .append(this.type)
         .append(", uuid=")
         .append(this.uuid)
         .append(", ip=")
         .append(this.ip)
         .append(", reason=")
         .append((Object)this.reason)
         .append(", executorUUID=")
         .append(this.bannedByUuid)
         .append(", executorName=")
         .append(this.bannedByName)
         .append(", serverScope=")
         .append(this.serverScope)
         .append(", serverOrigin=")
         .append(this.serverOrigin)
         .append(", dateStart=")
         .append(this.time)
         .append(", dateEnd=")
         .append(this.until)
         .append(", template=")
         .append((Object)UByte.toStringImpl(this.template))
         .append(", silent=");
      stringBuilder.append(this.silent).append(", ipban=").append(this.ipban).append(", active=").append(this.active).append(", id=").append(this.id).append(')');
      return stringBuilder.toString();
   }

   @Override
   public int hashCode() {
      int value = this.type.hashCode();
      int value2 = value * 31 + (this.uuid == null ? 0 : this.uuid.hashCode());
      int value3 = value2 * 31 + (this.ip == null ? 0 : this.ip.hashCode());
      int value4 = value3 * 31 + this.reason.hashCode();
      int value5 = value4 * 31 + this.bannedByUuid.hashCode();
      int value6 = value5 * 31 + (this.bannedByName == null ? 0 : this.bannedByName.hashCode());
      int value7 = value6 * 31 + (this.serverScope == null ? 0 : this.serverScope.hashCode());
      int value8 = value7 * 31 + (this.serverOrigin == null ? 0 : this.serverOrigin.hashCode());
      int value9 = value8 * 31 + Long.hashCode(this.time);
      int value10 = value9 * 31 + Long.hashCode(this.until);
      int value11 = value10 * 31 + UByte.hashCodeImpl(this.template);
      int value12 = value11 * 31 + Boolean.hashCode(this.silent);
      int value13 = value12 * 31 + Boolean.hashCode(this.ipban);
      int value14 = value13 * 31 + Boolean.hashCode(this.active);
      return value14 * 31 + Long.hashCode(this.id);
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof Punishment)) {
         return false;
      } else {
         Punishment punishment = (Punishment)obj;
         if (this.type != punishment.type) {
            return false;
         } else if (!Intrinsics.areEqual(this.uuid, punishment.uuid)) {
            return false;
         } else if (!Intrinsics.areEqual(this.ip, punishment.ip)) {
            return false;
         } else if (!Intrinsics.areEqual(this.reason, punishment.reason)) {
            return false;
         } else if (!Intrinsics.areEqual(this.bannedByUuid, punishment.bannedByUuid)) {
            return false;
         } else if (!Intrinsics.areEqual(this.bannedByName, punishment.bannedByName)) {
            return false;
         } else if (!Intrinsics.areEqual(this.serverScope, punishment.serverScope)) {
            return false;
         } else if (!Intrinsics.areEqual(this.serverOrigin, punishment.serverOrigin)) {
            return false;
         } else if (this.time != punishment.time) {
            return false;
         } else if (this.until != punishment.until) {
            return false;
         } else if (this.template != punishment.template) {
            return false;
         } else if (this.silent != punishment.silent) {
            return false;
         } else if (this.ipban != punishment.ipban) {
            return false;
         } else {
            return this.active != punishment.active ? false : this.id == punishment.id;
         }
      }
   }

   @Override
   public int compareTo(Object obj) {
      return this.computeIntValue((Punishment)obj);
   }

   public Punishment(
      PunishmentType punishmentType,
      String text,
      String text2,
      CharSequence message,
      String text3,
      String text4,
      String text5,
      String text6,
      long value,
      long value2,
      byte b,
      boolean flag,
      boolean flag2,
      boolean flag3,
      long value3,
      DefaultConstructorMarker defaultConstructorMarker
   ) {
      this(punishmentType, text, text2, message, text3, text4, text5, text6, value, value2, b, flag, flag2, flag3, value3);
   }
}
