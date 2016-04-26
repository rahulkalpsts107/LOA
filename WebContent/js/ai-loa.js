
var linespixedBlk5 = 70;
var linespixedBlk6 = 70;
var globalThis;
var humanTurn = 9999;
var cpuTurn = 1111;

//black = 0 white = 1
function linesOfAction(uiInstance,blkwidth,blkheight,pixelBlkWidth, humanPly)
{
    this.uiInstance = uiInstance;
    this.blkwidth = blkwidth;
    this.blkheight = blkheight;
    this.humanColor = humanPly;
    this.cpuColor= humanPly == 1 ?0:1;
    this.pixelBlkWidth = pixelBlkWidth ;
    this.boardWidth = this.blkwidth  * this.pixelBlkWidth;
    this.boardHeight = this.blkheight  * this.pixelBlkWidth;
    this.checkerScale = 0.8;
    this.radius = this.blkwidth * this.checkerScale / 2;
    this.boardColor = "#8b7e66";
    this.circlescale = 4.0;
    //this.getBoard();
    //always black should play first
    this.whoseTurn = this.humanColor == 0?humanTurn:cpuTurn;
    //this.attachPieces();
    console.log('cpu is '+this.cpuColor);
    console.log('human is '+this.humanColor);
    globalThis =this;
    this.isDeciding =0;
    this.isDecidingX=-1;
    this.isDecidingY=-1;
    this.latestBoard=[];//initialize
    this.lines =null;
    this.circles = null;
    this.moves = [];
    var d = new Date();
    this.previousClickTime = d.getTime();
    this.drawLinesOfActionBoard(pixelBlkWidth);
    this.makeMove();//first move
}

linesOfAction.prototype.makeMove = function(){
	if(this.whoseTurn == humanTurn)
	{
		//Lets wait for human
		this.getBoard();
	}
	else
	{
		console.log('CPU do the honour');
		//it means CPU turn
		this.cpuMove();
	}
}

linesOfAction.prototype.togglePlayer = function(){
	if(this.whoseTurn == humanTurn)
		this.whoseTurn = cpuTurn;
	else
		this.whoseTurn = humanTurn;
}

linesOfAction.prototype.cpuMove = function(){
	$.ajax('http://localhost:8080/LOA/api/game/cpumove', {
	      success: function(data) {
	    	  console.log('CPU move '+data);
	    	  globalThis.getBoard();//update board
	    	  globalThis.togglePlayer();
	    	  globalThis.whoWon();
	      },
	      type: 'GET',
	      error: function() {
	         console.log('error');
	      }
	   });
}

linesOfAction.prototype.whoWon = function(){
	$.ajax('http://localhost:8080/LOA/api/game/whowon', {
	      success: function(data) {
	    	console.log(data);
	    	if(data == 0)
	    	{
	    		alert('Human won congrats !');
	    		globalThis.deleteGame();
	    	}
	    	else if(data == 1)
	    	{
	    		alert('CPU won congrats !');
	    		globalThis.deleteGame();
	    	}
	    	else
	    		console.log('nobody won')
	      },
	      data: {
	          format: 'json',
	          //context: this,
	       },
	      type: 'GET',
	      error: function() {
	         console.log('error');
	      }
	   });
}

linesOfAction.prototype.deleteGame = function(){
	$.ajax('http://localhost:8080/LOA/api/game/deletegame', {
	      success: function(data) {
	    	  window.location = "http://localhost:8080/LOA/"
	      },
	      data: {
	          format: 'json',
	          //context: this,
	       },
	      type: 'GET',
	      error: function() {
	         console.log('error');
	      }
	   });
}


linesOfAction.prototype.getBoard = function(){
	$.ajax('http://localhost:8080/LOA/api/game/current', {
	      success: function(data) {
	    	console.log(data);
	    	globalThis.clearLines();
  		  	globalThis.clearPieces();
	    	globalThis.latestBoard=data["data"];
	        globalThis.attachPieces(data);
	      },
	      data: {
	          format: 'json',
	          //context: this,
	       },
	      type: 'GET',
	      error: function() {
	         console.log('error');
	      }
	   });
} 

linesOfAction.prototype.attachPieces = function(data){
	data = data["data"];
	var circles = globalThis.uiInstance.group();
	for (var i = 0; i < this.blkwidth; i++) {
        for (var j = 0; j < this.blkheight; j++) {
        	var yoffset = (i+1 - 0.5) * (this.boardWidth / this.blkwidth);
            var  xoffset= (j+1 - 0.5)*(this.boardHeight/ this.blkheight);
            var radius = this.blkwidth * this.circlescale;
            var circle;
            if(data[i][j] == 0)
            {
            	circle = this.uiInstance.circle(xoffset+35, yoffset+35, radius);
	            circle.attr({
	                //stroke: '#6699ff',
	                fill:'#000000'
	            });
            }
            else if(data[i][j]==1)
            {
            	circle = this.uiInstance.circle(xoffset+35, yoffset+35, radius);
            	circle.attr({
	                //stroke: '#6699ff',
	                fill:'#ffffff'
	            });
			}
            if(circle !== undefined)
            {
	            circle.click(function(a,ctx) {
	            	globalThis.onClickChecker(ctx,a);
	            	
	            });
	            this.board.add(circle);
	            circles.add(circle);
            }
        }
	}
	this.board.add(circles);
	this.circles = circles;
}

linesOfAction.prototype.drawLinesOfActionBoard = function(pixelBlkWidth){
    this.board = this.uiInstance.group();
    this.board.add(this.uiInstance.rect(0,0,this.boardWidth+100, this.boardHeight+100));
    this.board.attr({
        fill: this.boardColor
    })
    
    //var square = uiInstance.rect(210,40,160,160);
    var checkers = this.uiInstance.group();
    for (var i = 1; i <= this.blkwidth; i++) {
        for (var j = 1; j <= this.blkheight; j++) {
            var xoffset = (i - 0.5) * (this.boardWidth / this.blkwidth);
            var yoffset = (j - 0.5)*(this.boardHeight/ this.blkheight);
            var rect = this.uiInstance.rect(xoffset,yoffset,pixelBlkWidth,pixelBlkWidth);
            var total = i+j;
            if(total %2 == 0)
                rect.attr({
                    stroke: '#8b7e66',
                    fill:'#f5deb3'
                });
            else
                rect.attr({
                    stroke: '#8b7e66',
                    fill:'#a47c48'
                });
            rect.click(function(a,ctx) {
            	globalThis.onClickRect(ctx,a);
            });
          checkers.add(rect);
        }
    }
    
    checkers.attr({
        fill: "#fff",
    });
    
    this.board.add(checkers);
    this.board.drag();
}

linesOfAction.prototype.getAllMoves =  function (xP,yP){
	//console.log(xP+'----'+yP)
	$.ajax('http://localhost:8080/LOA/api/game/legalmoves', {
	      success: function(data) {
	    	globalThis.moves = data;
	    	console.log(data);
	    	var i;
	    	var locallines = globalThis.uiInstance.group();
	    	var list = "";
	    	for(i=0; i<data.length; i++)
	    	{
		    	var src_yoffset = (xP+1 - 0.5) * (globalThis.boardWidth / globalThis.blkwidth);
	            var src_xoffset= (yP+1 - 0.5)*(globalThis.boardHeight/ globalThis.blkheight);
	            var dst_yoffset = (data[i]["destX"]+1 - 0.5) * (globalThis.boardWidth / globalThis.blkwidth);
	            var dst_xoffset= (data[i]["destY"]+1 - 0.5)*(globalThis.boardHeight/ globalThis.blkheight);
	            src_yoffset+=35;
	            src_xoffset+=35;
	            dst_yoffset+=35;
	            dst_xoffset+=35;
	            var line = globalThis.uiInstance.line(src_xoffset,src_yoffset,dst_xoffset,dst_yoffset)
	            .attr({strokeWidth:5,stroke:"black",strokeLinecap:"round"});
	            locallines.add(line);
	    	}
	    	globalThis.lines = locallines;
	    	globalThis.board.add(globalThis.lines);
	      },
	      data: {
	    	  dataType: 'json',
	    	  player : globalThis.humanColor,
	    	  xPos : xP,
	    	  yPos: yP
	       },
	      type: 'GET',
	      error: function() {
	         console.log('error');
	      }
	});
}

linesOfAction.prototype.clearPieces = function(){
	  if(globalThis.circles != null)
	  {
		  globalThis.circles.remove();//remove all pieces
		  globalThis.circles = null;
	  }
}

linesOfAction.prototype.clearLines = function(){
	if(globalThis.lines != null)
	  {
		  globalThis.lines.remove();//remove all pieces
		  globalThis.lines = null;
	  }
}

linesOfAction.prototype.makeHumanMove =  function (src_x, src_y, dst_x, dst_y ){
	console.log('make human move');
	$.ajax('http://localhost:8080/LOA/api/game/makemove', {
	      success: function(data) {
	    	  console.log(data);
	    	  if(data == 1)
	          {
	    		  console.log('error in making move');
	    		  globalThis.clearLines();
	          }
	    	  else
	    	  {
	    		  globalThis.clearLines();
	    		  globalThis.clearPieces();
		    	  globalThis.getBoard();//redraw
		    	  globalThis.togglePlayer();
		    	  globalThis.makeMove();
	    	  }
	    	  globalThis.isDeciding =0;
		      globalThis.isDecidingX = -1;
			  globalThis.isDecidingY = -1;
	      },
	      data: {
	    	  dataType: 'json',
	    	  player : globalThis.humanColor,
	    	  destX: dst_x,
	      	  destY: dst_y,
	    	  srcX : src_x,
	    	  srcY: src_y,
	       },
	      type: 'GET',
	      error: function() {
	         console.log('error');
	      }
	});
}


linesOfAction.prototype.onClickRect = function (a,context){
	console.log('Rectangle clicked')
	var xWidth = context.srcElement.x.baseVal.value;
	var yWidth = context.srcElement.y.baseVal.value;
	var y =  (globalThis.blkwidth * xWidth)/globalThis.boardWidth;
	var x =  (globalThis.blkheight * yWidth)/globalThis.boardHeight;
	x=x-0.5;
	y=y-0.5;
	console.log('x '+x+' y '+y);
	if(globalThis.isDeciding == 1 && globalThis.whoseTurn == humanTurn)
	{
		var isRightMove = 0;
		var idx= 0;
		for(;idx<globalThis.moves.length ; idx++)
		{
			if(globalThis.moves[idx]["destX"] == x && globalThis.moves[idx]["destY"] == y)
			{
				isRightMove=1;
				break;
			}
		}
		if(isRightMove == 1)
			this.makeHumanMove(globalThis.isDecidingX,globalThis.isDecidingY,x,y);
		else
			console.log('You cant move there!');
	}
}

linesOfAction.prototype.onClickChecker = function (a , context)
{
	console.log('hiiii');
	var xWidth = context.srcElement.cx.baseVal.value;
	var yWidth = context.srcElement.cy.baseVal.value;
	var y =  (globalThis.blkwidth * xWidth)/globalThis.boardWidth;
	var x =  (globalThis.blkheight * yWidth)/globalThis.boardHeight;
	x=x-1;
	y=y-1;
	
	if(globalThis.whoseTurn == humanTurn)
	{
		if(globalThis.isDeciding!=1)
		{
			//console.log(globalThis.latestBoard);
			if(globalThis.latestBoard[x][y] == globalThis.humanColor )
			{
				var d = new Date();
				var current = d.getTime();
				if(((current - globalThis.previousClickTime)/1000) >0.5)//only if greater than a second
				{
					globalThis.isDeciding =1;
					globalThis.isDecidingX = x;
					globalThis.isDecidingY = y;
					globalThis.previousClickTime = current;
					this.getAllMoves(x,y);
				}
				else
				console.log('not yet')
			}
			else
			{
				console.log('not ur piece man');
				alert('Not ur piece')
			}
		}
		else
		{
			if(x == globalThis.isDecidingX && y == globalThis.isDecidingY)
			{
				if(globalThis.lines!==null)
				{
					var d = new Date();
					var current = d.getTime();
					if(((current - globalThis.previousClickTime)/1000) >0.5)//only if greater than a second
					{
						globalThis.previousClickTime = current;
						globalThis.lines.remove();
						globalThis.isDeciding =0;
						globalThis.isDecidingX = -1;
						globalThis.isDecidingY = -1;
						globalThis.lines = null;
					}
					else
						console.log('not yet')
					
				}
			}
		}
	}
}

function startGame( height,  width, Id, humanply) {
  console.log('game id is '+Id);
  var linesPixelBlk;
  if(height == 5)
	  linesPixelBlk = linespixedBlk5;
  else
	  linesPixelBlk = linespixedBlk6;
  var uiInstance = Snap($(document).width(), $(document).height());
  var instance = new linesOfAction(uiInstance, height, width, linesPixelBlk,humanply);
}