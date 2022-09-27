/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.underground;

import javax.ejb.Local;

/**
 *
 * @author Sely
 */
@Local
public interface UpdaterTimer {
    
    public void startTimer(long duration);
    public void stopTimer();
}
