package model;


import gui.LightOutGUI;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.*;

/** Implements the game rules for Lights out. This will be the Model in the MVC paradigm, your GUIs window to the world.
 * In addition to the methods provided, this class provides an iterator that will allow you to loop through every Tile.
 *  displayBoard. See the implementation of {@link tui.LightsOutTUI#displayBoard()} for an example.
 */
public class LightsOutModel implements Iterable<Tile>{
    // Message constants for Views
    /**
     * Message sent when a board has successfully loaded.
     */
    public static String LOADED = "loaded";
    /**
     * Message sent when a board has failed to load.
     */
    public static String LOAD_FAILED = "loadFailed";
    /**
     * The message that will precede a hint.
     */
    public static String HINT_PREFIX = "Hint:";
    private Random rng = null;
    private int moves;
    Board board;

    /**
     *Gets the side length of the board.
     * @return The dimension (side length) of the board this model represents
     */
    public int getDimension(){
        return Board.BOARD_SIZE;
    }

    /**
     * Those objects that are watching this object's every move
     */
    private final List< Observer< LightsOutModel, String > > observers;

    /**
     * Creates a new board (all tiles are off) , initializes observers list
     */
    public LightsOutModel() {
        moves = 0;
        observers = new LinkedList<>();
        board=new Board();
    }

    /**
     * Attempts to load a board from a given file name. It will announce to the observers if it was loaded successfully or not.
     * @param filename The file to load
     * @return True iff loaded successfully
     */
    public boolean loadBoardFromFile(String filename) {
    return loadBoardFromFile(new File(filename));
    }

    /**
     * Attempts to load a board from a file object. It will announce to the observers if it was loaded successfully or not.
     * @param file The file to load
     * @return True iff loaded successfully
     */
    public boolean loadBoardFromFile(File file)  {
        try {
            Scanner in = new Scanner(file);
            Iterator<Tile> it = board.iterator();

            while (it.hasNext()) {
                Tile t = it.next();
                int v = in.nextInt();

                if (v == 1) {
                    t.setOn(true);
                } else if (v == 0) {
                    t.setOn(false);
                } else {
                    announce(LOAD_FAILED);
                    return false; //invalid file
                }
            }
            moves = 0;
            announce(LOADED);

            return true;
        }catch (FileNotFoundException e) {
            announce(LOAD_FAILED);
            return false; //invalid file
        }


    }

    /**
     * Generates a randomized board. Will announce that the board was loaded successfully to every observer
     */
    public void generateRandomBoard(){
        int steps = getRNG().nextInt(6,10);
        generateRandomBoard(steps);

    }
     void generateRandomBoard(int steps){

        List<Tile> unchanged = new ArrayList<>(Board.BOARD_SIZE*Board.BOARD_SIZE);

        for (Tile t : board){
            unchanged.add(t);
        }
        Collections.shuffle(unchanged);
        for (int s = 0; s<steps && s<unchanged.size(); s++) {
            //   Solver.printBoard(board);
            unchanged.get(s).toggle();
        }
        // Solver.printBoard(board);
         moves = 0;
        announce(LOADED);
    }

    private Random getRNG(){
        if (rng == null){
            rng = new Random();
        }
        return rng;
    }

    /**
     * Gets the count of moves made for the current game
     * @return the current number of moves
     */
    public int getMoves(){
        return moves;
    }

    /**
     * Toggles the tiles at (x,y). I.e. changes on to off and off to on.
     * @param x X coordinate
     * @param y Y coordinate
     */
   public void toggleTile(int x, int y){
       board.toggleTile(x,y);
       moves++;
       announce(x +" "+y);
   }

    /**
     * Gives a hint to the user. This requires solving the entire puzzle, it may take a long time or even run out of memory for sufficiently complex puzzles ran on weak baby machines.
     * @return The Tile representing the next move the user should make
     */
   public Tile getHint(){
       List<SearchNode> sPath = Solver.aStar(new LOSearchNode(new Board(this.board)), new Board());
       SearchNode next = sPath.get(1);
       Tile ret = this.board.getTile(next.source.x,next.source.y);
       announce(HINT_PREFIX+" "+ret.getX()+", "+ret.getY());
      // return this.board.getTile(next.source.x,next.source.y);
       return ret;
   }
    /**
     * Tests is the game has been won.
     * @return True iff every tile on the board is off.
     */
    public boolean gameOver(){
        for (Tile t : board){
            if (t.isOn()){
                return  false;
            }
        }
        return true;
    }

    /**
     *Gets the Tile object on the board at (x,y)
     * @param x row
     * @param y column
     * @return The tile at (x,y)
     */
    public Tile getTile(int x, int y){
        return board.getTile(x,y);
    }

    /**
     * Add a new observer to the list for this model
     * @param obs an object that wants an
     *            {@link Observer#update(Object, Object)}
     *            when something changes here
     */
    public void addObserver( Observer< LightsOutModel, String > obs ) {
        this.observers.add( obs );
    }

    /**
     * Tests toggleTile and the iterator.
     * @param args cmd line args
     */
    public static void main(String[] args){
        LightsOutModel l = new LightsOutModel();
        l.toggleTile(0,0);
        for (Tile t : l){
            System.out.println(t);
        }
    }

    private void announce(){
        announce("");
    }
    /**
     * Announce to observers the model has changed;
     */
    private void announce( String arg ) {
        //System.out.println("something was done");
        for ( var obs : this.observers ) {
            obs.update( this, arg );
        }
    }
    @Override
    /**
     * Returns an iterator of Tiles that will move through the board from left to right and  top to bottom. Useful in for loops.
     */
    public Iterator<Tile> iterator() {
        return board.iterator();
    }
}
