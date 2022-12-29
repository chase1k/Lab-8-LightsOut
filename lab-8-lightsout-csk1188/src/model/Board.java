package model;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A board of NxN tiles. You will likely not need to interact with this directly.
 */
 class Board  implements Iterable<Tile>{

    static final int BOARD_SIZE = 5;
    private final Tile[][] tiles;
     Board() {
       tiles= new Tile[BOARD_SIZE][BOARD_SIZE];
        for(int x = 0; x < Board.BOARD_SIZE; x++){
            for(int y = 0; y < Board.BOARD_SIZE; y++){
                tiles[x][y]= new Tile(x,y,this,false);
            }
        }
    }

     Board(Board b){
        this(); // init array
       //deep copy
        for(int x = 0; x < Board.BOARD_SIZE; x++){
            for(int y = 0; y < Board.BOARD_SIZE; y++){
                tiles[x][y].setOn(b.tiles[x][y].isOn());
            }
        }

    }

    public static void main(String[] args) {
        Board b = new Board();
        Board a = new Board();
        a.toggleTile(1,1);


        System.out.println(a.equals(b));
        for (Tile t : a){
            System.out.println(t);
            for (Tile n :t.getNeighbors()){
                System.out.println("\t"+n);
            }
        }
    }

    Tile[][] getTiles() {
        return tiles;

    }

    Tile getTile(int x, int y) {
            return tiles[x][y];
    }

    void toggleTile(int x, int y) {
        getTile(x, y).toggle();
    }

    @Override
    public Iterator<Tile> iterator() {
        return new BoardIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board tiles1 = (Board) o;
        for( int x = 0; x <BOARD_SIZE; x ++){
            for( int y = 0; y <BOARD_SIZE; y ++){
                //System.out.println(tiles1.tiles[x][y]+" : "+tiles[x][y]);
                if (tiles1.tiles[x][y].isOn() != tiles[x][y].isOn()){

                    return false;
                }

            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        if(tiles == null){
            return 0;
        }
        int ret = 1;
        for(int row = 0; row<BOARD_SIZE; row++) {
            //System.out.println(Arrays.hashCode(tiles[row]));
            ret += 31 * ret + Arrays.hashCode(tiles[row]);
        }
        return ret;
    }

    /**
     * Iterates Left to Right; Top to Bottom
     */
   private class BoardIterator implements Iterator<Tile>{
        int x;
        int y;

        public BoardIterator() {
            x=-1;
            y=0;
        }

        @Override
        public Tile next() {
            if (x>=BOARD_SIZE-1 ){
                y += 1;
                x=0;
            }else{
                x+=1;
            }
           // System.out.printf("%d,%d",x,y);
            return tiles[x][y];
        }

        @Override
        public boolean hasNext() {
            //System.out.printf("HAS NEXT: %2$d < %1$d %3$d < %1$d%n",BOARD_SIZE,x,y);
            return x<BOARD_SIZE-1 || y<BOARD_SIZE-1;
        }

    }
}

