/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.underground;

import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

/**
 *
 * @author Sely
 */
@Stateless
public class UpdaterTimerBean implements UpdaterTimer {

    @Resource
    TimerService timerService;
    
    private Timer timer;
    
    @Override
    public void startTimer(long duration) {
        timer = timerService.createTimer(duration, null);
    }

    @Timeout
    public void timerTimeOut(){
        System.out.println("Times out");        
    }
    
    @Override
    public void stopTimer() {
        timer.cancel();
    }
}
