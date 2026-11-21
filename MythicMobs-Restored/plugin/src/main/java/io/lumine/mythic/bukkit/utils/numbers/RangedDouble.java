package io.lumine.mythic.bukkit.utils.numbers;

public class RangedDouble {
   protected final RangedDouble.Operation op;
   protected double min;
   protected double max;

   public RangedDouble(String value) {
      this(value, false);
   }

   public RangedDouble(String value, boolean squared) {
      if (value.startsWith(">=")) {
         String s = value.substring(2);
         this.min = Double.parseDouble(s);
         this.max = Double.MAX_VALUE;
         this.op = RangedDouble.Operation.GREATER_THAN_OR_EQUAL;
      } else if (value.startsWith("<=")) {
         String s = value.substring(2);
         this.min = -Double.MAX_VALUE;
         this.max = Double.parseDouble(s);
         this.op = RangedDouble.Operation.LESS_THAN_OR_EQUAL;
      } else if (value.startsWith(">")) {
         String s = value.substring(1);
         this.min = Double.parseDouble(s);
         this.max = Double.MAX_VALUE;
         this.op = RangedDouble.Operation.GREATER_THAN;
      } else if (value.startsWith("<")) {
         String s = value.substring(1);
         this.min = -Double.MAX_VALUE;
         this.max = Double.parseDouble(s);
         this.op = RangedDouble.Operation.LESS_THAN;
      } else if (value.contains("to")) {
         String[] split = value.split("to");
         this.min = Double.parseDouble(split[0]);
         this.max = Double.parseDouble(split[1]);
         this.op = RangedDouble.Operation.RANGE;
      } else if (!value.startsWith("-") && value.contains("-")) {
         String[] split = value.split("-");
         this.min = Double.parseDouble(split[0]);
         this.max = Double.parseDouble(split[1]);
         this.op = RangedDouble.Operation.RANGE;
      } else {
         this.min = Double.parseDouble(value);
         this.max = this.min;
         this.op = RangedDouble.Operation.EQUALS;
      }

      if (this.min > this.max) {
         double tmp = this.min;
         this.min = this.max;
         this.max = tmp;
      }

      if (squared) {
         this.min = this.min * this.min;
         this.max = this.max * this.max;
      }
   }

   public RangedDouble.Operation getOperation() {
      return this.op;
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof Number)) {
         return false;
      } else {
         double d = ((Number)o).doubleValue();
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

   public boolean equalsSquared(Object o) {
      if (!(o instanceof Number)) {
         return false;
      } else {
         double d = ((Number)o).doubleValue();
         double sqMin = Math.pow(this.min, 2.0);
         double sqMax = Math.pow(this.max, 2.0);
         switch (this.op) {
            case EQUALS:
               return d == sqMin;
            case GREATER_THAN:
               return d > sqMin;
            case GREATER_THAN_OR_EQUAL:
               return d >= sqMin;
            case LESS_THAN:
               return d < sqMax;
            case LESS_THAN_OR_EQUAL:
               return d <= sqMax;
            case RANGE:
               return d >= sqMin && d <= sqMax;
            default:
               return false;
         }
      }
   }

   @Override
   public String toString() {
      return "RangedDouble{min=" + this.min + ", max=" + this.max + ", op=" + this.op + "}";
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
