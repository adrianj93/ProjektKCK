
package Traktor;

public class Zasoby {
    int zasoby;
    int stankonta;
    
    public Zasoby() {
        zasoby = 0;
        stankonta = 2999;
    }
    
    public int GetZasoby() {
        return zasoby;
    }
    
    public int GetStanKonta() {
        return stankonta;
    }
    
    public void ModyfikujStanKonta(int stankonta_delta) {
        stankonta = stankonta + stankonta_delta;
    }
            
    
    public String GetStanKontaString() {
        stankonta = GetStanKonta();
        String StanKontaString = Integer.toString(stankonta);
        
        return StanKontaString;
}
}
