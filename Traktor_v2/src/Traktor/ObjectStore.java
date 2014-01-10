/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Traktor;

public class ObjectStore extends PoolObject {
    int capacity;
    protected int currentAmount;
    
    public ObjectStore()
    {
        currentAmount = 0;
        capacity = 0;
    }
    
    public ObjectStore(int _capacity)
    {
        currentAmount = 0;
        capacity = _capacity;
    }
}
