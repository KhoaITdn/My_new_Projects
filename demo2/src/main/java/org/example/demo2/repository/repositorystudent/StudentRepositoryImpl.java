package org.example.demo2.repository.repositorystudent;
import org.example.demo2.model.ClassName;
import org.example.demo2.model.Student;
import org.example.demo2.repository.BaseRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentRepositoryImpl implements IStudentRepository {
    private final BaseRepository baseRepository  = new BaseRepository();
    private static final String FIND_ALL = "select s.id , s.name , s.email,s.gender , s.point , c.class_id , c.class_name from student s \n" +
            "inner join class c on s.class_id = c.class_id\n" +
            "order by s.id asc";
    private static final String INSERT_STUDENT = "INSERT INTO student (name, email, gender,point, class_id) \n" + " VALUES ( ?, ?, ?, ?, ?)";
    private static final String DELETE_STUDENT = "DELETE FROM student WHERE id = ?";
    private static final String UPDATE_STUDENT = "UPDATE student SET name = ?, email = ?, gender = ?, point = ?, class_id = ? WHERE id = ?";
    private static final String GET_STUDENT_BY_ID = "SELECT s.id, s.name, s.email, s.gender, s.point, c.class_id, c.class_name\n" +
            "FROM student s\n" +
            "INNER JOIN class c ON s.class_id = c.class_id   \n" +
            "where id = ? ";
    private static final String FIND_ALL_CLASS = "SELECT * FROM class ";
    private static final String FIND_BY_NAME = "SELECT s.id, s.name, s.email, s.gender, s.point, c.class_id, c.class_name " +
            "FROM student s " +
            "INNER JOIN class c ON s.class_id = c.class_id " +
            "WHERE s.name LIKE ?;";

    private static final String GET_TOP_10_STUDENTS =
            "SELECT s.id, s.name, s.email, s.gender, s.point, c.class_id, c.class_name " +
                    "FROM student s " +
                    "INNER JOIN class c ON s.class_id = c.class_id " +
                    "ORDER BY s.point DESC " +
                    "LIMIT 10";
    @Override
    public List<Student> findAll() {
        Connection connection = baseRepository.getConnection();
        List<Student> students = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            // Statement là 1 trong 3 đối tượng java cung cấp để connect với DB
            // thường sẽ dùng phương thức executeQuery(dùng cho trường hợp select), chỉ lấy ra mà không thêm vào , xóa , update data
            // dùng cho select ko where , limit , having
            //executeUpdate() : dùng cho các TH insert , update , delete
            ResultSet resultSet = statement.executeQuery(FIND_ALL);
            // ResultSet sẽ hứng các data mà user thêm vào
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                int gender = resultSet.getInt("gender");
                double point = resultSet.getDouble("point");
                int classId = resultSet.getInt("class_id");
                String className = resultSet.getString("class_name");
                ClassName clazz = new ClassName(classId, className);
                Student student = new Student(id, name, email, gender, point, clazz);
                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } return students;

    }


    @Override
    public void addNewStudent(Student student) {
        Connection connection = baseRepository.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT);
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getEmail());
            preparedStatement.setInt(3, student.getGender());
            preparedStatement.setDouble(4, student.getPoint());
            preparedStatement.setInt(5, student.getClazz().getClass_id());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Student> getStudentByname(String name) {
        Connection connection = baseRepository.getConnection();
        List<Student> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NAME);
            preparedStatement.setString(1, "%" + name + "%");
            ResultSet resultSet  = preparedStatement.executeQuery();
            list = toList(resultSet);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }



    @Override
    public Student getStudentByid(int id) {
        Connection connection = baseRepository.getConnection();
        Student student = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_STUDENT_BY_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Student> list = toList(resultSet);
            // Nếu muốn trả về danh sách sinh viên (nếu có nhiều sinh viên với cùng ID, mặc dù điều này hiếm xảy ra):
//            student.addAll(list);

//             Nếu chỉ muốn trả về sinh viên đầu tiên:
             if (!list.isEmpty()) {
                 student = (list.get(0));
             }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return student;
    }

    @Override
    public void showDeleteForm(int id) {
        Connection connection = baseRepository.getConnection();
        try {PreparedStatement preparedStatement = connection.prepareStatement(DELETE_STUDENT);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // save moi
    @Override
    public void save(Student student) {
        Connection connection = baseRepository.getConnection();
        try {
            // Kiểm tra email trùng lặp
            if (emailExists(student.getEmail(), student.getId())) {
                throw new SQLException("Email đã tồn tại.");
            }
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDENT);
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getEmail());
            preparedStatement.setInt(3, student.getGender());
            preparedStatement.setDouble(4, student.getPoint());
            preparedStatement.setInt(5, student.getClazz().getClass_id());
            preparedStatement.setInt(6, student.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean emailExists(String email,int id ) throws SQLException {
        String sql = "SELECT COUNT(*) FROM student WHERE email = ? AND id <> ?";
        Connection connection = baseRepository.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setInt(2, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public List<ClassName> findClasses() {
        Connection connection = baseRepository.getConnection();
        List<ClassName> classNameList = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            // Statement là 1 trong 3 đối tượng java cung cấp để connect với DB
            // thường sẽ dùng phương thức executeQuery(dùng cho trường hợp select), chỉ lấy ra mà không thêm vào , xóa , update data
            // dùng cho select ko where , limit , having
            //executeUpdate() : dùng cho các TH insert , update , delete
            ResultSet resultSet = statement.executeQuery(FIND_ALL_CLASS);
            // ResultSet sẽ hứng các data mà user thêm vào
            while (resultSet.next()) {
                int id = resultSet.getInt("class_id");
                String name = resultSet.getString("class_name");
                ClassName className = new ClassName(id, name);
                classNameList.add(className);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } return classNameList;
    }



    private List<Student> toList(ResultSet resultSet) throws SQLException {
        List<Student> list = new ArrayList<>();
        while(resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String email = resultSet.getString("email");
            int gender = resultSet.getInt("gender");
            double point = resultSet.getDouble("point");
            int classId = resultSet.getInt("class_id");
            String className = resultSet.getString("class_name");
            ClassName clazz = new ClassName(classId,className);
            Student student = new Student(id, name,email, gender, point, clazz);
            list.add(student);
        }
        return list;
    }

    @Override
    public List<Student> getTop10Students() {
        Connection connection = baseRepository.getConnection();
        List<Student> topStudents = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_TOP_10_STUDENTS);
            topStudents = toList(resultSet);  // Sử dụng lại phương thức toList để chuyển ResultSet thành List<Student>
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return topStudents;
    }


}






