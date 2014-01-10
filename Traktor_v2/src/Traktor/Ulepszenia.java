
package Traktor;

public class Ulepszenia {
    int moc;
    int ladownosc;
    static Ulepszenia u = new Ulepszenia();

    public Ulepszenia() {
        this.moc = 20;
        this.ladownosc = 30;
    }
    
    public void PowerPack1() {
        int cena = 100;
        Zasoby.z.ModyfikujStanKonta(-cena);
        moc = (int) (moc+(moc*0.3));
    }
    
    public void CapacityPack1() {
        int cena = 120;
        Zasoby.z.ModyfikujStanKonta(-cena);
        ladownosc = (int) (ladownosc+(ladownosc*0.4));
    }
    
    public int GetMoc() {
        return moc;
    }
    
    public int GetCapacity() {
        return ladownosc;
    }
    
}
