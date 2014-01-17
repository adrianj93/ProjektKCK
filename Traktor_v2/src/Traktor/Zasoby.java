
package Traktor;

public class Zasoby {
    static Zasoby z = new Zasoby();
    int zb1,zb2,zb3,zb4,zb5,zb6;
    int zasoby;
    int stankonta;
    double elektrycznosc;
    
    public Zasoby() {
        zasoby = 0;
        stankonta = 2999;
        elektrycznosc = 100;
        
        zb1=10;
        zb2=20;
        zb3=30;
        zb4=40;
        zb5=50;
        zb6=60;
    }
    
    public int GetZb1() {
        return zb1;
    }
    
    public int GetZb2() {
        return zb2;
    }
    
    public int GetZb3() {
        return zb3;
    }
    
    public int GetZb4() {
        return zb4;
    }
    
    public int GetZb5() {
        return zb5;
    }
    
    public int GetZb6() {
        return zb6;
    }

    public int GetZasoby() {
        return zasoby;
    }
    
    public int GetStanKonta() {
        return stankonta;
    }

    /**
     *
     * @param a
     */
    public void ModyfikujZb1(int a)
    {
        zb1 = zb1 + a;
    }
    
    public void ModyfikujZb2(int a) {
        zb2 = zb2 + a;
    }
    
    public void ModyfikujZb3(int a) {
        zb3 = zb3 + a;
    }
    
    public void ModyfikujZb4(int a) {
        zb4 = zb4 + a;
    }
    
    public void ModyfikujZb5(int a) {
        zb5 = zb5 + a;
    }
    
    public void ModyfikujZb6(int a) {
        zb6 = zb6 + a;
    }
    
    public void ModyfikujStanKonta(int stankonta_delta) {
        stankonta = stankonta + stankonta_delta;
    }
            
    
    public String GetStanKontaString() {
        stankonta = GetStanKonta();
        String StanKontaString = Integer.toString(stankonta)+"€";
        
        return StanKontaString;
    }
    
    public double GetElektrycznosc() {
        return elektrycznosc;
    }
    
    public void ModyfikujElektrycznosc (double elektrycznosc_delta) {
        elektrycznosc = elektrycznosc + elektrycznosc_delta;
    }
}
