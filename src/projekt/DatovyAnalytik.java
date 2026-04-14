import java.util.Map;

public class DatovyAnalytik extends Zamestnanec {

    public DatovyAnalytik(int ID, String jmeno, String prijmeni, int rokNarozeni) {
        super(ID, jmeno, prijmeni, rokNarozeni);
    }
 
    @Override
    public void spustitDovednost() {
        Map<Zamestnanec, UrovenSpoluprace> mojiSpolupracovnici = getSpolupracovnici();

        if (mojiSpolupracovnici == null || mojiSpolupracovnici.isEmpty()) {
            System.out.println("Datová analýza: Zaměstnanec " + getJmeno() + " nemá žádné spolupracovníky.");
            return;
        }

        Zamestnanec kolegaSNejviceSpolecnymi = null;
        int maxSpolecnych = -1;

        for (Zamestnanec testovanyKolega : mojiSpolupracovnici.keySet()) {
            Map<Zamestnanec, UrovenSpoluprace> jehoSpolupracovnici = testovanyKolega.getSpolupracovnici();
            int pocetSpolecnych = 0;

            for (Zamestnanec mujDalsiKolega : mojiSpolupracovnici.keySet()) {
                if (mujDalsiKolega != testovanyKolega && jehoSpolupracovnici.containsKey(mujDalsiKolega)) {
                    pocetSpolecnych++;
                }
            }

            if (pocetSpolecnych > maxSpolecnych) {
                maxSpolecnych = pocetSpolecnych;
                kolegaSNejviceSpolecnymi = testovanyKolega;
            }
        }

        System.out.println("\n--- Analýza sítě spolupracovníků ---");
        System.out.println("Analytik: " + getJmeno() + " " + getPrijmeni());
        
        if (kolegaSNejviceSpolecnymi != null && maxSpolecnych > 0) {
            System.out.println("Nejvíce společných kolegů má s: " + kolegaSNejviceSpolecnymi.getJmeno() + 
                               " " + kolegaSNejviceSpolecnymi.getPrijmeni() + " (ID: " + kolegaSNejviceSpolecnymi.getID() + ")");
            System.out.println("Počet společných kolegů: " + maxSpolecnych);
        } else {
            System.out.println("Nenalezen žádný kolega se společnými vazbami.");
        }
        System.out.println("------------------------------------");
    }
}
