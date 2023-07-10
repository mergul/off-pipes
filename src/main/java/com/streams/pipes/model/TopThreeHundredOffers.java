package com.streams.pipes.model;

import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class TopThreeHundredOffers implements Iterable<OfferPayload>{
    private final ConcurrentSkipListMap<String, OfferPayload> currentNews = new ConcurrentSkipListMap<>();
    private final ConcurrentSkipListSet<OfferPayload> topHundred = new ConcurrentSkipListSet<>((o1, o2) -> {
        final int result = o2.getCount().compareTo(o1.getCount());
        if (result != 0) {
            return result;
        }
        return o1.getStartDate().compareTo(o2.getStartDate());
    });

    public void add(final OfferPayload payload) {
        if(currentNews.containsKey(payload.getId().toHexString())) {
            topHundred.remove(currentNews.remove(payload.getId().toHexString()));
        }
        topHundred.add(payload);
        currentNews.put(payload.getId().toHexString(), payload);
        if (topHundred.size() > 300) {
            final OfferPayload last = topHundred.last();
            currentNews.remove(last.getId().toHexString());
            topHundred.remove(last);
        }
    }

    public void remove(final OfferPayload value) {
        topHundred.remove(value);
        currentNews.remove(value.getId().toHexString());
    }
    @NonNull
    @Override
    public Iterator<OfferPayload> iterator() {
        return topHundred.iterator();
    }

    public Collection<OfferPayload> getList() {
        return this.topHundred;
    }
}

