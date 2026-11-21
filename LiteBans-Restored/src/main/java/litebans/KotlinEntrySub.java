package litebans;

import litebans.api.Entry;
import litebans.api.RandomID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KotlinEntrySub extends Entry {
   private final Platform platform;

   public KotlinEntrySub(
      @NotNull Platform platform,
   long value,
   @Nullable String text,
   @Nullable String text2,
   @Nullable String text3,
   @Nullable String text4,
   @Nullable String text5,
   @Nullable String text6,
   @Nullable String text7,
   @Nullable String text8,
   @Nullable String text9,
   long value2,
   long value3,
   @Nullable String text10,
   @Nullable String text11,
   byte b,
   boolean flag,
   boolean flag2,
   boolean flag3
) {
      super(value, text, text2, text3, text4, text5, text6, text7, text8, text9, value2, value3, text10, text11, b, flag, flag2, flag3);
      this.platform = platform;
   }

   @Override
   public String getDurationString() {
      long duration = this.getDuration();
      return Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, duration, false, 2, null);
   }

   @Override
   public long getRemainingDuration(long value) {
      return !this.isPermanent() && !this.isExpired(value) ? this.getDateEnd() - value : -1L;
   }

   @Override
   public String getRemainingDurationString(long value) {
      long remainingDuration = this.getRemainingDuration(value);
      return Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, remainingDuration, false, 2, null);
   }

   @Override
   public String getRandomID() {
      return RandomID.get().convert(this.getId());
   }

   @Override
   public long getDuration() {
      return this.isPermanent() ? -1L : this.getDateEnd() - this.getDateStart();
   }

   @Override
   public boolean isExpired(long value) {
      return !this.isPermanent() && value >= this.getDateEnd();
   }

   @Override
   public boolean isPermanent() {
      return this.getDateEnd() <= 0L;
   }

   @Override
   public int getTemplateID() {
      return UByte.constructorImpl(this.getTemplate()) & 0xFF;
   }

   @Override
   public String getTemplateName() {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      TemplateManager templateManager = module.getTemplateManager();
      PunishmentType punishmentType = PunishmentType.PUNISHMENT_TYPE_COMPANION.computePunishmentType(this.getType());
      Intrinsics.checkNotNull(punishmentType);
      Template template = templateManager.computeTemplate(punishmentType, this.getTemplateID());
      if (template != null) {
         String name = template.getName();
         if (name != null) {
            return name;
         }
      }

      return "";
   }

   @Override
   public boolean hasTemplate() {
      return this.getTemplateID() != (-1 & 255);
   }
}
