package com.jakespringer.reagne.test;

public interface Entity {
    /**
     * Called when an entity is added to the world. This method should start all
     * entity streams.
     */
    public void create();

    /**
     * Called when an entity is removed from the world. This method should
     * remove all entity streams by calling Stream#remove().
     */
    public void destroy();
}
