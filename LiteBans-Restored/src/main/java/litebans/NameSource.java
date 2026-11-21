package litebans;

public enum NameSource {
   NAME,
   NAME_WITH_EFS_FLAG,
   UNICODE_EXTRA_FIELD;

   public static final NameSource computeNameSource(String text) {
      return Enum.valueOf(NameSource.class, text);
   }
}
