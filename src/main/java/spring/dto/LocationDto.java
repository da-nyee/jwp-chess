package spring.dto;

import spring.chess.location.Col;
import spring.chess.location.Location;
import spring.chess.location.Row;

public class LocationDto {
    private static final String UNDER_BAR = "_";

    private final Row row;
    private final Col col;

    public LocationDto(String rawLocation) {
        String[] rowAndCol = parseRowAndCol(rawLocation);

        String row = rowAndCol[1];
        String col = rowAndCol[0];

        this.row = Row.of(Integer.parseInt(row));
        this.col = Col.of(col.charAt(0));
    }

    public Location toEntity() {
        return new Location(row, col);
    }

    private static String[] parseRowAndCol(String rowAndCol) {
        return rowAndCol.split(UNDER_BAR);
    }

    public Row getRow() {
        return row;
    }

    public Col getCol() {
        return col;
    }
}
