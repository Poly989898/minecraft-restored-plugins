package litebans;

public enum ArchiveNoneFormattingEventsEnum {
   NONE,
   FORMATTING,
   EVENTS,
   ALL;

   public static ArchiveNoneFormattingEventsEnum computeArchiveNoneFormattingEventsEnum(String text) {
      return Enum.valueOf(ArchiveNoneFormattingEventsEnum.class, text);
   }

   private static final ArchiveNoneFormattingEventsEnum[] obtainArchiveNoneFormattingEventsEnumArray() {
      return new ArchiveNoneFormattingEventsEnum[]{NONE, FORMATTING, EVENTS, ALL};
   }
}
