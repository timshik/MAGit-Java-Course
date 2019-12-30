package UI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletTemp {


    public class Servlets extends HttpServlet {
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            processRequest(request, response);
        }
        public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            processRequest(request, response);
        }
        protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        }

    }
}
//<form method="get" action="time/show">