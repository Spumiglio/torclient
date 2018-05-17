
package torclients;

import java.awt.BorderLayout;
import java.awt.Container;
import javafx.scene.layout.Border;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;



class ProgressBar  {
    JFrame f = new JFrame("Caricamento");
    JProgressBar progressBar = new JProgressBar();
    Container content = f.getContentPane();
    public ProgressBar()  {
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        progressBar.setStringPainted(true);
        TitledBorder border = BorderFactory.createTitledBorder("Caricamento...");
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.NORTH);
        f.setSize(300, 100);
        f.setVisible(true);
        barUpdater();

    }
    public void barUpdater(){
        Thread up = new Thread(){
          @Override
            public void run(){
              int value=0;
              while (value <= 100) {
                  try {
                      Thread.sleep(130);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
                  progressBar.setValue(value += 1);
              }
              try {
                  Thread.sleep(2000);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
              f.dispose();

          }
        };
        up.start();
    }

}

    
    
