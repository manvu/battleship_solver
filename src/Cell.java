/**
 * Class to store information for each cell including probability, CellState
 */

public final class Cell {
    CellState state;
    double probability;

    /**
     * Constructor for cell
     */
    public Cell() {
        this.state = CellState.Empty;
        this.probability = 0;
    }

    /**
     * Check if CellState is Sunk
     *
     * @return
     */
    public final boolean isCellSunk() {
        return state.equals(CellState.Sunk);
    }

    /**
     * Check if CellState is Hit
     *
     * @return
     */
    public final boolean isCellHit() {
        return state.equals(CellState.Hit);
    }

    /**
     * Check if CellState is Sunk
     *
     * @return
     */
    public final boolean isCellEmpty() {
        return state.equals(CellState.Empty);
    }

    /**
     * Get the probability
     *
     * @return
     */
    public final double getProbability() {
        return this.probability;
    }

    /**
     * Increment the probability by 1
     */
    public final void incrementProbability() {
        this.probability += 1;
    }

    /**
     * Set the probability
     *
     * @param probability
     */
    public final void setProbability(double probability) {
        this.probability = probability;
    }

    /**
     * Reset the probability
     */
    public final void resetProbability() {
        this.probability = 0;
    }

    /**
     * Get CellState of this Cell
     *
     * @return
     */
    public final CellState getCellState() {
        return this.state;
    }

    /**
     * Set CellState
     *
     * @param state
     */
    public final void setCellState(CellState state) {
        this.state = state;
    }
}