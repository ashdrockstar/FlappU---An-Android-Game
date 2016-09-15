package com.aishwarypramanik.flappu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
//	Texture[] birds;
	Texture[] heli;
	Texture bottomTube;
	Texture topTube;
	Texture gameOver;
	Texture city;

	int flapState;
	float birdY;
	float velocity;
	int gameState;
	float gravity;
	float gap;
	float maxTubeOffset;
	Random randomGenerator;
	float[] tubeOffset;
	float tubeVelocity;
	float[] tubeX;
	int numberOfTubes;
	float distanceBetweenTubes;

	ShapeRenderer shapeRenderer;
	Circle birdCircle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	int score;
	int currentTube;
	BitmapFont font;
	BitmapFont copyRightMark;

	Music gameMusic;
	Music gameOverMusic;
	@Override
	public void create () {
		init();
	}

	public void init()
	{

		batch = new SpriteBatch();
		background=new Texture("background1.jpg");
//		background=new Texture("bg.png");
//		birds=new Texture[2];
//		birds[0]=new Texture("bird.png");
//		birds[1]=new Texture("bird2.png");
		heli=new Texture[4];
		heli[0]=new Texture("h1.png");
		heli[1]=new Texture("h2.png");
		heli[2]=new Texture("h3.png");
		heli[3]=new Texture("h4.png");

		bottomTube=new Texture("bottomtube.png");
		topTube=new Texture("toptube.png");
		gameOver=new Texture("gameover2.png");
		randomGenerator=new Random();
		flapState=0;
		birdY=0;
		velocity=0;
		gameState=0;
		gravity=10;
		birdY = Gdx.graphics.getHeight() / 2 - heli[0].getHeight() / 2;
		gap=275;
		maxTubeOffset= Gdx.graphics.getHeight()/2-gap/2-100;
		tubeVelocity=8;
		numberOfTubes=4;
		tubeX=new float[numberOfTubes];
		distanceBetweenTubes=Gdx.graphics.getBackBufferWidth()*2/3+50;
		tubeOffset=new float[numberOfTubes];
		tubeX[0]=Gdx.graphics.getWidth() - topTube.getWidth()+distanceBetweenTubes/2;
		for(int i=1;i<numberOfTubes;i++) {
			tubeX[i] =tubeX[i-1]+distanceBetweenTubes;
			tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
		}
		birdCircle=new Circle();
		topTubeRectangles=new Rectangle[numberOfTubes];
		bottomTubeRectangles=new Rectangle[numberOfTubes];
		shapeRenderer=new ShapeRenderer();
		score=0;
		currentTube=0;
		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().scale(6);
		copyRightMark=new BitmapFont();
		copyRightMark.setColor(Color.GOLD);
		copyRightMark.getData().scale(0.5f);

		gameMusic=Gdx.audio.newMusic(Gdx.files.internal("creephollow.mp3"));
		gameOverMusic=Gdx.audio.newMusic(Gdx.files.internal("monster.mp3"));
		gameMusic.play();

		city=new Texture("city.png");
	}
	@Override
	public void render () {
		if(birdY<-heli[0].getHeight()) {
			gameState=2;
		}

		if (Gdx.input.justTouched() )
		{
			if (gameState==2) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				gameOverMusic.stop();
				init();
			}
//			Gdx.app.log("Touched","Screen");
			if ((gameState==1))
				velocity=-35;
			if (gameState==0) {
				gameState = 1;

			}

		}
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.draw(city,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight()/5);

		if(gameState==1) {

			for(int i=0;i<numberOfTubes;i++) {
				tubeX[i] -= tubeVelocity;
				if(tubeX[i]<0-topTube.getWidth()) {
					if (i - 1 < 0)
						tubeX[i] = tubeX[numberOfTubes - 1] + distanceBetweenTubes;
					else
						tubeX[i] = tubeX[i - 1] + distanceBetweenTubes;
					tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
				}
				batch.draw(topTube,
						tubeX[i],
						Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube,
						tubeX[i],
						Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i]);

				topTubeRectangles[i]=new Rectangle(tubeX[i],
						Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]+20,
						topTube.getWidth(),
						topTube.getHeight());
				bottomTubeRectangles[i]=new Rectangle(tubeX[i],
						Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i]-20,
						bottomTube.getWidth(),
						bottomTube.getHeight());
				if ((topTubeRectangles[currentTube].getX()+topTube.getWidth())<birdCircle.x) {
					score += 10;
					currentTube=(currentTube+1)%numberOfTubes;
					Gdx.app.log("Score",String.valueOf(score));
				}
			}
			velocity+=gravity;
			birdY -= velocity;
//			if (flapState == 0) {
//				try {
//					Thread.sleep(50);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				flapState = 1;
//			} else {
//				try {
//					Thread.sleep(50);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				flapState = 0;
//			}

			try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				flapState=(flapState+1)%4;

		}

		batch.draw(heli[flapState],
				Gdx.graphics.getWidth() / 2 - heli[flapState].getWidth() / 2,
				birdY);
		font.draw(batch,String.valueOf(score),100,200);
		copyRightMark.draw(batch,"@aishwarypramanik",Gdx.graphics.getWidth()-200,50);

		birdCircle.set(Gdx.graphics.getWidth()/2,birdY+heli[flapState].getHeight()/2,heli[flapState].getWidth()/2-20);



		if(gameState==1) {
//			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//			shapeRenderer.setColor(Color.BLUE);
//			shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
			for (int i = 0; i < numberOfTubes; i++) {


//				shapeRenderer.rect(
//						tubeX[i],
//						Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],
//						topTube.getWidth(),
//						topTube.getHeight());
//				shapeRenderer.rect(
//						tubeX[i],
//						Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i],
//						bottomTube.getWidth(),
//						bottomTube.getHeight());
				if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) ||
						Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
					gameState=2;
					break;
				}
			}

//			shapeRenderer.end();
		}
		else if(gameState==2)
		{
			batch.draw(gameOver,30-gameOver.getWidth()/5,Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);
			gameMusic.stop();
			gameOverMusic.play();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gameOverMusic.stop();

		}
		batch.end();

	}

	@Override
	public void dispose () {
		batch.dispose();
		gameMusic.dispose();
		gameOverMusic.dispose();
	}
}
