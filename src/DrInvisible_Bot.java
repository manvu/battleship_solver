import battleship.BattleShip;

import java.awt.Point;

/**
 * This battleship bot uses Probability Density Function and Parity Shooting (only when the last ship is the smallest ship
 *
 * The best score this bot has ever achieved is 45.75, though there are a lot of fluctuations
 *
 * @author Man Vu and Huy Mac
 */

public class DrInvisible_Bot {
    private final BattleShip battleShip;
    private final VirtualField virtualField;

    /**
     * Constructor keeps a copy of the BattleShip instance
     *
     * @param b previously created battleship instance - should be a new game
     */
    public DrInvisible_Bot(BattleShip b) {
        this.battleShip = b;
        virtualField = new VirtualField(battleShip.shipSizes());
    }

    /**
     * Create a random shot and calls the battleship shoot method
     *
     * @return true if a Ship is hit, false otherwise
     */

    public boolean fireShot() {
        // There is one ship remaining and it is the smallest ship, then do diagonal skew
        if (virtualField.aliveShips.size() == 1 && virtualField.aliveShips.get(0) == 2 && virtualField.hitPoints.size() == 0) {
            virtualField.diagonalSkew();
        }

        virtualField.updateProbability();

        // Store the number of sunk ships before and after a shot
        int shipsSunkBeforeShot = this.battleShip.numberOfShipsSunk();

        Point shot = virtualField.sinkMode();
        if (shot == null) {
            shot = virtualField.getNextShot();
        }

        boolean hit = this.battleShip.shoot(shot);

        if (hit) {
            virtualField.handleShotHit(shot);
        } else {
            virtualField.board[shot.x][shot.y].setCellState(CellState.Miss);
        }

        handleAfterShot(shipsSunkBeforeShot, shot);

        return hit;
    }

    private void handleAfterShot(int shipsSunkBeforeShot, Point shot) {
        int shipsSunkAfterShot = this.battleShip.numberOfShipsSunk();

        if (shipsSunkAfterShot > shipsSunkBeforeShot) {
            int length = virtualField.getSunkLength();

            boolean foundShip = false;
            for (int i = 0; i < virtualField.aliveShips.size(); ++i)
                if (length == virtualField.aliveShips.get(i) && length > 1) {
                    virtualField.aliveShips.remove(i);
                    foundShip = true;
                    break;
                }

            if (foundShip) virtualField.setCellStateSunk(length);

            virtualField.board[shot.x][shot.y].setCellState(CellState.Sunk);
            virtualField.sunkShips.clear();
            virtualField.hitPoints.clear();
            if (shipsSunkAfterShot == this.battleShip.shipSizes().length - 1) {
                int shotHits = virtualField.getNumberOfCellsWithState(CellState.Hit) + virtualField.getNumberOfCellsWithState(CellState.Sunk);
                shotHits = Math.min(19, shotHits);
                virtualField.aliveShips.clear();
                virtualField.aliveShips.add(virtualField.getMaxHits() - shotHits);
            }
        }
    }
}
