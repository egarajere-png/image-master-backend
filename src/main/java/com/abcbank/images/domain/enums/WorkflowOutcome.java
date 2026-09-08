package com.abcbank.images.domain.enums;

/** The two ways a Transition can end an item's journey instead of 
 * moving it to another queue — mirrors ItemStatus's terminal values. */

public enum WorkflowOutcome {

    COMPLETED,

    REMOVED
}