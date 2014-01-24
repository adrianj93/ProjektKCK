
package Traktor;

public class Zasoby {
    static Zasoby z = new Zasoby();
    int zb1,zb2,zb3,zb4,zb5,zb6;
    int zasoby;
    int przenica;
    int bataty;
    int oliwki;
    int jeczmien;
    int kawa;
    int herbata;
    double zasoby_max;
    double procent;
    int stankonta;
    double elektrycznosc;
    double paliwo;
    
    public Zasoby() {
        zasoby_max = 1000;
        zasoby = 0;
        stankonta = 2999;
        elektrycznosc = 100;
        procent = 0;
        paliwo = 100;
        
        
        zb1=10;
        zb2=20;
        zb3=30;
        zb4=40;
        zb5=50;
        zb6=60;
        
        this.przenica=5;
        this.bataty=5;
        this.oliwki=5;
        this.jeczmien=5;
        this.kawa=5;
        this.herbata=5;
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
        zasoby = zb1 + zb2 + zb3 + zb4 + zb5 + zb6;
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
    
    public double GetProcentZasoby(){
          ZasobyProcent();
          return procent;
    }
    
    public void ZasobyProcent(){
        procent = (GetZasoby()/GetZasobyMax())*100;
    }
    
    public double GetPaliwo()
    {
        return paliwo;
    }
    
    public void ModPaliwo(double a){
        paliwo = paliwo + a;
    }
    public double GetZasobyMax(){
        return zasoby_max;
    }
    public void CapacityPackLevelUp(double a){
        GetZasobyMax();
        zasoby_max = zasoby_max + a;
    }
    
     public void Uzupelnij_przenica() {
        int cena = 50;
        Zasoby.z.ModyfikujStanKonta(-cena);
        przenica = (int) (przenica+(przenica*0.5));
    }

        public void Uzupelnij_bataty() {
        int cena = 50;
        Zasoby.z.ModyfikujStanKonta(-cena);
        bataty = (int) (bataty+(bataty*0.5));
    }
        
        public void Uzupelnij_oliwki() {
        int cena = 50;
        Zasoby.z.ModyfikujStanKonta(-cena);
        oliwki = (int) (oliwki+(oliwki*0.5));
    }
        
        public void Uzupelnij_jeczmien() {
        int cena = 50;
        Zasoby.z.ModyfikujStanKonta(-cena);
        jeczmien = (int) (jeczmien+(jeczmien*0.5));
    }
        
        public void Uzupelnij_kawa() {
        int cena = 50;
        Zasoby.z.ModyfikujStanKonta(-cena);
        kawa = (int) (kawa+(kawa*0.5));
    }
        
        public void Uzupelnij_herbata() {
        int cena = 50;
        Zasoby.z.ModyfikujStanKonta(-cena);
        herbata = (int) (herbata+(herbata*0.5));
    }
    
    public int GetPrzenica() {
        return przenica;
    }
    
        public int GetBataty() {
        return bataty;
    }
        
        public int GetOliwki() {
        return oliwki;
    }
            
        public int GetJeczmien() {
        return jeczmien;
    }
                
        public int GetKawa() {
        return kawa;
    }
                    
       public int GetHerbata() {
        return herbata;
    }
    
}
