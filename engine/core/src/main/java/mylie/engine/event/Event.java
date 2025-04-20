package mylie.engine.event;

public interface Event {
    default <T extends Event> T is(Class<T> type){
        return type.isInstance(this) ? type.cast(this) : null;
    }
}
