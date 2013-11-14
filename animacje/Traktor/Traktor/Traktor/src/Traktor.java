import java.awt.Canvas;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.util.Scanner;


public class Traktor extends Canvas{
    
public BufferStrategy strategia;
public static final int SZEROKOSC = 800;
public static final int WYSOKOSC = 600;
public static final int SZYBKOSC = 60;
public BufferedImage traktor = null;
public HashMap sprites;
public int pozX,pozY,vX, vY, x, y, q, a,c;
private final Component Component;
Scanner in = new Scanner(System.in);






public Traktor() {
    
pozX=100;
pozY=100;
vX=3;

sprites = new HashMap();
JFrame okno = new JFrame(".:Traktor:.");
JPanel panel = (JPanel)okno.getContentPane();
setBounds(0,0,SZEROKOSC,WYSOKOSC);
panel.setPreferredSize(new Dimension(SZEROKOSC,WYSOKOSC));
panel.setLayout(null);
Component /*add*/ = panel.add(this);
okno.setBounds(0,0,SZEROKOSC,WYSOKOSC);
okno.setVisible(true);
createBufferStrategy(2);
strategia = getBufferStrategy();
requestFocus();
okno.addWindowListener( new WindowAdapter() {
public void windowClosing(WindowEvent e) {
System.exit(0);
}

});



}


public static void main(String[] args) {
Traktor inv = new Traktor();
inv.game();
}
@Override
public void paint(Graphics g){
if (traktor==null)
traktor = loadImage("1.png");
g.setColor(getBackground());
g.fillRect(0,0,getWidth(),getHeight());
g.drawImage(getSprite("1.png"), pozX, pozY,this);

g.setColor (Color.red);
g.fillOval(350,130,40,40);

}

public BufferedImage getSprite(String sciezka) 
{
BufferedImage img = (BufferedImage)sprites.get(sciezka);
if (img == null) {
img = loadImage(sciezka);
sprites.put(sciezka,img);
}
return img;
}

   






public void updateWorld() {
    
if(a==2)
{
q += 2;
if(q>0 && q<145)
{
    pozX += 2;
}







System.out.println("Wczytana liczba q, to: " + q);
System.out.println("Wczytana liczba pozX, to: " + pozX);
       
}

if(a==3)
{
q += 2;
if(q>0 && q<73)
{
    pozY += 2;
}
if(q>73 && q<233)
{
    pozX += 2;
}

if(q>233 && q<303)
{
    pozY -= 2;
}

if(q>303 && q<320)
{
    pozX += 2;
}



System.out.println("Wczytana liczba q, to: " + q);
System.out.println("Wczytana liczba pozX, to: " + pozX);
       
}





}






public void game() {
    c=2;
if(c==2){ 
    a=2;
while (q<145) {
updateWorld();
paint(getGraphics());
try {
Thread.sleep(SZYBKOSC);
} catch (InterruptedException e) {}

}
q=0;
System.out.println("Przeszkoda, wpisz 3, zeby ominac");
int c = in.nextInt();
 
if(c==3){
a=3;
while (q<400) {
updateWorld();
paint(getGraphics());
try {
Thread.sleep(SZYBKOSC);
} catch (InterruptedException e) {}

}
System.out.println("Przeszkoda ominieta");
}}
}




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



}
//  RYSOWANIE

