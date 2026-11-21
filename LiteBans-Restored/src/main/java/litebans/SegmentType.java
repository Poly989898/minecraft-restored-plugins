package litebans;

public enum SegmentType {
   TAPE(1),
   INODE(2),
   BITS(3),
   ADDR(4),
   END(5),
   CLRI(6);

   int intValue;

   public static final SegmentType computeSegmentType(String text) {
      return Enum.valueOf(SegmentType.class, text);
   }

   SegmentType(int value) {
      this.intValue = value;
   }

   public static final SegmentType computeSegmentType(int value) {
      for (SegmentType segmentType : values()) {
         if (segmentType.intValue == value) {
            return segmentType;
         }
      }

      return null;
   }
}
