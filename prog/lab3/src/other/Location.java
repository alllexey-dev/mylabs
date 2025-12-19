package other;

public enum Location {
    ENTRANCE("Вход"),
    DINING_TABLE("Обеденный стол"),
    HALL_CENTER("Центр зала"),
    SECLUDED_CORNER("Укромный уголок"),
    ROYAL_ROOM("Королевская комната"),
    OTHER("Вне замка")
    ;

    private final String title;

    Location(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
