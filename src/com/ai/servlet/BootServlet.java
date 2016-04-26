package com.ai.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.model.Game;

/**
 * Servlet implementation class BootServlet
 */
public class BootServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static int gameIdInc;
	private static Map<Integer, Game> games;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BootServlet() {
        super();
        games = new HashMap<>();
        getGameIdInc();
    }
    
    public static boolean isGameExist(int gameId)
    {
    	return games.containsKey(gameId);
    }
    
    public static int getSize(int gameId)
    {
    	int size =0;
    	Game g = games.get(gameId);
    	if(g!=null)
    		size = g.getSize();
    	return size;
    }
    
    public static void createGameEntry(int gameId, Game game)
    {
    	games.put(gameId, game);
    }
    
    public static int getGameIdInc()
    {
    	return gameIdInc++;
    }

    public static Game getGame(int gameId)
    {
    	return games.get(gameId);
    }
    
    public static void removeGame(int gameId){
    	if(isGameExist(gameId))
    		games.remove(gameId);
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		if(session != null)
			session.invalidate();
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		System.out.println("FINALIZE");
	}

}
