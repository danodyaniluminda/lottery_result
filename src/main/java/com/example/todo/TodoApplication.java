package com.example.todo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SpringBootApplication
public class TodoApplication {


	private static final String DB_URL = "jdbc:mysql://13.234.242.165:3306/a1";
	private static final String DB_USER = "iDevice";
	private static final String DB_PASSWORD = "iDevice_123456";

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
		lottery();
	}

	public static void lottery() {
		String url = "https://www.nlb.lk/results/govisetha"; // which I selected lottery result site URL

		try {
			Document doc = Jsoup.connect(url).get();
			Elements tableRows = doc.select("tr");

			// Establish a database connection
			try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
				for (Element row : tableRows) {
					Element td = row.select("td").first();

					if (td != null) {
						Element boldElement = td.select("b").first();
						Element dateElement = td.select("br").first();
						String result = boldElement.text();
						String date = dateElement.nextSibling().toString().trim();

						Elements numbers = row.select("li.Number-2, li.Number-1");
						String number1 = "";
						String number2 = "";
						String number3 = "";
						String number4 = "";

						// Extract number data
						int i = 1;
						for (Element number : numbers) {
							String numberText = number.text();
							if (i == 1) {
								number1 = numberText;
							} else if (i == 2) {
								number2 = numberText;
							} else if (i == 3) {
								number3 = numberText;
							} else if (i == 4) {
								number4 = numberText;
							}
							i++;
						}

						// Insert lottery result into the database table
						String insertQuery = "INSERT INTO govisetha_result_dns (lottery_result, date, number_1, number_2, number_3, number_4) VALUES (?, ?, ?, ?, ?, ?)";
						try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
							preparedStatement.setString(1, result);
							preparedStatement.setString(2, date);
							preparedStatement.setString(3, number1);
							preparedStatement.setString(4, number2);
							preparedStatement.setString(5, number3);
							preparedStatement.setString(6, number4);
							preparedStatement.executeUpdate();
						}

						System.out.println("Data inserted into the database.");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
