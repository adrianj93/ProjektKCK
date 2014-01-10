/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Traktor;

public class Board {
    
    int width;
    int height;
    PoolObject[][][] tabPool;    
    
    public Board()
    {
        width = 10;
        height = 10;
        initPool();
    }
    
    public Board(int _width, int _height)
    {
        width = _width;
        height = _height;
        initPool();
    }
    
    private void initPool()
    {
        tabPool = new PoolObject[width][][];
        
        for(int x=0; x < width;x++)
        {
            tabPool[x] = new PoolObject[height][];
            for(int y = 0; y < height;y++)
            {
                tabPool[x][y] = new PoolObject[2];
                
                for(int z = 0;z < 2;z++)
                {
                    tabPool[x][y][z] = null;
                }
            }
        }
    }
    
    public int getWidth()
    {   
        return width;
    }    
    
    public int getHeight()
    {
        return height;
    }
    
    public boolean CheckPool(int x, int y, Boolean agent)
    {
        if(agent)
            return tabPool[x][y][1] != null;
        else 
            return tabPool[x][y][0] != null;
    }
    
    public PoolObject GetObject(int x, int y, Boolean agent)
    {
         if(agent)
            return tabPool[x][y][1];
        else 
            return tabPool[x][y][0];
    }
    
    public void SetObject(int x, int y, Boolean agent, PoolObject obj)
    {
        if(agent)
            tabPool[x][y][1] = obj;
        else 
            tabPool[x][y][0] = obj;
    }
    
    public void DeleteObject(int x, int y, Boolean agent)
    {
        if(agent)
            tabPool[x][y][1] = null;
        else 
            tabPool[x][y][0] = null;
    }
       
}
