package Projekt;
import java.util.ArrayList;
import java.util.List;

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
            System.out.println("Neznama skupina");
            return;
        }

        seznamZamestnancu.add(novyZamestnanec);
        System.out.println("Byl přidán zaměstnanec " + jmeno + " " + prijmeni + ". Přidělené ID: " + dalsiID);
        
        dalsiID++;
    }

    public void vypisVsechnyZamestnance() {
        System.out.println("\n--- Seznam zaměstnanců ---");
        for (Zamestnanec z : seznamZamestnancu) {
            System.out.println("ID: " + z.getID() + " | " + z.getJmeno() + " " + z.getPrijmeni() + 
                               " (" + z.getRokNarozeni() + ") | Skupina: " + z.getClass().getSimpleName());
        }
    }
}
