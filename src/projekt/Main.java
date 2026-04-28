package projekt;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabazeZamestnancu databaze = new DatabazeZamestnancu();
        
        databaze.nactiZSQL();
        
        Scanner scanner = new Scanner(System.in);
        boolean bezi = true;
        
        System.out.println("--- Systém pro správu zaměstnanců ---");

        while (bezi) {
            System.out.println("\nVyberte akci:");
            System.out.println("1 - Přidat nového zaměstnance");
            System.out.println("2 - Vypsat všechny zaměstnance");
            System.out.println("3 - Přidat spolupráci mezi zaměstnanci");
            System.out.println("4 - Odebrat zaměstnance");
            System.out.println("5 - Vyhledat zaměstnance dle ID");
            System.out.println("6 - Spustit dovednost zaměstnance");
            System.out.println("7 - Abecední výpis zaměstnanců ve skupinách");
            System.out.println("8 - Zobrazit statistiky spolupráce");
            System.out.println("9 - Zobrazit počet zaměstnanců ve skupinách");
            System.out.println("10 - Uložit databázi zaměstnanců do souboru");
            System.out.println("11 - Načíst databázi zaměstnanců ze souboru");
            System.out.println("0 - Konec programu");
            System.out.print("Vaše volba: ");

            String volba = scanner.nextLine();

            switch (volba) {
                case "1":
                    System.out.print("Zadejte skupinu (analytik / specialista): ");
                    String skupina = scanner.nextLine();
                    System.out.print("Zadejte křestní jméno: ");
                    String jmeno = scanner.nextLine();
                    System.out.print("Zadejte příjmení: ");
                    String prijmeni = scanner.nextLine();
                    System.out.print("Zadejte rok narození: ");
                    int rokNarozeni = 0;
                    
                    try {
                        rokNarozeni = Integer.parseInt(scanner.nextLine());
                        databaze.pridatZamestnance(skupina, jmeno, prijmeni, rokNarozeni);
                    } catch (NumberFormatException e) {
                        System.out.println("Chyba: Rok narození musí být číslo!");
                    }
                    break;

                case "2":
                    databaze.vypisVsechnyZamestnance();
                    break;

                case "3":
                    try {
                        System.out.print("Zadejte ID prvního zaměstnance: ");
                        int id1 = Integer.parseInt(scanner.nextLine());
                        System.out.print("Zadejte ID druhého zaměstnance (kolegy): ");
                        int id2 = Integer.parseInt(scanner.nextLine());
                        System.out.print("Zadejte úroveň spolupráce (spatna / prumerna / dobra): ");
                        String uroven = scanner.nextLine();
                        
                        databaze.pridatSpolupraci(id1, id2, uroven);
                    } catch (NumberFormatException e) {
                        System.out.println("Chyba: ID musí být číslo!");
                    }
                    break;

                case "4":
                    try {
                        System.out.print("Zadejte ID zaměstnance k odebrání: ");
                        int idOdebrat = Integer.parseInt(scanner.nextLine());
                        databaze.odebratZamestnance(idOdebrat);
                    } catch (NumberFormatException e) {
                        System.out.println("Chyba: ID musí být číslo!");
                    }
                    break;

                case "5":
                    try {
                        System.out.print("Zadejte ID zaměstnance k vyhledání: ");
                        int idHledat = Integer.parseInt(scanner.nextLine());
                        databaze.vypisInfoOZamestnanci(idHledat);
                    } catch (NumberFormatException e) {
                        System.out.println("Chyba: ID musí být číslo!");
                    }
                    break;

                case "6":
                    try {
                        System.out.print("Zadejte ID zaměstnance pro spuštění dovednosti: ");
                        int idDovednost = Integer.parseInt(scanner.nextLine());
                        databaze.spustitDovednostZamestnance(idDovednost);
                    } catch (NumberFormatException e) {
                        System.out.println("Chyba: ID musí být číslo!");
                    }
                    break;
                    
                case "7":
                    databaze.vypisAbecedneVeSkupinach();
                    break;
                    
                case "8":
                    databaze.vypisStatistiky();
                    break;
                    
                case "9":
                    databaze.vypisPocetVeSkupinach();
                    break;
                
                case "10":
                    databaze.ulozDoSouboru("databaze.txt");
                    break;
                    
                case "11":
                    databaze.nactiZeSouboru("databaze.txt");
                    break;

                case "0":
                    bezi = false;
                    databaze.ulozDoSQL();
                    System.out.println("Konec programu");
                    break;

                default:
                    System.out.println("Neplatná volba, zkuste to prosím znovu.");
                    break;
            }
        }
        scanner.close();
    }
}
