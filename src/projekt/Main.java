package projekt;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabazeZamestnancu databaze = new DatabazeZamestnancu();
        Scanner scanner = new Scanner(System.in);
        boolean bezi = true;
        
        System.out.println("--- Systém pro správu zaměstnanců ---");

        while (bezi) {
            System.out.println("\nVyberte akci:");
            System.out.println("1 - Přidat nového zaměstnance");
            System.out.println("2 - Vypsat všechny zaměstnance");
            System.out.println("0 - Konec programu");

            String volba = scanner.nextLine();

            switch (volba) {
                case "1":
                    System.out.print("Zadejte skupinu (analytik / specialista): ");
                    String skupina = scanner.nextLine();

                    System.out.print("Zadejte jméno: ");
                    String jmeno = scanner.nextLine();

                    System.out.print("Zadejte příjmení: ");
                    String prijmeni = scanner.nextLine();

                    System.out.print("Zadejte rok narození: ");
                    int rokNarozeni = 0;
                    
                    
                    try {
                        rokNarozeni = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Chyba: Rok narození musí být číslo");
                        break;
                    }

                    databaze.pridatZamestnance(skupina, jmeno, prijmeni, rokNarozeni);
                    break;

                case "2":
                    databaze.vypisVsechnyZamestnance();
                    break;

                case "0":
                    bezi = false;
                    System.out.println("Konec");
                    break;

                default:
                    System.out.println("Neplatná volba, zkuste to prosím znovu.");
                    break;
            }
        }
        scanner.close();
    }
}
