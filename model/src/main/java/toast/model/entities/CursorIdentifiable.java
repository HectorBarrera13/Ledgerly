package toast.model.entities;

/**
 * Marker interface for entities that can be identified by a cursor ID.
 */
public interface CursorIdentifiable<C> {
    C getCursorId();
}
