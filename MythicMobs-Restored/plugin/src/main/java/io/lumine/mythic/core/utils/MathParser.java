package io.lumine.mythic.core.utils;

public class MathParser {
   public static double evalChance(String strValue) {
      double amount = 0.0;
      if (strValue.endsWith("%")) {
         String a = strValue.substring(0, strValue.length() - 1);
         amount = Double.valueOf(a) / 100.0;
      } else {
         amount = Double.valueOf(strValue);
      }

      return amount;
   }

   public static double eval(final String str) {
      class Parser {
         int pos = -1;
         int c;

         void eatChar() {
            this.c = ++this.pos < str.length() ? str.charAt(this.pos) : -1;
         }

         void eatSpace() {
            while (Character.isWhitespace(this.c)) {
               this.eatChar();
            }
         }

         double parse() {
            this.eatChar();
            double v = this.parseExpression();
            if (this.c != -1) {
               throw new RuntimeException("Unexpected: " + (char)this.c);
            } else {
               return v;
            }
         }

         double parseExpression() {
            double v = this.parseTerm();

            while (true) {
               this.eatSpace();
               if (this.c == 43) {
                  this.eatChar();
                  v += this.parseTerm();
               } else {
                  if (this.c != 45) {
                     return v;
                  }

                  this.eatChar();
                  v -= this.parseTerm();
               }
            }
         }

         double parseTerm() {
            double v = this.parseFactor();

            while (true) {
               this.eatSpace();
               if (this.c == 47) {
                  this.eatChar();
                  v /= this.parseFactor();
               } else {
                  if (this.c != 42 && this.c != 40) {
                     return v;
                  }

                  if (this.c == 42) {
                     this.eatChar();
                  }

                  v *= this.parseFactor();
               }
            }
         }

         double parseFactor() {
            boolean negate = false;
            this.eatSpace();
            if (this.c == 43 || this.c == 45) {
               negate = this.c == 45;
               this.eatChar();
               this.eatSpace();
            }

            double v;
            if (this.c == 40) {
               this.eatChar();
               v = this.parseExpression();
               if (this.c == 41) {
                  this.eatChar();
               }
            } else {
               StringBuilder sb = new StringBuilder();

               while (this.c >= 48 && this.c <= 57 || this.c == 46) {
                  sb.append((char)this.c);
                  this.eatChar();
               }

               if (sb.length() == 0) {
                  throw new RuntimeException("Unexpected: " + (char)this.c);
               }

               v = Double.parseDouble(sb.toString());
            }

            this.eatSpace();
            if (this.c == 94) {
               this.eatChar();
               v = Math.pow(v, this.parseFactor());
            }

            if (negate) {
               v = -v;
            }

            return v;
         }
      }

      return new Parser().parse();
   }
}
