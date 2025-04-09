package me.love2004.porn.modules.client;

import me.love2004.porn.modules.Module;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class TrayIcon extends Module {

    public TrayIcon(){
        super("TrayIcon","",Module.Category.CLIENT,true,false,false);

    }


    @Override
    public void onEnable() {
        if (SystemTray.isSupported()) {
            TrayIcon td = new TrayIcon();
            try {
                td.displayTray();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        disable();
    }



    public void displayTray() throws AWTException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("loveClient.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);

        trayIcon.displayMessage("loveClient b2", "Thanks for using", MessageType.INFO);
    }
}