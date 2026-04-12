package Projekt;
import java.util.HashMap;
import java.util.Map;

public abstract class Zamestnanec {
    private int ID;
    private String jmeno;
    private String prijmeni;
    private int rokNarozeni;
    
    private Map<Integer, UrovenSpoluprace> spolupracovnici;

    public Zamestnanec(int ID, String jmeno, String prijmeni, int rokNarozeni) {
        this.ID = ID;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
        this.spolupracovnici = new HashMap<>();
    }

    public int getID() { 
    	return ID; 
    	}
    
    public String getJmeno() { 
    	return jmeno; 
    	}
    
    public void setJmeno(String jmeno) { 
    	this.jmeno = jmeno; 
    	}
    
    public String getPrijmeni() { 
    	return prijmeni; 
    	}
    
    public void setPrijmeni(String prijmeni) { 
    	this.prijmeni = prijmeni; 
    	}
    
    public int getRokNarozeni() { 
    	return rokNarozeni; 
    	}
    
    public void setRokNarozeni(int rokNarozeni) { 
    	this.rokNarozeni = rokNarozeni; 
    	}
    
    public Map<Integer, UrovenSpoluprace> getSpolupracovnici() { 
    	return spolupracovnici; 
    	}

    public abstract void spustitDovednost();
}
