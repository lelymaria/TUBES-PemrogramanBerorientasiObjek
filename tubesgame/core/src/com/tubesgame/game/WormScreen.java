package com.tubesgame.game;

import com.badlogic.gdx.Game;


public class WormScreen extends Game {
    @Override
    public void create(){
        setScreen(new WormMenu(this));
    }
}
