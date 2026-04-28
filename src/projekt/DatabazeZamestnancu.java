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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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
    
    private final String DB_URL = "jdbc:sqlite:zaloha_zamestnancu.db";

    public void nactiZSQL() {
        System.out.println("Pokouším se načíst zálohu ze SQL databáze");
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS Zamestnanci (id INTEGER PRIMARY KEY, typ TEXT, jmeno TEXT, prijmeni TEXT, rokNarozeni INTEGER)");
                stmt.execute("CREATE TABLE IF NOT EXISTS Spoluprace (id1 INTEGER, id2 INTEGER, uroven TEXT)");
            }

            seznamZamestnancu.clear();
            int maxId = 0;

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM Zamestnanci")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String typ = rs.getString("typ");
                    String jmeno = rs.getString("jmeno");
                    String prijmeni = rs.getString("prijmeni");
                    int rok = rs.getInt("rokNarozeni");
                    
                    Zamestnanec z = typ.equals("analytik") ? 
                        new DatovyAnalytik(id, jmeno, prijmeni, rok) : 
                        new BezpecnostniSpecialista(id, jmeno, prijmeni, rok);
                    
                    seznamZamestnancu.add(z);
                    if (id > maxId) maxId = id;
                }
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM Spoluprace")) {
                while (rs.next()) {
                    int id1 = rs.getInt("id1");
                    int id2 = rs.getInt("id2");
                    UrovenSpoluprace uroven = UrovenSpoluprace.valueOf(rs.getString("uroven"));
                    
                    Zamestnanec z1 = najdiZamestnance(id1);
                    Zamestnanec z2 = najdiZamestnance(id2);
                    
                    if (z1 != null && z2 != null) {
                        z1.pridatSpolupracovnika(z2, uroven);
                        z2.pridatSpolupracovnika(z1, uroven);
                    }
                }
            }
            
            dalsiID = maxId + 1;
            System.out.println("SQL databáze byla úspěšně načtena.");
            
        } catch (Exception e) {
            System.out.println("SQL databáze není dostupná (" + e.getMessage() + ").");
        }
    }

    public void ulozDoSQL() {
        System.out.println("Zálohuji data do SQL databáze");
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS Zamestnanci");
                stmt.execute("DROP TABLE IF EXISTS Spoluprace");
                stmt.execute("CREATE TABLE Zamestnanci (id INTEGER PRIMARY KEY, typ TEXT, jmeno TEXT, prijmeni TEXT, rokNarozeni INTEGER)");
                stmt.execute("CREATE TABLE Spoluprace (id1 INTEGER, id2 INTEGER, uroven TEXT)");
            }

            String insertZam = "INSERT INTO Zamestnanci (id, typ, jmeno, prijmeni, rokNarozeni) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertZam)) {
                for (Zamestnanec z : seznamZamestnancu) {
                    pstmt.setInt(1, z.getID());
                    pstmt.setString(2, (z instanceof DatovyAnalytik) ? "analytik" : "specialista");
                    pstmt.setString(3, z.getJmeno());
                    pstmt.setString(4, z.getPrijmeni());
                    pstmt.setInt(5, z.getRokNarozeni());
                    pstmt.executeUpdate();
                }
            }

            String insertSpol = "INSERT INTO Spoluprace (id1, id2, uroven) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSpol)) {
                for (Zamestnanec z : seznamZamestnancu) {
                    for (Map.Entry<Zamestnanec, UrovenSpoluprace> entry : z.getSpolupracovnici().entrySet()) {
                        if (z.getID() < entry.getKey().getID()) {
                            pstmt.setInt(1, z.getID());
                            pstmt.setInt(2, entry.getKey().getID());
                            pstmt.setString(3, entry.getValue().name());
                            pstmt.executeUpdate();
                        }
                    }
                }
            }
            System.out.println("Záloha dat do SQL proběhla úspěšně.");
            
        } catch (Exception e) {
            System.out.println("Upozornění: Data se nepodařilo zazálohovat do SQL (" + e.getMessage() + ").");
        }
    }
}
