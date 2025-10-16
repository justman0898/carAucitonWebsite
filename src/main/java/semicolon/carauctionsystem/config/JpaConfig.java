package semicolon.carauctionsystem.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "semicolon.carauctionsystem.users.data.repository",
    entityManagerFactoryRef = "jpaEntityManagerFactory",
    transactionManagerRef = "jpaTransactionManager")
@EntityScan(basePackages = "semicolon.carauctionsystem.users.data.models")
public class JpaConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties jpaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource jpaDataSource() {
        return jpaDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean  jpaEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(jpaDataSource())
                .packages("semicolon.carauctionsystem.users.data.models")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory jpaEntityManagerFactory) {
        return new JpaTransactionManager(jpaEntityManagerFactory);
    }
}
