package projekt;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.text.Collator;
import java.util.Locale;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;

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
   
    public void ulozDoSouboru(String nazevSouboru) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nazevSouboru))) {
            for (Zamestnanec z : seznamZamestnancu) {
                String typ = (z instanceof DatovyAnalytik) ? "analytik" : "specialista";
                writer.println("EMP;" + typ + ";" + z.getID() + ";" + z.getJmeno() + ";" + z.getPrijmeni() + ";" + z.getRokNarozeni());
            }
            
            for (Zamestnanec z : seznamZamestnancu) {
                for (Map.Entry<Zamestnanec, UrovenSpoluprace> entry : z.getSpolupracovnici().entrySet()) {
                    Zamestnanec kolega = entry.getKey();
                    if (z.getID() < kolega.getID()) {
                        writer.println("REL;" + z.getID() + ";" + kolega.getID() + ";" + entry.getValue().name());
                    }
                }
            }
            System.out.println("Data byla úspěšně uložena do souboru: " + nazevSouboru);
        } catch (IOException e) {
            System.out.println("Chyba při ukládání do souboru: " + e.getMessage());
        }
    }

    public void nactiZeSouboru(String nazevSouboru) {
        try (Scanner fileScanner = new Scanner(new File(nazevSouboru))) {
            seznamZamestnancu.clear();
            int maxId = 0;

            while (fileScanner.hasNextLine()) {
                String radek = fileScanner.nextLine();
                if (radek.trim().isEmpty()) continue;

                String[] casti = radek.split(";");
                
                if (casti[0].equals("EMP")) {
                    String typ = casti[1];
                    int id = Integer.parseInt(casti[2]);
                    String jmeno = casti[3];
                    String prijmeni = casti[4];
                    int rok = Integer.parseInt(casti[5]);

                    Zamestnanec novyZamestnanec;
                    if (typ.equals("analytik")) {
                        novyZamestnanec = new DatovyAnalytik(id, jmeno, prijmeni, rok);
                    } else {
                        novyZamestnanec = new BezpecnostniSpecialista(id, jmeno, prijmeni, rok);
                    }
                    seznamZamestnancu.add(novyZamestnanec);
                    
                    if (id > maxId) {
                        maxId = id;
                    }

                } else if (casti[0].equals("REL")) {
                    int id1 = Integer.parseInt(casti[1]);
                    int id2 = Integer.parseInt(casti[2]);
                    UrovenSpoluprace uroven = UrovenSpoluprace.valueOf(casti[3]);

                    Zamestnanec z1 = najdiZamestnance(id1);
                    Zamestnanec z2 = najdiZamestnance(id2);
                    
                    if (z1 != null && z2 != null) {
                        z1.pridatSpolupracovnika(z2, uroven);
                        z2.pridatSpolupracovnika(z1, uroven);
                    }
                }
            }
            dalsiID = maxId + 1;
            System.out.println("Data byla úspěšně načtena ze souboru: " + nazevSouboru);

        } catch (FileNotFoundException e) {
            System.out.println("Soubor '" + nazevSouboru + "' nebyl nalezen.");
        } catch (Exception e) {
            System.out.println("Při čtení souboru došlo k chybě: " + e.getMessage());
        }
    }
}
