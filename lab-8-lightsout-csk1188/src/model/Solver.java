package model;

import java.io.PrintStream;
import java.util.*;

/**
 * Used to provide hints. Go away.
 */

class BoardCovering extends Board {
    public BoardCovering(Board board) {
        super(board);
    }

    public BoardCovering() {
        super();
    }

    void toggleTile(int x, int y) {
        Tile t = getTile(x, y);
        t.setOn(false);
        for (Tile n : t.getNeighbors()) {
            n.setOn(false);
        }
    }
}
class Edge {
    SearchNode source;
    SearchNode sink;
    //tile toggled from parent to child
    int x;
    int y;

    public Edge(SearchNode from, SearchNode to, int x, int y) {
        this.source = from;
        this.sink = to;
        this.x = x;
        this.y = y;
    }

}

abstract class SearchNode implements Comparable<SearchNode>{
    Board state;
    Edge source = null;
    Set<Edge> sinks = new HashSet();
    int g;
    int h;
    int f;
    Set<Pair> usedMoves= new HashSet<>();
    public SearchNode(Board b) {
        state = b;
        source = null;
        sinks = new HashSet<>();
        int g = 0;
        int h = 0;
        int f = 0;

    }

    //== and hash are predicated only on the state. f,h,g, doesn't matter, edges don't either; be careful
    int tilesOnH(){
        h = 0;
        for( Tile t : state){
            if (t.isOn()) {
                h += 1;
            }
        }
        return h;
    }

    int contigH(){
        Queue<Tile> toSearch = new LinkedList<>();
        for(Tile t : state){
            toSearch.add(t);
        }
        int ret = 0;
        while (!toSearch.isEmpty()){
            Tile islandSeet = toSearch.poll();
            if (!islandSeet.isOn()){
                continue;
            }

            Queue<Tile> frontier = new LinkedList<>();
            frontier.add(islandSeet);
            Set<Tile> v = new HashSet<>();
            while(!frontier.isEmpty()){
                Tile curr = frontier.poll();
                v.add(curr);
                for( Tile n : curr.getNeighbors()){
                    if(n.isOn() && !v.contains(n))
                        frontier.add(n);
                }
            }
            ret +=Math.ceil(v.size()/4.0);
            toSearch.removeAll(v);
        }

        return  ret;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || o instanceof SearchNode) return false;
        SearchNode that = (SearchNode) o;

        return state.equals(that.state);
    }

    @Override
    public int hashCode() {


        return Objects.hash(state);
    }

   abstract int calcH();

    int calcF(){
        f = g+calcH();
        return f;
    }

    int calcG(SearchNode parent){
        g = parent.g+1;
        return g;
    }

    void calcValues(SearchNode searchNode) {
        calcG(searchNode);
        calcF();
    }

    abstract  Set<SearchNode> getNeighbors();

    @Override
    public int compareTo(SearchNode o) {
        return f -o.f;
    }

    class Pair{
        int x;
        int y;
        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return x == pair.x && y == pair.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "(" +
                    + x +
                    ", " + y +
                    ')';
        }
    }

}
class LOSearchNode extends SearchNode{

    public LOSearchNode(Board b) {
        super(b);
    }
    public int calcH(){
       h =  tilesOnH(); //practically the fastest heuristic
        return h;
    }


    Set<SearchNode> getNeighbors() {
        Set<SearchNode> ret = new HashSet<>();
        for (int x = 0; x < Board.BOARD_SIZE; x++) {
            for (int y = 0; y < Board.BOARD_SIZE; y++) {
                Pair move = new Pair(x,y);
                if (usedMoves.contains(move)) {

                    continue;
                }

                Board childB = new Board(state);
                childB.toggleTile(x, y);
                SearchNode child = new LOSearchNode(childB);
                Edge e = new Edge(this, child, x, y);
                //  sinks.add(e); // add these in the search iff they aren't visited.
                child.source = e;
                child.calcValues(this);
                child.usedMoves.addAll(usedMoves);
                child.usedMoves.add(move);
                ret.add(child);
            }
        }
        return ret;
    }
}
 class Solver {

     static void printBoard(Board b){
        printBoard( b, System.out);
        }
        public static void printBoard(Board b, PrintStream ps){
        char ONSYMBOL = '●';
        char OFFSYMBOL = '○';
            //formatting for text ui is soooo elegant
            //System.out.print("\033[0;4m"); //turn on underline
            ps.print("  ");
            for(int c =0; c<Board.BOARD_SIZE; c++){
                ps.print(c+" ");
            }
            int currentRow = -1;


            for(Tile t : b){
                if (currentRow!=t.getY()){ //newline for new rows.
                    currentRow=t.getY();
                    ps.printf("%n%d ",currentRow);

                }
                char symbol = OFFSYMBOL;
                if (t.isOn()) {
                    symbol= ONSYMBOL;
                }
                ps.print(symbol+" ");

            }

            ps.println();
            ps.println();
        }
    static List<SearchNode> aStar(SearchNode start, Board goal) {
        PriorityQueue<SearchNode> pq = new PriorityQueue();
        Set<Board> visited = new HashSet<>();
        Map<Board, SearchNode> frontierMap = new HashMap<>();
        pq.add(start);

        int i = 0;

        while (!pq.isEmpty()) {

            SearchNode curr = pq.poll();
            if (curr.state.equals(goal)) {

                SearchNode c = curr;
                LinkedList<SearchNode> path = new LinkedList<>();

                while (c != null){
                    path.add(0,c);
                    if(c.source != null) {

                        c = c.source.source;
                    }else {
                        break;
                    }
                }


                return path;
            }
            if (visited.contains(curr.state)) { //shouldn't ever hit this but get rid of this if PQ can update elements
                continue;
            }

            visited.add(curr.state);
            frontierMap.remove(curr.state); // shouldn't matter but whatever.
            for (SearchNode n : curr.getNeighbors()) {

                if (visited.contains(n.state)) {
                    continue; // been there, done that
                }
                if (frontierMap.containsKey(n)) {
                    if (frontierMap.get(n).g <= n.g) {
                        System.out.println("Skipping");
                        continue; // n isn't better
                    }
                }

                curr.sinks.remove(n.source); //in case of overwriting a worse path
                curr.sinks.add(n.source);
                pq.add(n);


            }

        }
       // System.out.println("No Solution");
        printBoard(start.state);
        return null;
    }
     static void printPath(List<SearchNode> path){
        for (SearchNode p : path){
            if(p.source != null){
                System.out.println(p.source.x + " " + p.source.y);
            }

            printBoard(p.state);


        }
        System.out.println("Moves: "+ (path.size()-1));

    }
    public static void main( String [] args) {

        for (int i = 0; i<10000;i++) {

            LightsOutModel model = new LightsOutModel();

            model.generateRandomBoard(10);
            List<SearchNode> hPath =new LinkedList<>();
            try {
                long start = System.currentTimeMillis();
            List<SearchNode> sPath = aStar(new LOSearchNode(new Board(model.board)), new Board());
                long end = System.currentTimeMillis();

                System.out.println("Time: "+ (end-start)/1000.0+" Steps:"+ sPath.size());
            }catch (Error e){
                printBoard(model.board, System.err);
                System.err.println(e.getMessage());

            }

        }
    }
}


