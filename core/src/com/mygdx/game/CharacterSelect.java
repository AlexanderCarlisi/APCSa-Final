package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;


public class CharacterSelect {

    /** Fighter Selection indexes, better than storing Fighters that might get changed in Selector. */
    private final ArrayList<Integer> m_selectionIndexs;

    /** ControllerTypes selected by the Players */
    private final ArrayList<PlayerController.ControllerType> m_controllerTypes;

    /** Number of Players, variable they can be added and removed. */
    private int m_players;

    /** Button to confirm the Players are ready to start the match. */
    private boolean m_ready;


    /**
     * Creating the Object Opens the Screen on the App,
     * Once the characters are selected for each player, the isFinished
     * method will return the Fighters selected.
     */
    public CharacterSelect() {
        m_players = 2;
        m_selectionIndexs = new ArrayList<>();
        m_controllerTypes = new ArrayList<>();

        m_selectionIndexs.add(0, 1);
        m_controllerTypes.add(0, PlayerController.ControllerType.Keyboard);
        m_selectionIndexs.add(1, 0);
        m_controllerTypes.add(1, PlayerController.ControllerType.Controller);
        m_ready = true;

        // Add a Listener to a Fighter Button
        // When that Button is clicked, add the Fighter's data to m_fighters

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
     * If Character Selection is Over
     * Conditions : selectedFighters and Controllers are Equal to the Amount of Players, and they clicked the Ready Button.
     * @return
     */
    public boolean isFinished() {
        return (m_selectionIndexs.size() == m_players && m_controllerTypes.size() == m_players && m_ready);
    }


    /**
     * PRECONDITION : isFinished is true.
     * @return Generated Fighters from Json files.
     */
    public Fighter[] getFighters() {
        if (!isFinished()) return null;

        // Setup Json Config for Fighter Data
        Json json = new Json();
        json.setUsePrototypes(false);
        json.setOutputType(JsonWriter.OutputType.json);
        json.setElementType(Fighter.FighterConfig.class, "attackConfigs", Fighter.AttackConfig.class);

        Fighter[] fighters = new Fighter[m_players];
        for (int i = 0; i < m_players; i++) {
            fighters[i] = getFighter(json, m_selectionIndexs.get(i));
        }
        return fighters;
    }


    /**
     * PRECONDITION : isFinished is true, and getFighters has been called.
     * @param fighters Fighter array given by getFighters()
     * @return
     */
    public PlayerController[] getControllers(Fighter[] fighters) {
        if (!isFinished()) return null;

        PlayerController[] controllers = new PlayerController[m_players];
        for (int i = 0; i < m_players; i++) {
            controllers[i] = new PlayerController(fighters[i], m_controllerTypes.get(i));
            fighters[i].setController(controllers[i]);
        }

        return controllers;
    }
}
