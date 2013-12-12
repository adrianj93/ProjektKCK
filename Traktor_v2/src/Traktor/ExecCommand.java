/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Traktor;

public class ExecCommand {
    Command cmd;
    Boolean arg, argCus;
    int x,y;
    
    
    public ExecCommand(Command _cmd)
    {
        cmd = _cmd;
        arg = false;
        argCus = false;
    }
    
    public Command GetCmd()
    {
        return cmd;
    }
    
    public void SetArg()
    {
        arg = true;              
    }
    
    public void SetArgCus(int _x, int _y)
    {
        arg = true;
        argCus = true;
        x = _x;
        y = _y;             
    }
    
    public int GetX()
    {
        return x;
    }
    
    public int GetY()
    {
        return y;
    }
    
    public Boolean IsArg()
    {
        return arg;
    }
    
    public Boolean IsArgCus()
    {
        return argCus;
    }
}
