package litebans;

public class JsonParseException extends Exception {
   private static final long VALUE_MINUS_7880698968187728548 = -7880698968187728548L;
   public static final int VALUE_0 = 0;
   public static final int VALUE_1 = 1;
   public static final int VALUE_2 = 2;
   private int intValue;
   private Object obj2;
   private int intValue2;

   public JsonParseException(int value, int value2, Object obj) {
      this.intValue2 = value;
      this.intValue = value2;
      this.obj2 = obj;
   }

   public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      switch (this.intValue) {
         case 0:
            stringBuffer.append("Unexpected character (").append(this.obj2).append(") at position ").append(this.intValue2).append(".");
            break;
         case 1:
            stringBuffer.append("Unexpected token ").append(this.obj2).append(" at position ").append(this.intValue2).append(".");
            break;
         case 2:
            stringBuffer.append("Unexpected exception at position ").append(this.intValue2).append(": ").append(this.obj2);
            break;
         default:
            stringBuffer.append("Unkown error at position ").append(this.intValue2).append(".");
      }

      return stringBuffer.toString();
   }
}
