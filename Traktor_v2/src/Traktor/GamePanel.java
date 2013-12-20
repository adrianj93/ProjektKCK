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
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.HashMap;

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
        board = new Board(10,10);      
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
        board.SetObject(board.width-4, board.height-2, false, os1);
        board.SetObject(0, board.height-2, false, os2);
        
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
} catch (Exception e) {
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
        dlm.addElement(MechanizmCzasu.GetCzas() + " eTraktor: " + text);
    }
    
    public void Run(String text) {
        ExecCommand[] excmd = interpreter.PrepareText(text);
        boolean isOccupied = false;

        if (excmd.length == 0) {
            addTextToList(Informations.UnknownCommand);
        }

        for (int i = 0; i < excmd.length; i++) {
            if (!excmd[i].arg) {
                
                Point pAgent = findAgent();
                int no = excmd[i].cmd.GetNo();

                switch (no) {
                    case 1: //poruszanie się
                    { 
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
                                                    pAgent.x -= 1;
                                                    addTextToList("Dojechałem. Co robić?");
                                                    
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                }
                                                break;
                                            }
                                            case 2: {
                                                if (pAgent.y > 0) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x, pAgent.y - 1);
                                                    pAgent.y -= 1;
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                }
                                                break;
                                            }
                                            case 3: {
                                                if (pAgent.x < board.width - 1) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x+1, pAgent.y);
                                                    pAgent.x += 1;
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                }
                                                break;
                                            }
                                            case 4: {
                                                if (pAgent.y < board.height - 1) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x, pAgent.y + 1);
                                                    pAgent.y += 1;
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                }
                                                break;
                                            }
                                            
                                            
                                             case 5: {
                                                //kierowanie pomocnikiem
                                                 pozX=pAgent.x*40;
                                                 pozY=pAgent.y*40;
                                                break;
                                            }
                                             
                                             
                                             case 6: {
                                                //kierowanie pomocnikiem
                                                 pozX=50;
                                                 pozY=380;
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
                                            pAgent.x = excmd[j].GetX();
                                            pAgent.y = excmd[j].GetY();
                                        }
                                        else
                                        {
                                            addTextToList(Informations.OutsideEdge);
                                        }
                                    }
                                    else
                                    {
                                        addTextToList(Informations.OutsideEdge);
                                    }
                                    
                                }
                                
                                if(!isOccupied)
                                {
                                    board.DeleteObject(xOld, yOld, true);
                                    board.SetObject(pAgent.x, pAgent.y, true, agent); 
                                }
                                else
                                {   
                                    addTextToList(Informations.ExistStore);
                                }
                            }
                        }
                        break;
                    }
                    case 2: //podnoszenie
                    {
                        if (board.CheckPool(pAgent.x, pAgent.y,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x, pAgent.y, false);
                        }
                        break;
                    }
                    case 3: //opuszczanie
                    {                        
                            ObjectPackage obj = agent.GetPackage();
                            board.SetObject(pAgent.x, pAgent.y, false, obj);
                        
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
        
        
        scaleX = getWidth() / board.width;
        scaleY = getHeight() / board.height;
        
        for(int i = 0;i < board.getWidth();i++)
            for(int j = 0;j < board.getHeight();j++)
            {
                //tutaj laduje sie obrazek traktora, ktory jest wykrywany jako ten p.agent i pomicnik,
                //ale on akurat mogl chyba sie ladowac w kazdym miejscu etody
                if(board.CheckPool(i, j, true))
                {
                    g.drawImage(getSprite("1.png"), i*scaleX-17, j*scaleY-17,this);
                    
                    g.drawImage(getSprite("2.gif"), pozX, pozY,this);
                        
                }
                else if(board.CheckPool(i, j, false))
                {
                    PoolObject po = board.GetObject(i, j, false);
                    if(po instanceof ObjectStore)
                    {     
                        //hangar
                         g.drawImage(getSprite("hangar.gif"), i*scaleX, j*scaleY,this);

                    }
                    if(po instanceof ObjectPackage)
                    {     
                        //kamienie
                        g.drawImage(getSprite("kamien.gif"), i*scaleX, j*scaleY,this);
                         
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
    
    
    
    
    
    


    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 414, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 302, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
