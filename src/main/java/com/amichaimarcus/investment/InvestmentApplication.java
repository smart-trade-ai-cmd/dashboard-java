package com.amichaimarcus.investment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // ייבוא האנוטציה

@SpringBootApplication
@EnableScheduling // שורה זו מפעילה את השעון הפנימי של השרת

public class InvestmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvestmentApplication.class, args);
	}

}












/*

server.port=8080

# הגדרות חיבור ל-MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/smart_trade_db?sslmode=require
spring.datasource.username=root
spring.datasource.password=yladim1407



# הגדרות JPA / Hibernate
# חשוב: none אומר להיברנט לא לגעת במבנה הטבלאות הקיים של סאקילה
spring.jpa.hibernate.ddl-auto=none

# לוגים של SQL - מעולה כדי לראות מה קורה "מתחת למכסה המנוע"
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true */