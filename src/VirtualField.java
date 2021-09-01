import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This Virtual Field class is visualization of how the AI player views the battleship board
 *
 * @author: Man Vu & Huy Mac
 */

public class VirtualField {
    // 2D array to store all the states and probability of cells on the board
    public Cell[][] board;

    // All of the cells where ships are hit
    public ArrayList<Point> hitPoints;

    // List of sunk ships
    public Stack<Point> sunkShips = new Stack();

    // List of alive ships
    public ArrayList<Integer> aliveShips;

    // The size of the board
    public final int BOARD_SIZE = 10;

    private int maxHits;

    private ShipDirection sinkHitDirection;


    /**
     * Construction for Virtual Field
     *
     * @param shipSizes
     */
    public VirtualField(int[] shipSizes) {
        this.hitPoints = new ArrayList<>();
        this.sinkHitDirection = ShipDirection.To_Be_Determined;

        // Initialize all cells in the 2D board
        this.board = new Cell[this.BOARD_SIZE][this.BOARD_SIZE];
        for (int x = 0; x < this.BOARD_SIZE; ++x)
            for (int y = 0; y < this.BOARD_SIZE; ++y)
                this.board[x][y] = new Cell();

        // Initialize the array of alive ships
        this.aliveShips = new ArrayList<>();
        for (int size : shipSizes) {
            aliveShips.add(size);
            maxHits = getMaxHits() + size;
        }

    }

    /**
     * Gets an adjacent point by view
     *
     * @param view
     * @param x
     * @param y
     * @return
     */
    public Point getAdjacentCellByDirection(VIEW view, int x, int y) {
        return switch (view) {
            case LEFT -> new Point(x - 1, y);
            case RIGHT -> new Point(x + 1, y);
            case UP -> new Point(x, y - 1);
            case DOWN -> new Point(x, y + 1);
        };
    }

    /**
     * Check if the point is inside the board
     *
     * @param p
     * @return
     */
    public final boolean isInBounds(Point p) {
        return p.x >= 0 && p.x < BOARD_SIZE && p.y >= 0 && p.y < BOARD_SIZE;
    }

    /**
     * Get a list of adjacent points where ships can be placed
     *
     * @param x
     * @param y
     * @return
     */
    public final ArrayList<Point> getAvailableSurroundingPoints(int x, int y) {
        Point[] surroundingPoints = new Point[]{
                getAdjacentCellByDirection(VIEW.LEFT, x, y),
                getAdjacentCellByDirection(VIEW.RIGHT, x, y),
                getAdjacentCellByDirection(VIEW.DOWN, x, y),
                getAdjacentCellByDirection(VIEW.UP, x, y)};

        return Arrays.stream(surroundingPoints)
                .filter(point -> isInBounds(point) && board[point.x][point.y].isCellEmpty())
                .collect(Collectors.toCollection(ArrayList<Point>::new));
    }

    /**
     * This method tells whether ship can be placed at a cell or not
     *
     * @param cell
     * @return a boolean value
     */
    public boolean cannotPlaceShip(Cell cell) {
        return cell.getCellState() != CellState.Empty && cell.getCellState() != CellState.Hit;
    }

    /**
     * This method handles hit shot
     *
     * @param shot
     */
    public void handleShotHit(Point shot) {
        board[shot.x][shot.y].setCellState(CellState.Hit);

        if (hitPoints.isEmpty()) {
            sinkHitDirection = ShipDirection.To_Be_Determined;
        }

        if (sunkShips.empty()) {
            hitPoints.add(shot);
            setupSinkStack(shot);
        } else {
            ArrayList<Point> surroundingPoints = getAvailableSurroundingPoints(shot.x, shot.y);
            hitPoints.add(shot);

            if (shot.x == hitPoints.get(0).x) {
                sinkHitDirection = ShipDirection.Horizontal;

                surroundingPoints.stream().filter(p -> p.x == shot.x).forEach(p -> sunkShips.push(p));

            } else if (shot.y == hitPoints.get(0).y) {
                sinkHitDirection = ShipDirection.Vertical;

                surroundingPoints.stream().filter(p -> p.y == shot.y).forEach(p -> sunkShips.push(p));
            }
        }
    }

    /**
     * Return the number of cells with state
     *
     * @param state
     * @return
     */
    public int getNumberOfCellsWithState(CellState state) {
        int result = 0;

        for (Cell[] row : board)
            for (Cell cell : row) {
                if (cell.getCellState() == state) ++result;
            }

        return result;
    }

    /**
     * Update probability on the heat map after every shot is done
     */
    public void updateProbability() {
        // Reset the probability of all cells on the board
        resetProbability();

        boolean canPlace;

        for (Integer length : aliveShips) {
            for (int y = 0; y < BOARD_SIZE; ++y) {
                for (int x = 0; x < BOARD_SIZE - length + 1; ++x) {
                    // Update probability based on length of ship horizontally
                    canPlace = true;
                    for (int l = 0; l < length; ++l)
                        if (cannotPlaceShip(board[y][x + l])) {
                            canPlace = false;
                            break;
                        }

                    if (canPlace) {
                        for (int l = 0; l < length; ++l) board[y][x + l].incrementProbability();
                    }

                    // Update probability based on length of ship vertically
                    canPlace = true;
                    for (int l = 0; l < length; ++l)
                        if (cannotPlaceShip(board[x + l][y])) {
                            canPlace = false;
                            break;
                        }

                    if (canPlace) {
                        for (int l = 0; l < length; ++l) board[x + l][y].incrementProbability();
                    }
                }
            }
        }
    }

    /**
     * This method resets all probability of all cells on the board
     */
    public void resetProbability() {
        for (int x = 0; x < BOARD_SIZE; ++x)
            for (int i = 0; i < BOARD_SIZE; ++i)
                board[x][i].resetProbability();
    }

    /**
     * This method finds the best next shoot based on the probability map
     *
     * @return
     */
    public Point getNextShot() {
        Point nextShot = null;
        double maxProbability = -1;
        ArrayList<Point> maxProbabilityCells = new ArrayList<>();
        int hits = getNumberOfCellsWithState(CellState.Hit) + getNumberOfCellsWithState(CellState.Sunk);

        if (hits < getMaxHits() - 1)
            for (int x = 0; x < BOARD_SIZE; ++x)
                for (int y = 0; y < BOARD_SIZE; ++y)
                    if (board[x][y].isCellEmpty() && board[x][y].getProbability() > maxProbability) {
                        nextShot = new Point(x, y);
                        maxProbability = board[x][y].getProbability();
                        maxProbabilityCells.clear();
                        maxProbabilityCells.add(nextShot);
                    } else if (board[x][y].isCellEmpty() && board[x][y].getProbability() == maxProbability) {
                        nextShot = new Point(x, y);
                        maxProbabilityCells.add(nextShot);
                    }

        if (hits == getMaxHits() - 1 || maxProbability == 0) {
            ArrayList<Point> surroundingPoints;
            for (int x = 0; x < BOARD_SIZE; ++x)
                for (int y = 0; y < BOARD_SIZE; ++y)
                    if (board[x][y].isCellHit() || board[x][y].isCellSunk()) {
                        surroundingPoints = getAvailableSurroundingPoints(x, y);
                        if (surroundingPoints.size() > 0) return surroundingPoints.get(0);
                    }
        }

        return nextShot;
    }

    /**
     * After a ship has been sunken, this method sets the state of cells occupied by the ship
     *
     * @param shipLength
     */
    public void setCellStateSunk(int shipLength) {
        Point last = hitPoints.get(hitPoints.size() - 1);
        Point secondLast = hitPoints.get(Math.max(hitPoints.size() - 2, 0));

        if (last.x == secondLast.x) {
            int startY = getStartY(last);

            IntStream.range(startY, startY + shipLength).forEach(y -> board[last.x][y].setCellState(CellState.Sunk));
        } else {
            int startX = getStartX(last);

            IntStream.range(startX, startX + shipLength).forEach(x -> board[x][last.y].setCellState(CellState.Sunk));
        }
    }

    /**
     * @param last
     * @return
     */
    public int getStartY(Point last) {
        int startY = last.y;
        for (Point p : hitPoints) if (p.x == last.x && p.y < startY) startY = p.y;

        return startY;
    }

    /**
     * @param last
     * @return
     */
    public int getStartX(Point last) {
        int startX = last.x;
        for (Point p : hitPoints) if (p.y == last.y && p.x < startX) startX = p.x;

        return startX;
    }

    /**
     * This method returns the length of the ship that has just been sunk
     *
     * @return
     */
    public int getSunkLength() {
        Point latestHit = hitPoints.get(hitPoints.size() - 1);
        Point firstHit = hitPoints.get(Math.max(hitPoints.size() - 2, 0));
        int sunkLength = 0;

        if (latestHit.x == firstHit.x) {
            for (int y = getStartY(latestHit); y < BOARD_SIZE && board[latestHit.x][y].isCellHit(); ++y) ++sunkLength;
        } else {
            for (int x = getStartX(latestHit); x < BOARD_SIZE && board[x][latestHit.y].isCellHit(); ++x) ++sunkLength;
        }

        return sunkLength;
    }

    /**
     * This method will try to sink a ship has it has been found
     *
     * @return
     */
    public Point sinkMode() {
        do {
            if (sunkShips.size() > 0) {
                Point best = sunkShips.pop();
                if (!board[best.x][best.y].isCellEmpty()) {
                    continue;
                } else if (hitPoints.isEmpty()) {
                    return best;
                } else if (best.x == hitPoints.get(0).x) {
                    if (sinkHitDirection != ShipDirection.Vertical) {
                        return best;
                    } else if (best.y != hitPoints.get(0).y) {
                        continue;
                    } else {
                        return best;
                    }
                } else if (sinkHitDirection == ShipDirection.Horizontal) {
                    continue;
                } else if (best.y != hitPoints.get(0).y) {
                    continue;
                } else {
                    return best;
                }
            }

            for (int x = 0; x < BOARD_SIZE; ++x)
                for (int y = 0; y < BOARD_SIZE; ++y)
                    if (board[x][y].getCellState() == CellState.Hit) {
                        setupSinkStack(new Point(x, y));
                        if (sunkShips.size() > 0) {
                            return sunkShips.pop();
                        }
                    }

            return null;
        } while (true);
    }

    /**
     * This method initializes the sink stack when a shot is hit
     *
     * @param point
     */
    public void setupSinkStack(Point point) {
        ArrayList<Point> potentialCellsToShot = getAvailableSurroundingPoints(point.x, point.y);
        potentialCellsToShot.sort(Comparator.comparingDouble(shot -> board[shot.x][shot.y].getProbability()));

        sunkShips.clear();
        for (Point shot : potentialCellsToShot) sunkShips.push(shot);
    }

    /**
     * An additional strategy to probabilistic density function which shoots diagonally to find
     * the smallest ship when it is the only remaining one. It is shown that using diagonal skew
     * increases 20% of hitting the smallest ship when it is the only remaining one
     */
    public ArrayList<Point> diagonalSkew() {
        ArrayList<Point> potentialShots = new ArrayList<>();

        for (int y = 2; y < BOARD_SIZE - 1; y += 2) {
            for (int x = 1; x < BOARD_SIZE - 1; x += 2) {
                if (canPlaceShip(y, x)) {
                    potentialShots.add(new Point(x, y));
                    board[x][y].setProbability(board[x][y].getProbability() * 1.5);
                }
            }
        }

        for (int y = 1; y < BOARD_SIZE - 1; y += 2) {
            for (int x = 2; x < BOARD_SIZE - 1; x += 2) {
                if (canPlaceShip(y, x)) {
                    potentialShots.add(new Point(x, y));
                    board[x][y].setProbability(board[x][y].getProbability() * 1.5);
                }
            }
        }

        return potentialShots;
    }


    /**
     * Returns true if adjacent cells from up, down, left, right directions and itself are Empty
     *
     * @param y
     * @param x
     * @return
     */
    private boolean canPlaceShip(int y, int x) {
        return (board[y][x + 1].getCellState() == CellState.Empty &&
                board[y][x - 1].getCellState() == CellState.Empty &&
                board[y + 1][x].getCellState() == CellState.Empty &&
                board[y - 1][x].getCellState() == CellState.Empty) &&
                board[y][x].getCellState() == CellState.Empty;
    }

    /**
     * This method returns whether or not the point is on edge
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isEdge(int x, int y) {
        return x == 0 && !(y == 0) || x == BOARD_SIZE - 1 || y == 0 && !(x == 0) || y == BOARD_SIZE - 1;
    }

    /**
     * This method returns whether or not a point is in corner
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isCorner(int x, int y) {
        return x == 0 && y == 0 ||
                x == 0 && y == BOARD_SIZE - 1 ||
                x == BOARD_SIZE - 1 && y == 0 ||
                x == BOARD_SIZE - 1 && y == BOARD_SIZE - 1;
    }

    public int getMaxHits() {
        return maxHits;
    }
}
