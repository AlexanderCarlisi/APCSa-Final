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
                    runSpeed = 0.05f;
                    jumpForce = 0.3f;
                    weight = 1f;
                    width = GDXHelper.PTM(15f);
                    height = GDXHelper.PTM(25f);
                    fixtureDef = GDXHelper.generateFixtureDef(1f, 4f, 0f, width, height,
                            MyGdxGame.entityCategory.Fighter.getID(), MyGdxGame.entityCategory.Ground.getID());
                    attackConfigs = new Fighter.AttackConfig[] {
                            new Fighter.AttackConfig(Attack.direction.Neutral, 1.5f, 1f,
                                    new Vector2(0.2f, 0),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    false, true, true, false, 500f),
                            new Fighter.AttackConfig(Attack.direction.Side, 3, 1f,
                                    new Vector2(0.5f, 0),
                                    new Vector2(GDXHelper.PTM(25), GDXHelper.PTM(10)),
                                    false, true, true, false, 500f
                            ),
                            new Fighter.AttackConfig(Attack.direction.Up, 3, 1f,
                                    new Vector2(0, 0.4f),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(10)),
                                    false, false, true, false, 500f),
                            new Fighter.AttackConfig(Attack.direction.Down, 3, 1f,
                                    new Vector2(0.4f, -0.35f),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    false, true, true, false, 500f)
                    };
                    break;
                }

                case 1: {
                    name = "Test2";
                    runSpeed = 0.075f;
                    jumpForce = 0.5f;
                    weight = 1.5f;
                    width = GDXHelper.PTM(20f);
                    height = GDXHelper.PTM(35f);
                    fixtureDef = GDXHelper.generateFixtureDef(1f, 4f, 0f, width, height,
                            MyGdxGame.entityCategory.Fighter.getID(), MyGdxGame.entityCategory.Ground.getID());
                    attackConfigs = new Fighter.AttackConfig[] {
                            new Fighter.AttackConfig(Attack.direction.Neutral, 1.5f, 0.3f,
                                    new Vector2(0.2f, 0),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    false, true, true, false, 500f),
                            new Fighter.AttackConfig(Attack.direction.Side, 3, 0.6f,
                                    new Vector2(0.5f, 0),
                                    new Vector2(GDXHelper.PTM(25), GDXHelper.PTM(10)),
                                    false, true, true, false, 500f
                            ),
                            new Fighter.AttackConfig(Attack.direction.Up, 3, 1.5f,
                                    new Vector2(0, 0.4f),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(10)),
                                    false, false, true, false, 500f),
                            new Fighter.AttackConfig(Attack.direction.Down, 3, 1.2f,
                                    new Vector2(0.4f, -0.35f),
                                    new Vector2(GDXHelper.PTM(20), GDXHelper.PTM(15)),
                                    false, true, true, false, 500f)
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
