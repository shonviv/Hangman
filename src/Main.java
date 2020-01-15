import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import com.engine.core.*;
import com.engine.core.gfx.*;

import javax.swing.*;

import static java.lang.Character.toUpperCase;

public class Main extends AbstractGame
{
	//Required Basic Game Functional Data
	private static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	private static int screenWidth = device.getDisplayMode().getWidth();
	private static int screenHeight = device.getDisplayMode().getHeight();
	
	//Required Basic Game Visual data used in main below
	private static String gameName = "My Game";
	private static int windowWidth = 768;	//For fullscreen mode set these next two to screenWidth and screenHeight
	private static int windowHeight = 544;
	private static int fps = 60;

	public static int mode = 0;

	Font msgFont = new Font("Commodore 64 Pixelized", Font.PLAIN, 20);
	SpriteSheet playScreenImg;
	SpriteSheet characterImg;
	SpriteSheet selectScreenImg;

	SpriteSheet loadingScreenImg;
	SpriteSheet deathScreenImg;

	static List<String> wordList = new ArrayList<String>();
	List<String> usedCharacters = new ArrayList<String>();

	private static int failedGuesses = 0;
	private static String failedGuessStr = "";
	public static String word = "";

	boolean isSinglePlayer = true;
	ChooseWordTextField chooseWord = new ChooseWordTextField();

	File file = new File("/res/words.txt");

	public static void main(String[] args) 
	{
		GameContainer gameContainer = new GameContainer(new Main(), gameName, windowWidth, windowHeight, fps);
		gameContainer.Start();
	}

	@Override
	public void LoadContent(GameContainer gc)
	{
		//TODO: This subprogram automatically happens only once, just before the actual game loop starts.
		//It should never be manually called, the Engine will call it for you.
		//Load images, sounds and set up any data

		loadingScreenImg = new SpriteSheet(LoadImage.FromFile("/images/backgrounds/LoadingScreen.png"), 6, 10, 0, 60, 6);
		loadingScreenImg.destRec = new Rectangle(0, 0, loadingScreenImg.GetFrameWidth() * 2, loadingScreenImg.GetFrameHeight() * 2);
		loadingScreenImg.StartAnimation(1);

		deathScreenImg = new SpriteSheet(LoadImage.FromFile("/images/backgrounds/DeathScreen.png"), 6, 10, 0, 60, 6);
		deathScreenImg.destRec = new Rectangle(0, 0, deathScreenImg.GetFrameWidth() * 2, deathScreenImg.GetFrameHeight() * 2);
		deathScreenImg.StartAnimation(1);

		selectScreenImg = new SpriteSheet(LoadImage.FromFile("/images/backgrounds/SelectScreen.png"));
		selectScreenImg.destRec = new Rectangle(0, 0, selectScreenImg.GetFrameWidth() * 2, selectScreenImg.GetFrameHeight() * 2);

		playScreenImg = new SpriteSheet(LoadImage.FromFile("/images/backgrounds/PlayScreen.png"));
		playScreenImg.destRec = new Rectangle(0, 0, playScreenImg.GetFrameWidth() * 2, playScreenImg.GetFrameHeight() * 2);

		characterImg = new SpriteSheet(LoadImage.FromFile("/images/sprites/Character.png"));
	}

	@Override
	public void Update(GameContainer gc, float deltaTime) 
	{
		//TODO: Add your update logic here, including user input, movement, physics, collision, ai, sound, etc.

		if (Input.IsKeyPressed(KeyEvent.VK_ENTER))
		{
			if (!isSinglePlayer && mode == 1)
			{
				chooseWord.ChooseWord();
				mode--;
			}
		}

		if (Input.IsKeyPressed(KeyEvent.VK_UP) || Input.IsKeyPressed(KeyEvent.VK_DOWN)) isSinglePlayer = !isSinglePlayer;
		if (Input.IsKeyPressed(KeyEvent.VK_ENTER)) mode++;

		if (Input.IsKeyPressed(KeyEvent.VK_A)) Guess("a");
		if (Input.IsKeyPressed(KeyEvent.VK_B)) Guess("b");
		if (Input.IsKeyPressed(KeyEvent.VK_C)) Guess("c");
		if (Input.IsKeyPressed(KeyEvent.VK_D)) Guess("d");
		if (Input.IsKeyPressed(KeyEvent.VK_E)) Guess("e");
		if (Input.IsKeyPressed(KeyEvent.VK_F)) Guess("f");
		if (Input.IsKeyPressed(KeyEvent.VK_G)) Guess("g");
		if (Input.IsKeyPressed(KeyEvent.VK_H)) Guess("h");
		if (Input.IsKeyPressed(KeyEvent.VK_I)) Guess("i");
		if (Input.IsKeyPressed(KeyEvent.VK_J)) Guess("j");
		if (Input.IsKeyPressed(KeyEvent.VK_K)) Guess("k");
		if (Input.IsKeyPressed(KeyEvent.VK_L)) Guess("l");
		if (Input.IsKeyPressed(KeyEvent.VK_M)) Guess("m");
		if (Input.IsKeyPressed(KeyEvent.VK_N)) Guess("n");
		if (Input.IsKeyPressed(KeyEvent.VK_O)) Guess("o");
		if (Input.IsKeyPressed(KeyEvent.VK_P)) Guess("p");
		if (Input.IsKeyPressed(KeyEvent.VK_Q)) Guess("q");
		if (Input.IsKeyPressed(KeyEvent.VK_R)) Guess("r");
		if (Input.IsKeyPressed(KeyEvent.VK_S)) Guess("s");
		if (Input.IsKeyPressed(KeyEvent.VK_T)) Guess("t");
		if (Input.IsKeyPressed(KeyEvent.VK_U)) Guess("u");
		if (Input.IsKeyPressed(KeyEvent.VK_V)) Guess("v");
		if (Input.IsKeyPressed(KeyEvent.VK_W)) Guess("w");
		if (Input.IsKeyPressed(KeyEvent.VK_X)) Guess("x");
		if (Input.IsKeyPressed(KeyEvent.VK_Y)) Guess("y");
		if (Input.IsKeyPressed(KeyEvent.VK_Z)) Guess("z");
	}

	@Override
	public void Draw(GameContainer gc, Graphics2D gfx) 
	{
		//TODO: Add your draw logic here
		//The only other logic here should be selection logic (everything else should be in Update)

		if (mode == 0) OpeningSequence(gfx);
		if (mode == 1) SelectScreen(gfx);
		if (mode == 2) PlayGame(gfx);
		if (mode == 3) GameOverScreen(gfx);
		if (mode == 4) GameWon(gfx);
	}

	private void OpeningSequence(Graphics2D gfx)
	{
		Draw.Sprite(gfx, loadingScreenImg);
	}

	private void SelectScreen(Graphics2D gfx)
	{
		Draw.Sprite(gfx, selectScreenImg);
		Draw.Text(gfx, (isSinglePlayer == true ? ">" : "") + "ONE PLAYER", windowWidth/2.55f, windowWidth/2.75f, msgFont, Helper.GetColor(163,161,255), 1);
		Draw.Text(gfx, (isSinglePlayer == false ? ">" : "") +  "TWO PLAYER", windowWidth/2.55f, windowWidth/2.5f, msgFont, Helper.GetColor(163,161,255), 1);
	}

	private void PlayGame(Graphics2D gfx)
	{
		isGameOver();

		Draw.Sprite(gfx, playScreenImg);
		DrawCharacter(failedGuesses);
		Draw.Sprite(gfx, characterImg);

		System.out.println(wordList.size());
		String[] arr = wordList.toArray(new String[wordList.size()]);
		Draw.Text(gfx, String.join(" ", arr), windowWidth/11.875f, windowWidth/4.75f, msgFont, Helper.GetColor(163,161,255), 1);
		Draw.Text(gfx, failedGuessStr, windowWidth/2.5f, windowWidth/1.75f, msgFont, Helper.GetColor(163,161,255), 1);
	}

	private void DrawCharacter(int failedGuesses)
	{
		switch (failedGuesses)
		{
			case 0: characterImg.destRec = new Rectangle(68, 353, characterImg.GetFrameWidth() * 2, characterImg.GetFrameHeight() * 2);
					break;
			case 1: characterImg.destRec = new Rectangle(128, 322, characterImg.GetFrameWidth() * 2, characterImg.GetFrameHeight() * 2);
				break;
			case 2: characterImg.destRec = new Rectangle(192, 291, characterImg.GetFrameWidth() * 2, characterImg.GetFrameHeight() * 2);
				break;
			case 3: characterImg.destRec = new Rectangle(255, 258, characterImg.GetFrameWidth() * 2, characterImg.GetFrameHeight() * 2);
				break;
			case 4: characterImg.destRec = new Rectangle(318, 227, characterImg.GetFrameWidth() * 2, characterImg.GetFrameHeight() * 2);
				break;
			case 5: characterImg.destRec = new Rectangle(387, 195, characterImg.GetFrameWidth() * 2, characterImg.GetFrameHeight() * 2);
				break;
			case 6: characterImg.destRec = new Rectangle(451, 162, characterImg.GetFrameWidth() * 2, characterImg.GetFrameHeight() * 2);
				break;
			case 7: characterImg.destRec = new Rectangle(513, 130, characterImg.GetFrameWidth() * 2, characterImg.GetFrameHeight() * 2);
				break;
			case 8: characterImg.destRec = new Rectangle(608, 105, characterImg.GetFrameWidth() * 2, characterImg.GetFrameHeight() * 2);
				break;
		}
	}

	private void GameOverScreen(Graphics2D gfx)
	{
		Draw.Sprite(gfx, deathScreenImg);

	}

	private void GameWon(Graphics2D gfx)
	{

	}

	private void Guess(String strGuess)
	{
		if (!isCharacterUsed(strGuess))
		{
			char guess = strGuess.charAt(0);
			boolean rightGuess = false;

			for (int i = 0; i < word.length(); i++)
			{
				char current = word.charAt(i);
				if (current == guess)
				{
					wordList.set(i, String.valueOf(toUpperCase(guess)));
					rightGuess = true;
				}
			}

			if (!rightGuess)
			{
				failedGuesses++;
				failedGuessStr += strGuess.toUpperCase();
				usedCharacters.add(strGuess);
			}
		}
	}

	private boolean isCharacterUsed(String strGuess)
	{
		if (usedCharacters.contains(strGuess.toLowerCase())) return true;
		return false;
	}

	private void isGameOver()
	{
		System.out.println("dab");

		String[] arr = wordList.toArray(new String[wordList.size()]);

		if (failedGuesses == 8)
		{
			failedGuesses = 0;
			mode++; // ADD REAL MODE HERE LATER
		}
		else if (String.join("", arr).toLowerCase().equals(word))
		{
			mode += 2; // ADD REALL MODE HERE LATER
		}
	}

	public static void InitializeWord()
	{
		for (int i = 0; i < word.length(); i++)
		{
			wordList.add("_");

		}
	}

	public static void GetNewWord()
	{

	}
}

class ChooseWordTextField extends JFrame
{
	JPanel jp = new JPanel();
	JLabel jl = new JLabel();
	JTextField jt = new JPasswordField(30);
	JButton jb = new JButton("Enter");

	public String inputedWord = "";

	public void ChooseWord()
	{
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
		} catch (Exception ignored)
		{

		}

		setTitle("Choose your word!");
		setVisible(true);
		setSize(400, 200);
		setLocationRelativeTo(null);

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		jp.add(jt);

		jt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//String input = jt.getText();
				//jl.setText(input);
			}
		});

		jp.add(jb);
		jb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//String input = jt.getText();
				//jl.setText(input);

				inputedWord = jt.getText();

				Main.mode++;
				Main.word = inputedWord;
				Main.InitializeWord();
				setVisible(false);
				dispose();
			}
		});

		jp.add(jl);
		add(jp);
	}
}
