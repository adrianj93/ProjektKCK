/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package Traktor;

public class Agent extends PoolObject{
    int limit;
    int index;
    ObjectPackage tabPackage[];
    
    public Agent() {
        limit = 4;
        index = 0;
        tabPackage = new ObjectPackage[limit];
    }
    
    public void AddPackage(ObjectPackage obj)
    {
        tabPackage[index] = obj;
        index++;
    }
    
    public ObjectPackage GetPackage()
    {
        return tabPackage[index--];        
    }
       
 
}
