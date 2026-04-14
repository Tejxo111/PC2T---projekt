import java.util.Map;

public class BezpecnostniSpecialista extends Zamestnanec {

    public BezpecnostniSpecialista(int ID, String jmeno, String prijmeni, int rokNarozeni) {
        super(ID, jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public void spustitDovednost() {
        Map<Zamestnanec, UrovenSpoluprace> seznamSpolupracovniku = getSpolupracovnici();

        if (seznamSpolupracovniku == null || seznamSpolupracovniku.isEmpty()) {
            System.out.println("Bezpečnostní analýza: Zaměstnanec " + getJmeno() + " nemá žádné spolupracovníky.");
            return;
        }

        int pocetSpolupracovniku = seznamSpolupracovniku.size();
        double soucetKvality = 0;

        for (UrovenSpoluprace uroven : seznamSpolupracovniku.values()) {
            soucetKvality += uroven.getHodnota(); 
        }

        double prumernaKvalitaSpoluprace = soucetKvality / pocetSpolupracovniku;
        double zakladRizika = 4.0 - prumernaKvalitaSpoluprace; 
        double multiplikatorPocetLidi = 1.0 + (pocetSpolupracovniku * 0.1); 
        double celkoveRizikoveSkore = zakladRizika * multiplikatorPocetLidi;

        System.out.println("\n--- Bezpečnostní zpráva ---");
        System.out.println("Specialista: " + getJmeno() + " " + getPrijmeni());
        System.out.println("Počet vazeb: " + pocetSpolupracovniku + " (Průměrná kvalita: " + String.format("%.1f", prumernaKvalitaSpoluprace) + "/3.0)");
        System.out.println("Rizikové skóre: " + String.format("%.2f", celkoveRizikoveSkore));
        System.out.println("---------------------------");
    }
}
