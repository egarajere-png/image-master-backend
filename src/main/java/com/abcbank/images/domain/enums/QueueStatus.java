package com.abcbank.images.domain.enums;

/** Whether a queue currently accepts/shows work. 
 * Deactivating a queue (ACTIVE → INACTIVE) is how an admin retires a stage without deleting its history. */

public enum QueueStatus {

    ACTIVE,

    INACTIVE
}