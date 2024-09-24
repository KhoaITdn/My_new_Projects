package org.example.demo2.controller;
import org.example.demo2.model.ClassName;
import org.example.demo2.model.Student;
import org.example.demo2.service.IStudentService;
import org.example.demo2.service.StudentServiceImpl;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "StudentServlet", value = "/student")
public class StudentServlet extends HttpServlet {
    private final IStudentService iStudentService = new StudentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":
                showCreateForm(request, response);
                break;
            case "edit":
                showUpdateForm(request, response);
                break;
            case "delete":
                showDeleteForm(request, response);
                break;
            case "view":
                break;
            default:
                findAll(request, response);
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<ClassName> classNameList = iStudentService.findClasses();
        request.setAttribute("list", classNameList);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("create.jsp");
        requestDispatcher.forward(request, response);
    }


    private void findAll(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("Search");
        if (name == null) name = "";
        List<Student> list = iStudentService.getStudentByname(name);
        request.setAttribute("studentList", list);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("list.jsp");
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":
                try {
                    addNewStudent(request, response);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "edit":
                try {
                    save(request, response);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                findAll(request, response);
        }

    }

    public void forwardCreateForm(HttpServletRequest request, HttpServletResponse response, String name, String email, int gender
            , double point, int class_id) throws ServletException, IOException {
        Student student = new Student(name, email, gender, point, new ClassName(class_id));
        request.setAttribute("param", student);
        List<ClassName> classNameList = iStudentService.findClasses();
        request.setAttribute("list", classNameList);
        request.getRequestDispatcher("create.jsp").forward(request, response);

    }

    private void addNewStudent(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ServletException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        int gender = Integer.parseInt(request.getParameter("gender"));
        double point = Double.parseDouble(request.getParameter("point"));
        int class_id = Integer.parseInt(request.getParameter("class_id"));

        if (regaxName(name)) {
            request.setAttribute("errorMessage", "Dinh dang ten khong hop le.");
            forwardCreateForm(request, response, name, email, gender, point, class_id);
            return;
        }
        // Kiểm tra email có tồn tại và có hợp lệ không
        if (!iStudentService.isValidEmail(email)) {
            request.setAttribute("errorMessage", "Định dạng email không hợp lệ. Vui lòng nhập lại.");
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            request.setAttribute("gender", gender);
            request.setAttribute("point", point);
            request.setAttribute("class_id", class_id);
            request.getRequestDispatcher("create.jsp").forward(request, response);
            return;
        }

        if (iStudentService.emailExists(email, class_id)) {
            request.setAttribute("errorMessage", "Email đã tồn tại. Vui lòng nhập lại.");
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            request.setAttribute("gender", gender);
            request.setAttribute("point", point);
            request.setAttribute("class_id", class_id);
            request.setAttribute("list", iStudentService.findClasses());
            request.getRequestDispatcher("create.jsp").forward(request, response);
        } else {
            ClassName clazz = new ClassName(class_id);
            Student student = new Student(name, email, gender, point, clazz);
            iStudentService.addNewStudent(student);
            response.sendRedirect(request.getContextPath() + "?action=findAll");
        }
    }

    private void showDeleteForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("sid"));
        iStudentService.showDeleteForm(id);
        response.sendRedirect(request.getContextPath() + "?action=findAll");
    }

    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("sid"));
        Student studentList = iStudentService.getStudentByid(id);
        List<ClassName> classNameList = iStudentService.findClasses();
        request.setAttribute("listU", classNameList);
        request.setAttribute("st", studentList);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("update.jsp");
        requestDispatcher.forward(request, response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws
            IOException, SQLException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        int gender = Integer.parseInt(request.getParameter("gender"));
        double point = Double.parseDouble(request.getParameter("point"));
        int classId = Integer.parseInt(request.getParameter("class_id"));

//        if (!iStudentService.isValidEmail(email)) {
//            request.setAttribute("errorMessage", "Định dạng email không hợp lệ. Vui lòng nhập lại.");
//            request.setAttribute("name", name);
//            request.setAttribute("email", email);
//            request.setAttribute("gender", gender);
//            request.setAttribute("point", point);
//            request.setAttribute("id", id);
//            request.getRequestDispatcher("create.jsp").forward(request, response);
//            return;
//        }
        if (iStudentService.emailExists(email, id)) {
            request.setAttribute("errorMessage", "Email đã tồn tại. Vui lòng nhập lại.");
            Student student = new Student(name, email, gender, point, new ClassName(classId));
            request.setAttribute("st", student);
            List<ClassName> list = iStudentService.findClasses();
            request.setAttribute("listU", list);
            request.getRequestDispatcher("create.jsp").forward(request, response);
            return;
        }
        ClassName clazz = new ClassName(classId);
        Student student = new Student(name, email, gender, point, clazz);
        iStudentService.save(student);
        response.sendRedirect(request.getContextPath() + "?action=findAll");
    }

    public void forwardEditForm(HttpServletRequest request, HttpServletResponse response,int id, String name, String email, int gender
            , double point, int class_id) throws ServletException, IOException {

        Student student = new Student(id, name, email, gender, point, new ClassName(class_id));
        request.setAttribute("st", student);
        List<ClassName> classNameList = iStudentService.findClasses();
        request.setAttribute("listU", classNameList);
        request.getRequestDispatcher("update.jsp").forward(request,response);

    }

    private boolean regaxName(String name) {
        return !name.matches("^[\\p{L}\\s]{1,50}$");
    }






}






