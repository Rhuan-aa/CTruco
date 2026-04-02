package com.bueno.domain.usecases.user;

import org.springframework.stereotype.Service;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

@Service
public class EmailValidator {

    private static final int SMTP_PORT = 25;
    private static final int TIMEOUT_MS = 3000;
    private static final Set<String> DISPOSABLE_DOMAINS = Set.of(
            "mailinator.com", "10minutemail.com", "yopmail.com",
            "guerrillamail.com", "tempmail.com", "temp-mail.org"
    );

    public String validate(String email){
        if (email == null || !email.contains("@")) return "Invalid email format.";
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase();
        if (DISPOSABLE_DOMAINS.contains(domain)) return "Disposable/temporary emails are not allowed.";

        List<String> mxRecords = getMxRecords(domain);
        if (mxRecords.isEmpty()) return "The email domain does not have a valid receiving server.";

        String primaryMxServer = mxRecords.get(0);
        if (!isInboxReachable(primaryMxServer, email)) return "The inbox for this email address does not seem to exist.";

        return null;
    }

    private List<String> getMxRecords(String domain) {
        InitialDirContext initialContext = null;
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            initialContext = new InitialDirContext(env);

            Attributes attributes = initialContext.getAttributes(domain, new String[]{"MX"});
            Attribute attribute = attributes.get("MX");

            if (attribute == null || attribute.size() == 0) return Collections.emptyList();

            TreeMap<Integer, String> mxMap = new TreeMap<>();

            for (int i = 0; i < attribute.size(); i++) {
                String[] parts = attribute.get(i).toString().split(" ");
                int priority = Integer.parseInt(parts[0]);
                String mxHost = parts[1];
                if (mxHost.endsWith(".")) mxHost = mxHost.substring(0, mxHost.length() - 1);
                mxMap.put(priority, mxHost);
            }

            return new ArrayList<>(mxMap.values());

        } catch (Exception e) {
            return Collections.emptyList();
        } finally {
            if (initialContext != null) {
                try {
                    initialContext.close();
                } catch (Exception ignored) {}
            }
        }
    }

    private boolean isInboxReachable(String mxServer, String email) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(mxServer, SMTP_PORT), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String response = reader.readLine();

            if (response == null || !response.startsWith("220")) return false;

            sendCommand(writer, "HELO ctruco.com\r\n");
            reader.readLine();
            sendCommand(writer, "MAIL FROM:<validator@ctruco.com>\r\n");
            reader.readLine();
            sendCommand(writer, "RCPT TO:<" + email + ">\r\n");
            response = reader.readLine();
            sendCommand(writer, "QUIT\r\n");

            return response != null && response.startsWith("250");

        } catch (IOException e){
            return true;
        }
    }

    private void sendCommand(BufferedWriter writer, String command) throws IOException {
        writer.write(command);
        writer.flush();
    }
}
