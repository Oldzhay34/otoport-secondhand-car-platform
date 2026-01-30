package com.example.otoportdeneme.Enums;

public enum SubscriptionPlan {
    BASIC(10, 0, 1),
    PLUS(20, 2, 2),
    PRO(30, 5, 4);

    public final int listingLimit;
    public final int featuredLimit;
    public final int weight;

    SubscriptionPlan(int listingLimit, int featuredLimit, int weight) {
        this.listingLimit = listingLimit;
        this.featuredLimit = featuredLimit;
        this.weight = weight;
    }
}
