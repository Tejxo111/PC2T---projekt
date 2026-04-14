package projekt;
 
public enum UrovenSpoluprace {
    SPATNA(1),
    PRUMERNA(2),
    DOBRA(3);

    private final int hodnota;

    UrovenSpoluprace(int hodnota) {
        this.hodnota = hodnota;
    }

    public int getHodnota() {
        return hodnota;
    }
}
