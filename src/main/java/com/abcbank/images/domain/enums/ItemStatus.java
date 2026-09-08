package com.abcbank.images.domain.enums;

/** Lifecycle of a workflow item: ACTIVE while moving through queues, then 
 * COMPLETED or REMOVED once a transition resolves to a terminal outcome. */

public enum ItemStatus {

    ACTIVE,

    COMPLETED,

    REMOVED
}