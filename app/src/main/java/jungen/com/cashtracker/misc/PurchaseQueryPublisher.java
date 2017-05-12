package jungen.com.cashtracker.misc;

/**
 * Created by Joshua Jungen on 12.05.2017.
 */
public interface PurchaseQueryPublisher {

    void subscribe(PurchaseQuerySubscriber subscriber);
    void unsubscribe(PurchaseQuerySubscriber subscriber);
    void notifyAllQueryChanged();
}
