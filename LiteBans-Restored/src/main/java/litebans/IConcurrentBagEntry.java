package litebans;

public interface IConcurrentBagEntry {
   int intValue = 0;
   int intValue2 = 1;
   int intValue3 = -1;
   int intValue4 = -2;

   boolean compareAndSet(int value, int value2);

   void setState(int value);

   int getState();
}
