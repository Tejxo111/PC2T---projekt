package projekt;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.text.Collator;
import java.util.Locale;

public class DatabazeZamestnancu {
    private List<Zamestnanec> seznamZamestnancu = new ArrayList<>();
    private int dalsiID = 1;

    public void pridatZamestnance(String skupina, String jmeno, String prijmeni, int rokNarozeni) {
        Zamestnanec novyZamestnanec = null;

        if (skupina.equalsIgnoreCase("analytik")) {
            novyZamestnanec = new DatovyAnalytik(dalsiID, jmeno, prijmeni, rokNarozeni);
        } else if (skupina.equalsIgnoreCase("specialista")) {
            novyZamestnanec = new BezpecnostniSpecialista(dalsiID, jmeno, prijmeni, rokNarozeni);
        } else {
            System.out.println("Chyba: Neznámá skupina. Zadejte 'analytik' nebo 'specialista'.");
            return;
        }

        seznamZamestnancu.add(novyZamestnanec);
        System.out.println("Byl přidán zaměstnanec " + jmeno + " " + prijmeni + ". Přidělené ID: " + dalsiID);
        dalsiID++;
    }

    public Zamestnanec najdiZamestnance(int id) {
        for (Zamestnanec z : seznamZamestnancu) {
            if (z.getID() == id) {
                return z;
            }
        }
        return null;
    }

    public void pridatSpolupraci(int idZamestnance, int idKolegy, String urovenText) {
        Zamestnanec z1 = najdiZamestnance(idZamestnance);
        Zamestnanec z2 = najdiZamestnance(idKolegy);

        if (z1 == null || z2 == null) {
            System.out.println("Chyba: Jeden nebo oba zaměstnanci nebyli nalezeni.");
            return;
        }
        if (z1 == z2) {
            System.out.println("Chyba: Zaměstnanec nemůže spolupracovat sám se sebou.");
            return;
        }

        UrovenSpoluprace urovenEnum;
        try {
            urovenEnum = UrovenSpoluprace.valueOf(urovenText.toUpperCase().replace("Á", "A").replace("Š", "S").replace("Ů", "U"));
        } catch (IllegalArgumentException e) {
            System.out.println("Chyba: Neplatná úroveň spolupráce! Zadejte 'spatna', 'prumerna' nebo 'dobra'.");
            return;
        }

        z1.pridatSpolupracovnika(z2, urovenEnum);
        z2.pridatSpolupracovnika(z1, urovenEnum); 
        System.out.println("Spolupráce úspěšně zaznamenána.");
    }

    public void odebratZamestnance(int id) {
        Zamestnanec zKVymazani = najdiZamestnance(id);

        if (zKVymazani != null) {
            for (Zamestnanec z : seznamZamestnancu) {
                z.odebratSpolupracovnika(zKVymazani); 
            }
            seznamZamestnancu.remove(zKVymazani);
            System.out.println("Zaměstnanec s ID " + id + " byl smazán z databáze včetně všech svých vazeb.");
        } else {
            System.out.println("Chyba: Zaměstnanec s ID " + id + " nenalezen.");
        }
    }

    public void vypisInfoOZamestnanci(int id) {
        Zamestnanec z = najdiZamestnance(id);
        if (z != null) {
            System.out.println("\n--- Informace o zaměstnanci ---");
            System.out.println("ID: " + z.getID());
            System.out.println("Jméno: " + z.getJmeno() + " " + z.getPrijmeni());
            System.out.println("Rok narození: " + z.getRokNarozeni());
            System.out.println("Pozice: " + z.getClass().getSimpleName());
            System.out.println("Počet spolupracovníků: " + z.getSpolupracovnici().size());
        } else {
            System.out.println("Zaměstnanec s ID " + id + " nenalezen.");
        }
    }

    public void spustitDovednostZamestnance(int id) {
        Zamestnanec z = najdiZamestnance(id);
        if (z != null) {
            z.spustitDovednost(); 
        } else {
            System.out.println("Zaměstnanec s ID " + id + " nenalezen.");
        }
    }

    public void vypisVsechnyZamestnance() {
        System.out.println("\n--- Seznam zaměstnanců ---");
        if (seznamZamestnancu.isEmpty()) {
            System.out.println("Databáze je prázdná.");
            return;
        }
        for (Zamestnanec z : seznamZamestnancu) {
            System.out.println("ID: " + z.getID() + " | " + z.getJmeno() + " " + z.getPrijmeni() + 
                               " (" + z.getRokNarozeni() + ") | Skupina: " + z.getClass().getSimpleName());
        }
    }

    public void vypisAbecedneVeSkupinach() {
        if (seznamZamestnancu.isEmpty()) {
            System.out.println("Databáze je prázdná.");
            return;
        }

        List<Zamestnanec> analytici = new ArrayList<>();
        List<Zamestnanec> specialistove = new ArrayList<>();

        for (Zamestnanec z : seznamZamestnancu) {
            if (z instanceof DatovyAnalytik) analytici.add(z);
            else if (z instanceof BezpecnostniSpecialista) specialistove.add(z);
        }

        Collator czCollator = Collator.getInstance(Locale.of("cs", "CZ"));
        
        Comparator<Zamestnanec> czKomparator = Comparator
                .comparing(Zamestnanec::getPrijmeni, czCollator)
                .thenComparing(Zamestnanec::getJmeno, czCollator);

        analytici.sort(czKomparator);
        specialistove.sort(czKomparator);

        System.out.println("\n--- Datoví analytici (A-Z) ---");
        for (Zamestnanec z : analytici) System.out.println("- " + z.getPrijmeni() + " " + z.getJmeno() + " (ID: " + z.getID() + ")");

        System.out.println("\n--- Bezpečnostní specialisté (A-Z) ---");
        for (Zamestnanec z : specialistove) System.out.println("- " + z.getPrijmeni() + " " + z.getJmeno() + " (ID: " + z.getID() + ")");
    }

    public void vypisStatistiky() {
        if (seznamZamestnancu.isEmpty()) {
            System.out.println("Databáze je prázdná, nelze vypsat statistiky.");
            return;
        }

        int spatna = 0, prumerna = 0, dobra = 0;
        Zamestnanec maxVazebZ = null;
        int maxVazeb = -1;

        for (Zamestnanec z : seznamZamestnancu) {
            for (UrovenSpoluprace uroven : z.getSpolupracovnici().values()) {
                if (uroven == UrovenSpoluprace.SPATNA) spatna++;
                else if (uroven == UrovenSpoluprace.PRUMERNA) prumerna++;
                else if (uroven == UrovenSpoluprace.DOBRA) dobra++;
            }

            int pocetVazeb = z.getSpolupracovnici().size();
            if (pocetVazeb > maxVazeb) {
                maxVazeb = pocetVazeb;
                maxVazebZ = z;
            }
        }

        System.out.println("\n--- Statistiky systému ---");
        
        System.out.print("Převažující kvalita spolupráce ve firmě: ");
        if (spatna == 0 && prumerna == 0 && dobra == 0) {
            System.out.println("Zatím nebyly navázány žádné spolupráce.");
        } else {
            if (spatna >= prumerna && spatna >= dobra) System.out.println("ŠPATNÁ");
            else if (prumerna >= spatna && prumerna >= dobra) System.out.println("PRŮMĚRNÁ");
            else System.out.println("DOBRÁ");
        }

        if (maxVazebZ != null && maxVazeb > 0) {
            System.out.println("Nejvíce propojený zaměstnanec: " + maxVazebZ.getJmeno() + " " + maxVazebZ.getPrijmeni() + " (" + maxVazeb + " spojení)");
        } else {
            System.out.println("Zatím žádný zaměstnanec nemá kontakty.");
        }
    }

    public void vypisPocetVeSkupinach() {
        int pocetAnalytiku = 0;
        int pocetSpecialistu = 0;

        for (Zamestnanec z : seznamZamestnancu) {
            if (z instanceof DatovyAnalytik) pocetAnalytiku++;
            else if (z instanceof BezpecnostniSpecialista) pocetSpecialistu++;
        }

        System.out.println("\n--- Rozložení zaměstnanců ---");
        System.out.println("Datoví analytici: " + pocetAnalytiku);
        System.out.println("Bezpečnostní specialisté: " + pocetSpecialistu);
        System.out.println("Celkem zaměstnanců: " + seznamZamestnancu.size());
    }
}
