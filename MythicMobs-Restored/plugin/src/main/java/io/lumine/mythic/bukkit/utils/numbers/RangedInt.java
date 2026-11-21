package io.lumine.mythic.bukkit.utils.numbers;

public class RangedInt {
   protected final RangedInt.Operation op;
   protected int min;
   protected int max;

   public RangedInt(String value) {
      this(value, false);
   }

   public RangedInt(String value, boolean squared) {
      if (value.startsWith(">=")) {
         String s = value.substring(2);
         this.min = Integer.parseInt(s);
         this.max = Integer.MAX_VALUE;
         this.op = RangedInt.Operation.GREATER_THAN_OR_EQUAL;
      } else if (value.startsWith("<=")) {
         String s = value.substring(2);
         this.min = Integer.MIN_VALUE;
         this.max = Integer.parseInt(s);
         this.op = RangedInt.Operation.LESS_THAN_OR_EQUAL;
      } else if (value.startsWith(">")) {
         String s = value.substring(1);
         this.min = Integer.parseInt(s);
         this.max = Integer.MAX_VALUE;
         this.op = RangedInt.Operation.GREATER_THAN;
      } else if (value.startsWith("<")) {
         String s = value.substring(1);
         this.min = Integer.MIN_VALUE;
         this.max = Integer.parseInt(s);
         this.op = RangedInt.Operation.LESS_THAN;
      } else if (value.contains("to")) {
         String[] split = value.split("to");
         this.min = Integer.parseInt(split[0]);
         this.max = Integer.parseInt(split[1]);
         this.op = RangedInt.Operation.RANGE;
      } else if (!value.startsWith("-") && value.contains("-")) {
         String[] split = value.split("-");
         this.min = Integer.parseInt(split[0]);
         this.max = Integer.parseInt(split[1]);
         this.op = RangedInt.Operation.RANGE;
      } else {
         this.min = Integer.parseInt(value);
         this.max = this.min;
         this.op = RangedInt.Operation.EQUALS;
      }

      if (this.min > this.max) {
         int tmp = this.min;
         this.min = this.max;
         this.max = tmp;
      }

      if (squared) {
         this.min = this.min * this.min;
         this.max = this.max * this.max;
      }
   }

   public RangedInt.Operation getOperation() {
      return this.op;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof Number)) {
         throw new IllegalArgumentException("Can only compare to a Number");
      } else {
         int d = ((Number)o).intValue();
         switch (this.op) {
            case EQUALS:
               return d == this.min;
            case GREATER_THAN:
               return d > this.min;
            case GREATER_THAN_OR_EQUAL:
               return d >= this.min;
            case LESS_THAN:
               return d < this.max;
            case LESS_THAN_OR_EQUAL:
               return d <= this.max;
            case RANGE:
               return d >= this.min && d <= this.max;
            default:
               return false;
         }
      }
   }

   @Override
   public String toString() {
      return "RangedInt{min=" + this.min + ", max=" + this.max + ", op=" + this.op + "}";
   }

   public static enum Operation {
      EQUALS,
      GREATER_THAN,
      GREATER_THAN_OR_EQUAL,
      LESS_THAN,
      LESS_THAN_OR_EQUAL,
      RANGE;
   }
}
