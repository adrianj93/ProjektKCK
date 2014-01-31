/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Traktor;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Scanner;

public class GamePanel extends javax.swing.JPanel {
    
    public static final int SZEROKOSC = 25;
    public static final int WYSOKOSC = 250;
    
    DefaultListModel dlm; 
    Interpreter interpreter;
    Board board;
    Agent agent;
    public BufferedImage potworek = null;
    int scaleX;
    int scaleY;

    public int pozX,pozY;
    public GamePanel()
    {
        pozX=50;
        pozY=380;
        initComponents();
        board = new Board(11,10);      
        setObjectsRandom();
        interpreter = new Interpreter("src\\Traktor\\keyword.txt"); 
        sprites = new HashMap();
       
    }
    public static int losuj(int n){
        Random rand = new Random();
        return rand.nextInt(n);
    }
    public HashMap sprites;
    public void SetDLM(DefaultListModel _dlm)
    {
        dlm = _dlm;
    }
    
    
    //ustawienie elementów w okienku
    private void setObjectsRandom()
    {
        Random rand = new Random();
        int x,y;
        
        agent = new Agent();
        
        //Miejsce startu traktora
        board.SetObject(8, 9,true, agent);
        
        int countPackage = 5;
        //hangary
        ObjectStore os1 = new ObjectStore(5);
        ObjectStore os2 = new ObjectStore(2);
        board.SetObject(board.width-1, board.height-1, false, os1);
        board.SetObject(0, board.height-1, false, os2);
        
        //Losowe rozmieszczone kamienie
        while((countPackage--) > 0)
        {
            ObjectPackage op = new ObjectPackage();
            
            do
            {
                x = rand.nextInt(board.width-1 );
                y = rand.nextInt(board.height-2 );
            }while(board.CheckPool(x, y, false));
            
            board.SetObject(x, y, false, op);
        }
        
    }
    
    //Jak nie ma jakiegos obrazku to podczasz kompilowanie informuje nas o tym
    public BufferedImage loadImage(String sciezka) {
    URL url=null;
    try {
           url = getClass().getClassLoader().getResource(sciezka);
           return ImageIO.read(url);
        } 
    catch (IOException e) 
    {
        System.out.println("Przy otwieraniu " + sciezka +" jako " + url);
        System.out.println("Wystapil blad : "+e.getClass().getName()+""+e.getMessage());
        System.exit(0);
        return null;
    }
}
    
   
public BufferedImage getSprite(String sciezka) {
BufferedImage img = (BufferedImage)sprites.get(sciezka);
if (img == null) {
img = loadImage(sciezka);
sprites.put(sciezka,img);
}
return img;
}    
    
    private Point findAgent()
    {
        Point p = null;
    
        for(int i = 0; i < board.getWidth(); i++)
            for(int j = 0; j < board.getHeight(); j++)
            {
                if(board.CheckPool(i, j, true))
                {
                    PoolObject po = board.GetObject(i, j, true);
                    if(po instanceof Agent)
                    {                        
                        p = new Point(i,j);
                    }
                }
            }
        
        return p;
    }
    
    private void addTextToList(String text)
    {
        dlm.add(0,MechanizmCzasu.GetCzas() + " eT: " + text);
    }
    int SZYBKOSC=300;
    
    public void Run(String text) throws InterruptedException {
        ExecCommand[] excmd = interpreter.PrepareText(text);
        boolean isOccupied = false;
        boolean isObstacle = false;
        boolean isPlow = false;
        boolean isSow = false;
        boolean isWater = false;
        boolean isRoute = false;
        
        if(GameFrame.gf.aktpog=="słońce"){
                             SZYBKOSC=300;
        if (Ulepszenia.u.SwiatlaStan() == true)
            SZYBKOSC=300;
        }
        if(GameFrame.gf.aktpog=="deszcz"){
                             SZYBKOSC=700;
        if (Ulepszenia.u.SwiatlaStan() == true)
            SZYBKOSC=300;
        }
        if(GameFrame.gf.aktpog=="śnieg"){
                             SZYBKOSC=1000;
        if (Ulepszenia.u.SwiatlaStan() == true)
            SZYBKOSC=300;
        }
        if(GameFrame.gf.aktpog=="wiatr/mgła"){
                             SZYBKOSC=1000;
        if (Ulepszenia.u.SwiatlaStan() == true)
            SZYBKOSC=300;
        }
        if(GameFrame.gf.aktpog=="burza"){
                             SZYBKOSC=1500;
        if (Ulepszenia.u.SwiatlaStan() == true)
            SZYBKOSC=300;
        }
        
        
        Thread.sleep(SZYBKOSC);

        if (excmd.length == 0) {
            addTextToList(Informations.UnknownCommand);
        }
        
        // blokowanie drogi przed możliwością uprawy
        RouteObject op = new RouteObject();
        board.SetObject(2, 9, false, op);
        board.SetObject(3, 9, false, op);
        board.SetObject(4, 9, false, op);
        board.SetObject(5, 9, false, op);
        board.SetObject(6, 9, false, op);
        board.SetObject(7, 9, false, op);
        board.SetObject(8, 9, false, op);
        
        for (int i = 0; i < excmd.length; i++) {
            if (!excmd[i].arg) {
                
                Point pAgent = findAgent();
                int no = excmd[i].cmd.GetNo();
                   
                switch (no) {
                    case 1: //poruszanie się
                    { 
                        int gdzie=0;
                        for (int j = 0; j < excmd.length; j++) {
                            if (excmd[j].arg) {
                                int xOld = pAgent.x;
                                int yOld = pAgent.y;                              
                               
                                if (!excmd[j].argCus) {
                                    int mainNo = excmd[j].cmd.GetNo();

                                    if (mainNo == no) {
                                        int argNo = excmd[j].cmd.GetNoArg();
                                        
                                        switch (argNo) {
                                            
                                            case 1: {
                                                
                                                if (pAgent.x > 0) {   
                                                    isOccupied =  checkIfIsOccupied(pAgent.x-1, pAgent.y);
                                                    isObstacle =  checkIfIsObstacle(pAgent.x-1, pAgent.y);
                                                    pAgent.x -= 1;
                                                     int n;
                                                     n = 3;
                                                     int k = losuj(n); 
                                                    if(k==0)
                                                    addTextToList( "Dojechałem. Co dalej?");
                                                    if(k==1)
                                                    addTextToList( "Jestem na miejscu.");
                                                    if(k==2)
                                                    addTextToList( "Polecenie wykonane");
                                                    if(k==3)
                                                    addTextToList( "Dojechałem na wyznaczone miejsce");
                                                    gdzie=gdzie+1;
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                    addTextToList( "Co dalej?");
                                                }
                                                break;
                                            }
                                            case 2: {
                                                if (pAgent.y > 0) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x, pAgent.y - 1);
                                                    isObstacle =  checkIfIsObstacle(pAgent.x, pAgent.y - 1);
                                                    pAgent.y -= 1;
                                                    int n;
                                                     n = 3;
                                                     int k = losuj(n); 
                                                    if(k==0)
                                                    addTextToList( "Dojechałem. Co dalej?");
                                                    if(k==1)
                                                    addTextToList( "Jestem na miejscu.");
                                                    if(k==2)
                                                    addTextToList( "Polecenie wykonane");
                                                    if(k==3)
                                                    addTextToList( "Dojechałem na wyznaczone miejsce");
                                                    gdzie=gdzie+1;
                                                }
                                                else
                                                {
                                                    addTextToList( Informations.OutsideEdge);
                                                    addTextToList( "Co dalej?");
                                                }
                                                break;
                                            }
                                            case 3: {
                                                if (pAgent.x < board.width - 1) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x+1, pAgent.y);
                                                    isObstacle =  checkIfIsObstacle(pAgent.x+1, pAgent.y);
                                                    pAgent.x += 1;
                                                    int n;
                                                     n = 3;
                                                     int k = losuj(n); 
                                                    if(k==0)
                                                    addTextToList( "Dojechałem. Co dalej?");
                                                    if(k==1)
                                                    addTextToList( "Jestem na miejscu.");
                                                    if(k==2)
                                                    addTextToList( "Polecenie wykonane");
                                                    if(k==3)
                                                    addTextToList( "Dojechałem na wyznaczone miejsce");
                                                    gdzie=gdzie+1;
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                    addTextToList( "Co dalej?");
                                                }
                                                break;
                                            }
                                            case 4: {
                                                if (pAgent.y < board.height - 1) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x, pAgent.y + 1);
                                                    isObstacle =  checkIfIsObstacle(pAgent.x, pAgent.y + 1);
                                                    pAgent.y += 1;
                                                    int n;
                                                     n = 3;
                                                     int k = losuj(n); 
                                                    if(k==0)
                                                    addTextToList( "Dojechałem. Co dalej?");
                                                    if(k==1)
                                                    addTextToList( "Jestem na miejscu.");
                                                    if(k==2)
                                                    addTextToList( "Polecenie wykonane");
                                                    if(k==3)
                                                    addTextToList( "Dojechałem na wyznaczone miejsce");
                                                    gdzie=gdzie+1;
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                    addTextToList( "Co dalej?");
                                                }
                                                break;
                                            }
                                            
                                            
                                            case 5:{
                                                //kierowanie pomocnikiem
                                                 pozX=pAgent.x*40;
                                                 pozY=pAgent.y*40;
                                                 addTextToList( "Pomoc na miejscu. Co dalej?");
                                                 gdzie=gdzie+1;
                                                break;
                                                }
                                            case 6:{
                                                
                                                 pAgent.y =1;
                                                 pAgent.x =2;
                                                 gdzie=gdzie+1;
                                                 addTextToList( "Jestem na polu pierwszym.");
                                                break;
                                                }
                                            case 7:{
                                                pAgent.y =1;
                                                 pAgent.x =8;
                                                 gdzie=gdzie+1;
                                                 addTextToList( "Jestem na polu drugim.");
                                                break;
                                                }
                                            case 8:{
                                                pAgent.y =4;
                                                 pAgent.x =2;
                                                 gdzie=gdzie+1;
                                                 addTextToList( "Jestem na polu trzecim.");
                                                break;
                                                }
                                            case 9:{
                                                pAgent.y =4;
                                                 pAgent.x =8;
                                                 gdzie=gdzie+1;
                                                 addTextToList( "Jestem na polu czwartym.");
                                                break;
                                                }
                                            case 10:{
                                                pAgent.y =7;
                                                 pAgent.x =2;
                                                 gdzie=gdzie+1;
                                                 addTextToList( "Jestem na polu piątym.");
                                                break;
                                                }
                                            case 11:{
                                                pAgent.y =7;
                                                 pAgent.x =8;
                                                 gdzie=gdzie+1;
                                                 addTextToList( "Jestem na polu szóstym.");
                                                break;
                                                }
                                            
                                            
                                                   
                                               
                                
                                        }
                                       
                                    }
                                } else {    
                                    int tmpx = excmd[j].GetX();
                                    int tmpy = excmd[j].GetY();
                                    
                                    if(tmpx >= 0 && tmpx < board.getWidth())
                                    {
                                        if(tmpy >= 0 && tmpy < board.getHeight())
                                        {
                                            isOccupied = checkIfIsOccupied(tmpx, tmpy);
                                            isObstacle = checkIfIsObstacle(tmpx, tmpy);
                                            pAgent.x = excmd[j].GetX();
                                            pAgent.y = excmd[j].GetY();
                                            int n;
                                                     n = 3;
                                                     int k = losuj(n); 
                                                    if(k==0)
                                                    addTextToList( "Dojechałem. Co dalej?");
                                                    if(k==1)
                                                    addTextToList( "Jestem na miejscu.");
                                                    if(k==2)
                                                    addTextToList( "Polecenie wykonane");
                                                    if(k==3)
                                                    addTextToList( "Dojechałem na wyznaczone miejsce");
                                            gdzie=gdzie+1;
                                        }
                                        else
                                        {
                                            addTextToList(Informations.OutsideEdge);
                                            addTextToList( "Co dalej?");
                                        }
                                    }
                                    else
                                    {
                                        addTextToList( Informations.OutsideEdge);
                                        addTextToList( "Co dalej?");
                                    }
                                    
                                }
                                
                                if(!isOccupied && !isObstacle)
                                {
                                    board.DeleteObject(xOld, yOld, true);
                                    board.SetObject(pAgent.x, pAgent.y, true, agent); 
                                }
                                else
                                {   
                                    if(isOccupied){
                                    addTextToList(Informations.ExistStore);
                                    }else{
                                    addTextToList(Informations.Obstacle);
                                    }
                                }
                                
                                
                                
                  
                                
                            }
                            
                        }
                        if(gdzie==0)     
                                {
                                int n;
                                                     n = 3;
                                                     int k = losuj(n); 
                                                    if(k==0){
                                                    addTextToList("Wpisz np. jedz w górę");
                                                    addTextToList("Nie wiem, gdzie mam jechać. ;(");
                                                    }
                                                    if(k==1){
                                                        addTextToList("Spróbuj wpisać chociażby 'jedz 2 2' albo 'jedz gora'");
                                                    addTextToList( "Chyba coś źle wpisałeś, bo zupełnie nie rozumiem polecenia.");
                                                    }
                                                    if(k==2){
                                                    addTextToList( "nie czytam w myślach. :)");
                                                    addTextToList( "Jestem prostym programem,");}
                                                    if(k==3)
                                                    addTextToList( "Nie rozumiem o co Ci chodzi");    
                                
                                }
                        break;
                    }

                    case 2: //oranie
                    {
                        int gdzie=0;
                        int ilek=0;
                        for (int j = 0; j < excmd.length; j++) {
                          
                                if (!excmd[j].argCus) {
                                    int mainNo = excmd[j].cmd.GetNo();

                                    if (mainNo == no) {
                                        int argNo = excmd[j].cmd.GetNoArg();
                                        
                                        switch (argNo) {
                                            case 1: {
                        
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                            
                        }
                        else{
                             
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                             if(ilek>0)
                            {
                                
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                            addTextToList("ilość przeszkód: "+ilek);
                            }
                                            
                                                                        break;
                                                                        }
                                            
                        case 2: {
                        
                      
                                            
                          for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                            
                        }
                        else{
                             
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                             if(ilek>0)
                            {
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                            addTextToList("ilość przeszkód: "+ilek);
                            }
                                            
                                                                        break;
                                                                        }
                        case 3: {
                        
                      
                        for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                             isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                            
                        }
                        else{
                             
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                             if(ilek>0)
                            {
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                            addTextToList(" ilość przeszkód: "+ilek);
                            }
                                            
                                                                        break;
                                                                        }
                        case 4: {
                        
                      
                        for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                             isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                            
                        }
                        else{
                             
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                             if(ilek>0)
                            {
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                            addTextToList(" ilość przeszkód: "+ilek);
                            }
                                            
                                                                        break;
                                                                        }
                        case 5: {
                        
                      
                        for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                             isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                            
                        }
                        else{
                             
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                             if(ilek>0)
                            {
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                            addTextToList(" ilość przeszkód: "+ilek);
                            }
                                            
                                                                        break;
                                                                        }
                        case 6: {
                        
                      
                        for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                             isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                            
                        }
                        else{
                             
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                             if(ilek>0)
                            {
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                            addTextToList(" ilość przeszkód: "+ilek);
                            }
                                            
                                                                        break;
                                                                        }
                        case 7: {
                        
                      
                       
                        
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                           ilek=ilek+1;
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                                            
                                                                      
                                            
                          for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                                           
                        
                      
                        for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                                           
                         
                        for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                                           
                                           
                        
                        for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                                           
                                           
                      
                        for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                            
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        gdzie=gdzie +1;
                        }
                            }
                        }
                         if(ilek>0)
                            {
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                            addTextToList(" ilość przeszkód: "+ilek);
                            }
                                           
                                                                        break;
                                                                        }
                                        }}}}
                        if(gdzie==0)
                        {
                            int n;
                                                     n = 3;
                                                     int k = losuj(n); 
                                                    if(k==0)                        
                        addTextToList("Nie wiem co zaorać. :(");
                                                    if(k==1)                        
                        addTextToList("Co mam orać?");
                                                    if(k==2)                        
                        addTextToList("Chyba o czymś zapomniałeś.");
                                                    if(k==3)                        
                        addTextToList("Nie umiem czytać Ci w myślach. Precyzyjniej proszę!!!");
                        }
                                                                      break;
                                                                        }
                    
                    case 10:
                    {
                        
                        
                        int kamien=0;
                        if(pAgent.y==board.width-11)
                        {
                                              
                        addTextToList("GRANICA!!!");
                      
                        }
                        else{
                       isObstacle =  checkIfIsObstacle(pAgent.x-1, pAgent.y);
                        if(isObstacle)
                        {
                            kamien=kamien+1;
                            board.DeleteObject(pAgent.x-1, pAgent.y, false);
                            int n;
                                                     n = 3;
                                                     int k = losuj(n); 
                                                    if(k==0)     
                            addTextToList("Usunięto przeszkodę.");
                                                    if(k==1)     
                            addTextToList("Zadanie wykonano.");
                                                    if(k==2)     
                            addTextToList("Usunięta.");
                                                    if(k==3)     
                            addTextToList("Kamień usunięty.");
                           
                        }
                        }
                         if(pAgent.x==1 && pAgent.y==9)
                        {
                            int n;
                                                     n = 2;
                                                     int k = losuj(n); 
                                                    if(k==0)          
                        addTextToList("Nie będę usuwać hangaru!!!");
                                                    if(k==1)          
                        addTextToList("Widzisz tu jakiś kamień?");
                                                    if(k==2)          
                        addTextToList("nawet nie będę próbował usuwać hangaru.");
                        kamien=kamien+1;
                        }
                        
                        if(kamien==0){
                            int n;
                                                     n = 3;
                                                     int k = losuj(n); 
                                                    if(k==0)  
                        addTextToList("Nie widzę przede mną żadnej przeszkody. A Ty widzisz?");  
                                                    if(k==1)  
                        addTextToList("Brak przeszkody");   
                                                    if(k==2)  
                        addTextToList("Czytałeś w ogóle moją dokumentację?"); 
                                                    if(k==3)  
                        addTextToList("Nie widzę nic do usunięcia."); 
                        }
                        kamien=0;
                        
                        
                        
                    break;    
                    }
                    
                    
                    
                    
                    
                    
                    
                    case 3: //sianie
                    {    
                        int gdzie=0;
                        int ilek=0;
                        for (int j = 0; j < excmd.length; j++) {
                          
                                if (!excmd[j].argCus) {
                                    int mainNo = excmd[j].cmd.GetNo();

                                    if (mainNo == no) {
                                        int argNo = excmd[j].cmd.GetNoArg();
                                        
                                        switch (argNo) {
                                            case 1: {
                                            SowObjectPszenica op1 = new SowObjectPszenica();    
                                            gdzie=gdzie+1;
                                                
                        isRoute = checkIfIsRoute(pAgent.x, pAgent.y);
                        if(isRoute)
                        {
                            addTextToList("Nie można uprawiac na drodze");
                        }
                        else{
                         isPlow =  checkIfIsPlow(pAgent.x, pAgent.y);
                         if(isPlow)
                         {
                             
                             
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1; 
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        
                        }
                            
                            
                            }
                            
                        }
                          addTextToList("Posiałem pszenicę na polu pierwszym.");
                          if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1; 
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Posiałem pszenicę na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1; 
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                            addTextToList("Posiałem pszenicę na polu trzecim.");
                            if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1; 
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Posiałem pszenicę na polu czwartym.");
                           if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1; 
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Posiałem pszenicę na polu piątym.");
                          if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                           ilek=ilek+1; 
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Posiałem pszenicę na polu szóstym. Wyczuwam dobry zysk.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         }
                         else
                         {
                            ilek=ilek+1;       
                         }}
                      break;
                                                                        }
                                            case 2: {
                                            SowObjectBataty op1 = new SowObjectBataty();    
                                            gdzie=gdzie+1;
                                                
                        isRoute = checkIfIsRoute(pAgent.x, pAgent.y);
                        if(isRoute)
                        {
                            addTextToList("Nie można uprawiac na drodze");
                        }
                        else{
                         isPlow =  checkIfIsPlow(pAgent.x, pAgent.y);
                         if(isPlow)
                         {
                             
                             
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                           ilek=ilek+1; 
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }addTextToList("Posiałem bataty na polu pierszym. Wyczuwam dobry zysk.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1; 
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Posiałem bataty na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1; 
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                            addTextToList("Posiałem bataty na polu trzecim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1; 
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Posiałem bataty na polu czwartym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1; 
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Posiałem bataty na polu piątym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1; 
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Posiałem bataty na polu szóstym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         }
                         else
                         {
                            addTextToList( "Pole musi zostać najpierw zaorane");      
                         }}
                      break;
                                                                        }
                                            case 3: {
                                            SowObjectHerbata op1 = new SowObjectHerbata();    
                                            gdzie=gdzie+1;
                                                
                        isRoute = checkIfIsRoute(pAgent.x, pAgent.y);
                        if(isRoute)
                        {
                            addTextToList( "Nie można uprawiac na drodze");
                        }
                        else{
                         isPlow =  checkIfIsPlow(pAgent.x, pAgent.y);
                         if(isPlow)
                         {
                             
                             
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Posiałem herbatę na polu pierwszym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Posiałem herbatę na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                            addTextToList("Posiałem herbatę na polu trzecim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Posiałem herbatę na polu czwartym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Posiałem herbatę na polu piątym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Posiałem herbatę na polu szóstym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         }
                         else
                         {
                            addTextToList(  "Pole musi zostać najpierw zaorane");      
                         }}
                      break;
                                                                        }
                                            case 4: {
                                            SowObjectKawa op1 = new SowObjectKawa();    
                                            gdzie=gdzie+1;
                                                
                        isRoute = checkIfIsRoute(pAgent.x, pAgent.y);
                        if(isRoute)
                        {
                            addTextToList( "Nie można uprawiac na drodze");
                        }
                        else{
                         isPlow =  checkIfIsPlow(pAgent.x, pAgent.y);
                         if(isPlow)
                         {
                             
                             
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Posiałem kawę na polu pierwszym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Posiałem kawę na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                            addTextToList("Posiałem kawę na polu trzecim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Posiałem kawę na polu czwartym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Posiałem kawę na polu piątym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Posiałem kawę na polu szóstym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         }
                         else
                         {
                            addTextToList("Pole musi zostać najpierw zaorane");      
                         }}
                      break;
                                                                        }
                                            case 5: {
                                            SowObjectOliwki op1 = new SowObjectOliwki();    
                                            gdzie=gdzie+1;
                                                
                        isRoute = checkIfIsRoute(pAgent.x, pAgent.y);
                        if(isRoute)
                        {
                            addTextToList("Nie można uprawiac na drodze");
                        }
                        else{
                         isPlow =  checkIfIsPlow(pAgent.x, pAgent.y);
                         if(isPlow)
                         {
                             
                             
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Posiałem oliwki na polu pierwszym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             
                             addTextToList("Posiałem oliwki na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                            addTextToList("Posiałem oliwki na polu trzecim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Posiałem oliwki na polu czwartym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Posiałem oliwki na polu piątym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Posiałem oliwki na polu szóstym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         }
                         else
                         {
                            addTextToList("Pole musi zostać najpierw zaorane");      
                         }}
                      break;
                                                                        }
                                            case 6: {
                                            SowObjectJeczmien op1 = new SowObjectJeczmien();    
                                            gdzie=gdzie+1;
                                                
                        isRoute = checkIfIsRoute(pAgent.x, pAgent.y);
                        if(isRoute)
                        {
                            addTextToList("Nie można uprawiac na drodze");
                        }
                        else{
                         isPlow =  checkIfIsPlow(pAgent.x, pAgent.y);
                         if(isPlow)
                         {
                             
                             
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Posiałem jęczmień na polu pierwszym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Posiałem jęczmień na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                            addTextToList("Posiałem jęczmień na polu trzecim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Posiałem jęczmień na polu czwartym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Posiałem jęczmień na polu piątym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Posiałem jęczmień na polu szóstym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         }
                         else
                         {
                            addTextToList( "Pole musi zostać najpierw zaorane");      
                         }}
                      break;
                                                                        }
                                        }}}}
                        if(gdzie==0)
                                { 
                                  addTextToList( "-oliwki");
                                  addTextToList( "-Kawa");
                                  addTextToList( "-Jęczmień");
                                  addTextToList( "-Herbata");
                                  addTextToList( "-Bataty");
                                  addTextToList( "-Pszenica");
                                  addTextToList( "Ziarna do siania:");
                                  addTextToList( "Napisz mi co mam zasiać np. 'siej bataty'.");
                                  addTextToList( "Nie wiem jakim rodzajem posiać dane pole,");
                                  
  
                                  
                                  
                                
                                
                                }
                                                                      break;
                                                                        }
                    case 4: //zbieranie
                    {   
                        int ilek = 0;
                        isRoute = checkIfIsRoute(pAgent.x, pAgent.y);
                        if(isRoute)
                        {
                            addTextToList("Nie można uprawiac na drodze");
                        }
                        else{
                        
                        if(checkIfIsWaterPszenica(pAgent.x, pAgent.y))
                        {
                            CollectingObject op1 = new CollectingObject();
                            if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        board.DeleteObject(a, b, false);
                        
                        }
                            }
                        }
                          
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                           
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            int a=100;
                            
                          
                            addTextToList("Zebrano 100kg - ilość przeszkód("+ilek+") * 10 co daje nam: " +(a-ilek*10)+ "kg pszenicy");
                            Zasoby.z.ModyfikujZb1(a-ilek*10);
                            addTextToList("Aktualny stan: "+ Zasoby.z.GetZb1() + "kg");
                         }
                        
                        else if(checkIfIsWaterBataty(pAgent.x, pAgent.y))
                        {
                            CollectingObject op1 = new CollectingObject();
                            if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                          
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                           
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            
                            
                            int a=230;
                            
                          
                            addTextToList("Zebrano 230kg - ilość przeszkód("+ilek+") * 10 co daje nam: " +(a-ilek*10)+ "kg batatów");
                            Zasoby.z.ModyfikujZb2(a-ilek*10);
                            addTextToList("Aktualny stan: "+ Zasoby.z.GetZb2() + "kg");
                            
                         }
                        
                        
                        
                        else if(checkIfIsWaterHerbata(pAgent.x, pAgent.y))
                        {
                            CollectingObject op1 = new CollectingObject();
                            if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                          
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                           
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            
                            
                            int a=60;
                            
                          
                            addTextToList("Zebrano 60kg - ilość przeszkód("+ilek+") * 5 co daje nam: " +(a-ilek*5)+ "kg herbaty");
                            Zasoby.z.ModyfikujZb6(a-ilek*5);
                            addTextToList("Aktualny stan: "+ Zasoby.z.GetZb6() + "kg");
                         }
                        
                        
                        else if(checkIfIsWaterJeczmien(pAgent.x, pAgent.y))
                        {
                            CollectingObject op1 = new CollectingObject();
                            if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                          
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                           
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            
                            
                             int a=140;
                            
                          
                            addTextToList("Zebrano 140kg - ilość przeszkód("+ilek+") * 10 co daje nam: " +(a-ilek*10)+ "kg jęczmienia");
                            Zasoby.z.ModyfikujZb4(a-ilek*10);
                            addTextToList("Aktualny stan: "+ Zasoby.z.GetZb4() + "kg");
                         }
                        
                        
                        else if(checkIfIsWaterKawa(pAgent.x, pAgent.y))
                        {
                            CollectingObject op1 = new CollectingObject();
                            if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                          
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                           
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            
                            
                            int a=200;
                            
                          
                            addTextToList("Zebrano 200kg - ilość przeszkód("+ilek+") * 15 co daje nam: " +(a-ilek*15)+ "kg kawy");
                            Zasoby.z.ModyfikujZb5(a-ilek*15);
                            addTextToList("Aktualny stan: "+ Zasoby.z.GetZb5() + "kg");
                         }
                        
                        else if(checkIfIsWaterOliwki(pAgent.x, pAgent.y))
                        {
                            CollectingObject op1 = new CollectingObject();
                            if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                          
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                       board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                           
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            
                            int a=123;
                            addTextToList("Zebrano 123kg - ilość przeszkód("+ilek+") * 12 co daje nam: " +(a-ilek*12)+ "kg kawy");
                            Zasoby.z.ModyfikujZb3(a-ilek*12);
                            addTextToList("Aktualny stan: "+ Zasoby.z.GetZb3() + "kg");
                         }
                        
                         else
                         {
                            addTextToList("Pole musi zostać najpierw zaorane, posiane i podlane");      
                         }}
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        break;
                    }
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                             
                    
                    
                    case 5: //podlewanie
                    {   
                        int ilek=0;
                        isRoute = checkIfIsRoute(pAgent.x, pAgent.y);
                        if(isRoute)
                        {
                            addTextToList("Nie można uprawiac na drodze");
                        }
                        else{
                       
                        if(checkIfIsSowPszenica(pAgent.x, pAgent.y))
                        {
                            WaterObjectPszenica op1 = new WaterObjectPszenica();
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Podlałem pszenicę na polu pierwszym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Podlałem pszenicę na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }addTextToList("Podlałem pszenicę na polu trzecim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Podlałem pszenicę na polu czwartym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Podlałem pszenicę na polu piątym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Podlałem pszenicę na polu szóstym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         }
                        
                        
                        else if(checkIfIsSowBataty(pAgent.x, pAgent.y))
                        {
                            WaterObjectBataty op1 = new WaterObjectBataty();
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Podlałem bataty na polu pierwszym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Podlałem bataty na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                            addTextToList("Podlałem bataty na polu trzecim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Podlałem bataty na polu czwartym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Podlałem bataty na polu piątym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Podlałem bataty na polu szóstym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         }
                        
                        
                        
                        
                       else if(checkIfIsSowHerbata(pAgent.x, pAgent.y))
                        {
                            WaterObjectHerbata op1 = new WaterObjectHerbata();
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }addTextToList("Podlałem herbatę na polu pierwszym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                          
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Podlałem herbatę na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                            addTextToList("Podlałem herbatę na polu trzecim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Podlałem herbatę na polu czwartym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Podlałem herbatę na polu piątym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                         }
                             addTextToList("Podlałem herbatę na polu szóstym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        
                        
                        
                        else if(checkIfIsSowJeczmien(pAgent.x, pAgent.y))
                        {
                            WaterObjectJeczmien op1 = new WaterObjectJeczmien();
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                             ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Podlałem jęczmień na polu pierwszym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                             ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Podlałem jęczmień na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                             ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                            addTextToList("Podlałem jęczmień na polu trzecim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                             ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Podlałem jęczmień na polu czwartym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                             ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Podlałem jęczmień na polu piątym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Podlałem jęczmień na polu szóstym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         }
                        
                        
                        
                        
                        
                        
                        else if(checkIfIsSowOliwki(pAgent.x, pAgent.y))
                        {
                            WaterObjectOliwki op1 = new WaterObjectOliwki();
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Podlałem oliwki na polu pierwszym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                              addTextToList("Podlałem oliwki na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Podlałem oliwki na polu trzecim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                            addTextToList("Podlałem oliwki na polu czwartym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Podlałem oliwki na polu piątym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                              addTextToList("Podlałem oliwki na polu szóstym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         }
                        
                        else if(checkIfIsSowKawa(pAgent.x, pAgent.y))
                        {
                            WaterObjectKawa op1 = new WaterObjectKawa();
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                              
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Podlałem kawę na polu pierwszym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                              addTextToList("Podlałem kawę na polu drugim.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                            addTextToList("Podlałem kawę na polu trzecim.");
                            if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                           for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                           addTextToList("Podlałem kawę na polu czwartym.");
                           if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                          for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                          addTextToList("Podlałem kawę na polu piątym.");
                          if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            
                             for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            ilek=ilek+1;
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                             addTextToList("Podlałem kawę na polu szóstym.");
                             if(ilek!=0){
                          addTextToList("Jeśli je usuniesz automatycznie posieję i podleję puste pole bez potrzeby ponownego orania");
                            addTextToList("Na polu nadal są przeszkody. Aktualna ilość przeszkód: "+ilek);}
                         }
                         }
                         else
                         {
                            addTextToList("Pole musi zostać najpierw zaorane");      
                         }}
                        break;
                    }
                     
                    case 6: //stan upraw
                    {                        
                        addTextToList("Zajęcie magazynu: " + Zasoby.z.GetProcentZasoby() +"%");
                        addTextToList("Pojemność magazynu: " + Zasoby.z.GetZasobyMax() +"kg");
                        addTextToList("Łącznie: " + Zasoby.z.GetZasoby() + "kg");
                        addTextToList("  - herbata: " + Zasoby.z.GetZb6() + "kg");
                        addTextToList("  - kawa: " + Zasoby.z.GetZb5() + "kg");
                        addTextToList("  - jęczmień: " + Zasoby.z.GetZb4() + "kg");
                        addTextToList("  - oliwki: " + Zasoby.z.GetZb3() + "kg");
                        addTextToList("  - bataty: " + Zasoby.z.GetZb2() + "kg");
                        addTextToList("  - pszenica: " + Zasoby.z.GetZb1() + "kg");
                        addTextToList("Aktualny stan zbiorów:");
                       
                       break;
                    }
                    case 7:
                    {
                            //kierowanie pomocnikiem
                            pozX=50;
                            pozY=380;
                            addTextToList( "Pomoc wróciła. Co dalej?");                   
                        break;
                    }
                    
                        
                    case 11:
                    {
                        if (Ulepszenia.u.SwiatlaStan() == true) { addTextToList("Swiatla były już włączone");} else
                        { Ulepszenia.u.SwiatlaSwitch("on"); addTextToList("Wlaczylem swiatla. Rozpoczęto pobór enrgii elektrycznej"); 
                        
                        }
                        break;
                    }
                    
                    case 12:
                    {
                        if (Ulepszenia.u.SwiatlaStan() == false) { addTextToList("Swiatla były już wyłączone");} else
                        { Ulepszenia.u.SwiatlaSwitch("off"); addTextToList("Wyłączyłem światła"); 
                        
                        }
                        break;
                    }
                        
                    case 13:
                    {
                        
                        addTextToList("    - i inne...");
                        addTextToList("    - oraj [numer pola slownie] / [wszystko]");
                        addTextToList("    - wlacz/wylacz swiatla");
                        addTextToList("    - zbierz/posiej/podlej będac na danym polu");
                        addTextToList("      gdzie znajduje sie trakor");
                        addTextToList("    - gdzie jestes? - zwraca info");
                        addTextToList("      /[prawo] - porusza traktorem.");
                        addTextToList("    - przesun/pojedz [gora]/[dol]/[lewo]");
                        
                        addTextToList("Możesz skorzystać z rozmaitych komend: ");
                        break;
                    }
                        
                    case 14:
                    {
                        if (Ulepszenia.u.SwiatlaStan() == false) { addTextToList("Swiatla są wyłączone");} else
                        addTextToList( "Swiatla są włączone"); 
                        break;
                        
                    }
                        
                    case 15:
                    {
                        addTextToList( "Aktualny stan elektryczności - " + Zasoby.z.GetElektrycznosc() + "%"); 
                        break;
                        
                    }
                    case 16:
                    {
                        pozX=pAgent.x*40;
                        pozY=pAgent.y*40;
                        if((Zasoby.z.GetPaliwo()+20)>100){
                            Zasoby.z.paliwo = 100;
                        }else{
                        Zasoby.z.ModPaliwo(20.0);
                        }
                        Zasoby.z.ModyfikujStanKonta(-200);
                        addTextToList("Zatankowano. Pobrano 200€");
                        addTextToList("Aktualny Stan paliwa: " + Zasoby.z.GetPaliwo() + " litrów.");
                        addTextToList("Czy wykonać jakieś zdanie?");
                        break;
                        
                    }
                    case 17:
                    {
                        addTextToList("Aktualny stan paliwa to " + Zasoby.z.GetPaliwo() + " litrów.");
                        break;
                    }
					case 18:
                    {
                        addTextToList("Aktualny godzina to " + MechanizmCzasu.GetCzas());
                        break;
                    }
                    case 19:
                    {
                        addTextToList("Aktualna pogoda to " + GameFrame.gf.aktpog);
                        break;
                    }
                    case 20:
                    {
                        addTextToList("Stan dnia to " + MechanizmCzasu.GetPoraDnia());
                        break;
                    }
                    case 21:
                    {
                        addTextToList("Aktualny stan konta to: " + Zasoby.z.GetStanKontaString());
                        break;
                    }
                    case 22:
                    {
                        int maks;
                        maks = Ulepszenia.u.getLvlPP();
                        if(maks < 5){
                        addTextToList("Koszt ulepszenia w wysokości "+ Ulepszenia.u.getPPCenaString() + " pobrano z konta.");
                        Ulepszenia.u.CapacityPack1();
                        Ulepszenia.u.ppcena();
                        Ulepszenia.u.lvlPP();
                        addTextToList("Osiągnieto poziom " + Ulepszenia.u.getLvlPPstring()+"/5");
                        
                        double wzrost = 500.0*Ulepszenia.u.getLvlPP();
                        String wzrostString = Double.toString(wzrost);
                        addTextToList("Pojemność wzrosła o " + wzrostString + "kg");
                        Zasoby.z.CapacityPackLevelUp((500.0*Ulepszenia.u.getLvlPP()));
                        }else
                        {
                        addTextToList("Nie można już zwiększyć!");
                        } 
                        break;
                    }
                    case 24:
                    {
                        
        int n;
        n = 5;
        int k = losuj(n); 
        if(k==0)
        addTextToList("A nic ciekawego.");    
        if(k==1)
        addTextToList("Stoję tu i się nudzę.");
        if(k==2)
        addTextToList("Stoję tu i się nudzę.");
        if(k==3)
        addTextToList("Właśnie przeglądam internet. Ceny bataty poszły w górę. :(");
        if(k==4)
        addTextToList("Podziwiam właśnie moje piękne pola.");
        if(k==5)
        addTextToList("No właśnie nic! Może weźmiemy się do roboty?");
        
        
                        break;
                    }
                        
                        
                        case 26:
                    {
                        
        int n;
        n = 5;
        int k = losuj(n); 
        if(k==0)
        addTextToList("Hej, co tam?");    
        if(k==1)
        addTextToList("Siema!");
        if(k==2)
        addTextToList("Witaj");
        if(k==3)
        addTextToList("Czesc czołem kluski z rosołem");
        if(k==4)
        addTextToList("Hej, Podziwiam właśnie moje piękne pola.");
        if(k==5)
        addTextToList("Cześć! Może weźmiemy się do roboty?");
        
        
                        break;
                    }
                        
                        
                    case 25:
                    {
                        
                    addTextToList("moją pracę, nie wspominając o burzy...");   
                    addTextToList("mnie spowalnia, ale śnieg, wiatr i mgła znacznie utrudniają"); 
                    addTextToList("Przy słońcu prędkość jest bez zmian, deszcz delikatnie,");
                    addTextToList("dzięki temu znacznie poprawia mi sie prędkość.");
                    addTextToList("przy włączonych światłach poprawia mi się widoczność,");
                    addTextToList("Pilnuj pogody, gdyż im gorsze warunki tym jestem wolniejszy,");
                    
                break;}
        
                    case 23: 
                    {
//                       addTextToList("  - wszystkie: zasóball");
//                                addTextToList("  - herbata: zasób6");
//                                addTextToList("  - kawa: zasóbb5");
//                                addTextToList("  - jęczmień: zasób4");
//                                addTextToList("  - oliwki: zasób3");
//                                addTextToList("  - bataty: zasób2");
//                                addTextToList("  - pszenica: zasób1");
//                                addTextToList("Aby sprzedać zasób podaj przypisaną mu nazwe:");       
                        int ilek=0;
                        for (int j = 0; j < excmd.length; j++) {
                                
                                if (!excmd[j].argCus) {
                                    int mainNo = excmd[j].cmd.GetNo();

                                    if (mainNo == no) {
                                        int argNo = excmd[j].cmd.GetNoArg();
                                        
                                        switch (argNo){
                                        case 1: 
                                        {   
                                            int masa = Zasoby.z.GetZb1();
                                            masa = masa*25;
                                            addTextToList("Zarobiłeś " + masa + "€");
                                            addTextToList( "Sprzedałeś " + Zasoby.z.GetZb1() + "kg pszenicy.");
                                            addTextToList( "sprzedajesz cały zbiór 1");
                                            Zasoby.z.ModyfikujZb1(-Zasoby.z.GetZb1());
                                            Zasoby.z.ModyfikujStanKonta(masa);
//                       
                                            break;
                                        }
                                        case 2: 
                                        {
                                            int masa = Zasoby.z.GetZb2();
                                            masa = masa*40;
                                            addTextToList("Zarobiłeś " + masa + "€");
                                            addTextToList( "Sprzedałeś " + Zasoby.z.GetZb2() + "kg batatów.");
                                            addTextToList( "sprzedajesz cały zbiór 2");
                                            Zasoby.z.ModyfikujZb2(-Zasoby.z.GetZb2());
                                            Zasoby.z.ModyfikujStanKonta(masa);
                                            break;
                                        }
                                        case 3: 
                                        {
                                            int masa = Zasoby.z.GetZb3();
                                            masa = masa*50;
                                            addTextToList("Zarobiłeś " + masa + "€");
                                            addTextToList( "Sprzedałeś " + Zasoby.z.GetZb3() + "kg oliwek.");
                                            addTextToList( "sprzedajesz cały zbiór 3");
                                            Zasoby.z.ModyfikujZb3(-Zasoby.z.GetZb3());
                                            Zasoby.z.ModyfikujStanKonta(masa);
                                            break;
                                        }
                                        case 4: 
                                        {
                                            int masa = Zasoby.z.GetZb4();
                                            masa = masa*10;
                                            addTextToList("Zarobiłeś " + masa + "€");
                                            addTextToList( "Sprzedałeś " + Zasoby.z.GetZb4() + "kg jęczmienia.");
                                            addTextToList( "sprzedajesz cały zbiór 4");
                                            Zasoby.z.ModyfikujZb4(-Zasoby.z.GetZb4());
                                            Zasoby.z.ModyfikujStanKonta(masa);
                                            break;
                                        }
                                        case 5: 
                                        {
                                            int masa = Zasoby.z.GetZb5();
                                            masa = masa*15;
                                            addTextToList("Zarobiłeś " + masa + "€");
                                            addTextToList( "Sprzedałeś " + Zasoby.z.GetZb5() + "kg kawy.");
                                            addTextToList( "sprzedajesz cały zbiór 5");
                                            Zasoby.z.ModyfikujZb5(-Zasoby.z.GetZb5());
                                            Zasoby.z.ModyfikujStanKonta(masa);
                                            break;
                                        }
                                        case 6: 
                                        {
                                            int masa = Zasoby.z.GetZb6();
                                            masa = masa*5;
                                            addTextToList("Zarobiłeś " + masa + "€");
                                            addTextToList( "Sprzedałeś " + Zasoby.z.GetZb6() + "kg herbaty.");
                                            addTextToList( "sprzedajesz cały zbiór 6");
                                            Zasoby.z.ModyfikujZb6(-Zasoby.z.GetZb6());
                                            Zasoby.z.ModyfikujStanKonta(masa);
                                            break;
                                        }
                                        case 7: 
                                        {
                                            int masa = Zasoby.z.GetZb1()+Zasoby.z.GetZb2()+Zasoby.z.GetZb3()+Zasoby.z.GetZb4()+Zasoby.z.GetZb5()+Zasoby.z.GetZb6();
                                            int cena = Zasoby.z.GetZb1()*25+Zasoby.z.GetZb2()*40+Zasoby.z.GetZb3()*50+Zasoby.z.GetZb4()*10+Zasoby.z.GetZb5()*15+Zasoby.z.GetZb6()*5;
                                            addTextToList("Zarobiłeś " + cena + "€");
                                            addTextToList("Razem " + masa+"kg");
                                            addTextToList( Zasoby.z.GetZb5() + "kg kawy,"+ Zasoby.z.GetZb6() + "kg herbaty.");
                                            addTextToList( Zasoby.z.GetZb3() + "kg jęczmienia,"+ Zasoby.z.GetZb4() + "kg oliwek,");
                                            addTextToList( "Sprzedałeś " + Zasoby.z.GetZb1() + "kg pszenicy,"+ Zasoby.z.GetZb2() + "kg batatów,");
                                            addTextToList( "sprzedajesz wszystko");
                                            Zasoby.z.ModyfikujZb1(-Zasoby.z.GetZb1());
                                            Zasoby.z.ModyfikujZb2(-Zasoby.z.GetZb2());
                                            Zasoby.z.ModyfikujZb3(-Zasoby.z.GetZb3());
                                            Zasoby.z.ModyfikujZb4(-Zasoby.z.GetZb4());
                                            Zasoby.z.ModyfikujZb5(-Zasoby.z.GetZb5());
                                            Zasoby.z.ModyfikujZb6(-Zasoby.z.GetZb6());
                                            Zasoby.z.ModyfikujStanKonta(cena);
                                            break;
                                        }
                                        }}}}
                        break;
                    }
                     
                    case 8:
                    {
                     
                       
                    
                        if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2)
                        {
                            addTextToList("Jestem na polu pierwszym"); 
                            addTextToList("Dokładna pozycja to: " + pAgent.x + " " + pAgent.y); 
                            if(checkIfIsWaterBataty(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajdują się podlane bataty");    
                            }
                            else if(checkIfIsWaterPszenica(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlana pszenica");    
                            }
                            else if(checkIfIsWaterKawa(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlana kawa");    
                            }
                            else if(checkIfIsWaterHerbata(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlana herbata");    
                            }
                            else if(checkIfIsWaterJeczmien(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlany jęczmień");    
                            }
                            else if(checkIfIsWaterOliwki(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajdują się podlane oliwki");    
                            }
                             
                             
                             
                             
                            else if(checkIfIsSowPszenica(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiana pszenica");       
                            }
                            else if(checkIfIsSowOliwki(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajdują się zasiane oliwki");       
                            }
                            else if(checkIfIsSowBataty(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajdują się zasiane bataty");       
                            }
                            else if(checkIfIsSowHerbata(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiana herbata");       
                            }
                            else if(checkIfIsSowJeczmien(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiany jęczmień");       
                            }
                            else if(checkIfIsSowKawa(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiana kawa");       
                            }
                            else if(checkIfIsPlow(pAgent.x, pAgent.y)){
                            addTextToList("Ziemia na której stoję jest zaorana");           
                            }
                            else
                            {
                             addTextToList("Ziemia na której stoję jest ubita");   
                            }
                        }
                        
                        
                         else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2))
                         {
                           addTextToList( "Jestem na polu drugim"); 
                           addTextToList("Dokładna pozycja to: " + pAgent.x + " " + pAgent.y); 
                            if(checkIfIsWaterBataty(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajdują się podlane bataty");    
                            }
                            else if(checkIfIsWaterPszenica(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlana pszenica");    
                            }
                            else if(checkIfIsWaterKawa(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlana kawa");    
                            }
                            else if(checkIfIsWaterHerbata(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlana herbata");    
                            }
                            else if(checkIfIsWaterJeczmien(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlany jęczmień");    
                            }
                            else if(checkIfIsWaterOliwki(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajdują się podlane oliwki");    
                            }
                             
                             
                             
                             
                            else if(checkIfIsSowPszenica(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajduje się zasiana pszenica");       
                            }
                            else if(checkIfIsSowOliwki(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajdują się zasiane oliwki");       
                            }
                            else if(checkIfIsSowBataty(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajdują się zasiane bataty");       
                            }
                            else if(checkIfIsSowHerbata(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajduje się zasiana herbata");       
                            }
                            else if(checkIfIsSowJeczmien(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiany jęczmień");       
                            }
                            else if(checkIfIsSowKawa(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiana kawa");       
                            }
                            else if(checkIfIsPlow(pAgent.x, pAgent.y)){
                            addTextToList( "Ziemia na której stoję jest zaorana");           
                            }
                            else
                            {
                             addTextToList( "Ziemia na której stoję jest ubita");   
                            }
                         }
                        
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5)
                        {
                           addTextToList( "Jestem na polu trzecim"); 
                           addTextToList( "Dokładna pozycja to: " + pAgent.x + " " + pAgent.y); 
                            if(checkIfIsWaterBataty(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajdują się podlane bataty");    
                            }
                            else if(checkIfIsWaterPszenica(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajduje się podlana pszenica");    
                            }
                            else if(checkIfIsWaterKawa(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlana kawa");    
                            }
                            else if(checkIfIsWaterHerbata(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlana herbata");    
                            }
                            else if(checkIfIsWaterJeczmien(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlany jęczmień");    
                            }
                            else if(checkIfIsWaterOliwki(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajdują się podlane oliwki");    
                            }
                             
                             
                             
                             
                            else if(checkIfIsSowPszenica(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajduje się zasiana pszenica");       
                            }
                            else if(checkIfIsSowOliwki(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajdują się zasiane oliwki");       
                            }
                            else if(checkIfIsSowBataty(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajdują się zasiane bataty");       
                            }
                            else if(checkIfIsSowHerbata(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajduje się zasiana herbata");       
                            }
                            else if(checkIfIsSowJeczmien(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajduje się zasiany jęczmień");       
                            }
                            else if(checkIfIsSowKawa(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiana kawa");       
                            }
                            else if(checkIfIsPlow(pAgent.x, pAgent.y)){
                            addTextToList( "Ziemia na której stoję jest zaorana");           
                            }
                            else
                            {
                             addTextToList( "Ziemia na której stoję jest ubita");   
                            }
                         }
                           
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5)
                         {
                           addTextToList("Jestem na polu czwartym"); 
                           addTextToList("Dokładna pozycja to: " + pAgent.x + " " + pAgent.y); 
                            if(checkIfIsWaterBataty(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajdują się podlane bataty");    
                            }
                            else if(checkIfIsWaterPszenica(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlana pszenica");    
                            }
                            else if(checkIfIsWaterKawa(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajduje się podlana kawa");    
                            }
                            else if(checkIfIsWaterHerbata(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajduje się podlana herbata");    
                            }
                            else if(checkIfIsWaterJeczmien(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajduje się podlany jęczmień");    
                            }
                            else if(checkIfIsWaterOliwki(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajdują się podlane oliwki");    
                            }
                             
                             
                             
                             
                            else if(checkIfIsSowPszenica(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiana pszenica");       
                            }
                            else if(checkIfIsSowOliwki(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajdują się zasiane oliwki");       
                            }
                            else if(checkIfIsSowBataty(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajdują się zasiane bataty");       
                            }
                            else if(checkIfIsSowHerbata(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajduje się zasiana herbata");       
                            }
                            else if(checkIfIsSowJeczmien(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajduje się zasiany jęczmień");       
                            }
                            else if(checkIfIsSowKawa(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiana kawa");       
                            }
                            else if(checkIfIsPlow(pAgent.x, pAgent.y)){
                            addTextToList( "Ziemia na której stoję jest zaorana");           
                            }
                            else
                            {
                             addTextToList( "Ziemia na której stoję jest ubita");   
                            }
                         }
                           
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8)
                         {
                           addTextToList("Jestem na polu piatym");
                           addTextToList( "Dokładna pozycja to: " + pAgent.x + " " + pAgent.y); 
                            if(checkIfIsWaterBataty(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajdują się podlane bataty");    
                            }
                            else if(checkIfIsWaterPszenica(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajduje się podlana pszenica");    
                            }
                            else if(checkIfIsWaterKawa(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajduje się podlana kawa");    
                            }
                            else if(checkIfIsWaterHerbata(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajduje się podlana herbata");    
                            }
                            else if(checkIfIsWaterJeczmien(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajduje się podlany jęczmień");    
                            }
                            else if(checkIfIsWaterOliwki(pAgent.x, pAgent.y))
                            {
                            addTextToList( "Na ziemi na której stoję znajdują się podlane oliwki");    
                            }
                             
                             
                             
                             
                            else if(checkIfIsSowPszenica(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajduje się zasiana pszenica");       
                            }
                            else if(checkIfIsSowOliwki(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajdują się zasiane oliwki");       
                            }
                            else if(checkIfIsSowBataty(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajdują się zasiane bataty");       
                            }
                            else if(checkIfIsSowHerbata(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiana herbata");       
                            }
                            else if(checkIfIsSowJeczmien(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiany jęczmień");       
                            }
                            else if(checkIfIsSowKawa(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiana kawa");       
                            }
                            else if(checkIfIsPlow(pAgent.x, pAgent.y)){
                            addTextToList( "Ziemia na której stoję jest zaorana");           
                            }
                            else
                            {
                             addTextToList( "Ziemia na której stoję jest ubita");   
                            }
                         }
                         
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8)
                            {
                           addTextToList("Jestem na polu szostym");  
                           addTextToList("Dokładna pozycja to: " + pAgent.x + " " + pAgent.y); 
                            if(checkIfIsWaterBataty(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajdują się podlane bataty");    
                            }
                            else if(checkIfIsWaterPszenica(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajduje się podlana pszenica");    
                            }
                            else if(checkIfIsWaterKawa(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajduje się podlana kawa");    
                            }
                            else if(checkIfIsWaterHerbata(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajduje się podlana herbata");    
                            }
                            else if(checkIfIsWaterJeczmien(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajduje się podlany jęczmień");    
                            }
                            else if(checkIfIsWaterOliwki(pAgent.x, pAgent.y))
                            {
                            addTextToList("Na ziemi na której stoję znajdują się podlane oliwki");    
                            }
                             
                             
                             
                             
                            else if(checkIfIsSowPszenica(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiana pszenica");       
                            }
                            else if(checkIfIsSowOliwki(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajdują się zasiane oliwki");       
                            }
                            else if(checkIfIsSowBataty(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajdują się zasiane bataty");       
                            }
                            else if(checkIfIsSowHerbata(pAgent.x, pAgent.y))
                            {
                             addTextToList( "Na ziemi na której stoję znajduje się zasiana herbata");       
                            }
                            else if(checkIfIsSowJeczmien(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiany jęczmień");       
                            }
                            else if(checkIfIsSowKawa(pAgent.x, pAgent.y))
                            {
                             addTextToList("Na ziemi na której stoję znajduje się zasiana kawa");       
                            }
                            else if(checkIfIsPlow(pAgent.x, pAgent.y)){
                            addTextToList("Ziemia na której stoję jest zaorana");           
                            }
                            else
                            {
                             addTextToList("Ziemia na której stoję jest ubita");   
                            }
                         }
                         else{
                           addTextToList("Jestem na drodze");
                           addTextToList("Dokładna pozycja to: " + pAgent.x + " " + pAgent.y); 
                           
                             
                             
                             
                         }
                        
                             
                            
                             
                        
                        
                        
                        
                        
                        break;
                        
                    }
                    
                }
            }
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }
    
    private void drawBoard(Graphics g)
    {
        
        g.drawImage(getSprite("backgorund.jpg"), 0  , 0 ,this);
        
        scaleX = getWidth() / board.width;
        scaleY = getHeight() / board.height;
        
        for(int i = 0;i < board.getWidth();i++)
            for(int j = 0;j < board.getHeight();j++)
            {
                //tutaj laduje sie obrazek traktora, ktory jest wykrywany jako ten p.agent i pomicnik,
                //ale on akurat mogl chyba sie ladowac w kazdym miejscu etody
                if(board.CheckPool(i, j, true))
                {
                    
                    g.drawImage(getSprite("tlo21.png"), i*scaleX, j*scaleY,this);    
                        
                    g.drawImage(getSprite("track.png"), i*scaleX+2, j*scaleY+2,this);
                    
                    g.drawImage(getSprite("techn.png"), pozX, pozY,this);
                        
                }
                else if(board.CheckPool(i, j, false))
                {
                    PoolObject po = board.GetObject(i, j, false);
                    if(po instanceof ObjectStore)
                    {     
                        //hangar
                        g.setColor(Color.GREEN);
                        g.fillRect(i*scaleX, j*scaleY, 40, 40);
                         g.drawImage(getSprite("han.jpg"), i*scaleX, j*scaleY,this);

                    }
                    if(po instanceof ObjectPackage)
                    {     
                        //kamienie
                        g.drawImage(getSprite("tlo21.png"), i*scaleX, j*scaleY,this);   
                        g.drawImage(getSprite("kamien.png"), i*scaleX+2, j*scaleY+2,this);
                         
                    }
                    if(po instanceof PlowObject)
                    {
                        //oranie
                        g.drawImage(getSprite("zaorane.jpg"),i*scaleX, j*scaleY,this);
                    }
                    if(po instanceof SowObjectPszenica)
                    {
                        //zasianie
                        g.drawImage(getSprite("pszenica.jpg"),i*scaleX, j*scaleY,this);
                    }
                       if(po instanceof SowObjectBataty)
                    {
                        //zasianie
                        g.drawImage(getSprite("bataty.jpg"),i*scaleX, j*scaleY,this);
                    }
                          if(po instanceof SowObjectKawa)
                    {
                        //zasianie
                        g.drawImage(getSprite("kawa.jpg"),i*scaleX, j*scaleY,this);
                    }
                             if(po instanceof SowObjectOliwki)
                    {
                        //zasianie
                        g.drawImage(getSprite("oliwki.jpg"),i*scaleX, j*scaleY,this);
                    }
                                if(po instanceof SowObjectHerbata)
                    {
                        //zasianie
                        g.drawImage(getSprite("herbata.jpg"),i*scaleX, j*scaleY,this);
                    }
                                   if(po instanceof SowObjectJeczmien)
                    {
                        //zasianie
                        g.drawImage(getSprite("jeczmien.jpg"),i*scaleX, j*scaleY,this);
                    }
                    
                    if(po instanceof WaterObjectPszenica)
                    {
                        //podlane
                        g.drawImage(getSprite("podlanapszenica.jpg"),i*scaleX, j*scaleY,this);
                    }
                    if(po instanceof WaterObjectBataty)
                    {
                        //podlane
                        g.drawImage(getSprite("podlanebataty.jpg"),i*scaleX, j*scaleY,this);
                    }
                    if(po instanceof WaterObjectHerbata)
                    {
                        //podlane
                        g.drawImage(getSprite("podlanaherbata.jpg"),i*scaleX, j*scaleY,this);
                    }
                    if(po instanceof WaterObjectJeczmien)
                    {
                        //podlane
                        g.drawImage(getSprite("podlanyjeczmien.jpg"),i*scaleX, j*scaleY,this);
                    }
                    if(po instanceof WaterObjectOliwki)
                    {
                        //podlane
                        g.drawImage(getSprite("podlaneoliwki.jpg"),i*scaleX, j*scaleY,this);
                    }
                    if(po instanceof WaterObjectKawa)
                    {
                        //podlane
                        g.drawImage(getSprite("podlanakawa.jpg"),i*scaleX, j*scaleY,this);
                    }
                    if(po instanceof CollectingObject)
                    {
                        //pole po zbiorze
                        g.drawImage(getSprite("pole.jpg"),i*scaleX, j*scaleY,this);
                    }
                }
            }
    }
    
    
    private boolean checkIfIsOccupied (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
            if(po instanceof ObjectStore)
            {
                return true;
            }
            
        }
        return false;
    }
     private boolean checkIfIsObstacle (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof ObjectPackage)
            {
                return true;
            }
        }
        return false;
    }
      
     private boolean checkIfIsPlow (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof PlowObject)
            {
                return true;
            }
        }
        return false;
    }
       
      private boolean checkIfIsSowPszenica (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof SowObjectPszenica)
            {
                return true;
            }
             
        }
        return false;
    }
      private boolean checkIfIsSowBataty (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof SowObjectBataty)
            {
                return true;
            }
             
        }
        return false;
    }
        private boolean checkIfIsSowHerbata (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof SowObjectHerbata)
            {
                return true;
            }
             
        }
        return false;
    }
          private boolean checkIfIsSowJeczmien (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof SowObjectJeczmien)
            {
                return true;
            }
             
        }
        return false;
    }
            private boolean checkIfIsSowKawa (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof SowObjectKawa)
            {
                return true;
            }
             
        }
        return false;
    }
              private boolean checkIfIsSowOliwki (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof SowObjectOliwki)
            {
                return true;
            }
             
        }
        return false;
    }
      
       private boolean checkIfIsWaterPszenica (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof WaterObjectPszenica)
            {
                return true;
            }
        }
        return false;
    }
        private boolean checkIfIsWaterBataty (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof WaterObjectBataty)
            {
                return true;
            }
        }
        return false;
    }
         private boolean checkIfIsWaterHerbata (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof WaterObjectHerbata)
            {
                return true;
            }
        }
        return false;
    }
         private boolean checkIfIsWaterJeczmien (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof WaterObjectJeczmien)
            {
                return true;
            }
        }
        return false;
    }
         private boolean checkIfIsWaterKawa (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof WaterObjectKawa)
            {
                return true;
            }
        }
        return false;
    }
         private boolean checkIfIsWaterOliwki (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof WaterObjectOliwki)
            {
                return true;
            }
        }
        return false;
    }
        
        
       
       private boolean checkIfIsRoute (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof RouteObject)
            {
                return true;
            }
        }
        return false;
    }
    
    
    


    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 302, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
