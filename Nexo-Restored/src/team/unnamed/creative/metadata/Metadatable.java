package team.unnamed.creative.metadata;

public interface Metadatable {
   Metadata meta();

   Metadatable meta(Metadata var1);

   default boolean hasMetadata() {
      return !this.meta().parts().isEmpty();
   }
}
