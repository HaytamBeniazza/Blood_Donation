package com.servlets;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.apache.catalina.connector.Response;

import com.beans.Institution;
import com.beans.Demande;
import com.dao.InstitutionDAO;
import com.dao.DemandeDAO;
import jakarta.servlet.RequestDispatcher;


import daoImpl.InstitutionDAOImpl;
import daoImpl.DemandeDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name="in", urlPatterns = {"/institutionsController","*.in", "/"})
public class InstituionsController extends HttpServlet {
	
	private InstitutionDAO metierInstitution = new InstitutionDAOImpl() ; 
	private DemandeDAO demandeDAO = new DemandeDAOImpl() ; 

	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
        String choix = request.getServletPath();
        switch (choix){
	        case "/" :
	            home(request,response);
	            break;
            case "/register.in" :
                register(request,response);
                break;
            case "/login.in" :
                login(request,response);
                break;
            case "/contact.in" :
                contact(request,response);
                break;
            case "/signup.in" :
                signup(request,response);
                break;
            case "/logged.in" :
                loggedIn(request,response);
                break;
            case "/acceuil.in" :
                acceuil(request,response);
                break;
            case "/centre.in" :
                centreDetails(request,response);
                break;
            case "/demande.in" :
                demande(request,response);
                break;
            case "/demande-hopital.in" :
                hospitalDemande(request,response);
                break;
            case "/demande-hopital-comfirmer.in" :
                demandeConfirmed(request,response);
                break;
            case "/demande-hopital-repondu.in" :
                demandeRsponded(request,response);
                break;
            case "/reponse.in" :
                response(request,response);
                break;
            case "/logout.in":
                logout(request, response);
                break;
        }
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        RequestDispatcher dispatcher = request.getRequestDispatcher("/signup.jsp");
        dispatcher.forward(request, response);
	}
	
	public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
        dispatcher.forward(request, response);
	}
	
	
	public void contact(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        RequestDispatcher dispatcher = request.getRequestDispatcher("/contact.jsp");
        dispatcher.forward(request, response);
	}
	
	public void signup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
        Institution institution = new Institution();
        institution.setNom(request.getParameter("nom"));
        institution.setTel(request.getParameter("tel"));
        institution.setEmail(request.getParameter("email"));
        institution.setAdresse(request.getParameter("adresse"));
        institution.setPassword(request.getParameter("password"));
        institution.setEspace(request.getParameter("espace"));

        boolean success = metierInstitution.createInstitution(institution);
        if (success) {
            try {
                response.sendRedirect(request.getContextPath()+"/login.jsp?success="+success);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            response.sendRedirect(request.getContextPath()+"/login.jsp?success="+success);
        }
		
	}
	
	
	public void loggedIn(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		Institution institutionToLogin = new Institution()  ;
		institutionToLogin.setEmail(request.getParameter("email")); 
		institutionToLogin.setPassword(request.getParameter("password"));

		Institution institution = metierInstitution.getByEmail(institutionToLogin.getEmail()) ; 
		System.out.print(institution.getEmail());

		if(institution.isValid(institutionToLogin.getEmail(), institutionToLogin.getPassword()) && institution.getEspace().equals("center")) {
			HttpSession session = request.getSession() ; 
			session.setMaxInactiveInterval(60 * 60 * 60);
			session.setAttribute("user", institution.getId());
			session.setAttribute("institution", institution);
			session.setAttribute("institution1", institution.getEspace());
	        response.sendRedirect(request.getContextPath()+"/Center/dashboard.jsp") ; 
		}else if(institution.isValid(institutionToLogin.getEmail(), institutionToLogin.getPassword()) && institution.getEspace().equals("hopital")) {
			HttpSession session = request.getSession() ;
			session.setMaxInactiveInterval(60 * 60 * 60);
			session.setAttribute("user", institution.getId());
			session.setAttribute("institution", institution);
			session.setAttribute("institution1", institution.getEspace());
			 List<Institution> institutions = metierInstitution.getAllByRole("center");
			 if(institutions.isEmpty()) { 
				 request.setAttribute("noInstitutions", true); 
			 }else { 
				 request.setAttribute("institutions", institutions); 
			 }
			 RequestDispatcher dispatcher = request.getRequestDispatcher("/Hospital/acceuil.jsp");
			 dispatcher.forward(request, response);

		}else {
            String errorMessage = "email - mot de passe incorrect";
			request.setAttribute("error", errorMessage);
	        request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}
	
	public void centreDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
	       String centreIdParam = request.getParameter("id");
	       

	        if (centreIdParam != null && !centreIdParam.isEmpty()) {
	            try {
	            	Institution institution = metierInstitution.getById(Integer.parseInt(centreIdParam));
	                if (institution != null) {
	                    request.setAttribute("institution", institution);
	                } else {
	                    response.sendRedirect("error.jsp");
	                    return;
	                }
	            } catch (NumberFormatException e) {
	                response.sendRedirect("error.jsp");
	                return;
	            }
	        } else {
	            response.sendRedirect("error.jsp");
	            return;
	        }

	        RequestDispatcher dispatcher = request.getRequestDispatcher("/Hospital/centerDetails.jsp");
	        dispatcher.forward(request, response);
		
	}
	
	public void demande(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
        Demande demande = new Demande();
        demande.setNbrPochettesDemandes(Integer.parseInt(request.getParameter("nbrpochette")));
        demande.setGroupeSang(request.getParameter("sang"));
        demande.setNbrPochettesConfirmes(0);
        Institution centre = new Institution();
        String centreId = request.getParameter("id");
        System.out.println(centreId);
        centre = metierInstitution.getById(Integer.parseInt(centreId));
        demande.setCentre(centre);
        
        Institution hospital = new Institution();
        HttpSession session = request.getSession();
        Object userIdAttribute = session.getAttribute("user");

        if (userIdAttribute != null) {
            String userIdString = userIdAttribute.toString();
            System.out.println(userIdString);
            hospital = metierInstitution.getById(Integer.parseInt(userIdString));
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }

        demande.setHospital(hospital);

        boolean success = demandeDAO.addDemande(demande);
        if (success) {
        	
 	       String centreIdParam = request.getParameter("id");
	       

	        if (centreIdParam != null && !centreIdParam.isEmpty()) {
	            try {
	            	Institution institution = metierInstitution.getById(Integer.parseInt(centreIdParam));
	                if (institution != null) {
	                    request.setAttribute("institution", institution);
	                } else {
	                    response.sendRedirect("error.jsp");
	                    return;
	                }
	            } catch (NumberFormatException e) {
	                response.sendRedirect("error.jsp");
	                return;
	            }
	        } else {
	            response.sendRedirect("error.jsp");
	            return;
	        }
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Hospital/centerDetails.jsp?success=" + success);
            dispatcher.forward(request, response);
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Hospital/centerDetails.jsp?success=" + success);
            dispatcher.forward(request, response);
        }
		
	}
	
	
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate();
        RequestDispatcher dispatcher = request.getRequestDispatcher("/login.in");
        dispatcher.forward(request, response);
    }
    
    
    public void home(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object espaceObj = session.getAttribute("institution1");
        
        String espace = espaceObj.toString();
        
        if(espace == null) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
        }else if(espace.equals("center")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Center/dashboard.jsp");
            dispatcher.forward(request, response);
        }else if(espace.equals("hopital")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Hospital/acceuil.jsp");
            dispatcher.forward(request, response);
        }
        
    }
    
    public void response(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object userIdAttribute = session.getAttribute("user");
        String userIdString = userIdAttribute.toString();
    	List<Demande> answers = demandeDAO.getAnswersByHospital(Integer.parseInt(userIdString));
		 if(answers.isEmpty()) { 
			 request.setAttribute("noAnswers", true); 
		 }else { 
			 request.setAttribute("answers", answers); 
		 }
		 RequestDispatcher dispatcher = request.getRequestDispatcher("/Hospital/reponse.jsp");
		 dispatcher.forward(request, response);
        
    }
    
    
    public void acceuil(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 List<Institution> institutions = metierInstitution.getAllByRole("center");
		 if(institutions.isEmpty()) { 
			 request.setAttribute("noInstitutions", true); 
		 }else { 
			 request.setAttribute("institutions", institutions); 
		 }
		 RequestDispatcher dispatcher = request.getRequestDispatcher("/Hospital/acceuil.jsp");
		 dispatcher.forward(request, response);
    }
    
    
    
    public void hospitalDemande(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object userIdAttribute = session.getAttribute("user");
        String userIdString = userIdAttribute.toString();
    	List<Demande> answers = demandeDAO.getAnswersByCenter(Integer.parseInt(userIdString));
		 if(answers.isEmpty()) { 
			 request.setAttribute("noAnswers", true); 
		 }else { 
			 request.setAttribute("answers", answers); 
		 }
		 RequestDispatcher dispatcher = request.getRequestDispatcher("/Center/reponse.jsp");
		 dispatcher.forward(request, response);
   }
    
    
   public void demandeConfirmed(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   String centreIdParam = request.getParameter("id");
       Demande demande = demandeDAO.getDemandeByID(Integer.parseInt(centreIdParam));
       demandeDAO.updateDemandeToMatchConfirms(demande);
       
       HttpSession session = request.getSession();
       Object userIdAttribute = session.getAttribute("user");
       String userIdString = userIdAttribute.toString();
   	List<Demande> answers = demandeDAO.getAnswersByCenter(Integer.parseInt(userIdString));
		 if(answers.isEmpty()) { 
			 request.setAttribute("noAnswers", true); 
		 }else { 
			 request.setAttribute("answers", answers); 
		 }
		 RequestDispatcher dispatcher = request.getRequestDispatcher("/Center/reponse.jsp");
		 dispatcher.forward(request, response);
  }
   
   
   public void demandeRsponded(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   String IdParam = request.getParameter("id");
	   Demande demande = demandeDAO.getDemandeByID(Integer.parseInt(IdParam));
       Demande confirm = new Demande();
       confirm.setIdDemande(Integer.parseInt(IdParam));
       confirm.setNbrPochettesConfirmes(Integer.parseInt(request.getParameter("nbrConfirmee")));
       
       if(demande.getNbrPochettesDemandes() >= confirm.getNbrPochettesConfirmes()) {
    	   demandeDAO.updateDemandeTochangeConfirms(confirm);
    	   
           HttpSession session = request.getSession();
           Object userIdAttribute = session.getAttribute("user");
           String userIdString = userIdAttribute.toString();
       	List<Demande> answers = demandeDAO.getAnswersByCenter(Integer.parseInt(userIdString));
    		 if(answers.isEmpty()) { 
    			 request.setAttribute("noAnswers", true); 
    		 }else { 
    			 request.setAttribute("answers", answers); 
    		 }
    		 RequestDispatcher dispatcher = request.getRequestDispatcher("/Center/reponse.jsp");
    		 dispatcher.forward(request, response);
       }else {
           String errorMessage = "the value entered higher then the request";
			request.setAttribute("error", errorMessage);
			
	           HttpSession session = request.getSession();
	           Object userIdAttribute = session.getAttribute("user");
	           String userIdString = userIdAttribute.toString();
	       	List<Demande> answers = demandeDAO.getAnswersByCenter(Integer.parseInt(userIdString));
	    		 if(answers.isEmpty()) { 
	    			 request.setAttribute("noAnswers", true); 
	    		 }else { 
	    			 request.setAttribute("answers", answers); 
	    		 }
			 RequestDispatcher dispatcher = request.getRequestDispatcher("/Center/reponse.jsp");
			 dispatcher.forward(request, response);
       }
	   
	  
  }
	
	

}
