/**
 * Identifies every scene in the application and the FXML file that backs it.
 *
 * Each slice owner adds their scenes here. The factory uses the fxmlFile value
 * to load the matching FXML from the resources folder, so the enum is the single
 * shared list every slice plugs into.
 *
 * @author Bay Shahryar
 * @version 0.1.0
 * @since 7/28/26
 */
public enum SceneType {
    LOGIN("login.fxml"),
    REGISTER("register.fxml"),
    DASHBOARD("dashboard.fxml"),
    HOME("home.fxml"),
    COURSES("courses.fxml"),
    ENROLLMENT("enrollment.fxml"),
    ASSIGNMENTS("assignments.fxml"),
    GRADES("grades.fxml");

    private final String fxmlFile;

    SceneType(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    /**
     * Returns the FXML file name that backs this scene.
     *
     * @return the FXML file name, for example "login.fxml"
     */
    public String getFxmlFile() {
        return fxmlFile;
    }
}