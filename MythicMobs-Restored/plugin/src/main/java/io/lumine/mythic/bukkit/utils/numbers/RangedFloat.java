package io.lumine.mythic.bukkit.utils.numbers;

public class RangedFloat {
   protected final RangedFloat.Operation op;
   protected float min;
   protected float max;

   public RangedFloat(String value) {
      this(value, false);
   }

   public RangedFloat(String value, boolean squared) {
      if (value.startsWith(">=")) {
         float v = Float.parseFloat(value.substring(2));
         this.min = v;
         this.max = Float.MAX_VALUE;
         this.op = RangedFloat.Operation.GREATER_THAN_OR_EQUAL;
      } else if (value.startsWith("<=")) {
         float v = Float.parseFloat(value.substring(2));
         this.min = -Float.MAX_VALUE;
         this.max = v;
         this.op = RangedFloat.Operation.LESS_THAN_OR_EQUAL;
      } else if (value.startsWith(">")) {
         float v = Float.parseFloat(value.substring(1));
         this.min = v;
         this.max = Float.MAX_VALUE;
         this.op = RangedFloat.Operation.GREATER_THAN;
      } else if (value.startsWith("<")) {
         float v = Float.parseFloat(value.substring(1));
         this.min = -Float.MAX_VALUE;
         this.max = v;
         this.op = RangedFloat.Operation.LESS_THAN;
      } else if (value.contains("to")) {
         String[] split = value.split("to");
         this.min = Float.parseFloat(split[0]);
         this.max = Float.parseFloat(split[1]);
         this.op = RangedFloat.Operation.RANGE;
      } else if (!value.startsWith("-") && value.contains("-")) {
         String[] split = value.split("-");
         this.min = Float.parseFloat(split[0]);
         this.max = Float.parseFloat(split[1]);
         this.op = RangedFloat.Operation.RANGE;
      } else {
         float v = Float.parseFloat(value);
         this.min = v;
         this.max = v;
         this.op = RangedFloat.Operation.EQUALS;
      }

      if (this.min > this.max) {
         float tmp = this.min;
         this.min = this.max;
         this.max = tmp;
      }

      if (squared) {
         this.min = this.min * this.min;
         this.max = this.max * this.max;
      }
   }

   public RangedFloat.Operation getOperation() {
      return this.op;
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof Number)) {
         return false;
      } else {
         float d = ((Number)o).floatValue();
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
      return "RangedFloat{min=" + this.min + ", max=" + this.max + ", op=" + this.op + "}";
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
