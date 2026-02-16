import javax.swing.SwingUtilities;
import ui.CombinedApp;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CombinedApp app = new CombinedApp();
            app.setVisible(true);
        });
    }
}
