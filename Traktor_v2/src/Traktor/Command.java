/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Traktor;

public class Command {
    String text;
    int no;
    int noArg;
    
    public Command(String _text, int _no, int _noArg)
    {
        text = _text;
        no = _no;
        noArg = _noArg;
    }
    
    public String GetText()
    {
        return text;
    }
    
    public int GetNo()
    {
        return no;
    }
    
    public int GetNoArg()
    {
        return noArg;
    }
}
