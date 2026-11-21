package io.lumine.mythic.bukkit.utils.numbers;

public class RangedLong {
   protected final RangedLong.Operation op;
   protected long min;
   protected long max;

   public RangedLong(String value) {
      this(value, false);
   }

   public RangedLong(String value, boolean squared) {
      if (value.startsWith(">=")) {
         long v = Long.parseLong(value.substring(2));
         this.min = v;
         this.max = Long.MAX_VALUE;
         this.op = RangedLong.Operation.GREATER_THAN_OR_EQUAL;
      } else if (value.startsWith("<=")) {
         long v = Long.parseLong(value.substring(2));
         this.min = Long.MIN_VALUE;
         this.max = v;
         this.op = RangedLong.Operation.LESS_THAN_OR_EQUAL;
      } else if (value.startsWith(">")) {
         long v = Long.parseLong(value.substring(1));
         this.min = v;
         this.max = Long.MAX_VALUE;
         this.op = RangedLong.Operation.GREATER_THAN;
      } else if (value.startsWith("<")) {
         long v = Long.parseLong(value.substring(1));
         this.min = Long.MIN_VALUE;
         this.max = v;
         this.op = RangedLong.Operation.LESS_THAN;
      } else if (value.contains("to")) {
         String[] split = value.split("to");
         this.min = Long.parseLong(split[0]);
         this.max = Long.parseLong(split[1]);
         this.op = RangedLong.Operation.RANGE;
      } else if (!value.startsWith("-") && value.contains("-")) {
         String[] split = value.split("-");
         this.min = Long.parseLong(split[0]);
         this.max = Long.parseLong(split[1]);
         this.op = RangedLong.Operation.RANGE;
      } else {
         long v = Long.parseLong(value);
         this.min = v;
         this.max = v;
         this.op = RangedLong.Operation.EQUALS;
      }

      if (this.min > this.max) {
         long tmp = this.min;
         this.min = this.max;
         this.max = tmp;
      }

      if (squared) {
         this.min = this.min * this.min;
         this.max = this.max * this.max;
      }
   }

   public RangedLong.Operation getOperation() {
      return this.op;
   }

   public long getMin() {
      return this.min;
   }

   public long getMax() {
      return this.max;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof Number)) {
         return false;
      } else {
         long d = ((Number)o).longValue();
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
      return "RangedLong{min=" + this.min + ", max=" + this.max + ", op=" + this.op + "}";
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
