package litebans;

public final class EmptyMarker extends ResultException {
   public EmptyMarker() {
      super(Message.ERROR_NO_LOGINRECORD);
   }
}
