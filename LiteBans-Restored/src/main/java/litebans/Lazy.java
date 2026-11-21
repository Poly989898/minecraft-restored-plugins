package litebans;

public interface Lazy {
   Object getValue();

   boolean isInitialized();
}
