package com.tubesgame.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;

public class WormGames implements Screen{
    public Stage mainStage;
    private Stage uiStage;
    private AnimatedActor mousey;
    private BaseActor cheese;
    private BaseActor floor;
    private BaseActor winText;
    private boolean win;

    private float timeElapsed;
    private Label timeLabel;
    // game world dimensions
    final int MAPWIDTH = 800;
    final int MAPHEIGHT = 800;
    // window dimensions
    final int VIEWWIDTH = 640;
    final int VIEWHEIGHT = 480;
    private final int SPEED = 300;

    public Game game;
    public WormGames(Game g)
    {
        game = g;
        create();
    }

    public void create(){
        timeElapsed = 0 ;
        BitmapFont font = new BitmapFont();
        String text = "Time: 0 ";
        Label.LabelStyle style = new Label.LabelStyle(font , Color.NAVY );
        timeLabel = new Label(text,style);
        timeLabel.setFontScale(2);
        timeLabel.setPosition(500,440);

        win = false;
        mainStage = new Stage();
        uiStage = new Stage();


        floor = new BaseActor();
        floor.setTexture(new Texture(Gdx.files.internal("tanah.png")));
        floor.setPosition(0, 0);
        mainStage.addActor(floor);

        cheese = new BaseActor();
        cheese.setTexture(new Texture(Gdx.files.internal("apel.png")));
        cheese.setOrigin(cheese.getWidth()/2, cheese.getHeight()/2);
        cheese.setPosition(400,300);
        mainStage.addActor(cheese);
        uiStage.addActor(timeLabel);
        mousey = new AnimatedActor();

        TextureRegion[] frames = new TextureRegion[4];
        for (int n = 0; n < 4; n++)
        {
            String fileName = "cacing" + n + ".png";
            Texture tex = new Texture(Gdx.files.internal(fileName));
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            frames[n] = new TextureRegion( tex );
        }
        Array<TextureRegion> framesArray = new Array<TextureRegion>(frames);

        Animation anim = new Animation(0.5f, framesArray, Animation.PlayMode.LOOP_PINGPONG);

        mousey.setAnimation( anim );
        mousey.setOrigin( mousey.getWidth()/2, mousey.getHeight()/2 );
        mousey.setPosition( 20, 20 );
        mainStage.addActor(mousey);

        winText = new BaseActor();
        winText.setTexture(new Texture(Gdx.files.internal("you-win.png")));
        winText.setPosition(170,60);
        winText.setVisible(false);
        uiStage.addActor(winText);


    }

    @Override
    public void render(float delta){
        mousey.velocityX = 0;
        mousey.velocityY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            mousey.velocityX -= SPEED;
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            mousey.velocityX += SPEED;
        else if (Gdx.input.isKeyPressed(Input.Keys.UP))
            mousey.velocityY += SPEED;
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            mousey.velocityY -= SPEED;

        float dt = Gdx.graphics.getDeltaTime();
        mainStage.act(dt);
        uiStage.act(dt);
        if(!win){
            timeElapsed += dt;
            timeLabel.setText( "Time: " + (int)timeElapsed );
        }

        if ( mousey.getX() > MAPWIDTH  - mousey.getWidth()){
            mousey.setX(MAPWIDTH-mousey.getWidth());
        }

        Rectangle cheeseRectangle = cheese.getBoundingRectangle();
        Rectangle mouseyRectangle = mousey.getBoundingRectangle();


        if (!win && cheeseRectangle.overlaps(mouseyRectangle))
        {

            win = true;
            Action spinShrinkFadeOut = Actions.parallel(
                    Actions.alpha(1),
                    Actions.rotateBy(360, 1),
                    Actions.scaleTo(0,0,1),
                    Actions.fadeOut(1)
            );
            cheese.addAction(spinShrinkFadeOut);

            Action fadeInColorCycleForever  = Actions.sequence(
                    Actions.alpha(0),
                    Actions.show(),
                    Actions.fadeIn(2),
                    Actions.forever(Actions.sequence(

                            Actions.color(new Color(1,0,0,1),1),
                            Actions.color(new Color(0,0,1,1),1)
                            )
                    )

            );
            winText.addAction(fadeInColorCycleForever);
        }

        //draw graphics
        Gdx.gl.glClearColor(0.8f, 0.8f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Camera cam = mainStage.getCamera();
        // center camera on player
        cam.position.set( mousey.getX() + mousey.getOriginX(),
                mousey.getY() + mousey.getOriginY(), 0 );
        // bound camera to layout
        cam.position.x = MathUtils.clamp(cam.position.x,
                VIEWWIDTH/2, MAPWIDTH - VIEWWIDTH/2);
        cam.position.y = MathUtils.clamp(cam.position.y,
                VIEWHEIGHT/2, MAPHEIGHT - VIEWHEIGHT/2);
        cam.update();
        mainStage.draw();
        mousey.setX( MathUtils.clamp( mousey.getX(), 0, MAPWIDTH
                - mousey.getWidth() ));
        mousey.setY( MathUtils.clamp( mousey.getY(), 0, MAPHEIGHT
                - mousey.getHeight() ));
        if (Gdx.input.isKeyPressed(Input.Keys.M))
            game.setScreen( new WormMenu(game) );

        uiStage.draw();

    }
    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
