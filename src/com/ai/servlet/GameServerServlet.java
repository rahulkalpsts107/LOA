package com.ai.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ai.model.Game;
import com.ai.model.PlayerType;

/**
 * Servlet implementation class GameServerServlet
 */
@WebServlet("/GameServerServlet")
public class GameServerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GameServerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Integer Id = (Integer) request.getSession().getAttribute("gameId");
		if(Id==null)
		{
			String gameFormat = (String)request.getParameter("gameformat");
			Integer ply  = Integer.parseInt(request.getParameter("ply"));
			PlayerType humanPlayer = ply == PlayerType.BLACK.ordinal() ? PlayerType.BLACK: PlayerType.WHITE;
			Integer diff  = Integer.parseInt(request.getParameter("difficulty"));
			int size = Integer.parseInt(gameFormat);
			int uniqueId = BootServlet.getGameIdInc();
			Id = uniqueId;
			
			System.out.println("New Game request for Format "+gameFormat);
			
			Game game = new Game(size,diff);
			game.initializeHumanPlayerType(humanPlayer);
			
			BootServlet.createGameEntry(uniqueId, game);
			
			//request.setAttribute("gameformat", gameFormat);
			request.getSession().setAttribute("gameId", uniqueId);
		}
		else
		{
			if(!BootServlet.isGameExist(Id))
			{
				String gameFormat = (String)request.getParameter("gameformat");
				Integer ply  = Integer.parseInt(request.getParameter("ply"));
				Integer diff  = Integer.parseInt(request.getParameter("difficulty"));
				PlayerType humanPlayer = ply == PlayerType.BLACK.ordinal() ? PlayerType.BLACK: PlayerType.WHITE;
				int size = Integer.parseInt(gameFormat);
				int uniqueId = BootServlet.getGameIdInc();
				
				System.out.println("New Game request for Format "+gameFormat);
				
				Game game = new Game(size,diff);
				game.initializeHumanPlayerType(humanPlayer);
				
				BootServlet.createGameEntry(uniqueId, game);
				
				request.getSession().setAttribute("gameId", uniqueId);
			}
			else
				System.out.println("Existing game request");
		}
		
		response.getWriter().append(request.getContextPath());
        response.sendRedirect("loa.jsp?gameformat="+BootServlet.getSize(Id)+"&ply="+BootServlet.getGame(Id).getHumanPlayer().ordinal());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
