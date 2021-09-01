public enum CellState {
    Empty,
    Hit,
    Miss,
    Sunk;

    public String toString() {
        return switch (this) {
            case Empty -> "E";
            case Hit -> "H";
            case Miss -> "M";
            case Sunk -> "S";
            default -> "";
        };
    }
}
