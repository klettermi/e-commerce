package kr.hhplus.be.server.config;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.StatelessSession;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public StatelessSession statelessSession(EntityManagerFactory emf) {
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        return sessionFactory.openStatelessSession();
    }
}