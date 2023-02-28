package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {
    public static void main(String[] args) {
        User user = new User(8L, "Jane", "Doe", 25);
        SimpleJDBCRepository repo = new SimpleJDBCRepository();
//        repo.createUser(user);
        System.out.println(repo.findUserById(1L).getFirstName());
        System.out.println(repo.findUserByName("Jane").getLastName());
        System.out.println(repo.findAllUser().size());
        System.out.println(repo.updateUser(user));
        repo.deleteUser(2L);
    }

    private Connection connection;

    {
        try {
            connection = CustomDataSource.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers(firstname, lastname, age) VALUES (?,?,?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname=?, lastname=?, age=? WHERE id=?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id=?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id=?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname=?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user) {
        Long id = null;
        try {
            ps = connection.prepareStatement(createUserSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());

            id = (long) ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return id;
    }

    public User findUserById(Long userId) {
        User user = new User();
        try (
            PreparedStatement ps = connection.prepareStatement(findUserByIdSQL);
        ) {
            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            rs.next();
            user.setId(rs.getLong("id"));
            user.setFirstName(rs.getString("firstname"));
            user.setLastName((rs.getString("lastname")));
            user.setAge(rs.getInt("age"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public User findUserByName(String userName) {
        User user = new User();
        try (
            PreparedStatement ps = connection.prepareStatement(findUserByNameSQL);
        ) {
            ps.setString(1, userName);

            ResultSet rs = ps.executeQuery();
            rs.next();
            user.setId(rs.getLong("id"));
            user.setFirstName(rs.getString("firstname"));
            user.setLastName((rs.getString("lastname")));
            user.setAge(rs.getInt("age"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        User user = new User();

        try (
            PreparedStatement ps = connection.prepareStatement(findAllUserSQL);
        ) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user.setId(rs.getLong("id"));
                user.setFirstName(rs.getString("firstname"));
                user.setLastName((rs.getString("lastname")));
                user.setAge(rs.getInt("age"));
                users.add(user);
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public User updateUser(User user) {
        try (
            PreparedStatement ps = connection.prepareStatement(updateUserSQL);
        ) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public void deleteUser(Long userId) {
        try (
            PreparedStatement ps = connection.prepareStatement(deleteUser);
        ) {
            ps.setLong(1, userId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
