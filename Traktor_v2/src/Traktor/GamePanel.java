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

public class GamePanel extends javax.swing.JPanel {
    
    public static final int SZEROKOSC = 25;
    public static final int WYSOKOSC = 250;
    public static final int SZYBKOSC = 600;
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
                x = rand.nextInt(board.width );
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
    
    public void Run(String text) throws InterruptedException {
        ExecCommand[] excmd = interpreter.PrepareText(text);
        boolean isOccupied = false;
        boolean isObstacle = false;
        boolean isPlow = false;
        boolean isSow = false;
        boolean isWater = false;
        boolean isRoute = false;
        
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
                                                //p.agent to ten czarny kwadracik. Usunąłem go, ale umownie on cały czas jest
                                                //i w jego miejscu posawiłem traktor, dzieki temu działa usuwanie
                                                if (pAgent.x > 0) {   
                                                    isOccupied =  checkIfIsOccupied(pAgent.x-1, pAgent.y);
                                                    isObstacle =  checkIfIsObstacle(pAgent.x-1, pAgent.y);
                                                    pAgent.x -= 1;
                                                    addTextToList( "Dojechałem. Co dalej?");
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
                                                    addTextToList( "Dojechałem. Co dalej?");
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
                                                    addTextToList( "Dojechałem. Co dalej?");
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
                                                    addTextToList( "Dojechałem. Co dalej?");
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
                                addTextToList( "Nie wiem, gdzie mam jechać. ;(");
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
                        addTextToList("Nie wiem co zaorać. :(");
                        }
                                                                      break;
                                                                        }
                    
                    case 10:
                    {
                        if (board.CheckPool(pAgent.x-1, pAgent.y,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x-1, pAgent.y, false);
                           addTextToList( "Usunięto przeszkodę");
                        }
                        else if (board.CheckPool(pAgent.x+1, pAgent.y,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x+1, pAgent.y, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x, pAgent.y+1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x, pAgent.y+1, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x, pAgent.y-1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x, pAgent.y-1, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x+1, pAgent.y+1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x+1, pAgent.y+1, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x-1, pAgent.y+1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x-1, pAgent.y+1, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x+1, pAgent.y-1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x+1, pAgent.y-1, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x-1, pAgent.y-1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x-1, pAgent.y-1, false);
                        }
                        else{
                        addTextToList( "Brak przeszkody w pobliżu");
                        }
                  
                        
                        
                        
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                         }
                         }
                         else
                         {
                            addTextToList("Pole musi zostać najpierw zaorane");      
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList(  "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            
                            Zasoby.z.ModyfikujZb1(100);
                            addTextToList("Zebrano 100kg pszenicy");
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            Zasoby.z.ModyfikujZb2(230);
                            addTextToList("Zebrano 230kg batatów");
                            addTextToList("Aktualny stan to: " + Zasoby.z.GetZb2() + "kg");
                            
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            Zasoby.z.ModyfikujZb3(60);
                            addTextToList("Zebrano 60kg herbaty");
                            addTextToList("Aktualny stan to: " + Zasoby.z.GetZb3() + "kg");
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            Zasoby.z.ModyfikujZb4(140);
                            addTextToList("Zebrano 140kg jęczmienia");
                            addTextToList("Aktualny stan to: " + Zasoby.z.GetZb4() + "kg");
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            Zasoby.z.ModyfikujZb5(200);
                            addTextToList( "Zebrano 200kg kawy");
                            addTextToList("Aktualny stan to: " + Zasoby.z.GetZb5() + "kg");
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.DeleteObject(a, b, false);
                        }
                            }
                        }
                         }
                            Zasoby.z.ModyfikujZb6(123);
                            addTextToList("Zebrano 123kg oliwek");
                            addTextToList("Aktualny stan to: " + Zasoby.z.GetZb6() + "kg");
                         }
                        
                         else
                         {
                            addTextToList("Pole musi zostać najpierw zaorane");      
                         }}
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        break;
                    }
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    case 5: //podlewanie
                    {   
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                         }
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList( "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
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
                            addTextToList("Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
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
                        { Ulepszenia.u.SwiatlaSwitch("on"); addTextToList("Wlaczylem swiatla. Rozpoczęto pobór enrgii elektrycznej"); }
                        break;
                    }
                    
                    case 12:
                    {
                        if (Ulepszenia.u.SwiatlaStan() == false) { addTextToList("Swiatla były już wyłączone");} else
                        { Ulepszenia.u.SwiatlaSwitch("off"); addTextToList("Wyłączyłem światła"); }
                        break;
                    }
                        
                    case 13:
                    {
                        addTextToList("Możesz skorzystać z rozmaitych komend: ");
                        addTextToList("    - oraj [numer pola] / wszystko");
                        addTextToList("    - wlacz/wylacz swiatla");
                        addTextToList("    - zbierz/posiej/podlej");
                        addTextToList("    - gdzie jestes");
                        addTextToList("    - przesun/pojedz");
                        addTextToList("    - i inne...");
                        
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
                        addTextToList("Aktualny pogoda to " + Informations.getStringPogoda());
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
                        
                    g.drawImage(getSprite("track.png"), i*scaleX-2, j*scaleY-2,this);
                    
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
