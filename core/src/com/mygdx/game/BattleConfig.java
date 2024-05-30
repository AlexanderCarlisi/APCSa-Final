package com.mygdx.game;

/**
 * Battle Config Class
 * Data about the Battle
 */
public class BattleConfig {

    /** Game mode of the Battle. */
    public enum GameMode {
        FreeForAll,
        Doubles
    }

    public int stocks = 3; // -1 means infinite stocks
    public float timeLimit = -1f; // -1 means no Time Limit. Seconds
    public GameMode gamemode = GameMode.FreeForAll;

    /**
     * Not given a Constructor that sets Variables because
     * Variables should be set separately with a Rules Menu on the Character Select Screen.
     */
    public BattleConfig() {}
}
