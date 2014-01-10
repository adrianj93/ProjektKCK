/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Traktor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
    Command commands[];
    String dictFile;
    
    public Interpreter(String _dictFile)
    {
     dictFile = _dictFile;
     loadCommands();
    }

    Interpreter(URL resource) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private int loadCommands()
    {
        ArrayList<Command> _commands = new ArrayList<Command>();
        BufferedReader br = null;
 
	try { 
                String line; 
		br = new BufferedReader(new FileReader(dictFile));
                
		while ((line = br.readLine()) != null) {
				
                         String tabLine[];
                         tabLine =  line.split(";");
                         String tabArgs[];
                         int no  = 0;
                         int noArg = 0;
                         try
                         {
                            tabArgs = tabLine[1].split("#");                        
                            no = Integer.parseInt(tabArgs[0]);
                         
                            if(tabArgs.length > 1)
                            {
                                noArg = Integer.parseInt(tabArgs[1]);
                            }
                         }
                         catch(Exception e)
                         {
                             no = Integer.parseInt(tabLine[1]);
                         }
                         _commands.add(new Command(tabLine[0], no, noArg));
            }
 
	} catch (IOException e) {
			e.printStackTrace();
	} finally {
		try {
			if (br != null)br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
        commands = _commands.toArray(new Command[0]);
        return commands.length;
    }
    
    public ExecCommand[] PrepareText(String text)
    {       
        ArrayList<ExecCommand> currentCommands = new ArrayList<ExecCommand>();
        
        text = changePolishLetters(text);        
        String[] words = text.split(" ");
        
        int x = 0;
        int y = 0;
        Boolean isArgCus = false;
        
        for(int i = 0;i < words.length;i++)
        {
            if(!words[i].isEmpty())
            {
                for(int j = 0;j < commands.length;j++)
                {
                    String def = commands[j].text;
                    if(def.equals(words[i]))
                    {
                        ExecCommand ec = new ExecCommand(commands[j]);
                        if (commands[j].noArg > 0)
                        {
                            ec.SetArg();
                        }
                        currentCommands.add(ec);
                        break;
                    }
                }
                    
                    Pattern pattern = Pattern.compile("\\d{1,3}");
                    Matcher matcher = pattern.matcher(words[i]);
                    boolean isMatching = matcher.find();
        
                    if(isMatching)
                    {
                        String arg = matcher.group();
                        if(!isArgCus)
                        {
                            x = Integer.parseInt(arg);
                            isArgCus = true;
                        }
                        else
                        {
                            y = Integer.parseInt(arg);                        
                        }
                    }                      
            }           
        }
        
        if(isArgCus)
        {
            ExecCommand ec = new ExecCommand(null);
            ec.SetArgCus(x, y);
            currentCommands.add(ec);
        }
        
        return currentCommands.toArray(new ExecCommand[0]);
    }
    
      
    public String changePolishLetters(String word)
    {
        word = word.toLowerCase();
        String chars[][] = {{"ą", "a"}, {"ć", "c"}, {"ę", "e"}, 
            {"ł", "l"}, {"ń", "n"}, {"ó", "o"}, {"ś", "s"}, {"ź", "z"}, {"ż", "z"}};//TODO
        
        for(int i = 0;i < chars.length;i++)
        {
            word = word.replace(chars[i][0], chars[i][1]);
        }
        
        
        return word;
    }
    
   
}
