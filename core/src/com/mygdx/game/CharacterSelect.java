package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CharacterSelect {

    private Fighter[] m_fighters;
    private int m_players;
    private int[] m_selectionIndexs;

    /**
     * Creating the Object Opens the Screen on the App,
     * Once the characters are selected for each player, the isFinished
     * method will return the Fighters selected.
     */
    public CharacterSelect() {
        // Blank for now, to be implemented
        m_players = 2;
        m_fighters = new Fighter[2];
        m_selectionIndexs = new int[m_players];

        // Setup Json Config for Fighter Data
        Json json = new Json();
        json.setUsePrototypes(false);
        json.setOutputType(JsonWriter.OutputType.json);
        json.setElementType(Fighter.FighterConfig.class, "attackConfigs", Fighter.AttackConfig.class);

        // Add a Listener to a Fighter Button
        // When that Button is clicked, add the Fighter's data to m_fighters

        // Tests Hardcoded selection of Fighters for now.
        m_selectionIndexs[0] = 0;
        m_selectionIndexs[1] = 1;

        for (int i = 0; i < m_players; i++) {
            m_fighters[i] = getFighter(json, m_selectionIndexs[i]);
        }

        // CODE TO CONVERT a Fighter made in Code into a JSON.
//
//                try {
//                    FileWriter writer = new FileWriter(Gdx.files.internal("Fighters/Fighter1.json").file());
//                    writer.write(json.toJson(new Fighter.FighterConfig(name, runSpeed, jumpForce, weight, width, height, attackConfigs)));
//                    writer.close();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                System.exit(0);
    }


    /**
     * Parses Fighters Folder for JSON
     *
     * @param json configuration Json object
     * @param i Fighter index number
     * @return Fighter Class created from JSON data
     */
    private Fighter getFighter(Json json, int i) {
        // Get Fighter Data
        FileHandle fh = Gdx.files.internal("Fighters/Fighter"+i+".json");
        Fighter.FighterConfig config = json.fromJson(Fighter.FighterConfig.class, fh);
        return new Fighter(config);
    }


    /**
     * If all players have selected a Fighter.
     * Once all Fighters have been Selected this Method will return their Chosen fighters
     * in order of their Player index.
     *
     * @return null if not finished, and Fighter[] once done.
     */
    public Fighter[] isFinished() {
        for (Fighter fighter : m_fighters)
            if (fighter == null) return null;
        return m_fighters;
    }
}
