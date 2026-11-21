package litebans;

class KotlinUtil6 extends KotlinUtil5 {
   public static final Lazy computeLazy(Object obj) {
      return new InitializedLazyImpl(obj);
   }
}
