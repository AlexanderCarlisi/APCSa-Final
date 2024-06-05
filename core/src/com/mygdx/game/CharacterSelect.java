package com.mygdx.game;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

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

        // Add a Listener to a Fighter Button
        // When that Button is clicked, add the Fighter's data to m_fighters

        m_selectionIndexs[0] = 0;
        m_selectionIndexs[1] = 1;

        for (int i = 0; i < m_players; i++) {
            String name = "None";
            float runSpeed = 0;
            float jumpForce = 0;
            float weight = 0;
            FixtureDef fixtureDef = null;
            float width = 0;
            float height = 0;
            Fighter.AttackConfig[] attackConfigs = null;

            switch(m_selectionIndexs[i]) {
                case 0: {
                    name = "Test1";
                    runSpeed = 0.01f;
                    jumpForce = 0.05f;
                    weight = 1f;
                    width = GDXHelper.PTM(15f);
                    height = GDXHelper.PTM(25f);
                    fixtureDef = GDXHelper.generateFixtureDef(1f, 4f, 0f, width, height,
                            MyGdxGame.entityCategory.Fighter.getID(), MyGdxGame.entityCategory.Ground.getID());
                    attackConfigs = new Fighter.AttackConfig[] {
                            // Ground Basic
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Neutral, 1.5f, 1f, 0.3f,
                                    new Vector2(0.2f, 0),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    true, true, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Side, 3, 1f, 0.4f,
                                    new Vector2(0.5f, 0),
                                    new Vector2(GDXHelper.PTM(25), GDXHelper.PTM(10)),
                                    true, true, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Up, 3, 1f, 0.4f,
                                    new Vector2(0, 0.4f),
                                    new Vector2(GDXHelper.PTM(30), GDXHelper.PTM(10)),
                                    false, true, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Down, 3, 1f, 0.4f,
                                    new Vector2(0.4f, -0.35f),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    true, true, 250f),

                            // Air Basic
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Neutral, 1.5f, 1f, 0.3f,
                                    new Vector2(0.2f, 0),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    true, false, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Side, 3, 1f, 0.4f,
                                    new Vector2(0.5f, 0),
                                    new Vector2(GDXHelper.PTM(25), GDXHelper.PTM(10)),
                                    true, false, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Up, 3, 1f, 0.43f,
                                    new Vector2(0, 0.4f),
                                    new Vector2(GDXHelper.PTM(30), GDXHelper.PTM(10)),
                                    false, false, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Down, 3, 1f, 0.6f,
                                    new Vector2(0.4f, -0.35f),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    false, false, 250f),

                            // Specials
                            new Fighter.AttackConfig(Attack.attackType.Special, Attack.direction.Neutral, 1.5f, 2f, 0.3f, 5000,
                                    new Vector2(0.2f, 0),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    new Vector2(0.5f, 0f),
                                    false, false, 350f),
                            new Fighter.AttackConfig(Attack.attackType.Special, Attack.direction.Side, 3, 2f, 0.4f,
                                    new Vector2(0.5f, 0),
                                    new Vector2(GDXHelper.PTM(25), GDXHelper.PTM(10)),
                                    true, false, 400f),
                            new Fighter.AttackConfig(Attack.attackType.Special, Attack.direction.Up, 3, 2f, 0.6f, 100,
                                    new Vector2(0, 0.4f),
                                    new Vector2(GDXHelper.PTM(30), GDXHelper.PTM(10)),
                                    new Vector2(0f, 0.1f),
                                    false, true, 400f),
                            new Fighter.AttackConfig(Attack.attackType.Special, Attack.direction.Down, 3, 2f, 0.6f,
                                    new Vector2(0.4f, -0.35f),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    false, false, 400f),

                            // Smashes
                            new Fighter.AttackConfig(Attack.attackType.Smash, Attack.direction.Side, 10f, 2f, 0.5f,
                                    new Vector2(0.6f, 0f),
                                    new Vector2(GDXHelper.PTM(30f), GDXHelper.PTM(30f)),
                                    true, true, 800f),
                            new Fighter.AttackConfig(Attack.attackType.Smash, Attack.direction.Up, 10f, 2f, 0.8f,
                                    new Vector2(0f, 0.6f),
                                    new Vector2(GDXHelper.PTM(35), GDXHelper.PTM(30)),
                                    false, true, 800f),
                            new Fighter.AttackConfig(Attack.attackType.Smash, Attack.direction.Down, 10f, 2f, 0.8f,
                                    new Vector2(0f, -0.6f),
                                    new Vector2(GDXHelper.PTM(35), GDXHelper.PTM(30)),
                                    false, true, 800f),

                            // Ultimate
                            new Fighter.AttackConfig(Attack.attackType.Ultimate, Attack.direction.Neutral, 50f, 2f, 1f,
                                    new Vector2(0, 0),
                                    new Vector2(30f, 30f),
                                    false, true, 5100f)
                    };
                    break;
                }

                case 1: {
                    name = "Test2";
                    runSpeed = 0.075f;
                    jumpForce = 0.5f;
                    weight = 3.5f;
                    width = GDXHelper.PTM(20f);
                    height = GDXHelper.PTM(35f);
                    fixtureDef = GDXHelper.generateFixtureDef(1f, 4f, 0f, width, height,
                            MyGdxGame.entityCategory.Fighter.getID(), MyGdxGame.entityCategory.Ground.getID());
                    attackConfigs = new Fighter.AttackConfig[]{
                            // Ground Basic
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Neutral, 1.5f, 1f, 0.3f,
                                    new Vector2(0.2f, 0),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    true, true, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Side, 3, 1f, 0.4f,
                                    new Vector2(0.5f, 0),
                                    new Vector2(GDXHelper.PTM(25), GDXHelper.PTM(10)),
                                    true, true, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Up, 3, 1f, 0.4f,
                                    new Vector2(0, 0.4f),
                                    new Vector2(GDXHelper.PTM(30), GDXHelper.PTM(10)),
                                    false, true, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Down, 3, 1f, 0.4f,
                                    new Vector2(0.4f, -0.35f),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    true, true, 250f),

                            // Air Basic
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Neutral, 1.5f, 1f, 0.3f,
                                    new Vector2(0.2f, 0),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    true, false, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Side, 3, 1f, 0.4f,
                                    new Vector2(0.5f, 0),
                                    new Vector2(GDXHelper.PTM(25), GDXHelper.PTM(10)),
                                    true, false, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Up, 3, 1f, 0.43f,
                                    new Vector2(0, 0.4f),
                                    new Vector2(GDXHelper.PTM(30), GDXHelper.PTM(10)),
                                    false, false, 250f),
                            new Fighter.AttackConfig(Attack.attackType.Basic, Attack.direction.Down, 3, 1f, 0.6f,
                                    new Vector2(0.4f, -0.35f),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    false, false, 250f),

                            // Specials
                            new Fighter.AttackConfig(Attack.attackType.Special, Attack.direction.Neutral, 1.5f, 2f, 0.3f, 5000,
                                    new Vector2(0.2f, 0),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    new Vector2(0.5f, 0f),
                                    false, false, 350f),
                            new Fighter.AttackConfig(Attack.attackType.Special, Attack.direction.Side, 3, 2f, 0.4f,
                                    new Vector2(0.5f, 0),
                                    new Vector2(GDXHelper.PTM(25), GDXHelper.PTM(10)),
                                    true, false, 400f),
                            new Fighter.AttackConfig(Attack.attackType.Special, Attack.direction.Up, 3, 2f, 0.6f, 100,
                                    new Vector2(0, 0.4f),
                                    new Vector2(GDXHelper.PTM(30), GDXHelper.PTM(10)),
                                    new Vector2(0f, 0.1f),
                                    false, true, 400f),
                            new Fighter.AttackConfig(Attack.attackType.Special, Attack.direction.Down, 3, 2f, 0.6f,
                                    new Vector2(0.4f, -0.35f),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    false, false, 400f),

                            // Smashes
                            new Fighter.AttackConfig(Attack.attackType.Smash, Attack.direction.Side, 10f, 2f, 0.5f,
                                    new Vector2(0.6f, 0f),
                                    new Vector2(GDXHelper.PTM(30f), GDXHelper.PTM(30f)),
                                    true, true, 800f),
                            new Fighter.AttackConfig(Attack.attackType.Smash, Attack.direction.Up, 10f, 2f, 0.8f,
                                    new Vector2(0f, 0.6f),
                                    new Vector2(GDXHelper.PTM(35), GDXHelper.PTM(30)),
                                    false, true, 800f),
                            new Fighter.AttackConfig(Attack.attackType.Smash, Attack.direction.Down, 10f, 2f, 0.8f,
                                    new Vector2(0f, -0.6f),
                                    new Vector2(GDXHelper.PTM(35), GDXHelper.PTM(30)),
                                    false, true, 800f),

                            // Ultimate
                            new Fighter.AttackConfig(Attack.attackType.Ultimate, Attack.direction.Neutral, 50f, 2f, 1f,
                                    new Vector2(0, 0),
                                    new Vector2(30f, 30f),
                                    false, true, 5100f)
                    };
                    break;
                }
            }

            m_fighters[i] = new Fighter(name, runSpeed, jumpForce, weight, fixtureDef, width, height, attackConfigs);
        }
    }


    public Fighter[] isFinished() {
        for (Fighter fighter : m_fighters)
            if (fighter == null) return null;
        return m_fighters;
    }

}
