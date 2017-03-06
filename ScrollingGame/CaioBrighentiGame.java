import java.awt.event.KeyEvent;
import java.util.Random;
import java.awt.Color;

public class CaioBrighentiGame {
	
	// set DEMO to false to use your code (true uses DemoGame.class)
	private static final boolean DEMO = false;           
	
	// Game window should be wider than tall:  H_DIM < W_DIM   
	// (more effectively using space)
	// # of cells vertically by default: height of game
	private static final int H_DIM = 25; 
	// # of cells horizontally by default: width of game
	private static final int W_DIM = 10;  
	// default location of asdadthe user at the start of the game
	private static final int U_ROW = 0;
	
	private static final int FACTOR = 3;      // you might change that this
	                           // setting or declaration when working on timing
	                           // (speed up and down the game)
	                           
	                           
	private final String USER_IMG = "user.gif";    //ADD others
	private final String TEST_IMG = "piece.png";
	private final String BG_IMG = "background.jpg";
	                        
	private static Random rand = new Random();  //USE ME 
	                                          // don't instantiate me every frame
	
	private GameGrid grid; // graphical window to display the game (the VIEW)
	                       // a 2D game made of two dimensional array of Cells
	private int userRow;
	
	private long msElapsed;		// game clock: number of ms since start of
	                          // game main loop---see play() method
	private int timerClicks;	// used to space animation and key presses
	private int pauseTime;		// to control speed of game

	private Piece activePiece = null;

	private boolean gameOver = false;

	private int gameScore;

	
	public CaioBrighentiGame() {
		this(H_DIM, W_DIM, U_ROW);
	}
	
	public CaioBrighentiGame(int hdim, int wdim, int urow) {
		// to be filled
		init(hdim, wdim, urow);
	}
	
	private void init(int hdim, int wdim, int urow) {  
		
		/* initialize the game window

				NOTE: look at the various constructors to see what you can do!
				For example:
					grid = new GameGrid(hdim, wdim, Color.MAGENTA);
					
				You need to use the one that takes an image to implement the 
				splashscreen functionality (don't start there, but do it as your
				first extension)
		*/
		grid = new GameGrid(hdim, wdim);   
		
		/* initialize other aspects of game state */

		// animation settings
		timerClicks = 0;
		msElapsed = 0;
		pauseTime = 100;

		// store and initialize user position
		userRow = urow;
		//grid.setCellImage(new Location(userRow, 0), USER_IMG);
		
		updateTitle();

		grid.setGameBackground(BG_IMG);
		
	}
	
	public void play() {
		
		/* main game loop */
		while (!isGameOver()) {
			
			grid.pause(pauseTime); 	 // pause for some time (smooth animation)
			msElapsed += pauseTime;	 // count the total amount of time elapsed
			timerClicks++;				 // increment the timer count
		
			handleKeyPress();        // update state based on user key press
			
			handleMouseClick();      // when the game is running: 
			                         // click & read the console output 
			
			
			if (timerClicks % FACTOR == 0) {  // if it's the FACTOR timer tick
				                            // constant 3 initially
				//populateRightEdge();
				if (activePiece ==  null)
					addPiece();
				else
				scrollDown();


			}
			
			updateTitle();
			msElapsed += pauseTime;
		}

		System.out.println("GET FUCKED YOU LOST");
	}
	
	public void handleMouseClick() {
		
		Location loc = grid.checkLastLocationClicked();
		
		if (loc != null)
			System.out.println("You clicked on a square " + loc);
			//System.out.println(grid.getCellImage(loc));
	
	}

	
	public void handleKeyPress() {
			
		int key = grid.checkLastKeyPressed();
		
		//use Java constant names for key presses
		//http://docs.oracle.com/javase/7/docs/api/constant-values.html#java.awt.event.KeyEvent.VK_DOWN
		
		// Q for quit
		if (key == KeyEvent.VK_Q)
			System.exit(0);
		
		else if (key == KeyEvent.VK_S)
			System.out.println("could save the screen: add the call");
		
		/* To help you with step 9: 
		   use the 'T' key to help you with implementing speed up/slow down/pause
    	   this prints out a debugging message */
		else if (key == KeyEvent.VK_T)  {
			boolean interval =  (timerClicks % FACTOR == 0);
			System.out.println("pauseTime " + pauseTime + " msElapsed reset " + 
				msElapsed + " interval " + interval);
		} 
		else if (key == KeyEvent.VK_LEFT) {
			// Avoid run time error by checking if piece exists
			if (activePiece == null)
				return;		
			hidePiece();
			activePiece.moveLeft(grid);
			drawPiece();
		}
		else if (key == KeyEvent.VK_RIGHT){
			// Avoid run time error by checking if piece exists
			if (activePiece == null)
				return;		
			hidePiece();
			activePiece.moveRight(grid);
			drawPiece();
		} 
		else if (key == KeyEvent.VK_UP) {
			if (activePiece == null)
				return;		
			hidePiece();
			activePiece.rotate(grid);
			drawPiece();
		}
		else if (key == KeyEvent.VK_DOWN) {
			if (activePiece == null)
				return;		
			hidePiece();
			activePiece.scroll(grid);
			drawPiece();
		}
	}
	
	// Missing randomization
	// update game state to reflect adding in new cells in the right-most column 
	public void addPiece() {
		// Randomize color
		int r = rand.nextInt(8);
		Color piececolor = Color.BLACK;
		if (r == 0)
			piececolor = Color.BLUE;
		else if (r == 1)
			piececolor = Color.CYAN;
		else if (r == 2)
			piececolor = Color.GREEN;
		else if (r == 3)
			piececolor = Color.MAGENTA;
		else if (r == 4)
			piececolor = Color.ORANGE;
		else if (r == 5)
			piececolor = Color.PINK;
		else if (r == 6)
			piececolor = Color.RED;
		else if (r == 7)
			piececolor = Color.YELLOW;

		//Randomize location
		r = rand.nextInt(grid.getNumCols() - 3);
		Location pieceloc = new Location(0, r);

		// Randomize piece
		r = rand.nextInt(4);
		if (r == 0)
			activePiece = new L(pieceloc, piececolor);
		else if (r == 1)
			activePiece = new Square(pieceloc, piececolor);
		else if (r == 2)
			activePiece = new Cobra(pieceloc, piececolor);
		else if (r == 3)
			activePiece = new Worm(pieceloc, piececolor);
		drawPiece();	
	}

	public void hidePiece(){
		if (activePiece != null){
			for (int i = 0; i < activePiece.getLocs().length; i++ ) {
				grid.setCellImage(activePiece.getLocs()[i], null);
				grid.setColor(activePiece.getLocs()[i], null);
			}
		}
	}

	public void drawPiece(){
		if (activePiece != null) {
			for (int i = 0; i < activePiece.getLocs().length; i++ ) {
				grid.setCellImage(activePiece.getLocs()[i], TEST_IMG);
				grid.setColor(activePiece.getLocs()[i], activePiece.getColor());
			}
		}
	}
	
	// updates the game state to reflect scrolling left by one column 
	public void scrollDown() {
		//grid.setCellImage(activePiece.getLoc(), null);	
		hidePiece();
		if (!activePiece.scroll(grid)) {
			drawPiece();
			activePiece = null;
			checkRows();
			gameOver = checkEndGame();
		} else
			drawPiece();
	}

	public void checkRows(){
		// Iterate through each col
		for (int row = 0; row < grid.getNumRows(); row++) {
			// For each row, check if it is full and if so clear it and add points
			// After clearing, lower all blocks above and call CheckRows() again
			if (checkFullRow(row)){
				clearRow(row);
				addScore(grid.getNumCols());
				lowerBlocks(row);
				checkRows();
				break;
			}
		}
	}

	public boolean checkFullRow(int rownumber){
		for (int col = 0; col < grid.getNumCols(); col++) {
			if (grid.getCellImage(new Location(rownumber, col)) != TEST_IMG)
				return false;
		}
		return true;
	}

	public void clearRow(int rownumber){
		for (int col = 0; col < grid.getNumCols(); col++) {
			grid.setCellImage(new Location(rownumber, col), null);
			grid.setColor(new Location(rownumber, col), null);
		}
	}

	// Method to have blocks above a cleared row "fall"
	public void lowerBlocks(int startrow){
		for (int r = startrow - 1; r > 0; r--) {
			for (int c = 0; c < grid.getNumCols(); c++) {
				grid.setColor(new Location(r + 1, c), grid.getColor(new Location(r,c)));
				grid.setCellImage(new Location(r + 1, c), grid.getCellImage(new Location(r,c)));
				grid.setColor(new Location(r,c), null);
				grid.setCellImage(new Location(r,c), null);
			}
		}
	}

	public boolean checkEndGame(){
		for (int i = 0; i < grid.getNumCols(); i++) {
			if (grid.getCellImage(new Location(0, i)) == TEST_IMG)
				return true;
		}
		return false;
	}

	public boolean checkFullCol(int rownumber){
		for (int col = 0; col < grid.getNumCols(); col++) {
			if (grid.getCellImage(new Location(rownumber, col)) != TEST_IMG)
				return false;
		}
		return true;
	}
		
	// Only works for single piece currently
	public void handleCollision(Location loc) {
		
	}
	
	// return the "score" of the game 
	public int getScore() {
		return gameScore;    //dummy for now
	}

	public void addScore(int points){
		gameScore+=points;
	}
	
	
	// update the title bar of the game window 
	public void updateTitle() {
		grid.setTitle("Tetris:  " + getScore());
	}
	
   // return true if the game is finished, false otherwise
	//      used by play() to terminate the main game loop 
	public boolean isGameOver() {
		return gameOver;
	}
	
	public static void run() {
		if (DEMO) {       // reference game: 
			//   - play and observe first the mechanism of the demo 
			//     to understand the basic game 
			//   - go back to the demo anytime you don't know what the
			//     next step to implement and to test are. you should always be 
			//     clear and concrete about the ~5 lines you are trying to code and
			//     how to validate them
			//         figure out according to the game play 
			//         (the sequence of display and action) how the functionality
			//         you are implementing next is supposed to operate
			
			// It's critical to have a plan for each piece of code: follow, understand
			// and study the assignment description details; and explore the basic game. 
			// You should always know what you are doing (your current small goal) 
			// before implementing that piece or talk to us. 
			
			System.out.println("Running the demo: DEMO=" + DEMO);
			//constructor for client to adjust the game window size 
			//TRY different values
			DemoGame game = new DemoGame(5, 10, 0);
			//default constructor   (4 by 10)
			// DemoGame game = new DemoGame();
			game.play();
			
		} else {
			System.out.println("Running Caio student game: DEMO=" + DEMO);
			// !DEMO   -> your code should execute those lines when you are
			// implementing your game
			
			//test 1: with parameterless constructor
			CaioBrighentiGame game = new CaioBrighentiGame();
			
			//test 2: with constructor specifying grid size  
			//IT SHOULD ALSO WORK as long as height < width
			//ScrollingGame game = new ScrollingGame(10, 20, 4);
			
			game.play();
		}
	}
	
	public static void main(String[] args) {
		run();
	}
}