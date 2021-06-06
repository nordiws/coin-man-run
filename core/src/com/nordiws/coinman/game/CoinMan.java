package com.nordiws.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	Random random;

	//General variables
	SpriteBatch batch;
	Texture background;
	BitmapFont titleFont;
	BitmapFont gameOver;
	BitmapFont generalFont;
	FreeTypeFontGenerator generator;
	FreeTypeFontGenerator.FreeTypeFontParameter parameter;

	//Man variables
	Texture[] man;
	Texture[] manDead;
	Texture manJumpUp;
	Texture manJumpDown;
	int manState = 0;
	int pause = 0;
	float gravity = 0.3f;
	float velocity = 0;
	int manY = 0;
	Rectangle manRectangle;

	//Coin variables
	Texture coin;
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	int coinCount;
	int coinVelocity = 4;
	ArrayList<Rectangle> coinRectangles = new ArrayList<>();

	//Bomb variables
	Texture bomb;
	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	int bombCount;
	int bombVelocity = 6;
	ArrayList<Rectangle> bombRectangles = new ArrayList<>();

	//Score variables
	int score = 0;

	//Games state variable
	int gameState = 0;
	int roundCounter = 0;
	int level1 = 1000;
	int level2 = 3000;
	int level3 = 5000;
	int level4 = 10000;

	@Override
	public void create () {
		//General init variables
		batch = new SpriteBatch();
		background = new Texture("background.png");

		generator = new FreeTypeFontGenerator(Gdx.files.local("assets/font/prototype.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 100;
		generalFont = generator.generateFont(parameter);
		generalFont.setColor(Color.BLACK);
		generator.dispose();


		generator = new FreeTypeFontGenerator(Gdx.files.local("assets/font/game-on.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 280;
		titleFont = generator.generateFont(parameter);
		titleFont.setColor(Color.GOLD);
		generator.dispose();

		generator = new FreeTypeFontGenerator(Gdx.files.local("assets/font/game-on.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 280;
		gameOver = generator.generateFont(parameter);
		gameOver.setColor(Color.RED);
		generator.dispose();


		//Man init variables
		man = new Texture[4];
		man[0] = new Texture("man-run-frame-1.png");
		man[1] = new Texture("man-run-frame-2.png");
		man[2] = new Texture("man-run-frame-3.png");
		man[3] = new Texture("man-run-frame-4.png");
		manJumpUp = new Texture("man-jump-up.png");
		manJumpDown = new Texture("man-jump-fall.png");
		manDead = new Texture[4];
		manDead[0] = new Texture("man-dead-frame-1.png");
		manDead[1] = new Texture("man-dead-frame-2.png");
		manDead[2] = new Texture("man-dead-frame-1.png");
		manDead[3] = new Texture("man-dead-frame-2.png");
		manY = Gdx.graphics.getHeight() / 2;

		//Coin and bomb init variables
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");

		//Score init variables
		random = new Random();
	}

	public void makeCoin(){
		float height = random.nextFloat() * (Gdx.graphics.getHeight() -100);
		coinYs.add((int) height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float height = random.nextFloat() * (Gdx.graphics.getHeight() -100);
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	public void renderCoins(){
		if(coinCount < 100 ){
			coinCount++;
		} else {
			coinCount = 0;
			makeCoin();
		}
		coinRectangles.clear();
		for(int i=0; i<coinYs.size(); i++){
			batch.draw(coin, coinXs.get(i), coinYs.get(i));
			if (roundCounter < level1) {
				coinXs.set(i, coinXs.get(i) - coinVelocity);
			} else if (roundCounter < level2) {
				coinXs.set(i, coinXs.get(i) -coinVelocity*2);
			} else if (roundCounter < level3){
				coinXs.set(i, coinXs.get(i) -coinVelocity*3);
			} else if (roundCounter < level4){
				coinXs.set(i, coinXs.get(i) - coinVelocity*4);
			}
			coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
		}
	}

	public void renderBombs(){
		if(bombCount < 300 ){
			bombCount++;
		} else {
			bombCount = 0;
			makeBomb();
		}

		bombRectangles.clear();
		for(int i=0; i<bombYs.size(); i++){
			batch.draw(bomb, bombXs.get(i), bombYs.get(i));
			if (roundCounter < level1) {
				bombXs.set(i, bombXs.get(i) -bombVelocity);
			} else if (roundCounter < level2) {
				bombXs.set(i, bombXs.get(i) -bombVelocity*2);
			} else if (roundCounter < level3){
				bombXs.set(i, bombXs.get(i) -bombVelocity*3);
			} else if (roundCounter < level4){
				bombXs.set(i, bombXs.get(i) -bombVelocity*4);
			}
			bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
		}
	}

	public void renderMan(){
		if(Gdx.input.justTouched()){
			velocity = -12;
		}

		if(pause < 4){
			pause ++;
		} else {
			pause = 0;
			if (manState < 3) {
				manState++;
			} else {
				manState = 0;
			}
		}

		velocity += gravity;
		manY -= velocity;

		if (gameState == 1) {
			if (manY <= 40) {
				manY = 40;
				batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - 400 - man[manState].getWidth() / 2, manY);
			} else {
				if (velocity < 0){
					batch.draw(manJumpUp, Gdx.graphics.getWidth() / 2 - 400 - man[manState].getWidth() / 2, manY);
				} else {
					batch.draw(manJumpDown, Gdx.graphics.getWidth() / 2 - 400 - man[manState].getWidth() / 2, manY);
				}

			}
			manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - 400 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());
		} else if (gameState == 2){
			batch.draw(manDead[manState], Gdx.graphics.getWidth() / 2 - 400 - man[manState].getWidth() / 2, 50);
		}
	}

	public void checkCoinBombCollision(){
		for (int i=0; i<coinRectangles.size(); i++){
			if(Intersector.overlaps(manRectangle, coinRectangles.get(i))){
				score++;
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		for (int i=0; i<bombRectangles.size(); i++){
			if(Intersector.overlaps(manRectangle, bombRectangles.get(i))){
				gameState = 2;
			}
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		switch (gameState){
			case 0:
				//Waiting to start
				titleFont.draw(batch, "COIN MAN RUN", 120, 700);
				generalFont.draw(batch, "START!", 900, 400);
				if(Gdx.input.justTouched()){
					gameState = 1;
				}
				break;
			case 1:
				//In game
				roundCounter++;
				renderMan();
				renderCoins();
				renderBombs();
				checkCoinBombCollision();
				generalFont.draw(batch, String.valueOf(score), 1900, 1000);
				break;
			case 2:
				//Game over
				gameOver.draw(batch, "GAME OVER", 360, 800);
				generalFont.draw(batch, "Total score: "+ score, 990, 400);
				generalFont.draw(batch, "RESTART!", 1000, 250);
				renderMan();
				if(Gdx.input.justTouched()){
					manY = Gdx.graphics.getHeight() / 2;
					score = 0;
					velocity = 0;
					coinXs.clear();
					coinYs.clear();
					coinRectangles.clear();
					coinCount = 0;
					bombXs.clear();
					bombYs.clear();
					bombRectangles.clear();
					bombCount = 0;
					gameState = 1;
					roundCounter = 0;
				}
				break;
		}

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
