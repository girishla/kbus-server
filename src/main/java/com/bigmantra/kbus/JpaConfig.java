package com.bigmantra.kbus;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(entityManagerFactoryRef = "coreEntityManagerFactory", transactionManagerRef = "jpaTransactionManager")

public class JpaConfig {

	@Autowired
	@Qualifier("kbusdbjpa")
	private DataSource kbusdb;

	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String hbm2ddlMode;

	@Value("${spring.jpa.properties.hibernate.dialect}")
	private String sqlDialect;
	
	@Value("${spring.jpa.show-sql}")
	private boolean showSql;
	
	
	@Bean
	@Qualifier("jpatm")
    PlatformTransactionManager jpaTransactionManager(LocalContainerEntityManagerFactoryBean em) {
		return new JpaTransactionManager(em.getObject());
	}

	@Bean
    LocalContainerEntityManagerFactoryBean coreEntityManagerFactory() {

		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

		jpaVendorAdapter.setShowSql(showSql);

		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

		factoryBean.setDataSource(kbusdb);
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		factoryBean.setPackagesToScan(JpaConfig.class.getPackage()
				.getName());
		factoryBean.setJpaProperties(additionalProperties());

		return factoryBean;
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", hbm2ddlMode);
		properties.setProperty("hibernate.dialect", sqlDialect);
		properties.setProperty("hibernate.jdbc.lob.non_contextual_creation","true");
		return properties;
	}
	

	@Configuration
	@ConfigurationProperties("spring.datasource")
	public static class DataSourceConfig extends HikariConfig {

		@Bean
		@Qualifier("kbusdbjpa")
		public DataSource kbusdb() {
			return new HikariDataSource(this);
		}

			
	}

}
