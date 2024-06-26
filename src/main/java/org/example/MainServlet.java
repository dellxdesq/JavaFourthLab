package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import Model.User;
import service.AuthService;
import service.FileManager;
import service.Utility;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(urlPatterns = {"/Manager"})
public class MainServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        String login = (String) request.getSession().getAttribute("login");
        String password = (String) request.getSession().getAttribute("password");
        String email = (String) request.getSession().getAttribute("email");

        User user = AuthService.GetUser(login, password);

        if(user == null)
        {
            if(login!= null)
            {
                AuthService.CreateUser(new User(login, password, email));
            }
            else
            {
                response.sendRedirect(Utility.RedirectOn(request.getRequestURL().toString(), "/LoginServlet"));
                return;
            }
        }

        String currentPath = request.getParameter("path");

        if(currentPath == null)
        {
            currentPath = "C:/forjava/users";
        }
        if (!currentPath.contains(".."))
        {
            if(!Utility.IsCorrectFolderForUser(login, currentPath))
            {
                currentPath = Utility.GetCorrectRouteForFolder(login);
            }
        }
        else
        {
            currentPath = Utility.GetCorrectRouteForFolder(login);
        }

        FileManager fileManager = new FileManager();
        File[] folders = fileManager.allFolders(currentPath);
        if (folders == null)
        {
            folders = new File[0];
        }

        File[] files = fileManager.allFiles(currentPath);
        if (files == null)
        {
            files = new File[0];
        }

        Date generationDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

        request.setAttribute("generationTime", dateFormat.format(generationDate));
        request.setAttribute("folders", folders);
        request.setAttribute("files", files);
        request.setAttribute("currentPath", currentPath);
        request.setAttribute("login", login);
        request.getRequestDispatcher("Manager.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        request.getSession().removeAttribute("login");
        request.getSession().removeAttribute("password");

        response.sendRedirect(Utility.RedirectOn(request.getRequestURL().toString(),"/LoginServlet"));
    }
}
