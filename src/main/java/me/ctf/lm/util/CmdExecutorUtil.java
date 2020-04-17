package me.ctf.lm.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 10:18
 */
@Slf4j
public class CmdExecutorUtil {

    /**
     * ssh key
     */
    private static final String ID_RSA_PATH = System.getProperty("user.home") + "/.ssh/id_rsa";

    /**
     * 命令执行
     *
     * @param hostName
     * @param port
     * @param username
     * @param cmd
     * @return
     * @throws IOException
     */
    public static String authPasswordAndExecute(String hostName, int port, String username, String password, String cmd) throws IOException {
        Connection conn = new Connection(hostName, port);
        Session session = null;
        try {
            conn.connect();
            authenticateWithPassword(username, password, conn);
            session = conn.openSession();
            return execute(cmd, session);
        } finally {
            if (session != null) {
                session.close();
            }
            conn.close();
        }
    }

    private static String execute(String cmd, Session session) throws IOException {
        log.info("exec cmd: {}",cmd);
        session.execCommand(cmd);
        String result = ProcessStdoutUtil.processStdout(session.getStdout());
        if (StringUtils.isEmpty(result)) {
            result = StringUtils.removeStart(result, "\n");
            result = StringUtils.removeEnd(result, "\n");
        }
        return result;
    }

    /**
     * 命令执行
     *
     * @param hostName
     * @param port
     * @param username
     * @param cmd
     * @return
     * @throws IOException
     */
    public static String authPublicKeyAndExecute(String hostName, int port, String username, String pemPath, String cmd) throws IOException {
        Connection conn = new Connection(hostName, port);
        Session session = null;
        try {
            conn.connect();
            authenticateWithPublicKey(username, pemPath, conn);
            session = conn.openSession();
            return execute(cmd, session);
        } finally {
            if (session != null) {
                session.close();
            }
            conn.close();
        }
    }

    /**
     * 命令执行
     *
     * @param hostName
     * @param port
     * @param username
     * @param cmd
     * @return
     * @throws IOException
     */
    public static String authPublicKeyAndExecute(String hostName, int port, String username, String cmd) throws IOException {
        Connection conn = new Connection(hostName, port);
        Session session = null;
        try {
            conn.connect();
            authenticateWithPublicKey(username, conn);
            session = conn.openSession();
            return execute(cmd, session);
        } finally {
            if (session != null) {
                session.close();
            }
            conn.close();
        }
    }

    /**
     * 认证
     *
     * @param username
     * @param conn
     * @throws IOException
     */
    private static void authenticateWithPublicKey(String username, Connection conn) throws IOException {
        boolean isAuthenticated = conn.authenticateWithPublicKey(username, new File(ID_RSA_PATH), null);
        if (!isAuthenticated) {
            throw new RuntimeException("Authentication failed！");
        }
    }

    /**
     * 认证
     *
     * @param username
     * @param conn
     * @throws IOException
     */
    private static void authenticateWithPublicKey(String username, String pemPath, Connection conn) throws IOException {
        boolean isAuthenticated = conn.authenticateWithPublicKey(username, new File(pemPath), null);
        if (!isAuthenticated) {
            throw new RuntimeException("Authentication failed！");
        }
    }

    /**
     * 认证
     *
     * @param username
     * @param conn
     * @throws IOException
     */
    private static void authenticateWithPassword(String username, String password, Connection conn) throws IOException {
        boolean isAuthenticated = conn.authenticateWithPassword(username, password);
        if (!isAuthenticated) {
            throw new RuntimeException("Authentication failed！");
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(authPublicKeyAndExecute("172.16.158.109", 22, "admin", "grep '2019-12-19 16:51' /home/admin/lt-indicator/logs/risk-indicator/risk-indicator.log |grep '指标计算结果'"));
    }
}
