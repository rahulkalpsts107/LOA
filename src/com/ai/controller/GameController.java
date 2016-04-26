package com.ai.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.ai.model.Game;
import com.ai.model.PlayerType;
import com.ai.model.Point;
import com.ai.servlet.BootServlet;

@Path("/game")
public class GameController {

	@GET
	@Path("current")
	@Produces(MediaType.APPLICATION_JSON )
	public String currentBoard(@Context HttpServletRequest req)
	{
		System.out.println("called get current board");
		HttpSession session= req.getSession(false);
		String ret= "";
		if(session != null)
		{
			Integer Id = (Integer) req.getSession().getAttribute("gameId");
			if(Id != null)
			{
				Game game = BootServlet.getGame(Id);
				if(game != null)
				{
					return game.getJSONString();
				}
			}
		}
		System.out.println(ret);
		return ret;
	}
	/**
	 * Successful move will return 0 and unsuccessful move will return 1
	 * BLACKPLAYER = 0,
	 * WHITEPLAYER = 1,
	 */
	@GET
	@Path("makemove")
	@Produces(MediaType.APPLICATION_JSON )
	//@Produces(MediaType.APPLICATION_XML)
	public int makeMove( @Context HttpServletRequest req)
	{
		System.out.println("make move");
		int player = Integer.parseInt(req.getParameter("player"));
		int srcx= Integer.parseInt(req.getParameter("srcX"));
		int srcy= Integer.parseInt(req.getParameter("srcY"));
		int dstx= Integer.parseInt(req.getParameter("destX"));
		int dsty= Integer.parseInt(req.getParameter("destY"));
		System.out.println("make move from x "+ srcx + " y "+ srcy+ "to x "+dstx + " y "+dsty+ " pl: "+player);
		int ret = 1;
		HttpSession session= req.getSession(false);
		if(session != null)
		{
			Integer Id = (Integer) req.getSession().getAttribute("gameId");
			if(Id != null)
			{
				Game game = BootServlet.getGame(Id);
				if(game != null)
				{
					PlayerType movePlayer = player == PlayerType.BLACK.ordinal()? PlayerType.BLACK: PlayerType.WHITE;
					{
						boolean isDone = game.makeMove(new Point(srcx,srcy), new Point(dstx,dsty));
						if(isDone == true)
							ret = 0;
					}
				}
			}
		}
		return ret;
	}
	
	@GET
	@Path("legalmoves")
	@Produces(MediaType.APPLICATION_JSON )
	public List<MoveParam> legalMoves(@Context HttpServletRequest req)
	{
		int player = Integer.parseInt(req.getParameter("player"));
		int x= Integer.parseInt(req.getParameter("xPos"));
		int y= Integer.parseInt(req.getParameter("yPos"));
		PointParam point = new PointParam(x,y);
		System.out.println("legal moves x "+ x + " y "+ y+  " pl: "+player);
		List<MoveParam> moves= new ArrayList<>();
		HttpSession session= req.getSession(false);
		if(session != null)
		{
			Integer Id = (Integer) req.getSession().getAttribute("gameId");
			if(Id != null)
			{
				Game game = BootServlet.getGame(Id);
				if(game != null)
				{
					PlayerType movePlayer = player == PlayerType.BLACK.ordinal()? PlayerType.BLACK: PlayerType.WHITE;
					if(movePlayer == game.getHumanPlayer() && point != null)
					{
						List<Point> listOfMoves = game.getLegalMovesOfHuman(point.toPoint());
						for(Point p : listOfMoves)
						{
							moves.add(new MoveParam(point.getX(),point.getY(), p.getX(),p.getY()));
						}
					}
				}
			}
		}
		return moves;
	}
	
	@GET
	@Path("cpumove")
	@Produces(MediaType.APPLICATION_JSON )
	public boolean cpuMove(@Context HttpServletRequest req){
		HttpSession session= req.getSession(false);
		boolean ret=false;
		System.out.println("");
		if(session != null)
		{
			Integer Id = (Integer) req.getSession().getAttribute("gameId");
			if(Id != null)
			{
				Game game = BootServlet.getGame(Id);
				if(game != null)
				{
					ret = game.cpuPlay();
				}
			}
		}
		return ret;
	}
	
	@GET
	@Path("whowon")
	@Produces(MediaType.APPLICATION_JSON )
	public int whoWon(@Context HttpServletRequest req){
		HttpSession session= req.getSession(false);
		int ret=2;
		System.out.println("whowon ?");
		if(session != null)
		{
			Integer Id = (Integer) req.getSession().getAttribute("gameId");
			if(Id != null)
			{
				Game game = BootServlet.getGame(Id);
				if(game != null)
				{
					ret = game.whoWon();
				}
			}
		}
		return ret;
	}
	
	@GET
	@Path("deletegame")
	@Produces(MediaType.APPLICATION_JSON )
	public int deleteGame(@Context HttpServletRequest req){
		HttpSession session= req.getSession(false);
		int ret=0;
		System.out.println("deletegame ");
		if(session != null)
		{
			Integer Id = (Integer) req.getSession().getAttribute("gameId");
			if(Id != null)
			{
				BootServlet.removeGame(Id);
			}
			session.invalidate();
		}
		return ret;
	}
}
