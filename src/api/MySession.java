package api;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class MySession {

    private static final SessionFactory ourSessionFactory;
    private static Session session;

    static {
        try {
            ourSessionFactory = new Configuration().configure().buildSessionFactory();
            session = ourSessionFactory.openSession();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private MySession() {

    }

    public static Session getSession() {
        if (session.isOpen()) {
            return session;
        } else {
            session = ourSessionFactory.openSession();
            return session;
        }
    }


    public static boolean closeSessionFactory() {

        ourSessionFactory.close();
        return true;
    }
}
