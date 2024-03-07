package ru.alishev.springcourse.dao;

import org.springframework.stereotype.Component;
import ru.alishev.springcourse.models.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class PersonDAO {
    private static int PEOPLE_COUNT;

    private static final String URL = "jdbc:postgresql://localhost5432/first_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static Connection connection;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Person> people;

    {
        people = new ArrayList<>();
        people.add(new Person(++PEOPLE_COUNT, "Tom", 24, "tom@mail.ru"));
        people.add(new Person(++PEOPLE_COUNT, "Bob", 52, "bob@mail.ru"));
        people.add(new Person(++PEOPLE_COUNT, "Mike", 18, "mike@yahoo.com"));
        people.add(new Person(++PEOPLE_COUNT, "Katy", 34, "katy@gmail.com"));
    }

    public List<Person> index() throws SQLException {
//        return people;
        List<Person> personList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String SQL = "SELECT * FROM Person";
        ResultSet resultSet = statement.executeQuery(SQL);
        while (resultSet.next()) {
            Person person = new Person();

            person.setId(resultSet.getInt("id"));
            person.setName(resultSet.getString("name"));
            person.setEmail(resultSet.getString("email"));
            person.setAge(resultSet.getInt("age"));

            people.add(person);

        }
        return people;

    }

    public Person show(int id) throws SQLException {
        Person person = null;

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Person WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        person.setId(resultSet.getInt("id"));
        person.setName(resultSet.getString("name"));
        person.setEmail(resultSet.getString("email"));
        person.setAge(resultSet.getInt("age"));

//        return people.stream().filter(person -> person.getId() == id).findAny().orElse(null);
        return person;
    }

    public void save(Person person) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement("INSERT INTO Person VALUES (1,?,?,?)");
        preparedStatement.setString(1, person.getName());
        preparedStatement.setInt(2, person.getAge());
        preparedStatement.setString(3, person.getEmail());
        preparedStatement.executeUpdate();


        person.setId(++PEOPLE_COUNT);
        people.add(person);
        Statement statement = connection.createStatement();
        String SQL = "INSERT INTO Person VALUES(" + 1 + ",'" + person.getName()
                + "'" + "'," + person.getAge() + ",'" + person.getEmail() + "')";
        statement.executeUpdate(SQL);
    }

    public void update(int id, Person updatedPerson) throws SQLException {
        PreparedStatement preparedStatement
                = connection.prepareStatement("UPDATE Person SET name=?, age=?, email=? WHERE id=?");
        preparedStatement.setString(1, updatedPerson.getName());
        preparedStatement.setInt(2, updatedPerson.getAge());
        preparedStatement.setString(3, updatedPerson.getEmail());
        preparedStatement.setInt(4, id);
        preparedStatement.executeUpdate();
/*        Person personToBeUpdated = show(id);
        personToBeUpdated.setName(updatedPerson.getName());
        personToBeUpdated.setAge(updatedPerson.getAge());
        personToBeUpdated.setEmail(updatedPerson.getEmail());*/

    }

    public void delete(int id) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement("DELETE FROM Person WHERE id=?");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
//        people.removeIf(p -> p.getId() == id);

    }

}
