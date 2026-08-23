package lk.ac.icbt.sunrisedental.controller;

import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*;
import lk.ac.icbt.sunrisedental.dto.LoginRequest; import lk.ac.icbt.sunrisedental.exception.ValidationException; import lk.ac.icbt.sunrisedental.model.User; import lk.ac.icbt.sunrisedental.util.*;
import java.io.IOException; import java.util.Map;

@WebServlet("/api/auth/*") public class AuthServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException { try { if("/login".equals(req.getPathInfo())) { LoginRequest login=JsonResponse.body(req,LoginRequest.class); User user=AppServices.authentication().authenticate(login.username(),login.password()); HttpSession s=req.getSession(true);s.setAttribute("user",user);JsonResponse.write(resp,200,Map.of("username",user.username(),"fullName",user.fullName())); } else if("/logout".equals(req.getPathInfo())) { HttpSession s=req.getSession(false);if(s!=null)s.invalidate();JsonResponse.write(resp,200,Map.of("message","Logged out")); } else JsonResponse.error(resp,404,"Endpoint not found"); } catch(ValidationException e){JsonResponse.error(resp,401,e.getMessage());} catch(Exception e){JsonResponse.error(resp,400,"Invalid request");} }
    protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException { HttpSession s=req.getSession(false);User user=s==null?null:(User)s.getAttribute("user"); if(user==null)JsonResponse.error(resp,401,"Not authenticated");else JsonResponse.write(resp,200,Map.of("authenticated",true,"username",user.username(),"fullName",user.fullName())); }
}
