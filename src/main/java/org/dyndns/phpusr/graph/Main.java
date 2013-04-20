package org.dyndns.phpusr.graph;

import com.mxgraph.util.mxResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author phpusr
 *         Date: 27.05.12
 *         Time: 16:04
 */

/** Класс запускающий программу */
public class Main {

    public static void main(String[] args) {
        //Включение логирования
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.debug("Start application.");

        //Установка стиля
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        //Создание и показ формы
        GraphEditor frame = new GraphEditor();
        frame.showFrame();
    }

}

/** Класс формы */
class GraphEditor extends JFrame {

    /** Adds required resources for i18n */
    static {
        try {
            mxResources.add("org/dyndns/phpusr/graph/editor");
        } catch (Exception e) {
            // ignore
        }
    }

    public GraphEditor() throws HeadlessException {
        super(mxResources.get("prog.name"));
        setContentPane(GraphForm.getInstance(new GraphUtil(this)).getPnlMain());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Выравнивание по центру
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setLocation((screenWidth-getWidth())/2, (screenHeight-getHeight())/2);
    }

    public void showFrame() {
        setVisible(true);
    }
}
