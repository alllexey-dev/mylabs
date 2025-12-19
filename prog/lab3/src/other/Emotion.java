package other;

public enum Emotion {
    CALM("Спокойствие"),
    ENVY("Зависть"),
    LOVE("Любовь"),
    AMAZEMENT("Удивление"),
    PLEASURE("Удовольствие")
    ;
    private final String title;

    Emotion(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
