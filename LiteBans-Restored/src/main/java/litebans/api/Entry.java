package litebans.api;

import lombok.Generated;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public abstract class Entry {
   private final long id_f;
   private final String type;
   @Nullable
   private final String uuid;
   @Nullable
   private final String ip_f;
   private final String reason;
   @Nullable
   private final String executorUUID;
   @Nullable
   private final String executorName;
   @Nullable
   private final String removedByUUID;
   @Nullable
   private final String removedByName;
   @Nullable
   private final String removalReason;
   private final long dateStart;
   private final long dateEnd;
   private final String serverScope;
   private final String serverOrigin;
   private final byte template;
   private final boolean silent;
   private final boolean ipban;
   private final boolean active;

   public abstract long getDuration();

   public abstract String getDurationString();

   public abstract long getRemainingDuration(long value);

   public abstract String getRemainingDurationString(long value);

   public abstract String getRandomID();

   public abstract boolean isExpired(long value);

   public abstract boolean isPermanent();

   public abstract int getTemplateID();

   @NonNull
   public abstract String getTemplateName();

   public abstract boolean hasTemplate();

   @Generated
   public Entry(
      long value,
      String text,
      @Nullable String text2,
      @Nullable String text3,
      String text4,
      @Nullable String text5,
      @Nullable String text6,
      @Nullable String text7,
      @Nullable String text8,
      @Nullable String text9,
      long value2,
      long value3,
      String text10,
      String text11,
      byte b,
      boolean flag,
      boolean flag2,
      boolean flag3
   ) {
      this.id_f = value;
      this.type = text;
      this.uuid = text2;
      this.ip_f = text3;
      this.reason = text4;
      this.executorUUID = text5;
      this.executorName = text6;
      this.removedByUUID = text7;
      this.removedByName = text8;
      this.removalReason = text9;
      this.dateStart = value2;
      this.dateEnd = value3;
      this.serverScope = text10;
      this.serverOrigin = text11;
      this.template = b;
      this.silent = flag;
      this.ipban = flag2;
      this.active = flag3;
   }

   @Generated
   public long getId() {
      return this.id_f;
   }

   @Generated
   public String getType() {
      return this.type;
   }

   @Nullable
   @Generated
   public String getUuid() {
      return this.uuid;
   }

   @Nullable
   @Generated
   public String getIp() {
      return this.ip_f;
   }

   @Generated
   public String getReason() {
      return this.reason;
   }

   @Nullable
   @Generated
   public String getExecutorUUID() {
      return this.executorUUID;
   }

   @Nullable
   @Generated
   public String getExecutorName() {
      return this.executorName;
   }

   @Nullable
   @Generated
   public String getRemovedByUUID() {
      return this.removedByUUID;
   }

   @Nullable
   @Generated
   public String getRemovedByName() {
      return this.removedByName;
   }

   @Nullable
   @Generated
   public String getRemovalReason() {
      return this.removalReason;
   }

   @Generated
   public long getDateStart() {
      return this.dateStart;
   }

   @Generated
   public long getDateEnd() {
      return this.dateEnd;
   }

   @Generated
   public String getServerScope() {
      return this.serverScope;
   }

   @Generated
   public String getServerOrigin() {
      return this.serverOrigin;
   }

   @Generated
   public boolean isSilent() {
      return this.silent;
   }

   @Generated
   public boolean isIpban() {
      return this.ipban;
   }

   @Generated
   public boolean isActive() {
      return this.active;
   }

   @Generated
   @Override
   public String toString() {
      return "Entry(id="
         + this.getId()
         + ", type="
         + this.getType()
         + ", uuid="
         + this.getUuid()
         + ", ip="
         + this.getIp()
         + ", reason="
         + this.getReason()
         + ", executorUUID="
         + this.getExecutorUUID()
         + ", executorName="
         + this.getExecutorName()
         + ", removedByUUID="
         + this.getRemovedByUUID()
         + ", removedByName="
         + this.getRemovedByName()
         + ", removalReason="
         + this.getRemovalReason()
         + ", dateStart="
         + this.getDateStart()
         + ", dateEnd="
         + this.getDateEnd()
         + ", serverScope="
         + this.getServerScope()
         + ", serverOrigin="
         + this.getServerOrigin()
         + ", template="
         + this.getTemplate()
         + ", silent="
         + this.isSilent()
         + ", ipban="
         + this.isIpban()
         + ", active="
         + this.isActive()
         + ")";
   }

   @Generated
   protected byte getTemplate() {
      return this.template;
   }
}
